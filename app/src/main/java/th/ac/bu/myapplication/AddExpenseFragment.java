package th.ac.bu.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddExpenseFragment extends Fragment {

    private EditText etExpenseName, etExpenseAmount, etExpenseDate, etExpenseNote,  etCustomExpenseType;


    private Spinner spinnerExpenseType;
    private Button btnSave, btnCancel;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout fragment_add_expense.xml for this fragment
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views by their IDs (ต้องตรงกับที่กำหนดใน XML)
        etExpenseName = view.findViewById(R.id.etExpenseName);
        etExpenseAmount = view.findViewById(R.id.etExpenseAmount);
        spinnerExpenseType = view.findViewById(R.id.spinnerExpenseType);
        etCustomExpenseType = view.findViewById(R.id.etCustomExpenseType);
        etExpenseDate = view.findViewById(R.id.etExpenseDate);
        etExpenseNote = view.findViewById(R.id.etExpenseNote);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        db = FirebaseFirestore.getInstance();

        // Setup DatePickerDialog สำหรับ etExpenseDate (ไม่ให้ผู้ใช้พิมพ์เอง)
        etExpenseDate.setFocusable(false);
        etExpenseDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view1, int yearSelected, int monthSelected, int daySelected) {
                    // Format วันที่เป็น dd/MM/yyyy (selectedMonth+1 เพราะเริ่มต้นที่ 0)
                    String dateString = String.format("%02d/%02d/%04d", daySelected, monthSelected + 1, yearSelected);
                    etExpenseDate.setText(dateString);
                }
            }, year, month, day);
            dialog.show();
        });

        // Setup Spinner สำหรับ Expense Type
        String[] expenseTypes = {"Food", "Travel", "Utilities", "Entertainment", "Other"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, expenseTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpenseType.setAdapter(typeAdapter);

        // ตั้งค่าให้ EditText สำหรับระบุประเภทเพิ่มเติม (etCustomExpenseType) ปรากฏเฉพาะเมื่อเลือก "Others"
        spinnerExpenseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = expenseTypes[position];
                if (selected.equals("Others")) {
                    etCustomExpenseType.setVisibility(View.VISIBLE);
                } else {
                    etCustomExpenseType.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // ตั้งค่าเมื่อกดปุ่ม "บันทึก"
        btnSave.setOnClickListener(v -> {
            String expenseName = etExpenseName.getText().toString().trim();
            String amountStr = etExpenseAmount.getText().toString().trim();
            String date = etExpenseDate.getText().toString().trim();
            String note = etExpenseNote.getText().toString().trim();
            // ดึงค่าจาก Spinner สำหรับประเภท
            String selectedType = spinnerExpenseType.getSelectedItem().toString();

            // สำหรับประเภท "Others" ให้ใช้ค่าจาก etCustomExpenseType
            if (selectedType.equals("Others")) {
                selectedType = etCustomExpenseType.getText().toString().trim();

            }

            // ตรวจสอบข้อมูลที่จำเป็น (ไม่ต้องการให้กรอก "หมายเหตุ" ก็ไม่รวมในเงื่อนไข)
            if (TextUtils.isEmpty(expenseName) || TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(date)) {
                Toast.makeText(getContext(), "กรุณากรอกข้อมูลให้ครบ (หมายเหตุไม่จำเป็น)", Toast.LENGTH_SHORT).show();
                return;
            }

            Long amountValue;
            try {
                amountValue = Long.parseLong(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "กรุณากรอกจำนวนเงินเป็นตัวเลข", Toast.LENGTH_SHORT).show();
                return;
            }



            // สร้าง Map สำหรับบันทึกข้อมูลลง Firestore
            Map<String, Object> expenseData = new HashMap<>();
            expenseData.put("name", expenseName);
            expenseData.put("amount", amountValue);
            expenseData.put("category", selectedType);
            expenseData.put("date", date);
            expenseData.put("note", note); // "หมายเหตุ" ไม่บังคับ
            // เพิ่มฟิลด์ "timestamp" โดยใช้ FieldValue.serverTimestamp() เพื่อให้ Firestore กำหนด Timestamp
            expenseData.put("timestamp", FieldValue.serverTimestamp());
            // เพิ่ม uid ของผู้ใช้ที่ล็อกอินเข้ามา
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            expenseData.put("uid", currentUserId);

            // เพิ่มเอกสารใหม่ใน collection "expenses"
            db.collection("expenses").add(expenseData)
                    .addOnSuccessListener(documentReference -> {
                        // แสดง AlertDialog เพื่อแจ้งว่า บันทึกข้อมูลสำเร็จ
                        new AlertDialog.Builder(requireContext())
                                .setTitle("สำเร็จ")
                                .setMessage("บันทึกข้อมูลเรียบร้อย")
                                .setPositiveButton("ตกลง", (dialog, which) -> {
                                    dialog.dismiss();
                                    // หลังจากบันทึกสำเร็จ แทนที่จะไปหน้า ExpenseList ให้กลับไปหน้า HomeFragment
                                    if (getFragmentManager() != null) {
                                        getFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, new HomeFragment())
                                                .commit();
                                    }
                                })
                                .show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "บันทึกข้อมูลล้มเหลว: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // เมื่อกดปุ่ม "ยกเลิก" ให้ออกจากหน้าจอเพิ่ม Expense
        btnCancel.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())  // ตรงนี้ใส่ ID ของ container ที่ MainActivity ใช้
                    .commit();
        });
    }
}
