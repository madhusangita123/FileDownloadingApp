package maersk.coding.assignment.filedownloadingapp.repository;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import maersk.coding.assignment.filedownloadingapp.R;
import maersk.coding.assignment.filedownloadingapp.backend.RestClient;
import maersk.coding.assignment.filedownloadingapp.backend.RestClientCallback;
import maersk.coding.assignment.filedownloadingapp.model.DownLoadedItem;
import maersk.coding.assignment.filedownloadingapp.model.DownloadProgress;
import maersk.coding.assignment.filedownloadingapp.ui.MainActivity;
import okhttp3.ResponseBody;

public class FileRepository {

    private DownLoadedItem downLoadedFile;
    private DownloadProgress downloadProgress;
    private CompositeDisposable disposable;

    public  DownLoadedItem getFile(String url){
        downLoadedFile = new DownLoadedItem();
        downLoadedFile.setFileUrl( url );
        downLoadedFile.setFileName( "File"+ System.currentTimeMillis() );
        downloadProgress = new DownloadProgress( 0,false );
        disposable  = new CompositeDisposable();
        downloadFromURL( url );
        return downLoadedFile;
    }

    public  void downloadFromURL(String url){
        RestClient client = new RestClient( new RestClientCallback() {
            @Override
            public void onSuccess(ResponseBody response) {
                Log.i(getClass().getName(),response.toString());

                Disposable subscribe = saveToDisk(response).subscribeOn( Schedulers.io() )
                        .observeOn( AndroidSchedulers.mainThread() )
                        .subscribe( new Consumer<DownloadProgress>() {
                            @Override
                            public void accept(DownloadProgress progress) throws Exception {
                                if(progress.isDone()){
                                    downLoadedFile.setFilePathOnDisk( progress.getFilePath() );
                                }
                            }
                        } );
                disposable.add(subscribe);
            }

            @Override
            public void onFailure() {

            }
        } );
        client.downloadFromInternet( url );
    }

    public DownloadProgress getDownloadProgress() {
        return downloadProgress;
    }


    private Observable<DownloadProgress> saveToDisk(ResponseBody response){
        return Observable.create( new ObservableOnSubscribe<DownloadProgress>() {
            @Override
            public void subscribe(ObservableEmitter<DownloadProgress> emitter) throws Exception {
                try {

                    int count;
                    byte data[] = new byte[1024 * 4];
                    long fileSize = response.contentLength();
                    InputStream bis = new BufferedInputStream( response.byteStream(), 1024 * 8 );
                    File outputFile = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS ), downLoadedFile.getFileName()+".jpg" );
                    OutputStream output = new FileOutputStream( outputFile );
                    long total = 0;
                    while ((count = bis.read( data )) != -1) {
                        total += count;
                        int progress = (int) ((total * 100) / fileSize);
                        downloadProgress.setProgress( progress );
                        emitter.onNext( downloadProgress );
                        output.write( data, 0, count );
                    }
                    output.flush();
                    output.close();
                    bis.close();
                    downloadProgress.setDone( true );
                    downloadProgress.setFilePath( outputFile.getAbsolutePath() );
                    emitter.onNext( downloadProgress );

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        } );
    }

}
