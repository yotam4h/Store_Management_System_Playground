import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test
{
    private static final int MAX_THREADS = System.getenv("NUMBER_OF_PROCESSORS") != null ? Integer.parseInt(System.getenv("NUMBER_OF_PROCESSORS")) : 1;

    ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

}
