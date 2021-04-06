package org.zzy.dbscan.java.TLKDModel;

import org.zzy.dbscan.java.index.balanced_KDTree.KDBSCANPoint;

import java.io.Serializable;
import java.util.Vector;

public class KDNode implements Serializable {
    protected KDBSCANPoint key;
    protected KDNode left,right;
    protected boolean deleted;
    KDBSCANPoint value;
    MC mc;

    public KDBSCANPoint getKey() {
        return key;
    }

    public void setKey(KDBSCANPoint key) {
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

    public KDBSCANPoint getValue() {
        return value;
    }

    public void setValue(KDBSCANPoint value) {
        this.value = value;
    }

    public MC getMc() {
        return mc;
    }

    public void setMc(MC mc) {
        this.mc = mc;
    }

    private KDNode(KDBSCANPoint key, KDBSCANPoint value, MC mc){
        this.key=key;
        this.value=value;
        left=null;
        right=null;
        deleted=false;
        this.mc=mc;
    }
    protected static KDNode insertNode(KDBSCANPoint key, KDBSCANPoint value, KDNode node, int level, int dimension, MC mc){
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


    protected static void rangeSearch(KDBSCANPoint lowKey, KDBSCANPoint upKey, KDNode node, int divide, int dimensions,
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
//        int j;
//        for(j=0;j<dimensions && node.value.getValue()[j]>=lowKey.getValue()[j]
//         && node.value.getValue()[j]<=upKey.getValue()[j];j++)
//            ;
//        if((j==dimensions)&&(node.value.getDist(new KDBSCANPoint(key))<Eps)){
//            nodeVector.add(node);
//        }
        if(node.value.getDist(new KDBSCANPoint(key))<=Eps){
            nodeVector.add(node);
        }
    }

    protected static void rangeQuery(double[]  key,KDNode node,int divide,double Eps,Vector<KDNode>nodeVector,int dimensions){
        if(node==null){
            return;
        }
        if(node.value.getValue()[divide]>=key[divide]){
            if(node.value.getDist(new KDBSCANPoint(key))<=Eps){
                nodeVector.add(node);
            }
            rangeQuery(key,node.left,(divide+1)%dimensions,Eps,nodeVector,dimensions);
        }
        if(node.value.getValue()[divide]<key[divide]){
            if(node.value.getDist(new KDBSCANPoint(key))<=Eps){
                nodeVector.add(node);
            }
            rangeQuery(key,node.right,(divide+1)%dimensions,Eps,nodeVector,dimensions);
        }
    }

}
