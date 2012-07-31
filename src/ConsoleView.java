import java.util.Observable;
import java.util.Observer;


class ConsoleView implements Observer {
	
	Board board;
	
	ConsoleView(Board board) {
		this.board = board;
	}
	
	/*
	 * Prints the contents of the board and draws a border around it.
	 */
	public void print() {
		synchronized(board) {
			System.out.println("level=" + board.scoreManager.level + 
					", score=" + board.scoreManager.score + 
					", period=" + board.scoreManager.period);
			
			for (int col = 0; col < board.WIDTH + 2; col++)
				System.out.print("*");
			System.out.println();
	
			for (int row = 0; row < board.HEIGHT; row++) {
				System.out.print("|");
				for (int col = 0; col < board.WIDTH; col++) {
					int value = board.board[row][col];
					System.out.print(value == 0 ? " " : "#");
				}
				System.out.println("|");
			}
	
			for (int col = 0; col < board.WIDTH + 2; col++)
				System.out.print("*");
			System.out.println();
			
			System.out.println();
			System.out.println("Current Piece:");
			printCurrent();
		}
	}

	public void printCurrent() {
		for (int row = 0; row < board.getPieceHeight(); row++) {
			for (int col = 0; col < board.getPieceWidth(); col++) {
				if( board.currentPiece[row][col] == 0 )
					System.out.print(" ");
				else
					System.out.print("#");
			}
			System.out.println();
		}
	}

	public void update(Observable o, Object arg) {
		Board board = (Board) o;
		
		if( board.boardCreated ||
			board.pieceMoved ||
			board.pieceSwitched ||
			board.rowCompleted ||
			false
		) 
		{
			print();
		}
	}
	
}