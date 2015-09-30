package edu.uic.ibeis_analytics.scripts.big_database;

import com.opencsv.CSVReader;
import edu.uic.ibeis_analytics.ibeis_query.AnnotationCoverSetsCollection;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryRecordsCollection;
import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;
import edu.uic.ibeis_java_api.api.IbeisImage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComputeAnnotsCoverSetsBigDatabase {

    private static final int BIG_DB_ENCOUNTER_ID = 4;

    private static Ibeis ibeis = new Ibeis();
    private static List<IbeisAnnotation> dbAnnotations = new ArrayList<>();
    private static List<IbeisAnnotation> queryAnnotations = new ArrayList<>();

    private static final String QUERY_RECORDS_JSON = "src/main/resources/query_records_big_db.json";
    private static QueryRecordsCollection queryRecordsCollection = null;

    private static final String THRESHOLDS_CSV = "src/main/resources/computed_thresholds_big_db.csv";
    private static final int IS_A_GIRAFFE_THRESHOLDS_INDEX = 0;
    private static final int RECOGNITION_THRESHOLDS_INDEX = 1;
    private static HashMap<Long,Double>[] thresholdsHashMaps = new HashMap[2];

    private static final String COVER_SETS_JSON = "src/main/resources/computed_cover_sets_big_db.csv";

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

    }

    private static void getRecordsCollectionFromFile() {
        BufferedReader reader = null;
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
    }

    private static void getThresholdsHashmapFromFile() {
        thresholdsHashMaps[IS_A_GIRAFFE_THRESHOLDS_INDEX] = new HashMap<>();
        thresholdsHashMaps[RECOGNITION_THRESHOLDS_INDEX] = new HashMap<>();

        try {
            CSVReader reader = new CSVReader(new FileReader(THRESHOLDS_CSV));
            String[] nextLine;

            // skip headers
            reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                thresholdsHashMaps[IS_A_GIRAFFE_THRESHOLDS_INDEX].put(Long.parseLong(nextLine[0].replaceAll("\\s", "")),
                        Double.parseDouble(nextLine[2].replaceAll("\\s", "")));
                thresholdsHashMaps[RECOGNITION_THRESHOLDS_INDEX].put(Long.parseLong(nextLine[0].replaceAll("\\s", "")),
                        Double.parseDouble(nextLine[3].replaceAll("\\s", "")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveCoverSets(AnnotationCoverSetsCollection coverSetsCollection) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(COVER_SETS_JSON));
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
