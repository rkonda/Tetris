
class CollisionManager {
	
	Board board;
	
	CollisionManager(Board board) {
		this.board = board;
	}
	
	boolean isAtLeftBorder() {
		return board.currentCol <= 0;
	}

	boolean isAtRightBorder() {
		return board.currentCol + board.getPieceWidth() >= board.WIDTH;
	}

	boolean isAtBottomBorder() {
		return board.currentRow + board.getPieceHeight() >= board.HEIGHT;
	}

	// will the specified piece be outside the boundaries of the game if it were to replace the current piece
	boolean isOutside(int[][] newPiece) {
		return board.currentCol + newPiece[0].length > board.WIDTH ||
				board.currentRow + newPiece.length > board.HEIGHT;
	}
	
	// is the newly added piece colliding?
	boolean isColliding() {
		for (int row = 0; row < board.getPieceHeight(); row++) {
			for (int col = 0; col < board.getPieceWidth(); col++) {
				if (board.currentPiece[row][col] != 0 && 
					board.board[board.currentRow + row][board.currentCol + col] != 0) 
				{
					return true;
				}
			}
		}

		return false;
	}

	// will the current piece be colliding if it moved
	boolean isColliding(int horizontal, int downwards) {

		int boardBackup[][] = new int[board.HEIGHT][board.WIDTH];

		// backup the board
		for (int row = 0; row < board.HEIGHT; row++) {
			for (int col = 0; col < board.WIDTH; col++) {
				if (board.board[row][col] != 0) {
					boardBackup[row][col] = 1;
				}
			}
		}

		// unset the current piece from the board
		for (int row = 0; row < board.getPieceHeight(); row++) {
			for (int col = 0; col < board.getPieceWidth(); col++) {
				if( board.currentPiece[row][col] != 0 ) {
					board.board[board.currentRow + row][board.currentCol + col] = 0;
				}
			}
		}

		boolean isColliding = false;

		// check if the current piece would collide with existing pieces on the
		// board, when moved
		for (int row = 0; row < board.getPieceHeight(); row++) {
			for (int col = 0; col < board.getPieceWidth(); col++) {
				if (board.currentPiece[row][col] != 0 && 
					board.board[board.currentRow + row + downwards][board.currentCol + col + horizontal] != 0) 
				{
					isColliding = true;
					break;
				}
			}
		}

		// get the board back to the original state
		for (int row = 0; row < board.HEIGHT; row++) {
			for (int col = 0; col < board.WIDTH; col++) {
				if (boardBackup[row][col] != 0) {
					board.board[row][col] = 1;
				}
			}
		}

		return isColliding;
	}

	// will the given piece be colliding if it replaced the current piece?
	boolean isColliding(int[][] newPiece) {

		int boardBackup[][] = new int[board.HEIGHT][board.WIDTH];

		// backup the board
		for (int row = 0; row < board.HEIGHT; row++) {
			for (int col = 0; col < board.WIDTH; col++) {
				if (board.board[row][col] != 0) {
					boardBackup[row][col] = 1;
				}
			}
		}

		// unset the current piece from the board
		for (int row = 0; row < board.getPieceHeight(); row++) {
			for (int col = 0; col < board.getPieceWidth(); col++) {
				if( board.currentPiece[row][col] != 0 ) {
					board.board[board.currentRow + row][board.currentCol + col] = 0;
				}
			}
		}

		boolean isColliding = false;

		// check if the current piece would collide with existing pieces on the
		// board, when moved
		for (int row = 0; row < newPiece.length; row++) {
			for (int col = 0; col < newPiece[0].length; col++) {
				if (newPiece[row][col] != 0 && 
					board.board[board.currentRow + row][board.currentCol + col] != 0) 
				{
					isColliding = true;
					break;
				}
			}
		}

		// get the board back to the original state
		for (int row = 0; row < board.HEIGHT; row++) {
			for (int col = 0; col < board.WIDTH; col++) {
				if (boardBackup[row][col] != 0) {
					board.board[row][col] = 1;
				}
			}
		}

		return isColliding;
	}
		
}