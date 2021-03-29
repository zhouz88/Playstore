package zhengzhou.individual.catsDj.util;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CloudMusicApiInterface {
        @GET(BaseUrl.MUSIC_PLAY_LIST_DETAIL + "?type=song")
        Call<SongResult> getSongById(@Query("id") String id);

        @GET(BaseUrl.MUSIC_PLAY_LIST_DETAIL + "?type=detail")
        Call<SongImageResult> getSongImageById(@Query("id") String id);
}
