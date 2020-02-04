/* Kruskal.java */

package graphalg;

import graph.*;
import set.*;
import dict.*;

/**
 * The Kruskal class contains the method minSpanTree(), which implements
 * Kruskal's algorithm for computing a minimum spanning tree of a graph.
 */

public class Kruskal {

	private static void quickSort(Edge[] e, int l, int r) {
		if (l >= r) {
			return;
		}
		int num = (int) (l + Math.random() * (r - l + 1));
		Edge pivot = e[num];
		e[num] = e[r];
		e[r] = pivot;
		int i = l - 1;
		int j = r;
		do {
			do {
				i++;
			} while ((e[i].compareTo(pivot) < 0));
			do {
				j--;
			} while ((e[j].compareTo(pivot) > 0) && (j > l));
			if (i < j) {
				Edge temp = e[i];
				e[i] = e[j];
				e[j] = temp;
			}
		} while (i < j);
		e[r] = e[i];
		e[i] = pivot;
		quickSort(e, l, i - 1);
		quickSort(e, i + 1, r);
	}

  /**
   * minSpanTree() returns a WUGraph that represents the minimum spanning tree
   * of the WUGraph g.  The original WUGraph g is NOT changed.
   *
   * @param g The weighted, undirected graph whose MST we want to compute.
   * @return A newly constructed WUGraph representing the MST of g.
   */
  public static WUGraph minSpanTree(WUGraph g) {
  	WUGraph minST = new WUGraph();
  	Object[] v = g.getVertices();
  	HashTableChained vertex = new HashTableChained();

  	for (int i = 0; i < v.length; i++) {
  		minST.addVertex(v[i]);
  		vertex.insert(v[i], new Integer(i));
  	}

  	Edge[] edges = new Edge[g.edgeCount()];
  	int num = 0;
  	for (Object o: v) {
  		Neighbors n = g.getNeighbors(o);
  		int x = ((Integer) (vertex.find(o).value())).intValue();
  		for (int i = 0; i < n.neighborList.length; i++) {
  			int y = ((Integer) (vertex.find(n.neighborList[i]).value())).intValue();
  			if (x < y) {
  				edges[num] = new Edge(o, n.neighborList[i], n.weightList[i]);
  				num++;
  			}
  		}
  	}

  	num--;
  	quickSort(edges, 0, num);
  	DisjointSets set = new DisjointSets(v.length);

  	for (Edge e : edges) {
  		int x = ((Integer) (vertex.find(e.vertex1).value())).intValue();
  		int y = ((Integer) (vertex.find(e.vertex2).value())).intValue();
  		int rootX = set.find(x);
  		int rootY = set.find(y);
  		if (rootX != rootY) {
  			minST.addEdge(e.vertex1, e.vertex2, e.weight);
  			set.union(rootX, rootY);
  			if (minST.edgeCount() == v.length - 1) {
  				break;
  			}
  		}
  	}

  	return minST;
  }

}
