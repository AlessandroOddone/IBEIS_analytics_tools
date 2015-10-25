package edu.uic.ibeis_analytics.scripts.big_database;

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

public class ComputeThresholdsOneVsOne {
    private static final int BIG_DB_ENCOUNTER_ID = 4;

    private static final double STEP = 0.001;
    private static final double MAX_THRESHOLD = 100;

    private static Ibeis ibeis = new Ibeis();
    private static List<IbeisAnnotation> dbAnnotations = new ArrayList<>();
    private static QueryRecordsCollection queryRecordsCollection = null;
    private static CSVWriter outputWriter;

    public static void main(String[] args) {
        init();
        getQueryRecords();

        try {
            outputWriter = new CSVWriter(new FileWriter(FilePath.ONE_VS_ONE_THRESHOLDS_CSV.toString()));
            String[] entries = "annotation id#individual#is-a-giraffe threshold#recognition threshold".split("#");
            outputWriter.writeNext(entries);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            for(IbeisImage ibeisImage : ibeis.getEncounterById(BIG_DB_ENCOUNTER_ID).getImages()) {
                dbAnnotations.addAll(ibeisImage.getAnnotations());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getQueryRecords() {
        queryRecordsCollection = getRecordsCollectionFromFile();
        if (queryRecordsCollection == null) {
            queryRecordsCollection = new QueryAgainstDb(dbAnnotations,dbAnnotations, QueryAgainstDb.QueryType.ONE_VS_ONE).execute().getRecordsCollection();
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
            if(!individualName.equals("Zebra") && !individualName.equals("Other")) {
                String[] entries = {Long.toString(annotation.getId()),individualName,Double.toString(isGiraffeThreshold),Double.toString(recognitionThreshold)};
                outputWriter.writeNext(entries);
            }
        }
    }

    private static void saveRecordsCollection(QueryRecordsCollection queryRecordsCollection) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FilePath.ONE_VS_ONE_QUERY_RECORDS_JSON.toString()));
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
            reader = new BufferedReader(new FileReader(FilePath.ONE_VS_ONE_QUERY_RECORDS_JSON.toString()));
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
