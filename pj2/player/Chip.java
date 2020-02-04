package player;

class Chip {
	public int x, y;

	public Chip(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object c) {
		if (!(c instanceof Chip)) {
			return false;
		}
		return (x == ((Chip) c).x) && (y == ((Chip) c).y);
	}

	public String toString() {
		return x + ", " + y;
	}
}