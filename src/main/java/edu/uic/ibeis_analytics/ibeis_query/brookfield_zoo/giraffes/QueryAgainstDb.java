package edu.uic.ibeis_analytics.ibeis_query.brookfield_zoo.giraffes;

import edu.uic.ibeis_analytics.databases.brookfield_zoo.giraffes.BrookfieldZooGiraffesCollection;
import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisAnnotation;
import edu.uic.ibeis_java_api.api.IbeisQueryResult;
import edu.uic.ibeis_java_api.api.IbeisQueryScore;

import java.util.List;

public class QueryAgainstDb {

    private Ibeis ibeis = new Ibeis();

    private List<IbeisAnnotation> dbAnnotations;
    private List<IbeisAnnotation> queryAnnotations;

    private int currentQueryIndex = 0;
    private int currentDbIndex = 0;
    private QueryRecordsCollection recordsCollection = new QueryRecordsCollection();
    private IbeisAnnotation queryAnnotation;
    private IbeisAnnotation dbAnnotation;
    private QueryType queryType;

    public enum QueryType {
        ONE_VS_ONE, ONE_VS_ALL
    }

    public QueryAgainstDb(List<IbeisAnnotation> dbAnnotations, List<IbeisAnnotation> queryAnnotations) {
        this.dbAnnotations = dbAnnotations;
        this.queryAnnotations = queryAnnotations;
        this.queryType = QueryType.ONE_VS_ALL;
    }

    public QueryAgainstDb(List<IbeisAnnotation> dbAnnotations, List<IbeisAnnotation> queryAnnotations, QueryType queryType) {
        this.dbAnnotations = dbAnnotations;
        this.queryAnnotations = queryAnnotations;
        this.queryType = queryType;
    }

    public QueryAgainstDb execute() {
        switch (queryType) {
            case ONE_VS_ALL:
                return executeOneVsAll();

            case ONE_VS_ONE:
                return executeOneVsOne();
        }
        return null;
    }

    public QueryAgainstDb executeOneVsOne() {
        try {
            queryAnnotation = queryAnnotations.get(currentQueryIndex);
            for(int j=currentDbIndex; j<dbAnnotations.size(); j++) {
                dbAnnotation = dbAnnotations.get(j);
                currentDbIndex = j;
                if(queryAnnotation.getId() != dbAnnotation.getId() && !dbAnnotation.getIndividual().getName().equals
                        (BrookfieldZooGiraffesCollection.BrookfieldZooGiraffesDbNames.ZEBRA.getValue()) &&
                        !dbAnnotation.getIndividual().getName().equals
                                (BrookfieldZooGiraffesCollection.BrookfieldZooGiraffesDbNames.OTHER.getValue())) {
                    IbeisQueryScore ibeisQueryScore = ibeis.query(queryAnnotation, dbAnnotation).getScores().get(0);
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
            currentQueryIndex++;

            for(int i=currentQueryIndex; i<queryAnnotations.size(); i++) {
                queryAnnotation = queryAnnotations.get(i);
                currentQueryIndex = i;
                for(int j=0; j<dbAnnotations.size(); j++) {
                    dbAnnotation = dbAnnotations.get(j);
                    currentDbIndex = j;
                    if(queryAnnotation.getId() != dbAnnotation.getId() && !dbAnnotation.getIndividual().getName().equals
                            (BrookfieldZooGiraffesCollection.BrookfieldZooGiraffesDbNames.ZEBRA.getValue()) &&
                            !dbAnnotation.getIndividual().getName().equals
                                    (BrookfieldZooGiraffesCollection.BrookfieldZooGiraffesDbNames.OTHER.getValue())) {
                        IbeisQueryScore ibeisQueryScore = ibeis.query(queryAnnotation, dbAnnotation).getScores().get(0);
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
            executeOneVsOne();
            e.printStackTrace();
        } finally {
            return this;
        }
    }

    public QueryAgainstDb executeOneVsAll() {
        try {
                List<IbeisQueryResult> ibeisQueryResultList = ibeis.query(queryAnnotations, dbAnnotations);

                for (IbeisQueryResult result : ibeisQueryResultList) {
                    IbeisAnnotation queryAnnotation = result.getQueryAnnotation();
                    List<IbeisQueryScore> ibeisQueryScoreList = result.getScores();

                    for (IbeisQueryScore score : ibeisQueryScoreList) {
                        QueryRecord queryRecord = new QueryRecord();
                        queryRecord.setQueryAnnotation(queryAnnotation);
                        queryRecord.setDbAnnotation(score.getDbAnnotation());
                        queryRecord.setScore(score.getScore());
                        queryRecord.setSameGiraffe(queryAnnotation.getIndividual().getId() ==
                                score.getDbAnnotation().getIndividual().getId() ? true : false);
                        queryRecord.setGiraffe(queryAnnotation.getIndividual().getName().equals
                                (BrookfieldZooGiraffesCollection.BrookfieldZooGiraffesDbNames.ZEBRA.getValue()) ? false : true);

                        recordsCollection.add(queryRecord);
                    }
                }

        } catch (Exception e) {
            executeOneVsAll();
            e.printStackTrace();
        } finally {
            return this;
        }
    }

    public QueryRecordsCollection getRecordsCollection() {
        return recordsCollection;
    }

}
