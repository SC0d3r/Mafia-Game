public class GodFatherState extends ServerState {

  public GodFatherState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    this.gameServer.getGameState().setIsInGodFatherState(true);
    this.gameServer.sendGameStateToClients();

    UTIL.setTimerFor(20, this.gameServer.getReadyPlayers());

    this.gameServer.getGameState().setIsInGodFatherState(false);
    this.gameServer.sendGameStateToClients();
    this.narrator.changeState(STATES.DR_LACTER);
    return false;
  }

}
