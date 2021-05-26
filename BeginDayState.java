import java.util.ArrayList;

/**
 * this class runs when the day begins does 3 main things: 1- clears news 2-
 * clears chat 3- updates time to 'DAY'.next state for this class is
 * WinLostState
 */
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
    // this is for clearing the welcome and introduction of mafia and dr to mayor
    // etc...
    this.narrator.broadcast(SocketDataSender.SAVE_AND_CLEAR_CHAT, this.gameServer.getReadyPlayers());
    this.narrator.broadcast(SocketDataSender.CLEAR_NEWS, this.gameServer.getReadyPlayers());

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
    this.clearNews();
    this.narrator.broadcast(this.dataSender.createNews(news), this.gameServer.getReadyPlayers());
  }

  private void clearNews() {
    this.gameData.clearNews();
  }

}
