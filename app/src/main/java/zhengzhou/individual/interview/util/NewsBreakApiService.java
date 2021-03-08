package zhengzhou.individual.interview.util;

import io.reactivex.Single;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsBreakApiService {
    private static final String BASE_URL = "https://openapi.newsbreak.com";
    private static volatile NewsBreakApiService service;
    public static NewsBreakApiService getInstance() {
        if (service == null) {
            synchronized (NewsBreakApiService.class) {
                service = new NewsBreakApiService();
                service.api = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        // https://stackoverflow.com/questions/43434073/unable-to-create-call-adapter-for-io-reactivex-observable
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(NewsBreakApiInterface.class);
            }
        }
        return service;
    }

    private NewsBreakApiInterface api;

    private NewsBreakApiService() {
        api = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                // https://stackoverflow.com/questions/43434073/unable-to-create-call-adapter-for-io-reactivex-observable
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsBreakApiInterface.class);
    }

    public ResponseResult callGetNewsApi() {
        Call<ResponseResult> call = api.callGetNewsServiceApi(
                GetNewsAPICompositeKey
                        .builder()
                        .app("interview")
                        .token("hA7lIIPoxZWmhF9wd4muThQGiJzUwwW0")
                        .lat("41.8778344")
                        .lng("-87.6315493")
                        .build());
        try {
            return call.execute().body();
        } catch (Exception e) {
            return null;
        }
    }

    public Single<ResponseResult> getNewsApiSingle() {
        return api.callGetNewsServiceResultApi(GetNewsAPICompositeKey
                .builder()
                .app("interview")
                .token("hA7lIIPoxZWmhF9wd4muThQGiJzUwwW0")
                .lat("47.6062")
                .lng("122.3321")
                .build());
    }
}
