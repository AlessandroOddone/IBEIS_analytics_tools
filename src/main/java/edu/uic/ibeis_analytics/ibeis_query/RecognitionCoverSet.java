package edu.uic.ibeis_analytics.ibeis_query;

import edu.uic.ibeis_java_api.api.IbeisAnnotation;

import java.util.List;

public class RecognitionCoverSet {

    private IbeisAnnotation annotation;
    private List<IbeisAnnotation> coveredAnnotations;

    public RecognitionCoverSet(IbeisAnnotation annotation) {
        this.annotation = annotation;
    }

    public IbeisAnnotation getAnnotation() {
        return annotation;
    }

    public List<IbeisAnnotation> getCoveredAnnotations() {
        return coveredAnnotations;
    }

    public void setCoveredAnnotations(List<IbeisAnnotation> coveredAnnotations) {
        this.coveredAnnotations = coveredAnnotations;
    }

    public void add(IbeisAnnotation coveredAnnotation) {
        this.coveredAnnotations.add(coveredAnnotation);
    }

    @Override
    public String toString() {
        StringBuilder coveredAnnotsSringBuilder = new StringBuilder();
        for (IbeisAnnotation ibeisAnnotation : coveredAnnotations) {
            coveredAnnotsSringBuilder.append(ibeisAnnotation.getId() + ",");
        }
        coveredAnnotsSringBuilder.deleteCharAt(coveredAnnotsSringBuilder.lastIndexOf(","));

        return "[aid: " + annotation.getId() + ", covered_aids: " + coveredAnnotsSringBuilder.toString() + "]";
    }

}

