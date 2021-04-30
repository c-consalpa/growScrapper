package com.consalpa.Workers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SiteRoamer implements Runnable {
    private int requestsMade = 0;

    private final String BASE_URL = "https://growerline.ru";
    private final String SITE_MAP_PAGE = "map.php";

    public void run() {
        Document doc = null;
        try {
            doc = Jsoup.connect(BASE_URL + "/" + SITE_MAP_PAGE).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Get all categories from site map
        Elements allCategoryLinks = doc.select("ul.map-level-1 > li");
        // create Map<category, subCategoryItems>
        Map<String, List<String>> catalogue = new LinkedHashMap<>();
        allCategoryLinks.forEach(category -> {
            String categoryName  = category.select("li > a").attr("href");
            Elements subElems = category.select("li > ul > li > a");
            List<String> category_subList = new ArrayList<>();

            subElems.forEach(element -> {
                String link = element.attr("href");
                        category_subList.add(link.substring(0, link.length() - 1));
            });
            catalogue.put(categoryName, category_subList);
        });

        // Create directories to store scrapping results
        try {
            createOutputDirs(catalogue);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            traverseCatalogue(catalogue);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void traverseCatalogue(Map<String, List<String>> cat) throws IOException {
        // loop through top-level categories
        for (Map.Entry<String, List<String>> entry : cat.entrySet()) {
            String subcategoryName = entry.getKey().replace("/", "");
            // loop through subcategories
            FileWriter fw = new FileWriter("output/" + subcategoryName + ".txt");

            for (String subCat : entry.getValue()) {
                String subCategoryURL = new StringBuilder(BASE_URL)
                        .append(subCat)
                        .toString();
                //get link to each product
                Document document = Jsoup.connect(subCategoryURL).get();
                Elements links = document.select("a.catalog-section__item__link");
                    // and finally parse each product
                    for (Element link : links) {
                        String productRelLink = link.attr("href");
                        String productURL = new StringBuilder(BASE_URL)
                                            .append(productRelLink)
                                            .toString();
                        // fetch data from each page
                        String scrappedData = scrapPageData(productURL);
                        fw.write(scrappedData);
                        fw.write("\r\n");
                        fw.flush();
                    }




            }
            fw.close();
        }
        System.out.println("Total requests count: " + requestsMade);
    }

    private String scrapPageData(String productPageURL) throws IOException {
        System.out.println("Analyzing page: " + productPageURL);
        StringBuilder buffer = new StringBuilder();
        buffer.append(productPageURL);
        buffer.append("\r\n");


        // GET each subcategory page
        Document document = Jsoup.connect(productPageURL).get();
        requestsMade++;
        Elements h1_s = document.select("h1");
        buffer.append((h1_s.get(0)).text());

//        Element descriptionBox = document.getElementById("catalog-detail-descr");
        // if page doesn't have description - break
//        if (descriptionBox == null) {
//            buffer.append("Description is not available for:").append(productPageURL);
//            return buffer.toString();
//        }
//        descriptionBox.traverse(new NodeVisitor() {
//            public void head(Node node, int depth) {
////                System.out.println("Entering tag: " + node.nodeName());
//
//                if (node instanceof TextNode) {
//                    TextNode textNode = (TextNode) node;
//                    if (textNode.text().length() > 3) buffer .append(textNode.text());
//                }
//            }
//            public void tail(Node node, int depth) {
////                System.out.println("Exiting tag: " + node.nodeName());
//            }
//        });
        buffer.append("\r\n********");
        System.out.println("Analysis complete");
        return buffer.toString();
    }

    private void createOutputDirs(Map<String, List<String>> cat) throws IOException {
        // TODO: use SL4J
        File outputRoot = new File("output");
        if (!outputRoot.exists()) {
            System.out.println("Creating output directory: " + outputRoot);
            outputRoot.mkdirs();
        }

        for (Map.Entry<String, List<String>> entry : cat.entrySet()) {
            String categoryName = entry.getKey();
            // cut first and last slashes from string
            String fName = categoryName.replace("/", "") + ".txt";


            File catFile = new File(outputRoot, fName);
            if (!catFile.exists()) {
                catFile.getParentFile().mkdirs();
                catFile.createNewFile();
            }
        }
    }
}
