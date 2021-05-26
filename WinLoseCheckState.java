/**
 * this class will gets called whenever the night ends to check if the game is
 * finished. The game finishes whenever the # of mafia players are equel to # of
 * citizens or the # of mafia players reaches 0
 */
public class WinLoseCheckState extends ServerState {

  public WinLoseCheckState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    if (this.didMafiaWin()) {
      this.narrator.changeState(STATES.MAFIA_WINNER);
      return false;
    }
    if (this.didCitizenWin()) {
      this.narrator.changeState(STATES.CITIZEN_WINNER);
      return false;
    }

    this.narrator.changeState(STATES.ENABLE_CHAT);
    return false;
  }

  public boolean didCitizenWin() {
    return this.gameServer.getGameState().getAliveMafiaUsernames().size() == 0;
  }

  private boolean didMafiaWin() {
    return this.gameServer.getGameState().getAliveMafiaUsernames().size() >= this.gameServer.getGameState()
        .getAliveCitizenUsernames().size();
  }

}
