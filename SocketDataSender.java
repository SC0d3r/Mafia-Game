import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SocketDataSender {
  public static final String HEADER_INFO_BAR = "::INFO";
  public static final String CHAT_MESSAGE = "::CHAT";
  public static final String CHAT_COMMAND = "::COMMAND_CHAT";
  public static final String SEPERATOR = "/";
  public static final String SECONDARY_SEPERATOR = "@";
  public static final String BEGIN_GAME = "::BEGIN_GAME";
  public static final String DISABLE_CHAT = "::DISABLE_CHAT";
  public static final String ENABLE_CHAT = "::ENABLE_CHAT";
  public static final String REMOVE_HEADER_INFO = "::REMOVE_INFO";
  public static final String SAVE_AND_CLEAR_CHAT = "::SAVE+CLEAR_CHAT";
  public static final String ENABLE_VOTING = "::ENABLE_VOTING";
  public static final String DISABLE_VOTING = "::DISABLE_VOTING";
  public static final String VOTE_FOR_USER = "::VOTE_FOR_USER";
  public static final String VOTING_MAP_CLIENT_SIDE = "::VOTING_MAP_CLIENT_SIDE";
  public static final String VOTING_TABLE = "::VOTING_TABLE";
  public static final String VOTING_MAP_SERVER_SIDE = "::VOTING_MAP_SERVER_SIDE";
  public static final String BEGIN_MAYOR_DECISIONING = "::BEGIN_MAYOR_DECISIONING";
  public static final String MAYOR_VOTES = "::MAYOR_VOTES";
  public static final String SEND_PLAYER_STATE = "::SEND_PLAYER_STATE";
  public static final String START_MAYOR_VOTING_STATE = "::START_MAYOR_VOTING_STATE";
  public static final String END_MAYOR_VOTING_STATE = "::END_MAYOR_VOTING_STATE";
  public static final String SEND_NEWS = "::SEND_NEWS";
  public static final String CLEAR_NEWS = "::CLEAR_NEWS";

  public SocketDataSender() {
  }

  public String createPlayerState(Player p) {
    String data = p.serialize();
    return SEND_PLAYER_STATE + SEPERATOR + data;
  }

  public String createNews(ArrayList<String> news) {
    return SEND_NEWS + SEPERATOR + String.join(SECONDARY_SEPERATOR, news);
  }

  public String createInfo(String tag, String infoText) {
    return HEADER_INFO_BAR + SEPERATOR + tag + SEPERATOR + infoText;
  }

  public String createMayorVote(String yesOrNo) {
    return MAYOR_VOTES + SEPERATOR + yesOrNo;
  }

  public String createVotingTable(ArrayList<String> choices) {
    return VOTING_TABLE + SEPERATOR + String.join(",", choices);
  }

  public String createVoteFor(String voterUsername, String voteeUsername) {
    return VOTE_FOR_USER + SEPERATOR + voterUsername + SEPERATOR + voteeUsername;
  }

  public String createVoteMapString(HashMap<String, String> votes) {
    ArrayList<String> result = new ArrayList<>();
    for (Map.Entry<String, String> el : votes.entrySet()) {
      result.add(el.getKey() + ":" + el.getValue());
    }
    return String.join(",", result);
  }

  public String createVoteMapClientSide(HashMap<String, String> votes) {
    return VOTING_MAP_CLIENT_SIDE + SEPERATOR + this.createVoteMapString(votes);
  }

  public HashMap<String, String> turnVoteMapStringIntoHashMap(String hashString) {
    HashMap<String, String> result = new HashMap<>();
    String[] keyValuePairs = hashString.split(",");
    for (String keyValue : keyValuePairs) {
      String key = keyValue.split(":")[0];
      String value = keyValue.split(":")[1];

      result.put(key, value);
    }
    return result;
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

  public String createVotingMapForServer(String voter, String voteFor, HashMap<String, String> votes) {
    return VOTING_MAP_SERVER_SIDE + SEPERATOR + voter + SEPERATOR + voteFor + SEPERATOR + createVoteMapString(votes);
  }

}
