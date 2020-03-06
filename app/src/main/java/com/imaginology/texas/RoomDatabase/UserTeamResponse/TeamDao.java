package com.imaginology.texas.RoomDatabase.UserTeamResponse;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TeamDao {

    @Insert
    void insertTeamToDatabase(List<TeamEntity> teamEntityList);

    @Query("SELECT * FROM team")
    List<TeamEntity> getAllTeamFromDatabase();

    @Query("SELECT * FROM team where userId= :uid")
    TeamEntity getTeamFromDatabase(Long uid);

    @Query("DELETE FROM team")
    void deleteAllTeamFromDatabse();
}
