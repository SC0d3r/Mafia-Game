public class SocketDataSender {
  public static final String HEADER_INFO_BAR = "::INFO";
  public static final String CHAT_MESSAGE = "::CHAT";
  public static final String CHAT_COMMAND = "::COMMAND_CHAT";
  public static final String SEPERATOR = "/";
  public static final String BEGIN_GAME = "::BEGIN_GAME";
  public static final String DISABLE_CHAT = "::DISABLE_CHAT";
  public static final String ENABLE_CHAT = "::ENABLE_CHAT";
  public static final String REMOVE_HEADER_INFO = "::REMOVE_INFO";
  public static final String SAVE_AND_CLEAR_CHAT = "::SAVE+CLEAR_CHAT";

  public SocketDataSender() {
  }

  public String createInfo(String tag, String infoText) {
    return HEADER_INFO_BAR + SEPERATOR + tag + SEPERATOR + infoText;
  }

  public String createChatMessage(String username, String message) {
    return CHAT_MESSAGE + SEPERATOR + username + SEPERATOR + message;
  }

  public String createChatCommand(String cmd) {
    return CHAT_COMMAND + SEPERATOR + cmd;
  }

  public String createRemoveInfo(String infoName) {
    return REMOVE_HEADER_INFO + SEPERATOR + infoName;
  }

}
