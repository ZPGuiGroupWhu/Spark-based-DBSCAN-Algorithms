//package org.zzy.dbscan.java.kdrp;
//
//import org.zzy.dbscan.java.model.KDNode;
//import org.zzy.dbscan.java.model.KDTree;
//import org.zzy.dbscan.java.model.MC;
//import org.zzy.dbscan.java.model.Point;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
//public class kdrpJava implements Serializable {
//    //构建微类簇
//    public List<MC> buildMicroClusters(List<Point>points,double Eps, KDTree<Point> kdTree){
//        List<MC> MCList=new ArrayList<>();
//        List<Point> unassignedList=new ArrayList<>();//第一轮未标记的点
//        int uFlag;
//        for(Point point1:points){
//            buildMCs(point1.getValue(),point1,MCList,unassignedList, Eps,kdTree);
//        }
//        for(Point point2:unassignedList){
//            uFlag=processUnassignedPoint(point2,kdTree.root,0,point2.getValue().length,Eps);
//            if(uFlag==0){
//                MC mc=new MC();
//                mc.setId(MCList.size());
//                mc.setCenter(point2.getValue());
//                mc.setPoints(point2);
//                point2.setMCID(mc.getId());
//                kdTree.insertTree(mc.getCenter(),new Point(mc.getCenter()),mc);
//                MCList.add(mc.getId(),mc);
//            }
//        }
//        return MCList;
//    }
//    //构建第二层KD树
//    public void buildAuxKDTree(MC mc){
//        KDTree<Point> kdTree1=new KDTree<>(2);
//        for(Point point:mc.getPoints()){
//            kdTree1.insertTree(point.getValue(),point,null);
//        }
//        mc.setAuxKDTree(kdTree1);
//    }
//    //在MC中构建第二层KD树
//    public void buildAuxKDTreeFromMC(List<MC>list){
//        for(MC mc:list){
//            buildAuxKDTree(mc);
//        }
//    }
//
//    public void buildMCs(double[]key,Point value,List<MC> list,List<Point> unassignedList,double Eps,KDTree<Point> kdTree){
//        int pFlag;
//        if(kdTree.root==null){
//            MC mc=new MC();
//            mc.setId(list.size());//编号是MC列表长度
//            mc.setCenter(key);
//            mc.setPoints(value);
//            mc.setAuxKDTree(value);
//            value.setMCID(mc.getId());
//            kdTree.insertTree(mc.getCenter(),new Point(mc.getCenter()),mc);
//            list.add(mc);
//        }else {
//            pFlag=processPoint(value,kdTree.root,unassignedList,0,key.length, Eps);
//            if(pFlag==0){
//                MC mc=new MC();
//                mc.setId(list.size());
//                mc.setCenter(key);
//                mc.setPoints(value);
//                mc.setAuxKDTree(value);
//                value.setMCID(mc.getId());
//                kdTree.insertTree(mc.getCenter(),new Point(mc.getCenter()),mc);
//                list.add(mc);
//            }
//        }
//    }
//
//    public int processPoint(Point p, KDNode node, List<Point> unassignedList, int level, int dimentions, double Eps){
//        int pFlag=0;
//        if(node.getKey().getDist(p)<= Eps){
//            node.getMc().setPoints(p);
//            node.getMc().setAuxKDTree(p);
//            p.setMCID(node.getMc().getId());
//            return 1;
//        }
//        if(node.getKey().getDist(p)<= 2*Eps){
//            unassignedList.add(p);
//            return 1;
//        }
//        if(node.getLeft()!=null && p.getValue()[level]<=node.getValue().getValue()[level]){
//            pFlag=processPoint(p,node.getLeft(),unassignedList,(level+1)%dimentions,dimentions,Eps);
//        }
//        if(node.getRight()!=null && p.getValue()[level]>node.getValue().getValue()[level]){
//            pFlag=processPoint(p,node.getRight(),unassignedList,(level+1)%dimentions,dimentions,Eps);
//        }
//
//        return pFlag;
//    }
//
//    public int processUnassignedPoint(Point p, KDNode node, int level, int dimentions, double Eps){
//        int uFlag=0;
//        if(node.getKey().getDist(p)<= Eps){
//            node.getMc().setPoints(p);
//            node.getMc().setAuxKDTree(p);
//            p.setMCID(node.getMc().getId());
//            return 1;
//        }
//        if(node.getLeft()!=null && p.getValue()[level]<=node.getValue().getValue()[level]){
//            uFlag=processUnassignedPoint(p,node.getLeft(),(level+1)%dimentions,dimentions,Eps);
//        }
//        if(node.getRight()!=null && p.getValue()[level]>node.getValue().getValue()[level]){
//            uFlag=processUnassignedPoint(p,node.getRight(),(level+1)%dimentions,dimentions,Eps);
//        }
//        return uFlag;
//    }
//
//    public List<Point> findReachableMCsFromPoint(Point point,KDTree<Point> kdTree,double Eps){
//        List<Point> list=kdTree.reachableMCSearch(point.getValue(),2*Eps);
//        return list;
//    }
//    //获取点及其邻域
//    public List<Point> findNBPoints(Point point,double Eps,int MinPts,KDTree<Point> kdTree){
//        long i=System.currentTimeMillis();
//        List<Point>list=findReachableMCsFromPoint(point,kdTree,Eps);
//        long j=System.currentTimeMillis();
//        System.out.println("邻域点个数为："+list.size());
//        System.out.println("时间消耗为："+(j-i)/1000);
//        return list;
//    }
//    public List<Point>processPoint(List<List<Point>> lists){
//        List<Point>list=new ArrayList<>();
//        for(List<Point> points:lists){
//            for(Point point:points){
//                list.add(point);
//            }
//        }
//        return list;
//    }
//
//}
