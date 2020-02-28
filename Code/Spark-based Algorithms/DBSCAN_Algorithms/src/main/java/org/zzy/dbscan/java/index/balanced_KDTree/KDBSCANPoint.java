package org.zzy.dbscan.java.index.balanced_KDTree;

import java.io.Serializable;

public class KDBSCANPoint implements Serializable {
    private double[] value;
    private boolean visited=false;
    private int cluster=0;
    private Flag flag= Flag.NotFlagged;
    public enum Flag{
        Border,Core,Noise,NotFlagged
    }

    public KDBSCANPoint() {
    }

    public double[] getValue() {
        return value;
    }

    public void setValue(double[] value) {
        this.value = value;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return value[0] +"," + value[1] +"," + cluster;
    }
}
