package maersk.coding.assignment.filedownloadingapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import maersk.coding.assignment.filedownloadingapp.model.DownLoadedItem;
import maersk.coding.assignment.filedownloadingapp.model.DownloadProgress;
import maersk.coding.assignment.filedownloadingapp.repository.FileRepository;


public class FileViewModel extends ViewModel{

    private static final int ONE_SECOND = 1000;

    private MutableLiveData<DownLoadedItem> downLoadableLiveData = null;
    private MutableLiveData<DownloadProgress> downloadProgressMutableLiveData = null;
    private  FileRepository  fileRepository;
    private Timer timer;

    public FileViewModel(){
        downloadProgressMutableLiveData = new MutableLiveData<>();
        fileRepository = new FileRepository();
    }


    public MutableLiveData<DownloadProgress> getDownloadProgress(){
        timer = new Timer();
        // Update the UI every one minute.
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                downloadProgressMutableLiveData.postValue(fileRepository.getDownloadProgress());
            }
        }, ONE_SECOND, ONE_SECOND);
        return  downloadProgressMutableLiveData;
    }

    public MutableLiveData<DownLoadedItem> getFile(String url){
        downLoadableLiveData = new MutableLiveData<>();

        DownLoadedItem downLoadableFile = fileRepository.getFile( url );
        downLoadableLiveData.postValue( downLoadableFile );

        return downLoadableLiveData;
    }

}
