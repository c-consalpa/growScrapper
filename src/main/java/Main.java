import Workers.WebScrapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    public static void main(String[] args) {
        Thread t1 = new Thread(new WebScrapper());
        t1.start();

    }
}
