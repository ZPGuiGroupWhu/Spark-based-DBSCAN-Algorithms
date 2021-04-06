package org.zzy.dbscan.java.index.Grid;

import org.zzy.dbscan.java.index.balanced_KDTree.KDBSCANPoint;
import org.zzy.dbscan.java.index.balanced_KDTree.KDTree;
import org.zzy.dbscan.java.index.balanced_KDTree.KDTreeChange;

import java.io.Serializable;
import java.util.ArrayList;

public class GridIndexHash implements Serializable {
    public int xGrid;
    public int yGrid;
    public ArrayList<KDBSCANPoint> pAL;
    public KDTreeChange auxKDtree=new KDTreeChange();


    public GridIndexHash(){
        this.xGrid = 0;
        this.yGrid = 0;
        this.pAL = new ArrayList<KDBSCANPoint>();
    }
    public GridIndexHash(int xGrid,int yGrid){
        this.xGrid = xGrid;
        this.yGrid = yGrid;
        this.pAL = new ArrayList<KDBSCANPoint>();
    }

}
