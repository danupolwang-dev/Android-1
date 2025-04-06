package th.ac.bu.myapplication;

public class Expense {
    private String name;
    private Long amount;  // ต้องเป็น Long ตามที่บันทึกใน AddExpense
    private String category;
    private String date;
    private String note;

    // Constructor ที่ไม่มีพารามิเตอร์ จำเป็นสำหรับ Firestore
    public Expense() {}

    public Expense(String name, Long amount, String category, String date, String note) {
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    // Getter และ Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
