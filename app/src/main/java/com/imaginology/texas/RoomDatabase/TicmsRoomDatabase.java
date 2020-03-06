package com.imaginology.texas.RoomDatabase;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.imaginology.texas.RoomDatabase.RoutineEntity.RoutineEntity;
import com.imaginology.texas.RoomDatabase.RoutineEntity.RoutineDao;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseDao;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamDao;
import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamEntity;

@Database(entities={UserLoginResponseEntity.class,
        RoutineEntity.class,TeamEntity.class},version = 7, exportSchema=false)
public abstract class TicmsRoomDatabase extends RoomDatabase {
    private static String TAG= TicmsRoomDatabase.class.getSimpleName();
    public abstract UserLoginResponseDao userLoginResponseDao();
    public abstract RoutineDao getRoutineDao();
    public abstract TeamDao getTeamDao();
    private static TicmsRoomDatabase ticmsRoomDatabase=null;

    public static TicmsRoomDatabase getDatabaseInstance(Context context){
        Log.d(TAG, "Get database instance called");
        if(ticmsRoomDatabase==null){
            synchronized (TicmsRoomDatabase.class){
                if(ticmsRoomDatabase==null){
                    ticmsRoomDatabase=Room.databaseBuilder(context.getApplicationContext(),
                            TicmsRoomDatabase.class, "ticms_database")
                            .addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4,MIGRATION_4_5,MIGRATION_5_6,
                                    MIGRATION_6_7)
                            .build();
                    Log.d(TAG, "Database instance created");
                }
            }
        }
        return ticmsRoomDatabase;
    }


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE routine(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    +"course TEXT,"
                    +"semester TEXT,"
                    +"day TEXT,"
                    +"startTime TEXT,"
                    +"endTime TEXT,"
                    +"subject TEXT)"
            );
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE login_instance "+
                    "ADD COLUMN status TEXT");
        }
    };

    static final Migration MIGRATION_3_4=new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

           try {
               database.execSQL("create table team(id INTEGER Primary key autoincrement," +
                       "name TEXT," +
                       "type TEXT,userId INTEGER)"
               );
           }catch (Exception e){
               e.printStackTrace();
           }

        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE login_instance "+
                    "ADD COLUMN mobileNumber TEXT");
        }
    };
    static final Migration MIGRATION_5_6 = new Migration(5,6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE login_instance "+
                    "ADD COLUMN gender TEXT");
        }
    };

    static final Migration MIGRATION_6_7 = new Migration(6,7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            try {
                database.execSQL("ALTER TABLE login_instance "+
                        "ADD COLUMN courseId INTEGER");
                database.execSQL("ALTER TABLE login_instance "+
                        "ADD COLUMN courseName TEXT");
            } catch (Exception e){
                System.out.println("Error:::"+e.getMessage());
            }
        }
    };



}
