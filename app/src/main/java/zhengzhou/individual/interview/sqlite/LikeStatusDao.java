package zhengzhou.individual.interview.sqlite;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LikeStatusDao {
    @Query("SELECT * FROM  likeStatus WHERE uid LIKE :id LIMIT 1")
     LikeStatus findByName(long id);

    @Delete
     void delete(LikeStatus likeStatus);

    @Insert
   void insert(LikeStatus likeStatus);

    @Query("SELECT * FROM likeStatus")
    List<LikeStatus> getAll();
}