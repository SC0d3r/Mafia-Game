import java.util.ArrayList;

public class WinLoseCheckState extends ServerState {

  public WinLoseCheckState(Narrator narrator, GameServer server) {
    super(narrator, server);
  }

  @Override
  public boolean run() {
    if (this.didMafiaWin()) {
      this.narrator.changeState(STATES.MAFIA_WINNER);
    }
    if (this.didCitizenWin()) {
      this.narrator.changeState(STATES.CITIZEN_WINNER);
    }

    this.narrator.changeState(STATES.ENABLE_CHAT);
    return false;
  }

  public boolean didCitizenWin() {
    return this.getMafias().size() == 0;
  }

  private boolean didMafiaWin() {
    return this.getMafias().size() >= this.getCitizens().size();
  }

  private ArrayList<Player> getMafias() {
    ArrayList<Player> result = new ArrayList<>();
    for (Player p : this.gameServer.getReadyPlayers()) {
      if (p.getRole() == ROLE.GOD_FATHER || p.getRole() == ROLE.MAFIA_MEMBER || p.getRole() == ROLE.DR_LACTER)
        result.add(p);
    }
    return result;
  }

  public ArrayList<Player> getCitizens() {
    ArrayList<Player> result = new ArrayList<>();
    for (Player p : this.gameServer.getReadyPlayers()) {
      if (p.getRole() == ROLE.GOD_FATHER || p.getRole() == ROLE.DR_LACTER || p.getRole() == ROLE.MAFIA_MEMBER)
        continue;
      result.add(p);
    }
    return result;
  }
}
