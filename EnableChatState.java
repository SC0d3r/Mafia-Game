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
    UTIL.setTimerFor(30, this.gameServer.getReadyPlayers());
    this.saveAndClearChatMessages();
    this.narrator.changeState(STATES.END_OF_DAY_VOTING);
    return false;
  }

  private void saveAndClearChatMessages() {
    String cmd = SocketDataSender.SAVE_AND_CLEAR_CHAT;
    this.narrator.broadcast(cmd, this.gameServer.getReadyPlayers());
  }

}
