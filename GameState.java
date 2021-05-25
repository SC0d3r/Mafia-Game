import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GameState implements Serializable {
  private boolean isInPsychologistState;
  private boolean isInMayorState;
  private boolean isInProfessionalState;
  private String professionalTargetUsername;
  private boolean isInDetectiveState;
  private String detectiveSuspicionTarget;
  private boolean isVotingEnabled;
  private boolean isInDieHardState;
  private boolean isDieHardRequestedInvestigation;
  private ArrayList<String> alivePlayerUsernames;
  private volatile HashMap<String, String> votes;

  public GameState() {
    this.isInPsychologistState = false;
    this.isInProfessionalState = false;
    this.isInDetectiveState = false;
    this.isInDieHardState = false;
    this.isDieHardRequestedInvestigation = false;
    this.professionalTargetUsername = "";
    this.detectiveSuspicionTarget = "";
    this.isInMayorState = false;
    this.alivePlayerUsernames = new ArrayList<>();
    this.isVotingEnabled = false;
    this.votes = new HashMap<>();
  }

  public boolean getIsDieHardRequestedInvestigation() {
    return this.isDieHardRequestedInvestigation;
  }

  public void setIsDieHardRequestedInvestigation(boolean status) {
    this.isDieHardRequestedInvestigation = status;
  }

  public boolean getIsInDieHardState() {
    return this.isInDieHardState;
  }

  public void setIsInDieHardState(boolean status) {
    this.isInDieHardState = status;
  }

  public void clearVotes() {
    this.votes = new HashMap<>();
  }

  public String getProfessionalTarget() {
    return this.professionalTargetUsername;
  }

  public boolean getIsInDetectiveState() {
    return this.isInDetectiveState;
  }

  public void setIsInDetectiveState(boolean status) {
    this.isInDetectiveState = status;
  }

  public String getDetectiveSuspicionTarget() {
    return this.detectiveSuspicionTarget;
  }

  public void setDetectiveSuspicionTarget(String username) {
    this.detectiveSuspicionTarget = username;
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
