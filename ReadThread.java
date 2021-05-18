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

  public ReadThread(Socket socket, GameClient client) {
    this.socket = socket;
    this.client = client;

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
        System.out.println("\n" + response);

        if (this.client.getUsername() != null) {
          System.out.print("<" + this.client.getUsername() + ">: ");
        }
      } catch (IOException ex) {
        System.out.println("Error on reading from server: " + ex.getMessage());
        ex.printStackTrace();
        break;
      }
    }
  }
}
