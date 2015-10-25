package edu.uic.ibeis_analytics.scripts.vs_test;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.QuickChart;
import com.xeiam.xchart.SwingWrapper;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryAgainstDb;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryRecord;
import edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes.QueryRecordsCollection;
import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;
import edu.uic.ibeis_java_api.api.IbeisImage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OverallThresholdsVsTestPlotsScript {

    private static final int DB_ENCOUNTER_ID = 1;
    private static final int TEST_ENCOUNTER_ID = 2;

    private static Ibeis ibeis = new Ibeis();
    private static List<IbeisAnnotation> dbAnnotations = new ArrayList<>();
    private static List<IbeisAnnotation> queryAnnotations = new ArrayList<>();

    private static final String QUERY_RECORDS_JSON = "src/main/resources/query_records_vs_test.json";
    private static QueryRecordsCollection queryRecordsCollection = null;

    private static final double STEP = 0.001;
    private static final double MAX_THRESHOLD = 30;
    private static final int ARRAY_LENGTH = Double.valueOf(MAX_THRESHOLD/STEP).intValue() + 1;

    private static double[] xThresholdValue = new double[ARRAY_LENGTH];

    private static double[] yRecThresholdAccuracy = new double[ARRAY_LENGTH];
    private static double[] yRecThresholdPrecision = new double[ARRAY_LENGTH];
    private static double[] yRecThresholdRecall = new double[ARRAY_LENGTH];

    private static double[] yNotAGiraffeThresholdAccuracy = new double[ARRAY_LENGTH];
    private static double[] yNotAGiraffeThresholdPrecision = new double[ARRAY_LENGTH];
    private static double[] yNotAGiraffeThresholdRecall = new double[ARRAY_LENGTH];

    private static double[] yIsAGiraffeThresholdAccuracy = new double[ARRAY_LENGTH];
    private static double[] yIsAGiraffeThresholdPrecision = new double[ARRAY_LENGTH];
    private static double[] yIsAGiraffeThresholdRecall = new double[ARRAY_LENGTH];

    private static double[] yRecTP = new double[ARRAY_LENGTH];
    private static double[] yRecFN = new double[ARRAY_LENGTH];
    private static double[] yIsAGiraffeTP = new double[ARRAY_LENGTH];
    private static double[] yIsAGiraffeFN = new double[ARRAY_LENGTH];


    public static void main(String[] args) {
        init();
        getQueryRecords();
        plotCharts();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getQueryRecords() {
        queryRecordsCollection = getRecordsCollectionFromFile();
        if (queryRecordsCollection == null) {
            queryRecordsCollection = new QueryAgainstDb(dbAnnotations,queryAnnotations).execute().getRecordsCollection();
            saveRecordsCollection(queryRecordsCollection);
        }
    }

    private static void plotCharts() {

        for(int i=0; i<=MAX_THRESHOLD/STEP; i++) {

            double threshold = i*STEP;
            xThresholdValue[i] = threshold;

            double recTP = 0;
            double recFP = 0;
            double recTN = 0;
            double recFN = 0;

            double noGiraffeTP = 0;
            double noGiraffeFP = 0;
            double noGiraffeTN = 0;
            double noGiraffeFN = 0;

            double isGiraffeTP = 0;
            double isGiraffeFP = 0;
            double isGiraffeTN = 0;
            double isGiraffeFN = 0;

            for(QueryRecord queryRecord : queryRecordsCollection.getQueryRecords()) {
                //System.out.println("QUERY RECORD: " + queryRecord);
                //recognition threshold

                if (queryRecord.getScore() >= threshold) {//positive
                    if(queryRecord.isSameGiraffe()) {//true
                        recTP++;
                    }
                    else {//false
                        recFP++;
                    }
                }
                else {//negative
                    if(!queryRecord.isSameGiraffe()) {//true
                        recTN++;
                    }
                    else {//false
                        recFN++;
                    }
                }

                //non-giraffe threshold
                if(queryRecord.getScore() <= threshold) {//recognised as not-a-giraffe
                    if(!queryRecord.isGiraffe()) {//true positive
                        noGiraffeTP++;
                    }
                    else {//false positive
                        noGiraffeFP++;
                    }
                }
                else {//not recognised as not-a-giraffe
                    if(queryRecord.isGiraffe()) {//true negative
                        noGiraffeTN++;
                    }
                    else {//false negative
                        noGiraffeFN++;
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
                else {//not recognised as giraffe
                    if(!queryRecord.isGiraffe()) {//true negative
                        isGiraffeTN++;
                    }
                    else {//false negative
                        isGiraffeFN++;
                    }
                }
            }
            System.out.println("RECOGNITION THRESHOLD:");
            System.out.println("tp = " + recTP);
            System.out.println("fp = " + recFP);
            System.out.println("tn = " + recTN);
            System.out.println("fn = " + recFN + "\n");

            System.out.println("NOT-A-GIRAFFE THRESHOLD:");
            System.out.println("tp = " + noGiraffeTP);
            System.out.println("fp = " + noGiraffeFP);
            System.out.println("tn = " + noGiraffeTN);
            System.out.println("fn = " + noGiraffeFN + "\n");

            yRecThresholdAccuracy[i] = ((recTP+recTN)/(recTP+recTN+recFP+recFN));
            yRecThresholdPrecision[i] = (recTP/(recTP+recFP));
            yRecThresholdRecall[i] = (recTP/(recTP+recFN));

            yNotAGiraffeThresholdAccuracy[i] = ((noGiraffeTP+noGiraffeTN)/(noGiraffeTP+noGiraffeTN+noGiraffeFP+noGiraffeFN));
            yNotAGiraffeThresholdPrecision[i] = (noGiraffeTP/(noGiraffeTP+noGiraffeFP));
            yNotAGiraffeThresholdRecall[i] = (noGiraffeTP/(noGiraffeTP+noGiraffeFN));

            yIsAGiraffeThresholdAccuracy[i] = ((isGiraffeTP+isGiraffeTN)/(isGiraffeTP+isGiraffeTN+isGiraffeFP+isGiraffeFN));
            yIsAGiraffeThresholdPrecision[i] = (isGiraffeTP/(isGiraffeTP+isGiraffeFP));
            yIsAGiraffeThresholdRecall[i] = (isGiraffeTP/(isGiraffeTP+isGiraffeFN));

            yRecTP[i] = recTP;
            yRecFN[i] = recFN;
            yIsAGiraffeTP[i] = isGiraffeTP;
            yIsAGiraffeFN[i] = isGiraffeFN;
        }

        //recognition threshold accuracy chart
        Chart recAccChart = QuickChart.getChart("Recognition Threshold Accuracy", "threshold", "accuracy", "accuracy(threshold)",
                xThresholdValue, yRecThresholdAccuracy);
        new SwingWrapper(recAccChart).displayChart();
        //recognition threshold precision chart
        Chart recPrecChart = QuickChart.getChart("Recognition Threshold Precision", "threshold", "precision", "precision(threshold)",
                xThresholdValue, yRecThresholdPrecision);
        new SwingWrapper(recPrecChart).displayChart();
        //recognition threshold recall chart
        Chart recRecChart = QuickChart.getChart("Recognition Threshold Recall", "threshold", "recall", "recall(threshold)",
                xThresholdValue, yRecThresholdRecall);
        new SwingWrapper(recRecChart).displayChart();

        //non-giraffe threshold chart
        Chart noGiraffeAccChart = QuickChart.getChart("Not-a-giraffe Threshold Accuracy", "threshold", "accuracy", "accuracy(threshold)",
                xThresholdValue, yNotAGiraffeThresholdAccuracy);
        new SwingWrapper(noGiraffeAccChart).displayChart();
        //non-giraffe threshold chart
        Chart noGiraffePrecChart = QuickChart.getChart("Not-a-giraffe Threshold Precision", "threshold", "precision", "precision(threshold)",
                xThresholdValue, yNotAGiraffeThresholdPrecision);
        new SwingWrapper(noGiraffePrecChart).displayChart();
        //non-giraffe threshold chart
        Chart noGiraffeRecChart = QuickChart.getChart("Not-a-giraffe Threshold Recall", "threshold", "recall", "recall(threshold)",
                xThresholdValue, yNotAGiraffeThresholdRecall);
        new SwingWrapper(noGiraffeRecChart).displayChart();

        //is-giraffe threshold accuracy chart
        Chart isGiraffeAccChart = QuickChart.getChart("Is-a-giraffe Threshold Accuracy", "threshold", "accuracy", "accuracy(threshold)",
                xThresholdValue, yIsAGiraffeThresholdAccuracy);
        new SwingWrapper(isGiraffeAccChart).displayChart();
        //is-giraffe threshold precision chart
        Chart isGiraffePrecChart = QuickChart.getChart("Is-a-giraffe Threshold Precision", "threshold", "precision", "precision(threshold)",
                xThresholdValue, yIsAGiraffeThresholdPrecision);
        new SwingWrapper(isGiraffePrecChart).displayChart();
        //is-giraffe threshold recall chart
        Chart isGiraffeRecChart = QuickChart.getChart("Is-a-giraffe Threshold Recall", "threshold", "recall", "recall(threshold)",
                xThresholdValue, yIsAGiraffeThresholdRecall);
        new SwingWrapper(isGiraffeRecChart).displayChart();

        //recognition true positives
        Chart recTpChart = QuickChart.getChart("Recognition TP", "threshold", "#TP", "#TP(threshold)",
                xThresholdValue, yRecTP);
        new SwingWrapper(recTpChart).displayChart();
        //recognition false negatives
        Chart recFnChart = QuickChart.getChart("Recognition FN", "threshold", "#FN", "#FN(threshold)",
                xThresholdValue, yRecFN);
        new SwingWrapper(recFnChart).displayChart();
        //is-a-giraffe true positives
        Chart isGiraffeTpChart = QuickChart.getChart("Is-a-giraffe TP", "threshold", "#TP", "#TP(threshold)",
                xThresholdValue, yIsAGiraffeTP);
        new SwingWrapper(isGiraffeTpChart).displayChart();
        //is-a-giraffe false negatives
        Chart isGiraffeFnChart = QuickChart.getChart("Is-a-giraffe FN", "threshold", "#FN", "#FN(threshold)",
                xThresholdValue, yIsAGiraffeFN);
        new SwingWrapper(isGiraffeFnChart).displayChart();
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
