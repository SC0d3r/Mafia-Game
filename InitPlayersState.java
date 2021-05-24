import java.util.ArrayList;
import java.util.Collections;

public class InitPlayersState extends ServerState {
  private SocketDataSender dataSender;
  private GameData gameData;

  public InitPlayersState(Narrator narrator, GameServer server, GameData data) {
    super(narrator, server);
    this.gameData = data;
    this.dataSender = new SocketDataSender();
  }

  @Override
  public boolean run() {
    this.setPlayersRoles();
    this.gameServer.sendPlayerStateToClients();
    this.sendGameStateToClients();
    this.createPlayersHeaderInfoBar();
    this.narrator.changeState(STATES.INTRODUCE_MAFIAS);
    return false;
  }

  private void sendGameStateToClients() {
    this.gameServer.getGameState().setAlivePlayerUsernames(this.gameServer.getAlivePlayersUsernames());
    this.gameServer.sendGameStateToClients();
  }

  private void createPlayersHeaderInfoBar() {
    for (Player p : this.gameServer.getReadyPlayers()) {
      String isAlive = this.dataSender.createInfo("Alive", p.getIsAlive() ? "YES" : "NO");
      String timeOfDay = this.dataSender.createInfo("TIME", DAYTIME.toString(this.gameData.getDayTime()));
      String role = this.dataSender.createInfo("ROLE", ROLE.toString(p.getRole()));

      p.sendMessage(isAlive);
      UTIL.sleep(40);
      p.sendMessage(timeOfDay);
      UTIL.sleep(40);
      p.sendMessage(role);
      UTIL.sleep(40);
      p.sendMessage(SocketDataSender.DISABLE_CHAT);
    }
  }

  private void setPlayersRoles() {
    ArrayList<ROLE> roles = this.createRoles();
    int i = 0;
    for (Player p : this.gameServer.getReadyPlayers()) {
      p.setRole(roles.get(i));
      i++;
    }
  }

  private ArrayList<ROLE> createRoles() {
    int howManyPlayers = this.gameServer.getReadyPlayers().size();
    ROLE[] allRoles = { ROLE.PROFESSIONAL, ROLE.GOD_FATHER, ROLE.PSYCHOLOGIST, ROLE.DETECTIVE, ROLE.DR_CITY, ROLE.MAYOR,
        ROLE.DR_LACTER, ROLE.DIE_HARD, ROLE.CITIZEN, ROLE.MAFIA_MEMBER };

    ArrayList<ROLE> result = new ArrayList<>();
    for (int i = 0; i < howManyPlayers; i++) {
      result.add(allRoles[i]);
    }
    Collections.shuffle(result);
    return result;
  }
}
