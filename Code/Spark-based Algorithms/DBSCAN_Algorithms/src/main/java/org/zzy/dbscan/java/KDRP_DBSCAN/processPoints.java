package org.zzy.dbscan.java.KDRP_DBSCAN;

import org.zzy.dbscan.java.model.KDNode;
import org.zzy.dbscan.java.model.KDTree;
import org.zzy.dbscan.java.model.MC;
import org.zzy.dbscan.java.model.Point;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class processPoints implements Serializable {
    List<Point>wndqCorelist=new ArrayList<>();
    List<Point>noiseList=new ArrayList<>();
    List<MC> MCList=new ArrayList<>();
    int clusterID=1;
    public List<Point> kdrpDBSCAN(List<Point> points,double eps,int minpoints,int numOfPartition,KDTree<Point> mukdtree){
        buildMicroClusters(points,eps,mukdtree);
        for(MC mc:MCList){
            buildAuxKDTree(mc);
        }
        for(MC mc:MCList){
            processMicroClusters(mc,eps,minpoints);
//            List<MC> reachList=findReachableMC(mc,mukdtree,eps);
//            mc.setReachList(reachList);
        }
        processRemPoints(MCList,mukdtree,eps,minpoints);
//        UnionFindPoints unionFindPoints=new UnionFindPoints(points);
        postProcessCore(mukdtree,eps);
        postProcessNoise(eps);
        return points;
    }



    //KD树结构由外面传进来
    //构建微类簇
    public void buildMicroClusters(List<Point>points,double Eps, KDTree<Point> kdTree){
        List<Point> unassignedList=new ArrayList<>();//第一轮未标记的点
        int uFlag;
        for(int i=0;i<points.size();i++){
            buildMCs(points.get(i).getValue(),points.get(i),MCList,unassignedList, Eps,kdTree);
        }
//        points.removeAll(points);//处理完成就删除，节省空间，以后所有的操作都处理MClist中的点
//        for(Point point:points){
//            buildMCs(point.getValue(),point,MCList,unassignedList, Eps,kdTree);
//        }
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
//        unassignedList.removeAll(unassignedList);
//        for(Point point:unassignedList){
//            uFlag=processUnassignedPoint(point,kdTree.root,0,point.getValue().length,Eps);
//            if(uFlag==0){
//                MC mc=new MC();
//                mc.setId(MCList.size());
//                mc.setCenter(point.getValue());
//                mc.setPoints(point);
////                point.setMCID(mc.getId());
//                kdTree.insertTree(mc.getCenter(),new Point(mc.getCenter()),mc);
//                MCList.add(mc.getId(),mc);
//            }
//        }
//        //构建第二层R树
//        for(MC mc:MCList){
//            KDTree<Point> kdTree1=new KDTree<>(2);
//            for(Point point:mc.getPoints()){
//                kdTree1.insertTree(point.getValue(),point,null);
//            }
//            mc.setAuxKDTree(kdTree1);
//        }
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
//        if(node.getLeft()==null && node.getRight()==null){
//            if(node.getKey().getDist(p)<= Eps){
//                node.getMc().setPoints(p);
//                return 1;
//            }
//            if(node.getKey().getDist(p)<= 2*Eps){
//                unassignedList.add(p);
//                return 1;
//            }
//        }else if(node.getLeft()==null && p.getValue()[level]<=node.getValue().getValue()[level]){
//            if(node.getKey().getDist(p)<= Eps){
//                node.getMc().setPoints(p);
//                return 1;
//            }
//            if(node.getKey().getDist(p)<= 2*Eps){
//                unassignedList.add(p);
//                return 1;
//            }
//        }else if(node.getRight()==null && p.getValue()[level]>node.getValue().getValue()[level]){
//            if(node.getKey().getDist(p)<= Eps){
//                node.getMc().setPoints(p);
//                return 1;
//            }
//            if(node.getKey().getDist(p)<= 2*Eps){
//                unassignedList.add(p);
//                return 1;
//            }
//        }else {
//            if(p.getValue()[level]>node.getValue().getValue()[level]){
//                pFlag=processPoint(p,node.getRight(),unassignedList,(level+1)%dimentions,dimentions,Eps);
//            }else {
//                pFlag=processPoint(p,node.getLeft(),unassignedList,(level+1)%dimentions,dimentions,Eps);
//            }
//        }
        if(node.getKey().getDist(p)<= Eps){
            node.getMc().setInPoints(p,Eps);
            node.getMc().setOutPoints(p,Eps);
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
//        if(node.getLeft()==null && node.getRight()==null){
//            if(node.getKey().getDist(p)<= Eps){
//                node.getMc().setPoints(p);
//                return 1;
//            }
//        }else if(node.getLeft()==null && p.getValue()[level]<=node.getValue().getValue()[level]){
//            if(node.getKey().getDist(p)<= Eps){
//                node.getMc().setPoints(p);
//                return 1;
//            }
//        }else if(node.getRight()==null && p.getValue()[level]>node.getValue().getValue()[level]){
//            if(node.getKey().getDist(p)<= Eps){
//                node.getMc().setPoints(p);
//                return 1;
//            }
//        }else {
//            if(p.getValue()[level]>node.getValue().getValue()[level]){
//                uFlag=processUnassignedPoint(p,node.getRight(),(level+1)%dimentions,dimentions,Eps);
//            }else {
//                uFlag=processUnassignedPoint(p,node.getLeft(),(level+1)%dimentions,dimentions,Eps);
//            }
//        }
        if(node.getKey().getDist(p)<= Eps){
            node.getMc().setInPoints(p,Eps);
            node.getMc().setOutPoints(p,Eps);
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
//        mc.setIC(Eps);
        if(mc.getInPoints().size()>MinPts){
            for(Point point:mc.getInPoints()){//将Z.IC中的每个点设置为核心点
                point.setFlag(Point.Flag.Core);
                wndqCorelist.add(point);
            }
            //将Z中的每个点设置为与Z.center相同的类簇，由于之前分成IC和IC以外，所以分两步执行
            Point center=new Point(mc.getCenter());
            center.setCluster(clusterID++);
            for(Point point:mc.getInPoints()){
                Union(center,point);
            }
            for(Point point:mc.getOutPoints()){
                Union(center,point);
            }
        }else if((mc.getInPoints().size()+mc.getOutPoints().size())>MinPts){
            Point center=new Point(mc.getCenter());
            center.setCluster(clusterID++);
            for(Point point:mc.getOutPoints()){
                if(point.getValue()==mc.getCenter()){
                    point.setFlag(Point.Flag.Core);
                }
                Union(center,point);
            }
            for(Point point:mc.getInPoints()){
                if(point.getValue()==mc.getCenter()){
                    point.setFlag(Point.Flag.Core);
                }
                Union(center,point);
            }
        }
    }
    //点的合并操作
    public void Union(Point center,Point point){
        point.setCluster(center.getCluster());
    }
    //发现可达的MCs的IDs
    public List<Integer> findReachableMC(MC mc,KDTree<Point> kdTree,double Eps){
        List<Integer> list=kdTree.reachableMCSearch(mc.getCenter(),3*Eps);
        return list;
    }
    //处理未被标记为core的点。因为前面标记的时候是处理的MC中的点，所以这里依旧传入mcList
    public void processRemPoints(List<MC>mcList,KDTree<Point> kdTree,double Eps,int MinPts){
        for(int i=0;i<mcList.size();i++){
            MC mc=mcList.get(i);//获取微类簇
            for(int j=0;j<mc.getInPoints().size();j++){//获取内部点
                if(!mc.getInPoints().get(j).getFlag().equals(Point.Flag.Core)){
                    List<Point>list=new ArrayList<>();//邻域点列表
                    findNBHD(mc,kdTree,mc.getInPoints().get(j),Eps,MinPts,list);
                    if(list.size()<MinPts){
                        for(Point point:list){
                            if(point.getFlag().equals(Point.Flag.Core)){
                                Union(point,mc.getInPoints().get(j));break;
                            }
                        }
                        if(mc.getInPoints().get(j).getCluster()==0){
                            noiseList.add(mc.getInPoints().get(j));
                        }
                    }else {
                        mc.getInPoints().get(j).setFlag(Point.Flag.Core);
                        for(Point point:list){
                            if(point.getFlag().equals(Point.Flag.Core))Union(mc.getInPoints().get(j),point);
                            else if(point.getCluster()==0)Union(mc.getInPoints().get(j),point);
                        }
                    }
                }
            }
            for(int k=0;k<mc.getOutPoints().size();k++){//获取外部点
                if(!mc.getOutPoints().get(k).getFlag().equals(Point.Flag.Core)){
                    List<Point>list=new ArrayList<>();
                    findNBHD(mc,kdTree,mc.getOutPoints().get(k),Eps,MinPts,list);
                    if(list.size()<MinPts){
                        for(Point point:list){
                            if(point.getFlag().equals(Point.Flag.Core)){
                                Union(point,mc.getOutPoints().get(k));break;
                            }
                        }
                        if(mc.getOutPoints().get(k).getCluster()==0){
                            noiseList.add(mc.getOutPoints().get(k));
                        }
                    }else {
                        mc.getOutPoints().get(k).setFlag(Point.Flag.Core);
                        for(Point point:list){
                            if(point.getFlag().equals(Point.Flag.Core))Union(mc.getOutPoints().get(k),point);
                            else if(point.getCluster()==0)Union(mc.getOutPoints().get(k),point);
                        }
                    }
                }
            }

        }
    }
    public void findNBHD(MC mc,KDTree<Point> kdTree,Point point,double Eps,int MinPts,List<Point>list){
//        List<MC> reachabledMCs=mc.getReachList();//直接使用可达MC做邻域查询,不按照论文中的做进一步处理了
        List<Integer> reachabledMCs=findReachableMC(mc,kdTree,Eps);//直接使用可达MC做邻域查询,不按照论文中的做进一步处理了
        List<Point>listIC=new ArrayList<>();//内 Eps/2的邻域点
        List<Point>listOC=new ArrayList<>();//除了内部以外的邻域点
        for(Integer mc1:reachabledMCs){
//            List<Point>list1= mc1.getAuxKDTree().rangeSearch(point.getValue(),Eps);
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
                wndqCorelist.add(q);
                Union(point,q);
            }
        }
        //简化到上面了
//        for(Point pointIC:listIC){
//            list.add(pointIC);
//        }
//        for(Point pointOC:listOC){
//            list.add(pointOC);
//        }
    }
    public void postProcessCore(KDTree<Point>kdTree,double Eps){
        for(Point point:wndqCorelist){
            MC mc=MCList.get(point.getMCID());
//            MC mc=getMCfromPoint(point,kdTree.root,point.getValue().length,Eps);
            List<Integer> reachabledMC=kdTree.reachableMCSearch(mc.getCenter(),3*Eps);
            for(Integer mc1:reachabledMC){
                for(Point point1:MCList.get(mc1).getInPoints()){
                    if(point1.getFlag().equals(Point.Flag.Core) && point.getCluster()!=point1.getCluster() && point.getDist(point1)<=Eps)Union(point1,point);
                }
                for(Point point2:MCList.get(mc1).getOutPoints()){
                    if(point2.getFlag().equals(Point.Flag.Core) && point.getCluster()!=point2.getCluster() && point.getDist(point2)<=Eps)Union(point2,point);
                }
            }
        }
    }
    public MC getMCfromPoint(Point key,KDNode node,int dimension,double Eps){
        for(int level=0;node!=null;level=(level+1)%dimension){
            if(key.equals(node.getKey()) || key.getDist(node.getKey())<=Eps){
                return node.getMc();
            }else if(key.getValue()[level]>node.getValue().getValue()[level]){
                node=node.getRight();
            }else {
                node=node.getLeft();
            }
        }
        return null;
    }
    public void postProcessNoise(double Eps){
        for(Point noise:noiseList){
            for(int i=0;i<wndqCorelist.size();i++){
                Point core=wndqCorelist.get(i);
                if(noise.getDist(core)<=Eps){
                    Union(core,noise);
                    noiseList.remove(i);
                    break;
                }
            }
        }
    }

}
