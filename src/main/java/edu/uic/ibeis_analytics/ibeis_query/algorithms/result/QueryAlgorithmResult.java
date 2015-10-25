package edu.uic.ibeis_analytics.ibeis_query.algorithms.result;

import edu.uic.ibeis_analytics.ibeis_query.AnnotationDbElement;
import edu.uic.ibeis_java_api.api.IbeisIndividual;

public class QueryAlgorithmResult {
    private IbeisIndividual individual;
    private Species species;
    private boolean thresholdExit;
    private AnnotationDbElement thresholdExitAnnotation;
    private long executionTime;

    public QueryAlgorithmResult(long executionTime) {
        this.individual = null;
        this.species = Species.UNKNOWN;
        this.executionTime = executionTime;
    }

    public QueryAlgorithmResult(Species species, long executionTime) {
        this.individual = null;
        this.species = species;
        this.executionTime = executionTime;
    }

    public QueryAlgorithmResult(IbeisIndividual individual, Species species, long executionTime) {
        this.individual = individual;
        this.species = species;
        this.executionTime = executionTime;
    }

    public IbeisIndividual getIndividual() {
        return individual;
    }


    public Species getSpecies() {
        return species;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public boolean isThresholdExit() {
        return thresholdExit;
    }

    public void setThresholdExit(boolean thresholdExit) {
        this.thresholdExit = thresholdExit;
    }

    public AnnotationDbElement getThresholdExitAnnotation() {
        return thresholdExitAnnotation;
    }

    public void setThresholdExitAnnotation(AnnotationDbElement thresholdExitAnnotation) {
        this.thresholdExitAnnotation = thresholdExitAnnotation;
    }
}
