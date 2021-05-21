import java.util.ArrayList;
import java.util.Collections;

public class Narrator implements Runnable {
  private GameServer server;
  private GameData gameData;
  private SocketDataSender dataSender;
  private int dayCount;

  public Narrator(GameServer server, GameData gameData) {
    this.server = server;
    this.gameData = GameData.getInstance();
    this.dataSender = new SocketDataSender();
    this.dayCount = 0;
  }

  public void initPlayers() {
    this.setPlayersRoles();
    this.sendPlayerStateToClient();
    this.createPlayersHeaderInfoBar();
  }

  private void sendPlayerStateToClient() {
    for (Player p : this.server.getReadyPlayers()) {
      p.sendMessage(this.dataSender.createPlayerState(p));
    }
  }

  private void createPlayersHeaderInfoBar() {
    for (Player p : this.server.getReadyPlayers()) {
      String isAlive = this.dataSender.createInfo("Alive", p.getIsAlive() ? "YES" : "NO");
      String timeOfDay = this.dataSender.createInfo("TIME", DAYTIME.toString(this.gameData.getDayTime()));
      String role = this.dataSender.createInfo("ROLE", ROLE.toString(p.getRole()));

      p.sendMessage(isAlive);
      this.sleep(0, 0, 40);
      p.sendMessage(timeOfDay);
      this.sleep(0, 0, 40);
      p.sendMessage(role);
      this.sleep(0, 0, 40);
      p.sendMessage(SocketDataSender.DISABLE_CHAT);
    }
  }

  private boolean isThereMayorInGame() {
    for (Player p : this.server.getReadyPlayers()) {
      if (p.getRole() == ROLE.MAYOR)
        return true;
    }
    return false;
  }

  private void setPlayersRoles() {
    ArrayList<ROLE> roles = this.createRoles();
    int i = 0;
    for (Player p : this.server.getReadyPlayers()) {
      p.setRole(roles.get(i));
      i++;
    }
  }

  private void updateDayTime(DAYTIME dt) {
    this.gameData.updateDayTime(dt);
    String timeOfDay = this.dataSender.createInfo("TIME", DAYTIME.toString(this.gameData.getDayTime()));
    for (Player p : this.server.getReadyPlayers()) {
      p.sendMessage(timeOfDay);
    }
  }

  private ArrayList<ROLE> createRoles() {
    int howManyPlayers = this.server.getReadyPlayers().size();
    ROLE[] allRoles = { ROLE.GOD_FATHER, ROLE.MAYOR, ROLE.DR_CITY, ROLE.DETECTIVE, ROLE.PROFESSIONAL, ROLE.PSYCHOLOGIST,
        ROLE.DR_LACTER, ROLE.DIE_HARD, ROLE.CITIZEN, ROLE.MAFIA_MEMBER };

    ArrayList<ROLE> result = new ArrayList<>();
    for (int i = 0; i < howManyPlayers; i++) {
      result.add(allRoles[i]);
    }
    Collections.shuffle(result);
    return result;
  }

  public void introduceMafiaToTheirTeammate() {
    ArrayList<Player> mafias = this.getMafias();
    ArrayList<String> messages = this.createIntroductionMessages(mafias);
    this.broadcast(String.join("\n", messages), mafias);
  }

  private void broadcast(String message, ArrayList<Player> players) {
    for (Player p : players)
      p.sendMessage(message);
  }

  private ArrayList<String> createIntroductionMessages(ArrayList<Player> mafias) {
    ArrayList<String> messages = new ArrayList<>();
    for (Player p : mafias) {
      if (p.getRole() == ROLE.GOD_FATHER)
        messages.add("God-Father: " + p.getUsername());

      else if (p.getRole() == ROLE.DR_LACTER)
        messages.add("Dr.Lacter: " + p.getUsername());
      else
        messages.add("Mafia-Member: " + p.getUsername());
    }
    return messages;
  }

  public Player fetchPlayer(ROLE role) {
    Player result = null;
    for (Player p : this.server.getReadyPlayers()) {
      if (p.getRole() == role)
        result = p;
      break;
    }
    return result;
  }

  public void introduceDRToMayor() {
    Player drCity = fetchPlayer(ROLE.DR_CITY);
    Player mayor = fetchPlayer(ROLE.MAYOR);
    if (drCity == null || mayor == null)
      return;

    String messageForMayor = this.dataSender.createChatCommand("Dr.City is " + drCity.getUsername());
    String messageForDrCity = this.dataSender.createChatCommand("Mayor is " + mayor.getUsername());

    drCity.sendMessage(messageForDrCity);
    mayor.sendMessage(messageForMayor);

  }

  public ArrayList<Player> getMafias() {
    ArrayList<Player> result = new ArrayList<>();
    for (Player p : this.server.getReadyPlayers()) {
      if (p.getRole() == ROLE.GOD_FATHER || p.getRole() == ROLE.MAFIA_MEMBER || p.getRole() == ROLE.DR_LACTER)
        result.add(p);
    }
    return result;
  }

  public boolean isIntroductionNight() {
    return this.dayCount == 0;
  }

  private void announceCitizensWinner() {
    for (Player p : this.server.getReadyPlayers()) {
      p.sendMessage(this.dataSender.createChatCommand("      CITIZENS WON THE GAME"));
    }
  }

  private void announceMafiaWinner() {
    for (Player p : this.server.getReadyPlayers()) {
      p.sendMessage(this.dataSender.createChatCommand("      MAFIAS WON THE GAME"));
    }
  }

