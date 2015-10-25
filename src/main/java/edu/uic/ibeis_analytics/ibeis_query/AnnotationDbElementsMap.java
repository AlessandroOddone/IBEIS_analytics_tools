package edu.uic.ibeis_analytics.ibeis_query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

public class AnnotationDbElementsMap {

    private HashMap<Long,AnnotationDbElement> annotationDbElementHashMap = new HashMap<>();

    public HashMap<Long,AnnotationDbElement> getElements() {
        return annotationDbElementHashMap;
    }

    public void setElements(HashMap<Long,AnnotationDbElement> annotationDbElementHashMap) {
        this.annotationDbElementHashMap = annotationDbElementHashMap;
    }

    public void add(AnnotationDbElement annotationDbElement) {
        annotationDbElementHashMap.put(annotationDbElement.getAnnotation().getId(), annotationDbElement);
    }

    public void remove(AnnotationDbElement annotationDbElement) {
        annotationDbElementHashMap.remove(annotationDbElement.getAnnotation().getId());
    }

    public String toJsonString() {
        return getGson().toJson(this);
    }

    public static AnnotationDbElementsMap fromJsonString(String jsonString) {
        return getGson().fromJson(jsonString, AnnotationDbElementsMap.class);
    }

    private static Gson getGson() {
        return new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().create();
    }
}
