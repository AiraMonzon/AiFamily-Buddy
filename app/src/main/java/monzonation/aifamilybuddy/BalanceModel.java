package monzonation.aifamilybuddy;

public class BalanceModel {

    private  String id;
    private  String name;
    private  String date;
    private  long balance;
    private  long expense;
    private  long income;

    public BalanceModel(String id, String name, String date, long balance, long expense, long income) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.balance = balance;
        this.expense = expense;
        this.income = income;
    }

    public BalanceModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getExpense() {
        return expense;
    }

    public void setExpense(long expense) {
        this.expense = expense;
    }

    public long getIncome() {
        return income;
    }

    public void setIncome(long income) {
        this.income = income;
    }
}
