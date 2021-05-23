public class PsychologistState extends GameState {

  private SocketDataSender dataSender;

  public PsychologistState(Narrator narrator, GameServer server, SocketDataSender dataSender) {
    super(narrator, server);
    this.dataSender = dataSender;
  }

  @Override
  public boolean run() {
    this.narrator.broadcast("PSYCHOLOGIST is deciding <?>", this.gameServer.getReadyPlayers());
    this.narrator.setTimerFor(10);
    this.narrator.changeState(STATES.BEGIN_DAY);
    return false;
  }

}
