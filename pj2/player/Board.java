package player;

import java.util.*;

class Board {
	private final static int[][] DIRECT = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
	private final static boolean ADD = true, STEP = false;
	private final static int BLACK = 0, WHITE = 1;
	//0-empty; 1-color = 0; 2-color = 1;
	private int[][] state, connected;
	private LinkedList<Chip> black, white;
	private Chip[] v;
	private int len;

	public Board() {
		state = new int[8][8];
		state[0][0] = -1;
		state[7][0] = -1;
		state[0][7] = -1;
		state[7][7] = -1;
		black = new LinkedList<Chip>();
		white = new LinkedList<Chip>();
	}

	void setState(int x, int y, int s) {
		state[x][y] = s;
	}

	private boolean checkConnectedGroup(int x, int y, int curState) {
		int[] clusterX = new int[3], clusterY = new int[3];
		clusterX[0] = x;
		clusterY[0] = y;
		int front = 0, rear = 0;
		while (front <= rear) {
			for (int i = 0; i < 8; i++) {
				int xx = clusterX[front] + DIRECT[i][0], yy = clusterY[front] + DIRECT[i][1];
				if ((xx >= 0) && (xx < 8) && (yy >= 0) && (yy < 8)) {
					if (!((xx == x) && (yy == y)) && (state[xx][yy] == curState)) {
						rear++;
						if (rear == 2) {
							return false;
						}
						clusterX[rear] = xx;
						clusterY[rear] = yy;
					}
				}
			}
			front++;
		}
		return true;
	}

	boolean validMove(Move m, int color) {
		int x1 = m.x1, y1 = m.y1;

		if ((x1 < 0) || (x1 > 7) || (y1 < 0) || (y1 > 7)) {
			return false;
		}
		if (state[x1][y1] == -1) {
			return false;
		}
		if (color == 0) {
			if ((x1 == 0) || (x1 == 7)) {
				return false;
			}
		} else {
			if ((y1 == 0) || (y1 == 7)) {
				return false;
			}
		}
		if (state[x1][y1] != 0) {
			return false;
		}

		if (m.moveKind == Move.ADD) {
			setState(x1, y1, color + 1);
			if (!checkConnectedGroup(x1, y1, color + 1)) {
				setState(x1, y1, 0);
				return false;
			}
			setState(x1, y1, 0);
		} else {
			int x2 = m.x2, y2 = m.y2;
			setState(x2, y2, 0);
			setState(x1, y1, color + 1);
			if (!checkConnectedGroup(x1, y1, color + 1)) {
				setState(x2, y2, color + 1);
				setState(x1, y1, 0);
				return false;
			}
			setState(x2, y2, color + 1);
			setState(x1, y1, 0);
		}

		return true;
	}

