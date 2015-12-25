package com.example.limethecoder.eventkicker.net;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Тарас - матрас on 12/20/2015.
 */
public class ApiResponse<T> {
    @SerializedName("success")
    public boolean success;

    @SerializedName("single")
    public T single;

    @SerializedName("multiple")
    public ArrayList<T> multiple;
}
