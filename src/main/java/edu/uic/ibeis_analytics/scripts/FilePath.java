package edu.uic.ibeis_analytics.scripts;

public enum FilePath {
    ONE_VS_ONE_QUERY_RECORDS_COLLECTION_WRAPPER_JSON ("src/main/resources/database/one_vs_one_query_records_collection_wrapper.json"),
    ONE_VS_ONE_THRESHOLDS_ANNOT_INFOS_JSON ("src/main/resources/database/one_vs_one_thresholds_annot_infos.json"),
    IDENTIFICATION_COVER_SETS_JSON ("src/main/resources/database/cover_sets.json"),
    MIN_SET_COVER_ANNOT_INFOS_JSON ("src/main/resources/database/min_set_cover.json"),
    ONE_VS_MANY_QUERY_RECORDS_COLLECTION_WRAPPER_JSON ("src/main/resources/database/one_vs_many_query_records_collection_wrapper.json"),
    ONE_VS_MANY_THRESHOLDS_ANNOT_INFOS_JSON ("src/main/resources/database/one_vs_many_thresholds_annot_infos.json"),
    VALIDATION_OUTPUT_JSON ("src/main/resources/database/validation_output.json");

    private String path;

    FilePath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }
}
