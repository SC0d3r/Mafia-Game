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

    String username = console.readLine("\nEnter Username: ");
    this.client.setUsername(username);
    this.wirter.println(username);

    String text;
    do {
      text = console.readLine("<" + username + ">: ");
      this.wirter.println(text);
    } while (!text.equals("exit"));

    try {
      this.socket.close();
    } catch (IOException ex) {
      System.out.println("Error on writinerver: " + ex.getMessage());

    }
  }

}
