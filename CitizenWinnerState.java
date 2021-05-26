public class CitizenWinnerState extends ServerState {
  private GameData gameData;

  public CitizenWinnerState(Narrator narrator, GameServer server, GameData gameData) {
    super(narrator, server);
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    this.gameData.setDidMafiaWin(false);

    UTIL.setTimerFor(2, this.gameServer.getReadyPlayers());

    return true;// means game is finished
  }
}
