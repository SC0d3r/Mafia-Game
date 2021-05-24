import java.util.ArrayList;

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
  public static final String START_PSYCHOLOGIST_TURN = "::START_PSYCHOLOGIST_TURN";
  public static final String END_PSYCHOLOGIST_TURN = "::END_PSYCHOLOGIST_TURN";
  public static final String PSYCHOLOGIST_REQUEST = "::PSYCHOLOGIST_REQUEST";
  public static final String SEND_GAME_STATE = "::SEND_GAME_STATE";

  public SocketDataSender() {
  }

  public String createPlayerState(Player p) {
    String data = p.serialize();
    return SEND_PLAYER_STATE + SEPERATOR + data;
  }

  public String createGameState(GameState gameState) {
    String data = gameState.serialize();
    return SEND_GAME_STATE + SEPERATOR + data;
  }

  public String createPsychologistRequest(String toBeSilencedUsername) {
    return PSYCHOLOGIST_REQUEST + SEPERATOR + toBeSilencedUsername;
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

  public String createChatMessage(String username, String message) {
    return CHAT_MESSAGE + SEPERATOR + username + SEPERATOR + message;
  }

  public String createChatCommand(String cmd) {
    return CHAT_COMMAND + SEPERATOR + cmd;
  }

  public String createRemoveInfo(String infoName) {
    return REMOVE_HEADER_INFO + SEPERATOR + infoName;
  }

  public String createVotingMapForServer(String voter, String voteFor) {
    return VOTING_MAP_SERVER_SIDE + SEPERATOR + voter + SEPERATOR + voteFor;
  }

}