  public void beginDayNightCycle(int beginDayNumber) {
    while (true) {
      if (this.didMafiaWin()) {
        this.announceMafiaWinner();
        this.sleep(0, 30, 0);
        System.exit(0);
        return;
      }
      if (this.didCitizenWin()) {
        this.announceCitizensWinner();
        this.sleep(0, 30, 0);
        System.exit(0);
        return;
      }

      if (this.gameData.isDay())
        this.runDayCycle();
      else
        this.runNightCycle();

      // this.gameData.setIsVotingGotCanceled(false);// reseting mayour decision to
      // false
      this.sendPlayerStateToClient();
    }
  }

  private void runNightCycle() {
    // this.clearNews();
    this.setTimerFor(20);
    this.updateDayTime(DAYTIME.DAY);
    this.sendNews();
    this.dayCount++;
  }

  private void runDayCycle() {
    // this.clearNews();
    this.enableChat();
    this.setTimerFor(10);
    this.sleep(0, 1, 0);
    this.saveAndClearChatMessages();
    this.startVotingSession();
    this.sendNews();
  }

  private void saveAndClearChatMessages() {
    String cmd = SocketDataSender.SAVE_AND_CLEAR_CHAT;
    for (Player p : this.server.getReadyPlayers()) {
      p.sendMessage(cmd);
    }
  }

  private void startVotingSession() {
    this.disableChat();
    this.enableOnlyVotingChat(this.server.getAlivePlayersUsernames());
    this.setTimerFor(20);
    this.sleep(0, 1, 0);
    this.disableVoting();
    this.enableMayorDecisioning();
    this.updateDayTime(DAYTIME.NIGHT);
  }

  private void setTimerFor(int seconds) {
    new UpdateTimer(seconds, this.server.getReadyPlayers()).run();
  }

  private void enableMayorDecisioning() {
    if (this.isThereMayorInGame()) {
      this.broadcast(SocketDataSender.START_MAYOR_VOTING_STATE, this.server.getReadyPlayers());
      this.setTimerFor(10);
    }

    if (this.isVotingGotCanceledByMayor()) {
      this.gameData.addNews("Voting got canceled by Mayor");
    } else {
      Player mostVotedPlayer = this.server.getMostVotedPlayer();
      if (mostVotedPlayer != null) {
        mostVotedPlayer.kill();
        this.gameData.addNews("<" + mostVotedPlayer.getUsername() + "> voted out!");
      } else
        this.gameData.addNews("WHAT HAPPEND");
    }

    this.broadcast(SocketDataSender.END_MAYOR_VOTING_STATE, this.server.getReadyPlayers());
  }

  private boolean isVotingGotCanceledByMayor() {
    return this.gameData.getIsVotingGotCanceled();
  }

  private void disableVoting() {
    for (Player p : this.server.getReadyPlayers()) {
      p.sendMessage(SocketDataSender.DISABLE_VOTING);
    }
  }

  private void sendNews() {
    if (!this.gameData.isThereAnyNews())
      return;
    ArrayList<String> news = this.gameData.getNews();
    this.broadcast(this.dataSender.createNews(news), this.server.getReadyPlayers());
    // this.gameData.clearNews();
    this.clearNews();
  }

  private void enableOnlyVotingChat(ArrayList<String> choices) {
    for (Player p : this.server.getReadyPlayers()) {
      p.sendMessage(SocketDataSender.ENABLE_VOTING);
      p.sendMessage(this.dataSender.createVotingTable(choices));
    }

  }

  private void enableChat() {

    for (Player p : this.server.getReadyPlayers()) {
      p.setCanChat(true);
      // p.sendMessage(SocketDataSender.ENABLE_CHAT);
    }

    this.sendPlayerStateToClient();
  }

  private void disableChat() {
    // System.out.println("disabling chat for all players");
    for (Player p : this.server.getReadyPlayers()) {
      p.setCanChat(false);
      // p.sendMessage(SocketDataSender.DISABLE_CHAT);
    }
    this.sendPlayerStateToClient();
  }

  public boolean didMafiaWin() {
    return this.getMafias().size() >= this.getCitizens().size();
  }

  public boolean didCitizenWin() {
    return this.getMafias().size() == 0;
  }

  public ArrayList<Player> getCitizens() {
    ArrayList<Player> result = new ArrayList<>();
    for (Player p : this.server.getReadyPlayers()) {
      if (p.getRole() == ROLE.GOD_FATHER || p.getRole() == ROLE.DR_LACTER || p.getRole() == ROLE.MAFIA_MEMBER)
        continue;
      result.add(p);
    }
    return result;
  }

  @Override
  public void run() {
    // this.server.setIsGameStarted(true);
    // 1 - give each player a role
    this.initPlayers();
    // 2 - init game data
    // 3 - awakes each mafia players to recognize their teammates
    // First Night(Meeting)
    this.introduceMafiaToTheirTeammate();
    this.introduceDRToMayor();

    this.sleep(0, 3, 0);
    this.updateDayTime(DAYTIME.DAY);
    // this.gameData.updateDayTime(DAYTIME.DAY);
    this.beginDayNightCycle(this.dayCount);
    // 4 - normal night > 1- awakes 1-mafia 2- dr.lakter 3- dr.city 4- detective
    // 5-killer 6-ravanshenas 7-diehard

    // 5 - normal day -tell which players left the game then everyone can talk(is
    // awake) and give info to all players
    // about player who got killed or left the game

    // 6 - voting(30 seconds) asks mayor if he wants to cancel the voting

    // 7 - end game condition if #mafia >= #citizens => mafia won the game if #mafia
    // = 0 => citizens won the game
  }

  private void clearNews() {
    this.gameData.clearNews();
    // this.broadcast(SocketDataSender.CLEAR_NEWS, this.server.getReadyPlayers());
  }

  private void sleep(int min, int sec, int ms) {
    try {
      Thread.sleep(min * 1000 * 60 + 1000 * sec + ms);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

}
