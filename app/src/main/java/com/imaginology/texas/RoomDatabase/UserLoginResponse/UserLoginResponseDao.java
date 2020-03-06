package com.imaginology.texas.RoomDatabase.UserLoginResponse;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface UserLoginResponseDao {
    @Insert
    void insertUserDetail(UserLoginResponseEntity mUserLoginResponseEntity);

    @Query("DELETE FROM login_instance")
    void deleteAllLoginInstances();

    @Update
    void updateUerDetail(UserLoginResponseEntity mUserLoginResponseEntity);

    @Query("SELECT * FROM login_instance LIMIT 1 OFFSET 0")
    UserLoginResponseEntity getLoginInstance();

    @Query("UPDATE login_instance SET profilePicture = :ImageUrl WHERE id = :uid")
    void updateImageUrl(long uid, String ImageUrl);



}
