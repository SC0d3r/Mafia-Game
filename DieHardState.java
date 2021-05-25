import java.util.ArrayList;
import java.util.Collections;

public class DieHardState extends ServerState {
  private GameData gameData;

  public DieHardState(Narrator narrator, GameServer server, GameData gameData) {
    super(narrator, server);
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    if (!this.gameServer.isPlayerInGame(ROLE.DIE_HARD)) {
      this.narrator.changeState(STATES.BEGIN_DAY);
      return false;
    }

    this.gameServer.getGameState().setIsInDieHardState(true);
    this.gameServer.sendGameStateToClients();

    UTIL.setTimerFor(20, this.gameServer.getReadyPlayers());

    if (this.gameServer.getGameState().getIsDieHardRequestedInvestigation()) {
      ArrayList<Player> deadPlayers = this.gameServer.getDeadPlayers();
      Collections.shuffle(deadPlayers);
      ArrayList<String> deadPlayerRoles = this.getDeadPlayerRoles(deadPlayers);
      this.gameData.addNews("Dead Player(s) were: " + String.join(", ", deadPlayerRoles));
    }
    this.gameServer.getGameState().setIsDieHardRequestedInvestigation(false);
    this.gameServer.getGameState().setIsInDieHardState(false);
    this.gameServer.sendGameStateToClients();

    this.narrator.changeState(STATES.BEGIN_DAY);
    return false;
  }

  private ArrayList<String> getDeadPlayerRoles(ArrayList<Player> deadPlayers) {
    ArrayList<String> roles = new ArrayList<>();
    for (Player p : deadPlayers) {
      roles.add(ROLE.toString(p.getRole()));
    }
    return roles;
  }

}
