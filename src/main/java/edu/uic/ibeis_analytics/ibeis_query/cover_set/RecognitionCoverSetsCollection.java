package edu.uic.ibeis_analytics.ibeis_query.cover_set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class RecognitionCoverSetsCollection {

    private List<RecognitionCoverSet> coverSets = new ArrayList<>();

    public List<RecognitionCoverSet> getCoverSets() {
        return coverSets;
    }

    public void add(RecognitionCoverSet coverSet) {
        coverSets.add(coverSet);
    }

    public String toJsonString() {
        return getGson().toJson(this);
    }

    public static RecognitionCoverSetsCollection fromJsonString(String jsonString) {
        return getGson().fromJson(jsonString, RecognitionCoverSetsCollection.class);
    }

    private static Gson getGson() {
        return new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().create();
    }
}
