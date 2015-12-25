package com.example.limethecoder.eventkicker;

import android.util.Base64;

import com.example.limethecoder.eventkicker.net.ApiResponse;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Тарас - матрас on 12/20/2015.
 */

public class ServiceManager {
    private static final String BASE_URL = "http://192.168.1.116:3600";

    public static MyApiEndpointInterface newService() {
        return newService(null, null);
    }

    public static MyApiEndpointInterface newService(String username, String password) {
        OkHttpClient httpClient = new OkHttpClient();

        if (username != null && password != null) {
            String credentials = username + ":" + password;
            final String basic =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            httpClient.interceptors().clear();
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basic)
                    .header("Accept", "applicaton/json")
                    .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(httpClient)
                .build();

        MyApiEndpointInterface apiService =
                retrofit.create(MyApiEndpointInterface.class);
        return apiService;
    }

    public interface MyApiEndpointInterface {

        @POST("/api/users")
        Call<ApiResponse<User>> createUser(@Body User user);

        @POST("/api/events")
        Call<ApiResponse<EventItem>> createEvent(@Body EventItem event);

        @POST("/api/login")
        Call<ApiResponse<User>> login();

        @GET("/api/events/named")
        Call<ApiResponse<EventItem>> loadEvents();

        @GET("/api/events/{id}")
        Call<ApiResponse<EventItem>> getEvent(@Path("id") int eventId);


        @GET("/api/users")
        Call<ApiResponse<EventItem>> loadUsers();

        @GET("/api/users/{id}")
        Call<ApiResponse<User>> getUser(@Path("id") int userId);

        @GET("/api/users/{id}/events")
        Call<ApiResponse<EventItem>> getUserEvents(@Path("id") int userId);
        /*
           @POST("/api/events")
        Call<ApiResponse<EventItem>> createEvent(@Query("name") String name, @Query("authorId") int authorId,
                                              @Query("description") String description, @Query("timeCreated") String timeCreated,
                                              @Query("timeScheduled") String timeScheduled);
         */
    }

}
