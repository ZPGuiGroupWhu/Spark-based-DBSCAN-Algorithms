package org.gzp.team.geocommerceservice.model.geojson;

public class Feature2<T> {
    private Geometry geometry;
    private String type;
    private T properties;

    public Feature2() {
    }


    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getProperties() {
        return properties;
    }

    public void setProperties(T properties) {
        this.properties = properties;
    }
}
