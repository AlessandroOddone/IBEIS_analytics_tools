package edu.uic.ibeis_analytics.scripts.vs_test;

import com.opencsv.CSVWriter;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryAgainstDb;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryRecord;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryRecordsCollection;
import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;
import edu.uic.ibeis_java_api.api.IbeisImage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ComputePerAnnotationThresholdsVsTest {

    private static final int DB_ENCOUNTER_ID = 1;
    private static final int TEST_ENCOUNTER_ID = 2;

    private static Ibeis ibeis = new Ibeis();
    private static List<IbeisAnnotation> dbAnnotations = new ArrayList<>();
    private static List<IbeisAnnotation> queryAnnotations = new ArrayList<>();

    private static final String QUERY_RECORDS_JSON = "src/main/resources/query_records_vs_test.json";
    private static QueryRecordsCollection queryRecordsCollection = null;

    private static final String THRESHOLDS_OUT_FILE = "src/main/resources/computed_thresholds_vs_test.csv";
    private static CSVWriter outputWriter;

    private static final double STEP = 0.001;
    private static final double MAX_THRESHOLD = 30;

    public static void main(String[] args) {
        init();
        getQueryRecords();
        for (IbeisAnnotation ibeisAnnotation : dbAnnotations) {
            computeThresholds(ibeisAnnotation);
        }
        try {
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void init() {
        try {
            for(IbeisImage ibeisImage : ibeis.getEncounterById(DB_ENCOUNTER_ID).getImages()) {
                System.out.println("IMAGE ID " + ibeisImage.getId() + ": annotation " + ibeisImage.getAnnotations().get(0).getId());
                dbAnnotations.addAll(ibeisImage.getAnnotations());
            }
            for(IbeisImage ibeisImage : ibeis.getEncounterById(TEST_ENCOUNTER_ID).getImages()) {
                queryAnnotations.addAll(ibeisImage.getAnnotations());
            }
            outputWriter = new CSVWriter(new FileWriter(THRESHOLDS_OUT_FILE));
            String[] entries = "annotation id#individual#is-a-giraffe threshold#recognition threshold".split("#");
            outputWriter.writeNext(entries);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getQueryRecords() {
        queryRecordsCollection = getRecordsCollectionFromFile();
        if (queryRecordsCollection == null) {
            queryRecordsCollection = new QueryAgainstDb(dbAnnotations,queryAnnotations,QueryAgainstDb.QueryType.ONE_VS_ONE).execute().getRecordsCollection();
            saveRecordsCollection(queryRecordsCollection);
        }
    }

    private static void computeThresholds(IbeisAnnotation annotation) {
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

                if (queryRecord.getDbAnnotation().getId() == annotation.getId()) {
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
        String individualName = null;

        try {
            individualName = annotation.getIndividual().getName();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            String[] entries = {Long.toString(annotation.getId()),individualName,Double.toString(isGiraffeThreshold),Double.toString(recognitionThreshold)};
            outputWriter.writeNext(entries);
        }
    }

    private static void saveRecordsCollection(QueryRecordsCollection queryRecordsCollection) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(QUERY_RECORDS_JSON));
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
            reader = new BufferedReader(new FileReader(QUERY_RECORDS_JSON));
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
