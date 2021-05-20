import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class WriteThread extends Thread {
  private PrintWriter wirter;
  private Socket socket;
  private GameClient client;

  public WriteThread(Socket socket, GameClient client) {
    this.socket = socket;
    this.client = client;

    try {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      OutputStream output = socket.getOutputStream();
      this.wirter = new PrintWriter(output, true);
    } catch (IOException ex) {
      System.out.println("Error on getting output stream: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  @Override
  public void run() {
    Console console = System.console();

    boolean isUserAdded = false;
    String username = "";
    while (!isUserAdded) {
      username = console.readLine("\nEnter Username: ");

      if (this.client.doesUsernameExists(username)) {
        System.out.println("This user name already exists!");
        continue;
      }

      this.wirter.println(username);
      this.client.setUsername(username);
      isUserAdded = true;
    }

    String text = "";
    do {
      if (!this.client.getIsChatDisabled()) {
        text = console.readLine(username + ": ");

        this.wirter.println(text);
      }
    } while (!text.equals("exit"));

    try {
      this.socket.close();
    } catch (IOException ex) {
      System.out.println("Error on writinerver: " + ex.getMessage());

    }
  }

}
