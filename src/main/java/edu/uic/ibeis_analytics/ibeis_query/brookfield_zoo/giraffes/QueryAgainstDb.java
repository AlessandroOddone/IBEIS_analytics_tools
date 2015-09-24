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
            System.out.println("NUM_QUERY_ANNOTATIONS: " + queryAnnotations.size());
            System.out.println("NUM_DB_ANNOTATIONS: " + dbAnnotations.size());
            for(IbeisAnnotation queryAnnotation : queryAnnotations) {
                List<IbeisQueryScore> queryScores = new ArrayList<>();
                List<QueryRecord> bestRecognizers = new ArrayList<>();

                for(IbeisAnnotation dbAnnotation : dbAnnotations) {
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

                    if(!bestRecognizers.isEmpty()) {
                        if(score == bestRecognizers.get(0).getScore()) {
                            bestRecognizers.add(queryRecord);
                        }
                        else if(score > bestRecognizers.get(0).getScore()) {
                            bestRecognizers.set(0, queryRecord);
                        }
                    }
                    else {
                        bestRecognizers.add(queryRecord);
                    }
                    recordsCollection.add(queryRecord);
                }
                for (QueryRecord queryRecord : bestRecognizers) {
                    queryRecord.setBestRecognizer(true);
                }
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION");
            e.printStackTrace();
        } finally {
            return recordsCollection;
        }
    }
}
