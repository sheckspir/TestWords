package com.example.alexander.testwords.api;

import com.example.alexander.testwords.model.Word;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAdapter {
    private static final String BASE_URL = "http://dictionary.skyeng.ru/api/v1/";
    private static RetrofitAdapter instance = new RetrofitAdapter();
    private Retrofit retrofit;

    //Непотокобезопасно, синглтон надо нормально уметь писать
    //Зачем хранить инстанс адаптера, почему бы не хранить инстанс API?
    // На счёт потокобезопасности соглашусь на 100%. Думаю решение с не ленивой инициализацией будет подходящим
    // это сделает его потокобезопасным, а ленивость нам тут не к чему, если мы будем обращаться к адаптеру, то только за получением апи
    // В данном случае причин не хранить инстанс API не вижу. Но если разделять апи и делать их в виде нескольких классов, это может потребовать
    // больше затрат на поддержку, а также меньше затребует на хранение в памяти, что потенциально уменьшит вероятность OutOfMemory.
    public static RetrofitAdapter getInstance() {
        return instance;
    }

    public RetrofitAdapter() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        BooleanSerializer booleanSerializer = new BooleanSerializer();
        WordDeserializer wordDeserializer = new WordDeserializer();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Word.class, wordDeserializer)
                .registerTypeAdapter(Boolean.class, booleanSerializer)
                .registerTypeAdapter(boolean.class, booleanSerializer)
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

    public <T> T create(final Class<T> service) {
        return retrofit.create(service);
    }

    private static class BooleanSerializer implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {

        @Override
        public JsonElement serialize(Boolean arg0, Type arg1, JsonSerializationContext arg2) {
            return new JsonPrimitive(arg0 ? 1 : 0);
        }

        @Override
        public Boolean deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            return arg0.getAsInt() == 1;
        }
    }

    public static class WordDeserializer implements JsonDeserializer<Word> {
        private static final String NEED_ADD = "http:";

        @Override
        public Word deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            BooleanSerializer booleanSerializer = new BooleanSerializer();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Boolean.class, booleanSerializer)
                    .registerTypeAdapter(boolean.class, booleanSerializer)
                    .create();
            Word word = gson.fromJson(json,Word.class);
            List<String> images = word.getImages();
            for (int i = 0; i < images.size(); i++) {
                images.set(i, NEED_ADD + images.get(i));
            }
            return word;
        }
    }
}
