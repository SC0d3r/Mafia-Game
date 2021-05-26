import java.util.HashMap;

/**
 * this class will gets called when there is a mayor in game and asks him if he
 * wants to disable the voting
 */
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
    if (this.gameServer.isPlayerInGame(ROLE.MAYOR)) {
      // if (this.isThereMayorInGame() ) {
      this.gameServer.getGameState().setIsInMayorState(true);
      this.gameServer.sendGameStateToClients();
      UTIL.setTimerFor(10, this.gameServer.getReadyPlayers());
    }

    if (this.isVotingGotCanceledByMayor()) {
      this.gameData.addNews("Voting got canceled by Mayor");
    } else {
      Player mostVotedPlayer = this.gameServer.getMostVotedPlayer(votes);
      if (mostVotedPlayer != null) {
        this.gameServer.killPlayer(mostVotedPlayer);
        this.gameData.addNews("[-] " + mostVotedPlayer.getUsername() + " voted out!");
      }
    }

    this.gameServer.getGameState().setIsInMayorState(false);
    this.gameServer.sendGameStateToClients();
    this.narrator.changeState(STATES.BEGIN_NIGHT);
    return false;
  }

  private boolean isVotingGotCanceledByMayor() {
    return this.gameData.getIsVotingGotCanceled();
  }
}
