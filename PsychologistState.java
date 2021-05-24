public class PsychologistState extends ServerState {

  public PsychologistState(Narrator narrator, GameServer server, SocketDataSender dataSender) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    if (this.isTherePsychologistInGame()) {
      this.gameServer.getGameState().setIsInPsychologistState(true);
      this.gameServer.sendGameStateToClients();
      UTIL.setTimerFor(10, this.gameServer.getReadyPlayers());
      this.gameServer.getGameState().setIsInPsychologistState(false);
      // UTIL.sleep(20);
      this.gameServer.sendGameStateToClients();
      // UTIL.sleep(20);
    }
    this.narrator.changeState(STATES.BEGIN_DAY);
    return false;
  }

  private boolean isTherePsychologistInGame() {
    return this.gameServer.getPlayerByRole(ROLE.PSYCHOLOGIST) != null;
  }

}
