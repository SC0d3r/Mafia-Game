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
  // private boolean isChatDisabled;
  private Player player;
  private boolean isInMayorVotingState;

  public GameClient(String hostname, int port) {
    this.port = port;
    this.hostname = hostname;
    // this.inGameUsernames = GameClient.fetchUsernames("usernames.txt");
    // this.isChatDisabled = false;
    this.isInMayorVotingState = false;
  }

  public void setPlayer(Player p) {
    this.player = p;
  }

  public void setIsInMayorVotingState(boolean state) {
    this.isInMayorVotingState = state;
  }

  public boolean getIsInMayorVotingState() {
    return this.isInMayorVotingState;
  }

  public boolean isRole(ROLE role) {
    return this.player.getRole() == role;
  }

  // public void setIsChatDisabled(boolean status) {
  // this.isChatDisabled = status;
  // }

  public boolean getCanChat() {
    if (this.player == null)
      return true;
    return this.player.getCanChat();
    // return this.isChatDisabled;
  }

  public boolean isAlive() {
    if (this.player == null) // user is not in game , is in the lobby
      return true;
    return this.player.getIsAlive();
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

      SocketDataReciever socketData = new SocketDataReciever();
      new ReadThread(socket, this, socketData).start();
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      new WriteThread(socket, this, socketData).start();

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
