import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

class GameServer {
  private int port;
  private Set<String> usernames;
  private Set<UserThread> userThreads;

  public GameServer(int port) {
    this.port = port;
    this.usernames = new HashSet<>();
    this.userThreads = new HashSet<>();
  }

  public void execute() {
    try (ServerSocket serverSocket = new ServerSocket(this.port)) {
      System.out.println("Chat Server Listening On [PORT]: " + this.port);
      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("New User Connected!");

        UserThread newUser = new UserThread(socket, this);
        this.userThreads.add(newUser);
        newUser.start();
      }
    } catch (IOException ex) {
      System.out.println("Server Error: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
    System.out.println("\n::Welcome to Mafia game::\n");
    if (args.length < 1) {
      System.out.println("Syntax : >java GameServer <port-number>");
      System.exit(0);
    }
    int port = Integer.parseInt(args[0]);
    GameServer gameServer = new GameServer(port);
    gameServer.execute();

  }

  public void broadcast(String message, UserThread excludeUser) {
    for (UserThread user : this.userThreads) {
      if (user == excludeUser)
        continue;
      user.sendMessage(message);
    }
  }

  public boolean addUserName(String username) {
    if (this.usernames.contains(username))
      return false;
    this.usernames.add(username);
    return true;
  }

  public void removeUser(String username, UserThread user) {
    boolean removed = this.usernames.remove(username);
    if (removed) {
      this.userThreads.remove(user);
      System.out.println("User [" + username + "] quit the game!");
    }
  }

  public boolean hasUsers() {
    return !this.usernames.isEmpty();
  }

  public Set<String> getUsernames() {
    return this.usernames;
  }
}