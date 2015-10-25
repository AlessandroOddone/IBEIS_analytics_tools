package edu.uic.ibeis_analytics.ibeis_query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class AnnotationDbElementsList {

    private List<AnnotationDbElement> annotationDbElementList = new ArrayList<>();

    public List<AnnotationDbElement> getElements() {
        return annotationDbElementList;
    }

    public void setElements(List<AnnotationDbElement> annotationDbElementList) {
        this.annotationDbElementList = annotationDbElementList;
    }

    public void add(AnnotationDbElement annotationDbElement) {
        annotationDbElementList.add(annotationDbElement);
    }

    public void remove(AnnotationDbElement annotationDbElement) {
        annotationDbElementList.remove(annotationDbElement);
    }

    public String toJsonString() {
        return getGson().toJson(this);
    }

    public static AnnotationDbElementsList fromJsonString(String jsonString) {
        return getGson().fromJson(jsonString, AnnotationDbElementsList.class);
    }

    private static Gson getGson() {
        return new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().create();
    }
}
