package org.zzy.dbscan.java.KDRP_DBSCAN;

public class UnionFind {
    private int[] id;//存储一组数字  下标代表第i个数  值代表所属的类簇
    private int count;//类簇个数
    private int[] sz;//每个类簇内点的个数

    public UnionFind(int N) {
        count = N;
        id = new int[N];
        sz = new int[N];
        for(int i = 0; i < N; i++) {
            id[i] = i;
            sz[i] = 1;
        }
    }

    public int getCount() {
        return count;
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    public int find(int p) {
        if (p != id[p]){
            id[p] = find(id[p]);
//            union(p,id[p]);
        }
        return id[p];
    }

    public void union(int p, int q){
        int pRoot = find(p);
        int qRoot = find(q);

        if(pRoot == qRoot) return;

        if(sz[pRoot] < sz[qRoot]) { id[pRoot] = qRoot; sz[qRoot] += sz[pRoot]; }
        else                      { id[qRoot] = pRoot; sz[pRoot] += sz[qRoot]; }
        count--;
    }
    public static void print(int[] arr){
        for(int i:arr){
            System.out.print(i+" ");
        }
    }

    public static void main(String[] args){
        UnionFind unionFind=new UnionFind(5);
        System.out.println();
        print(unionFind.id);
        unionFind.union(1,2);
        System.out.println();
        print(unionFind.id);
        unionFind.union(0,4);
        System.out.println();
        print(unionFind.id);
        unionFind.union(3,2);
        System.out.println();
        print(unionFind.id);
        unionFind.union(3,4);
        System.out.println();
        print(unionFind.id);

    }



}

