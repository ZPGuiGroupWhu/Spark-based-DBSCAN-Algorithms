package org.zzy.dbscan.java.model;

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

    public void insertTree(double[]key,Point value,MC mc){
        if(key.length!=dimentions){
            throw new RuntimeException("KD-Tree：wrong key size！");
        }else {
            root=KDNode.insertNode(new Point(key),value,root,0,dimentions,mc);
        }
        nodeCount++;
    }
    public List<Point> range(double[]lowkey,double[]upkey,double[] key,double Eps){
        Vector<KDNode> nodeVector=new Vector<>();
        KDNode.rangeSearch(new Point(lowkey),new Point(upkey),root,0,dimentions,key,Eps,nodeVector);
        List<Point>list=new ArrayList<>(nodeVector.size());
        for(int i=0;i<nodeVector.size();i++){
            KDNode node=nodeVector.elementAt(i);
            list.add(i,node.value);
        }
        return list;
    }
    public List<Integer> rangeMC(double[]lowkey,double[]upkey,double[] key,double Eps){
        Vector<KDNode> nodeVector=new Vector<>();
        KDNode.rangeSearch(new Point(lowkey),new Point(upkey),root,0,dimentions,key,Eps,nodeVector);
        List<Integer>list=new ArrayList<>(nodeVector.size());
        for(int i=0;i<nodeVector.size();i++){
            KDNode node=nodeVector.elementAt(i);
            list.add(i,node.mc.getId());
        }
        return list;
    }

    public List<Point> rangeSearch(double[] key,double Eps){
        double[][] corners=new double[2][key.length];
        for(int i=0;i<key.length;i++){
            corners[0][i]=key[i]-Eps;
            corners[1][i]=key[i]+Eps;
        }
        return range(corners[0],corners[1],key,Eps);
    }
    public List<Integer> reachableMCSearch(double[] key,double Eps){
        double[][] corners=new double[2][key.length];
        for(int i=0;i<key.length;i++){
            corners[0][i]=key[i]-Eps;
            corners[1][i]=key[i]+Eps;
        }
        return rangeMC(corners[0],corners[1],key,Eps);
    }

//    public void buildMicroClusters(double[]key,Point value,List<MC> list,List<Point> unassignedList,double Eps){
//        int pFlag;
//        if(root==null){
//            MC mc=new MC();
//            mc.setId(list.size()+1);
//            mc.setCenter(key);
//            mc.setPoints(value);
//            insertTree(mc.getCenter(),new Point(mc.getCenter()),mc);
//            list.add(mc);
//        }else {
//            pFlag=processPoint(value,root,unassignedList,0,key.length, Eps);
//            if(pFlag==0){
//                MC mc=new MC();
//                mc.setId(list.size()+1);
//                mc.setCenter(key);
//                mc.setPoints(value);
//                insertTree(mc.getCenter(),new Point(mc.getCenter()),mc);
//                list.add(mc);
//            }
//        }
//    }
//    public int processPoint(Point p,KDNode node,List<Point> unassignedList,int level,int dimentions,double Eps){
//        int pFlag=0;
//        if(node.left==null && node.right==null){
//            if(node.key.getDist(p)<= Eps){
//                node.mc.setPoints(p);
//                return 1;
//            }
//            if(node.key.getDist(p)<= 2*Eps){
//                unassignedList.add(p);
//                return 1;
//            }
//        }else if(node.left==null && p.getValue()[level]<=node.value.getValue()[level]){
//            if(node.key.getDist(p)<= Eps){
//                node.mc.setPoints(p);
//                return 1;
//            }
//            if(node.key.getDist(p)<= 2*Eps){
//                unassignedList.add(p);
//                return 1;
//            }
//        }else if(node.right==null && p.getValue()[level]>node.value.getValue()[level]){
//            if(node.key.getDist(p)<= Eps){
//                node.mc.setPoints(p);
//                return 1;
//            }
//            if(node.key.getDist(p)<= 2*Eps){
//                unassignedList.add(p);
//                return 1;
//            }
//        }else {
//            if(p.getValue()[level]>node.value.getValue()[level]){
//                processPoint(p,node.right,unassignedList,(level+1)%dimentions,dimentions,Eps);
//            }else {
//                processPoint(p,node.left,unassignedList,(level+1)%dimentions,dimentions,Eps);
//            }
//        }
//        return pFlag;
//    }
}
