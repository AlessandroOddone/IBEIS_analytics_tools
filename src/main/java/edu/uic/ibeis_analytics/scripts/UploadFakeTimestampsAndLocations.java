package edu.uic.ibeis_analytics.scripts;

import edu.uic.ibeis_java_api.api.Ibeis;
import edu.uic.ibeis_java_api.api.IbeisImage;
import edu.uic.ibeis_java_api.api.location.GeoCoordinates;

import java.util.*;

public class UploadFakeTimestampsAndLocations {

    private static final int ENCOUNTER_ID = 38;
    private static final double MIN_LAT = 41.833399;
    private static final double MAX_LAT = 41.833981;
    private static final double MIN_LON = -87.837575;
    private static final double MAX_LON = -87.836309;

    private static Ibeis ibeis = new Ibeis();

    private static List<IbeisImage> arnietaImages = new ArrayList<>();
    private static List<IbeisImage> frannyImages = new ArrayList<>();
    private static List<IbeisImage> jasiriImages = new ArrayList<>();
    private static List<IbeisImage> mithraImages = new ArrayList<>();
    private static List<IbeisImage> potokaImages = new ArrayList<>();

    public static void main(String[] args) {
        try {
            List<IbeisImage> encounterImages = ibeis.getEncounterById(ENCOUNTER_ID).getImages();
            for (IbeisImage image : encounterImages) {
                switch (image.getAnnotations().get(0).getIndividual().getName()) {
                    case "Arnieta":
                        arnietaImages.add(image);
                        break;
                    case "Franny":
                        frannyImages.add(image);
                        break;
                    case "Jasiri":
                        jasiriImages.add(image);
                        break;
                    case "Mithra":
                        mithraImages.add(image);
                        break;
                    case "Potoka":
                        potokaImages.add(image);
                        break;
                }
            }
            setFakeTimestampsAndLocations(arnietaImages);
            setFakeTimestampsAndLocations(frannyImages);
            setFakeTimestampsAndLocations(jasiriImages);
            setFakeTimestampsAndLocations(mithraImages);
            setFakeTimestampsAndLocations(potokaImages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setFakeTimestampsAndLocations(List<IbeisImage> images) {
        try {
            GregorianCalendar dateTime = new GregorianCalendar(2015, 11, 12, 6, 0);
            for (IbeisImage image : images) {
                dateTime.add(Calendar.HOUR_OF_DAY, 2);
                if(dateTime.HOUR_OF_DAY > 20) {
                    dateTime.add(Calendar.DAY_OF_MONTH, 1);
                    dateTime.set(Calendar.HOUR_OF_DAY, 6);
                }
                image.setDatetime(dateTime);
                image.setGpsPosition(getRandomCoordinates());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static GeoCoordinates getRandomCoordinates() {
        Random random = new Random();
        double randomLat = MIN_LAT + (MAX_LAT - MIN_LAT) * random.nextDouble();
        double randomLon = MIN_LON + (MAX_LON - MIN_LON) * random.nextDouble();
        return new GeoCoordinates(randomLat,randomLon);
    }
}
