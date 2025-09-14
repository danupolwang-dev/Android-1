package th.ac.bu.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ExpenseListFragment extends Fragment {

    private RecyclerView rv;
    private Spinner spinnerFilter;
    private ExpenseAdapter adapter;
    private final List<Expense> expenses = new ArrayList<>();
    private final List<Expense> allExpenses = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv = view.findViewById(R.id.rvExpenses);
        spinnerFilter = view.findViewById(R.id.spinner_filter);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExpenseAdapter(
                expenses,
                exp -> openEdit(exp),
                (exp, pos) -> confirmDelete(exp, pos)
        );
        rv.setAdapter(adapter);

        setupSpinner();

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            toast("ยังไม่ได้ล็อกอิน");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("expenses")
                .whereEqualTo("uid", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        Log.e("ExpenseList", "listen", e);
                        return;
                    }

                    List<Expense> newData = new ArrayList<>();
                    assert snap != null;
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Expense exp = doc.toObject(Expense.class);
                        if (exp == null) continue;
                        exp.setId(doc.getId());
                        newData.add(exp);
                    }

                    allExpenses.clear();
                    allExpenses.addAll(newData);
                    filterExpenses(spinnerFilter.getSelectedItem().toString());
                });
    }

    private void setupSpinner() {
        String[] types = {"All", "Food", "Travel", "Utilities", "Entertainment","Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                types
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                filterExpenses(selected);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void filterExpenses(String category) {
        List<Expense> filtered = new ArrayList<>();
        for (Expense e : allExpenses) {
            String cat = e.getCategory();
            if (cat == null || cat.trim().isEmpty()) cat = "Other";

            if (category.equals("All") || cat.equalsIgnoreCase(category)) {
                filtered.add(e);
            }
        }
        adapter.replaceData(filtered);
    }

    private void openEdit(Expense e) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, EditExpenseFragment.newInstance(e))
                .addToBackStack(null)
                .commit();
    }

    private void confirmDelete(Expense exp, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("ลบรายการ")
                .setMessage("คุณต้องการลบ \"" + exp.getName() + "\" ใช่หรือไม่?")
                .setPositiveButton("ลบ", (dialog, which) -> deleteExpenseFromFirestore(exp))
                .setNegativeButton("ยกเลิก", null)
                .show();
    }

    private void deleteExpenseFromFirestore(Expense exp) {
        FirebaseFirestore.getInstance()
                .collection("expenses")
                .document(exp.getId())
                .delete()
                .addOnSuccessListener(aVoid -> toast("ลบรายการแล้ว"))
                .addOnFailureListener(e -> toast("ลบไม่สำเร็จ: " + e.getMessage()));
    }

    private void toast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
