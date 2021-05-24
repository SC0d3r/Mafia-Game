import java.util.ArrayList;

public class BeginDayState extends ServerState {
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

    this.sendNewsToClient();
    this.narrator.changeState(STATES.WIN_LOST_CHECK);
    return false;
  }

  private void sendNewsToClient() {
    if (!this.gameData.isThereAnyNews())
      return;
    ArrayList<String> news = this.gameData.getNews();
    this.narrator.broadcast(this.dataSender.createNews(news), this.gameServer.getReadyPlayers());
    this.clearNews();
  }

  private void clearNews() {
    this.gameData.clearNews();
  }

}
