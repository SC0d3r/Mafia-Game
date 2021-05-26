/**
 * this class checks to see if dr lacter is present asks him which mafia players
 * that he wants to save from professional , Then he can type the name of the
 * mafia player. He can only save himself once.
 */
public class DRLacterState extends ServerState {
  public DRLacterState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    if (!this.gameServer.isPlayerInGame(ROLE.DR_LACTER)) {
      this.narrator.changeState(STATES.DR_CITY);
      return false;
    }
    this.gameServer.getGameState().setIsInDrLacterState(true);
    this.gameServer.sendGameStateToClients();

    UTIL.setTimerFor(20, this.gameServer.getReadyPlayers());

    this.gameServer.getGameState().setIsInDrLacterState(false);
    this.gameServer.sendGameStateToClients();
    this.narrator.changeState(STATES.DR_CITY);
    return false;
  }

}
