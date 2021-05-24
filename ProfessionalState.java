public class ProfessionalState extends ServerState {

  private GameData gameData;

  public ProfessionalState(Narrator narrator, GameServer server, GameData gameData) {
    super(narrator, server);
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    this.gameServer.getGameState().setIsInProfessionalState(true);
    this.gameServer.sendGameStateToClients();
    UTIL.setTimerFor(20, this.gameServer.getReadyPlayers());

    String professionalTarget = this.gameServer.getGameState().getProfessionalTarget();
    this.checkProfessionalTarget(professionalTarget);

    this.gameServer.getGameState().setIsInProfessionalState(false);
    this.gameServer.getGameState().setProfessionalTarget("");
    this.gameServer.sendGameStateToClients();
    this.narrator.changeState(STATES.BEGIN_DAY);
    return false;
  }

  private void checkProfessionalTarget(String professionalTarget) {
    if (professionalTarget.isBlank())
      // didnt chose anyone
      return;
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
    Player target = this.gameServer.getPlayerByUsername(targetUsername);
    return target.getRole() != ROLE.GOD_FATHER && target.getRole() != ROLE.DR_LACTER
        && target.getRole() != ROLE.MAFIA_MEMBER;
  }

}
