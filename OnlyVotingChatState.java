import java.util.ArrayList;

public class OnlyVotingChatState extends GameState {

  private SocketDataSender dataSender;

  public OnlyVotingChatState(Narrator narrator, GameServer server, SocketDataSender dataSender) {
    super(narrator, server);
    this.dataSender = dataSender;
  }

  @Override
  public boolean run() {
    ArrayList<String> choices = this.gameServer.getAlivePlayersUsernames();
    for (Player p : this.gameServer.getReadyPlayers()) {
      p.sendMessage(SocketDataSender.ENABLE_VOTING);
      p.sendMessage(this.dataSender.createVotingTable(choices));
    }

    this.narrator.setTimerFor(20);

    this.disableVoting();

    this.narrator.changeState(STATES.MAYOR_DECISIONING);

    return false;
  }

  private void disableVoting() {
    this.narrator.broadcast(SocketDataSender.DISABLE_VOTING, this.gameServer.getReadyPlayers());
  }

}
