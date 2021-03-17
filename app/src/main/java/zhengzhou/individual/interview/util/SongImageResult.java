package zhengzhou.individual.interview.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class SongImageResult {

    @SerializedName("songs")
    @Expose
    public List<Song> songs = new ArrayList<Song>();

    public static class Al {
        @SerializedName("id")
        @Expose
        public Long id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("picUrl")
        @Expose
        public String picUrl;
        @SerializedName("tns")
        @Expose
        public List<Object> tns = new ArrayList<Object>();
        @SerializedName("pic_str")
        @Expose
        public String picStr;
        @SerializedName("pic")
        @Expose
        public Long pic;

        public boolean like =false;
    }

    public static class Song {

        @SerializedName("al")
        @Expose
        public Al al;

    }

}