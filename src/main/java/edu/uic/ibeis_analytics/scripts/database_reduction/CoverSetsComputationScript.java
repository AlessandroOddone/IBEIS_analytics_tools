package edu.uic.ibeis_analytics.scripts.database_reduction;

import edu.uic.ibeis_analytics.scripts.FilePath;
import edu.uic.ibeis_java_api.exceptions.InvalidThresholdTypeException;
import edu.uic.ibeis_java_api.identification_tools.IbeisDbAnnotationInfosWrapper;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.dataset_reduction.IdentificationCoverSetsComputationHandler;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.query_computation.QueryRecordsCollectionWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CoverSetsComputationScript {

    private static QueryRecordsCollectionWrapper queryRecordsCollectionWrapper;
    private static IbeisDbAnnotationInfosWrapper oneVsOneThresholdsIbeisDbAnnotationInfosWrapper;

    private static IdentificationCoverSetsComputationHandler identificationCoverSetsComputationHandler;

    public static void main(String[] args) {
        init();
        computeCoverSets();
    }

    private static void init() {
        queryRecordsCollectionWrapper = readRecordsCollectionWrapperFromFile();
        oneVsOneThresholdsIbeisDbAnnotationInfosWrapper = readOneVsOneThresholdsIbeisDbAnnotationInfosWrapperFromFile();

        try {
            identificationCoverSetsComputationHandler = new IdentificationCoverSetsComputationHandler(queryRecordsCollectionWrapper,oneVsOneThresholdsIbeisDbAnnotationInfosWrapper);
        } catch (InvalidThresholdTypeException e) {
            e.printStackTrace();
        }
    }

    private static void computeCoverSets() {
        try {
            identificationCoverSetsComputationHandler.execute(new File(FilePath.IDENTIFICATION_COVER_SETS_JSON.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            computeCoverSets();
        }
    }

    private static QueryRecordsCollectionWrapper readRecordsCollectionWrapperFromFile() {
        BufferedReader reader = null;
        QueryRecordsCollectionWrapper queryRecordsCollectionWrapper = null;
        try {
            reader = new BufferedReader(new FileReader(new File(FilePath.ONE_VS_ONE_QUERY_RECORDS_COLLECTION_WRAPPER_JSON.toString())));
            queryRecordsCollectionWrapper = QueryRecordsCollectionWrapper.fromJson(reader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return queryRecordsCollectionWrapper;
    }

    private static IbeisDbAnnotationInfosWrapper readOneVsOneThresholdsIbeisDbAnnotationInfosWrapperFromFile() {
        BufferedReader reader = null;
        IbeisDbAnnotationInfosWrapper oneVsOneThresholdsIbeisDbAnnotationInfosWrapper = null;
        try {
            reader = new BufferedReader(new FileReader(new File(FilePath.ONE_VS_ONE_THRESHOLDS_ANNOT_INFOS_JSON.toString())));
            oneVsOneThresholdsIbeisDbAnnotationInfosWrapper = IbeisDbAnnotationInfosWrapper.fromJson(reader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return oneVsOneThresholdsIbeisDbAnnotationInfosWrapper;
    }
}
