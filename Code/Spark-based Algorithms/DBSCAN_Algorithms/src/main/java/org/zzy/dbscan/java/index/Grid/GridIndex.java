package org.zzy.dbscan.java.index.Grid;

import org.zzy.dbscan.java.index.balanced_KDTree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GridIndex {
    //放入的时候就建索引
    public void creatPointGridIndex(List<KDBSCANPoint> list,GridIndexHashAll gIHAll){
        for(int x=0;x<gIHAll.xNum;x++){
            for(int y=0;y<gIHAll.yNum;y++){
                gIHAll.gIH.add(new GridIndexHash(x,y));
            }
        }
        for(KDBSCANPoint point:list){
            int xP = (int)((point.getValue()[0]-gIHAll.rectange.getX())/(gIHAll.dx));
            int yP = (int)((point.getValue()[1]-gIHAll.rectange.getY())/(gIHAll.dy));
            GridIndexHash gIHPoint = gIHAll.gIH.get(xP*(gIHAll.yNum)+yP);//获取点对应的网格索引对象
            point.setGridIndexID(xP*(gIHAll.yNum)+yP);
            DBSCANRectange rectange=new DBSCANRectange(gIHPoint.xGrid*gIHAll.dx+gIHAll.rectange.getX(),
                    gIHPoint.yGrid*gIHAll.dy+gIHAll.rectange.getY(),
                    gIHPoint.xGrid*gIHAll.dx+gIHAll.rectange.getX()+gIHAll.dx,
                    gIHPoint.yGrid*gIHAll.dy+gIHAll.rectange.getY()+gIHAll.dy);
                gIHPoint.auxKDtree.kdtree= Node.insert(point,gIHPoint.auxKDtree.kdtree,0,rectange);
        }
    }
}
