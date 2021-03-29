package zhengzhou.individual.catsDj.util;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Query;

public interface NewsBreakApiInterface {
    @GET("/serving")
    Single<ResponseResult> getNewsServiceResult(@Query("app") String application,
                                                @Query("token") String token, @Query("lat") String latitude,
                                                @Query("lng") String longitude);

    @HTTP(method = "GET", path = "/serving")
    Observable<ResponseResult> getNewsService(@Query("app") String app,
                                              @Query("token") String token, @Query("lat") String lat,
                                              @Query("lng") String lng);

    default Single<ResponseResult> callGetNewsServiceResultApi(GetNewsAPICompositeKey key) {
        return getNewsServiceResult(key.getApp(), key.getToken(), key.getLat(), key.getLng());
    }

    default Observable<ResponseResult> callGetNewsServiceApi(GetNewsAPICompositeKey key) {
        return getNewsService(key.getApp(), key.getToken(), key.getLat(), key.getLng());
    }
}
