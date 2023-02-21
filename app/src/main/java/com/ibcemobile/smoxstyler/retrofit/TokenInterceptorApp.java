package com.ibcemobile.smoxstyler.retrofit;

import android.content.Context;
import android.util.Log;

import com.ibcemobile.smoxstyler.SessionManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class TokenInterceptorApp implements Interceptor {
    private String token;
    private Context context;

    public TokenInterceptorApp(Context context) {
        this.context = context;
        try {

            token = SessionManager.Companion.getInstance(context).getApiKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {

        if (!NetworkUtil.isOnline(context)) {
            throw new NoConnectivityException();
        }

        if (token != null && !token.isEmpty()) {
            Request original = chain.request();

            //Log.e("okhttp", "token: " + token);

            Request request = original.newBuilder()
                    .header("Authentication", token)
                    .build();
            Response response = chain.proceed(request);

            Log.e("intercepter", "token: Authentication " + request.header("Authentication"));

            if (response.code() == 401) {
               /* Preferences.saveBoolean(context, ConstantValue.KEY_IS_LOGIN, false);
                context.startActivity(new Intent(context, SignInActivity.class));*/
                return response;
            }
            return response;
        } else {
            /*Preferences.saveBoolean(context, ConstantValue.KEY_IS_LOGIN, false);
            context.startActivity(new Intent(context, SignInActivity.class));*/
            return null;
        }
    }
}
