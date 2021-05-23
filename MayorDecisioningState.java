public class MayorDecisioningState extends GameState {

  private GameData gameData;

  public MayorDecisioningState(Narrator narrator, GameServer server, GameData gameData) {
    super(narrator, server);
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    if (this.isThereMayorInGame()) {
      this.narrator.broadcast(SocketDataSender.START_MAYOR_VOTING_STATE, this.gameServer.getReadyPlayers());
      this.narrator.setTimerFor(10);
    }

    if (this.isVotingGotCanceledByMayor()) {
      this.gameData.addNews("Voting got canceled by Mayor");
    } else {
      Player mostVotedPlayer = this.gameServer.getMostVotedPlayer();
      if (mostVotedPlayer != null) {
        mostVotedPlayer.kill();
        this.gameData.addNews("<" + mostVotedPlayer.getUsername() + "> voted out!");
      } else
        this.gameData.addNews("Nothing happend!");
    }

    this.narrator.broadcast(SocketDataSender.END_MAYOR_VOTING_STATE, this.gameServer.getReadyPlayers());
    this.narrator.changeState(STATES.BEGIN_NIGHT);
    return false;
  }

  private boolean isVotingGotCanceledByMayor() {
    return this.gameData.getIsVotingGotCanceled();
  }

  private boolean isThereMayorInGame() {
    return this.gameServer.getPlayerByRole(ROLE.MAYOR) != null;
  }
}
