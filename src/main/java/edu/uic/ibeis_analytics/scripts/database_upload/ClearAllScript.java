package edu.uic.ibeis_analytics.scripts.database_upload;

import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.exceptions.EmptyListParameterException;

public class ClearAllScript {

    public static void main (String[] args) {
        try {
            Ibeis ibeis = new Ibeis();
            try {
                ibeis.deleteAnnotations(ibeis.getAllAnnotations());
            } catch (EmptyListParameterException e) {
                e.printStackTrace();
            }
            try {
                ibeis.deleteImages(ibeis.getAllImages());
            } catch (EmptyListParameterException e) {
                e.printStackTrace();
            }
            try {
                ibeis.deleteEncounters(ibeis.getAllEncounters());
            } catch (EmptyListParameterException e) {
                e.printStackTrace();
            }
            try {
                ibeis.deleteIndividuals(ibeis.getAllIndividuals());
            } catch (EmptyListParameterException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
