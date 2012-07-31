import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

class Board extends Observable {
	
	// The board is represented as an array of arrays, with 10 rows and 10
	// columns.
	int HEIGHT, WIDTH;
	int[][] board;
	ScoreManager scoreManager = new ScoreManager(this);

	boolean boardCreated = false;
	void notifyBoardCreated() {
		boardCreated = true;
		setChanged();
		notifyObservers();
		boardCreated = false;
	}

	boolean rowCompleted = false;
	private void notifyRowCompleted() {
		rowCompleted = true;
		setChanged();
		notifyObservers();
		rowCompleted = false;
	}
	
	boolean newPieceCreated = false;
	private void notifyNewPieceCreated() {
		newPieceCreated = true;
		setChanged();
		notifyObservers();
		newPieceCreated = false;
	}

	boolean pieceMoved = false;
	private void notifyPieceMoved() {
		pieceMoved = true;
		setChanged();
		notifyObservers();
		pieceMoved = false;
	}

	boolean pieceSwitched = false;
	private void notifyPieceSwitched() {
		pieceSwitched = true;
		setChanged();
		notifyObservers();
		pieceSwitched = false;
	}
	

	public Board() {
		Configuration.load(this);
		
		board = new int[HEIGHT][WIDTH];
		
		loadPieces();

		createNewPiece();

		addObserver(this.scoreManager);
		addObserver( new ConsoleView(this));
		scoreManager.setTimer();

		notifyBoardCreated();
	}

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

	void loadPieces() {
		Configuration.loadPieces(this);
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

				notifyRowCompleted();

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

		PieceChooser.choose(this);
		
		currentRow = 0;
		currentCol = 0;

		if (isColliding()) {
			this.scoreManager.endGame();
			return;
		}

		notifyNewPieceCreated();

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
		
		notifyPieceSwitched();
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
	
	public synchronized boolean moveLeft() {

		if (isAtLeftBorder() || isColliding(-1, 0)) {
			return false;
		}

		setCurrentPiece(-1, 0);

		notifyPieceMoved();
		
		return true;
	}


	public synchronized boolean moveRight() {

		if (isAtRightBorder() || isColliding(1, 0)) {
			return false;
		}

		setCurrentPiece(1, 0);

		notifyPieceMoved();
		
		return true;
	}

	public synchronized boolean moveDown() {

		if (isAtBottomBorder() || isColliding(0, 1)) {
			removeCompletedRows();
			createNewPiece();
			return false;
		}

		setCurrentPiece(0, 1);

		notifyPieceMoved();
		
		return true;
	}

}