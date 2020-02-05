package application;

import database.Connectivity;
import database.CreateTable;
import database.InsertRecord;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @FXML
    private Label connStat;
    @FXML
    private Button connect;
    @FXML
    private Button disconnect;
    @FXML
    private Button tableCreate;
    @FXML
    private Button tableDelete;
    @FXML
    private Button recordInsert;
    @FXML
    private Button recordDelete;
    @FXML
    private Button exit;
    @FXML
    private TextField tableNameField;
    @FXML
    private TextField recordField;
    @FXML
    private ComboBox<String> tableList;
    @FXML
    private ComboBox<String> recordList;

    private static AppController instance;

    private final String disconnectedLabel = "Currently not connected...";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connStat.setText(disconnectedLabel);

        disableButtons(true);

        tableList.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                recordPooler(newValue);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void getInstance(Stage stage) throws IOException {
        if (instance == null) {
            FXMLLoader loader = new FXMLLoader(AppController.class.getResource("/app.fxml"));
            Parent root = loader.load();
            instance = loader.getController();

            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    @FXML
    private void onConnect()  {
        try {
            Connectivity.INSTANCE.dbConnect();
            tablePooler();

            String connectedLabel = "Connected...";
            connStat.setText(connectedLabel);

            disableButtons(false);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDisconnect() {
        Connectivity.INSTANCE.dbDisconnect();
        connStat.setText(disconnectedLabel);

        //clear data
        tableNameField.clear();
        recordField.clear();
        tableList.getItems().clear();
        recordList.getItems().clear();

        disableButtons(true);
    }

    @FXML
    private void onExit() {
        exit.getScene().getWindow().hide();
    }

    @FXML
    private void onTableCreate() {
        try {
            CreateTable cr = new CreateTable();
            cr.createTable(tableNameField.getText());

            tableNameField.clear();
            tablePooler();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onTableDelete() {
        try {
            CreateTable cr = new CreateTable();
            cr.deleteTable(tableList.getValue());

            tablePooler();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRecordCreate() {
        try {
            InsertRecord rec = new InsertRecord();
            rec.createRecord(tableList.getValue(), recordField.getText());

            recordField.clear();
            recordPooler(tableList.getValue());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRecordDelete() {
        try {
            InsertRecord rec = new InsertRecord();
            rec.deleteRecord(tableList.getValue(), recordList.getValue());

            recordPooler(tableList.getValue());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void tablePooler() throws SQLException {
        // Execute a query
        String sql = "SHOW TABLES";
        ResultSet rs = Connectivity.INSTANCE.getStmt().executeQuery(sql);

        // Extract data from result set
        ArrayList<String> dataList = new ArrayList<>();
        while (rs.next()) {
            // Retrieve by table name
            String data = rs.getString("TABLE_NAME");
            dataList.add(data);
        }

        tableList.setItems(observableArrayList(dataList));
    }

    public void recordPooler(String table) throws SQLException {
        if (table != null) {
            // Execute a query
            String sql = "SELECT * FROM \"" + table + "\"";
            ResultSet rs = Connectivity.INSTANCE.getStmt().executeQuery(sql);

            // Extract data from result set
            ArrayList<String> recList = new ArrayList<>();
            while (rs.next()) {
                // Retrieve by column name
                String rec = rs.getString("data");
                recList.add(rec);
            }

            recordList.setItems(observableArrayList(recList));
        }
    }

    public void disableButtons(boolean bool) {
        connect.setDisable(!bool);
        disconnect.setDisable(bool);
        tableCreate.setDisable(bool);
        tableDelete.setDisable(bool);
        recordInsert.setDisable(bool);
        recordDelete.setDisable(bool);
    }

}

