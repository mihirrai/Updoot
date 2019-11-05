package com.ducktapedapps.updoot.utils;

import com.ducktapedapps.updoot.model.CommentData;
import com.ducktapedapps.updoot.model.LinkData;
import com.ducktapedapps.updoot.model.ListingData;
import com.ducktapedapps.updoot.model.Thing;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


public class ThingDeserializer implements JsonDeserializer<Thing> {
    private static final String TAG = "ThingDeserializer";

    @Override
    public Thing deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = null;
        if (json instanceof JsonObject) {
            jsonObject = json.getAsJsonObject();
        } else if (json instanceof JsonArray) {
            jsonObject = json.getAsJsonArray().get(1).getAsJsonObject();
        }
        if (jsonObject != null) {
            String kind = jsonObject.get("kind").getAsString();
            if (kind != null) {
                JsonElement element = jsonObject.get("data");
                if (element != null)
                    switch (kind) {
                        case "Listing":
                            return new Thing("Listing", context.deserialize(element, ListingData.class));
                        case "t3":
                            return new Thing("t3", context.deserialize(element, LinkData.class));
                        case "more":
                        case "t1":
                            return new Thing("t1", context.deserialize(element, CommentData.class));
                    }
            }
        }
        return null;
    }
}
