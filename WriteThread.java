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
      UTIL.sleep(10);
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
    String username = this.promptUsername(console);
    this.createPromptLoop(console, username);
    this.closeSocket();
  }

  private void createPromptLoop(Console console, String username) {
    String text = "";
    do {
      text = this.promptUser(console, username);
    } while (!text.equals("exit"));
  }

  private String promptUsername(Console console) {
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
    return username;
  }

  private void closeSocket() {
    try {
      this.socket.close();
    } catch (IOException ex) {
      System.out.println("Error on writinerver: " + ex.getMessage());

    }
  }

  private String promptUser(Console console, String username) {
    String text = "";

    if (!this.client.isAlive()) {
      text = console.readLine().trim().toLowerCase();// for player who wants to type 'exit' to quit the game
      return text;
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
        return text;
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
        return text;
      }
    }

    if (this.client.getCanChat() && this.client.isAlive()) {
      text = console.readLine(username + ": ").trim();
      if (text.isBlank())
        return text;
      if (this.client.getCanChat() && this.client.isAlive())// maybe state changed while stuck in readline function
        this.wirter.println(text);
    }

    return text;
  }

}
