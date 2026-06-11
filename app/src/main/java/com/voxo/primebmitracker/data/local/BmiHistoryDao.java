package com.voxo.primebmitracker.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BmiHistoryDao {

    @Query("SELECT * FROM bmi_history ORDER BY timestamp DESC")
    LiveData<List<BmiHistoryEntity>> observeAllOrdered();

    @Query("SELECT * FROM bmi_history ORDER BY timestamp DESC")
    List<BmiHistoryEntity> getAllOrderedSync();

    @Query("SELECT * FROM bmi_history ORDER BY timestamp DESC LIMIT 1")
    BmiHistoryEntity getLatestSync();

    @Insert
    long insert(BmiHistoryEntity entity);

    @Query("DELETE FROM bmi_history WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM bmi_history")
    void deleteAll();
}
