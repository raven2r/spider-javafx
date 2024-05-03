module me.raven2r.spiderjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.kordamp.ikonli.javafx;

    opens me.raven2r.spiderjavafx to javafx.fxml;
    exports me.raven2r.spiderjavafx;
}