import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class WriteThread extends Thread {
  private PrintWriter wirter;
  private Socket socket;
  private GameClient client;
  private SocketDataReciever socketData;
  private SocketDataSender dataSender;

  public WriteThread(Socket socket, GameClient client, SocketDataReciever socketData) {
    this.socket = socket;
    this.client = client;
    this.socketData = socketData;
    this.dataSender = new SocketDataSender();

    try {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      OutputStream output = socket.getOutputStream();
      this.wirter = new PrintWriter(output, true);
    } catch (IOException ex) {
      System.out.println("Error on getting output stream: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private boolean isAValidMayorVote(String respose) {
    respose = respose.trim().toLowerCase();
    return respose.equals("y") || respose.equals("yes") || respose.equals("n") || respose.equals("no");
  }

  @Override
  public void run() {
    Console console = System.console();

    boolean isUserAdded = false;
    String username = "";
    while (!isUserAdded) {
      username = console.readLine("\nEnter Username: ");

      if (this.client.doesUsernameExists(username)) {
        System.out.println("This user name already exists!");
        continue;
      }

      this.wirter.println(username);
      this.client.setUsername(username);
      isUserAdded = true;
    }

    String text = "";
    do {
      if (!this.client.isAlive()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        continue;
      }
      if (this.client.getIsInMayorVotingState()) {
        if (this.client.isRole(ROLE.MAYOR)) {
          while (true) {
            if (!this.client.getIsInMayorVotingState() && !this.client.isAlive())
              break;
            text = console.readLine("<> Do you want to cancel voting [Y/N]? ").trim();
            if (this.isAValidMayorVote(text))
              break;
          }

          System.out.println("You voted: " + text);
          if (this.client.getIsInMayorVotingState() && this.client.isAlive()) {// this is for if condition changes while
                                                                               // stuck in above loop
            // System.out.println("HERE");
            this.wirter.println(this.dataSender.createMayorVote(text));
          }
        }
        if (this.client.getIsInMayorVotingState())// this is for if condition changes while stuck in above loop
          continue;
      }

      if (this.socketData.getIsVotingInProgress()) {
        while (true) {
          if (!this.socketData.getIsVotingInProgress() || !this.client.isAlive())
            break;
          text = console.readLine("Vote: ").trim();
          if (this.socketData.isValidVote(text))
            break;

          if (this.socketData.getIsVotingInProgress())
            System.out.println("<   Not a valid Vote  : " + text + " >");
        }
        if (this.socketData.getIsVotingInProgress() && this.client.isAlive()) {
          this.wirter.println(this.dataSender.createVotingMapForServer(username, text, this.socketData.getVotes()));
          continue;
        }
      }

      if (this.client.getCanChat() && this.client.isAlive()) {
        text = console.readLine(username + ": ").trim();
        if (text.isBlank())
          continue;

        if (this.client.getCanChat() && this.client.isAlive())// maybe state changed while stuck in readline function
          this.wirter.println(text);
      }
    } while (!text.equals("exit"));

    try {
      this.socket.close();
    } catch (IOException ex) {
      System.out.println("Error on writinerver: " + ex.getMessage());

    }
  }

}
