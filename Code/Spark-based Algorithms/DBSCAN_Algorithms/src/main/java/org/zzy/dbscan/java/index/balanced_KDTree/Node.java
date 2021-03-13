package org.zzy.dbscan.java.index.balanced_KDTree;


import org.zzy.dbscan.java.kdrp.MCluster;
import org.zzy.dbscan.java.model.MC;

import java.io.Serializable;

public class Node implements Serializable {
    private long leval;//定义数据的层数，根节点为0，依次向下
    //分割的维度
    private int partitionDimention;
     //分割的值
    private double partitionValue;
     //平衡KD树每个节点都有数据
    private KDBSCANPoint value;
     //是否为叶子
    private boolean isLeaf=false;
    //左树
    private Node left;
     //右树
    private Node right;
     //每个维度的最小值
    private double[] min;
     //每个维度的最大值
    private double[] max;
    //每个节点对应的矩形区域
    private DBSCANRectangle rectangle;

    private MCluster mCluster;

    public MCluster getmCluster() {
        return mCluster;
    }

    public void setmCluster(MCluster mCluster) {
        this.mCluster = mCluster;
    }

    public long getLeval() {
        return leval;
    }

    public void setLeval(long leval) {
        this.leval = leval;
    }

    public DBSCANRectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(DBSCANRectangle rectangle) {
        this.rectangle = rectangle;
    }

    public int getPartitionDimention() {
        return partitionDimention;
    }

    public void setPartitionDimention(int partitionDimention) {
        this.partitionDimention = partitionDimention;
    }

    public double getPartitionValue() {
        return partitionValue;
    }

    public void setPartitionValue(double partitionValue) {
        this.partitionValue = partitionValue;
    }

    public KDBSCANPoint getValue() {
        return value;
    }

    public void setValue(KDBSCANPoint value) {
        this.value = value;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public double[] getMin() {
        return min;
    }

    public void setMin(double[] min) {
        this.min = min;
    }

    public double[] getMax() {
        return max;
    }

    public void setMax(double[] max) {
        this.max = max;
    }
}
