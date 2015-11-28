package edu.uic.ibeis_analytics.scripts.database_upload;

import edu.uic.ibeis_java_api.api.IbeisIndividual;
import edu.uic.ibeis_java_api.database_upload_tools.hotspotter.HotspotterDatabaseUploader;

import java.io.File;
import java.util.Collection;

public class UploadDatabaseScript {

    private static final String DATABASE_MAIN_FOLDER = "/home/alessandro/IdeaProjects/ibeis_analytics/src/main/resources/brookfield_zoo_giraffes_database/";
    private static final String VALIDATION_MAIN_FOLDER = "/home/alessandro/IdeaProjects/ibeis_analytics/src/main/resources/brookfield_zoo_giraffes_validation/";

    private static final String DATABASE_ENCOUNTER_NAME = "Brookfield Zoo Giraffes Database";
    private static final String VALIDATION_ENCOUNTER_NAME = "Brookfield Zoo Giraffes Validation Set";

    private static final Collection<IbeisIndividual> individuals = new BrookfieldZooGiraffesCollection().getIndividuals();

    public static void main(String args[]) {
        //upload database
        try {
            new HotspotterDatabaseUploader(new File(DATABASE_MAIN_FOLDER), DATABASE_ENCOUNTER_NAME, individuals).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //upload validation set
        try {
            new HotspotterDatabaseUploader(new File(VALIDATION_MAIN_FOLDER), VALIDATION_ENCOUNTER_NAME, individuals).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
