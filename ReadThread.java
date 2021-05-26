import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ReadThread extends Thread {

  private BufferedReader reader;
  private Socket socket;
  private GameClient client;
  private SocketDataReciever socketData;

  public ReadThread(Socket socket, GameClient client, SocketDataReciever socketData) {
    this.socket = socket;
    this.client = client;
    this.socketData = socketData;

    try {
      InputStream input = socket.getInputStream();
      this.reader = new BufferedReader(new InputStreamReader(input));
    } catch (IOException ex) {
      System.out.println("Error on getting input stream: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  @Override
  public void run() {
    while (true) {
      try {
        String response = reader.readLine();
        GameServer.clrscr();

        this.executeCommand(response);

        this.addAndUpdateData(response);

        this.draw(response);

        this.printPromptText();

      } catch (IOException ex) {
        System.out.println("Error on reading from server: " + ex.getMessage());
        ex.printStackTrace();
        break;
      }
    }
  }

  private void printSeperator() {
    System.out.println("\n-------------------------------------------------------------");
  }

  private void printPromptText() {
    if (this.client.getGameState().getIsGameFinished()) {
      this.printSeperator();
      System.out.println(this.client.getUsername() + ": ");
      return;
    }

    if (!this.client.getPlayer().getIsAlive()) {
      this.printSeperator();
      System.out.print("[X_X] You are dead.");
      return;
    }

    if (this.client.getPlayer().getIsSilenced()) {
      this.printSeperator();
      System.out.print("[o_O] you got silenced!");
      return;
    }

    if (this.client.getGameState().getIsVotingEnabled()) {
      this.printSeperator();
      System.out.print("Press Enter to start voting ...");
      return;
    }

    if (this.client.getGameState().getIsInProfessionalState()) {
      if (!this.client.isRole(ROLE.PROFESSIONAL)) {
        System.out.print("Professional is deciding ...");
      } else {
        this.printAvailableTargets();
        this.printSeperator();
        System.out.print("Press Enter to select your target ...");
      }
      return;
    }

    if (this.client.getGameState().getIsInDetectiveState()) {
      if (!this.client.isRole(ROLE.DETECTIVE)) {
        System.out.print("Detective is deciding ...");
      } else {
        ArrayList<String> aliveUsernames = this.client.getGameState().getAlivePlayerUsernames();
        aliveUsernames.remove(this.client.getUsername());
        System.out.println("Suspects: " + String.join(", ", aliveUsernames));
        this.printSeperator();
        System.out.print("Press Enter to select your suspect ...");
      }
      return;
    }

    if (this.client.getGameState().getIsInDieHardState()) {
      if (!this.client.isRole(ROLE.DIE_HARD)) {
        System.out.print("DIEHARD is deciding ...");
      } else {
        this.printSeperator();
        System.out.print("Press Enter to start ...");
      }
      return;
    }

    if (this.client.getGameState().getIsInDrLacterState()) {
      if (!this.client.isRole(ROLE.DR_LACTER)) {
        System.out.print("DR Lacter is deciding ...");
      } else {
        ArrayList<String> aliveMafias = this.client.getGameState().getAliveMafiaUsernames();
        System.out.println("Mafia Memeber: " + String.join(", ", aliveMafias));
        this.printSeperator();
        System.out.print("Press Enter to select your target ...");
      }
      return;
    }

    if (this.client.getGameState().getIsInDrCityState()) {
      if (!this.client.isRole(ROLE.DR_CITY)) {
        System.out.print("DR City is deciding ...");
      } else {
        ArrayList<String> aliveCitizen = this.client.getGameState().getAliveCitizenUsernames();
        System.out.println("Citizen : " + String.join(", ", aliveCitizen));
        this.printSeperator();
        System.out.print("Press Enter to select your target ...");
      }
      return;
    }

    if (this.client.getGameState().getIsInGodFatherState()) {
      if (!this.client.isRole(ROLE.GOD_FATHER)) {
        System.out.print("God Father is deciding ...");
      } else {
        ArrayList<String> aliveCitizen = this.client.getGameState().getAliveCitizenUsernames();
        System.out.println("Targets: " + String.join(", ", aliveCitizen));
        this.printSeperator();
        System.out.print("Press Enter to select your target ...");
      }
      return;
    }
    if (this.client.getGameState().getIsInIntroductionState()) {
      System.out.println();
      this.printSeperator();
      System.out.print("Wait till introduction finishes.");
      return;
    }

    if (this.client.getGameState().getIsInMafiaGatheringState()) {
      if (!this.client.getGameState().getAliveMafiaUsernames().contains(this.client.getUsername())) {
        System.out.print("Mafia Members are talking to each other ...");
      } else {
        this.printSeperator();
        System.out.print(this.client.getUsername() + ": ");
      }
      return;
    }

    if (this.client.getGameState().getIsInPsychologistState() && this.client.getPlayer().getIsAlive()) {
      if (!this.client.isRole(ROLE.PSYCHOLOGIST)) {
        System.out.print("Psychologist is deciding ...");
      } else {
        ArrayList<String> aliveUsersnames = this.client.getGameState().getAlivePlayerUsernames();
        aliveUsersnames.remove(this.client.getUsername());

        System.out.println("Users: " + String.join(", ", aliveUsersnames));
        this.printSeperator();
        System.out.print("Press [Enter] to start typing ...");
      }
      return;
    }

    if (this.client.getGameState().getIsInMayorState()) {
      if (!this.client.isRole(ROLE.MAYOR))
        System.out.print("Mayor is deciding ...");
      else
        System.out.print("Press Enter to start decisioning on voting ...");
      return;
    }

    if (!this.client.getGameState().getIsGameFinished() && !this.client.getPlayer().getCanChat()
        && this.client.getPlayer().getIsAlive()) {
      System.out.print("<.. zzZZZzz ..>");
      return;
    }

    if (this.client.getUsername() != null) {
      this.printSeperator();
      System.out.print(this.client.getUsername() + ": ");
    } else {
      // bellow if is for the issue of printing 2 times when new user connects to
      // server
      if (this.client.getGameState().getIsThereAnyUserOnline())
        GameServer.clrscr();
      System.out.printf("\nEnter username: ");
    }
  }

  private void printAvailableTargets() {
    ArrayList<String> aliveUsernames = this.client.getGameState().getAlivePlayerUsernames();
    aliveUsernames.remove(this.client.getUsername());
    System.out.println("Available targets: " + String.join(", ", aliveUsernames));
  }

  private void draw(String response) {
    System.out.println(this.socketData.getHeaderBarInformations());
    System.out.println(this.socketData.getNews());
    if (this.client.getGameState().getIsInMafiaGatheringState()) {
      if (this.client.isRole(ROLE.MAFIA_MEMBER) || this.client.isRole(ROLE.GOD_FATHER)
          || this.client.isRole(ROLE.DR_LACTER)) {
        System.out.println(this.socketData.getChatMessages());
      }
    } else
      System.out.println(this.socketData.getChatMessages());

    if (this.client.getGameState().getIsVotingEnabled()) {
      HashMap<String, String> votes = this.client.getGameState().getVotes();
      String votingTable = this.socketData.getVotingTable(votes);
      System.out.println(votingTable);
    }

    if (!this.socketData.isResponseCommand(response))
      System.out.println(response);

    UTIL.sleep(10);// to add the time needed to change the user name otherwise username is still
                   // null!
  }

  private void addAndUpdateData(String response) {
    this.socketData.addInfo(response);
    this.socketData.removeHeaderInfo(response);

    this.socketData.addNews(response);

    if (this.client.getGameState().getIsGameFinished() || this.client.getCanChat()
        || this.client.getGameState().getIsInMafiaGatheringState()) {
      this.socketData.addChatMessage(response);
      this.socketData.addChatCommand(response);
    }
  }

  private void executeCommand(String response) {
    if (this.socketData.isGameStateData(response)) {
      GameState newGameState = this.socketData.extractGameState(response);
      this.client.setGameState(newGameState);
    }

    if (this.socketData.isClearNewsCommand(response)) {
      this.socketData.clearNews();
    }

    if (this.socketData.isGameBeginCommand(response)) {
      this.socketData.clearChatMessages();
      this.socketData.clearHeader();
    }

    if (this.socketData.isSendPlayerStateCommand(response)) {
      this.client.setPlayer(this.socketData.extractPlayerState(response));
    }

    if (this.socketData.isAddAndClearChatCommand(response)) {
      this.socketData.saveAndClearChatMessages();
    }
  }
}
