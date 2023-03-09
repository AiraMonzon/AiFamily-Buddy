package monzonation.aifamilybuddy;

public class IncomeModel {
    private long id;
    private  String name;
    private  String amount;
    private  String date;

    private  String details;

    public IncomeModel(long id, String name, String amount, String date, String details) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.details = details;
    }

    public IncomeModel() {
    }

    @Override
    public String toString() {
        return "IncomeModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount='" + amount + '\'' +
                ", date='" + date + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
