public class CitizenWinnerState extends ServerState {

  private SocketDataSender dataSender;
  private GameData gameData;

  public CitizenWinnerState(Narrator narrator, GameServer server, SocketDataSender dataSender, GameData gameData) {
    super(narrator, server);
    this.dataSender = dataSender;
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    // this.announceCitizensWinner();
    this.gameData.setDidMafiaWin(false);

    // UTIL.setTimerFor(10, this.gameServer.getReadyPlayers());
    return true;// means game is finished
  }

  // private void announceCitizensWinner() {
  // String message = this.dataSender.createChatCommand(" CITIZEN WON THE GAME ");
  // this.narrator.broadcast(message, this.gameServer.getReadyPlayers());
  // }
}
