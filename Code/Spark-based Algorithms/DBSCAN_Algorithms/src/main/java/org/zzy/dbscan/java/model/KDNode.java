package org.zzy.dbscan.java.model;

import java.io.Serializable;
import java.util.Vector;

public class KDNode implements Serializable {
    protected Point key;
    protected KDNode left,right;
    protected boolean deleted;
    Point value;
    MC mc;

    public Point getKey() {
        return key;
    }

    public void setKey(Point key) {
        this.key = key;
    }

    public KDNode getLeft() {
        return left;
    }

    public void setLeft(KDNode left) {
        this.left = left;
    }

    public KDNode getRight() {
        return right;
    }

    public void setRight(KDNode right) {
        this.right = right;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Point getValue() {
        return value;
    }

    public void setValue(Point value) {
        this.value = value;
    }

    public MC getMc() {
        return mc;
    }

    public void setMc(MC mc) {
        this.mc = mc;
    }

    private KDNode(Point key, Point value, MC mc){
        this.key=key;
        this.value=value;
        left=null;
        right=null;
        deleted=false;
        this.mc=mc;
    }
    protected static KDNode insertNode(Point key,Point value,KDNode node,int level,int dimension,MC mc){
        if(node==null){
            node=new KDNode(key,value,mc);
        }else if(key.equals(node.key)){
            if(node.deleted){
                node.deleted=false;
                node.value=value;
                node.mc=mc;
            }
        }else if(key.getValue()[level]>node.value.getValue()[level]){
            node.right=insertNode(key,value,node.right,(level+1)%dimension,dimension,mc);
        }else {
            node.left=insertNode(key,value,node.left,(level+1)%dimension,dimension,mc);
        }
        return node;
    }

    protected static KDNode searchNode(Point key,KDNode node,int dimension){
        for(int level=0;node!=null;level=(level+1)%dimension){
            if(!node.deleted&& key.equals(node.key)){
                return node;
            }else if(key.getValue()[level]>node.value.getValue()[level]){
                node=node.right;
            }else {
                node=node.left;
            }
        }
        return null;
    }

    protected static void rangeSearch(Point lowKey, Point upKey, KDNode node, int divide, int dimensions,
                                      double[] key, double Eps, Vector<KDNode>nodeVector){
        if(node==null){
            return;
        }
        if(node.value.getValue()[divide]>=lowKey.getValue()[divide]){
            rangeSearch(lowKey,upKey,node.left,(divide+1)%dimensions,dimensions,key,Eps,nodeVector);
        }
        if(node.value.getValue()[divide]<upKey.getValue()[divide]){
            rangeSearch(lowKey,upKey,node.right,(divide+1)%dimensions,dimensions,key,Eps,nodeVector);
        }
        int j;
        for(j=0;j<dimensions && node.value.getValue()[j]>=lowKey.getValue()[j]
         && node.value.getValue()[j]<=upKey.getValue()[j];j++)
            ;
        if((j==dimensions)&&(node.value.getDist(new Point(key))<Eps)){
            nodeVector.add(node);
        }
    }
}
