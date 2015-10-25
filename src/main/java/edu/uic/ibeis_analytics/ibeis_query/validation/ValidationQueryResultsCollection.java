package edu.uic.ibeis_analytics.ibeis_query.validation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class ValidationQueryResultsCollection {
    private List<ValidationQueryResult> validationQueryResults = new ArrayList<>();

    public List<ValidationQueryResult> getResults() {
        return validationQueryResults;
    }

    public void add(ValidationQueryResult result) {
        validationQueryResults.add(result);
    }

    public String toJsonString() {
        return getGson().toJson(this);
    }

    public static ValidationQueryResultsCollection fromJsonString(String jsonString) {
        return getGson().fromJson(jsonString, ValidationQueryResultsCollection.class);
    }

    private static Gson getGson() {
        return new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().create();
    }
}
