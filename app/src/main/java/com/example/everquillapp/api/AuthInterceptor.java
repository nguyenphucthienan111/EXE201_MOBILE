package com.example.everquillapp.api;

import android.content.Context;
import com.example.everquillapp.utils.TokenManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;

    public AuthInterceptor(Context context) {
        this.tokenManager = new TokenManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Skip auth header for login/register endpoints
        String path = originalRequest.url().encodedPath();
        if (path.contains("/auth/login") || 
            path.contains("/auth/register") ||
            path.contains("/auth/google")) {
            return chain.proceed(originalRequest);
        }

        String token = tokenManager.getToken();
        
        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest);
        }

        // Add Authorization header
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }
}


