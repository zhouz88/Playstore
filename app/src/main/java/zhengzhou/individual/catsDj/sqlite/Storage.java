package zhengzhou.individual.catsDj.sqlite;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Storage {
    public static LikeDatabase db;
    public static int musicConfig = 0;

    public static List<String> copyIds = new ArrayList<>(Arrays.asList(
            1813200064+"",
            1810021921+"",

            28188307 + "",
            25880354 + "",

            1824473080 + "",
            1494985963 + "",

            1406673720 + "",
            1436384830 + "",

            1377100917 + "",
            1814798370 + "",

            27570833 + "",
            543986441 + "",

            1440162979 + "",
            5142104 + ""));

    public static void init(Context context) {
        db = Room.databaseBuilder(context,
                LikeDatabase.class, "DblikeStatus").build();
    }
}
