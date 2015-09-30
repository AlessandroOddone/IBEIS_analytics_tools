package edu.uic.ibeis_analytics.ibeis_query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryRecordsCollection;

import java.util.ArrayList;
import java.util.List;

public class AnnotationCoverSetsCollection {

    private List<AnnotationCoverSet> coverSets = new ArrayList<>();

    public List<AnnotationCoverSet> getCoverSets() {
        return coverSets;
    }

    public void add(AnnotationCoverSet coverSet) {
        coverSets.add(coverSet);
    }

    public String toJsonString() {
        return getGson().toJson(this);
    }

    public static AnnotationCoverSetsCollection fromJsonString(String jsonString) {
        return getGson().fromJson(jsonString, AnnotationCoverSetsCollection.class);
    }

    private static Gson getGson() {
        return new GsonBuilder().serializeNulls().create();
    }
}
