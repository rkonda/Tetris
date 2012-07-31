import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Tetris {
	public static void main(String[] args) {
		Board board = new Board();
		board.print();

		while (!board.hasGameEnded()) {

			int key = -1;
			try {
				key = System.in.read();
			} catch (IOException e) {
				break;
			}

			if (key == (int) 'j') {
				board.moveLeft();
			} else if (key == (int) 'k') {
				board.moveRight();
			} else if (key == (int) 'm') {
				board.moveDown();
			} else if (key == (int) 'r') {
				board.rotate();
			} else if (key == (int) 'e') {
				board.mirrorUp();
			} else if (key == (int) 'd') {
				board.mirrorRight();
			} else {
				continue;
			}

			if (board.hasGameEnded())
				break;
		}
		
		System.exit(0);
	}

	public static void main_mac(String[] args) {
		Board board = new Board();
		board.print();

		while (!board.hasGameEnded()) {

			String input = TetrisUtils.getInput();
			if (input == TetrisUtils.LEFT) {
				board.moveLeft();
			} else if (input == TetrisUtils.RIGHT) {
				board.moveRight();
			} else if (input == TetrisUtils.DOWN) {
				board.moveDown();
			} else {
				continue;
			}

			if (board.hasGameEnded())
				break;
		}
	}

}

class Board {
	static int WIDTH = 10;
	static int HEIGHT = 20;
	// The board is represented as an array of arrays, with 10 rows and 10
	// columns.
	int[][] board = new int[HEIGHT][WIDTH];

	public int[][] getBoard() {
		return board;
	}

	int getPieceHeight(int pieceIndex) {
		return pieces.get(pieceIndex).length;
	}

	int getPieceWidth(int pieceIndex) {
		return pieces.get(pieceIndex)[0].length;
	}

	int getPieceHeight() {
		return currentPiece.length;
	}

	int getPieceWidth() {
		return currentPiece[0].length;
	}

	List<int[][]> pieces = new ArrayList<int[][]>();

	boolean gameEnded = false;

	public boolean hasGameEnded() {
		return gameEnded;
	}

	ScheduledThreadPoolExecutor timerExecutor = null;

	long score = 0;
	int level = 1;
	long period = 2000;

	public Board() {
		loadPieces();

		createNewPiece();

		setTimer();
	}

