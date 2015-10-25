package edu.uic.ibeis_analytics.ibeis_query;

import edu.uic.ibeis_java_api.api.IbeisAnnotation;
import edu.uic.ibeis_java_api.api.IbeisIndividual;

public class AnnotationDbElement {
    private IbeisAnnotation annotation;
    private IbeisIndividual individual;
    private double isGiraffeThreshold;
    private double recognitionThreshold;

    public AnnotationDbElement(IbeisAnnotation annotation, IbeisIndividual individual, double isGiraffeThreshold,
                               double recognitionThreshold) {
        this.annotation = annotation;
        this.individual = individual;
        this.isGiraffeThreshold = isGiraffeThreshold;
        this.recognitionThreshold = recognitionThreshold;
    }

    public IbeisAnnotation getAnnotation() {
        return annotation;
    }

    public IbeisIndividual getIndividual() {
        return individual;
    }

    public double getIsGiraffeThreshold() {
        return isGiraffeThreshold;
    }

    public double getRecognitionThreshold() {
        return recognitionThreshold;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AnnotationDbElement) {
            if(annotation.getId() == ((AnnotationDbElement) obj).getAnnotation().getId() &&
                    individual.getId() == ((AnnotationDbElement) obj).getIndividual().getId() &&
                    isGiraffeThreshold == ((AnnotationDbElement) obj).isGiraffeThreshold &&
                    recognitionThreshold == ((AnnotationDbElement) obj).recognitionThreshold) {
                return true;
            }
        }
        return false;
    }
}
