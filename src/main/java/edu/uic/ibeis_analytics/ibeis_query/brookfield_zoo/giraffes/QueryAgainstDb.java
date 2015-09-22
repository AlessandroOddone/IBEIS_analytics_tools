package edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes;

import edu.uic.ibeis_analytics.databases.brookfield_zoo.giraffes.BrookfieldZooGiraffesCollection;
import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;
import edu.uic.ibeis_java_api.api.IbeisQueryScore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryAgainstDb {

    private Ibeis ibeis = new Ibeis();

    private List<IbeisAnnotation> dbAnnotations;
    private List<IbeisAnnotation> queryAnnotations;

    public QueryAgainstDb(List<IbeisAnnotation> dbAnnotations, List<IbeisAnnotation> queryAnnotations) {
        this.dbAnnotations = dbAnnotations;
        this.queryAnnotations = queryAnnotations;
    }

    public List<QueryRecord> execute() {
        List<QueryRecord> queryRecords = new ArrayList<>();

        try {
            for(IbeisAnnotation queryAnnotation : queryAnnotations) {
                List<IbeisQueryScore> ibeisQueryScores = ibeis.query(queryAnnotation,dbAnnotations).getScores();
                for (IbeisQueryScore queryScore : ibeisQueryScores) {
                    QueryRecord queryRecord = new QueryRecord();
                    queryRecord.setQueryAnnotation(queryAnnotation);
                    queryRecord.setDbAnnotation(queryScore.getDbAnnotation());
                    queryRecord.setScore(queryScore.getScore());
                    queryRecord.setSameGiraffe(queryAnnotation.getIndividual().getId() ==
                            queryScore.getDbAnnotation().getIndividual().getId() ? true : false);
                    queryRecord.setGiraffe(queryAnnotation.getIndividual().getName().equals
                            (BrookfieldZooGiraffesCollection.BrookfieldZooGiraffesDbNames.ZEBRA.getValue()) ? false : true);
                    queryRecord.setBestRecognizer(Collections.max(ibeisQueryScores).getScore() == queryScore.getScore() ?
                            true : false);

                    queryRecords.add(queryRecord);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryRecords;
    }
}
