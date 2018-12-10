package maersk.coding.assignment.filedownloadingapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import maersk.coding.assignment.filedownloadingapp.R;
import maersk.coding.assignment.filedownloadingapp.model.DownLoadedItem;
import maersk.coding.assignment.filedownloadingapp.model.DownloadProgress;
import maersk.coding.assignment.filedownloadingapp.utils.AppUtils;
import maersk.coding.assignment.filedownloadingapp.viewmodel.FileViewModel;

public class MainActivity extends AppCompatActivity {

    private TextView mProgressTextView;
    private ImageButton mDownloadBtn;
    private ImageView mImageView;
    private TextInputLayout mURLInput;
    private ProgressBar mProgressBar;
    private MutableLiveData<DownLoadedItem> mDownLoadedItem;
    private MutableLiveData<DownloadProgress> mDownLoadProgress;
    private FileViewModel mFileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        mFileViewModel = ViewModelProviders.of( this ).get( FileViewModel.class );

        mDownloadBtn = findViewById( R.id.download_btn );
        mDownloadBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppUtils.isConnected( MainActivity.this )){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkRequiredPermission();
                    } else {
                        downloadFile(mURLInput.getEditText().getText()==null?
                                getString( R.string.default_url ):mURLInput.getEditText().getText().toString());
                    }
                }else
                    Toast.makeText(MainActivity.this,getString( R.string.no_connection ),Toast.LENGTH_LONG).show();


            }
        } );

        mImageView = findViewById( R.id.image );

        mURLInput = findViewById( R.id.url_input_layout );
        mURLInput.getEditText().setText(getString( R.string.default_url ));
        mURLInput.getEditText().setSelection(mURLInput.getEditText().getText().length());
        mURLInput.getEditText().setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService( Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        } );

        mProgressTextView = findViewById( R.id.progress_text );
        mProgressBar = findViewById( R.id.progressBar );

        if(savedInstanceState!=null){
            if(!savedInstanceState.getBoolean(getString( R.string.in_progress ))){
                downloadFile(savedInstanceState.getString(getString( R.string.url_to_download)));
            }else
                downloaded(mFileViewModel.getDownloadProgress().getValue());
        }

    }


    private void downloadFile(String url){
        mDownloadBtn.setEnabled( false );
        mDownloadBtn.setAlpha((float) 0.5 );
        mDownloadBtn.setBackgroundColor( getResources().getColor( android.R.color.background_dark) );
        mProgressTextView.setVisibility( View.VISIBLE );
        mProgressBar.setVisibility( View.VISIBLE );
        mDownLoadedItem = mFileViewModel.getFile( url );
        mDownLoadedItem.observe( this, new Observer<DownLoadedItem>() {
            @Override
            public void onChanged(@Nullable DownLoadedItem downLoadedItem) {
                if(downLoadedItem.getFilePathOnDisk()!=null)
                    setImageView( downLoadedItem.getFilePathOnDisk() );
            }
        } );
        mDownLoadProgress = mFileViewModel.getDownloadProgress();
        mDownLoadProgress.observe( this,new Observer<DownloadProgress>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(@Nullable DownloadProgress downloadProgress) {
                if(downloadProgress.isDone()){
                   downloaded( downloadProgress );
                }
                else{
                    mProgressTextView.setText( new StringBuilder().append( getString( R.string.downloading_text ) ).append( downloadProgress.getProgress() ).append( "%" ).toString() );
                    mProgressBar.setProgress(downloadProgress.getProgress());
                }

            }
        } );
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void checkRequiredPermission() {
        String requiredpermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        boolean hasPermission = (checkSelfPermission(requiredpermission) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            requestPermissions(new String[]{requiredpermission},1);
        }else{
            downloadFile(mURLInput.getEditText().getText()==null?
                    getString( R.string.default_url ):mURLInput.getEditText().getText().toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadFile(mURLInput.getEditText().getText()==null?
                        getString( R.string.default_url ):mURLInput.getEditText().getText().toString());
            }
        }
    }

    private void downloaded(DownloadProgress downloadProgress){
        mProgressTextView.setText("Downloaded");
        mProgressBar.setProgress(100);
        mDownloadBtn.setEnabled( true );
        mDownloadBtn.setAlpha((float) 1.0 );
        mDownloadBtn.setBackgroundColor( getResources().getColor( android.R.color.holo_blue_bright ) );
        setImageView( downloadProgress.getFilePath() );
    }

    private void setImageView(String filePath){
        File imgFile = new  File(filePath);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            mImageView.setImageBitmap(myBitmap);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(getString( R.string.in_progress ),
                mDownLoadProgress!=null? mDownLoadProgress.getValue().isDone(): false);
        outState.putInt(getString( R.string.progress), mDownLoadProgress!=null? mDownLoadProgress.getValue().getProgress(): 0);
        outState.putString( getString( R.string.url_to_download),mURLInput.getEditText().getText().toString() );
        super.onSaveInstanceState( outState );
    }
}
