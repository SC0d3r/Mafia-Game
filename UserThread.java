import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class UserThread extends Thread {
  private Socket socket;
  private GameServer gameServer;
  private PrintWriter writer;

  public UserThread(Socket socket, GameServer gameServer) {
    this.socket = socket;
    this.gameServer = gameServer;

  }

  @Override
  public void run() {
    try {
      InputStream input = this.socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));

      OutputStream output = this.socket.getOutputStream();
      this.writer = new PrintWriter(output, true);

      this.printUsers();

      boolean isAdded = false;
      String username = "";
      while (!isAdded) {
        System.out.print("Username> ");
        username = reader.readLine();
        isAdded = this.gameServer.addUserName(username);
      }

      String serverMessage = "New User Connected: " + username;
      this.gameServer.broadcast(serverMessage, this);

      String clientMessage;
      do {
        clientMessage = reader.readLine();
        serverMessage = username + ": " + clientMessage;
        this.gameServer.broadcast(serverMessage, this);
      } while (!clientMessage.equals("exit"));

      this.gameServer.removeUser(username, this);
      this.socket.close();

      serverMessage = username + "Quit the game!";
      this.gameServer.broadcast(serverMessage, this);
    } catch (IOException ex) {
      System.out.println("UserThread Error: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void printUsers() {
    if (this.gameServer.hasUsers()) {
      this.writer.println("Connected users: " + this.gameServer.getUsernames());
    } else {
      this.writer.println("There is no online user!");
    }
  }

  public void sendMessage(String message) {
    this.writer.println(message);
  }
}
