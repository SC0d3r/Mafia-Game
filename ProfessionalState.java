public class ProfessionalState extends ServerState {

  private GameData gameData;

  public ProfessionalState(Narrator narrator, GameServer server, GameData gameData) {
    super(narrator, server);
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    if (!this.gameServer.isPlayerInGame(ROLE.PROFESSIONAL)) {
      this.narrator.changeState(STATES.DETECTIVE);
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
    this.narrator.changeState(STATES.DETECTIVE);
    return false;
  }

  private void checkProfessionalTarget(String professionalTarget) {
    if (this.isTargetACitizen(professionalTarget)) {
      this.gameServer.getPlayerByRole(ROLE.PROFESSIONAL).kill();
      this.gameServer.sendPlayerStateToClients();
      this.gameData.addNews("Professional got kicked out! for a wrong shot :D ");
      return;
    }
    Player target = this.gameServer.getPlayerByUsername(professionalTarget);
    target.kill();
    this.gameServer.sendPlayerStateToClients();
    this.gameData.addNews(target.getUsername() + " Got killed [X_x] by professional last night.");
  }

  private boolean isTargetACitizen(String targetUsername) {
    return !this.gameServer.isMafia(targetUsername);
    // Player target = this.gameServer.getPlayerByUsername(targetUsername);
    // return target.getRole() != ROLE.GOD_FATHER && target.getRole() !=
    // ROLE.DR_LACTER
    // && target.getRole() != ROLE.MAFIA_MEMBER;
  }

}
