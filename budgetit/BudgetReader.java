package budgetit;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class BudgetReader {
    public static List<Transaction> getTransactionsFromBudget(File file) throws FileNotFoundException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        FileReader fileReader = new FileReader(file);
        CSVReader csvReader = new CSVReader(fileReader);

        csvReader.forEach(strings -> {
            double amount = Double.parseDouble(strings[1]);
            String name = strings[4]
                    .replaceAll("PURCHASE AUTHORIZED ON", "")
                    .replaceAll(" CARD ", "")
                    .trim()
                    .replaceAll("[0-9.]", "");
            transactions.add(new Transaction(name, amount));
        });
        return transactions;
    }
}
