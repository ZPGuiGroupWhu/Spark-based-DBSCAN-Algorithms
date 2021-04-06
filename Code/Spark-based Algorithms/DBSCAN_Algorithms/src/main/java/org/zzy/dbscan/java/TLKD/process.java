package org.zzy.dbscan.java.TLKD;

import org.zzy.dbscan.java.index.balanced_KDTree.DBSCANRectange;
import org.zzy.dbscan.java.index.balanced_KDTree.KDBSCANPoint;
import org.zzy.dbscan.java.TLKDModel.KDNode;
import org.zzy.dbscan.java.TLKDModel.KDTree;
import org.zzy.dbscan.java.TLKDModel.MC;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class process implements Serializable {
    //构建微类簇
    public void buildMicroClusters(List<KDBSCANPoint>points, double Eps, KDTree<KDBSCANPoint> kdTree){
//        List<MC> MCList=new ArrayList<>();
        List<KDBSCANPoint> unassignedList=new ArrayList<>();//第一轮未标记的点
        int uFlag;
        for(KDBSCANPoint point1:points){
            buildMCs(point1.getValue(),point1,unassignedList, Eps,kdTree);
        }
        for(KDBSCANPoint point2:unassignedList){
            uFlag=processUnassignedPoint(point2,kdTree.root,0,point2.getValue().length,Eps);
            if(uFlag==0){
                MC mc=new MC();
//                mc.setId(MCList.size());
//                mc.setCenter(point2.getValue());
//                mc.setPoints(point2);
                mc.setAuxKDTree(point2);
//                point2.setMCID(mc.getId());
                kdTree.insertTree(point2.getValue(),point2,mc);
//                MCList.add(mc.getId(),mc);
            }
        }
//        return MCList;
    }
    //构建第二层KD树
//    public void buildAuxKDTree(MC mc){
//        KDTree<KDBSCANPoint> kdTree1=new KDTree<>(2);
//        for(KDBSCANPoint point:mc.getPoints()){
//            kdTree1.insertTree(point.getValue(),point,null);
//        }
//        mc.setAuxKDTree(kdTree1);
//    }
    //在MC中构建第二层KD树
//    public void buildAuxKDTreeFromMC(List<MC>list){
//        for(MC mc:list){
//            buildAuxKDTree(mc);
//        }
//    }

    public void buildMCs(double[]key,KDBSCANPoint value,List<KDBSCANPoint> unassignedList,double Eps,KDTree<KDBSCANPoint> kdTree){
        int pFlag;
        if(kdTree.root==null){
            MC mc=new MC();
//            mc.setId(list.size());//编号是MC列表长度
//            mc.setCenter(key);
//            mc.setPoints(value);
            mc.setAuxKDTree(value);
//            value.setMCID(mc.getId());
            kdTree.insertTree(key,value,mc);
//            list.add(mc);
        }else {
            pFlag=processPoint(value,kdTree.root,unassignedList,0,key.length, Eps);
            if(pFlag==0){
                MC mc=new MC();
//                mc.setId(list.size());
//                mc.setCenter(key);
//                mc.setPoints(value);
                mc.setAuxKDTree(value);
//                value.setMCID(mc.getId());
                kdTree.insertTree(key,value,mc);
//                list.add(mc);
            }
        }
    }

    public int processPoint(KDBSCANPoint p, KDNode node, List<KDBSCANPoint> unassignedList, int level, int dimensions, double Eps){
        int pFlag=0;
        if(node.getKey().getDist(p)<= Eps){
//            node.getMc().setPoints(p);
            node.getMc().setAuxKDTree(p);
//            p.setMCID(node.getMc().getId());
            return 1;
        }
        if(node.getKey().getDist(p)<= 2*Eps){
            unassignedList.add(p);
            return 1;
        }
        if(node.getLeft()!=null && p.getValue()[level]<=node.getValue().getValue()[level]){
            pFlag=processPoint(p,node.getLeft(),unassignedList,(level+1)%dimensions,dimensions,Eps);
        }
        if(node.getRight()!=null && p.getValue()[level]>node.getValue().getValue()[level]){
            pFlag=processPoint(p,node.getRight(),unassignedList,(level+1)%dimensions,dimensions,Eps);
        }

        return pFlag;
    }

    public int processUnassignedPoint(KDBSCANPoint p, KDNode node, int level, int dimensions, double Eps){
        int uFlag=0;
        if(node.getKey().getDist(p)<= Eps){
//            node.getMc().setPoints(p);
            node.getMc().setAuxKDTree(p);
//            p.setMCID(node.getMc().getId());
            return 1;
        }
        if(node.getLeft()!=null && p.getValue()[level]<=node.getValue().getValue()[level]){
            uFlag=processUnassignedPoint(p,node.getLeft(),(level+1)%dimensions,dimensions,Eps);
        }
        if(node.getRight()!=null && p.getValue()[level]>node.getValue().getValue()[level]){
            uFlag=processUnassignedPoint(p,node.getRight(),(level+1)%dimensions,dimensions,Eps);
        }
        return uFlag;
    }

    public List<KDBSCANPoint> findReachableMCsFromPoint(KDBSCANPoint point,KDTree<KDBSCANPoint> kdTree,double Eps){
        List<KDBSCANPoint> list=kdTree.reachableMCSearch(point.getValue(),2*Eps);
        return list;
    }
    //获取点及其邻域
    public List<KDBSCANPoint> findNBPoints(KDBSCANPoint point,double Eps,int MinPts,KDTree<KDBSCANPoint> kdTree){
        long i=System.currentTimeMillis();
        List<KDBSCANPoint>list=findReachableMCsFromPoint(point,kdTree,Eps);
        long j=System.currentTimeMillis();
        System.out.println("邻域点个数为："+list.size());
        System.out.println("时间消耗为："+(j-i)/1000);
        return list;
    }
    public List<KDBSCANPoint>processPoint(List<List<KDBSCANPoint>> lists){
        List<KDBSCANPoint>list=new ArrayList<>();
        for(List<KDBSCANPoint> points:lists){
            for(KDBSCANPoint point:points){
                list.add(point);
            }
        }
        return list;
    }
    //取出MCList中的点
//    public List<KDBSCANPoint> getPoints(List<MC> mcList){
//        List<KDBSCANPoint>list=new ArrayList<>();
//        for(MC mc:mcList){
//            list.addAll(mc.getPoints());
//        }
//        return list;
//    }
    //遍历双层KD树获取数据点
    public List<KDBSCANPoint> nonRecOrder(KDNode node){
        List<KDBSCANPoint> list=new ArrayList<>();
        if(node==null)return list;
        Stack<KDNode> stack=new Stack<>();
        stack.push(node);
        while (!stack.isEmpty()){
            KDNode kdNode=stack.pop();
            List<KDBSCANPoint> ls=nonRecOrderTemp(kdNode.getMc().getAuxKDTree().root);
            list.addAll(ls);
            if(kdNode.getRight()!=null){
                stack.push(kdNode.getRight());
            }
            if(kdNode.getLeft()!=null){
                stack.push(kdNode.getLeft());
            }
        }
        return list;
    }
    public List<KDBSCANPoint> nonRecOrderTemp(KDNode node){
        List<KDBSCANPoint> list=new ArrayList<>();
        if(node==null)return null;
        Stack<KDNode> stack=new Stack<>();
        stack.push(node);
        while (!stack.isEmpty()){
            KDNode kdNode=stack.pop();
            list.add(kdNode.getValue());
            if(kdNode.getRight()!=null){
                stack.push(kdNode.getRight());
            }
            if(kdNode.getLeft()!=null){
                stack.push(kdNode.getLeft());
            }
        }
        return list;
    }

    //计算MBR
    public DBSCANRectange getRectange(Double x1,Double y1,Double x2,Double y2){
        DBSCANRectange rectange=new DBSCANRectange(x1,y1,x2,y2);
        return rectange;
    }
}
