import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * this class will spawns 4 server on ports:2222,3333,4444,5555 that clients can
 * connect and start the game
 */
public class Main {
  public static void main(String[] args) {
    ExecutorService myExecutor = Executors.newCachedThreadPool();
    myExecutor.execute(new GameServer(2222));
    myExecutor.execute(new GameServer(3333));
    myExecutor.execute(new GameServer(4444));
    myExecutor.execute(new GameServer(5555));
  }

}
