package com.imaginology.texas.util;

public class SemesterUtil {

    private static String[] getAllSemesters(){
        String[] semesters = new String[Semester.values().length];
        for(int i = 0; i < semesters.length; i++){
            semesters[i] =  Semester.values()[i].name();
        }
        return semesters;
    }

    private static String[] getPlusTwoSemester(){
        String[] semesters = new String[3];
        for(int i = 0; i < 3; i++){
            semesters[i] =  Semester.values()[i].name();
        }
        return semesters;
    }

    private static String[] getMasterSemester(){
        String[] semesters = new String[5];
        for(int i = 0; i < 5; i++){
            semesters[i] =  Semester.values()[i].name();
        }
        return semesters;
    }

    public static String[] getSemester(CourseLevel courseLevel){
        switch (courseLevel){
            case PLUS_TWO:
                return getPlusTwoSemester();
            case MASTERS:
                return getMasterSemester();
                default:
                    return getAllSemesters();
        }
    }
}
