import java.util.ArrayList;

/**
 * a utility class to pause time and update the timer info on top of screen for
 * users
 */
public class UpdateTimer {
  private int timerInSeconds;
  private ArrayList<Player> players;
  private SocketDataSender dataSender;
  private GameState gameState;

  public UpdateTimer(int timerInSeconds, ArrayList<Player> players) {
    // super();
    this.timerInSeconds = timerInSeconds;
    this.players = players;
    this.dataSender = new SocketDataSender();
  }

  public UpdateTimer(int timerInSeconds, ArrayList<Player> players, GameState gameState) {
    this.timerInSeconds = timerInSeconds;
    this.players = players;
    this.dataSender = new SocketDataSender();
    this.gameState = gameState;
  }

  public boolean isFinished() {
    return this.timerInSeconds <= 0;
  }

  // @Override
  public void run() {
    this.sendToClients();

    boolean areAllPlayersReadyToVote = false;
    while (true) {

      if (this.gameState != null) {
        areAllPlayersReadyToVote = this.gameState.getReadyPlayersToBeginVoting().size() == this.gameState
            .getUsernamesWhoCanChat().size();
      }

      if (this.isFinished() || areAllPlayersReadyToVote) {

        for (Player p : this.players) {
          String removeInfo = this.dataSender.createRemoveInfo("Remaining");
          p.sendMessage(removeInfo);
        }
        return;
      }

      if (this.timerInSeconds % 10 == 0) {
        this.sendToClients();
      }

      this.wait(1000);
      this.timerInSeconds -= 1;
    }
  }

  private void sendToClients() {
    for (Player p : this.players) {
      String remainingSeconds = this.dataSender.createInfo("Remaining", this.timerInSeconds + "s");
      p.sendMessage(remainingSeconds);
    }
  }

  private void wait(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
