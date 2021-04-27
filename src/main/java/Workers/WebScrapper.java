package Workers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebScrapper implements Runnable {
private final String HOME_PAGE_URL = "https://growerline.ru/";
private final String CATALOGUE_CLASS_NAME  = "catalog-parent-section";


    public void run() {
        try {
            Document doc = Jsoup.connect(HOME_PAGE_URL).get();
            Elements root = doc.getElementsByClass("unchor_catalog");
            root.
            els.addClass("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
