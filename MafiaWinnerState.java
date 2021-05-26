public class MafiaWinnerState extends ServerState {
  private SocketDataSender dataSender;
  private GameData gameData;

  public MafiaWinnerState(Narrator narrator, GameServer server, SocketDataSender dataSender, GameData gameData) {
    super(narrator, server);
    this.dataSender = dataSender;
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    // this.announceMafiaWinner();
    this.gameData.setDidMafiaWin(true);

    // UTIL.setTimerFor(10, this.gameServer.getReadyPlayers());
    return true;// means game is finished
  }

  // private void announceMafiaWinner() {
  // String message = this.dataSender.createChatCommand(" MAFIA WON THE GAME ");
  // this.narrator.broadcast(message, this.gameServer.getReadyPlayers());
  // }

}
