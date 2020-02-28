package org.zzy.dbscan.java.index.classical_KDTree;

import java.io.Serializable;
import java.util.Vector;

// K-D Tree node 
class KDNode implements Serializable {

	/**
	 * 
	 */
//	private static final long serialVersionUID = 1L;
	protected Point key;  //存储的每个节点的key，是一个k维数组
	protected KDNode left, right;//左右节点
	protected boolean deleted;//是否被删除
	Point value;

	// default constructor 默认构造方法
	private KDNode(Point key, Point val) {

		this.key = key;
		value = val;
		left = null;
		right = null;
		deleted = false;
	}

	// insert Node
	protected static KDNode insertNode(Point key, Point val, KDNode node, int level, int dimension) {
/**
 * 插入操作
 * 这里的 level 就是算法论文里面描述的 discriminator，根节点为第 0 层，下面一次是 1,2，...，k-1，再从0循环
 */
		if (node == null) {//如果为空则新建节点
			node = new KDNode(key, val);
		} else if (key.equals(node.key)) {//如果插入的点与原始点相同
			// "re-insert"
			if (node.deleted) {//如果原始节点被删除
				node.deleted = false;
				node.value = val;
			}

		} else if (key.coord[level] > node.value.coord[level]) {
			node.right = insertNode(key, val, node.right, (level + 1) % dimension, dimension);
		} else {
			node.left = insertNode(key, val, node.left, (level + 1) % dimension, dimension);
		}

		return node;
	}


	
	// search Node
	protected static KDNode searchNode(Point key, KDNode node, int dimension) {

		for (int lev = 0; node != null; lev = (lev + 1) % dimension) {

			if (!node.deleted && key.equals(node.key)) {
				return node;
			} else if (key.coord[lev] > node.value.coord[lev]) {
				node = node.right;
			} else {
				node = node.left;
			}
		}

		return null;
	}

	// range Search
	protected static void rangeSearch(Point lowKey, Point upKey, KDNode node, int divide, int dimensions,
			double[] key,double Eps,Vector<KDNode> nodeVector) {

		if (node == null) {
			return;
		}
		if (node.value.coord[divide] >= lowKey.coord[divide]) {
			rangeSearch(lowKey, upKey, node.left, (divide + 1) % dimensions, dimensions,key,Eps, nodeVector);
		}

		if (node.value.coord[divide] < upKey.coord[divide]) {
			rangeSearch(lowKey, upKey, node.right, (divide + 1) % dimensions, dimensions, key,Eps,nodeVector);
		}
		
		int j;
		for (j = 0; j < dimensions && node.value.coord[j] >= lowKey.coord[j]
				&& node.value.coord[j] <= upKey.coord[j]; j++)
			;
		
		if((j == dimensions)&&(node.value.getDist(new Point(key))<Eps)){
			nodeVector.add(node);
		}
//		nodeVector.add(node);
		
	}

}


