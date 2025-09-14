package th.ac.bu.myapplication;

import java.util.List;
import java.util.ArrayList;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;

/** แก้ไขรายการค่าใช้จ่าย */
public class EditExpenseFragment extends Fragment {

    /* ---------- factory ---------- */
    public static EditExpenseFragment newInstance(@NonNull Expense exp) {
        EditExpenseFragment f = new EditExpenseFragment();
        Bundle b = new Bundle();
        b.putSerializable("expense", exp);     // Expense implements Serializable
        f.setArguments(b);
        return f;
    }

    /* ---------- member ---------- */
    private Expense          expense;   // รายการเดิม
    private FirebaseFirestore db;

    private EditText etName, etAmount, etDate, etNote;
    private Spinner  spType;

    /* ---------- life‑cycle ---------- */
    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            expense = (Expense) getArguments().getSerializable("expense");
        }
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        /* ---------- bind ---------- */
        etName   = v.findViewById(R.id.etExpenseName);
        etAmount = v.findViewById(R.id.etExpenseAmount);
        etDate   = v.findViewById(R.id.etExpenseDate);
        etNote   = v.findViewById(R.id.etExpenseNote);
        spType   = v.findViewById(R.id.spinnerExpenseType);
        Button btnSave   = v.findViewById(R.id.btnSave);
        Button btnCancel = v.findViewById(R.id.btnCancel);

        // Step 1: เตรียมรายการเริ่มต้น
        String[] defaultTypes = {"Food","Travel","Utilities","Entertainment","Other"};
        List<String> typeList = new ArrayList<>();
        for (String t : defaultTypes) typeList.add(t);

        // Step 2: ดึงค่าประเภทที่เคยเลือก
        String currentType = (expense != null && expense.getCategory() != null) ? expense.getCategory() : "";

        // Step 3: ถ้าประเภทนั้นไม่อยู่ใน default → เพิ่มเข้าไป
        if (!TextUtils.isEmpty(currentType) && !typeList.contains(currentType)) {
            typeList.add(0, currentType);  // เพิ่มไว้ด้านบนสุด
        }

        // Step 4: สร้าง Adapter และตั้งค่า Spinner
        ArrayAdapter<String> ad = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                typeList
        );
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(ad);

        // Step 5: ตั้งค่าให้เลือกค่าเดิม
        int p = ad.getPosition(currentType);
        if (p >= 0) spType.setSelection(p);


        /* ---------- action ---------- */
        btnSave.setOnClickListener(_v -> saveChange());
        btnCancel.setOnClickListener(_v ->
                requireActivity().getSupportFragmentManager().popBackStack());
        // Step 6: เติมข้อมูลเก่าเข้า EditText
        if (expense != null) {
            etName.setText(expense.getName());
            etAmount.setText(String.valueOf(expense.getAmount()));
            etDate.setText(expense.getDate());
            etNote.setText(expense.getDescription());  // หมายเหตุ
        }
    }


    /* ---------- helper ---------- */
    private void showDateDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (DatePicker dp, int y, int m, int d) ->
                        etDate.setText(String.format("%02d/%02d/%04d", d, m+1, y)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveChange() {
        /* ---- validate ---- */
        String name  = etName.getText().toString().trim();
        String amtSt = etAmount.getText().toString().trim();
        String date  = etDate.getText().toString().trim();
        String note  = etNote.getText().toString().trim();
        String type  = spType.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(amtSt) || TextUtils.isEmpty(date)) {
            Toast.makeText(requireContext(),"กรุณากรอกข้อมูลให้ครบ",Toast.LENGTH_SHORT).show();
            return;
        }

        double amt;
        try { amt = Double.parseDouble(amtSt); }
        catch (NumberFormatException e) {
            Toast.makeText(requireContext(),"จำนวนเงินไม่ถูกต้อง",Toast.LENGTH_SHORT).show();
            return;
        }

        /* ---- update local object ---- */
        expense.setName(name);
        expense.setAmount(amt);
        expense.setDate(date);
        expense.setDescription(note);
        expense.setType(type);
        expense.setTimestamp(new Date());          // ⭐ time‑stamp ใหม่ทุกครั้ง

        /* ---- push to Firestore (merge) ---- */
        db.collection("expenses")
                .document(expense.getId())
                .set(expense, SetOptions.merge())        // merge = ไม่ทับ uid เดิม
                .addOnSuccessListener(_v -> showDone())
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "อัปเดตล้มเหลว: "+e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showDone() {
        new AlertDialog.Builder(requireContext())
                .setMessage("บันทึกสำเร็จ")
                .setPositiveButton("ตกลง", (d, w) -> {
                    d.dismiss();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .show();
    }
}
