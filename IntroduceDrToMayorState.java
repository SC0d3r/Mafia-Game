public class IntroduceDrToMayorState extends ServerState {

  private SocketDataSender dataSender;

  public IntroduceDrToMayorState(Narrator narrator, GameServer server, SocketDataSender dataSender) {
    super(narrator, server);
    this.dataSender = dataSender;
  }

  @Override
  public boolean run() {
    Player drCity = this.narrator.fetchPlayer(ROLE.DR_CITY);
    Player mayor = this.narrator.fetchPlayer(ROLE.MAYOR);
    if (drCity != null && mayor != null) {
      String messageForMayor = this.dataSender.createChatCommand("Dr.City is " + drCity.getUsername());
      String messageForDrCity = this.dataSender.createChatCommand("Mayor is " + mayor.getUsername());

      drCity.sendMessage(messageForDrCity);
      mayor.sendMessage(messageForMayor);
    }

    UTIL.sleepMSM(0, 3, 0);

    this.narrator.changeState(STATES.BEGIN_DAY);
    return false;
  }

}
