import java.util.ArrayList;

public class UpdateTimer {
  private int timerInSeconds;
  private ArrayList<Player> players;
  private SocketDataSender dataSender;

  public UpdateTimer(int timerInSeconds, ArrayList<Player> players) {
    // super();
    this.timerInSeconds = timerInSeconds;
    this.players = players;
    this.dataSender = new SocketDataSender();
  }

  public boolean isFinished() {
    return this.timerInSeconds <= 0;
  }

  // @Override
  public void run() {
    while (true) {
      if (this.isFinished()) {
        for (Player p : this.players) {
          String removeInfo = this.dataSender.createRemoveInfo("Remaining");
          p.sendMessage(removeInfo);
          // this.wait(10);
        }
        return;
      }

      for (Player p : this.players) {
        String remainingSeconds = this.dataSender.createInfo("Remaining", this.timerInSeconds + "s");
        p.sendMessage(remainingSeconds);
      }
      this.wait(10000);
      this.timerInSeconds -= 10;
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
