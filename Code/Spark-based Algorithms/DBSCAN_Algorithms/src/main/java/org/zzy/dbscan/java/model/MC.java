package org.zzy.dbscan.java.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MC implements Serializable {
    private int id;//超球体的ID，记录个数
    private List<Point> outPoints;//超球体内eps/2以外区域包含的点
    private double[] center;//超球体的中心坐标
    private List<Point> inPoints;//内部eps/2超球体包含的点
    private KDTree<Point> auxKDTree;
    public List<Integer> reachList;//可达的MCs的ID列表

    public List<Point> getOutPoints() {
        return outPoints;
    }
    List<Point> outPointsTemp=new ArrayList<>();

    public void setOutPoints(Point point,double eps) {
        double dis=point.getDist(new Point(center));
        if(dis>0.5*eps && dis<=eps){
            outPoints.add(point);
        }
        this.outPoints = outPointsTemp;
    }

    public List<Point> getInPoints() {
        return inPoints;
    }

    List<Point> inPointsTemp=new ArrayList<>();

    public void setInPoints(Point point,double eps) {
        double dis=point.getDist(new Point(center));
        if(dis<=0.5*eps){
            inPointsTemp.add(point);
        }
        this.inPoints = inPointsTemp;
    }

    public List<Integer> getReachList() {
        return reachList;
    }

    public void setReachList(List<Integer> reachList) {
        this.reachList = reachList;
    }

    public KDTree<Point> getAuxKDTree() {
        return auxKDTree;
    }

    public void setAuxKDTree(KDTree<Point> auxKDTree) {
        this.auxKDTree = auxKDTree;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double[] getCenter() {
        return center;
    }

    public void setCenter(double[] center) {
        this.center = center;
    }
}
