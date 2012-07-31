import java.util.HashMap;
import java.util.Random;


class PieceChooser {
	
	private static HashMap<Board, Random> randomGenerators = new HashMap<Board, Random>();
	
	static void choose(Board board) {
	
		if( !randomGenerators.containsKey(board) ) {
			randomGenerators.put(board, new Random(System.currentTimeMillis()) );
		}
		
		java.util.Random random = randomGenerators.get(board);
		
		int currentPieceIndex = random.nextInt(board.pieces.size());
		copyPiece(board, currentPieceIndex);
	}

	private static void copyPiece(Board board, int currentPieceIndex) {
		board.currentPieceIndex = currentPieceIndex;
		board.currentPiece = new int[board.getPieceHeight(board.currentPieceIndex)][board.getPieceWidth(board.currentPieceIndex)];
		for (int row = 0; row < board.getPieceHeight(board.currentPieceIndex); row++) {
			for (int col = 0; col < board.getPieceWidth(board.currentPieceIndex); col++) {
				board.currentPiece[row][col] = board.pieces.get(board.currentPieceIndex)[row][col];
			}
		}
	}
}