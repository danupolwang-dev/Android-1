package th.ac.bu.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ExpenseListActivity extends AppCompatActivity {

    private RecyclerView rvExpenses;
    private List<Expense> mExpenses;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);
        mExpenses = new ArrayList<>();
        rvExpenses = findViewById(R.id.rvExpenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(expenseList);
        rvExpenses.setAdapter(expenseAdapter);

        db = FirebaseFirestore.getInstance();

        // ดึงข้อมูลจาก Firestore
        CollectionReference expenseRef = db.collection("expenses");
        expenseRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    expenseList.clear();
                    for(DocumentSnapshot doc : task.getResult()){
                        Expense expense = doc.toObject(Expense.class);
                        expenseList.add(expense);
                    }
                    expenseAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ExpenseListActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ExpenseList", "Error getting documents", task.getException());
                }
            }
        });
    }
}
