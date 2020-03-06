package com.imaginology.texas.service;

import android.content.Context;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    private static final String TAG = "RetrofitManager";
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://devapp.texasintl.edu.np/";
//    private static final String BASE_URL = "https://api.texasimaginology.com/";
//public static final String BASE_URL = "http://10.10.20.136:8086/auth/api/v1/";

    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_PRAGMA = "Pragma";

    private static Retrofit mRetrofit, mCachedRetrofit;

    private static Cache mCache;
    private static OkHttpClient mOkHttpClient, mCachedOkHttpClient;

    private ApiClient() {
    }

    public static Retrofit getClient() {

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getUnsafeOkHttpClient().build())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient.Builder getUnsafeOkHttpClient() {
        //Create Okhttp client for Request and Response Log
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.addInterceptor(interceptor).build();

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Retrofit getRetrofit(Context mContext) {
        if (mRetrofit == null) {
            // Add all interceptors you want (headers, URL, logging)
//
//            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
//                    .addInterceptor(provideOfflineCacheInterceptor())
//                    .addNetworkInterceptor(provideCacheInterceptor())
//                    .cache(provideCache());
//
//            mOkHttpClient = httpClient.build();

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    // Add your adapter factory to handler Errors
//                    .client(mOkHttpClient)
                    .client(getUnsafeOkHttpClient(mContext, mCache).build())
                    .build();
        }

        return mRetrofit;
    }

    //For CertPathValidatorException : Trust anchor for certificate path not found - Retrofit Android while using https urls
    private static OkHttpClient.Builder getUnsafeOkHttpClient(Context mContext, Cache mCache) {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.addInterceptor(provideOfflineCacheInterceptor(mContext))
                    .addNetworkInterceptor(provideCacheInterceptor(mContext))
                    .cache(provideCache(mContext, mCache));
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static Cache provideCache(Context mContext, Cache mCache) {
        if (mCache == null) {
            try {
                mCache = new Cache(new File(mContext.getCacheDir(), "http-cache"),
                        10 * 1024 * 1024); // 10 MB
            } catch (Exception e) {
                Log.e(TAG, "Could not create Cache!");
            }
        }

        return mCache;
    }


    private static Interceptor provideOfflineCacheInterceptor(Context context) {
        return chain -> {
            Request request = chain.request();

            if (!isConnected(context)) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        };
    }

    private static Interceptor provideForcedOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();

            CacheControl cacheControl = new CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build();

            request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build();

            return chain.proceed(request);
        };
    }

    public static boolean isConnected(Context mContext) {
        try {
            android.net.ConnectivityManager e = (android.net.ConnectivityManager) mContext.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = e.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        return false;
    }

    public static Interceptor provideCacheInterceptor(Context context) {
        return chain -> {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl;

            if (isConnected(context)) {
                cacheControl = new CacheControl.Builder()
                        .maxAge(0, TimeUnit.SECONDS)
                        .build();
            } else {
                cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();
            }

            return response.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build();

        };
    }

//    public Retrofit getCachedRetrofit() {
//        if (mCachedRetrofit == null) {
//            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
//                    // Add all interceptors you want (headers, URL, logging)
//                    .addInterceptor(provideForcedOfflineCacheInterceptor())
//                    .cache(provideCache());
//
//            mCachedOkHttpClient = httpClient.build();
//
//            mCachedRetrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
//                    .client(mCachedOkHttpClient)
//                    .build();
//        }
//
//        return mCachedRetrofit;
//    }


//    public void clean() {
//        if (mOkHttpClient != null) {
//            // Cancel Pending Request
//            mOkHttpClient.dispatcher().cancelAll();
//        }
//
//        if (mCachedOkHttpClient != null) {
//            // Cancel Pending Cached Request
//            mCachedOkHttpClient.dispatcher().cancelAll();
//        }
//
//        mRetrofit = null;
//        mCachedRetrofit = null;
//
//        if (mCache != null) {
//            try {
//                mCache.evictAll();
//            } catch (IOException e) {
//                Log.e(TAG, "Error cleaning http cache");
//            }
//        }
//
//        mCache = null;
//    }


}