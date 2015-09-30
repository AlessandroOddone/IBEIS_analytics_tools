package edu.uic.ibeis_analytics.scripts.db_upload;

import edu.uic.ibeis_analytics.databases.DatabaseUploader;
import edu.uic.ibeis_analytics.databases.brookfield_zoo.giraffes.BrookfieldZooGiraffesCollection;
import edu.uic.ibeis_java_api.api.IbeisIndividual;

import java.io.File;
import java.util.Collection;

public class DatabaseUploadScript {

    private static final String DB_MAIN_FOLDER = DatabaseUploadScript.class.getResource("/brookfield_zoo_giraffes_db/").getPath();
    private static final String TEST_MAIN_FOLDER = DatabaseUploadScript.class.getResource("/brookfield_zoo_giraffes_test/").getPath();
    private static final String VALIDATION_MAIN_FOLDER = DatabaseUploadScript.class.getResource("/brookfield_zoo_giraffes_validation/").getPath();
    private static final String BIG_DB_MAIN_FOLDER = DatabaseUploadScript.class.getResource("/brookfield_zoo_giraffes_big/").getPath();

    private static final String DB_ENCOUNTER_NAME = "Brookfield Zoo Giraffes Db";
    private static final String TEST_ENCOUNTER_NAME = "Brookfield Zoo Giraffes Test";
    private static final String VALIDATION_ENCOUNTER_NAME = "Brookfield Zoo Giraffes Validation";
    private static final String BIG_DB_ENCOUNTER_NAME = "Brookfield Zoo Giraffes Big Database";

    private static final Collection<IbeisIndividual> individuals = new BrookfieldZooGiraffesCollection().getIndividuals();

    public static void main(String args[]) {
        //upload main db
        //new DatabaseUploader(new File(DB_MAIN_FOLDER), DB_ENCOUNTER_NAME, individuals).execute();

        //upload test db
        //new DatabaseUploader(new File(TEST_MAIN_FOLDER), TEST_ENCOUNTER_NAME, individuals).execute();

        //upload validation db
        //new DatabaseUploader(new File(VALIDATION_MAIN_FOLDER), VALIDATION_ENCOUNTER_NAME, individuals).execute();

        //upload big db
        new DatabaseUploader(new File(BIG_DB_MAIN_FOLDER), BIG_DB_ENCOUNTER_NAME, individuals).execute();
    }
}
