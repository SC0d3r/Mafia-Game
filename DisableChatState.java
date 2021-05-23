public class DisableChatState extends GameState {

  public DisableChatState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    for (Player p : this.gameServer.getReadyPlayers()) {
      p.setCanChat(false);
    }
    this.narrator.sendPlayerStateToClient();
    this.narrator.changeState(STATES.ONLY_VOTING_CHAT);
    return false;
  }

}
