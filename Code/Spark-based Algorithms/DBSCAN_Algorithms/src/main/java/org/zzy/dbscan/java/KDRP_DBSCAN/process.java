package org.zzy.dbscan.java.KDRP_DBSCAN;

import org.zzy.dbscan.java.model.KDNode;
import org.zzy.dbscan.java.model.KDTree;
import org.zzy.dbscan.java.model.MC;
import org.zzy.dbscan.java.model.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class process implements Serializable {
//    public List<MC> getMCs(List<Point>points,double Eps, KDTree<Point> kdTree,List<MC> MCList){
//        buildMicroClusters(points,Eps,kdTree,MCList);
//        return MCList;
//    }


    //KD树结构、MCList由外面传进来
    //构建微类簇
    public List<MC> buildMicroClusters(List<Point>points,double Eps, KDTree<Point> kdTree){
        List<MC> MCList=new ArrayList<>();
        List<Point> unassignedList=new ArrayList<>();//第一轮未标记的点
        int uFlag;
        for(int i=0;i<points.size();i++){
            buildMCs(points.get(i).getValue(),points.get(i),MCList,unassignedList, Eps,kdTree);
        }
        for(int j=0;j<unassignedList.size();j++){
            uFlag=processUnassignedPoint(unassignedList.get(j),kdTree.root,0,unassignedList.get(j).getValue().length,Eps);
            if(uFlag==0){
                MC mc=new MC();
                mc.setId(MCList.size());
                mc.setCenter(unassignedList.get(j).getValue());
                mc.setInPoints(unassignedList.get(j),Eps);
                mc.setOutPoints(unassignedList.get(j),Eps);
                unassignedList.get(j).setMCID(mc.getId());
                kdTree.insertTree(mc.getCenter(),new Point(mc.getCenter()),mc);
                MCList.add(mc.getId(),mc);
            }
        }
        return MCList;
    }
    //构建第二层KD树
    public void buildAuxKDTree(MC mc){
        KDTree<Point> kdTree1=new KDTree<>(2);
        for(Point inPoint:mc.getInPoints()){
            kdTree1.insertTree(inPoint.getValue(),inPoint,null);
        }
        for(Point outPoint:mc.getOutPoints()){
            kdTree1.insertTree(outPoint.getValue(),outPoint,null);
        }
        mc.setAuxKDTree(kdTree1);
    }

    public void buildMCs(double[]key,Point value,List<MC> list,List<Point> unassignedList,double Eps,KDTree<Point> kdTree){
        int pFlag;
        if(kdTree.root==null){
            MC mc=new MC();
            mc.setId(list.size());//编号是MC列表长度
            mc.setCenter(key);
            mc.setInPoints(value,Eps);
            mc.setOutPoints(value,Eps);
            value.setMCID(mc.getId());
            kdTree.insertTree(mc.getCenter(),new Point(mc.getCenter()),mc);
            list.add(mc);
        }else {
            pFlag=processPoint(value,kdTree.root,unassignedList,0,key.length, Eps);
            if(pFlag==0){
                MC mc=new MC();
                mc.setId(list.size());
                mc.setCenter(key);
                mc.setInPoints(value,Eps);
                mc.setOutPoints(value,Eps);
                value.setMCID(mc.getId());
                kdTree.insertTree(mc.getCenter(),new Point(mc.getCenter()),mc);
                list.add(mc);
            }
        }
    }

    public int processPoint(Point p, KDNode node, List<Point> unassignedList, int level, int dimentions, double Eps){
        int pFlag=0;
        if(node.getKey().getDist(p)<= Eps){
            node.getMc().setInPoints(p,Eps);
            node.getMc().setOutPoints(p,Eps);
            p.setMCID(node.getMc().getId());
            return 1;
        }
        if(node.getKey().getDist(p)<= 2*Eps){
            unassignedList.add(p);
            return 1;
        }
        if(node.getLeft()!=null && p.getValue()[level]<=node.getValue().getValue()[level]){
            pFlag=processPoint(p,node.getLeft(),unassignedList,(level+1)%dimentions,dimentions,Eps);
        }
        if(node.getRight()!=null && p.getValue()[level]>node.getValue().getValue()[level]){
            pFlag=processPoint(p,node.getRight(),unassignedList,(level+1)%dimentions,dimentions,Eps);
        }

        return pFlag;
    }

    public int processUnassignedPoint(Point p, KDNode node, int level, int dimentions, double Eps){
        int uFlag=0;
        if(node.getKey().getDist(p)<= Eps){
            node.getMc().setInPoints(p,Eps);
            node.getMc().setOutPoints(p,Eps);
            p.setMCID(node.getMc().getId());
            return 1;
        }
        if(node.getLeft()!=null && p.getValue()[level]<=node.getValue().getValue()[level]){
            uFlag=processUnassignedPoint(p,node.getLeft(),(level+1)%dimentions,dimentions,Eps);
        }
        if(node.getRight()!=null && p.getValue()[level]>node.getValue().getValue()[level]){
            uFlag=processUnassignedPoint(p,node.getRight(),(level+1)%dimentions,dimentions,Eps);
        }
        return uFlag;
    }
    //对微类簇中可以直接标记为wndq的核心点进行标记
    public void processMicroClusters(MC mc,double Eps,int MinPts){
        if(mc.getInPoints().size()>=MinPts){
            for(Point point:mc.getInPoints()){//将Z.IC中的每个点设置为核心点
                point.setFlag(Point.Flag.Core);
            }
        }else if((mc.getInPoints().size()+mc.getOutPoints().size())>=MinPts){
            for(Point point:mc.getOutPoints()){
                if(point.getValue()==mc.getCenter()){
                    point.setFlag(Point.Flag.Core);
                }
            }
            for(Point point:mc.getInPoints()){
                if(point.getValue()==mc.getCenter()){
                    point.setFlag(Point.Flag.Core);
                }
            }
        }
    }
    //点的合并操作
//    public void Union(Point center,Point point){
//        point.setCluster(center.getCluster());
//    }
    //发现可达的MCs的IDs
    public List<Integer> findReachableMC(MC mc,KDTree<Point> kdTree,double Eps){
        List<Integer> list=kdTree.reachableMCSearch(mc.getCenter(),3*Eps);
        return list;
    }
    //处理未被标记为core的点。
    public void processRemPoints(MC mc,KDTree<Point> kdTree,double Eps,int MinPts,List<MC>MCList){
            for(int j=0;j<mc.getInPoints().size();j++){//获取内部点
                if(!mc.getInPoints().get(j).getFlag().equals(Point.Flag.Core)){
                    List<Point>list=new ArrayList<>();//邻域点列表
                    findNBHD(mc,kdTree,mc.getInPoints().get(j),Eps,MinPts,list,MCList);
                    if(list.size()>=MinPts) {
                        mc.getInPoints().get(j).setFlag(Point.Flag.Core);
                    }
                }
            }
            for(int k=0;k<mc.getOutPoints().size();k++){//获取外部点
                if(!mc.getOutPoints().get(k).getFlag().equals(Point.Flag.Core)){
                    List<Point>list=new ArrayList<>();
                    findNBHD(mc,kdTree,mc.getOutPoints().get(k),Eps,MinPts,list,MCList);
                    if(list.size()>=MinPts) {
                        mc.getOutPoints().get(k).setFlag(Point.Flag.Core);
                    }
                }
            }

    }
    public void findNBHD(MC mc,KDTree<Point> kdTree,Point point,double Eps,int MinPts,List<Point>list,List<MC> MCList){
        List<Integer> reachabledMCs=findReachableMC(mc,kdTree,Eps);//直接使用可达MC做邻域查询,不按照论文中的做进一步处理了
        List<Point>listIC=new ArrayList<>();//内 Eps/2的邻域点
        List<Point>listOC=new ArrayList<>();//除了内部以外的邻域点
        for(Integer mc1:reachabledMCs){
            List<Point>list1=MCList.get(mc1).getAuxKDTree().rangeSearch(point.getValue(),Eps);//通过ID获取MC
            for(Point point1:list1){
                list.add(point1);
                if(point1.getDist(point)<0.5*Eps){
                    listIC.add(point1);
                }else
                listOC.add(point1);
            }
        }
        if(listIC.size()>MinPts){
            for(Point q:listIC){
                q.setFlag(Point.Flag.Core);
            }
        }
    }
    public List<Point> findNBPoints(Point point,double Eps,int MinPts,KDTree<Point> kdTree,List<MC> MCList){
        List<Point>list=new ArrayList<>();
        List<Integer> reachabledMCs=findReachableMC(MCList.get(point.getMCID()),kdTree,Eps);//直接使用可达MC做邻域查询,不按照论文中的做进一步处理了
        for(Integer mc1:reachabledMCs){
            List<Point>list1=MCList.get(mc1).getAuxKDTree().rangeSearch(point.getValue(),Eps);//通过ID获取MC
            for(Point point1:list1){
                list.add(point1);
            }
        }
        return list;
    }
    public List<Point>processPoint(List<List<Point>> lists){
        List<Point>list=new ArrayList<>();
        for(List<Point> points:lists){
            for(Point point:points){
                list.add(point);
            }
        }
        return list;
    }

}
