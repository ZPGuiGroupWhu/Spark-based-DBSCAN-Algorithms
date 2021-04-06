package org.zzy.dbscan.java.index.Grid;

import org.zzy.dbscan.java.index.balanced_KDTree.DBSCANRectange;
import org.zzy.dbscan.java.index.balanced_KDTree.KDBSCANPoint;
import org.zzy.dbscan.java.index.balanced_KDTree.KDTreeChange;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GridIndexHashAll implements Serializable {
    public DBSCANRectange rectange;//格网的最外围空间
    public double dx;//每个网格的横向增量
    public double dy;//每个网格的纵向增量
    public int xNum;//网格索引的横向网格总数
    public int yNum;//网格索引的纵向网格总数
    public ArrayList<GridIndexHash> gIH;//网格索引数组

    public GridIndexHashAll build(DBSCANRectange rectange,double dx,double dy){
        GridIndexHashAll gridIndexHashAll=new GridIndexHashAll();
        gridIndexHashAll.rectange = rectange;
        gridIndexHashAll.dx = dx;
        gridIndexHashAll.dy = dy;
        gridIndexHashAll.xNum = (int) ((rectange.getX2()-rectange.getX())/dx);//计算所得
        gridIndexHashAll.yNum = (int) ((rectange.getY2()-rectange.getY())/dy);//计算所得
        gridIndexHashAll.gIH = new ArrayList<GridIndexHash>();
        return gridIndexHashAll;
    }

    public List<KDBSCANPoint> getPoints(){
        List<KDBSCANPoint> list=new ArrayList<>();
        for(GridIndexHash hash:gIH){
            if(hash.auxKDtree.kdtree!=null){
                list.addAll(hash.auxKDtree.getNodes());
            }
        }
        return list;
    }

    //范围查询
    public List<KDBSCANPoint> rangeQuery(KDBSCANPoint point,double eps){
        GridIndexHash gridIndexHash=gIH.get(point.getGridIndexID());
        Stack<GridIndexHash> stack=new Stack<>();
        //九邻域
        stack.push(gridIndexHash);
        int x=gridIndexHash.xGrid;
        int y=gridIndexHash.yGrid;
        int x1=x-1 ,y1=y-1;
        int x2=x+1 ,y2=y+1;
        int x3=x-1 ,y3=y+1;
        int x4=x+1 ,y4=y-1;
        int x5=x-1 ,y5=y;
        int x6=x+1 ,y6=y;
        int x7=x ,y7=y-1;
        int x8=x ,y8=y+1;
        if(0<=x1 &&
                x1<xNum &&
                0<=y1 &&
                y1<yNum){
            GridIndexHash gIHPoint = gIH.get(x1*(yNum)+y1);
            if(gIHPoint.auxKDtree.kdtree!=null){
                stack.push(gIHPoint);
            }
        }
        if(0<=x2 &&
                x2<xNum &&
                0<=y2 &&
                y2<yNum){
            GridIndexHash gIHPoint = gIH.get(x2*(yNum)+y2);
            if(gIHPoint.auxKDtree.kdtree!=null){
                stack.push(gIHPoint);
            }
        }
        if(0<=x3 &&
                x3<xNum &&
                0<=y3 &&
                y3<yNum){
            GridIndexHash gIHPoint = gIH.get(x3*(yNum)+y3);
            if(gIHPoint.auxKDtree.kdtree!=null){
                stack.push(gIHPoint);
            }
        }
        if(0<=x4 &&
                x4<xNum &&
                0<=y4 &&
                y4<yNum){
            GridIndexHash gIHPoint = gIH.get(x4*(yNum)+y4);
            if(gIHPoint.auxKDtree.kdtree!=null){
                stack.push(gIHPoint);
            }
        }
        if(0<=x5 &&
                x5<xNum &&
                0<=y5 &&
                y5<yNum){
            GridIndexHash gIHPoint = gIH.get(x5*(yNum)+y5);
            if(gIHPoint.auxKDtree.kdtree!=null){
                stack.push(gIHPoint);
            }
        }
        if(0<=x6 &&
                x6<xNum &&
                0<=y6 &&
                y6<yNum){
            GridIndexHash gIHPoint = gIH.get(x6*(yNum)+y6);
            if(gIHPoint.auxKDtree.kdtree!=null){
                stack.push(gIHPoint);
            }
        }
        if(0<=x7 &&
                x7<xNum &&
                0<=y7 &&
                y7<yNum){
            GridIndexHash gIHPoint = gIH.get(x7*(yNum)+y7);
            if(gIHPoint.auxKDtree.kdtree!=null){
                stack.push(gIHPoint);
            }
        }
        if(0<=x8 &&
                x8<xNum &&
                0<=y8 &&
                y8<yNum){
            GridIndexHash gIHPoint = gIH.get(x8*(yNum)+y8);
            if(gIHPoint.auxKDtree.kdtree!=null){
                stack.push(gIHPoint);
            }
        }
        List<KDBSCANPoint> list=new ArrayList<>();
        while (!stack.isEmpty()){
            GridIndexHash index=stack.pop();
            List<KDBSCANPoint> list1=index.auxKDtree.rangeSearch(point,eps);
            list.addAll(list1);
        }
        return list;
    }

    //范围查询
    public List<KDBSCANPoint> rangeQuery2(KDBSCANPoint point,double eps){
        List<KDBSCANPoint> result=new ArrayList<>();
        GridIndexHash gridIndexHash=gIH.get(point.getGridIndexID());
        List<KDBSCANPoint> list=gridIndexHash.auxKDtree.rangeSearch(point,eps);
        result.addAll(list);
        //八邻域
        int x=gridIndexHash.xGrid;
        int y=gridIndexHash.yGrid;
        int x1=x-1 ,y1=y-1;
        int x2=x+1 ,y2=y+1;
        int x3=x-1 ,y3=y+1;
        int x4=x+1 ,y4=y-1;
        int x5=x-1 ,y5=y;
        int x6=x+1 ,y6=y;
        int x7=x ,y7=y-1;
        int x8=x ,y8=y+1;
        if(0<=x1 &&
                x1<xNum &&
                0<=y1 &&
                y1<yNum){
            GridIndexHash gIHPoint = gIH.get(x1*(yNum)+y1);
            if(gIHPoint.auxKDtree.kdtree!=null){
                List<KDBSCANPoint> list1=gIHPoint.auxKDtree.rangeSearch(point,eps);
                result.addAll(list1);
            }
        }
        if(0<=x2 &&
                x2<xNum &&
                0<=y2 &&
                y2<yNum){
            GridIndexHash gIHPoint = gIH.get(x2*(yNum)+y2);
            if(gIHPoint.auxKDtree.kdtree!=null){
                List<KDBSCANPoint> list1=gIHPoint.auxKDtree.rangeSearch(point,eps);
                result.addAll(list1);
            }
        }
        if(0<=x3 &&
                x3<xNum &&
                0<=y3 &&
                y3<yNum){
            GridIndexHash gIHPoint = gIH.get(x3*(yNum)+y3);
            if(gIHPoint.auxKDtree.kdtree!=null){
                List<KDBSCANPoint> list1=gIHPoint.auxKDtree.rangeSearch(point,eps);
                result.addAll(list1);
            }
        }
        if(0<=x4 &&
                x4<xNum &&
                0<=y4 &&
                y4<yNum){
            GridIndexHash gIHPoint = gIH.get(x4*(yNum)+y4);
            if(gIHPoint.auxKDtree.kdtree!=null){
                List<KDBSCANPoint> list1=gIHPoint.auxKDtree.rangeSearch(point,eps);
                result.addAll(list1);
            }
        }
        if(0<=x5 &&
                x5<xNum &&
                0<=y5 &&
                y5<yNum){
            GridIndexHash gIHPoint = gIH.get(x5*(yNum)+y5);
            if(gIHPoint.auxKDtree.kdtree!=null){
                List<KDBSCANPoint> list1=gIHPoint.auxKDtree.rangeSearch(point,eps);
                result.addAll(list1);
            }
        }
        if(0<=x6 &&
                x6<xNum &&
                0<=y6 &&
                y6<yNum){
            GridIndexHash gIHPoint = gIH.get(x6*(yNum)+y6);
            if(gIHPoint.auxKDtree.kdtree!=null){
                List<KDBSCANPoint> list1=gIHPoint.auxKDtree.rangeSearch(point,eps);
                result.addAll(list1);
            }
        }
        if(0<=x7 &&
                x7<xNum &&
                0<=y7 &&
                y7<yNum){
            GridIndexHash gIHPoint = gIH.get(x7*(yNum)+y7);
            if(gIHPoint.auxKDtree.kdtree!=null){
                List<KDBSCANPoint> list1=gIHPoint.auxKDtree.rangeSearch(point,eps);
                result.addAll(list1);
            }
        }
        if(0<=x8 &&
                x8<xNum &&
                0<=y8 &&
                y8<yNum){
            GridIndexHash gIHPoint = gIH.get(x8*(yNum)+y8);
            if(gIHPoint.auxKDtree.kdtree!=null){
                List<KDBSCANPoint> list1=gIHPoint.auxKDtree.rangeSearch(point,eps);
                result.addAll(list1);
            }
        }

        return result;
    }
}
