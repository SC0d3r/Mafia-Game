public class PsychologistState extends ServerState {

  public PsychologistState(Narrator narrator, GameServer server, SocketDataSender dataSender) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    if (!this.gameServer.isPlayerInGame(ROLE.PSYCHOLOGIST)) {
      this.narrator.changeState(STATES.DIEHARD);
      return false;
    }

    this.gameServer.getGameState().setIsInPsychologistState(true);
    this.gameServer.sendGameStateToClients();
    UTIL.setTimerFor(10, this.gameServer.getReadyPlayers());
    this.gameServer.getGameState().setIsInPsychologistState(false);
    this.gameServer.sendGameStateToClients();
    this.narrator.changeState(STATES.DIEHARD);
    return false;
  }

}
