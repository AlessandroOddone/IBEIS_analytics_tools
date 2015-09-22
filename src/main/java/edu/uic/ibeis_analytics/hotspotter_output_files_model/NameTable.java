package edu.uic.ibeis_analytics.hotspotter_output_files_model;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NameTable {

    private List<NameTableEntry> tableEntries = new ArrayList<>();

    public NameTable(File file) {
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
                int id = Integer.parseInt(nextLine[0].replaceAll("\\s", ""));
                String name = nextLine[1].replaceAll("\\s", "");

                tableEntries.add(new NameTableEntry(id,name));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tableEntries;
    }

    public List<NameTableEntry> getTableEntries() {
        return tableEntries;
    }
}
