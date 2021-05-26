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
  private boolean isInDrLacterState;
  private boolean isDrLacterSavedHimselfAllready;
  private boolean isInMafiaGatheringState;
  private boolean isInGodFatherState;
  private String godFatherTargetUsername;
  private String drLacterCuresUsername;
  private boolean isInDrCityState;
  private String drCitySaveTarget;
  private boolean isInIntroductionState;
  private boolean isDrCitySavedHimselfAllready;
  private boolean isGameFinished;
  private ArrayList<String> usernames;
  private ArrayList<String> alivePlayerUsernames;
  private ArrayList<String> aliveMafiaUsernames;
  private ArrayList<String> aliveCitizenUsernames;
  private volatile HashMap<String, String> votes;
  private boolean isThereAnyUserOnline;

  public GameState() {
    this.usernames = new ArrayList<>();// this field is for make sure that usernames in game is unique
    this.isThereAnyUserOnline = false;
    this.isGameFinished = false;
    this.isInPsychologistState = false;
    this.isInProfessionalState = false;
    this.isInDetectiveState = false;
    this.isInDrLacterState = false;
    this.isInDieHardState = false;
    this.isInIntroductionState = false;
    this.isInMafiaGatheringState = false;
    this.isInDrCityState = false;
    this.drCitySaveTarget = "";
    this.drLacterCuresUsername = "";
    this.godFatherTargetUsername = "";
    this.isDrLacterSavedHimselfAllready = false;
    this.isDieHardRequestedInvestigation = false;
    this.isInGodFatherState = false;
    this.isDrCitySavedHimselfAllready = false;

    this.professionalTargetUsername = "";
    this.detectiveSuspicionTarget = "";
    this.isInMayorState = false;
    this.isVotingEnabled = false;
    this.votes = new HashMap<>();
    this.alivePlayerUsernames = new ArrayList<>();
    this.aliveMafiaUsernames = new ArrayList<>();
    this.aliveCitizenUsernames = new ArrayList<>();
  }

  public void setIsThereAnyUserOnline(boolean status) {
    this.isThereAnyUserOnline = status;
  }

  public boolean getIsThereAnyUserOnline() {
    return this.isThereAnyUserOnline;
  }

  public void setUsernames(ArrayList<String> usernames) {
    this.usernames = usernames;
  }

  public ArrayList<String> getUsernames() {
    return this.usernames;
  }

  public void addUsername(String username) {
    this.usernames.add(username);
  }

  public void removeUsername(String username) {
    this.usernames.remove(username);
  }

  public void setIsGameFinished(boolean status) {
    this.isGameFinished = status;
  }

  public boolean getIsGameFinished() {
    return this.isGameFinished;
  }

  public boolean getIsInDrCityState() {
    return this.isInDrCityState;
  }

  public boolean getIsInIntroductionState() {
    return this.isInIntroductionState;
  }

  public void setIsInIntroductionState(boolean status) {
    this.isInIntroductionState = status;
  }

  public void setIsInDrCityState(boolean status) {
    this.isInDrCityState = status;
  }

  public String getDrCitySaveTarget() {
    return this.drCitySaveTarget;
  }

  public void setIsDrCitySavedHimselfAllready(boolean status) {
    this.isDrCitySavedHimselfAllready = status;
  }

  public boolean getIsDrCitySavedHimselfAllready() {
    return this.isDrCitySavedHimselfAllready;
  }

  public void setDrCitySaveTarget(String username) {
    this.drCitySaveTarget = username;
  }

  public boolean getIsInGodFatherState() {
    return this.isInGodFatherState;
  }

  public void setIsInGodFatherState(boolean status) {
    this.isInGodFatherState = status;
  }

  public void setGodFatherTargetUsername(String username) {
    this.godFatherTargetUsername = username;
  }

  public String getGodFatherTargetUsername() {
    return this.godFatherTargetUsername;
  }

  public void setIsInMafiaGatheringState(boolean status) {
    this.isInMafiaGatheringState = status;
  }

  public boolean getIsInMafiaGatheringState() {
    return this.isInMafiaGatheringState;
  }

  public ArrayList<String> getAliveCitizenUsernames() {
    return this.aliveCitizenUsernames;
  }

  public void setAliveCitizenUsernames(ArrayList<String> usernames) {
    this.aliveCitizenUsernames = usernames;
  }

  public ArrayList<String> getAliveMafiaUsernames() {
    return this.aliveMafiaUsernames;
  }

  public void setAliveMafiaUsernames(ArrayList<String> usernames) {
    this.aliveMafiaUsernames = usernames;
  }

  public void setDrLacterCuresUsername(String username) {
    this.drLacterCuresUsername = username;
  }

  public String getDrLacterCuresUsername() {
    return this.drLacterCuresUsername;
  }

  public boolean getIsDrLacterSavedHimselfAllready() {
    return this.isDrLacterSavedHimselfAllready;
  }

  public void setIsDrLacterSavedHimselfAllready(boolean status) {
    this.isDrLacterSavedHimselfAllready = status;
  }

  public void setIsInDrLacterState(boolean status) {
    this.isInDrLacterState = status;
  }

  public boolean getIsInDrLacterState() {
    return this.isInDrLacterState;
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
