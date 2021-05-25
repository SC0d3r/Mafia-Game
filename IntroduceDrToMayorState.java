public class IntroduceDrToMayorState extends ServerState {

  private SocketDataSender dataSender;

  public IntroduceDrToMayorState(Narrator narrator, GameServer server, SocketDataSender dataSender) {
    super(narrator, server);
    this.dataSender = dataSender;
  }

  @Override
  public boolean run() {
    Player drCity = this.gameServer.getPlayerByRole(ROLE.DR_CITY);
    Player mayor = this.gameServer.getPlayerByRole(ROLE.MAYOR);
    if (drCity != null && mayor != null) {
      String messageForMayor = this.dataSender.createChatCommand("  Dr.City is " + drCity.getUsername());
      String messageForDrCity = this.dataSender.createChatCommand("  Mayor is " + mayor.getUsername());

      drCity.sendMessage(messageForDrCity);
      mayor.sendMessage(messageForMayor);
    }

    UTIL.sleepMSM(0, 3, 0);

    // introduction finishes
    this.gameServer.getGameState().setIsInIntroductionState(false);
    this.gameServer.sendGameStateToClients();

    this.narrator.changeState(STATES.BEGIN_DAY);
    return false;
  }

}
