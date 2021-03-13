package org.zzy.dbscan.java.kdrp;

import org.zzy.dbscan.java.index.balanced_KDTree.KDBSCANPoint;
import org.zzy.dbscan.java.index.balanced_KDTree.KDTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MCluster implements Serializable {
    private int id;
    private List<KDBSCANPoint> points=new ArrayList<>();
    private KDTree auxKDTree;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<KDBSCANPoint> getPoints() {
        return points;
    }

    public void setPoints(List<KDBSCANPoint> points) {
        this.points = points;
    }

    public KDTree getAuxKDTree() {
        return auxKDTree;
    }

    public void setAuxKDTree(KDTree auxKDTree) {
        this.auxKDTree = auxKDTree;
    }
}
