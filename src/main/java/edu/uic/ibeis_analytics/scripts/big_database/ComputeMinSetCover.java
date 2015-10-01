package edu.uic.ibeis_analytics.scripts.big_database;

import edu.uic.ibeis_analytics.ibeis_query.RecognitionCoverSetsCollection;

public class ComputeMinSetCover {

    private static final String RECOGNITION_COVER_SETS_JSON = "src/main/resources/computed_cover_sets_big_db.csv";

    private static RecognitionCoverSetsCollection coverSetsCollection;

    public static void main(String[] args) {
        getCoverSetsCollectionFromFile();
        computeMinSetCover();
    }

}
