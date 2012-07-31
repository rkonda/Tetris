package game;


import game.tetris.Board;
import game.tetris.TetrisUtils;

import java.io.IOException;
import java.util.*;

class Tetris {
	public static void main(String[] args) {
		Board board = new Board();

		while (!board.hasGameEnded()) {

			int key = -1;
			try {
				key = System.in.read();
			} catch (IOException e) {
				break;
			}

			// Movements
			if (key == (int) 'j') {
				board.moveLeft();
			} else if (key == (int) 'k') {
				board.moveRight();
			} else if (key == (int) 'm') {
				board.moveDown();
			} else if (key == (int) 'z') {
				board.moveToBottom();
			} 
			
			
			// Other transformations
			else if (key == (int) 'r') {
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
