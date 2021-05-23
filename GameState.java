public abstract class GameState {
  protected Narrator narrator;
  protected GameServer gameServer;

  public GameState(Narrator narrator, GameServer server) {
    this.narrator = narrator;
    this.gameServer = server;
  }

  public abstract boolean run();
}
