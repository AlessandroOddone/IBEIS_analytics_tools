package edu.uic.ibeis_analytics.scripts.identification;

import edu.uic.ibeis_analytics.scripts.FilePath;
import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;
import edu.uic.ibeis_java_api.api.IbeisImage;
import edu.uic.ibeis_java_api.identification_tools.IbeisDbAnnotationInfosWrapper;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.query_computation.QueryHandler;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.query_computation.QueryRecordsCollectionWrapper;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.query_computation.QueryType;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.thresholds_computation.IdentificationThresholdsComputationHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneVsManyThresholdsComputationScript {
    private static final int DATABASE_ENCOUNTER_ID = 1;
    private static final int MAX_THRESHOLD = 100;
    private static final double STEP = 0.01;
    private static final double TARGET_INDIVIDUAL_IDENTIFICATION_PRECISION = 0.95;
    private static final double TARGET_SPECIES_IDENTIFICATION_PRECISION = 0.99;

    private static Ibeis ibeis = new Ibeis();
    private static List<IbeisAnnotation> queryAnnotations = new ArrayList<>();
    private static List<IbeisAnnotation> databaseAnnotations = new ArrayList<>();

    private static IbeisDbAnnotationInfosWrapper minSetCoverIbeisDbAnnotationInfosWrapper;
    private static QueryHandler queryHandler;
    private static QueryRecordsCollectionWrapper queryRecordsCollectionWrapper;


    public static void main(String[] args) {
        init();
        computeQueryRecords();
        computeOneVsManyThresholds();
    }

    private static void init() {
        try {
            minSetCoverIbeisDbAnnotationInfosWrapper = readMinSetCoverIbeisDbAnnotationInfosWrapperFromFile();
            for (IbeisAnnotation annotation : minSetCoverIbeisDbAnnotationInfosWrapper.getIbeisDbAnnotationList()) {
                databaseAnnotations.add(annotation);
            }
            for(IbeisImage ibeisImage : ibeis.getEncounterById(DATABASE_ENCOUNTER_ID).getImages()) {
                if (ibeisImage.getId() >= 292) {
                    for (IbeisAnnotation annotation : ibeisImage.getAnnotations()) {
                        if (!databaseAnnotations.contains(annotation)) {
                            queryAnnotations.add(annotation);
                        }
                    }
                }
            }
            queryHandler = new QueryHandler(queryAnnotations, databaseAnnotations, minSetCoverIbeisDbAnnotationInfosWrapper.getTargetSpecies(),
                    QueryType.ONE_VS_ALL, null, Arrays.asList("NAG"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void computeQueryRecords() {
        try {
            queryHandler.execute(new File(FilePath.ONE_VS_MANY_QUERY_RECORDS_COLLECTION_WRAPPER_JSON.toString()));
            queryRecordsCollectionWrapper = queryHandler.getQueryRecordsCollectionWrapper();
        } catch (Exception e) {
            e.printStackTrace();
            computeQueryRecords();
        }
    }

    private static void computeOneVsManyThresholds() {
        try {
            new IdentificationThresholdsComputationHandler(queryRecordsCollectionWrapper).execute(MAX_THRESHOLD, STEP, TARGET_INDIVIDUAL_IDENTIFICATION_PRECISION, TARGET_SPECIES_IDENTIFICATION_PRECISION, new File(FilePath.ONE_VS_MANY_THRESHOLDS_ANNOT_INFOS_JSON.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            computeOneVsManyThresholds();
        }
    }

    private static IbeisDbAnnotationInfosWrapper readMinSetCoverIbeisDbAnnotationInfosWrapperFromFile() {
        BufferedReader reader = null;
        IbeisDbAnnotationInfosWrapper ibeisDbAnnotationInfosWrapper = null;
        try {
            reader = new BufferedReader(new FileReader(new File(FilePath.MIN_SET_COVER_ANNOT_INFOS_JSON.toString())));
            ibeisDbAnnotationInfosWrapper = IbeisDbAnnotationInfosWrapper.fromJson(reader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ibeisDbAnnotationInfosWrapper;
    }
}
