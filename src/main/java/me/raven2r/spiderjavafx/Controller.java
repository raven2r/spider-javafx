package me.raven2r.spiderjavafx;

import javafx.animation.AnimationTimer;
import javafx.css.Match;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    @FXML
    private Label welcomeText;

    @FXML
    TextArea urlTextArea;
    @FXML
    TextArea visitsTextArea;
    @FXML
    ListView<String> listView;
    @FXML
    WebView webView;
    @FXML
    ProgressBar progressBar;

    public Controller() {
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onStartButtonClick() {
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2) {
                    String selectedItem = listView.getSelectionModel().getSelectedItem();
                    webView.getEngine().load(selectedItem);
                }
            }
        });

        Pattern patter = Pattern.compile("[0-9]+");

        if(!Pattern.matches("[0-9]*", visitsTextArea.getText())) {
            visitsTextArea.clear();
            return;
        }

        int numberOfVisits = Integer.parseInt(visitsTextArea.getText());
        WebSpider spider = new WebSpider(numberOfVisits);

        long startTime = System.nanoTime();
        int[] seconds = {0};

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsedTime = (now - startTime) / 1_000_000_000;

                if(elapsedTime >= seconds[0] + 1) {
                    seconds[0]++;
                    progressBar.setProgress(spider.getNumberOfVisitedURLs() / (double)numberOfVisits);
                }
            }
        };

        Runnable crawlRunnable = () -> {
            if(!urlTextArea.getText().equals("")) {
                timer.start();
                spider.crawl(urlTextArea.getText());

                spider.getExtractedLinks().forEach(l -> {
                    System.out.println(l);
                    listView.getItems().add(l);
                });

                timer.stop();
                progressBar.setProgress(0);
            }
        };

        var crawlThread = new Thread(crawlRunnable);

        try {
            crawlThread.start();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}