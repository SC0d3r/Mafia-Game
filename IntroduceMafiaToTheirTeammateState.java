import java.util.ArrayList;

public class IntroduceMafiaToTheirTeammateState extends ServerState {

  public IntroduceMafiaToTheirTeammateState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    ArrayList<Player> mafias = this.getMafias();
    ArrayList<String> messages = this.createIntroductionMessages(mafias);
    this.narrator.broadcast(String.join("\n", messages), mafias);
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
