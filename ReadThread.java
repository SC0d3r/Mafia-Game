import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ReadThread extends Thread {

  private BufferedReader reader;
  private Socket socket;
  private GameClient client;
  private SocketDataReciever socketData;

  public ReadThread(Socket socket, GameClient client) {
    this.socket = socket;
    this.client = client;
    this.socketData = new SocketDataReciever();

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
        // GameServer.clrscr();

        if (this.socketData.isGameBeginCommand(response)) {
          this.socketData.clearChatMessages();
          this.socketData.clearHeader();
        }
        if (this.socketData.isAddAndClearChatCommand(response)) {
          this.socketData.saveAndClearChatMessages();
        }

        this.socketData.addInfo(response);
        this.socketData.removeHeaderInfo(response);

        if (!this.client.getIsChatDisabled()) {
          this.socketData.addChatMessage(response);
          this.socketData.addChatCommand(response);
        }

        System.out.println(this.socketData.getHeaderBarInformations());
        System.out.println(this.socketData.getChatMessages());

        if (this.socketData.shouldDisableChat(response))
          this.client.setIsChatDisabled(true);
        if (this.socketData.shouldEnableChat(response))
          this.client.setIsChatDisabled(false);

        if (!this.socketData.isResponseCommand(response))
          System.out.println(response);

        try {
          Thread.sleep(10);// to add the time needed to change the user name otherwise username is still
                           // null!
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        if (this.client.getIsChatDisabled()) {
          System.out.print("<You are Sleep>");
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
