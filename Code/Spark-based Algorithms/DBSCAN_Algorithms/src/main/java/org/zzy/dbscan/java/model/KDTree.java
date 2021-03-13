package org.zzy.dbscan.java.model;

import org.zzy.dbscan.java.index.balanced_KDTree.KDBSCANPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class KDTree<T> implements Serializable {
    public int dimentions;
    public KDNode root;
    public int nodeCount;

    public KDTree(int k){
        dimentions=k;
        root=null;
    }

    public void insertTree(double[]key, KDBSCANPoint value, MC mc){
        if(key.length!=dimentions){
            throw new RuntimeException("KD-Tree：wrong key size！");
        }else {
            root=KDNode.insertNode(new KDBSCANPoint(key),value,root,0,dimentions,mc);
        }
        nodeCount++;
    }
    public List<KDBSCANPoint> range(double[]lowkey,double[]upkey,double[] key,double Eps){
        Vector<KDNode> nodeVector=new Vector<>();
        KDNode.rangeSearch(new KDBSCANPoint(lowkey),new KDBSCANPoint(upkey),root,0,dimentions,key,Eps,nodeVector);
        List<KDBSCANPoint>list=new ArrayList<>(nodeVector.size());
        for(KDNode kdNode:nodeVector){
            list.add(kdNode.value);
        }
//        for(int i=0;i<nodeVector.size();i++){
//            KDNode node=nodeVector.elementAt(i);
//            list.add(i,node.value);
//        }
        return list;
    }
    public List<KDBSCANPoint> rangeMC(double[]lowkey,double[]upkey,double[] key,double Eps){
        Vector<KDNode> nodeVector=new Vector<>();
        KDNode.rangeSearch(new KDBSCANPoint(lowkey),new KDBSCANPoint(upkey),root,0,dimentions,key,Eps,nodeVector);
        List<KDBSCANPoint>list=new ArrayList<>(nodeVector.size());
        for(KDNode kdNode:nodeVector){
            List<KDBSCANPoint>list1=kdNode.mc.getAuxKDTree().rangeSearch(key,0.5*Eps);
            if(list1!=null){
                list.addAll(list1);
            }
        }
        return list;
    }

    public List<KDBSCANPoint> rangeSearch(double[] key,double Eps){
        double[][] corners=new double[2][key.length];
        for(int i=0;i<key.length;i++){
            corners[0][i]=key[i]-Eps;
            corners[1][i]=key[i]+Eps;
        }
        return range(corners[0],corners[1],key,Eps);
    }
    public List<KDBSCANPoint> reachableMCSearch(double[] key,double Eps){
        double[][] corners=new double[2][key.length];
        for(int i=0;i<key.length;i++){
            corners[0][i]=key[i]-Eps;
            corners[1][i]=key[i]+Eps;
        }
        return rangeMC(corners[0],corners[1],key,Eps);
    }
}
