package edu.uic.ibeis_analytics.scripts.big_database;

import edu.uic.ibeis_analytics.ibeis_query.algorithms.QueryAlgorithm;
import edu.uic.ibeis_analytics.ibeis_query.algorithms.QueryAlgorithmType;
import edu.uic.ibeis_analytics.ibeis_query.algorithms.result.QueryAlgorithmResult;
import edu.uic.ibeis_analytics.ibeis_query.algorithms.result.Species;
import edu.uic.ibeis_analytics.ibeis_query.validation.IndividualRecognitionOutcome;
import edu.uic.ibeis_analytics.ibeis_query.validation.SpeciesRecognitionOutcome;
import edu.uic.ibeis_analytics.ibeis_query.validation.ValidationQueryResult;
import edu.uic.ibeis_analytics.ibeis_query.validation.ValidationQueryResultsCollection;
import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;
import edu.uic.ibeis_java_api.api.IbeisImage;
import edu.uic.ibeis_java_api.api.IbeisIndividual;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ValidateQueryAlgorithm {
    private static int VALIDATION_ENCOUNTER_ID = 5;

    private static final QueryAlgorithmType QUERY_ALGORITHM_TYPE = QueryAlgorithmType.THRESHOLDS_ONE_VS_ALL;

    private static Ibeis ibeis = new Ibeis();
    private static List<IbeisAnnotation> queryAnnotations = new ArrayList<>();
    private static List<QueryAlgorithmResult> queryResults = new ArrayList<>();
    private static ValidationQueryResultsCollection validationResultsCollection = new ValidationQueryResultsCollection();

    public static void main(String[] args) {
        init();
        computeQueries();
        saveResults();
        displayResults();
    }

    private static void init() {
        try {
            for (IbeisImage image : ibeis.getEncounterById(VALIDATION_ENCOUNTER_ID).getImages()) {
                queryAnnotations.addAll(image.getAnnotations());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void computeQueries() {
        for (IbeisAnnotation queryAnnotation : queryAnnotations) {
            try {
                IbeisIndividual queryIndividual = queryAnnotation.getIndividual();
                QueryAlgorithmResult result = new QueryAlgorithm(queryAnnotation, QUERY_ALGORITHM_TYPE).execute();
                queryResults.add(result);

                IbeisIndividual resultIndividual = result.getIndividual();
                Species resultSpecies = result.getSpecies();

                SpeciesRecognitionOutcome speciesRecognitionOutcome;
                //species recognition
                if (queryIndividual.getName().equals("Zebra")) {//not a giraffe
                    if (resultSpecies == Species.UNKNOWN) {//detected not a giraffe
                        speciesRecognitionOutcome = SpeciesRecognitionOutcome.TRUE_NEGATIVE;
                    } else {//detected is a giraffe
                        speciesRecognitionOutcome = SpeciesRecognitionOutcome.FALSE_POSITIVE;
                    }
                } else {//is a giraffe
                    if (resultSpecies == Species.GIRAFFE) {//detected is a giraffe
                        speciesRecognitionOutcome = SpeciesRecognitionOutcome.TRUE_POSITIVE;
                    } else {//detected not a giraffe
                        speciesRecognitionOutcome = SpeciesRecognitionOutcome.FALSE_NEGATIVE;
                    }
                }

                IndividualRecognitionOutcome individualRecognitionOutcome;
                //individual recognition
                if (queryIndividual.getName().equals("Zebra") || queryIndividual.getName().equals("Other")) {//not an individual
                    if (resultIndividual == null) {//no individual detected
                        individualRecognitionOutcome = IndividualRecognitionOutcome.TRUE_NEGATIVE;
                    } else {//individual detected
                        individualRecognitionOutcome = IndividualRecognitionOutcome.FALSE_POSITIVE;
                    }
                } else {//an individual
                    if (resultIndividual == null) {//no individual detected
                        individualRecognitionOutcome = IndividualRecognitionOutcome.FALSE_NEGATIVE;
                    } else if (resultIndividual.getId() == queryIndividual.getId()) {//correct individual detected
                        individualRecognitionOutcome = IndividualRecognitionOutcome.TRUE_POSITIVE;
                    } else {//wrong individual detected
                        individualRecognitionOutcome = IndividualRecognitionOutcome.FALSE_POSITIVE;
                    }
                }
                validationResultsCollection.add(new ValidationQueryResult(queryAnnotation, speciesRecognitionOutcome,
                        individualRecognitionOutcome, result.getExecutionTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveResults() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FilePath.VALIDATION_OUTPUT_JSON.toString()));
            writer.write(validationResultsCollection.toJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void displayResults() {
        double speciesTP = 0;
        double speciesFP = 0;
        double speciesTN = 0;
        double speciesFN = 0;
        double individualTP = 0;
        double individualFP = 0;
        double individualTN = 0;
        double individualFN = 0;
        double sumOfTimes = 0;
        for (ValidationQueryResult result : validationResultsCollection.getResults()) {
            SpeciesRecognitionOutcome speciesRecognitionOutcome = result.getSpeciesRecognitionOutcome();
            switch (speciesRecognitionOutcome) {
                case TRUE_POSITIVE:
                    speciesTP++;
                    break;
                case FALSE_POSITIVE:
                    speciesFP++;
                    break;
                case TRUE_NEGATIVE:
                    speciesTN++;
                    break;
                case FALSE_NEGATIVE:
                    speciesFN++;
                    break;
            }
            IndividualRecognitionOutcome individualRecognitionOutcome = result.getIndividualRecognitionOutcome();
            switch (individualRecognitionOutcome) {
                case TRUE_POSITIVE:
                    individualTP++;
                    break;
                case FALSE_POSITIVE:
                    individualFP++;
                    break;
                case TRUE_NEGATIVE:
                    individualTN++;
                    break;
                case FALSE_NEGATIVE:
                    individualFN++;
                    break;
            }
            sumOfTimes += result.getComputationTime();
        }
        System.out.println("SPECIES ACCURACY: " + (speciesTP+speciesTN)/(speciesTP+speciesFP+speciesTN+speciesFN));
        System.out.println("SPECIES PRECISION: " + speciesTP/(speciesTP+speciesFP));
        System.out.println("SPECIES RECALL: " + speciesTP/(speciesTP+speciesFN));

        System.out.println("INDIVIDUAL ACCURACY: " + (individualTP+individualTN)/(individualTP+individualFP+individualTN+individualFN));
        System.out.println("INDIVIDUAL PRECISION: " + individualTP/(individualTP+individualFP));
        System.out.println("INDIVIDUAL RECALL: " + individualTP/(individualTP+individualFN));

        System.out.println("AVERAGE COMPUTATION TIME: " + new DecimalFormat("#.###").
                format((sumOfTimes/validationResultsCollection.getResults().size())/1000000000) + " s");
    }
}
