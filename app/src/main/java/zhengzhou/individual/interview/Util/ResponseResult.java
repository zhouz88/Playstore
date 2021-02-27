package zhengzhou.individual.interview.Util;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ResponseResult {

    @SerializedName("result")
    @Expose
    public List<Result> result = new ArrayList<Result>();

    public static class Result {
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("documents")
        @Expose
        public List<Document> documents = new ArrayList<Document>();

        public static class Document {
            @SerializedName("summary")
            @Expose
            public String summary;
            @SerializedName("epoch")
            @Expose
            public Long epoch;
            @SerializedName("labelBgColor")
            @Expose
            public String labelBgColor;
            @SerializedName("_id")
            @Expose
            public String id;
            @SerializedName("titleText")
            @Expose
            public String titleText;
            @SerializedName("imageSource")
            @Expose
            public String imageSource;
            @SerializedName("subTitleTextRight")
            @Expose
            public String subTitleTextRight;
            @SerializedName("linkUrl")
            @Expose
            public String linkUrl;
            @SerializedName("labelText")
            @Expose
            public String labelText;
        }
    }
}
