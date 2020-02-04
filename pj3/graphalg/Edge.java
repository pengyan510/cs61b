package graphalg;

class Edge implements Comparable {
	Object vertex1, vertex2;
	int weight;

	protected Edge(Object o1, Object o2, int w) {
		vertex1 = o1;
		vertex2 = o2;
		weight = w;
	}

	public int compareTo(Object o) {
		if (weight < ((Edge) o).weight) {
			return -1;
		} else if (weight == ((Edge) o).weight) {
			return 0;
		} else {
			return 1;
		}
	}
}