import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

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

        // if (response.equals(SocketDataSender.CLEAR_NEWS)) {

        // System.out.println("HERE CLEAE NEWS");
        // try {
        // Thread.sleep(3000);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // this.socketData.clearNews();
        // }

        if (response.equals(SocketDataSender.DISABLE_VOTING)) {
          this.socketData.disableVoting();
          this.socketData.setIsVotingInProgress(false);
        }

        if (this.socketData.shouldEnableVoting()) {
          this.socketData.setIsVotingInProgress(true);
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

        if (this.socketData.isStartMayorVotingState(response)) {
          this.client.setIsInMayorVotingState(true);
        }

        if (this.socketData.isEndMayorVotingState(response)) {
          this.client.setIsInMayorVotingState(false);
        }

        this.socketData.addVotingTable(response);
        this.socketData.updateVotingTable(response);
        this.socketData.addInfo(response);
        this.socketData.removeHeaderInfo(response);

        this.socketData.addNews(response);

        if (this.client.getCanChat()) {
          this.socketData.addChatMessage(response);
          this.socketData.addChatCommand(response);
        }

        System.out.println(this.socketData.getHeaderBarInformations());
        System.out.println(this.socketData.getNews());
        System.out.println(this.socketData.getChatMessages());

        if (this.socketData.getIsVotingInProgress()) {
          String votingTable = this.socketData.getVotingTable();
          System.out.println(votingTable);
        }

        // if (this.socketData.shouldDisableChat(response))
        // this.client.setIsChatDisabled(true);
        // if (this.socketData.shouldEnableChat(response))
        // this.client.setIsChatDisabled(false);

        if (!this.socketData.isResponseCommand(response))
          System.out.println(response);

        try {
          Thread.sleep(10);// to add the time needed to change the user name otherwise username is still
                           // null!
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        if (!this.client.isAlive()) {
          System.out.println("\n-------------------------------------------------------------");
          System.out.print("[*] You are dead.");
          continue;
        }

        if (this.socketData.getIsVotingInProgress()) {
          System.out.println("\n-------------------------------------------------------------");
          System.out.print("Press Enter to start voting ...");
          continue;
        }

        if (this.client.getIsInMayorVotingState()) {
          if (!this.client.isRole(ROLE.MAYOR))
            System.out.print("Mayor is deciding ...");
          else
            System.out.print("Press Enter to start decisioning on voting ...");
          continue;
        }

        if (!this.client.getCanChat() && this.client.isAlive()) {
          System.out.print("<.. zzZZZzz ..>");
          continue;
        }

        if (this.client.getUsername() != null) {
          System.out.println("\n-------------------------------------------------------------");
          System.out.print(this.client.getUsername() + ": ");
        } else
          System.out.print("Enter username: ");
      } catch (IOException ex) {
        System.out.println("Error on reading from server: " + ex.getMessage());
        ex.printStackTrace();
        break;
      }
    }
  }

}
