package zhengzhou.individual.catsDj.util;

import lombok.Getter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CloudMusicService {
    @Getter
    private CloudMusicApiInterface api;
    private static volatile CloudMusicService service;
    public static CloudMusicService getInstance() {
        if (service == null) {
            synchronized (CloudMusicService.class) {
                service = new CloudMusicService();
                service.api = new Retrofit.Builder()
                        .baseUrl(BaseUrl.MUSIC_PLAY_LIST_DETAIL)
                        // https://stackoverflow.com/questions/43434073/unable-to-create-call-adapter-for-io-reactivex-observable
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(SafeClient.safeClient)
                        .build()
                        .create(CloudMusicApiInterface.class);
            }
        }
        return service;
    }
}
