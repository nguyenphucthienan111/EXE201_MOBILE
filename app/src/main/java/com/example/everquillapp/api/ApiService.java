package com.example.everquillapp.api;

import com.example.everquillapp.models.ApiResponse;
import com.example.everquillapp.models.Journal;
import com.example.everquillapp.models.Template;
import com.example.everquillapp.models.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {
    
    // ============ Authentication ============
    @POST("auth/register")
    Call<ApiResponse<User>> register(@Body Map<String, String> body);
    
    @POST("auth/login")
    Call<ApiResponse<User>> login(@Body Map<String, String> body);
    
    @POST("auth/refresh")
    Call<ApiResponse<String>> refreshToken();
    
    @POST("auth/logout")
    Call<ApiResponse<String>> logout();
    
    @POST("auth/verify")
    Call<ApiResponse<User>> verifyEmailWithCode(@Body Map<String, String> body);
    
    @POST("auth/forgot-password")
    Call<ApiResponse<String>> forgotPassword(@Body Map<String, String> body);
    
    @POST("auth/reset-password/:token")
    Call<ApiResponse<String>> resetPassword(@Path("token") String token, @Body Map<String, String> body);
    
    @GET("auth/verify-email/:token")
    Call<ApiResponse<String>> verifyEmail(@Path("token") String token);
    
    // ============ User Profile ============
    @GET("users/me")
    Call<User> getCurrentUser();
    
    @PUT("users/me")
    Call<ApiResponse<User>> updateProfile(@Body Map<String, String> body);
    
    @POST("users/change-password")
    Call<ApiResponse<String>> changePassword(@Body Map<String, String> body);
    
    @DELETE("users/me")
    Call<ApiResponse<String>> deleteAccount(@Body Map<String, String> body);
    
    @Multipart
    @POST("users/avatar")
    Call<ApiResponse<User>> uploadAvatar(@Part MultipartBody.Part avatar);
    
    @DELETE("users/avatar")
    Call<ApiResponse<String>> deleteAvatar();
    
    // ============ Journals ============
    @GET("journals")
    Call<List<Journal>> getJournals(@QueryMap Map<String, String> params);
    
    @GET("journals/{id}")
    Call<Journal> getJournal(@Path("id") String id);
    
    @POST("journals")
    Call<ApiResponse<Journal>> createJournal(@Body Map<String, Object> body);
    
    @PUT("journals/{id}")
    Call<ApiResponse<Journal>> updateJournal(@Path("id") String id, @Body Map<String, Object> body);
    
    @DELETE("journals/{id}")
    Call<ApiResponse<String>> deleteJournal(@Path("id") String id);
    
    @POST("journals/sync/{id}")
    Call<ApiResponse<Journal>> markSynced(@Path("id") String id);
    
    // ============ AI Features ============
    @POST("journals/analyze/{id}")
    Call<ApiResponse<Map<String, Object>>> analyzeJournal(@Path("id") String id);
    
    @POST("journals/suggest-basic")
    Call<ApiResponse<Map<String, Object>>> getSuggestionsBasic(@Body Map<String, String> body);
    
    @POST("journals/suggest")
    Call<ApiResponse<Map<String, Object>>> getSuggestionsAdvanced(@Body Map<String, String> body);
    
    @GET("journals/{id}/analysis-history")
    Call<ApiResponse<List<Map<String, Object>>>> getAnalysisHistory(@Path("id") String id);
    
    // ============ Templates ============
    @GET("templates")
    Call<ApiResponse<List<Template>>> getTemplates();
    
    @POST("templates/{id}/use")
    Call<ApiResponse<Template>> useTemplate(@Path("id") String id);
    
    @Multipart
    @POST("templates/upload")
    Call<ApiResponse<Template>> uploadTemplate(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );
    
    @DELETE("templates/{id}")
    Call<ApiResponse<String>> deleteTemplate(@Path("id") String id);
    
    // ============ Dashboard ============
    @GET("journals/dashboard")
    Call<ApiResponse<Map<String, Object>>> getDashboard(@Query("period") String period);
    
    @GET("journals/stats")
    Call<ApiResponse<Map<String, Object>>> getStats();
    
    // ============ Premium / Plans ============
    @GET("plans")
    Call<ApiResponse<List<Map<String, Object>>>> getPlans();
    
    @POST("payments/premium")
    Call<ApiResponse<Map<String, String>>> createPayment(@Body Map<String, Object> body);
    
    @GET("payments/{id}")
    Call<ApiResponse<Map<String, Object>>> getPayment(@Path("id") String id);

    @GET("payments/confirm/{paymentId}")
    Call<ApiResponse<Map<String, Object>>> confirmPayment(@Path("paymentId") String paymentId);

    @GET("payments/check-user-plan")
    Call<ApiResponse<Map<String, Object>>> checkUserPlan();
    
    // ============ Notifications ============
    @GET("notifications")
    Call<ApiResponse<Map<String, Object>>> getNotifications(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("unread") Boolean unread
    );
    
    @GET("notifications/unread-count")
    Call<ApiResponse<Integer>> getUnreadCount();
    
    @PUT("notifications/{id}/read")
    Call<ApiResponse<String>> markAsRead(@Path("id") String id);
    
    @PUT("notifications/read-all")
    Call<ApiResponse<String>> markAllAsRead();
    
    // ============ Reviews ============
    @GET("reviews")
    Call<ApiResponse<List<Map<String, Object>>>> getReviews(@Query("page") int page);
    
    @POST("reviews")
    Call<ApiResponse<Map<String, Object>>> createReview(@Body Map<String, Object> body);
}

