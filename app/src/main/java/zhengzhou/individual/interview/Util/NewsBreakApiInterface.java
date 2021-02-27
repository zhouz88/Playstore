package zhengzhou.individual.interview.Util;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Query;

public interface NewsBreakApiInterface {
    @GET("/serving")
    Call<ResponseResult> getNewsServiceResult(@Query("app") String app,
                                              @Query("token") String token, @Query("lat") String lat,
                                              @Query("lng") String lng);


    @HTTP(method = "GET", path = "/serving")
    Call<ResponseResult> getNewsService(@Query("app") String app,
                                              @Query("token") String token, @Query("lat") String lat,
                                              @Query("lng") String lng);

    default Call<ResponseResult> callGetNewsServiceResultApi(GetNewsAPICompositeKey key) {
        return getNewsService(key.getApp(), key.getToken(), key.getLat(), key.getLng());
    }
}
