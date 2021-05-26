import java.util.ArrayList;

/**
 * this class will introduce mafias to each other
 */
public class IntroduceMafiaToTheirTeammateState extends ServerState {

  private SocketDataSender dataSender;

  public IntroduceMafiaToTheirTeammateState(Narrator narrator, GameServer server, SocketDataSender dataSender) {
    super(narrator, server);
    this.dataSender = dataSender;
  }

  @Override
  public boolean run() {
    ArrayList<Player> mafias = this.getMafias();
    ArrayList<String> messages = this.createIntroductionMessages(mafias);
    this.narrator.broadcast(this.dataSender.createChatCommand(String.join(", ", messages)), mafias);
    UTIL.setTimerFor(2, this.gameServer.getReadyPlayers());
    this.narrator.changeState(STATES.INTRODUCE_DR_TO_MAYOR);
    return false;
  }

  private ArrayList<Player> getMafias() {
    ArrayList<Player> result = new ArrayList<>();
    for (Player p : this.gameServer.getReadyPlayers()) {
      if (p.getRole() == ROLE.GOD_FATHER || p.getRole() == ROLE.MAFIA_MEMBER || p.getRole() == ROLE.DR_LACTER)
        result.add(p);
    }
    return result;
  }

  private ArrayList<String> createIntroductionMessages(ArrayList<Player> mafias) {
    ArrayList<String> messages = new ArrayList<>();
    for (Player p : mafias) {
      if (p.getRole() == ROLE.GOD_FATHER)
        messages.add("God-Father: " + p.getUsername());

      else if (p.getRole() == ROLE.DR_LACTER)
        messages.add("Dr.Lacter: " + p.getUsername());
      else
        messages.add("Mafia-Member: " + p.getUsername());
    }
    return messages;
  }
}
