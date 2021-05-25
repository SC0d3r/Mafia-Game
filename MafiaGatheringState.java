public class MafiaGatheringState extends ServerState {

  public MafiaGatheringState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    this.gameServer.getGameState().setIsInMafiaGatheringState(true);
    this.gameServer.sendGameStateToClients();

    UTIL.setTimerFor(20, this.gameServer.getReadyPlayers());

    this.gameServer.getGameState().setIsInMafiaGatheringState(false);
    this.narrator.broadcast(SocketDataSender.SAVE_AND_CLEAR_CHAT, this.gameServer.getReadyPlayers());
    this.gameServer.sendGameStateToClients();
    this.narrator.changeState(STATES.GOD_FATHER);
    return false;
  }

}
