import java.util.ArrayList;

/**
 * this class will enable chat for only voting
 */
public class OnlyVotingChatState extends ServerState {

  public OnlyVotingChatState(Narrator narrator, GameServer server, SocketDataSender dataSender) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    ArrayList<String> choices = this.gameServer.getAlivePlayersUsernames();
    this.gameServer.getGameState().setIsVotingEnabled(true);
    this.gameServer.getGameState().initVotingChoices(choices);
    this.gameServer.sendGameStateToClients();

    UTIL.setTimerFor(30, this.gameServer.getReadyPlayers());

    this.gameServer.getGameState().setIsVotingEnabled(false);
    this.gameServer.sendGameStateToClients();

    this.narrator.changeState(STATES.MAYOR_DECISIONING);

    return false;
  }

}
