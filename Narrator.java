import java.util.ArrayList;
import java.util.Collections;

// public class Narrator extends Thread {

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
    ArrayList<ROLE> roles = this.createRoles(this.server.getReadyPlayers().size());
    int i = 0;
    for (Player p : this.server.getReadyPlayers()) {
      p.setRole(roles.get(i));
      i++;
    }
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
      // p.sendMessage(SocketDataSender.DISABLE_CHAT);
    }
  }

  private void updateDayTime(DAYTIME dt) {
    this.gameData.updateDayTime(dt);
    String timeOfDay = this.dataSender.createInfo("TIME", DAYTIME.toString(this.gameData.getDayTime()));
    for (Player p : this.server.getReadyPlayers()) {
      p.sendMessage(timeOfDay);
    }
  }

  private ArrayList<ROLE> createRoles(int howManyPlayers) {
    ROLE[] allRoles = { ROLE.GOD_FATHER, ROLE.DR_CITY, ROLE.DETECTIVE, ROLE.PROFESSIONAL, ROLE.PSYCHOLOGIST,
        ROLE.DR_LACTER, ROLE.MAYOR, ROLE.DIE_HARD, ROLE.CITIZEN, ROLE.MAFIA_MEMBER };

    ArrayList<ROLE> result = new ArrayList<>();
    for (int i = 0; i < howManyPlayers; i++) {
      result.add(allRoles[i]);
    }
    Collections.shuffle(result);
    return result;
  }

  public void introduceMafiaToTheirTeammate() {
    ArrayList<Player> mafias = this.getMafias();
    ArrayList<String> messages = new ArrayList<>();
    for (Player p : mafias) {
      if (p.getRole() == ROLE.GOD_FATHER)
        messages.add("God-Father: " + p.getUsername());

      else if (p.getRole() == ROLE.DR_LACTER)
        messages.add("Dr.Lacter: " + p.getUsername());
      else
        messages.add("Mafia-Member: " + p.getUsername());
    }
    for (Player p : mafias) {
      p.sendMessage(String.join("\n", messages));
    }
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

  public void beginDayNightCycle() {
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

      if (this.gameData.getDayTime() == DAYTIME.DAY)
        this.runDayCycle();
      else
        this.runNightCycle();

    }
  }

  private void runNightCycle() {
    new UpdateTimer(20, this.server.getReadyPlayers()).run();
    this.updateDayTime(DAYTIME.DAY);
    this.dayCount++;
  }

  private void runDayCycle() {
    this.enableChat();
    UpdateTimer timer = new UpdateTimer(2 * 60, this.server.getReadyPlayers());
    timer.run();
    this.sleep(0, 1, 0);
    this.saveAndClearChatMessages();
    this.votingCycle();
  }

  private void saveAndClearChatMessages() {
    String cmd = SocketDataSender.SAVE_AND_CLEAR_CHAT;
    for (Player p : this.server.getReadyPlayers()) {
      p.sendMessage(cmd);
    }
  }

  private void votingCycle() {
    this.disableChat();
    new UpdateTimer(30, this.server.getReadyPlayers()).run();
    this.sleep(0, 1, 0);
    this.updateDayTime(DAYTIME.NIGHT);
  }
  // TODO: fix the bug : the last person who types !ready cant type in chat!

  private void enableChat() {
    for (Player p : this.server.getReadyPlayers()) {
      p.sendMessage(SocketDataSender.ENABLE_CHAT);
    }
  }

  private void disableChat() {
    // System.out.println("disabling chat for all players");
    for (Player p : this.server.getReadyPlayers()) {
      p.sendMessage(SocketDataSender.DISABLE_CHAT);
    }
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
    this.server.setIsGameStarted(true);
    // 1 - give each player a role
    this.initPlayers();
    // 2 - init game data
    // 3 - awakes each mafia players to recognize their teammates
    // First Night(Meeting)
    this.introduceMafiaToTheirTeammate();
    this.introduceDRToMayor();

    this.sleep(0, 3, 0);
    this.gameData.updateDayTime(DAYTIME.DAY);
    this.beginDayNightCycle();
    // 4 - normal night > 1- awakes 1-mafia 2- dr.lakter 3- dr.city 4- detective
    // 5-killer 6-ravanshenas 7-diehard

    // 5 - normal day -tell which players left the game then everyone can talk(is
    // awake) and give info to all players
    // about player who got killed or left the game

    // 6 - voting(30 seconds) asks mayor if he wants to cancel the voting

    // 7 - end game condition if #mafia >= #citizens => mafia won the game if #mafia
    // = 0 => citizens won the game
  }

  private void sleep(int min, int sec, int ms) {
    try {
      Thread.sleep(min * 1000 * 60 + 1000 * sec + ms);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

}
