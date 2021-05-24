import java.util.HashMap;

public class MayorDecisioningState extends ServerState {

  private GameData gameData;

  public MayorDecisioningState(Narrator narrator, GameServer server, GameData gameData) {
    super(narrator, server);
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    HashMap<String, String> votes = this.gameServer.getGameState().getVotes();
    this.gameServer.getGameState().clearVotes();
    if (this.isThereMayorInGame()) {
      this.gameServer.getGameState().setIsInMayorState(true);
      this.gameServer.sendGameStateToClients();
      UTIL.setTimerFor(10, this.gameServer.getReadyPlayers());
    }

    if (this.isVotingGotCanceledByMayor()) {
      this.gameData.addNews("Voting got canceled by Mayor");
    } else {
      Player mostVotedPlayer = this.gameServer.getMostVotedPlayer(votes);
      if (mostVotedPlayer != null) {
        mostVotedPlayer.kill();
        this.gameServer.getGameState().setAlivePlayerUsernames(this.gameServer.getAlivePlayersUsernames());
        this.gameData.addNews("<" + mostVotedPlayer.getUsername() + "> voted out!");
      }
      // else
      // this.gameData.addNews("Nothing happend!");
    }

    if (this.isThereMayorInGame())
      this.gameServer.getGameState().setIsInMayorState(false);
    this.gameServer.sendGameStateToClients();
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
