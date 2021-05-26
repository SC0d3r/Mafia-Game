/**
 * this class will gets called when the mafias are the winner of the game
 */
public class MafiaWinnerState extends ServerState {
  private GameData gameData;

  public MafiaWinnerState(Narrator narrator, GameServer server, GameData gameData) {
    super(narrator, server);
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    this.gameData.setDidMafiaWin(true);

    UTIL.setTimerFor(2, this.gameServer.getReadyPlayers());

    return true;// means game is finished
  }

}
