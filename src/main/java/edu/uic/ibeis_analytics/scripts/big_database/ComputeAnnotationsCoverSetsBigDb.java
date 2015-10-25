package edu.uic.ibeis_analytics.scripts.big_database;

import com.opencsv.CSVReader;
import edu.uic.ibeis_analytics.ibeis_query.AnnotationDbElement;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryRecord;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryRecordsCollection;
import edu.uic.ibeis_analytics.ibeis_query.cover_set.RecognitionCoverSet;
import edu.uic.ibeis_analytics.ibeis_query.cover_set.RecognitionCoverSetsCollection;
import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;
import edu.uic.ibeis_java_api.api.IbeisImage;
import edu.uic.ibeis_java_api.api.IbeisIndividual;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComputeAnnotationsCoverSetsBigDb {

    private static final int BIG_DB_ENCOUNTER_ID = 4;

    private static Ibeis ibeis = new Ibeis();
    private static List<IbeisAnnotation> dbAnnotations = new ArrayList<>();
    private static QueryRecordsCollection queryRecordsCollection = null;
    private static HashMap<Long,Double> isGiraffeThresholdsHashMaps = new HashMap();
    private static HashMap<Long,Double> recognitionThresholdsHashMaps = new HashMap();

    public static void main(String[] args) {
        init();
        getRecordsCollectionFromFile();
        getThresholdsHashmapFromFile();
        computeCoverSets();
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

    private static void computeCoverSets() {
        RecognitionCoverSetsCollection coverSetsCollection = new RecognitionCoverSetsCollection();

        IbeisIndividual individual = null;
        for(IbeisAnnotation annotation : dbAnnotations) {
            try {
                individual = annotation.getIndividual();
                String individualName = individual.getName();
                if (!individualName.equals("Zebra") && !individualName.equals("Other")) {
                    double isGiraffeThreshold = isGiraffeThresholdsHashMaps.get(annotation.getId());
                    double recognitionThreshold = recognitionThresholdsHashMaps.get(annotation.getId());
                    RecognitionCoverSet coverSet = new RecognitionCoverSet(new AnnotationDbElement(annotation,
                            individual, isGiraffeThreshold, recognitionThreshold));

                    for(QueryRecord queryRecord : queryRecordsCollection.getQueryRecords()) {
                        if(queryRecord.getDbAnnotation().getId() == annotation.getId()) {
                            if(queryRecord.getScore() >= recognitionThreshold &&
                                    queryRecord.isSameGiraffe()) {
                                coverSet.add(queryRecord.getQueryAnnotation());
                            }
                        }
                    }
                    coverSetsCollection.add(coverSet);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        saveCoverSets(coverSetsCollection);
    }

    private static void getRecordsCollectionFromFile() {
        BufferedReader reader = null;
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
    }

    private static void getThresholdsHashmapFromFile() {
        recognitionThresholdsHashMaps = new HashMap<>();

        try {
            CSVReader reader = new CSVReader(new FileReader(FilePath.ONE_VS_ONE_THRESHOLDS_CSV.toString()));
            String[] nextLine;

            // skip headers
            reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                isGiraffeThresholdsHashMaps.put(Long.parseLong(nextLine[0].replaceAll("\\s", "")),
                        Double.parseDouble(nextLine[2].replaceAll("\\s", "")));
                recognitionThresholdsHashMaps.put(Long.parseLong(nextLine[0].replaceAll("\\s", "")),
                        Double.parseDouble(nextLine[3].replaceAll("\\s", "")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveCoverSets(RecognitionCoverSetsCollection coverSetsCollection) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FilePath.RECOGNITION_COVER_SETS_JSON.toString()));
            writer.write(coverSetsCollection.toJsonString());
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
}
