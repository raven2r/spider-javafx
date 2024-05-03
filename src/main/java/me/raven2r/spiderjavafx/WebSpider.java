package me.raven2r.spiderjavafx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSpider {
    private List<String> visitedUrls;
    private List<String> extractedLinks;
    private int maxPages;

    public WebSpider(int maxPages) {
        this.maxPages = maxPages;
        visitedUrls = new ArrayList<>();
        extractedLinks = new ArrayList<>();
    }

    public void crawl(String startUrl) {
        try {
            crawlPage(startUrl);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void crawlPage(final String url) throws Exception {
        String regex = "(?i)https?://(?:[a-z0-9\\p{L}-]+\\.)+[a-z\\p{L}-]{2,}(?::\\d{1,5})?(?:/[\\w\\p{Punct}]*)*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url.strip());

        if (url.isEmpty() || !matcher.matches()) {
            System.out.println(url.isEmpty() + " " + !matcher.matches());
            System.out.println(url);
            throw new Exception("Wrong URL");
        }

        if (visitedUrls.size() >= maxPages || visitedUrls.contains(url)) {
            return;
        }
        visitedUrls.add(url);

        try {
            URL currentUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) currentUrl.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder pageContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                pageContent.append(line);
            }
            reader.close();

            extractLinks(pageContent.toString());

            for (String link : extractedLinks) {
                crawlPage(link);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractLinks(String pageContent) {
        Pattern pattern = Pattern.compile("href=\"(.*?)\"");
        Matcher matcher = pattern.matcher(pageContent);

        while (matcher.find()) {
            String link = matcher.group(1);
            if (link.startsWith("http")) {
                extractedLinks.add(link);
            }
        }
    }

    public int getNumberOfVisitedURLs() {
        return visitedUrls.size();
    }

    public List<String> getExtractedLinks() {
        return new ArrayList<String>(extractedLinks);
    }

    public static void main(String[] args) throws Exception {
        String startUrl = "https://example.com";
        int maxPages = 10;

        WebSpider spider = new WebSpider(maxPages);
        spider.crawl(startUrl);

        List<String> extractedLinks = spider.getExtractedLinks();
        System.out.println("Extracted Links:");
        for (String link : extractedLinks) {
            System.out.println(link);
        }
    }
}
