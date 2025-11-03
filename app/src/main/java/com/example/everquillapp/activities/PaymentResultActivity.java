package com.example.everquillapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.everquillapp.api.ApiClient;
import com.example.everquillapp.api.ApiService;
import com.example.everquillapp.models.ApiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentResultActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Uri uri = getIntent().getData();
		String path = uri != null ? uri.getPath() : null;

		String paymentId = getSharedPreferences("everquill", MODE_PRIVATE)
				.getString("lastPaymentId", null);

		ApiService api = ApiClient.getApiService(this);

		if (paymentId != null) {
			apiConfirm(api, paymentId, path);
		} else {
			Toast.makeText(this, "Payment link returned", Toast.LENGTH_SHORT).show();
			finishToPremium();
		}
	}

	private void apiConfirm(ApiService api, String paymentId, @Nullable String path) {
		api.getClass();
		ApiService svc = api;
		svc.getClass();
		svc.confirmPayment(paymentId).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
			@Override
			public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
				if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
					String message;
					Map<String, Object> data = response.body().getData();
					Object statusObj = data != null ? data.get("status") : null;
					String status = statusObj != null ? statusObj.toString() : "";
					if ("success".equalsIgnoreCase(status)) {
						message = "Thanh toán thành công. Tài khoản đã nâng cấp.";
					} else if ("failed".equalsIgnoreCase(status)) {
						message = "Thanh toán thất bại hoặc đã hủy.";
					} else {
						message = "Đang xử lý thanh toán...";
					}
					Toast.makeText(PaymentResultActivity.this, message, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(PaymentResultActivity.this, "Không xác nhận được thanh toán", Toast.LENGTH_SHORT).show();
				}
				finishToPremium();
			}

			@Override
			public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
				Toast.makeText(PaymentResultActivity.this, "Lỗi mạng khi xác nhận", Toast.LENGTH_SHORT).show();
				finishToPremium();
			}
		});
	}

	private void finishToPremium() {
		Intent intent = new Intent(this, PremiumActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}
}
