import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            p.sendMessage("\n :::: WELCOME TO MAFIA GAME ::::");
          }
          this.sleep(2000);
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

        if (this.dataReciever.isPsychologistRequest(clientMessage)) {
          String toBeSilencedUsername = this.dataReciever.extractPshychologistRequest(clientMessage);
          Player toBeSilenced = this.gameServer.getPlayerByUsername(toBeSilencedUsername);
          toBeSilenced.setIsSilenced(true);
          this.gameServer.sendPlayerStateToClients();
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
      System.out.println("UserThread Error: " + ex.getMessage());
      ex.printStackTrace();
    } finally {
      this.gameServer.removeUser(username, this);
      String chatCMD = this.dataSender.createChatCommand("< " + username + " left the game >");
      this.gameServer.broadcast(chatCMD, this);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
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
