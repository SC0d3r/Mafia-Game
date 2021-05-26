/**
 * disables the chat
 */
public class DisableChatState extends ServerState {

  public DisableChatState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    for (Player p : this.gameServer.getReadyPlayers()) {
      p.setCanChat(false);
      p.setIsSilenced(false);
    }
    this.gameServer.sendPlayerStateToClients();
    this.narrator.changeState(STATES.ONLY_VOTING_CHAT);
    return false;
  }

}
