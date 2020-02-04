package player;

class Best {
	public int depth;
	public double score;
	public Move move;

	public Best() {
		
	}

	public Best(int depth, double score) {
		this.depth = depth;
		this.score = score;
	}
}