package org.gzp.team.geocommerceservice.model.geojson;

import java.util.List;

public class JSON<T> {
    private String type;
    private List<Feature2> features;


    public JSON() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Feature2> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature2> features) {
        this.features = features;
    }
}
