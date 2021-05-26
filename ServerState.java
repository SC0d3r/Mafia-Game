/**
 * parent state for all the states of the game
 */
public abstract class ServerState {
  protected Narrator narrator;
  protected GameServer gameServer;

  public ServerState(Narrator narrator, GameServer server) {
    this.narrator = narrator;
    this.gameServer = server;
  }

  public abstract boolean run();
}
