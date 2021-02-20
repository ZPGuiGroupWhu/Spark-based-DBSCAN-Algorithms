package org.zzy.dbscan.java.KDRP_DBSCAN;

import org.zzy.dbscan.java.model.Point;

import java.util.List;

public class UnionFindPoints {
    private int[] id;//存储一组数字  下标代表第i个数  值代表所属的类簇
    private int count;//类簇个数
    private int[] sz;//每个类簇内点的个数

    public UnionFindPoints(List<Point>points) {
        count = points.size();
        id = new int[points.size()];
        sz = new int[points.size()];
        for(Point point:points) {
            id[point.getId()-1] = point.getId();
            point.setCluster(point.getId());//每个点的初始类簇ID设置为其自身点的ID
            sz[point.getId()-1] = 1;
        }
    }

    public int getCount() {
        return count;
    }

    public boolean connected(Point p, Point q) {
        return find(p.getId()-1) == find(q.getId()-1);
    }
    //查看类簇编号
    public int find(int p) {
        if (p != id[p]) id[p] = find(id[p]);
        return id[p];
    }

    public void unionPoints(Point p, Point q){
        int pRoot = find(p.getId()-1);//类簇编号
        int qRoot = find(q.getId()-1);//类簇编号

        if(pRoot == qRoot) return;//类簇编号相同直接返回

        if(sz[pRoot] < sz[qRoot]) {
            id[pRoot] = qRoot;//p合并给q
            p.setCluster(q.getCluster());//p的类簇ID变成q的
            sz[qRoot] += sz[pRoot];
        }
        else{
            id[qRoot] = pRoot;//q合并给p
            q.setCluster(p.getCluster());
            sz[pRoot] += sz[qRoot];
        }
        count--;
    }
}

