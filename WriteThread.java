import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

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

  private boolean isAValidYesNoResponse(String respose) {
    respose = respose.trim().toLowerCase();
    return respose.equals("y") || respose.equals("yes") || respose.equals("n") || respose.equals("no");
  }

  private boolean createBooleanFromYesNo(String yesNo) {
    yesNo = yesNo.toLowerCase();
    return yesNo.equals("y") || yesNo.equals("yes");
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
      return console.readLine().trim().toLowerCase();// for player who wants to type 'exit' to quit the game
    }

    if (this.client.getGameState().getIsInPsychologistState()) {
      if (this.client.isRole(ROLE.PSYCHOLOGIST)) {
        while (this.client.getGameState().getIsInPsychologistState() && this.client.isAlive()) {
          text = console.readLine("[~] Which player you want to silence for the next turn ?").trim();
          ArrayList<String> aliveUsernames = this.client.getGameState().getAlivePlayerUsernames();
          aliveUsernames.remove(this.client.getUsername());
          if (aliveUsernames.contains(text) || !this.client.getGameState().getIsInPsychologistState())
            break;
        }

        if (this.client.getGameState().getIsInPsychologistState() && this.client.isAlive()) {// this is for if condition
                                                                                             // changes while
          // stuck in above loop
          System.out.println("You chose: " + text);
          this.wirter.println(this.dataSender.createPsychologistRequest(text));
        }
      }
      if (this.client.getGameState().getIsInPsychologistState())// this is for if condition changes while
        // stuck in above loop
        return text;

    }

    if (this.client.getGameState().getIsInProfessionalState()) {
      if (this.client.isRole(ROLE.PROFESSIONAL)) {
        while (this.client.getGameState().getIsInProfessionalState() && this.client.getPlayer().getIsAlive()) {
          text = console.readLine("[~] Which target do you want to eliminate? ").trim();
          ArrayList<String> aliveUsernames = this.client.getGameState().getAlivePlayerUsernames();
          aliveUsernames.remove(this.client.getUsername());
          if (aliveUsernames.contains(text) || !this.client.getGameState().getIsInProfessionalState())
            break;
        }

        if (this.client.getGameState().getIsInProfessionalState() && this.client.getPlayer().getIsAlive()) {// this is
                                                                                                            // for if
                                                                                                            // condition
          // changes while
          // stuck in above loop
          System.out.println("You chose: " + text);
          this.wirter.println(this.dataSender.createProfessionalTarget(text));
        }
      }
      if (this.client.getGameState().getIsInProfessionalState())
        return text;
    }

    if (this.client.getGameState().getIsInDetectiveState()) {
      if (this.client.isRole(ROLE.DETECTIVE)) {
        while (this.client.getGameState().getIsInDetectiveState() && this.client.getPlayer().getIsAlive()) {
          text = console.readLine("[~] Which player do you suspect? ").trim();
          ArrayList<String> aliveUsernames = this.client.getGameState().getAlivePlayerUsernames();
          aliveUsernames.remove(this.client.getUsername());
          if (aliveUsernames.contains(text) || !this.client.getGameState().getIsInDetectiveState())
            break;
        }

        if (this.client.getGameState().getIsInDetectiveState() && this.client.getPlayer().getIsAlive()) {// this is
                                                                                                         // for if
                                                                                                         // condition
          // changes while
          // stuck in above loop
          System.out.println("You chose: " + text);
          this.wirter.println(this.dataSender.createDetectiveQuery(text));
        }
      }
      if (this.client.getGameState().getIsInDetectiveState())
        return text;
    }

    if (this.client.getGameState().getIsInDieHardState()) {
      if (this.client.isRole(ROLE.DIE_HARD)) {
        while (this.client.getGameState().getIsInDieHardState() && this.client.getPlayer().getIsAlive()) {
          text = console.readLine("[~] Do you want to start investigation [Y/N]? ").trim();
          if (this.isAValidYesNoResponse(text) || !this.client.getGameState().getIsInDieHardState())
            break;
        }
        if (this.client.getGameState().getIsInDieHardState() && this.client.getPlayer().getIsAlive()) {
          System.out.println("You chose: " + text);
          this.wirter.println(this.dataSender.createDieHardRequest(this.createBooleanFromYesNo(text)));
        }
      }
      if (this.client.getGameState().getIsInDieHardState())
        return text;
    }

    if (this.client.getGameState().getIsInMayorState()) {
      if (this.client.isRole(ROLE.MAYOR)) {
        while (true) {
          if (!this.client.getGameState().getIsInMayorState() || !this.client.isAlive())
            break;
          text = console.readLine("[~] Do you want to cancel voting [Y/N]? ").trim();
          if (this.isAValidYesNoResponse(text))
            break;
        }

        if (this.client.getGameState().getIsInMayorState() && this.client.isAlive()) {// this is for if condition
          // changes while
          // stuck in above loop
          // System.out.println("HERE");
          System.out.println("You voted: " + text);
          this.wirter.println(this.dataSender.createMayorVote(text));
        }
      }
      if (this.client.getGameState().getIsInMayorState())// this is for if condition changes while stuck in above loop
        return text;
    }

    // if (this.socketData.getIsVotingInProgress()) {
    if (this.client.getGameState().getIsVotingEnabled()) {
      while (this.client.getGameState().getIsVotingEnabled() && this.client.getPlayer().getIsAlive()) {
        text = console.readLine("Vote: ").trim();
        if (this.client.getGameState().isAValidVote(text))
          break;

        if (this.client.getGameState().getIsVotingEnabled())
          System.out.println("<   Not a valid Vote  : " + text + " >");
      }
      if (this.client.getGameState().getIsVotingEnabled() && this.client.getPlayer().getIsAlive()) {
        this.wirter.println(this.dataSender.createVotingMapForServer(username, text));
      }
      if (this.client.getGameState().getIsVotingEnabled())
        return text;
    }

    if (this.client.getPlayer().getCanChat() && !this.client.getPlayer().getIsSilenced()
        && this.client.getPlayer().getIsAlive()) {
      text = console.readLine(username + ": ").trim();
      if (text.isBlank())
        return text;
      // maybe state changed while stuck in readline function
      if (this.client.getPlayer().getCanChat() && !this.client.getPlayer().getIsSilenced()
          && this.client.getPlayer().getIsAlive()) {
        this.wirter.println(text);
      }

      return text;
    }

    return text;
  }

}
