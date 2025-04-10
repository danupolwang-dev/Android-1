package th.ac.bu.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddExpense extends Fragment {

    private EditText etExpenseName, etAmount, etCategory, etDate, etNote;
    private Button btnSaveExpense;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_add_expense, container, false);

        // Bind views
        etExpenseName = view.findViewById(R.id.et_expense_name);
        etAmount = view.findViewById(R.id.et_amount);
        etCategory = view.findViewById(R.id.et_category);
        etDate = view.findViewById(R.id.et_date);
        etNote = view.findViewById(R.id.et_note);
        btnSaveExpense = view.findViewById(R.id.btn_save_expense);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        btnSaveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data from EditText
                String expenseName = etExpenseName.getText().toString().trim();
                String amountStr = etAmount.getText().toString().trim();
                String category = etCategory.getText().toString().trim();
                String date = etDate.getText().toString().trim();
                String note = etNote.getText().toString().trim();

                // Basic validation
                if(expenseName.isEmpty() || amountStr.isEmpty() || category.isEmpty() || date.isEmpty()){
                    Toast.makeText(getContext(), "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert amount from String to Long
                Long amountValue;
                try {
                    amountValue = Long.parseLong(amountStr);
                } catch(NumberFormatException e) {
                    Toast.makeText(getContext(), "กรุณากรอกจำนวนเงินให้ถูกต้อง", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create Map to store expense data
                Map<String, Object> expense = new HashMap<>();
                expense.put("name", expenseName);
                expense.put("amount", amountValue);
                expense.put("category", category);
                expense.put("date", date);
                expense.put("note", note);

                // Save to Firestore
                db.collection("expenses")
                        .add(expense)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
                            // Clear fields or navigate back
                            clearFields();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "บันทึกข้อมูลล้มเหลว: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        return view;
    }

    private void clearFields() {
        etExpenseName.setText("");
        etAmount.setText("");
        etCategory.setText("");
        etDate.setText("");
        etNote.setText("");
    }
}
