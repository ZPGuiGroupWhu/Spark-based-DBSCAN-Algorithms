package org.gzp.team.geocommerceservice.model.geojson;

import java.util.List;

public class GridFeatureCollection {
    private String type;
    private Crs crs;
    private List<Feature> features;

    public GridFeatureCollection() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Crs getCrs() {
        return crs;
    }

    public void setCrs(Crs crs) {
        this.crs = crs;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }
}
