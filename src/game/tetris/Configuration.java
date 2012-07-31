package game.tetris;


class Configuration {
	private static int WIDTH = 10;
	private static int HEIGHT = 20;
	
	static void load(Board board) {
		board.HEIGHT = HEIGHT;
		board.WIDTH = WIDTH;
	}

	static void loadPieces(Board board) {
		int[][] rectangle = new int[1][4];
		rectangle[0][0] = 1;
		rectangle[0][1] = 1;
		rectangle[0][2] = 1;
		rectangle[0][3] = 1;

		board.pieces.add(rectangle);

		int[][] square = new int[2][2];
		square[0][0] = 1;
		square[0][1] = 1;
		square[1][0] = 1;
		square[1][1] = 1;

		board.pieces.add(square);

		int[][] snake = new int[2][3];
		snake[0][0] = 1;
		snake[0][1] = 1;
		snake[1][1] = 1;
		snake[1][2] = 1;

		board.pieces.add(snake);

		int[][] hammer = new int[2][3];
		hammer[0][1] = 1;
		hammer[1][0] = 1;
		hammer[1][1] = 1;
		hammer[1][2] = 1;

		board.pieces.add(hammer);
	}

}