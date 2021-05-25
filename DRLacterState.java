public class DRLacterState extends ServerState {
  private GameData gameData;

  public DRLacterState(Narrator narrator, GameServer server, GameData gameData) {
    super(narrator, server);
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    if (!this.gameServer.isPlayerInGame(ROLE.DR_LACTER)) {
      this.narrator.changeState(STATES.MAYOR_DECISIONING);
      return false;
    }
    this.gameServer.getGameState().setIsInDrLacterState(true);
    this.gameServer.sendGameStateToClients();

    UTIL.setTimerFor(20, this.gameServer.getReadyPlayers());

    this.checkIsDrLacterSavingHimself();

    this.gameServer.getGameState().setIsInDrLacterState(false);
    this.gameServer.sendGameStateToClients();
    this.narrator.changeState(STATES.MAYOR_DECISIONING);
    return false;
  }

  private void checkIsDrLacterSavingHimself() {
    String drLacterUsername = this.gameServer.getPlayerByRole(ROLE.DR_LACTER).getUsername();
    String curesUsername = this.gameServer.getGameState().getDrLacterCuresUsername();
    if (curesUsername.equals(drLacterUsername)) {
      this.gameServer.getGameState().setIsDrLacterSavedHimselfAllready(true);
    }
  }

}
