package edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class QueryRecordsCollection {

    private List<QueryRecord> queryRecords = new ArrayList<>();

    public List<QueryRecord> getQueryRecords() {
        return queryRecords;
    }

    public void add(QueryRecord queryRecord) {
        queryRecords.add(queryRecord);
    }

    public String toJsonString() {
        return getGson().toJson(this);
    }

    public static QueryRecordsCollection fromJsonString(String jsonString) {
        return getGson().fromJson(jsonString, QueryRecordsCollection.class);
    }

    private static Gson getGson() {
        return new GsonBuilder().serializeNulls().create();
    }
}
