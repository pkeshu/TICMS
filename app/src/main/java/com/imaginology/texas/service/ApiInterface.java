package com.imaginology.texas.service;


import com.imaginology.texas.ClassRoutine.RoutineResponseDto;
import com.imaginology.texas.Counseling.CounselingDetail.CounselingDetailDto;
import com.imaginology.texas.Counseling.CounselingDto;
import com.imaginology.texas.Counseling.CounselingListDto;
import com.imaginology.texas.Counseling.CreateStudentCounseling.CreateStudentCounselingDto;
import com.imaginology.texas.Counseling.EditCounseling.EditCounselingDto;
import com.imaginology.texas.Counseling.ProcessCounseling.ProcessCounselDto;
import com.imaginology.texas.Courses.CoursesDto;
import com.imaginology.texas.Dashboard.Teacher.TeacherDashboardDTO;
import com.imaginology.texas.Login.ChangePasswordDto;
import com.imaginology.texas.Login.LoginDto;
import com.imaginology.texas.Logout.LogoutDto;
import com.imaginology.texas.Notification.PrivateMessage.PrivateMessageDto;
import com.imaginology.texas.Notification.PushNotification.NotificationDto;
import com.imaginology.texas.Notification.PushNotification.NotificationEditRequestDTO;
import com.imaginology.texas.Notification.PushNotification.NotificationReply.NotificationReplyDto;
import com.imaginology.texas.Notification.sendNotification.SendNotificationDTO;
import com.imaginology.texas.Students.StudentDto;
import com.imaginology.texas.Students.StudentListDto;
import com.imaginology.texas.Subjects.SubjectsDto;
import com.imaginology.texas.Teachers.TeacherDto;
import com.imaginology.texas.Teachers.TeacherListDto;
import com.imaginology.texas.Team.AddingMemberInTeam.AddMemberInTeamDto;
import com.imaginology.texas.Team.TeamCreationRequestDto;
import com.imaginology.texas.Team.TeamDetails.TeamResponseDto;
import com.imaginology.texas.Team.TeamDto;
import com.imaginology.texas.Users.UserDto;
import com.imaginology.texas.Users.UserListDto;
import com.imaginology.texas.util.ProfilePicEditRequest;
import com.imaginology.texas.util.ProfilePicEditResponseDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    final String preFix = "auth/api/v1/";
    final String preFixForRoutine = "routine/api/v1/";

    @POST(preFix + "login")
    Call<LoginDto> sendUsernameAndPassword(@Body LoginDto loginDto,
                                           @Header("domain") String domain);

    @PUT(preFix + "changePassword")
    Call<ResponseBody> changePassword(@Header("loginId") long loginId,
                                      @Header("customerId") long customerId,
                                      @Header("token") String token,
                                      @Header("userId") String userId,
                                      @Body ChangePasswordDto changePasswordDto);


    @PUT(preFix + "users/resetPassword")
    Call<ResponseBody> resetPassword(@Header("customerId") long customerId,
                                     @Header("loginId") long loginId,
                                     @Header("token") String token,
                                     @Body ChangePasswordDto changePasswordDto);

    @POST(preFix + "logout")
    Call<ResponseBody> logout(@Header("loginId") long loginId,
                              @Body LogoutDto logoutDto);

    @GET(preFix + "users")
    Call<UserListDto> listUsers(@Header("loginId") Long loginId,
                                @Header("customerId") Long customerId,
                                @Header("token") String token,
                                @Query("sort") String sort,
                                @Query("size") int size,
                                @Query("page") int page,
                                @Query("search") String search);

    @GET(preFix + "notifications")
    Call<NotificationDto> listNotification(@Header("loginId") Long lodinId,
                                           @Header("customerId") Long customerId,
                                           @Header("token") String token,
                                           @Query("page") Long page,
                                           @Query("size") Long size,
                                           @Query("sort") String sort);

    @GET(preFix + "notifications/seen/{id}")
    Call<ResponseBody> markNotificationAsRead(@Path("id") Long id,
                                              @Header("loginId") Long loginId,
                                              @Header("customerId") Long customerId,
                                              @Header("token") String token);

    @GET(preFix + "students")
    Call<StudentListDto> listStudents(@Header("customerId") Long customerId,
                                      @Header("token") String token,
                                      @Header("loginId") Long loginId,
                                      @Query("sort") String sort,
                                      @Query("size") int size,
                                      @Query("page") int page,
                                      @Query("search") String search);

    @GET(preFix + "teachers")
    Call<TeacherListDto> listTeachers(@Header("loginId") Long loginId,
                                      @Header("customerId") Long customerId,
                                      @Query("sort") String sort,
                                      @Query("size") int size,
                                      @Query("page") int page,
                                      @Query("search") String search,
                                      @Header("token") String token);

    @GET(preFix + "students/{studentId}")
    Call<StudentDto> studentInfo(@Header("customerId") Long customerId,
                                 @Path("studentId") Long studentId,
                                 @Header("token") String token,
                                 @Header("loginId") Long loginId);

    @GET(preFix + "users/{id}")
    Call<UserDto> userInfo(@Header("loginId") Long loginId,
                           @Path("id") Long id,
                           @Header("customerId") Long customerId,
                           @Header("token") String token);

    @GET(preFix + "teachers/{teacherId}")
    Call<TeacherDto> teacherInfo(
            @Header("loginId") Long loginId,
            @Header("customerId") Long customerId,
            @Path("teacherId") Long teacherId,
            @Header("token") String token);

    @POST(preFix + "notifications")
    Call<ResponseBody> sendNotification(@Header("customerId") Long customerId,
                                        @Header("loginId") Long loginId,
                                        @Body SendNotificationDTO sendNotificationDTO,
                                        @Header("token") String token);


    @GET(preFix + "courses")
    Call<List<CoursesDto>> getAllCourse(@Header("customerId") Long customerId,
                                        @Header("token") String token,
                                        @Header("userId") Long userId);

    @GET(preFix + "routines/courses/{courseId}/routines")
    Call<RoutineResponseDto> classRoutineDto(@Header("customerId") Long customerId,
                                             @Header("loginId") Long loginId,
                                             @Path("courseId") Long courseId,
                                             @Header("token") String token);

    @GET(preFix + "teams")
    Call<TeamDto> getTeams(@Header("customerId") Long customerId,
                           @Header("token") String token,
                           @Header("loginId") Long loginId,
                           @Query("page") int page,
                           @Query("size") int size,
                           @Query("sort") String sort,
                           @Query("search") String search
    );


    @GET(preFix + "subjects/{courseId}/{semester}")
    Call<List<SubjectsDto>> getListOfSubjects(@Header("customerId") Long customerId,
                                              @Path("courseId") Long courseId,
                                              @Path("semester") String semester,
                                              @Header("token") String token,
                                              @Header("loginId") Long loginId);

    @POST(preFix + "studentCounsellings")
    Call<CounselingDto> counselling(@Header("userId") Long loginId,
                                    @Header("customerId") Long customerId,
                                    @Header("token") String token,
                                    @Body CounselingDto counselingDto);

    @GET(preFix + "studentCounsellings")
    Call<CounselingListDto> getListOfCounsellings(@Query("courseId") List<Long
            > courseId,
                                                        @Header("customerId") Long customerId,
                                                        @Header("loginId") Long loginId,
                                                        @Header("token") String token,
                                                        @Query("ignoreDate") boolean ignoreDate,
                                                        @Query("fromDate") String fromDate,
                                                        @Query("page") int page,
                                                        @Query("size") int size,
                                                        @Query("sort") String sort,
                                                        @Query("toDate") String toDate
    );

    @GET(preFix + "studentCounsellings/{id}")
    Call<CounselingDetailDto> counsellingInfo(@Header("customerId") Long customerId,
                                              @Header("loginId") Long loginId,
                                              @Path("id") Long id,
                                              @Header("token") String token);

    @PUT(preFix + "studentCounsellings")
    Call<CounselingDto> editCounsellingInfo(@Header("customerId") Long customerId,
                                            @Header("loginId") Long loginId,
                                            @Body CounselingDto counselingDto,
                                            @Header("token") String token);

    @PUT(preFix + "users/{profileId}/profile-pic-change")
    Call<ProfilePicEditResponseDTO> uplodProfilePic(@Path("profileId") Long profileId,
                                                    @Header("loginId") Long loginId,
                                                    @Header("customerId") Long customerId,
                                                    @Body ProfilePicEditRequest profilePic,
                                                    @Header("token") String token);

    @PUT(preFix + "users/{profileId}/profile-pic-change")
    Call<ProfilePicEditRequest> uplodProfilePic1(@Path("profileId") Long profileId,
                                                 @Header("loginId") Long loginId,
                                                 @Body ProfilePicEditRequest profilePic,
                                                 @Header("token") String token);

    @GET(preFixForRoutine + "routines/teachers/{teacherId}")
    Call<TeacherDashboardDTO> getTeacherRoutines(@Path("teacherId") Long teacherId,
                                                 @Header("customerId") Long customerId,
                                                 @Header("loginId") Long loginId,
                                                 @Header("token") String token);


    @GET(preFix + "notifications/markAllAsRead")
    Call<ResponseBody> markasRead(@Header("loginId") Long loginId, @Header("customerId") Long customerId, @Header("token") String token);

    @DELETE(preFix + "notifications/{id}")
    Call<Void> deleteNotification(@Header("loginId") Long loginId, @Path("id") Long id, @Header("token") String token);

    @PUT(preFix + "notifications")
    Call<Void> editNotification(@Header("loginId") Long loginId,
                                @Header("customerId") Long customerId,
                                @Body NotificationEditRequestDTO notificationEditRequestDTO,
                                @Header("token") String token);

    @PUT(preFix + "notifications/{notificationId}/reply")
    Call<NotificationReplyDto.Notifications> replyNotification(@Header("loginId") Long loginId,
                                 @Header("customerId") Long customerId,
                                 @Path("notificationId") Long notificationId,
                                 @Body NotificationReplyDto notificationReplyDto,
                                 @Header("token") String token);

    @GET(preFix + "notifications/{notificationId}/reply")
    Call<NotificationReplyDto> listReplyNotification(@Header("loginId") Long loginId,
                                                     @Header("customerId") Long customerId,
                                                     @Path("notificationId") Long notificationId,
                                                     @Header("token") String token,
                                                     @Query("page") Long page,
                                                     @Query("size") Long size,
                                                     @Query("sort") String sort);

    @POST(preFix + "teams")
    Call<ResponseBody> createTeams(@Header("customerId") Long customerId,
                                   @Header("loginId") Long loginId,
                                   @Body TeamCreationRequestDto teamCreationRequestDto,
                                   @Header("token") String token
    );

    @PUT(preFix + "teams/{teamId}/members")
    Call<ResponseBody> addMemberToTeam(@Header("customerId") Long customerId,
                                       @Header("loginId") Long loginId,
                                       @Path("teamId") int teamId,
                                       @Body AddMemberInTeamDto addMemberInTeamDto,
                                       @Header("token") String token);


    @GET(preFix + "teams/{teamId}/members")
    Call<TeamResponseDto> getAllMemberFromTeam(@Header("customerId") Long customerId,
                                               @Header("loginId") Long loginId,
                                               @Path("teamId") int teamId,
                                               @Header("token") String token);

    @PUT(preFix + "students/{profileId}/profile-pic-change")
    Call<ProfilePicEditResponseDTO> changeStudentProfilePicture(@Header("customerId") Long customerId,
                                                          @Header("loginId") Long loginId,
                                                          @Path("profileId") Long profileId,
                                                          @Body ProfilePicEditRequest profilePicEditRequest,
                                                          @Header("token") String token
    );

    @PUT(preFix + "teachers/{profileId}/profile-pic-change")
    Call<ProfilePicEditResponseDTO> changeTeacherProfilePicture(@Header("customerId") Long customerId,
                                                          @Header("loginId") Long loginId,
                                                          @Path("profileId") Long profileId,
                                                          @Body ProfilePicEditRequest profilePicEditRequest,
                                                          @Header("token") String token
    );
    @PUT(preFix+"studentCounsellings/{id}/process-counsil")
    Call<ResponseBody> processCounsil(@Header("customerId") Long customerId,
                                      @Header("loginId") Long loginId,
                                      @Path("id") int id,
                                      @Body ProcessCounselDto editCounselDto,
                                      @Header("token") String token
                                      );
    @PUT(preFix + "studentCounsellings/{profileId}/profile-pic-change")
    Call<ResponseBody> editProfilePictureofStudentInCounseling(@Header("customerId") Long customerId,
                                                          @Header("loginId") Long loginId,
                                                          @Path("profileId") int profileId,
                                                          @Body ProfilePicEditRequest profilePicEditRequest,
                                                          @Header("token") String token);
    @PUT(preFix+ "studentCounsellings")
    Call<ResponseBody> editStudentCounselling(@Header("customerId") Long customerId,
                                              @Header("loginId") Long loginId,
                                              @Body EditCounselingDto editCounselingDto,
                                              @Header("token") String token
                                              );
    @POST(preFix+"studentCounsellings")
    Call<ResponseBody> createStudentCounselling(@Header("customerId") Long customerId,
                                                @Header("loginId") Long loginId,
                                                @Body CreateStudentCounselingDto createStudentCounselingDto,
                                                @Header("token") String token
                                                );
    @POST(preFix+"notifications/private")
    Call<ResponseBody> sendPrivateMessage(@Header("customerId") Long CustomerId,
                                          @Header("loginId") Long loginId,
                                          @Body PrivateMessageDto privateMessageDto,
                                          @Header("token") String token);
}
