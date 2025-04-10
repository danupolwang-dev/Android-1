package th.ac.bu.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddExpenseFragment extends Fragment {

  private EditText etExpenseName, etAmount, etCategory, etDate, etNote;
  private Button btnSaveExpense;
  private FirebaseFirestore db;

  // สร้าง Fragment โดย inflate layout (ควรเปลี่ยนชื่อไฟล์ layout ให้เป็น fragment_add_expense.xml เพื่อไม่สับสน)
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    // ใช้ไฟล์ layout ที่เหมาะกับ Fragment เช่น R.layout.fragment_add_expense
<<<<<<< HEAD
<<<<<<< HEAD
    return inflater.inflate(R.layout.fragment_add_expense, container, false);
=======
    return inflater.inflate(R.layout.activity_add_expense, container, false);
>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d
=======
    return inflater.inflate(R.layout.activity_add_expense, container, false);
>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d
  }

  // หลังจาก View ถูกสร้างเสร็จ onViewCreated จะถูกเรียก
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // ผูก View กับตัวแปร (ใช้ view.findViewById แทน findViewById แบบ Activity)
    etExpenseName = view.findViewById(R.id.et_expense_name);
    etAmount = view.findViewById(R.id.et_amount);
    etCategory = view.findViewById(R.id.et_category);
    etDate = view.findViewById(R.id.et_date);
    etNote = view.findViewById(R.id.et_note);
    btnSaveExpense = view.findViewById(R.id.btn_save_expense);

    // เรียกใช้งาน Firestore
    db = FirebaseFirestore.getInstance();

    // กดปุ่มบันทึก
    btnSaveExpense.setOnClickListener(v -> {
      String expenseName = etExpenseName.getText().toString().trim();
      String amountStr = etAmount.getText().toString().trim();
      String category = etCategory.getText().toString().trim();
      String date = etDate.getText().toString().trim();
      String note = etNote.getText().toString().trim();

      // ตัวอย่างการตรวจสอบความถูกต้อง
      if (TextUtils.isEmpty(expenseName) || TextUtils.isEmpty(amountStr) ||
              TextUtils.isEmpty(category) || TextUtils.isEmpty(date)) {
        Toast.makeText(getContext(), "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
        return;
      }

      Long amountValue;
      try {
        amountValue = Long.parseLong(amountStr);
      } catch (NumberFormatException e) {
        Toast.makeText(getContext(), "กรุณากรอกจำนวนเงินเป็นตัวเลข", Toast.LENGTH_SHORT).show();
        return;
      }

      // สร้าง map สำหรับบันทึกลง Firestore
      Map<String, Object> expense = new HashMap<>();
      expense.put("name", expenseName);
      expense.put("amount", amountValue);
      expense.put("category", category);
      expense.put("date", date);
      expense.put("note", note);

      // บันทึกข้อมูลลง Firestore
      db.collection("expenses")
              .add(expense)
              .addOnSuccessListener(documentReference -> {
                Toast.makeText(getContext(), "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
                // ตัวอย่าง: กลับไปหน้า HomeFragment หลังบันทึกเสร็จ
                if (getActivity() != null) {
                  getActivity().getSupportFragmentManager().beginTransaction()
<<<<<<< HEAD
<<<<<<< HEAD
                          .replace(R.id.fragment_container, new ExpenseListFragment())
=======
                          .replace(R.id.fragment_container, new HomeFragment())
>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d
=======
                          .replace(R.id.fragment_container, new HomeFragment())
>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d
                          .commit();
                }
              })
              .addOnFailureListener(e ->
                      Toast.makeText(getContext(), "บันทึกไม่สำเร็จ: " + e.getMessage(), Toast.LENGTH_SHORT).show()
              );
    });
  }
}
