package edu.uic.ibeis_analytics.hotspotter_output_files_model;

import edu.uic.ibeis_java_api.api.data.annotation.BoundingBox;

public class ChipTableEntry implements Comparable<ChipTableEntry> {

    private int id;
    private int imageId;
    private int nameId;
    private BoundingBox boundingBox;

    public ChipTableEntry(int id, int imageId, int nameId, BoundingBox boundingBox) {
        this.id = id;
        this.imageId = imageId;
        this.nameId = nameId;
        this.boundingBox = boundingBox;
    }

    public int getId() {
        return id;
    }

    public int getImageId() {
        return imageId;
    }

    public int getNameId() {
        return nameId;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChipTableEntry)) {
            return false;
        }
        if (getId() == ((ChipTableEntry) obj).getId()) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(ChipTableEntry o) {
        return Integer.compare(getId(), o.getId());
    }
}
