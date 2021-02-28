package zhengzhou.individual.interview.util;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Query;

public interface NewsBreakApiInterface {
    @GET("/serving")
    Single<ResponseResult> getNewsServiceResult(@Query("app") String app,
                                                @Query("token") String token, @Query("lat") String lat,
                                                @Query("lng") String lng);


    @HTTP(method = "GET", path = "/serving")
    Call<ResponseResult> getNewsService(@Query("app") String app,
                                              @Query("token") String token, @Query("lat") String lat,
                                              @Query("lng") String lng);

    default Single<ResponseResult> callGetNewsServiceResultApi(GetNewsAPICompositeKey key) {
        return getNewsServiceResult(key.getApp(), key.getToken(), key.getLat(), key.getLng());
    }

    default Call<ResponseResult> callGetNewsServiceApi(GetNewsAPICompositeKey key) {
        return getNewsService(key.getApp(), key.getToken(), key.getLat(), key.getLng());
    }
}
