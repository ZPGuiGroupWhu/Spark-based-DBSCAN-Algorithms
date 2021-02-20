package org.zzy.dbscan.java.model;

import java.io.Serializable;

public class Point implements Serializable {
    private int id;
    private int MCID;

    public int getMCID() {
        return MCID;
    }

    public void setMCID(int MCID) {
        this.MCID = MCID;
    }

    private double[] value;
    private boolean visited=false;
    private int cluster=0;
    private Flag flag= Flag.NotFlagged;
    public enum Flag{
        Border,Core,Noise,NotFlagged
    }

    public Point() {
    }
    public Point(double[] coord){
        this.value=coord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    public double getDist(Point p){
        double dis=Math.sqrt(Math.pow((this.value[0]-p.value[0]), 2)+Math.pow((this.value[1]-p.value[1]), 2));
        return dis;
    }
    @Override
    public String toString() {
        return value[0] +"," + value[1] +"," + cluster;
    }

}
