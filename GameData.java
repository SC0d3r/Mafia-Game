enum DAYTIME {
  DAY, NIGHT;

  public static String toString(DAYTIME dt) {
    if (dt == DAYTIME.DAY)
      return "Day";
    return "Night";
  }
}

public class GameData {
  private static GameData instance;
  private volatile DAYTIME daytime;

  private GameData() {
    this.daytime = DAYTIME.NIGHT;
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
