import java.util.ArrayList;

/**
 * this class is responsible for creating all the messages between client and
 * server
 */
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
  public static final String PROFESSIONAL_TARGET = "::PROFESSIONAL_TARGET";
  public static final String DETECTIVE_QUERY = "::DETECTIVE_QUERY";
  public static final String DIEHARD_REQUESTED_INVESTIGATION = "::DIEHARD_REQUESTED_INVESTIGATION";
  public static final String DR_LACTER_CURES = "::DR_LACTER_CURES";
  public static final String MAFIA_CHAT_MESSAGE = "::MAFIA_CHAT_MESSAGE";
  public static final String GOD_FATHER_TARGET_USERNAME = "::GOD_FATHER_TARGET_USERNAME";
  public static final String DR_CITY_CURES = "::DR_CITY_CURES";

  public SocketDataSender() {
  }

  public String createPlayerState(Player p) {
    String data = p.serialize();
    return SEND_PLAYER_STATE + SEPERATOR + data;
  }

  public String createDrCityCuresMessage(String username) {
    return DR_CITY_CURES + SEPERATOR + username;
  }

  public String createGodFatherTarget(String username) {
    return GOD_FATHER_TARGET_USERNAME + SEPERATOR + username;
  }

  public String createDrLacterCureMessage(String toBeCured) {
    return DR_LACTER_CURES + SEPERATOR + toBeCured;
  }

  public String createMafiaChatMessage(String username, String message) {
    return MAFIA_CHAT_MESSAGE + SEPERATOR + username + SEPERATOR + message;
  }

  public String createDieHardRequest(boolean status) {
    return DIEHARD_REQUESTED_INVESTIGATION + SEPERATOR + status;
  }

  public String createProfessionalTarget(String username) {
    return PROFESSIONAL_TARGET + SEPERATOR + username;
  }

  public String createDetectiveQuery(String username) {
    return DETECTIVE_QUERY + SEPERATOR + username;
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
