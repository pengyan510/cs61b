/* WUGraph.java */

package graph;

import list.*;
import dict.*;

/**
 * The WUGraph class represents a weighted, undirected graph.  Self-edges are
 * permitted.
 */

public class WUGraph {

	private HashTableChained vertexTable, edgeTable;
	private DList vertexList;
	private int vertexCnt, edgeCnt;

  /**
   * WUGraph() constructs a graph having no vertices or edges.
   *
   * Running time:  O(1).
   */
  public WUGraph() {
  	vertexTable = new HashTableChained();
  	edgeTable = new HashTableChained();
  	vertexList = new DList();
  }

  /**
   * vertexCount() returns the number of vertices in the graph.
   *
   * Running time:  O(1).
   */
  public int vertexCount() {
  	return vertexCnt;
  }

  /**
   * edgeCount() returns the total number of edges in the graph.
   *
   * Running time:  O(1).
   */
  public int edgeCount() {
  	return edgeCnt;
  }

  /**
   * getVertices() returns an array containing all the objects that serve
   * as vertices of the graph.  The array's length is exactly equal to the
   * number of vertices.  If the graph has no vertices, the array has length
   * zero.
   *
   * (NOTE:  Do not return any internal data structure you use to represent
   * vertices!  Return only the same objects that were provided by the
   * calling application in calls to addVertex().)
   *
   * Running time:  O(|V|).
   */
  public Object[] getVertices() {
  	Object[] vertices = new Object[vertexCnt];
  	ListNode node = vertexList.front();
  	int i = 0;
  	while (node.isValidNode()) {
  		try {
  			vertices[i] = ((VertexNode) (node.item())).vertex;
  			i++;
  			node = node.next();
  		} catch (InvalidNodeException e) {
  			System.err.println(e);
  		}
  	}

  	return vertices;
  }

  /**
   * addVertex() adds a vertex (with no incident edges) to the graph.
   * The vertex's "name" is the object provided as the parameter "vertex".
   * If this object is already a vertex of the graph, the graph is unchanged.
   *
   * Running time:  O(1).
   */
  public void addVertex(Object vertex) {
  	if (vertexTable.find(vertex) != null) {
  		return;
  	}

  	vertexCnt++;
  	VertexNode v = new VertexNode(vertex);
  	vertexList.insertBack(v);
  	vertexTable.insert(vertex, vertexList.back());
  }

  /**
   * removeVertex() removes a vertex from the graph.  All edges incident on the
   * deleted vertex are removed as well.  If the parameter "vertex" does not
   * represent a vertex of the graph, the graph is unchanged.
   *
   * Running time:  O(d), where d is the degree of "vertex".
   */
  public void removeVertex(Object vertex) {
  	if (vertexTable.find(vertex) == null) {
  		return;
  	}

  	vertexCnt--;
  	ListNode node = (ListNode) (vertexTable.remove(vertex).value());
  	ListNode adjNode = null;
  	try {
  		adjNode = ((VertexNode) (node.item())).adjList.front();
  	} catch (InvalidNodeException e) {
  		System.err.println(e);
  	}
  	while (adjNode.isValidNode()) {
  		try {
  			AdjListNode adjItem = (AdjListNode) (adjNode.item());
  			edgeTable.remove(new VertexPair(adjItem.vertex1, adjItem.vertex2));
  			edgeCnt--;
  			ListNode partnerNode = adjItem.partner;
  			if (partnerNode != adjNode) {
  				partnerNode.remove();
  			}
  			adjNode = adjNode.next();
  		} catch (InvalidNodeException e) {
  			System.err.println(e);
  		}
  	}

  	try {
  		node.remove();
  	} catch (InvalidNodeException e) {
  		System.err.println(e);
  	}

  }

  /**
   * isVertex() returns true if the parameter "vertex" represents a vertex of
   * the graph.
   *
   * Running time:  O(1).
   */
  public boolean isVertex(Object vertex) {
  	return vertexTable.find(vertex) != null;
  }

  /**
   * degree() returns the degree of a vertex.  Self-edges add only one to the
   * degree of a vertex.  If the parameter "vertex" doesn't represent a vertex
   * of the graph, zero is returned.
   *
   * Running time:  O(1).
   */
  public int degree(Object vertex) {
  	Entry e = vertexTable.find(vertex);
  	if (e == null) {
  		return 0;
  	}
  	try {
  		return ((VertexNode) (((ListNode) (e.value())).item())).degree();
  	} catch (InvalidNodeException ex) {
  		System.err.println(ex);
  	}
  	return 0;
  }

