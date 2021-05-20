import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class GameServer {
  private int port;
  private volatile Set<String> usernames;
  private volatile Set<UserThread> userThreads;
  private boolean isGameStarted;
  private SocketDataSender dataSender;
  private volatile ArrayList<Player> readyPlayers;
  private Narrator ravi;
  private static final int MIN_NUMBER_OF_PLAYERS = 3;
  // public int a = 0;

  public GameServer(int port) {
    this.port = port;
    this.usernames = new HashSet<>();
    this.userThreads = new HashSet<>();
    this.isGameStarted = false;
    this.dataSender = new SocketDataSender();
    this.readyPlayers = new ArrayList<>();
    this.ravi = new Narrator(this, GameData.getInstance());
  }

  public boolean getIsGameStarted() {
    return this.isGameStarted;
  }

  public ArrayList<Player> getReadyPlayers() {
    return this.readyPlayers;
  }

  public UserThread getUser(String username) {
    for (UserThread user : this.userThreads) {
      if (user.getUsername().equals(username))
        return user;

    }
    System.out.println(":::::: ERROR WHY NO NAME! ::::::");
    return null;
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

  public Set<UserThread> getUserThreads() {
    return this.userThreads;
  }

  public void setIsGameStarted(boolean status) {
    this.isGameStarted = status;
  }

  public void waitingLobbyInfo(String username) {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    String watingFor = this.dataSender.createInfo("Waiting",
        (GameServer.MIN_NUMBER_OF_PLAYERS - this.getReadyPlayers().size()) + "");
    String isReady = this.dataSender.createInfo("Ready", "YES");
    String notReady = this.dataSender.createInfo("Ready", "NO ,Type '!ready'");
    for (UserThread user : this.userThreads) {
      if (this.isInReadyPlayersList(user.getUsername()))
        // if (this.readyPlayers.contains(user.getUsername()))
        user.sendMessage(isReady);
      else
        user.sendMessage(notReady);

    }
    this.broadcast(watingFor, null);

  }

  private boolean isInReadyPlayersList(String username) {
    for (Player p : this.readyPlayers) {
      if (p.getUsername().equals(username))
        return true;
    }
    return false;
  }

  public void startGame() {
    if (this.isGameStarted)
      return;
    this.isGameStarted = true;
    System.out.println("Port: " + port + " Starting the game");

    // Narrator ravi = new Narrator(this, GameData.getInstance());
    this.ravi.run();
  }

  public void endGame() {
    System.out.println("Game Ended.");
    this.isGameStarted = false;
    this.readyPlayers = new ArrayList<>();
  }

  public synchronized void registerForGame(String username, UserThread user) {
    if (this.isGameStarted) {
      user.sendMessage(" :: Game is Already started! ::");
      return;
    }
    // if allready registered just return
    for (Player p : this.readyPlayers) {
      if (p.getUsername().equals(username))
        return;
    }

    System.out.println("User [" + username + "] is ready for the game.");
    this.readyPlayers.add(new Player(username, ROLE.CITIZEN, user));
  }

  public void unregisterFromGame(String username) {
    System.out.println("User [" + username + "] unregistered from game.");

    // removing the player from list
    ArrayList<Player> result = new ArrayList<>();
    for (Player p : this.readyPlayers) {
      if (p.getUsername().equals(username))
        continue;
      result.add(p);
    }
    this.readyPlayers = result;
  }

  public boolean canBeginTheGame() {
    // System.out.println("HERE");
    // System.out.println(this.readyPlayers.size());
    if (this.isGameStarted)
      return false;
    if (this.readyPlayers.size() >= GameServer.MIN_NUMBER_OF_PLAYERS)
      // if (this.a >= GameServer.MIN_NUMBER_OF_PLAYERS)
      return true;
    return false;
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

  // public void broadcast(String message, String username) {
  // for (UserThread user : this.userThreads) {
  // if (user.hasUsername(username)) {
  // continue;
  // }
  // user.sendMessage(message);
  // }
  // }

  public boolean addUserName(String username) {
    if (username.equals("null"))// this is for if user quits before typing his username
      return false;
    if (this.usernames.contains(username))
      return false;

    this.usernames.add(username);
    // this.saveUsernamesToFile("usernames.txt");
    return true;
  }

  public void removeUser(String username, UserThread user) {
    boolean removed = this.usernames.remove(username);
    if (removed) {
      this.userThreads.remove(user);
      System.out.println("User [" + username + "] quit the game!");

      // this.saveUsernamesToFile("usernames.txt");
      this.unregisterFromGame(username);
    }
  }

  public boolean hasUsers() {
    // if(this.usernames.
    return !this.usernames.isEmpty();
  }

  public Set<String> getUsernames() {
    return this.usernames;
  }

  public static void clrscr() {
    // Clears Screen in java
    try {
      if (System.getProperty("os.name").contains("Windows"))
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      else
        Runtime.getRuntime().exec("clear");
    } catch (IOException | InterruptedException ex) {
    }
  }

  public void saveUsernamesToFile(String fileName) {
    try (PrintWriter pr = new PrintWriter(fileName)) {
      pr.println(String.join(",", this.usernames));

    } catch (Exception ex) {
      System.out.println("Error while saving usernames : " + ex.getMessage());
      ex.printStackTrace();
    }
  }
}