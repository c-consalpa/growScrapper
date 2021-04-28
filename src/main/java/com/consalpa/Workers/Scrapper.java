package com.consalpa.Workers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Scrapper implements Runnable {
    private final String SITE_MAP_PAGE = "https://growerline.ru/map.php";
    public void run() {

        try {
            Document doc = Jsoup.connect(SITE_MAP_PAGE).get();
            Elements el = doc.select("ul.map-level-0 ul.map-level-1");

            System.out.println(el);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