	LinkedList<Move> validMoveList(int color) {
		if (color == BLACK) {
			len = black.size();
			v = new Chip[len];
			black.toArray(v);
		} else {
			len = white.size();
			v = new Chip[len];
			white.toArray(v);
		}

		LinkedList<Move> moveList = new LinkedList<Move>();
		if (len < 10) {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					Move m = new Move(i, j);
					if (validMove(m, color)) {
						moveList.add(m);
					}
				}
			}
		} else {
			for (int k = 0; k < len; k++) {
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						Move m = new Move(i, j, v[k].x, v[k].y);
						if (validMove(m, color)) {
							moveList.add(m);
						}
					}
				}
			}
		}

		return moveList;
	}

	void setMove(Move m, int color) {
		setState(m.x1, m.y1, color + 1);
		if (color == BLACK) {
			black.add(new Chip(m.x1, m.y1));
		} else {
			white.add(new Chip(m.x1, m.y1));
		}
		if (m.moveKind == Move.STEP) {
			setState(m.x2, m.y2, 0);
			if (color == BLACK) {
				black.remove(new Chip(m.x2, m.y2));
			} else {
				white.remove(new Chip(m.x2, m.y2));
			}
		}
	}

	void cancelMove(Move m, int color) {
		setState(m.x1, m.y1, 0);
		if (color == BLACK) {
			black.remove(new Chip(m.x1, m.y1));
		} else {
			white.remove(new Chip(m.x1, m.y1));
		}
		if (m.moveKind == Move.STEP) {
			setState(m.x2, m.y2, color + 1);
			if (color == BLACK) {
				black.add(new Chip(m.x2, m.y2));
			} else {
				white.add(new Chip(m.x2, m.y2));
			}
		}
	}

	private int detDirect(Chip i, Chip j) {
		int diffX = j.x - i.x, diffY = j.y - i.y;

		if (diffX * diffY != 0) {
			if ((diffX + diffY != 0) && (diffX != diffY)) {
				return -1;
			}
		}

		diffX = new Integer(diffX).compareTo(0);
		diffY = new Integer(diffY).compareTo(0);
		int d = 0;
		for (int k = 0; k < 8; k++) {
			if ((diffX == DIRECT[k][0]) && (diffY == DIRECT[k][1])) {
				d = k;
				break;
			}
		}

		int x = i.x + diffX, y = i.y + diffY;
		while (!((x == j.x) && (y == j.y))) {
			if (state[x][y] != 0) {
				return -1;
			}
			x += diffX;
			y += diffY;
		}

		return d;
	}

	private boolean searchNetwork(int color, int depth, int prevDirect, int cur, boolean[] visited) {
		if (((color == BLACK) && (v[cur].y == 7)) || (color == WHITE) && (v[cur].x == 7)) {
			if (depth >= 6) {
				return true;
			}
		}

		for (int i = 0; i < len; i++) {
			if ((connected[cur][i] >= 0) && !visited[i]) {
				if (connected[cur][i] != prevDirect) {
					if ((v[i].x != 0) && (v[i].y != 0)) {
						visited[i] = true;
						if (searchNetwork(color, depth + 1, connected[cur][i], i, visited)) {
							return true;
						}
						visited[i] = false;
					}
				}
			}
		}

		return false;
	}

	int[][] findConnected(int color) {
		if (color == BLACK) {
			len = black.size();
			v = new Chip[len];
			black.toArray(v);
		} else {
			len = white.size();
			v = new Chip[len];
			white.toArray(v);
		}	

		int[][] connected = new int[len][len];
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				connected[i][j] = -1;
				if (i == j) {
					continue;
				}
				if (color == BLACK) {
					if ((v[i].y + v[j].y == 0) || (v[i].y + v[j].y == 14)) {
						continue;
					}
				}
				if (color == WHITE) {
					if ((v[i].x + v[j].x == 0) || (v[i].x + v[j].x == 14)) {
						continue;
					}
				}

				connected[i][j] = detDirect(v[i], v[j]);
			}
		}

		return connected;
	}

	boolean findNetwork(int color) {
		if (((color == BLACK) && (black.size() < 6)) || ((color == WHITE) && (white.size() < 6))) {
			return false;
		}	

		connected = findConnected(color);
		boolean[] visited = new boolean[len];
		for (int i = 0; i < len; i++) {
			if (((color == BLACK) && (v[i].y == 0)) || ((color == WHITE) && (v[i].x == 0))) {
				visited[i] = true;
				if (searchNetwork(color, 1, -1, i, visited)) {
					return true;
				}
				visited[i] = false;
			}
		}

		return false;
	}

	double evalFunc(int color) {
		int[][] conMachine = findConnected(color), conHuman = findConnected(1 - color);
		int edgeCountMachine = 0, edgeCountHuman = 0;

		for (int i = 0; i < conMachine.length; i++) {
			for (int j = 0; j < conMachine[i].length; j++) {
				if (conMachine[i][j] >= 0) {
					edgeCountMachine++;
				}
			}
		}

		for (int i = 0; i < conHuman.length; i++) {
			for (int j = 0; j < conHuman[i].length; j++) {
				if (conHuman[i][j] >= 0) {
					edgeCountHuman++;
				}
			}
		}

		return ((double) edgeCountMachine + 1) / (edgeCountMachine + edgeCountHuman + 2) * 2 - 1;
	}

	public void display() {
		System.out.println("Black: " + black.size() + "   White: " + white.size());
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (state[j][i] == 1) {
					System.out.print("B ");
				} else if (state[j][i] == 2) {
					System.out.print("W ");
				} else {
					System.out.print("  ");
				}
			}
			System.out.println();
		}
	}

}