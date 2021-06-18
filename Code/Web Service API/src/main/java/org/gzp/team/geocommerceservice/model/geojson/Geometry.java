package org.gzp.team.geocommerceservice.model.geojson;

import java.util.List;

public class Geometry {
    private String type;
    private List coordinates;

    public Geometry() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List coordinates) {
        this.coordinates = coordinates;
    }
}
