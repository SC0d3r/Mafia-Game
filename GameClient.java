import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient {
  private String hostname;
  private int port;
  private String username;

  public GameClient(String hostname, int port) {
    this.port = port;
    this.hostname = hostname;
  }

  public void execute() {
    try {
      Socket socket = new Socket(this.hostname, this.port);
      System.out.println("Successfully connected to server!");

      new ReadThread(socket, this).start();
      new WriteThread(socket, this).start();

    } catch (UnknownHostException ex) {
      System.out.println("Server not found: " + ex.getMessage());
    } catch (IOException ex) {
      System.out.println("I/O Error : " + ex.getMessage());
    }
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return this.username;
  }

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Syntax : >java GameClient <host-name> <port-number>");
      System.exit(0);
    }
    String hostname = args[0];
    int port = Integer.parseInt(args[1]);

    GameClient gameClient = new GameClient(hostname, port);
    gameClient.execute();
  }
}
