package maersk.coding.assignment.filedownloadingapp.backend;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RestClient {

    private RestClientCallback mCallback;

    public RestClient(RestClientCallback callback){
        mCallback = callback;
    }

    public void downloadFromInternet(String url){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("http://farm1.static.flickr.com/");
        Retrofit retrofit = builder.client(httpClient.build()).build();
        DownloadService downloadService = retrofit.create(DownloadService.class);
        Call<ResponseBody> call = downloadService.downloadFile(url);
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.i( getClass().getName(),response.toString() );
                    mCallback.onSuccess( response.body() );
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mCallback.onFailure();
            }
        } );
    }


}
