package th.ac.bu.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddExpense extends AppCompatActivity {

  private EditText etExpenseName, etAmount, etCategory, etDate, etNote;
  private Button btnSaveExpense;
  private FirebaseFirestore db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_expense);

    // ผูก View กับ Layout
    etExpenseName = findViewById(R.id.et_expense_name);
    etAmount = findViewById(R.id.et_amount);
    etCategory = findViewById(R.id.et_category);
    etDate = findViewById(R.id.et_date);
    etNote = findViewById(R.id.et_note);
    btnSaveExpense = findViewById(R.id.btn_save_expense);

    // เรียกใช้งาน Firestore
    db = FirebaseFirestore.getInstance();

    btnSaveExpense.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        // ดึงข้อมูลจาก EditText
        String expenseName = etExpenseName.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        // ตรวจสอบข้อมูลพื้นฐาน
        if(expenseName.isEmpty() || amountStr.isEmpty() || category.isEmpty() || date.isEmpty()){
          Toast.makeText(AddExpense.this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
          return;
        }

        // แปลงค่า amount จาก String เป็น Long
        Long amountValue;
        try {
          amountValue = Long.parseLong(amountStr);
        } catch(NumberFormatException e) {
          Toast.makeText(AddExpense.this, "กรุณากรอกจำนวนเงินให้ถูกต้อง", Toast.LENGTH_SHORT).show();
          return;
        }

        // สร้าง Map เพื่อเก็บข้อมูลค่าใช้จ่าย
        Map<String, Object> expense = new HashMap<>();
        expense.put("name", expenseName);
        expense.put("amount", amountValue); // บันทึกเป็น Long
        expense.put("category", category);
        expense.put("date", date);
        expense.put("note", note);

        // บันทึกข้อมูลลงใน Firestore
        db.collection("expenses")
                .add(expense)
                .addOnSuccessListener(documentReference -> {
                  Toast.makeText(AddExpense.this, "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
                  // เคลียร์ฟิลด์หรือกลับไปยังหน้าก่อนหน้า
                  finish();
                })
                .addOnFailureListener(e -> {
                  Toast.makeText(AddExpense.this, "บันทึกข้อมูลล้มเหลว: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
      }
    });
  }
}