	void setTimer() {
		timerExecutor = new ScheduledThreadPoolExecutor(1);
		timerExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				moveDown();
			}
		}, period, period, TimeUnit.MILLISECONDS);
	}

	void goToNextLevel() {
		level += 1;
		period -= 100;
		if(period < 100) {
			period = 100;
		}
		timerExecutor.shutdownNow();
		setTimer();
	}

	void loadPieces() {
		int[][] rectangle = new int[1][4];
		rectangle[0][0] = 1;
		rectangle[0][1] = 1;
		rectangle[0][2] = 1;
		rectangle[0][3] = 1;

		pieces.add(rectangle);

		int[][] square = new int[2][2];
		square[0][0] = 1;
		square[0][1] = 1;
		square[1][0] = 1;
		square[1][1] = 1;

		pieces.add(square);

		int[][] snake = new int[2][3];
		snake[0][0] = 1;
		snake[0][1] = 1;
		snake[1][1] = 1;
		snake[1][2] = 1;

		pieces.add(snake);

		int[][] hammer = new int[2][3];
		hammer[0][1] = 1;
		hammer[1][0] = 1;
		hammer[1][1] = 1;
		hammer[1][2] = 1;

		pieces.add(hammer);
	}

	/*
	 * Prints the contents of the board and draws a border around it.
	 */
	public synchronized void print() {
		System.out.println("level=" + this.level + ", score=" + this.score + ", period=" + period);
		
		for (int col = 0; col < WIDTH + 2; col++)
			System.out.print("*");
		System.out.println();

		char output;
		for (int row = 0; row < HEIGHT; row++) {
			System.out.print("|");
			for (int col = 0; col < WIDTH; col++) {
				int value = board[row][col];
				System.out.print(value == 0 ? " " : "#");
			}
			System.out.println("|");
		}

		for (int col = 0; col < WIDTH + 2; col++)
			System.out.print("*");
		System.out.println();
		
		//System.out.println();
		//printCurrent();
	}

	void printCurrent() {
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				if( currentPiece[row][col] == 0 )
					System.out.print(" ");
				else
					System.out.print("#");
			}
			System.out.println();
		}
	}
	
	void setCurrentPiece(int horizontal, int downwards) {
		// set pieces on the board

		// remove the current piece form the board
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				if (currentPiece[row][col] != 0) {
					board[currentRow + row][currentCol + col] = 0;
				}
			}
		}

		currentRow += downwards;
		currentCol += horizontal;

		// add the new piece to the board
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				if (currentPiece[row][col] != 0) {
					board[currentRow + row][currentCol + col] = 1;
				}
			}
		}
	}

	void endGame() {
		gameEnded = true;
		timerExecutor.shutdownNow();
		timerExecutor = null;
		System.exit(0);
	}

	void removeCompletedRows() {
		for (int row = 0; row < HEIGHT; row++) {
			boolean foundUnfilledColumn = false;

			for (int col = 0; col < WIDTH; col++) {
				if (board[row][col] == 0) {
					foundUnfilledColumn = true;
					break;
				}
			}

			if (!foundUnfilledColumn) {

				score += (level * 100);

				for (int row2 = row - 1; row2 >= 0; row2--) {
					for (int col = 0; col < WIDTH; col++) {
						board[row2 + 1][col] = 0;
					}

					for (int col = 0; col < WIDTH; col++) {
						if (board[row2][col] != 0)
							board[row2 + 1][col] = 1;
					}
				}

				for (int col = 0; col < WIDTH; col++) {
					board[0][col] = 0;
				}

				print();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.interrupted();
				}
			}
		}
	}

	int[][] currentPiece;
	int currentPieceIndex = 0;
	int currentRow = 0;
	int currentCol = 0;

	void createNewPiece() {

		java.util.Random random = new Random(System.currentTimeMillis());
		currentPieceIndex = random.nextInt(pieces.size());
		currentPiece = new int[getPieceHeight(currentPieceIndex)][getPieceWidth(currentPieceIndex)];
		for (int row = 0; row < getPieceHeight(currentPieceIndex); row++) {
			for (int col = 0; col < getPieceWidth(currentPieceIndex); col++) {
				currentPiece[row][col] = pieces.get(currentPieceIndex)[row][col];
			}
		}
		
		currentRow = 0;
		currentCol = 0;

		if (isColliding()) {
			endGame();
			return;
		}

		score += (level * 10);
		if (score >= level * level * 1000) {
			goToNextLevel();
		}

		// starting offset for the piece
		setCurrentPiece(2, 0);
	}

	public synchronized void switchPiece( int[][] newPiece ) {
		if( isOutside(newPiece) || isColliding(newPiece) ) {
			return;
		}

		// unset the current piece in the board
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				if( board[currentRow + row][currentCol + col] != 0 && currentPiece[row][col] != 0) {
					board[currentRow + row][currentCol + col] = 0;
				}
			}
		}		
		
		currentPiece = newPiece;
		
		// set the new piece on the board
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				if( currentPiece[row][col] != 0 ) {
					board[currentRow + row][currentCol + col] = currentPiece[row][col];
				}
			}
		}
		
		print();
	}

	public int[][] getRotatedPiece() {
		int[][] newPiece = new int[getPieceWidth()][getPieceHeight()];
		
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				newPiece[col][getPieceHeight()-1-row] = currentPiece[row][col];
			}
		}
		
		return newPiece;
	}

	public int[][] getMirroredUpPiece() {
		int[][] newPiece = new int[getPieceHeight()][getPieceWidth()];
		
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				newPiece[getPieceHeight()-1-row][col] = currentPiece[row][col];
			}
		}
		
		return newPiece;
	}

	public int[][] getMirroredRightPiece() {
		int[][] newPiece = new int[getPieceHeight()][getPieceWidth()];
		
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				newPiece[row][getPieceWidth()-1-col] = currentPiece[row][col];
			}
		}
		
		return newPiece;
	}

	public synchronized void rotate() {
		int[][] newPiece = getRotatedPiece();
		
		switchPiece(newPiece);
	}

	public synchronized void mirrorUp() {
		int[][] newPiece = getMirroredUpPiece();
		
		switchPiece(newPiece);
	}

	public synchronized void mirrorRight() {
		int[][] newPiece = getMirroredRightPiece();
		
		switchPiece(newPiece);
	}
	
	boolean isAtLeftBorder() {
		return currentCol <= 0;
	}

	boolean isAtRightBorder() {
		return currentCol + getPieceWidth() >= WIDTH;
	}

	boolean isAtBottomBorder() {
		return currentRow + getPieceHeight() >= HEIGHT;
	}

	// will the specified piece be outside the boundaries of the game if it were to replace the current piece
	boolean isOutside(int[][] newPiece) {
		return currentCol + newPiece[0].length > WIDTH ||
				currentRow + newPiece.length > HEIGHT;
	}
	
	// is the newly added piece colliding?
	boolean isColliding() {
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				if (currentPiece[row][col] != 0 && 
					board[currentRow + row][currentCol + col] != 0) 
				{
					return true;
				}
			}
		}

		return false;
	}

	// will the current piece be colliding if it moved
	boolean isColliding(int horizontal, int downwards) {

		int boardBackup[][] = new int[HEIGHT][WIDTH];

		// backup the board
		for (int row = 0; row < HEIGHT; row++) {
			for (int col = 0; col < WIDTH; col++) {
				if (board[row][col] != 0) {
					boardBackup[row][col] = 1;
				}
			}
		}

		// unset the current piece from the board
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				if( currentPiece[row][col] != 0 ) {
					board[currentRow + row][currentCol + col] = 0;
				}
			}
		}

		boolean isColliding = false;

		// check if the current piece would collide with existing pieces on the
		// board, when moved
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				if (currentPiece[row][col] != 0 && 
					board[currentRow + row + downwards][currentCol + col + horizontal] != 0) 
				{
					isColliding = true;
					break;
				}
			}
		}

		// get the board back to the original state
		for (int row = 0; row < HEIGHT; row++) {
			for (int col = 0; col < WIDTH; col++) {
				if (boardBackup[row][col] != 0) {
					board[row][col] = 1;
				}
			}
		}

		return isColliding;
	}

	// will the given piece be colliding if it replaced the current piece?
	boolean isColliding(int[][] newPiece) {

		int boardBackup[][] = new int[HEIGHT][WIDTH];

		// backup the board
		for (int row = 0; row < HEIGHT; row++) {
			for (int col = 0; col < WIDTH; col++) {
				if (board[row][col] != 0) {
					boardBackup[row][col] = 1;
				}
			}
		}

		// unset the current piece from the board
		for (int row = 0; row < getPieceHeight(); row++) {
			for (int col = 0; col < getPieceWidth(); col++) {
				if( currentPiece[row][col] != 0 ) {
					board[currentRow + row][currentCol + col] = 0;
				}
			}
		}

		boolean isColliding = false;

		// check if the current piece would collide with existing pieces on the
		// board, when moved
		for (int row = 0; row < newPiece.length; row++) {
			for (int col = 0; col < newPiece[0].length; col++) {
				if (newPiece[row][col] != 0 && 
					board[currentRow + row][currentCol + col] != 0) 
				{
					isColliding = true;
					break;
				}
			}
		}

		// get the board back to the original state
		for (int row = 0; row < HEIGHT; row++) {
			for (int col = 0; col < WIDTH; col++) {
				if (boardBackup[row][col] != 0) {
					board[row][col] = 1;
				}
			}
		}

		return isColliding;
	}

	public synchronized void moveLeft() {

		if (isAtLeftBorder() || isColliding(-1, 0)) {
			return;
		}

		setCurrentPiece(-1, 0);

		print();
	}

	public synchronized void moveRight() {

		if (isAtRightBorder() || isColliding(1, 0)) {
			return;
		}

		setCurrentPiece(1, 0);

		print();
	}

	public synchronized void moveDown() {

		if (isAtBottomBorder() || isColliding(0, 1)) {
			removeCompletedRows();
			createNewPiece();
			return;
		}

		setCurrentPiece(0, 1);

		print();
	}

}
