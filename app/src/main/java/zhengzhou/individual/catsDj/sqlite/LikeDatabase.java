package zhengzhou.individual.catsDj.sqlite;

import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {LikeStatus.class}, version = 1)
public abstract class LikeDatabase extends RoomDatabase {
    public abstract LikeStatusDao likeStatusDao();
}