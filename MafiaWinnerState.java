public class MafiaWinnerState extends GameState {
  private SocketDataSender dataSender;

  public MafiaWinnerState(Narrator narrator, GameServer server, SocketDataSender dataSender) {
    super(narrator, server);
    this.dataSender = dataSender;
  }

  @Override
  public boolean run() {
    this.announceMafiaWinner();
    // TODO: add winner to game data

    UTIL.sleepMSM(0, 30, 0);
    return true;// means game is finished
  }

  private void announceMafiaWinner() {
    String message = this.dataSender.createChatCommand(" &*&( MAFIA WON THE GAME )&*&");
    this.narrator.broadcast(message, this.gameServer.getReadyPlayers());
  }

}
