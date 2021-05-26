/**
 * this class will play the role of god father in game and asks the god father
 * if he wants to eliminate someone
 */
public class GodFatherState extends ServerState {

  public GodFatherState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    if (!this.gameServer.isPlayerInGame(ROLE.GOD_FATHER)) {
      this.narrator.changeState(STATES.DR_LACTER);
      return false;
    }

    this.gameServer.getGameState().setIsInGodFatherState(true);
    this.gameServer.sendGameStateToClients();

    UTIL.setTimerFor(20, this.gameServer.getReadyPlayers());

    this.gameServer.getGameState().setIsInGodFatherState(false);
    this.gameServer.sendGameStateToClients();
    this.narrator.changeState(STATES.DR_LACTER);
    return false;
  }

}
