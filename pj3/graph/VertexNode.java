package graph;

import list.*;

class VertexNode {
	protected Object vertex;
	protected DList adjList;

	protected VertexNode(Object o) {
		vertex = o;
		adjList = new DList();
	}

	protected int degree() {
		return adjList.length();
	}
}