package zhengzhou.individual.catsDj.util;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SongResult {
    @SerializedName("data")
    @Expose
    public List<Datum> data = new ArrayList<Datum>();
    @SerializedName("code")
    @Expose
    public Long code;

    public static class Datum {

        @SerializedName("id")
        @Expose
        public Long id;
        @SerializedName("url")
        @Expose
        public String url;
        @SerializedName("br")
        @Expose
        public Long br;
        @SerializedName("size")
        @Expose
        public Long size;
        @SerializedName("md5")
        @Expose
        public String md5;
        @SerializedName("code")
        @Expose
        public Long code;
        @SerializedName("expi")
        @Expose
        public Long expi;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("gain")
        @Expose
        public Long gain;
        @SerializedName("fee")
        @Expose
        public Long fee;
        @SerializedName("uf")
        @Expose
        public Object uf;
        @SerializedName("payed")
        @Expose
        public Long payed;
        @SerializedName("flag")
        @Expose
        public Long flag;
        @SerializedName("canExtend")
        @Expose
        public Boolean canExtend;
        @SerializedName("freeTrialInfo")
        @Expose
        public Object freeTrialInfo;
        @SerializedName("level")
        @Expose
        public String level;
        @SerializedName("encodeType")
        @Expose
        public String encodeType;
        @SerializedName("freeTrialPrivilege")
        @Expose
        public FreeTrialPrivilege freeTrialPrivilege;
        @SerializedName("freeTimeTrialPrivilege")
        @Expose
        public FreeTimeTrialPrivilege freeTimeTrialPrivilege;
        @SerializedName("urlSource")
        @Expose
        public Long urlSource;


        public static class FreeTimeTrialPrivilege {
            @SerializedName("resConsumable")
            @Expose
            public Boolean resConsumable;
            @SerializedName("userConsumable")
            @Expose
            public Boolean userConsumable;
            @SerializedName("type")
            @Expose
            public Long type;
            @SerializedName("remainTime")
            @Expose
            public Long remainTime;

        }

        public static class FreeTrialPrivilege {

            @SerializedName("resConsumable")
            @Expose
            public Boolean resConsumable;
            @SerializedName("userConsumable")
            @Expose
            public Boolean userConsumable;

        }
    }
}
