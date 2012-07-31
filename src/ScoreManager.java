import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


class ScoreManager implements Observer {

	Board board;
	
	ScoreManager(Board board) {
		this.board = board;
	}
	
	long score = 0;
	int level = 1;
	long period = 2000;
	
	ScheduledThreadPoolExecutor timerExecutor = null;

	void setTimer() {
		timerExecutor = new ScheduledThreadPoolExecutor(1);
		timerExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				board.moveDown();
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
	
	void endGame() {
		board.gameEnded = true;
		timerExecutor.shutdownNow();
		timerExecutor = null;
		System.exit(0);
	}

	private void eventHandler_newPieceCreated() {
		score += (level * 10);
		if (score >= level * level * 1000) {
			goToNextLevel();
		}
	}

	private void eventHandler_rowCompleted() {
		score += (level * 100);
	}
		
	public void update(Observable arg0, Object arg1) {
		if(board.newPieceCreated) {
			board.newPieceCreated = false;
			
			eventHandler_newPieceCreated();
			return;
		}
		
		if(board.rowCompleted) {
			board.rowCompleted = false;
			
			eventHandler_rowCompleted();
			return;
		}
	}
}