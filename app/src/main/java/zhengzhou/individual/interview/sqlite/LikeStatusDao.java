package zhengzhou.individual.interview.sqlite;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface LikeStatusDao {
    @Query("SELECT * FROM  likeStatus WHERE uid LIKE :id LIMIT 1")
    public LikeStatus findByName(long id);

    @Delete
    public void delete(LikeStatus likeStatus);

    @Insert
    public void insert(LikeStatus likeStatus);
}