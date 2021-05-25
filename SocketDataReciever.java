import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jdk.javadoc.doclet.Reporter;

public class SocketDataReciever {
  private HashMap<String, String> headerInformation;
  private ArrayList<String> chatMessages;
  private ArrayList<String> news;

  private ArrayList<String> chatHistory;
  private boolean isVotingInProgress;

  public SocketDataReciever() {
    this.headerInformation = new HashMap<>();
    this.chatMessages = new ArrayList<>();
    this.chatHistory = new ArrayList<>();
    this.isVotingInProgress = false;
    this.news = new ArrayList<>();
  }

  public void clearNews() {
    this.news = new ArrayList<>();
  }

  public boolean isGameStateData(String response) {
    return response.contains(SocketDataSender.SEND_GAME_STATE);
  }

  public GameState extractGameState(String response) {
    String data = response.split(SocketDataSender.SEPERATOR)[1];
    return GameState.deserialize(data);
  }

  public boolean isProfessionalTargetCommand(String response) {
    return response.contains(SocketDataSender.PROFESSIONAL_TARGET);
  }

  public String extractProfessionalTarget(String response) {
    return response.split(SocketDataSender.SEPERATOR)[1];
  }

  public boolean isDetectiveQuery(String response) {
    return response.contains(SocketDataSender.DETECTIVE_QUERY);
  }

  public boolean isDieHardRequest(String response) {
    return response.contains(SocketDataSender.DIEHARD_REQUESTED_INVESTIGATION);
  }

  public boolean extractDieHardRequest(String response) {
    return Boolean.valueOf(response.split(SocketDataSender.SEPERATOR)[1]);
  }

  public boolean isDrLacterCureMessage(String response) {
    return response.contains(SocketDataSender.DR_LACTER_CURES);
  }

  public String extractDrLacterCuresUsername(String response) {
    return response.split(SocketDataSender.SEPERATOR)[1];
  }

  public boolean isMafiaChatMessage(String response) {
    return response.contains(SocketDataSender.MAFIA_CHAT_MESSAGE);
  }

  public String extractMafiaChatMessageUsername(String response) {
    return response.split(SocketDataSender.SEPERATOR)[1];
  }

  public String extractMafiaChatMessageBody(String response) {
    return response.split(SocketDataSender.SEPERATOR)[2];
  }

  public String extractDetectiveQuery(String response) {
    return response.split(SocketDataSender.SEPERATOR)[1];
  }

  public boolean isPsychologistRequest(String respose) {
    return respose.contains(SocketDataSender.PSYCHOLOGIST_REQUEST);
  }

  public String extractPshychologistRequest(String response) {
    String[] datas = response.split(SocketDataSender.SEPERATOR);
    String toBeSilenced = datas[1];
    return toBeSilenced;
  }

  public void setIsVotingInProgress(boolean status) {
    this.isVotingInProgress = status;
  }

  public boolean getIsVotingInProgress() {
    return this.isVotingInProgress;
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
        || response.equals(SocketDataSender.ENABLE_CHAT) || this.isAddAndClearChatCommand(response)
        || response.equals(SocketDataSender.ENABLE_VOTING) || response.equals(SocketDataSender.DISABLE_VOTING)
        || this.isVotingForUserCommand(response) || response.equals(SocketDataSender.BEGIN_MAYOR_DECISIONING)
        || this.isMayorVote(response) || this.isSendPlayerStateCommand(response)
        || this.isStartMayorVotingState(response) || this.isEndMayorVotingState(response) || this.isNews(response)
        || response.equals(SocketDataSender.CLEAR_NEWS) || response.equals(SocketDataSender.START_PSYCHOLOGIST_TURN)
        || response.equals(SocketDataSender.END_PSYCHOLOGIST_TURN) || this.isPsychologistRequest(response)
        || this.isGameStateData(response);
  }

  public boolean isNews(String response) {
    return response.contains(SocketDataSender.SEND_NEWS);
  }

  public void addNews(String response) {
    if (!this.isNews(response))
      return;

    this.news.clear();
    String[] news = response.split(SocketDataSender.SEPERATOR)[1].split(SocketDataSender.SECONDARY_SEPERATOR);
    this.news = new ArrayList<String>(Arrays.asList(news));
  }

  public boolean isMayorVote(String response) {
    return response.contains(SocketDataSender.MAYOR_VOTES);
  }

  private String extractMayorVote(String response) {
    return response.split(SocketDataSender.SEPERATOR)[1];
  }

  public boolean doesMayorCancelVoting(String response) {
    String vote = this.extractMayorVote(response).trim().toLowerCase();
    if (vote.equals("yes") || vote.equals("y"))
      return true;
    return false;
  }

  public boolean isStartMayorVotingState(String response) {
    return response.equals(SocketDataSender.START_MAYOR_VOTING_STATE);
  }

  public boolean isEndMayorVotingState(String response) {
    return response.equals(SocketDataSender.END_MAYOR_VOTING_STATE);
  }

  public boolean isSendPlayerStateCommand(String response) {
    return response.contains(SocketDataSender.SEND_PLAYER_STATE);
  }

  public Player extractPlayerState(String response) {
    String stateString = response.split(SocketDataSender.SEPERATOR)[1];
    return Player.deserialize(stateString);
  }

  private boolean isVotingForUserCommand(String response) {
    return response.contains(SocketDataSender.VOTE_FOR_USER);
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

  public boolean isGodFatherTargetUsername(String response) {
    return response.contains(SocketDataSender.GOD_FATHER_TARGET_USERNAME);
  }

  public String extractGodFatherTargetUsername(String response) {
    return response.split(SocketDataSender.SEPERATOR)[1];
  }

  public boolean isVotingMapForServer(String response) {
    return response.contains(SocketDataSender.VOTING_MAP_SERVER_SIDE);
  }

  public String extractServerSideVoter(String response) {
    return response.split(SocketDataSender.SEPERATOR)[1];
  }

  public String extractServerSideVotee(String response) {
    return response.split(SocketDataSender.SEPERATOR)[2];
  }

  public void addChatMessage(String message) {
    if (this.isChatMessage(message)) {
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

  public String getNews() {
    if (this.news.isEmpty())
      return "";
    String result = " > NEWS SECTION ";
    int i = 1;
    for (String s : this.news) {
      if (i > 1)
        result += "                ";
      result += i + "- " + s + "%n";
      i++;
    }
    return String.format(result);
  }

  public String getVotingTable(HashMap<String, String> votes) {
    String result = "";
    result += String.format("+---------------------+---------------------+%n");
    result += String.format("|       Players       |        Vote         |%n");
    result += String.format("+---------------------+---------------------+%n");
    String leftAlignFormat = "| %s | %s |%n";
    // String leftAlignFormat = "| %-21s | %-21s |%n";
    Set<String> usernames = votes.keySet();
    for (String username : usernames) {
      String vote = votes.get(username);
      vote = vote == null ? "< ! >" : vote;
      result += String.format(leftAlignFormat, centerString(19, username), centerString(19, vote));
    }
    result += String.format("+---------------------+---------------------+%n");
    return result;
  }

  public static String centerString(int width, String s) {
    return String.format("%-" + width + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
  }

}
