/**
 * this class will just goes to disable chat state
 */
public class EndOfDayVotingState extends ServerState {

  public EndOfDayVotingState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    this.narrator.changeState(STATES.DISABLE_CHAT);
    return false;
  }

}
