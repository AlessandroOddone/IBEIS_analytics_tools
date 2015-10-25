package edu.uic.ibeis_analytics.scripts.big_database;

public enum FilePath {
    ONE_VS_ONE_THRESHOLDS_CSV ("src/main/resources/big_db/one_vs_one_thresholds.csv"),
    ONE_VS_ONE_QUERY_RECORDS_JSON ("src/main/resources/big_db/one_vs_one_query_records.json"),
    RECOGNITION_COVER_SETS_JSON ("src/main/resources/big_db/recognition_cover_sets.json"),
    MIN_SET_COVER_LIST_JSON ("src/main/resources/big_db/min_set_cover.json"),
    MIN_SET_COVER_ONE_VS_ONE_THRESHOLDS_JSON ("src/main/resources/big_db/min_set_cover_one_vs_one.json"),
    MIN_SET_COVER_QUERY_RECORDS_JSON ("src/main/resources/big_db/min_set_cover_query_records.json"),
    MIN_SET_COVER_ONE_VS_ALL_THRESHOLDS_JSON ("src/main/resources/big_db/min_set_cover_one_vs_all.json"),
    VALIDATION_OUTPUT_JSON ("src/main/resources/big_db/validation_output.json");

    private String path;

    FilePath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }
}
