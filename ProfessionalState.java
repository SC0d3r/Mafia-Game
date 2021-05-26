/**
 * this class will check if there is a professional in the game and if there is
 * one , Asks him if he wants to kill a player if he kills a citizen he will get
 * killed instead
 */
public class ProfessionalState extends ServerState {

  private GameData gameData;

  public ProfessionalState(Narrator narrator, GameServer server, GameData gameData) {
    super(narrator, server);
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    if (!this.gameServer.isPlayerInGame(ROLE.PROFESSIONAL)) {
      this.narrator.changeState(STATES.PSYCHOLOGIST);
      return false;
    }

    this.gameServer.getGameState().setIsInProfessionalState(true);
    this.gameServer.sendGameStateToClients();
    UTIL.setTimerFor(20, this.gameServer.getReadyPlayers());

    String professionalTarget = this.gameServer.getGameState().getProfessionalTarget();
    if (!professionalTarget.isBlank())
      this.checkProfessionalTarget(professionalTarget);

    this.gameServer.getGameState().setIsInProfessionalState(false);
    this.gameServer.getGameState().setProfessionalTarget("");
    this.gameServer.sendGameStateToClients();
    this.narrator.changeState(STATES.PSYCHOLOGIST);
    return false;
  }

  private void checkProfessionalTarget(String professionalTarget) {
    if (this.isTargetACitizen(professionalTarget)) {
      Player professional = this.gameServer.getPlayerByRole(ROLE.PROFESSIONAL);
      this.gameServer.killPlayer(professional);
      this.gameServer.sendPlayerStateToClients();
      this.gameData.addNews("Professional got kicked out! for a wrong shot :D ");
      return;
    }
    Player target = this.gameServer.getPlayerByUsername(professionalTarget);
    String drLacterCuresUsername = this.gameServer.getGameState().getDrLacterCuresUsername();
    this.gameServer.getGameState().setDrLacterCuresUsername("");// reseting

    if (target.getUsername().equals(drLacterCuresUsername)) {
      if (this.gameServer.getPlayerByRole(ROLE.DR_LACTER).getUsername().equals(drLacterCuresUsername)) {
        this.gameServer.getGameState().setIsDrLacterSavedHimselfAllready(true);
      }
      this.gameData.addNews("[+] DR.Lacter saved a mafia player!");
    } else {
      this.gameServer.killPlayer(target);
      this.gameData.addNews(target.getUsername() + "[X_x] Got killed by professional last night.");
    }

    this.gameServer.sendPlayerStateToClients();
  }

  private boolean isTargetACitizen(String targetUsername) {
    return !this.gameServer.isMafia(targetUsername);
  }

}
