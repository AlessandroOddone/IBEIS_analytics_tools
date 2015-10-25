package edu.uic.ibeis_analytics.scripts.big_database;

import edu.uic.ibeis_analytics.ibeis_query.AnnotationDbElement;
import edu.uic.ibeis_analytics.ibeis_query.AnnotationDbElementsList;
import edu.uic.ibeis_analytics.ibeis_query.AnnotationDbElementsMap;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryAgainstDb;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryRecord;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryRecordsCollection;
import edu.uic.ibeis_java_api.api.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ComputeThresholdsAgainstMinSetCover {
    private static final int BIG_DB_ENCOUNTER_ID = 4;

    private static final double STEP = 0.0001;
    private static final double MAX_THRESHOLD = 30;

    private static Ibeis ibeis = new Ibeis();
    private static List<IbeisAnnotation> queryAnnotations = new ArrayList<>();
    private static List<IbeisAnnotation> minSetCoverAnnotations = new ArrayList<>();
    private static QueryRecordsCollection queryRecordsCollection = null;
    private static AnnotationDbElementsList inputMinSetCoverAnnotationElementsDb;
    private static AnnotationDbElementsMap outputMinSetCoverAnnotationElementsMap = new AnnotationDbElementsMap();

    public static void main(String[] args) {
        init();
        getQueryRecords();
        for (AnnotationDbElement annotationDbElement : inputMinSetCoverAnnotationElementsDb.getElements()) {
            computeThresholdsVsMinSetCover(annotationDbElement);
        }
        saveResult();
    }

    private static void init() {
        loadAnnotationElementsDb();

        try {
            for (AnnotationDbElement e : inputMinSetCoverAnnotationElementsDb.getElements()) {
                minSetCoverAnnotations.add(e.getAnnotation());
            }

            for(IbeisImage ibeisImage : ibeis.getEncounterById(BIG_DB_ENCOUNTER_ID).getImages()) {
                for (IbeisAnnotation a : ibeisImage.getAnnotations()) {
                    if (!minSetCoverAnnotations.contains(a)) {
                        queryAnnotations.add(a);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getQueryRecords() {
        queryRecordsCollection = getRecordsCollectionFromFile();
        if (queryRecordsCollection == null) {
            queryRecordsCollection = new QueryAgainstDb(minSetCoverAnnotations, queryAnnotations, QueryAgainstDb.QueryType.ONE_VS_ALL).execute().getRecordsCollection();
            saveRecordsCollection(queryRecordsCollection);
        }
    }

    private static void computeThresholdsVsMinSetCover(AnnotationDbElement annotationDbElement) {
        double recognitionThreshold = Double.POSITIVE_INFINITY;
        double isGiraffeThreshold = Double.POSITIVE_INFINITY;

        for(int i=0; i<=MAX_THRESHOLD/STEP; i++) {

            double threshold = i*STEP;
            double recTP = 0;
            double recFP = 0;
            double isGiraffeTP = 0;
            double isGiraffeFP = 0;

            for(QueryRecord queryRecord : queryRecordsCollection.getQueryRecords()) {
                //System.out.println("QUERY RECORD: " + queryRecord);

                if (queryRecord.getDbAnnotation().getId() == annotationDbElement.getAnnotation().getId()) {
                    //recognition threshold
                    if (queryRecord.getScore() >= threshold) {//positive
                        if(queryRecord.isSameGiraffe()) {//true
                            recTP++;
                        }
                        else {//false
                            recFP++;
                        }
                    }

                    //is-a-giraffe threshold
                    if(queryRecord.getScore() >= threshold) {//recognised as giraffe
                        if(queryRecord.isGiraffe()) {//true positive
                            isGiraffeTP++;
                        }
                        else {//false positive
                            isGiraffeFP++;
                        }
                    }
                }
            }
            double recognitionPrec = (recTP/(recTP+recFP));
            double isGiraffePrec = (isGiraffeTP/(isGiraffeTP+isGiraffeFP));

            if(recognitionThreshold == Double.POSITIVE_INFINITY) {//recognition threshold not found yet
                if(recognitionPrec > 0.999) {
                    recognitionThreshold = threshold;
                }
            }
            if(isGiraffeThreshold == Double.POSITIVE_INFINITY) {//is-a-giraffe threshold not found yet
                if(isGiraffePrec > 0.999) {
                    isGiraffeThreshold = threshold;
                }
            }
        }
        outputMinSetCoverAnnotationElementsMap.add(new AnnotationDbElement(annotationDbElement.getAnnotation(),
                        annotationDbElement.getIndividual(), isGiraffeThreshold, recognitionThreshold));
    }

    private static void loadAnnotationElementsDb() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(FilePath.MIN_SET_COVER_ONE_VS_ONE_THRESHOLDS_JSON.toString()));
            inputMinSetCoverAnnotationElementsDb = AnnotationDbElementsList.fromJsonString(reader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveResult() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FilePath.MIN_SET_COVER_ONE_VS_ALL_THRESHOLDS_JSON.toString()));
            writer.write(outputMinSetCoverAnnotationElementsMap.toJsonString());
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

    private static void saveRecordsCollection(QueryRecordsCollection queryRecordsCollection) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FilePath.MIN_SET_COVER_QUERY_RECORDS_JSON.toString()));
            writer.write(queryRecordsCollection.toJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static QueryRecordsCollection getRecordsCollectionFromFile() {
        BufferedReader reader = null;
        QueryRecordsCollection queryRecordsCollection = null;
        try {
            reader = new BufferedReader(new FileReader(FilePath.MIN_SET_COVER_QUERY_RECORDS_JSON.toString()));
            queryRecordsCollection = QueryRecordsCollection.fromJsonString(reader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return queryRecordsCollection;
    }
}
