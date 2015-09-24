package edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes;

import edu.uic.ibeis_java_api.api.IbeisAnnotation;

public class QueryRecord {

    private IbeisAnnotation queryAnnotation;
    private IbeisAnnotation dbAnnotation;
    private double score;
    private boolean sameGiraffe;
    private boolean giraffe;
    private boolean bestRecognizer = false;

    public IbeisAnnotation getQueryAnnotation() {
        return queryAnnotation;
    }

    public IbeisAnnotation getDbAnnotation() {
        return dbAnnotation;
    }

    public double getScore() {
        return score;
    }

    public boolean isSameGiraffe() {
        return sameGiraffe;
    }

    public boolean isGiraffe() {
        return giraffe;
    }

    public boolean isBestRecognizer() {
        return bestRecognizer;
    }

    public void setQueryAnnotation(IbeisAnnotation queryAnnotation) {
        this.queryAnnotation = queryAnnotation;
    }

    public void setDbAnnotation(IbeisAnnotation dbAnnotation) {
        this.dbAnnotation = dbAnnotation;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setSameGiraffe(boolean sameGiraffe) {
        this.sameGiraffe = sameGiraffe;
    }

    public void setGiraffe(boolean giraffe) {
        this.giraffe = giraffe;
    }

    public void setBestRecognizer(boolean bestRecognizer) {
        this.bestRecognizer = bestRecognizer;
    }

    @Override
    public String toString() {
        return "[query_aid: " + queryAnnotation.getId() + ", db_aid: " + dbAnnotation.getId() + ", score:" + score +
                ", sameGiraffe: " + sameGiraffe + ", giraffe: " + giraffe + ", best_rec: " + bestRecognizer + "]";
    }
}
