import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SocketDataReciever {
  private HashMap<String, String> headerInformation;
  private ArrayList<String> chatMessages;
  private ArrayList<String> news;

  private ArrayList<String> chatHistory;
  private volatile HashMap<String, String> votes;
  private boolean isVotingInProgress;

  public SocketDataReciever() {
    this.headerInformation = new HashMap<>();
    this.chatMessages = new ArrayList<>();
    this.chatHistory = new ArrayList<>();
    this.votes = new HashMap<>();
    this.isVotingInProgress = false;
    this.news = new ArrayList<>();
  }

  public void clearNews() {
    this.news = new ArrayList<>();
  }

  public HashMap<String, String> getVotes() {
    return this.votes;
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
        || this.isVotingForUserCommand(response) || this.isCreateVotingTableCommand(response)
        || this.isVoteMapClientSideCommand(response) || response.equals(SocketDataSender.BEGIN_MAYOR_DECISIONING)
        || this.isMayorVote(response) || this.isSendPlayerStateCommand(response)
        || this.isStartMayorVotingState(response) || this.isEndMayorVotingState(response) || this.isNews(response)
        || response.equals(SocketDataSender.CLEAR_NEWS);
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

  public boolean shouldEnableVoting() {
    return this.votes.size() > 0;
  }

  public void disableVoting() {
    this.votes = new HashMap<>();
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

  public void addVotingTable(String response) {
    if (!this.isCreateVotingTableCommand(response))
      return;
    this.votes = new HashMap<>();
    List<String> choices = this.extractVotingChoices(response);
    for (String choice : choices) {
      this.votes.put(choice, "< ! >");
    }
  }

  private List<String> extractVotingChoices(String response) {
    String choices = response.split(SocketDataSender.SEPERATOR)[1];
    return Arrays.asList(choices.split(","));
  }

  private boolean isCreateVotingTableCommand(String response) {
    return response.contains(SocketDataSender.VOTING_TABLE);
  }

  // public void addVote(String response) {
  // if (!this.isVotingForUserCommand(response))
  // return;
  // // Set<String> choices = this.votes.keySet();
  // String voteFor = this.extractVoteFor(response);
  // String voter = this.extractVoter(response);
  // if (!this.isValidVote(voteFor))
  // return;
  // this.votes.put(voter, voteFor);
  // }

  public void updateVotingTable(String response) {
    if (!this.isVoteMapClientSideCommand(response))
      return;
    SocketDataSender sender = new SocketDataSender();
    String hashString = response.split(SocketDataSender.SEPERATOR)[1];
    this.votes = sender.turnVoteMapStringIntoHashMap(hashString);
  }

  public Set<String> getVotingChoices() {
    return this.votes.keySet();
  }

  private String extractVoteFor(String response) {
    String voteFor = response.split(SocketDataSender.SEPERATOR)[2];
    return voteFor;
  }

  public boolean isVotingMapForServer(String response) {
    return response.contains(SocketDataSender.VOTING_MAP_SERVER_SIDE);
  }

  public void addVotingMapServerSide(String response) {
    String voter = this.extractServerSideVoter(response);
    String votee = this.extractServerSideVotee(response);
    this.votes = this.createVoteHashMapServerSide(response);
    this.votes.put(voter, votee);
  }

  private String extractServerSideVoter(String response) {
    return response.split(SocketDataSender.SEPERATOR)[1];
  }

  private String extractServerSideVotee(String response) {
    return response.split(SocketDataSender.SEPERATOR)[2];
  }

  private boolean isVoteMapClientSideCommand(String response) {
    return response.contains(SocketDataSender.VOTING_MAP_CLIENT_SIDE);
  }

  private HashMap<String, String> createVoteHashMapServerSide(String response) {
    HashMap<String, String> result = new HashMap<>();
    String hashString = response.split(SocketDataSender.SEPERATOR)[3];
    String[] keyValuePairs = hashString.split(",");
    for (String keyValue : keyValuePairs) {
      String key = keyValue.split(":")[0];
      String value = keyValue.split(":")[1];

      result.put(key, value);
    }
    return result;
  }

  public boolean isValidVote(String voteFor) {
    Set<String> choices = this.getVotingChoices();
    return choices.contains(voteFor);
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

  public String getVotingTable() {
    String result = "";
    result += String.format("+---------------------+---------------------+%n");
    result += String.format("|       Players       |        Vote         |%n");
    result += String.format("+---------------------+---------------------+%n");
    String leftAlignFormat = "| %s | %s |%n";
    // String leftAlignFormat = "| %-21s | %-21s |%n";
    Set<String> usernames = this.votes.keySet();
    for (String username : usernames) {
      String vote = this.votes.get(username);
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
