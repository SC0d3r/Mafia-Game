import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * this class will gets spawned whenever a new user(socket) gets accepted by the
 * server and will read the messages recieved from client and updates the game
 * state
 */
public class UserThread extends Thread {
  private Socket socket;
  private GameServer gameServer;
  private PrintWriter writer;
  private String username;
  private SocketDataSender dataSender;
  private SocketDataReciever dataReciever;

  public UserThread(Socket socket, GameServer gameServer, SocketDataReciever dataReciever) {
    this.socket = socket;
    this.gameServer = gameServer;
    this.username = "";
    this.dataSender = new SocketDataSender();
    this.dataReciever = dataReciever;

  }

  public boolean hasUsername(String username) {
    return this.username.equals(username);
  }

  public String getUsername() {
    return this.username;
  }

  @Override
  public void run() {
    String username = "";
    try {
      InputStream input = this.socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));

      OutputStream output = this.socket.getOutputStream();
      this.writer = new PrintWriter(output, true);

      username = reader.readLine();
      this.username = username.trim();

      // bellow lines are for to make sure the usernames are unique
      this.gameServer.getGameState().addUsername(this.username);
      String gameStateCMD = this.dataSender.createGameState(this.gameServer.getGameState());
      this.gameServer.broadcast(gameStateCMD, null);

      String serverMessage = "New User Connected: " + username;
      String newUserConnected = this.dataSender.createChatCommand("__ " + username + " Connected to lobby __");
      this.gameServer.broadcast(newUserConnected, this);

      this.gameServer.waitingLobbyInfo(username);

