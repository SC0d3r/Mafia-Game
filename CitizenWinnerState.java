public class CitizenWinnerState extends GameState {

  private SocketDataSender dataSender;

  public CitizenWinnerState(Narrator narrator, GameServer server, SocketDataSender dataSender) {
    super(narrator, server);
    this.dataSender = dataSender;
  }

  @Override
  public boolean run() {
    this.announceCitizensWinner();
    // TODO: add winner to game data

    UTIL.sleepMSM(0, 30, 0);
    return true;// means game is finished
  }

  private void announceCitizensWinner() {

    String message = this.dataSender.createChatCommand(" &*&( CITIZEN WON THE GAME )&*&");
    this.narrator.broadcast(message, this.gameServer.getReadyPlayers());
  }
}
