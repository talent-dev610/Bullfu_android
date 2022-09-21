package com.bullhu.android.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static JsonObject getJsonObject(JsonElement json) {

        if (json == null || json.isJsonNull())
            return null;

        return json.getAsJsonObject();
    }

    public static JsonArray getJsonArray(JsonElement json) {

        if (json == null || json.isJsonNull())
            return null;

        return json.getAsJsonArray();
    }

    public static String getString(JsonElement json) {

        if (json == null || json.isJsonNull())
            return "";

        return json.getAsString();
    }

    public static int getInt(JsonElement json) {

        if (json == null || json.isJsonNull())
            return 0;

        return json.getAsInt();
    }

    public static float getFloat(JsonElement json) {

        if (json == null || json.isJsonNull())
            return 0;

        return json.getAsFloat();
    }

    public static long getLong(JsonElement json) {

        if (json == null || json.isJsonNull())
            return 0;

        return json.getAsLong();
    }

    public static boolean getBoolean(JsonElement json) {

        if (json == null || json.isJsonNull())
            return false;

        return json.getAsBoolean();
    }

    public static double getDouble(JsonElement json) {

        if (json == null || json.isJsonNull())
            return 0;

        return json.getAsDouble();
    }

}
