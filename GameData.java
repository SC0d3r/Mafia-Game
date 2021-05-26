import java.util.ArrayList;

enum DAYTIME {
  DAY, NIGHT;

  public static String toString(DAYTIME dt) {
    if (dt == DAYTIME.DAY)
      return "Day";
    return "Night";
  }
}

/**
 * manages the daytime update and news part of the application
 */
public class GameData {
  private static GameData instance;
  private volatile DAYTIME daytime;
  private ArrayList<String> news;
  private boolean didMafiaWin;
  private boolean isVotingGotCanceled;

  private GameData() {
    this.daytime = DAYTIME.NIGHT;
    this.news = new ArrayList<>();
    this.isVotingGotCanceled = false;
    this.didMafiaWin = false;
  }

  public void setDidMafiaWin(boolean status) {
    this.didMafiaWin = status;
  }

  public boolean getDidMafiaWin() {
    return this.didMafiaWin;
  }

  public void setIsVotingGotCanceled(boolean state) {
    this.isVotingGotCanceled = state;
  }

  public void clearNews() {
    this.news = new ArrayList<>();
  }

  public boolean isThereAnyNews() {
    return !this.news.isEmpty();
  }

  public boolean getIsVotingGotCanceled() {
    return this.isVotingGotCanceled;
  }

  public boolean isDay() {
    return this.daytime == DAYTIME.DAY;
  }

  public void addNews(String news) {
    this.news.add(news);
  }

  public ArrayList<String> getNews() {
    return this.news;
  }

  public void updateDayTime(DAYTIME dt) {
    this.daytime = dt;
  }

  public DAYTIME getDayTime() {
    return this.daytime;
  }

  public static GameData getInstance() {
    if (instance != null)
      return instance;
    instance = new GameData();
    return instance;
  }

}
