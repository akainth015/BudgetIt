package budgetit;

public class Transaction {
    private final String vendorName;
    private final double amount;

    public Transaction(String vendorName, double amount) {
        this.vendorName = vendorName;
        this.amount = amount;
    }

    public String getVendorName() {
        return vendorName;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "vendorName='" + vendorName + '\'' +
                ", amount=" + amount +
                '}';
    }
}
