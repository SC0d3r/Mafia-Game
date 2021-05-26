import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class EnableChatState extends ServerState {

  public EnableChatState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    for (Player p : this.gameServer.getReadyPlayers()) {
      p.setCanChat(true);
    }
    this.gameServer.sendPlayerStateToClients();

    // bellow line will be used in uptateTimer to see if all players who can chat
    // typed !ready or not to begin voting early
    this.gameServer.getGameState().setUsernamesWhoChat(this.gameServer.getWhoCanChat());

    int fiveMinutes = 60 * 5;
    UTIL.setTimerFor(fiveMinutes, this.gameServer.getReadyPlayers(), this.gameServer.getGameState());

    this.gameServer.getGameState().clearReadyPlayersToBeginVoting();
    this.gameServer.sendGameStateToClients();

    this.saveAndClearChatMessages();
    this.narrator.changeState(STATES.END_OF_DAY_VOTING);
    return false;
  }

  private void saveAndClearChatMessages() {
    String cmd = SocketDataSender.SAVE_AND_CLEAR_CHAT;
    this.narrator.broadcast(cmd, this.gameServer.getReadyPlayers());
  }

}
