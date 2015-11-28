package edu.uic.ibeis_analytics.scripts.database_reduction;

import edu.uic.ibeis_analytics.scripts.FilePath;
import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;
import edu.uic.ibeis_java_api.api.IbeisImage;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.query_computation.QueryHandler;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.query_computation.QueryRecordsCollectionWrapper;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.query_computation.QueryType;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.thresholds_computation.IdentificationThresholdsComputationHandler;
import edu.uic.ibeis_java_api.values.Species;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneVsOneThresholdsComputationScript {
    private static final int DATABASE_ENCOUNTER_ID = 1;

    private static final int MAX_THRESHOLD = 100;
    private static final double STEP = 0.01;
    private static final double TARGET_INDIVIDUAL_IDENTIFICATION_PRECISION = 0.9;
    private static final double TARGET_SPECIES_IDENTIFICATION_PRECISION = 0.99;

    private static Ibeis ibeis = new Ibeis();
    private static List<IbeisAnnotation> queryAnnotations = new ArrayList<>();
    private static List<IbeisAnnotation> databaseAnnotations = new ArrayList<>();
    private static QueryHandler queryHandler;
    private static QueryRecordsCollectionWrapper queryRecordsCollectionWrapper;

    public static void main(String[] args) {
        init();
        computeQueryRecords();
        computeOneVsOneThresholds();
    }

    private static void init() {
        try {
            for(IbeisImage ibeisImage : ibeis.getEncounterById(DATABASE_ENCOUNTER_ID).getImages()) {
                databaseAnnotations.addAll(ibeisImage.getAnnotations());
                if (ibeisImage.getId() >= 292) {
                    queryAnnotations.addAll(ibeisImage.getAnnotations());
                }
            }
            queryHandler = new QueryHandler(queryAnnotations, databaseAnnotations, Species.GIRAFFE, QueryType.ONE_VS_ONE, null, Arrays.asList("NAG"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void computeQueryRecords() {
        try {
            queryHandler.execute(new File(FilePath.ONE_VS_ONE_QUERY_RECORDS_COLLECTION_WRAPPER_JSON.toString()));
            queryRecordsCollectionWrapper = queryHandler.getQueryRecordsCollectionWrapper();
        } catch (Exception e) {
            e.printStackTrace();
            computeQueryRecords();
        }
    }

    private static void computeOneVsOneThresholds() {
        try {
             new IdentificationThresholdsComputationHandler(queryRecordsCollectionWrapper).execute(MAX_THRESHOLD, STEP, TARGET_INDIVIDUAL_IDENTIFICATION_PRECISION, TARGET_SPECIES_IDENTIFICATION_PRECISION, new File(FilePath.ONE_VS_ONE_THRESHOLDS_ANNOT_INFOS_JSON.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            computeOneVsOneThresholds();
        }
    }
}