      String clientMessage = "";
      do {

        if (!this.gameServer.getIsGameStarted() && this.gameServer.canBeginTheGame()) {
          this.gameServer.setIsGameStarted(true);// this is for not entering this if statement and making another
                                                 // instances of Narrator
          for (Player p : this.gameServer.getReadyPlayers()) {
            p.sendMessage(SocketDataSender.BEGIN_GAME);
            p.sendMessage(this.dataSender.createChatCommand("             :::: WELCOME TO MAFIA GAME :::: "));
            p.sendMessage(this.dataSender.createChatCommand("     "));
          }
          UTIL.sleep(2000);
          // 2 bellow lines fixes the blocking issue, Otherwise this thread will be busy
          // to recieve or send this user messages
          ExecutorService myExecutor = Executors.newCachedThreadPool();
          myExecutor.execute(new Narrator(this.gameServer, GameData.getInstance()));
          continue;
        }

        clientMessage = reader.readLine();
        if (this.dataReciever.isVotingMapForServer(clientMessage)) {
          synchronized (this) {
            String voter = this.dataReciever.extractServerSideVoter(clientMessage);
            String votee = this.dataReciever.extractServerSideVotee(clientMessage);
            this.gameServer.getGameState().updateVote(voter, votee);
          }

          this.gameServer.sendGameStateToClients();
          continue;
        }

        if (this.dataReciever.isMayorVote(clientMessage)) {
          boolean vote = this.dataReciever.doesMayorCancelVoting(clientMessage);
          // this.gam
          GameData gd = GameData.getInstance();
          gd.setIsVotingGotCanceled(vote);
          continue;
        }

        if (this.dataReciever.isDrLacterCureMessage(clientMessage)) {
          String toBeCuredUsername = this.dataReciever.extractDrLacterCuresUsername(clientMessage);
          this.gameServer.getGameState().setDrLacterCuresUsername(toBeCuredUsername);
          continue;
        }

        if (this.dataReciever.isDieHardRequest(clientMessage)) {
          boolean status = this.dataReciever.extractDieHardRequest(clientMessage);
          this.gameServer.getGameState().setIsDieHardRequestedInvestigation(status);
          continue;
        }

        if (this.dataReciever.isPsychologistRequest(clientMessage)) {
          String toBeSilencedUsername = this.dataReciever.extractPshychologistRequest(clientMessage);
          Player toBeSilenced = this.gameServer.getPlayerByUsername(toBeSilencedUsername);
          toBeSilenced.setIsSilenced(true);
          this.gameServer.sendPlayerStateToClients();
          continue;
        }
        if (this.dataReciever.isProfessionalTargetCommand(clientMessage)) {
          String toBeKilledUsername = this.dataReciever.extractProfessionalTarget(clientMessage);
          this.gameServer.getGameState().setProfessionalTarget(toBeKilledUsername);
          continue;
        }

        if (this.dataReciever.isMafiaChatMessage(clientMessage)) {
          String SenderUsername = this.dataReciever.extractMafiaChatMessageUsername(clientMessage);
          String SenderMessage = this.dataReciever.extractMafiaChatMessageBody(clientMessage);
          String chatMessageToSend = this.dataSender.createChatMessage(SenderUsername, SenderMessage);
          this.gameServer.broadcast(chatMessageToSend, null);
          continue;
        }

        if (this.dataReciever.isGodFatherTargetUsername(clientMessage)) {
          String targetUsername = this.dataReciever.extractGodFatherTargetUsername(clientMessage);
          this.gameServer.getGameState().setGodFatherTargetUsername(targetUsername);
          continue;
        }

        if (this.dataReciever.isDetectiveQuery(clientMessage)) {
          String queryUsername = this.dataReciever.extractDetectiveQuery(clientMessage);
          this.gameServer.getGameState().setDetectiveSuspicionTarget(queryUsername);
          continue;
        }

        if (this.dataReciever.isDrCityCuresMessage(clientMessage)) {
          String cureTargetUsername = this.dataReciever.extractDrCityCuresUsername(clientMessage);
          this.gameServer.getGameState().setDrCitySaveTarget(cureTargetUsername);
          continue;
        }

        if (this.gameServer.getIsGameStarted() && clientMessage.equals("!ready")) {
          this.gameServer.getGameState().addToReadyPlayersToBeginVoting(this.username);
          int currentReadyPlayers = this.gameServer.getGameState().getReadyPlayersToBeginVoting().size();
          int total = this.gameServer.getGameState().getUsernamesWhoCanChat().size();

          String message = "[" + currentReadyPlayers + "\\" + total + "] " + this.username + " is ready for voting.";
          this.gameServer.broadcast(this.dataSender.createChatCommand(message), null);
          continue;
        }

        if (!this.gameServer.getIsGameStarted() && clientMessage.equals("!ready")) {
          this.gameServer.registerForGame(username, this);
          this.sleep(10);
          this.gameServer.waitingLobbyInfo(username);
          this.sendMessage(this.dataSender.createChatCommand("... you are ready ..."));
          continue;
        }
        if (clientMessage.equals("!list")) {
          String cmdMessage = this.dataSender
              .createChatCommand("(Users) " + String.join(", ", this.gameServer.getUsernames()));
          this.sendMessage(cmdMessage);
          continue;
        }

        if (!this.gameServer.getIsGameStarted()) {
          this.gameServer.waitingLobbyInfo(username);
        }

        serverMessage = this.dataSender.createChatMessage(username, clientMessage);
        if (!this.gameServer.getIsGameStarted()) {

          for (UserThread user : this.gameServer.getUserThreads()) {
            user.sendMessage(serverMessage);
          }
        } else {
          for (Player p : this.gameServer.getReadyPlayers()) {
            p.sendMessage(serverMessage);
          }
        }
      } while (!clientMessage.equals("exit"));

      this.socket.close();
    } catch (IOException ex) {
      if (this.gameServer.getIsDebugModeOn()) {
        System.out.println("UserThread Error: " + ex.getMessage());
        ex.printStackTrace();
      }
    } finally {
      this.gameServer.killUser(this);
      // this.gameServer.removeUser(username, this);
      String chatCMD = this.dataSender.createChatCommand("< " + username + " left the game >");
      this.gameServer.broadcast(chatCMD, this);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (!this.gameServer.getIsGameStarted())
        this.gameServer.waitingLobbyInfo(username);
    }
  }

  private void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  public synchronized void sendMessage(String message) {
    this.writer.println(message);
  }
}
