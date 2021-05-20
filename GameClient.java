import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class GameClient {
  private String hostname;
  private int port;
  private String username;
  // private Set<String> inGameUsernames;
  private boolean isChatDisabled;

  public GameClient(String hostname, int port) {
    this.port = port;
    this.hostname = hostname;
    // this.inGameUsernames = GameClient.fetchUsernames("usernames.txt");
    this.isChatDisabled = false;
  }

  public void setIsChatDisabled(boolean status) {
    this.isChatDisabled = status;
  }

  public boolean getIsChatDisabled() {
    return this.isChatDisabled;
  }

  public boolean doesUsernameExists(String username) {
    // TODO: add check for duplicate usernames
    return false;
    // return this.inGameUsernames.contains(username);
  }

  public void execute() {
    try {
      Socket socket = new Socket(this.hostname, this.port);
      System.out.println("Successfully connected to server!");

      new ReadThread(socket, this).start();
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
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

  public static Set<String> fetchUsernames(String fileName) {
    try (Scanner in = new Scanner(new FileReader(fileName))) {
      String usernames = in.nextLine().trim();
      System.out.println("all usernames : " + usernames);
      return new HashSet<String>(Arrays.asList(usernames.split(",")));
    } catch (Exception ex) {
      System.out.println("Error while reading usernames : " + ex.getMessage());
      ex.printStackTrace();
      return new HashSet<>();
    }
  }
}
