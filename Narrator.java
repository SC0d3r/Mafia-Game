import java.util.ArrayList;
import java.util.HashMap;

public class Narrator implements Runnable {
  private GameServer server;
  private GameData gameData;
  private SocketDataSender dataSender;
  private ServerState currentState;

  private HashMap<STATES, ServerState> states;

  public Narrator(GameServer server, GameData gameData) {
    this.server = server;
    this.gameData = GameData.getInstance();
    this.dataSender = new SocketDataSender();
    this.states = new HashMap<>();

    this.initStates();
  }

  private void initStates() {
    ServerState initPlayersState = new InitPlayersState(this, this.server, this.gameData);

    this.states.put(STATES.INIT_PLAYERS, initPlayersState);
    this.states.put(STATES.INTRODUCE_MAFIAS, new IntroduceMafiaToTheirTeammateState(this, this.server));
    this.states.put(STATES.INTRODUCE_DR_TO_MAYOR, new IntroduceDrToMayorState(this, this.server, this.dataSender));
    this.states.put(STATES.BEGIN_DAY, new BeginDayState(this, this.server, this.gameData, this.dataSender));
    this.states.put(STATES.WIN_LOST_CHECK, new WinLoseCheckState(this, this.server));
    this.states.put(STATES.MAFIA_WINNER, new MafiaWinnerState(this, this.server, this.dataSender));
    this.states.put(STATES.CITIZEN_WINNER, new CitizenWinnerState(this, this.server, this.dataSender));
    this.states.put(STATES.ENABLE_CHAT, new EnableChatState(this, this.server));
    this.states.put(STATES.END_OF_DAY_VOTING, new EndOfDayVotingState(this, this.server));
    this.states.put(STATES.DISABLE_CHAT, new DisableChatState(this, this.server));
    this.states.put(STATES.ONLY_VOTING_CHAT, new OnlyVotingChatState(this, this.server, this.dataSender));
    this.states.put(STATES.MAYOR_DECISIONING, new MayorDecisioningState(this, this.server, this.gameData));
    this.states.put(STATES.BEGIN_NIGHT, new BeginNightState(this, this.server, this.gameData, this.dataSender));
    this.states.put(STATES.PSYCHOLOGIST, new PsychologistState(this, this.server, this.dataSender));
    this.states.put(STATES.PROFESSIONAL, new ProfessionalState(this, this.server, this.gameData));
    this.states.put(STATES.DETECTIVE, new DetectiveState(this, this.server, this.gameData));
    this.states.put(STATES.DIEHARD, new DieHardState(this, this.server, this.gameData));

    this.currentState = initPlayersState;
  }

  public void changeState(STATES state) {
    this.currentState = this.states.get(state);
  }

  public void broadcast(String message, ArrayList<Player> players) {
    for (Player p : players)
      p.sendMessage(message);
  }

  public Player fetchPlayer(ROLE role) {
    Player result = null;
    for (Player p : this.server.getReadyPlayers()) {
      if (p.getRole() == role)
        result = p;
      break;
    }
    return result;
  }

  @Override
  public void run() {
    while (true) {
      boolean isFinished = this.currentState.run();
      if (isFinished)
        break;
    }
    this.broadcast("GAME IS FINISHED", this.server.getReadyPlayers());
  }

}
