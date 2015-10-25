package edu.uic.ibeis_analytics.databases;

import edu.uic.ibeis_analytics.hotspotter_output_files_model.*;
import edu.uic.ibeis_java_api.api.*;
import edu.uic.ibeis_java_api.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class DatabaseUploader {

    private static ChipTable chipTable;
    private static ImageTable imageTable;
    private static NameTable nameTable;

    private static Ibeis ibeis = new Ibeis();

    private String databaseName;
    private IbeisEncounter encounter;
    private Collection<IbeisIndividual> individuals;

    private static HashMap<Integer,IbeisImage> ibeisImageHashMap;
    private static HashMap<Integer,IbeisIndividual> ibeisIndividualHashMap;

    public DatabaseUploader(File hotspotterOutputMainFolder, String databaseName, Collection<IbeisIndividual> individuals) {

        chipTable = new ChipTable(new File(hotspotterOutputMainFolder.toString() + "/_hsdb/chip_table.csv"));
        imageTable = new ImageTable(new File(hotspotterOutputMainFolder.toString() + "/_hsdb/image_table.csv"));
        nameTable = new NameTable(new File(hotspotterOutputMainFolder.toString() + "/_hsdb/name_table.csv"));
        this.databaseName = databaseName;
        this.individuals = individuals;
    }

    public void execute() {
        encounter = getEncounter(databaseName);
        ibeisIndividualHashMap = loadIndividuals(individuals);
        ibeisImageHashMap = loadImages();
        addAnnotations();
    }

    private IbeisEncounter getEncounter(String name) {
        try {
            return ibeis.addEncounter(name);
        } catch (IOException | BadHttpRequestException | UnsuccessfulHttpRequestException e) {
            e.printStackTrace();
        } catch (IndividualNameAlreadyExistsException e) {
            return findEncounter(name);
        } return null;
    }

    private IbeisEncounter findEncounter(String name) {
        try {
            List<IbeisEncounter> allEncounters = ibeis.getAllEncounters();
            for (IbeisEncounter encounter : allEncounters) {
                if (encounter.getName().equals(name)) {
                    return encounter;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<Integer,IbeisIndividual> loadIndividuals(Collection<IbeisIndividual> individuals) {
        ibeisIndividualHashMap = new HashMap<>();

        for (NameTableEntry nameTableEntry : nameTable.getTableEntries()) {
            int nameId = nameTableEntry.getId();
            String name = nameTableEntry.getName();

            for (IbeisIndividual individual : individuals) {
                try {
                    if (name.equals(individual.getName())) {
                        ibeisIndividualHashMap.put(nameId,individual);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ibeisIndividualHashMap;
    }

    private HashMap<Integer,IbeisImage> loadImages() {
        ibeisImageHashMap = new HashMap<>();

        for (ImageTableEntry imageTableEntry : imageTable.getTableEntries()) {
            IbeisImage ibeisImage = null;
            try {
                ibeisImage = ibeis.uploadImage(imageTableEntry.getFilepath());
            } catch (UnsupportedImageFileTypeException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsuccessfulHttpRequestException e) {
                e.printStackTrace();
            } catch (BadHttpRequestException e) {
                e.printStackTrace();
            } finally {
                try {
                    ibeisImage.addToEncounter(encounter);
                    ibeisImageHashMap.put(imageTableEntry.getId(),ibeisImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return  ibeisImageHashMap;
    }

    private static void addAnnotations() {
        try {
            for (ChipTableEntry chipTableEntry : chipTable.getTableEntries()) {
                IbeisImage image = ibeisImageHashMap.get(chipTableEntry.getImageId());
                if (image.getAnnotations().isEmpty()) {
                    ibeis.addAnnotation(image,chipTableEntry.getBoundingBox())
                            .setIndividual(ibeisIndividualHashMap.get(chipTableEntry.getNameId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
