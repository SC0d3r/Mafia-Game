import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class GameServer {
  private int port;
  private volatile Set<String> usernames;
  private volatile Set<UserThread> userThreads;
  private boolean isGameStarted;
  private SocketDataSender dataSender;
  private volatile ArrayList<Player> readyPlayers;
  private Narrator ravi;
  private GameState gameState;
  private SocketDataReciever dataReciever;
  private static final int MIN_NUMBER_OF_PLAYERS = 3;

  public GameServer(int port) {
    this.port = port;
    this.usernames = new HashSet<>();
    this.userThreads = new HashSet<>();
    this.isGameStarted = false;
    this.dataSender = new SocketDataSender();
    this.readyPlayers = new ArrayList<>();
    this.gameState = new GameState();
    this.ravi = new Narrator(this, GameData.getInstance());
    this.dataReciever = new SocketDataReciever();
  }

  public GameState getGameState() {
    return this.gameState;
  }

  public boolean getIsGameStarted() {
    return this.isGameStarted;
  }

  public void sendGameStateToClients() {
    String gameStateData = this.dataSender.createGameState(this.gameState);
    for (Player p : this.readyPlayers) {
      p.sendMessage(gameStateData);
    }
  }

  public void sendPlayerStateToClients() {
    for (Player p : this.getReadyPlayers()) {
      p.sendMessage(this.dataSender.createPlayerState(p));
    }
  }

  //
  public ArrayList<Player> getReadyPlayers() {
    return this.readyPlayers;
  }

  public ArrayList<String> getReadyPlayersUsernames() {
    ArrayList<String> result = new ArrayList<>();
    for (Player p : this.readyPlayers) {
      result.add(p.getUsername());
    }
    return result;
  }

  public ArrayList<String> getAlivePlayersUsernames() {
    ArrayList<String> result = new ArrayList<>();
    for (Player p : this.readyPlayers) {
      if (p.getIsAlive())
        result.add(p.getUsername());
    }
    return result;
  }

  public UserThread getUser(String username) {
    for (UserThread user : this.userThreads) {
      if (user.getUsername().equals(username))
        return user;

    }
    System.out.println(":::::: ERROR WHY NO NAME! ::::::");
    return null;
  }

  public Player getPlayerByUsername(String username) {
    for (Player p : this.readyPlayers) {
      if (p.getUsername().equals(username))
        return p;
    }
    return null;
  }

  public Player getMostVotedPlayer(HashMap<String, String> voteMap) {
    Collection<String> votes = voteMap.values();
    String[] voteList = votes.toArray(new String[votes.size()]);

    String vote = this.findMostFrequetString(voteList);
    if (vote.equals("{TIED}"))
      return null;
    return this.getPlayerByUsername(vote);
  }

  private String findMostFrequetString(String[] list) {
    HashMap<String, Integer> frequencyMap = this.createFrequencyMap(list);
    frequencyMap.remove("< ! >");
    ArrayList<Integer> values = new ArrayList<>();
    values.addAll(frequencyMap.values());
    if (values.size() == 0 || isVotingTied(values)) {
      return "{TIED}";
    }
    return this.getKeyOfBiggestValueInMap(frequencyMap);
  }

  private boolean isVotingTied(ArrayList<Integer> values) {
    int halfOfAlivePlayers = (int) Math.ceil(this.getAlivePlayersUsernames().size() / 2f);
    Collections.sort(values);
    int biggestVoteNumber = values.get(values.size() - 1);
    if (biggestVoteNumber >= halfOfAlivePlayers)
      return false;

    return values.get(0) == values.get(values.size() - 1);
  }

  private HashMap<String, Integer> createFrequencyMap(String[] list) {
    HashMap<String, Integer> result = new HashMap<>();
    for (String s : list) {
      Integer value = result.get(s);
      int nextValue = value == null ? 1 : value + 1;
      result.put(s, nextValue);
    }
    return result;
  }

  private String getKeyOfBiggestValueInMap(HashMap<String, Integer> map) {
    String biggestKey = "";
    int biggestValue = Integer.MIN_VALUE;

    for (Map.Entry<String, Integer> el : map.entrySet()) {
      String key = el.getKey();
      int value = el.getValue();
      if (value > biggestValue) {
        biggestValue = value;
        biggestKey = key;
      }
    }
    return biggestKey;
  }

  public void execute() {
    try (ServerSocket serverSocket = new ServerSocket(this.port)) {
      System.out.println("Chat Server Listening On [PORT]: " + this.port);
      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("New User Connected!");

        UserThread newUser = new UserThread(socket, this, this.dataReciever);

        this.userThreads.add(newUser);
        newUser.start();

      }
    } catch (IOException ex) {
      System.out.println("Server Error: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  public Player getPlayerByRole(ROLE role) {
    for (Player p : this.readyPlayers) {
      if (p.getRole() == role)
        return p;
    }
    return null;
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

    System.out.println("User [" + username + "] is realy ***** ready for the game.");
    this.readyPlayers.add(new Player(username, ROLE.CITIZEN, user));
  }

  public ArrayList<Player> getDeadPlayers() {
    ArrayList<Player> result = new ArrayList<>();
    for (Player p : this.readyPlayers) {
      if (!p.getIsAlive())
        result.add(p);
    }
    return result;
  }

  public boolean isPlayerInGame(ROLE role) {
    Player p = this.getPlayerByRole(role);
    return p != null && p.getIsAlive();
  }

  public ArrayList<String> getAliveMafiaUsernames() {
    ArrayList<String> result = new ArrayList<>();
    for (String username : this.getAlivePlayersUsernames()) {
      if (this.isMafia(username))
        result.add(username);
    }
    return result;
  }

  public ArrayList<String> getAliveCitizenUsernames() {
    ArrayList<String> result = new ArrayList<>();
    for (String username : this.getAlivePlayersUsernames()) {
      if (!this.isMafia(username))
        result.add(username);
    }
    return result;
  }

  public void killPlayer(Player p) {
    p.kill();
    ArrayList<String> aliveUsernames = this.getAlivePlayersUsernames();
    ArrayList<String> aliveMafiaUsernames = this.getAliveMafiaUsernames();
    ArrayList<String> aliveCitizenUsernamese = this.getAliveCitizenUsernames();

    this.getGameState().setAlivePlayerUsernames(aliveUsernames);
    this.getGameState().setAliveMafiaUsernames(aliveMafiaUsernames);
    this.getGameState().setAliveCitizenUsernames(aliveCitizenUsernamese);
    this.sendPlayerStateToClients();
  }

  public boolean isMafia(String username) {
    Player p = this.getPlayerByUsername(username);
    return p.getRole() == ROLE.GOD_FATHER || p.getRole() == ROLE.DR_LACTER || p.getRole() == ROLE.MAFIA_MEMBER;
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