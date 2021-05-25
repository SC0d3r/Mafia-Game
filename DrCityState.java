public class DrCityState extends ServerState {
  private GameData gameData;

  public DrCityState(Narrator narrator, GameServer server, GameData gameData) {
    super(narrator, server);
    this.gameData = gameData;
  }

  @Override
  public boolean run() {
    String godFatherTarget = this.gameServer.getGameState().getGodFatherTargetUsername();
    this.gameServer.getGameState().setGodFatherTargetUsername(""); // reseting
    Player target = this.gameServer.getPlayerByUsername(godFatherTarget);
    if (!this.gameServer.isPlayerInGame(ROLE.DR_CITY)) {
      this.gameServer.killPlayer(target);
      this.gameData.addNews("[X_x] " + godFatherTarget + " got killed last night.");
      return false;
    }

    this.gameServer.getGameState().setIsInDrCityState(true);
    this.gameServer.sendGameStateToClients();

    UTIL.setTimerFor(20, this.gameServer.getReadyPlayers());

    String drCitySaveTarget = this.gameServer.getGameState().getDrCitySaveTarget();
    this.gameServer.getGameState().setDrCitySaveTarget("");// reseting

    if (drCitySaveTarget.equals(godFatherTarget)) {
      if (this.gameServer.getPlayerByRole(ROLE.DR_CITY).getUsername().equals(drCitySaveTarget)) {
        this.gameServer.getGameState().setIsDrCitySavedHimselfAllready(true);
      }
      this.gameData.addNews("[+] Dr.City saved a citizen last night.");
    } else {
      this.gameServer.killPlayer(target);
      this.gameData.addNews("[X_x] " + godFatherTarget + " got killed last night.");
    }

    this.gameServer.getGameState().setIsInDrCityState(false);
    this.gameServer.sendGameStateToClients();

    this.narrator.changeState(STATES.DETECTIVE);
    return false;
  }

}
