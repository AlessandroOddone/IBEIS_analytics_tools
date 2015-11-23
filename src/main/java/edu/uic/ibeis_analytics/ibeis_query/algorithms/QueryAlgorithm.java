package edu.uic.ibeis_analytics.ibeis_query.algorithms;

import edu.uic.ibeis_analytics.ibeis_query.AnnotationDbElement;
import edu.uic.ibeis_analytics.ibeis_query.AnnotationDbElementsList;
import edu.uic.ibeis_analytics.ibeis_query.AnnotationDbElementsMap;
import edu.uic.ibeis_analytics.ibeis_query.algorithms.result.QueryAlgorithmResult;
import edu.uic.ibeis_analytics.ibeis_query.algorithms.result.Species;
import edu.uic.ibeis_analytics.scripts.big_database.FilePath;
import edu.uic.ibeis_java_api.api.*;
import edu.uic.ibeis_java_api.exceptions.MalformedHttpRequestException;
import edu.uic.ibeis_java_api.exceptions.InvalidEncounterIdException;
import edu.uic.ibeis_java_api.exceptions.UnsuccessfulHttpRequestException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class QueryAlgorithm {
    private static final QueryAlgorithmType DEFAULT_ALGORITHM = QueryAlgorithmType.BEST_SCORE;
    private static List<AnnotationDbElement> orderedAnnotationElementsDb;
    private static HashMap<Long,AnnotationDbElement> unorderedAnnotationElementsDb;

    private Ibeis ibeis = new Ibeis();

    private IbeisAnnotation queryAnnotation;
    private QueryAlgorithmType selectedAlgorithm;

    public QueryAlgorithm(IbeisAnnotation queryAnnotation) {
        this(queryAnnotation, DEFAULT_ALGORITHM);
    }

    public QueryAlgorithm(IbeisAnnotation queryAnnotation, QueryAlgorithmType selectedAlgorithm) {
        this.queryAnnotation = queryAnnotation;
        this.selectedAlgorithm = selectedAlgorithm;
        loadAnnotationElementsDb();
    }

    public QueryAlgorithmResult execute() throws IOException, MalformedHttpRequestException, UnsuccessfulHttpRequestException, InvalidEncounterIdException {
        switch (selectedAlgorithm) {
            case BEST_SCORE:
                return executeBestScore();
            case THRESHOLDS_ONE_VS_ONE:
                return executeThresholdsOneVsOne();
            case THRESHOLDS_ONE_VS_ALL:
                return executeThresholdsOneVsAll();
        }
        return null;
    }

    private QueryAlgorithmResult executeBestScore() throws IOException, MalformedHttpRequestException, UnsuccessfulHttpRequestException, InvalidEncounterIdException {
        long startTime = System.nanoTime();

        IbeisQueryResult queryResult = ibeis.queryNoCache(queryAnnotation, getDbAnnotations());
        List<IbeisQueryScore> queryScores = queryResult.getScores();
        //System.out.println("QUERY RESULT: " + queryResult);

        //sort query scores from the highest to the lowest
        Collections.sort(queryScores, Collections.reverseOrder());
        //get the highest score
        IbeisQueryScore highestScore = queryScores.get(0);
        //System.out.println("HIGHEST SCORE: " + highestScore);

        return new QueryAlgorithmResult(highestScore.getDbAnnotation().getIndividual(), Species.GIRAFFE, System.nanoTime()-startTime);
    }

    private QueryAlgorithmResult executeThresholdsOneVsOne() throws IOException, MalformedHttpRequestException, UnsuccessfulHttpRequestException, InvalidEncounterIdException {
        long startTime = System.nanoTime();

        boolean isGiraffe = false;

        for (AnnotationDbElement e : orderedAnnotationElementsDb) {
            IbeisAnnotation dbAnnotation = e.getAnnotation();
            double isGiraffeThreshold = e.getIsGiraffeThreshold();
            double recognitionThreshold = e.getRecognitionThreshold();

            IbeisQueryResult queryResult = ibeis.queryNoCache(queryAnnotation, dbAnnotation);
            double score = queryResult.getScores().get(0).getScore();

            if (score >= recognitionThreshold) {
                QueryAlgorithmResult queryAlgorithmResult = new QueryAlgorithmResult(dbAnnotation.getIndividual(), Species.GIRAFFE, System.nanoTime()-startTime);
                queryAlgorithmResult.setThresholdExit(true);
                queryAlgorithmResult.setThresholdExitAnnotation(e);
                return queryAlgorithmResult;
            }
            if (!isGiraffe) {
                if (score >= isGiraffeThreshold) {
                    isGiraffe = true;
                }
            }
        }
        if (isGiraffe) {
            return new QueryAlgorithmResult(Species.GIRAFFE, System.nanoTime()-startTime);
        }
        return new QueryAlgorithmResult(System.nanoTime()-startTime);
    }

    private QueryAlgorithmResult executeThresholdsOneVsAll() throws IOException, MalformedHttpRequestException, UnsuccessfulHttpRequestException, InvalidEncounterIdException {
        long startTime = System.nanoTime();

        IbeisQueryResult queryResult = ibeis.queryNoCache(queryAnnotation, getDbAnnotations());
        List<IbeisQueryScore> queryScores = queryResult.getScores();
        //System.out.println("QUERY RESULT: " + queryResult);

        //sort query scores from the highest to the lowest
        Collections.sort(queryScores, Collections.reverseOrder());
        //get the highest score

        boolean isGiraffe = false;
        for (IbeisQueryScore queryScore : queryScores) {
            double score = queryScore.getScore();
            if (!(score > 0)) break;
            long annotationId = queryScore.getDbAnnotation().getId();
            AnnotationDbElement annotationDbElement = unorderedAnnotationElementsDb.get(annotationId);
            if (score >= annotationDbElement.getRecognitionThreshold()) {
                QueryAlgorithmResult queryAlgorithmResult = new QueryAlgorithmResult(queryScore.getDbAnnotation().getIndividual(), Species.GIRAFFE, System.nanoTime()-startTime);
                queryAlgorithmResult.setThresholdExit(true);
                queryAlgorithmResult.setThresholdExitAnnotation(annotationDbElement);
                return queryAlgorithmResult;
            }
            if (!isGiraffe) {
                if (score >= annotationDbElement.getIsGiraffeThreshold()) {
                    isGiraffe = true;
                }
            }
        }
        if (isGiraffe) {
            return new QueryAlgorithmResult(Species.GIRAFFE, System.nanoTime()-startTime);
        }
        return new QueryAlgorithmResult(System.nanoTime()-startTime);
    }

    private List<IbeisAnnotation> getDbAnnotations() throws IOException, MalformedHttpRequestException, UnsuccessfulHttpRequestException, InvalidEncounterIdException {
        List<IbeisAnnotation> dbAnnotations = new ArrayList<>();
        for(AnnotationDbElement e : orderedAnnotationElementsDb) {
            dbAnnotations.add(e.getAnnotation());
        }
        return dbAnnotations;
    }

    private void loadAnnotationElementsDb() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(FilePath.MIN_SET_COVER_ONE_VS_ONE_THRESHOLDS_JSON.toString()));
            orderedAnnotationElementsDb = AnnotationDbElementsList.fromJsonString(reader.readLine()).getElements();
            reader.close();
            reader = new BufferedReader(new FileReader(FilePath.MIN_SET_COVER_ONE_VS_ALL_THRESHOLDS_JSON.toString()));
            unorderedAnnotationElementsDb = AnnotationDbElementsMap.fromJsonString(reader.readLine()).getElements();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
