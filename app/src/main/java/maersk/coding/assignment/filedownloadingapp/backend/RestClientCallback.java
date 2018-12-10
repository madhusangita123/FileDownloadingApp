package maersk.coding.assignment.filedownloadingapp.backend;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;

public interface RestClientCallback {

    void onSuccess(ResponseBody response);
    void onFailure();
}
