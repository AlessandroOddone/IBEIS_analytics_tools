package edu.uic.ibeis_analytics.ibeis_query.cover_set;

import edu.uic.ibeis_analytics.ibeis_query.AnnotationDbElement;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RecognitionCoverSet implements Comparable<RecognitionCoverSet> {

    private AnnotationDbElement annotationDbElement;
    private List<IbeisAnnotation> coveredAnnotations = new ArrayList<>();

    public RecognitionCoverSet(AnnotationDbElement annotationDbElement) {
        this.annotationDbElement = annotationDbElement;
    }

    public AnnotationDbElement getAnnotationDbElement() {
        return annotationDbElement;
    }

    public List<IbeisAnnotation> getCoveredAnnotations() {
        return coveredAnnotations;
    }

    public void add(IbeisAnnotation annotation) {
        this.coveredAnnotations.add(annotation);
    }

    public void remove(IbeisAnnotation annotation) {
        remove(Arrays.asList(annotation));
    }

    public void remove(Collection<IbeisAnnotation> annotations) {
        this.coveredAnnotations.removeAll(annotations);
    }

    @Override
    public String toString() {
        StringBuilder coveredAnnotsSringBuilder = new StringBuilder();
        for (IbeisAnnotation annotation : coveredAnnotations) {
            coveredAnnotsSringBuilder.append(annotation.getId() + ",");
        }
        coveredAnnotsSringBuilder.deleteCharAt(coveredAnnotsSringBuilder.lastIndexOf(","));

        return "[annotation_db_element:{aid:" + annotationDbElement.getAnnotation().getId() +
                ",is_giraffe_threshold:" + annotationDbElement.getIsGiraffeThreshold() +
                ",rec_threshold:" + annotationDbElement.getRecognitionThreshold() + "}" +
                ",covered_aids: " + coveredAnnotsSringBuilder.toString() + "]";
    }

    @Override
    public int compareTo(RecognitionCoverSet o) {
        if (this.coveredAnnotations.size() == 1 && o.getCoveredAnnotations().size() == 1) {
            if (this.coveredAnnotations.get(0) == this.annotationDbElement.getAnnotation() &&
                    o.getCoveredAnnotations().get(0) != o.getAnnotationDbElement().getAnnotation()) {
                return -1;
            }
        }
        return Integer.compare(this.coveredAnnotations.size(),o.getCoveredAnnotations().size());
    }
}

