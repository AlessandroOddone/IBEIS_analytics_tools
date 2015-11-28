package edu.uic.ibeis_analytics.scripts.database_reduction;

import edu.uic.ibeis_analytics.scripts.FilePath;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.dataset_reduction.IdentificationCoverSetsCollectionWrapper;
import edu.uic.ibeis_java_api.identification_tools.pre_processing.dataset_reduction.IdentificationMinSetCoverComputationHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MinSetCoverComputationScript {

    private static IdentificationMinSetCoverComputationHandler identificationMinSetCoverComputationHandler;

    public static void main(String[] args) {
        init();
        computeMinSetCover();
    }

    private static void init() {
        identificationMinSetCoverComputationHandler = new IdentificationMinSetCoverComputationHandler(readIdentificationCoverSetsCollectionWrapperFromFile());
    }

    private static void computeMinSetCover() {
        try {
            identificationMinSetCoverComputationHandler.execute(new File(FilePath.MIN_SET_COVER_ANNOT_INFOS_JSON.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            computeMinSetCover();
        }
    }

    private static IdentificationCoverSetsCollectionWrapper readIdentificationCoverSetsCollectionWrapperFromFile() {
        BufferedReader reader = null;
        IdentificationCoverSetsCollectionWrapper identificationCoverSetsCollectionWrapper = null;
        try {
            reader = new BufferedReader(new FileReader(new File(FilePath.IDENTIFICATION_COVER_SETS_JSON.toString())));
            identificationCoverSetsCollectionWrapper = IdentificationCoverSetsCollectionWrapper.fromJson(reader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return identificationCoverSetsCollectionWrapper;
    }
}
