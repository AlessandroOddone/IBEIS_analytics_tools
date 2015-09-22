package edu.uic.ibeis_analytics.hotspotter_output_files_model;

import com.opencsv.CSVReader;
import edu.uic.ibeis_java_api.api.data.annotation.BoundingBox;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChipTable {

    private List<ChipTableEntry> tableEntries = new ArrayList<>();

    public ChipTable(File file) {
        tableEntries = loadTable(file);
    }

    public List loadTable(File file) {

        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            String [] nextLine;

            // skip headers
            reader.readNext();
            reader.readNext();
            reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                int id = Integer.parseInt(nextLine[0].replaceAll("\\s",""));
                int imageId = Integer.parseInt(nextLine[1].replaceAll("\\s",""));
                int nameId = Integer.parseInt(nextLine[2].replaceAll("\\s",""));

                String[] boundingBoxStringValues = nextLine[3].replaceAll("\\s*[\\[\\]]\\s*", "").replaceAll("\\s+", " ").split("\\s");

                int x = Integer.parseInt(boundingBoxStringValues[0]);
                int y = Integer.parseInt(boundingBoxStringValues[1]);
                int w = Integer.parseInt(boundingBoxStringValues[2]);
                int h = Integer.parseInt(boundingBoxStringValues[3]);
                BoundingBox boundingBox = new BoundingBox(x,y,w,h);

                tableEntries.add(new ChipTableEntry(id,imageId,nameId,boundingBox));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tableEntries;
    }

    public List<ChipTableEntry> getTableEntries() {
        return tableEntries;
    }
}
