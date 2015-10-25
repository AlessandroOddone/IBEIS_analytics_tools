package edu.uic.ibeis_analytics.ibeis_query.validation;

import edu.uic.ibeis_java_api.api.IbeisAnnotation;

public class ValidationQueryResult {

    private IbeisAnnotation queryAnnotation;
    private SpeciesRecognitionOutcome speciesRecognitionOutcome;
    private IndividualRecognitionOutcome individualRecognitionOutcome;
    private long computationTime;

    public ValidationQueryResult(IbeisAnnotation queryAnnotation, SpeciesRecognitionOutcome speciesRecognitionOutcome,
                                 IndividualRecognitionOutcome individualRecognitionOutcome, long computationTime) {
        this.queryAnnotation = queryAnnotation;
        this.speciesRecognitionOutcome = speciesRecognitionOutcome;
        this.individualRecognitionOutcome = individualRecognitionOutcome;
        this.computationTime = computationTime;
    }

    public IbeisAnnotation getQueryAnnotation() {
        return queryAnnotation;
    }

    public SpeciesRecognitionOutcome getSpeciesRecognitionOutcome() {
        return speciesRecognitionOutcome;
    }

    public IndividualRecognitionOutcome getIndividualRecognitionOutcome() {
        return individualRecognitionOutcome;
    }

    public long getComputationTime() {
        return computationTime;
    }
}
