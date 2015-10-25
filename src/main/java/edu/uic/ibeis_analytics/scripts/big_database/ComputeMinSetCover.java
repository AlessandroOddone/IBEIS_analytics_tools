package edu.uic.ibeis_analytics.scripts.big_database;

import com.google.gson.Gson;
import edu.uic.ibeis_analytics.ibeis_query.AnnotationDbElement;
import edu.uic.ibeis_analytics.ibeis_query.AnnotationDbElementsList;
import edu.uic.ibeis_analytics.ibeis_query.cover_set.RecognitionCoverSet;
import edu.uic.ibeis_analytics.ibeis_query.cover_set.RecognitionCoverSetsCollection;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComputeMinSetCover {
    private static int TOT_ANNOTATIONS;
    private static RecognitionCoverSetsCollection coverSetsCollection;
    private static List<RecognitionCoverSet> remainingCoverSets;
    private static List<IbeisAnnotation> coveredAnnotations = new ArrayList<>();
    private static AnnotationDbElementsList orderedAnnotationsResult = new AnnotationDbElementsList();

    public static void main(String[] args) {
        getCoverSetsCollectionFromFile();
        TOT_ANNOTATIONS = coverSetsCollection.getCoverSets().size();
        computeMinSetCover();
        saveResult();
    }

    public static void getCoverSetsCollectionFromFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(FilePath.RECOGNITION_COVER_SETS_JSON.toString()));
            coverSetsCollection = RecognitionCoverSetsCollection.fromJsonString(reader.readLine());
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

    public static void computeMinSetCover() {
        remainingCoverSets = coverSetsCollection.getCoverSets();
        Collections.sort(remainingCoverSets, Collections.reverseOrder());

        while (coveredAnnotations.size() < TOT_ANNOTATIONS && remainingCoverSets.size() > 0 &&
                remainingCoverSets.get(0).getCoveredAnnotations().size() > 0) {
            RecognitionCoverSet bestCoverSet = remainingCoverSets.get(0);
            orderedAnnotationsResult.add(bestCoverSet.getAnnotationDbElement());
            coveredAnnotations.addAll(bestCoverSet.getCoveredAnnotations());
            remainingCoverSets.remove(bestCoverSet);

            for (RecognitionCoverSet rcs : remainingCoverSets) {
                rcs.getCoveredAnnotations().removeAll(coveredAnnotations);
                rcs.getCoveredAnnotations().remove(bestCoverSet.getAnnotationDbElement().getAnnotation());
            }
            Collections.sort(remainingCoverSets, Collections.reverseOrder());
        }
    }

    private static void saveResult() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FilePath.MIN_SET_COVER_ONE_VS_ONE_THRESHOLDS_JSON.toString()));
            writer.write(orderedAnnotationsResult.toJsonString());
            writer.close();

            List<IbeisAnnotation> minSetCoverAnnotations = new ArrayList<>();
            for (AnnotationDbElement e : orderedAnnotationsResult.getElements()) {
                minSetCoverAnnotations.add(e.getAnnotation());
            }
            writer = new BufferedWriter(new FileWriter(FilePath.MIN_SET_COVER_LIST_JSON.toString()));
            writer.write(new Gson().toJson(minSetCoverAnnotations));
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
}
