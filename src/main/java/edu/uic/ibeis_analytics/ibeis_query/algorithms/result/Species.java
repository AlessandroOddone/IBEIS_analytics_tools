package edu.uic.ibeis_analytics.ibeis_query.algorithms.result;

public enum Species {
    GIRAFFE("Giraffe"), UNKNOWN("N/A");

    private String value;

    Species(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
