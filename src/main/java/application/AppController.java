package application;

import database.Connectivity;
import database.Create;
import database.Record;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    private Button exit;
    @FXML
    private TextField tableNameField;
    @FXML
    private ComboBox<String> tableRecord;
    @FXML
    private TextField dataField;
    @FXML
    private ComboBox<String> tableForDelete;
    @FXML
    private ComboBox<String> tableToRecord;
    @FXML
    private ComboBox<String> recordForDelete;

    private static AppController instance;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableToRecord.valueProperty().addListener((observable, oldValue, newValue) -> {
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
    private void onConnect() throws SQLException {
        Connectivity.INSTANCE.dbConnect();
        tablePooler();
    }

    @FXML
    private void onDisconnect() {
        Connectivity.INSTANCE.dbDisconnect();
    }

    @FXML
    private void onExit() {
        exit.getScene().getWindow().hide();
    }

    @FXML
    private void onTableCreate() {
        try {
            Create cr = new Create();
            cr.createTable(tableNameField.getText());

            tablePooler();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onTableDelete() {
        try {
            Create cr = new Create();
            cr.deleteTable(tableForDelete.getValue());

            tablePooler();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRecordCreate() {
        try {
            Record rec = new Record();
            rec.createRecord(tableRecord.getValue(), dataField.getText());

            recordPooler(tableToRecord.getValue());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRecordDelete() {
        try {
            Record rec = new Record();
            rec.deleteRecord(tableToRecord.getValue(), recordForDelete.getValue());

            recordPooler(tableToRecord.getValue());
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
            // Retrieve by column name
            String data = rs.getString("TABLE_NAME");
            dataList.add(data);
        }

        tableRecord.setItems(observableArrayList(dataList));
        tableForDelete.setItems(observableArrayList(dataList));
        tableToRecord.setItems(observableArrayList(dataList));
    }

    public void recordPooler(String table) throws SQLException {
        if (table != null) {
            // Execute a query
            String sql = "SELECT * FROM \"" + table + "\"";
            ResultSet rs = Connectivity.INSTANCE.getStmt().executeQuery(sql);

            // Extract data from result set
            ArrayList<String> recordList = new ArrayList<>();
            while (rs.next()) {
                // Retrieve by column name
                String rec = rs.getString("data");
                recordList.add(rec);
            }

            recordForDelete.setItems(observableArrayList(recordList));
        }
    }

}

