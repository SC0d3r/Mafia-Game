public class DRLacterState extends ServerState {
  public DRLacterState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    if (!this.gameServer.isPlayerInGame(ROLE.DR_LACTER)) {
      this.narrator.changeState(STATES.MAYOR_DECISIONING);
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