  /**
   * getNeighbors() returns a new Neighbors object referencing two arrays.  The
   * Neighbors.neighborList array contains each object that is connected to the
   * input object by an edge.  The Neighbors.weightList array contains the
   * weights of the corresponding edges.  The length of both arrays is equal to
   * the number of edges incident on the input vertex.  If the vertex has
   * degree zero, or if the parameter "vertex" does not represent a vertex of
   * the graph, null is returned (instead of a Neighbors object).
   *
   * The returned Neighbors object, and the two arrays, are both newly created.
   * No previously existing Neighbors object or array is changed.
   *
   * (NOTE:  In the neighborList array, do not return any internal data
   * structure you use to represent vertices!  Return only the same objects
   * that were provided by the calling application in calls to addVertex().)
   *
   * Running time:  O(d), where d is the degree of "vertex".
   */
  public Neighbors getNeighbors(Object vertex) {
  	Entry e = vertexTable.find(vertex);
  	if (e == null) {
  		return null;
  	}

  	Neighbors n = new Neighbors();
  	try {
  		VertexNode v = (VertexNode) (((ListNode) (e.value())).item());
  		int deg = v.degree();
  		if (deg == 0) {
  			return null;
  		}
  		n.neighborList = new Object[deg];
  		n.weightList = new int[deg];
  		ListNode node = v.adjList.front();
  		int i = 0;
  		while (node.isValidNode()) {
  			n.neighborList[i] = ((AdjListNode) (node.item())).vertex2;
  			n.weightList[i] = ((AdjListNode) (node.item())).weight;
  			i++;
  			node = node.next();
  		}
  	} catch (InvalidNodeException ex) {
  		System.err.println(ex);
  	}

  	return n;
  }

  /**
   * addEdge() adds an edge (u, v) to the graph.  If either of the parameters
   * u and v does not represent a vertex of the graph, the graph is unchanged.
   * The edge is assigned a weight of "weight".  If the graph already contains
   * edge (u, v), the weight is updated to reflect the new value.  Self-edges
   * (where u.equals(v)) are allowed.
   *
   * Running time:  O(1).
   */
  public void addEdge(Object u, Object v, int weight) {
  	if ((vertexTable.find(u) == null) || (vertexTable.find(v) == null)) {
  		return;
  	}
  	VertexPair pair = new VertexPair(u, v);
  	Entry e = edgeTable.find(pair);
  	if (e != null) {
  		try {
  			((AdjListNode) (((ListNode) (e.value())).item())).weight = weight;
  		} catch (InvalidNodeException ex) {
  			System.err.println(ex);
  		}
  		return;
  	}
  	edgeCnt++;
  	try {
  		VertexNode vertexU = (VertexNode) (((ListNode) (vertexTable.find(u).value())).item());
  		VertexNode vertexV = (VertexNode) (((ListNode) (vertexTable.find(v).value())).item());
  		AdjListNode adjU = new AdjListNode(u, v, weight);
  		AdjListNode adjV = new AdjListNode(v, u, weight);
  		vertexU.adjList.insertBack(adjU);
  		if (vertexU != vertexV) {
  			vertexV.adjList.insertBack(adjV);
  			adjU.partner = vertexV.adjList.back();
  			adjV.partner = vertexU.adjList.back();
  		} else {
  			adjU.partner = vertexU.adjList.back();
  		}
  		edgeTable.insert(pair, vertexU.adjList.back());
  	} catch (InvalidNodeException ex) {
  		System.err.println(ex);
  	}

  }

  /**
   * removeEdge() removes an edge (u, v) from the graph.  If either of the
   * parameters u and v does not represent a vertex of the graph, the graph
   * is unchanged.  If (u, v) is not an edge of the graph, the graph is
   * unchanged.
   *
   * Running time:  O(1).
   */
  public void removeEdge(Object u, Object v) {
  	if ((vertexTable.find(u) == null) || (vertexTable.find(v) == null)) {
  		return;
  	}
  	VertexPair pair = new VertexPair(u, v);
  	Entry e = edgeTable.remove(pair);
  	if (e == null) {
  		return;
  	}

  	edgeCnt--;
  	try {
  		ListNode node = (ListNode) (e.value());
  		ListNode pairNode = ((AdjListNode) (node.item())).partner;
  		if (pairNode != node) {
  			pairNode.remove();
  		}
  		node.remove();
  	} catch (InvalidNodeException ex) {
  		System.err.println(ex);
  	}
  }

  /**
   * isEdge() returns true if (u, v) is an edge of the graph.  Returns false
   * if (u, v) is not an edge (including the case where either of the
   * parameters u and v does not represent a vertex of the graph).
   *
   * Running time:  O(1).
   */
  public boolean isEdge(Object u, Object v) {
  	if ((vertexTable.find(u) == null) || (vertexTable.find(v) == null)) {
  		return false;
  	}
  	return edgeTable.find(new VertexPair(u, v)) != null;
  }

  /**
   * weight() returns the weight of (u, v).  Returns zero if (u, v) is not
   * an edge (including the case where either of the parameters u and v does
   * not represent a vertex of the graph).
   *
   * (NOTE:  A well-behaved application should try to avoid calling this
   * method for an edge that is not in the graph, and should certainly not
   * treat the result as if it actually represents an edge with weight zero.
   * However, some sort of default response is necessary for missing edges,
   * so we return zero.  An exception would be more appropriate, but also more
   * annoying.)
   *
   * Running time:  O(1).
   */
  public int weight(Object u, Object v) {
  	if (! isEdge(u, v)) {
  		return 0;
  	}
  	try {
  		return ((AdjListNode) (((ListNode) edgeTable.find(new VertexPair(u, v)).value()).item())).weight;
  	}
  	catch (InvalidNodeException e) {
  		System.err.println(e);
  	}

  	return 0;
  }

}
