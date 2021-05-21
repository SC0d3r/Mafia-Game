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

  public void kill() {
    this.isALive = false;
    this.canChat = false;
  }

  public String serialize() {
    // TODO: add isSilent field to player to be used when Psychologist silences a
    // user
    return this.role + SocketDataSender.SECONDARY_SEPERATOR + this.isALive + SocketDataSender.SECONDARY_SEPERATOR
        + this.canChat + SocketDataSender.SECONDARY_SEPERATOR + this.username;
  }

  public static Player deserialize(String serializedData) {
    String[] datas = serializedData.split(SocketDataSender.SECONDARY_SEPERATOR);

    ROLE role = ROLE.valueOf(datas[0]);
    boolean isAlive = Boolean.valueOf(datas[1]);
    boolean canChat = Boolean.valueOf(datas[2]);
    String username = datas[3];

    Player p = new Player(username, role, null);
    p.setIsAlive(isAlive);
    p.setCanChat(canChat);
    return p;
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