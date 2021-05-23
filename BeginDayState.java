public class BeginDayState extends GameState {
  private GameData gameData;
  private SocketDataSender dataSender;

  public BeginDayState(Narrator narrator, GameServer server, GameData gameDate, SocketDataSender dataSender) {
    super(narrator, server);
    this.gameData = gameDate;
    this.dataSender = dataSender;
  }

  @Override
  public boolean run() {
    this.gameData.updateDayTime(DAYTIME.DAY);
    String timeOfDay = this.dataSender.createInfo("TIME", DAYTIME.toString(this.gameData.getDayTime()));
    this.narrator.broadcast(timeOfDay, this.gameServer.getReadyPlayers());
    this.narrator.changeState(STATES.WIN_LOST_CHECK);
    return false;
  }

}
