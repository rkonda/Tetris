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
	CollisionManager collisionManager = new CollisionManager(this);
	
	void createNewPiece() {

		PieceChooser.choose(this);
		
		currentRow = 0;
		currentCol = 0;

		if (collisionManager.isColliding()) {
			this.scoreManager.endGame();
			return;
		}

		notifyNewPieceCreated();

		// starting offset for the piece
		setCurrentPiece(2, 0);
	}

	public synchronized void switchPiece( int[][] newPiece ) {
		if( collisionManager.isOutside(newPiece) || collisionManager.isColliding(newPiece) ) {
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

	public synchronized boolean moveLeft() {

		if (collisionManager.isAtLeftBorder() || collisionManager.isColliding(-1, 0)) {
			return false;
		}

		setCurrentPiece(-1, 0);

		notifyPieceMoved();
		
		return true;
	}

	public synchronized boolean moveRight() {

		if (collisionManager.isAtRightBorder() || collisionManager.isColliding(1, 0)) {
			return false;
		}

		setCurrentPiece(1, 0);

		notifyPieceMoved();
		
		return true;
	}

	public synchronized boolean moveDown() {
		return moveDown(true);
	}
	
	public synchronized boolean moveDown(boolean doPrint) {

		if (collisionManager.isAtBottomBorder() || collisionManager.isColliding(0, 1)) {
			removeCompletedRows();
			createNewPiece();
			return false;
		}

		setCurrentPiece(0, 1);

		if(doPrint)
			notifyPieceMoved();
		
		return true;
	}

	public synchronized void moveToBottom() {
		while ( moveDown(false) );
		
		notifyPieceMoved();
	}

}