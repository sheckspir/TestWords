package com.example.alexander.testwords.api;

import com.example.alexander.testwords.model.Word;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DictionaryApi {

    @GET("wordtasks")
    Call<List<Word>> getTranslate(@Query("meaningIds") String wordIds, @Query("width") int widthOfScreen);
}
