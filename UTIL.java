import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.Gson;

public class UTIL {
  public static final Gson gson = new Gson();

  public static void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void sleepMSM(int min, int second, int ms) {
    int ONE_SECOND = 1000;
    min = ONE_SECOND * 60 * min;
    second = second * ONE_SECOND;
    int total = min + second + ms;

    sleep(total);
  }

  /** Read the object from Base64 string. */
  public static <T> T objectFromString(String s, Class<T> cls) {
    return gson.fromJson(s, cls);
  }

  public static String objectToString(Serializable o) {
    return gson.toJson(o);
  }

  public static void setTimerFor(int seconds, ArrayList<Player> players) {
    new UpdateTimer(seconds, players).run();
  }

  public static void setTimerFor(int seconds, ArrayList<Player> players, GameState gameState) {
    new UpdateTimer(seconds, players, gameState).run();
  }
}
