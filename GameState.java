import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GameState implements Serializable {
  private boolean isInPsychologistState;
  private boolean isInMayorState;
  private boolean isInProfessionalState;
  private String professionalTargetUsername;
  private boolean isVotingEnabled;
  private ArrayList<String> alivePlayerUsernames;
  private volatile HashMap<String, String> votes;

  public GameState() {
    this.isInPsychologistState = false;
    this.isInProfessionalState = false;
    this.professionalTargetUsername = "";
    this.isInMayorState = false;
    this.alivePlayerUsernames = new ArrayList<>();
    this.isVotingEnabled = false;
    this.votes = new HashMap<>();
  }

  public void clearVotes() {
    this.votes = new HashMap<>();
  }

  public String getProfessionalTarget() {
    return this.professionalTargetUsername;
  }

  public void setProfessionalTarget(String username) {
    this.professionalTargetUsername = username;
  }

  public void initVotingChoices(ArrayList<String> choices) {
    this.clearVotes();
    for (String c : choices) {
      this.votes.put(c, "< ! >");
    }
  }

  public boolean isAValidVote(String vote) {
    return this.votes.keySet().contains(vote);
  }

  public HashMap<String, String> getVotes() {
    return this.votes;
  }

  public void setIsVotingEnabled(boolean status) {
    this.isVotingEnabled = status;
  }

  public boolean getIsVotingEnabled() {
    return this.isVotingEnabled;
  }

  public void updateVote(String voter, String votee) {
    this.votes.put(voter, votee);
  }

  public ArrayList<String> getAlivePlayerUsernames() {
    return this.alivePlayerUsernames;
  }

  public void setAlivePlayerUsernames(ArrayList<String> usernames) {
    this.alivePlayerUsernames = usernames;
  }

  public void setIsInPsychologistState(boolean status) {
    this.isInPsychologistState = status;
  }

  public boolean getIsInPsychologistState() {
    return this.isInPsychologistState;
  }

  public boolean getIsInProfessionalState() {
    return this.isInProfessionalState;
  }

  public void setIsInProfessionalState(boolean status) {
    this.isInProfessionalState = status;
  }

  public void setIsInMayorState(Boolean state) {
    this.isInMayorState = state;
  }

  public boolean getIsInMayorState() {
    return this.isInMayorState;
  }

  public String serialize() {
    return UTIL.objectToString(this);
  }

  public static GameState deserialize(String serializedData) {
    GameState gs = UTIL.objectFromString(serializedData, GameState.class);
    return gs;
  }
}
