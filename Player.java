import java.io.Serializable;

/**
 * this class represents the player of the game that can be serialized and send
 * back to client whenever one of its states changes
 */
public class Player implements Serializable {
  private String username;
  private ROLE role;
  private boolean isALive;
  private boolean canChat;
  private transient UserThread user;
  private boolean isSilenced;

  public Player(String username, ROLE role, UserThread user) {
    this.role = role;
    this.isALive = true;
    this.canChat = true;
    this.username = username;
    this.user = user;
    this.isSilenced = false;
  }

  public void kill() {
    this.isALive = false;
    this.canChat = false;
  }

  public boolean getIsSilenced() {
    return this.isSilenced;
  }

  public void setIsSilenced(boolean status) {
    this.isSilenced = status;
  }

  public String serialize() {
    return UTIL.objectToString(this);
  }

  public static Player deserialize(String serializedData) {
    return UTIL.objectFromString(serializedData, Player.class);
  }

  public void setIsAlive(boolean isAlive) {
    this.isALive = isAlive;
  }

  public void setCanChat(boolean canChat) {
    this.canChat = canChat;
  }

  public String getUsername() {
    return this.username;
  }

  public synchronized void sendMessage(String message) {
    if (this.user == null) {
      System.out.println("ERROR: From client you cant send message with player object");
      return;
    }
    this.user.sendMessage(message);
  }

  public void setRole(ROLE role) {
    this.role = role;
  }

  public boolean getIsAlive() {
    return this.isALive;
  }

  public boolean getCanChat() {
    return this.canChat;
  }

  public ROLE getRole() {
    return this.role;
  }

}