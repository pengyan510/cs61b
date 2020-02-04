package graph;

import list.*;

class AdjListNode {
	protected Object vertex1, vertex2;
	protected ListNode partner;
	protected int weight;

	protected AdjListNode(Object o1, Object o2, int w) {
		vertex1 = o1;
		vertex2 = o2;
		weight = w;
	}
}