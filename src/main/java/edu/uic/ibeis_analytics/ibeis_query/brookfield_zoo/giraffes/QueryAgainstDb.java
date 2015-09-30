package edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes;

import edu.uic.ibeis_analytics.databases.brookfield_zoo.giraffes.BrookfieldZooGiraffesCollection;
import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;
import edu.uic.ibeis_java_api.api.IbeisQueryScore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryAgainstDb {

    private Ibeis ibeis = new Ibeis();

    private List<IbeisAnnotation> dbAnnotations;
    private List<IbeisAnnotation> queryAnnotations;

    public QueryAgainstDb(List<IbeisAnnotation> dbAnnotations, List<IbeisAnnotation> queryAnnotations) {
        this.dbAnnotations = dbAnnotations;
        this.queryAnnotations = queryAnnotations;
    }

    public QueryRecordsCollection execute() {
        QueryRecordsCollection recordsCollection = new QueryRecordsCollection();

        try {
            for(IbeisAnnotation queryAnnotation : queryAnnotations) {
                List<IbeisQueryScore> queryScores = new ArrayList<>();

                for(IbeisAnnotation dbAnnotation : dbAnnotations) {
                    if(queryAnnotation.getId() != dbAnnotation.getId()) {
                        IbeisQueryScore ibeisQueryScore = ibeis.query(queryAnnotation, Arrays.asList(dbAnnotation)).getScores().get(0);
                        queryScores.add(ibeisQueryScore);
                        double score = ibeisQueryScore.getScore();

                        QueryRecord queryRecord = new QueryRecord();
                        queryRecord.setQueryAnnotation(queryAnnotation);
                        queryRecord.setDbAnnotation(ibeisQueryScore.getDbAnnotation());
                        queryRecord.setScore(score);
                        queryRecord.setSameGiraffe(queryAnnotation.getIndividual().getId() ==
                                ibeisQueryScore.getDbAnnotation().getIndividual().getId() ? true : false);
                        queryRecord.setGiraffe(queryAnnotation.getIndividual().getName().equals
                                (BrookfieldZooGiraffesCollection.BrookfieldZooGiraffesDbNames.ZEBRA.getValue()) ? false : true);

                        recordsCollection.add(queryRecord);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return recordsCollection;
        }
    }
}
