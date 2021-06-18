package org.gzp.team.geocommerceservice.model.geojson;

public class Crs<T> {
    private String type;
    private T properties;

    public Crs() {
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
