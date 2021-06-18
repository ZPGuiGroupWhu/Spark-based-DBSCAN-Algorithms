package org.gzp.team.geocommerceservice.model.geojson;

public class GridProperties {
    private Integer fid;
    private Integer source_id;
    private Integer join_count;
    private String gizscore;
    private String gipvalue;
    private Integer nneighbors;
    private Integer gi_bin;

    public GridProperties() {
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getSource_id() {
        return source_id;
    }

    public void setSource_id(Integer source_id) {
        this.source_id = source_id;
    }

    public Integer getJoin_count() {
        return join_count;
    }

    public void setJoin_count(Integer join_count) {
        this.join_count = join_count;
    }

    public String getGizscore() {
        return gizscore;
    }

    public void setGizscore(String gizscore) {
        this.gizscore = gizscore;
    }

    public String getGipvalue() {
        return gipvalue;
    }

    public void setGipvalue(String gipvalue) {
        this.gipvalue = gipvalue;
    }

    public Integer getNneighbors() {
        return nneighbors;
    }

    public void setNneighbors(Integer nneighbors) {
        this.nneighbors = nneighbors;
    }

    public Integer getGi_bin() {
        return gi_bin;
    }

    public void setGi_bin(Integer gi_bin) {
        this.gi_bin = gi_bin;
    }
}
