import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SocketDataReciever {
  private HashMap<String, String> headerInformation;
  private ArrayList<String> chatMessages;

  private ArrayList<String> chatHistory;

  public SocketDataReciever() {
    this.headerInformation = new HashMap<>();
    this.chatMessages = new ArrayList<>();
    this.chatHistory = new ArrayList<>();
  }

  private void addChatToHistory() {
    this.chatHistory.addAll(this.chatMessages);
  }

  public boolean isAddAndClearChatCommand(String response) {
    return response.contains(SocketDataSender.SAVE_AND_CLEAR_CHAT);
  }

  public boolean isResponseCommand(String response) {
    return this.isChatCommand(response) || this.isChatMessage(response) || this.isHeaderInfo(response)
        || this.isRemoveHeaderInfo(response) || response.equals(SocketDataSender.DISABLE_CHAT)
        || response.equals(SocketDataSender.ENABLE_CHAT) || this.isAddAndClearChatCommand(response);
  }

  public void removeHeaderInfo(String response) {
    if (!this.isRemoveHeaderInfo(response))
      return;
    String infoName = response.split(SocketDataSender.SEPERATOR)[1];
    this.headerInformation.remove(infoName);
  }

  public void clearHeader() {
    this.headerInformation = new HashMap<>();
  }

  public boolean isRemoveHeaderInfo(String response) {
    return response.contains(SocketDataSender.REMOVE_HEADER_INFO);
  }

  public void clearChatMessages() {
    this.chatMessages = new ArrayList<>();
  }

  public void saveAndClearChatMessages() {
    this.addChatToHistory();
    this.clearChatMessages();
  }

  public void addInfo(String info) {
    if (this.isHeaderInfo(info)) {
      String[] tagAndText = this.extractHeaderInfo(info);
      this.headerInformation.put(tagAndText[0], tagAndText[1]);
    }
  }

  public boolean shouldDisableChat(String response) {
    return response.equals(SocketDataSender.DISABLE_CHAT);
  }

  public boolean shouldEnableChat(String response) {
    return response.equals(SocketDataSender.ENABLE_CHAT);
  }

  public void addChatMessage(String message) {
    // System.out.println(message);
    if (this.isChatMessage(message)) {
      // System.out.println(message);
      String chatMessage = this.extractChatMessage(message);
      this.chatMessages.add(chatMessage);
    }

  }

  public boolean isChatCommand(String message) {
    return message.contains(SocketDataSender.CHAT_COMMAND);
  }

  public boolean isGameBeginCommand(String response) {
    return response.equals(SocketDataSender.BEGIN_GAME);
  }

  public void addChatCommand(String message) {
    if (this.isChatCommand(message)) {
      String chatCMD = this.extractChatCommand(message);
      this.chatMessages.add(chatCMD);
    }
  }

  public boolean isHeaderInfo(String maybeInfo) {
    return maybeInfo.contains(SocketDataSender.HEADER_INFO_BAR);
  }

  public boolean isChatMessage(String maybeChatMessage) {
    return maybeChatMessage.contains(SocketDataSender.CHAT_MESSAGE);
  }

  private String extractChatMessage(String messageWithTags) {
    String[] datas = messageWithTags.split(SocketDataSender.SEPERATOR);
    String username = datas[1];
    String body = datas[2];
    return username + ": " + body;
  }

  private String[] extractHeaderInfo(String sendInfo) {
    String[] datas = sendInfo.split(SocketDataSender.SEPERATOR);
    String tag = datas[1];
    String infoText = datas[2];

    return new String[] { tag, infoText };
  }

  private String extractChatCommand(String chatMessage) {
    return chatMessage.split(SocketDataSender.SEPERATOR)[1];
  }

  public String getHeaderBarInformations() {
    String info = "";
    for (Map.Entry<String, String> entry : this.headerInformation.entrySet()) {
      info += entry.getKey() + ":" + entry.getValue() + " | ";
    }
    info += "\n-------------------------------------------------------------\n";
    return info;
  }

  public String getChatMessages() {
    return String.join("\n", this.chatMessages);
  }

}
