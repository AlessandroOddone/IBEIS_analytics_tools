package edu.uic.ibeis_analytics.scripts.db_upload;

import edu.uic.ibeis_analytics.databases.brookfield_zoo.giraffes.BrookfieldZooGiraffesCollection;
import edu.uic.ibeis_java_api.api.IbeisIndividual;
import edu.uic.ibeis_java_api.database_upload_tools.hotspotter.HotspotterDatabaseUploader;

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
        /*
        //upload main db
        try {
            new HotspotterDatabaseUploader(new File(DB_MAIN_FOLDER), DB_ENCOUNTER_NAME, individuals).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        /*
        //upload test db
        try {
            new HotspotterDatabaseUploader(new File(TEST_MAIN_FOLDER), TEST_ENCOUNTER_NAME, individuals).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        /*
        //upload validation db
        try {
            new HotspotterDatabaseUploader(new File(VALIDATION_MAIN_FOLDER), VALIDATION_ENCOUNTER_NAME, individuals).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        //upload big db
        try {
            new HotspotterDatabaseUploader(new File(BIG_DB_MAIN_FOLDER), BIG_DB_ENCOUNTER_NAME, individuals).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
