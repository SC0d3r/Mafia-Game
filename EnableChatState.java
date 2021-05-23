public class EnableChatState extends GameState {

  public EnableChatState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    for (Player p : this.gameServer.getReadyPlayers()) {
      p.setCanChat(true);
    }
    this.narrator.sendPlayerStateToClient();
    this.narrator.setTimerFor(20);
    // UTIL.sleepMSM(0, 10, 0);

    this.saveAndClearChatMessages();
    this.narrator.changeState(STATES.END_OF_DAY_VOTING);
    return false;
  }

  private void saveAndClearChatMessages() {
    String cmd = SocketDataSender.SAVE_AND_CLEAR_CHAT;
    this.narrator.broadcast(cmd, this.gameServer.getReadyPlayers());
  }

}
