package budgetit;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Spending by Category");

        FileChooser fileChooser = new FileChooser();

        Label filePath = new Label("Choose a budget file");
        Button button = new Button("Choose");
        button.setOnAction(event -> {
            File budget = fileChooser.showOpenDialog(primaryStage);
            if (budget == null) return;
            filePath.setText(budget.getAbsolutePath());

            try {
                List<Transaction> transactions = BudgetReader.getTransactionsFromBudget(budget);
                transactions.forEach(transaction -> {
                    String category = TransactionClassifier.getMostSimilarCategory(transaction.getVendorName());
                    while (category == null) {
                        TextInputDialog categoryInput = new TextInputDialog();
                        categoryInput.setTitle("Enter a category");
                        categoryInput.setHeaderText("Enter a category for the following transaction");
                        categoryInput.setContentText(transaction.getVendorName());
                        Optional<String> input = categoryInput.showAndWait();
                        if (input.isPresent()) {
                            category = input.get();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setContentText("You must enter a category!");
                            alert.showAndWait();
                        }
                    }
                    TransactionClassifier.getCategories().putIfAbsent(category, new ArrayList<>());
                    TransactionClassifier.getCategories().get(category).add(transaction);
                });
                TransactionClassifier.getCategories().forEach((category, transactionsInCategory) -> pieChartData.add(new PieChart.Data(category, transactionsInCategory.stream().mapToDouble(Transaction::getAmount).sum())));
            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("BudgetIt could not find the file you specified");
                alert.show();
            }
        });

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(filePath, button, chart);
        flowPane.setPadding(new Insets(8));
        FlowPane.setMargin(filePath, new Insets(0, 8, 0, 0));

        Scene scene = new Scene(flowPane);

        primaryStage.setTitle("BudgetIt");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
