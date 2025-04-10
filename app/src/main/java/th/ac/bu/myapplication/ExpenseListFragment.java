package th.ac.bu.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExpenseListFragment extends Fragment {

    private RecyclerView rvExpenses;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout สำหรับ ExpenseListFragment (สร้าง fragment_expense_list.xml ใน res/layout)
        return inflater.inflate(R.layout.fragment_expense_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvExpenses = view.findViewById(R.id.rvExpenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));

        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(expenseList);
        rvExpenses.setAdapter(expenseAdapter);

        db = FirebaseFirestore.getInstance();

        // ดึงข้อมูลจาก Firestore จากคอลเล็กชัน "expenses"
        CollectionReference expenseRef = db.collection("expenses");
        expenseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                expenseList.clear();
                for (DocumentSnapshot doc : task.getResult()) {
                    Expense expense = doc.toObject(Expense.class);
                    expenseList.add(expense);
                }
                expenseAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ExpenseListFragment", "Error getting documents", task.getException());
            }
        });
    }
}
