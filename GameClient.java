import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * sets up the client part of application and spawns 2 threads 1-read 2-write
 */
public class GameClient {
  private String hostname;
  private int port;
  private String username;
  private Player player;
  private GameState gameState;
  private boolean isDebugModeOn;

  public GameClient(String hostname, int port) {
    this.isDebugModeOn = false;
    this.port = port;
    this.hostname = hostname;
    this.gameState = new GameState();
    this.player = new Player("Anonymouse", ROLE.CITIZEN, null);
  }

  public boolean getIsDebugModeOn() {
    return this.isDebugModeOn;
  }

  public void setDebugMode(boolean status) {
    this.isDebugModeOn = status;
  }

  public Player getPlayer() {
    return this.player;
  }

  public void setGameState(GameState state) {
    this.gameState = state;
  }

  public GameState getGameState() {
    return this.gameState;
  }

  public void setPlayer(Player p) {
    this.player = p;
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
    return false;
  }

  public void execute() {
    try {
      Socket socket = new Socket(this.hostname, this.port);
      System.out.println("Successfully connected to server!");

      SocketDataReciever socketData = new SocketDataReciever();
      new ReadThread(socket, this, socketData).start();
      UTIL.sleep(10);
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
      System.out.println("Syntax : >java GameClient <host-name> <port-number> <debug-mode>");
      System.exit(0);
    }
    String hostname = args[0];
    int port = Integer.parseInt(args[1]);

    GameClient gameClient = new GameClient(hostname, port);
    boolean debug = false;
    try {
      debug = Boolean.valueOf(args[2]);
    } catch (Exception ex) {
    }
    gameClient.setDebugMode(debug);
    gameClient.execute();
  }

}
