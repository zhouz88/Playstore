package zhengzhou.individual.interview.util;

import io.reactivex.Observable;
import io.reactivex.Single;

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

    public Observable<ResponseResult> callGetNewsApiObserver(GetNewsAPICompositeKey key) {
        Observable<ResponseResult> observable = api.callGetNewsServiceApi(key);
        return observable;
    }

    public Single<ResponseResult> getNewsApiSingle() {
        return api.callGetNewsServiceResultApi(GetNewsAPICompositeKey
                .builder()
                .applicationName("interview")
                .tokenName("hA7lIIPoxZWmhF9wd4muThQGiJzUwwW0")
                .latitude("47.6062")
                .longitude("122.3321")
                .build());
    }
}
