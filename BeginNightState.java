/**
 * this class updates the time to Night and its next state is MafiaGathering
 */
public class BeginNightState extends ServerState {
  private GameData gameData;
  private SocketDataSender dataSender;

  public BeginNightState(Narrator narrator, GameServer server, GameData gameData, SocketDataSender dataSender) {
    super(narrator, server);
    this.gameData = gameData;
    this.dataSender = dataSender;
  }

  @Override
  public boolean run() {
    this.gameData.updateDayTime(DAYTIME.NIGHT);
    String timeOfDay = this.dataSender.createInfo("TIME", DAYTIME.toString(this.gameData.getDayTime()));
    this.narrator.broadcast(timeOfDay, this.gameServer.getReadyPlayers());
    this.narrator.changeState(STATES.MAFIA_GATHERING);
    return false;
  }

}
