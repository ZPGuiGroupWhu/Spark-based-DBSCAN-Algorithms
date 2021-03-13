package org.zzy.dbscan.java.model;

import org.zzy.dbscan.java.index.balanced_KDTree.KDBSCANPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MC implements Serializable {
//    private int id;//超球体的ID，记录个数
//    private double[] center;//超球体的中心坐标
//    private List<KDBSCANPoint> points=new ArrayList<>();//超球体包含的点
    private KDTree<KDBSCANPoint> auxKDTree=new KDTree<>(2);
//    public List<Integer> reachList;//可达的MCs的ID列表



//    public List<KDBSCANPoint> getPoints() {
//        return points;
//    }
//
//    public void setPoints(KDBSCANPoint point) {
//        points.add(point);
//    }
//
//    public List<Integer> getReachList() {
//        return reachList;
//    }
//
//    public void setReachList(List<Integer> reachList) {
//        this.reachList = reachList;
//    }

    public KDTree<KDBSCANPoint> getAuxKDTree() {
        return auxKDTree;
    }

    public void setAuxKDTree(KDBSCANPoint point) {
        auxKDTree.insertTree(point.getValue(),point,null);
    }

    public void setAuxKDTree(KDTree<KDBSCANPoint> auxKDTree) {
        this.auxKDTree = auxKDTree;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public double[] getCenter() {
//        return center;
//    }
//
//    public void setCenter(double[] center) {
//        this.center = center;
//    }
}
