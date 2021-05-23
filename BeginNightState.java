import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class BeginNightState extends GameState {
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
    this.sendNewsToClient();
    this.narrator.changeState(STATES.PSYCHOLOGIST);
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
