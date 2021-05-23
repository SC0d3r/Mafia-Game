import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient {
  private String hostname;
  private int port;
  private String username;
  private Player player;
  private boolean isInMayorVotingState;

  public GameClient(String hostname, int port) {
    this.port = port;
    this.hostname = hostname;
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

  public boolean getCanChat() {
    if (this.player == null)
      return true;
    return this.player.getCanChat();
  }

  public boolean isAlive() {
    if (this.player == null) // user is not in game , is in the lobby
      return true;
    return this.player.getIsAlive();
  }

  public boolean doesUsernameExists(String username) {
    // TODO: add check for duplicate usernames
    return false;
  }

  public void execute() {
    try {
      Socket socket = new Socket(this.hostname, this.port);
      System.out.println("Successfully connected to server!");

      SocketDataReciever socketData = new SocketDataReciever();
      new ReadThread(socket, this, socketData).start();
      UTIL.sleep(10);
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

}
