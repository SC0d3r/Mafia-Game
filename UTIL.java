public class UTIL {
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

}
