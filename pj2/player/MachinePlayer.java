/* MachinePlayer.java */

package player;

import java.util.*;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

  private int color, searchDepth;
  private Board board;
  private final static boolean MACHINE = true, HUMAN = false;
  private final static int UPPERBOUND = 500000; 

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
  	this.color = color;
  	board = new Board();
  }

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
  	this(color);
  	this.searchDepth = searchDepth;
  }

  private Best minimax(boolean side, int depth, double alpha, double beta, int bound) {
  	if (side == MACHINE) {
  		if (board.findNetwork(color)) {
  			return new Best(depth, 1);
  		}
  		if (board.findNetwork(1 - color)) {
  			return new Best(depth, -1);
  		}
  	} else {
  		if (board.findNetwork(1 - color)) {
  			return new Best(depth, -1);
  		}
  		if (board.findNetwork(color)) {
  			return new Best(depth, 1);
  		}
  	}

  	if (((searchDepth != 0) && (depth == searchDepth)) || ((searchDepth == 0) && (bound > UPPERBOUND))) {
  		return new Best(depth, board.evalFunc(color));
  	}

  	Best myBest;
  	Best reply;
  	if (side == MACHINE) {
  		myBest = new Best(UPPERBOUND, alpha);
  	} else {
  		myBest = new Best(UPPERBOUND, beta);
  	}

  	int color;
  	if (side == MACHINE) {
  		color = this.color;
  	} else {
  		color = 1 - this.color;
  	}

  	LinkedList<Move> validMove = board.validMoveList(color);
  	int newBound = (validMove.size() + 1) * bound;
  	myBest.move = validMove.getFirst();

  	for (Move m: validMove) {
  		board.setMove(m, color);
  		reply = minimax(!side, depth + 1, alpha, beta, newBound);
  		board.cancelMove(m, color);
  		if ((side == MACHINE) && ((reply.score > myBest.score) || ((reply.score == myBest.score) && (reply.depth < myBest.depth)))) {
  			myBest.move = m;
  			myBest.score = reply.score;
  			myBest.depth = reply.depth;
  			alpha = reply.score;
  		}
  		if ((side == HUMAN) && ((reply.score < myBest.score) || ((reply.score == myBest.score) && (reply.depth < myBest.depth)))) {
  			myBest.move = m;
  			myBest.score = reply.score;
  			myBest.depth = reply.depth;
  			beta = reply.score;
  		}
  		if (alpha > beta) {
  			return myBest;
  		}
  	}

  	return myBest;
  }

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
  	Best best = minimax(MACHINE, 0, -1, 1, 1);
  	board.setMove(best.move, color);
  	return best.move;
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
    if (board.validMove(m, 1 - color)) {
    	board.setMove(m, 1 - color);
    	return true;
    }

    return false;
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
    if (board.validMove(m, color)) {
    	board.setMove(m, color);
    	return true;
    }

    return false;
  }

  public static void main(String[] args) {
  	System.out.println("Check opponentMove & forceMove");
  	MachinePlayer mp = new MachinePlayer(0);
  	mp.forceMove(new Move(1, 6));
  	mp.forceMove(new Move(2, 4));
  	mp.forceMove(new Move(2, 1));
  	mp.forceMove(new Move(3, 0));
  	mp.forceMove(new Move(4, 7));
  	mp.forceMove(new Move(5, 3));
  	mp.forceMove(new Move(5, 2));
  	mp.forceMove(new Move(6, 5));
  	System.out.println(mp.forceMove(new Move(1, 2)));
  	System.out.println(mp.forceMove(new Move(2, 0)));
  	System.out.println(mp.forceMove(new Move(3, 2)));
  	System.out.println(mp.forceMove(new Move(3, 1)));
  	System.out.println(mp.forceMove(new Move(6, 4)));
  	System.out.println(mp.forceMove(new Move(5, 4)));
  	System.out.println(mp.forceMove(new Move(7, 1)));
  	System.out.println(mp.forceMove(new Move(0, 5)));
  	System.out.println(mp.forceMove(new Move(2, 5, 4, 7)));
  	System.out.println(mp.forceMove(new Move(2, 6, 4, 7)));
  	mp.board.display();

  	System.out.println("Check findNetwork & evalFunc");
  	System.out.println(mp.board.findNetwork(0));
  	System.out.println(mp.board.evalFunc(0));
  	mp = new MachinePlayer(0);
  	mp.forceMove(new Move(1, 3));
  	mp.forceMove(new Move(2, 0));
  	mp.forceMove(new Move(2, 5));
  	mp.forceMove(new Move(3, 3));
  	mp.forceMove(new Move(3, 5));
  	mp.forceMove(new Move(4, 2));
  	mp.forceMove(new Move(5, 5));
  	mp.forceMove(new Move(5, 7));
  	mp.forceMove(new Move(6, 0));
  	mp.forceMove(new Move(6, 5));
  	System.out.println(mp.board.findNetwork(0));
  	
  	System.out.println("Check chooseMove");
  	mp.board.cancelMove(new Move(5, 5), 0);
  	mp.board.display();
  	System.out.println(mp.chooseMove());
  	System.out.println(mp.board.findNetwork(0));

  	System.out.println("Check validMoveList");
  	mp = new MachinePlayer(1);
  	mp.forceMove(new Move(0, 1));
  	mp.forceMove(new Move(0, 3));
  	mp.forceMove(new Move(2, 1));
  	mp.forceMove(new Move(2, 3));
  	mp.forceMove(new Move(2, 5));
  	mp.forceMove(new Move(2, 6));
  	mp.forceMove(new Move(7, 1));
  	mp.opponentMove(new Move(3, 0));
  	mp.opponentMove(new Move(3, 3));
  	mp.opponentMove(new Move(3, 6));
  	mp.opponentMove(new Move(5, 1));
  	mp.opponentMove(new Move(6, 1));
  	mp.opponentMove(new Move(6, 3));
  	mp.opponentMove(new Move(6, 4));
  	for (Move m: mp.board.validMoveList(1)) {
  		System.out.println(m);
  	}
  	mp.board.display();
  	//System.out.println(mp.chooseMove());
  	System.out.println(mp.board.findNetwork(1));  	
  }

}
