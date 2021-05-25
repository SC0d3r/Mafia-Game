public class DetectiveState extends ServerState {

  private GameData gameData;

  public DetectiveState(Narrator narrator, GameServer server, GameData gameData) {
    super(narrator, server);
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    if (!this.gameServer.isPlayerInGame(ROLE.DETECTIVE)) {
      this.narrator.changeState(STATES.DIEHARD);
      return false;
    }

    this.gameServer.getGameState().setIsInDetectiveState(true);
    this.gameServer.sendGameStateToClients();
    UTIL.setTimerFor(20, this.gameServer.getReadyPlayers());

    String suspectUsername = this.gameServer.getGameState().getDetectiveSuspicionTarget();
    if (!suspectUsername.isBlank()) {
      Player susPlayer = this.gameServer.getPlayerByUsername(suspectUsername);
      if (this.gameServer.isMafia(suspectUsername) && susPlayer.getRole() != ROLE.GOD_FATHER) {
        this.gameData.addNews("Detective query: " + suspectUsername + " is a Mafia!");
      } else
        this.gameData.addNews("Dear detective I dont know that player!");
    }

    this.gameServer.getGameState().setDetectiveSuspicionTarget("");
    this.gameServer.getGameState().setIsInDetectiveState(false);
    this.gameServer.sendGameStateToClients();

    this.narrator.changeState(STATES.DIEHARD);
    return false;
  }

}
