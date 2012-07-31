package game.tetris;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


/*
 * TetrisUtils provides a single function, getInput(), used for responding to user keystrokes.
 * NOTE(philc): Much of this input IO code was lifted from the Internet.
 */
public class TetrisUtils {
  public static String LEFT = "LEFT";
  public static String UP = "UP";
  public static String RIGHT = "RIGHT";
  public static String DOWN = "DOWN";
  
  private static String ttyConfig;
  
  /**
   * Reads input from STDIN. Returns one of the strings LEFT, UP, RIGHT, DOWN if an arrow key was pressed,
   * or the character that was typed.
   */
  public static String getInput() {
    byte[] buffer = new byte[5];
    boolean inputInterrupted = false;

    try {
      setTerminalToCBreak();
      System.in.read(buffer);

      // Handle CTRL+C.
      if (buffer[0] == 3 && buffer[1] == 0) {
        System.out.println("CTRL+C");
        throw new InterruptedException();
      // The byte sequence [27, 91, XX] represents the arrow keys when read over STDIN.
      } else if (buffer[0] == 27 && buffer[1] == 91) {
        if (buffer[2] == 68) return LEFT;
        else if (buffer[2] == 65) return UP;
        else if (buffer[2] == 67) return RIGHT;
        else if (buffer[2] == 66) return DOWN;
        else return null;
      } else {
        return new String(buffer);
      }
    }
    catch (IOException e) { System.err.println("IOException"); }
    catch (InterruptedException e) { inputInterrupted = true; }
    finally {
      try { stty( ttyConfig.trim() ); }
      catch (Exception e) { System.err.println("Exception restoring tty config"); }
    }

    if (inputInterrupted)
      System.exit(0);

    return null;
  }

  private static void setTerminalToCBreak() throws IOException, InterruptedException {
    ttyConfig = stty("-g");
    // set the console to be character-buffered instead of line-buffered, and disable character echoing.
    stty("raw -echo");
  }

  /**
  *  Execute the stty command with the specified arguments
  *  against the current active terminal.
  */
  private static String stty(final String args) throws IOException, InterruptedException {
    String cmd = "stty " + args + " < /dev/tty";
    return exec(new String[] { "sh", "-c", cmd });
  }

  /**
  *  Execute the specified command and return the output
  *  (both stdout and stderr).
  */
  private static String exec(final String[] cmd) throws IOException, InterruptedException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    Process p = Runtime.getRuntime().exec(cmd);
    int c;
    InputStream in = p.getInputStream();

    while ((c = in.read()) != -1) {
      bout.write(c);
    }

    in = p.getErrorStream();

    while ((c = in.read()) != -1) {
      bout.write(c);
    }

    p.waitFor();

    String result = new String(bout.toByteArray());
    return result;
  }
}