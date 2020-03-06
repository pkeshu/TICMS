package com.imaginology.texas.RoomDatabase.RoutineEntity;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RoutineDao {
    @Insert
    void storeRoutineToDatabase(List<RoutineEntity> roomRoutineList);

    @Query("SELECT * FROM routine")
    List<RoutineEntity> getAllRoutineFromDatabase();

    @Query("DELETE FROM routine")
    void deleteAllRoutineFromDatabse();
}
