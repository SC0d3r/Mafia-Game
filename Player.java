public class Player {
  private String username;
  private ROLE role;
  private boolean isALive;
  private boolean canChat;
  private UserThread user;

  public Player(String username, ROLE role, UserThread user) {
    this.role = role;
    this.isALive = true;
    this.canChat = false;
    this.username = username;
    this.user = user;
  }

  public String getUsername() {
    return this.username;
  }

  public synchronized void sendMessage(String message) {
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