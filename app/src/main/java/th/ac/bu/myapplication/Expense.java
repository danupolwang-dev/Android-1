package th.ac.bu.myapplication;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.io.Serializable;
import java.util.Date;

@IgnoreExtraProperties        // ถ้าเอกสาร Firestore มีฟิลด์เกินกว่าที่ประกาศไว้ จะไม่ error
public class Expense implements Serializable {
    private String  id;          // ← id ของเอกสาร (ไม่ต้องเขียนลง Firestore)
    private String  name;
    private double  amount;
    private String  category;    // ← ชื่อจริงที่เก็บใน Firestore
    private String  date;        // 16/04/2025 …
    private String  description; // หมายเหตุ
    private String  uid;         // ใครเป็นคนบันทึก (ใช้สำหรับ query)
    // ⬆️ ส่วน field
    private Date timestamp;


    /* ------------ constructor ว่าง (จำเป็นให้ Firestore mapping) ------------ */
    public Expense() {}

    /* ------------ getter/setter หลัก ------------ */
    public String getId() { return id; }
    public void   setId(String id) { this.id = id; }

    public String getName()  { return name; }
    public void   setName(String name) { this.name = name; }

    public double getAmount() { return amount; }
    public void   setAmount(double amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void   setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void   setDate(String date) { this.date = date; }

    public String getDescription() { return description; }
    public void   setDescription(String description) { this.description = description; }

    public String getUid() { return uid; }
    public void   setUid(String uid) { this.uid = uid; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    /* -------------------------------------------------------------------------
       “สะพาน” เมทอดเก่า (getType / setType)  ชี้มาที่ฟิลด์เดียวกัน (category)
       ------------------------------------------------------------------------- */
    @Exclude          // ไม่ต้องเขียน field ซ้ำในเอกสาร Firestore
    public String getType() {
        return category;
    }
    @Exclude
    public void setType(String type) {
        this.category = type;
    }
}
