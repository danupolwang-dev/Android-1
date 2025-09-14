package th.ac.bu.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvTodayExpense, tvHomeInfo;
    private Button btnAddExpense, btnViewExpenses, btnViewStatistics;
    private FirebaseFirestore db;
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout home.xml สำหรับหน้า HomeFragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ผูก View จาก home.xml
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvTodayExpense = view.findViewById(R.id.tvTodayExpense);
        tvHomeInfo = view.findViewById(R.id.tvHomeInfo);
        btnAddExpense = view.findViewById(R.id.btnAddExpense);
        btnViewExpenses = view.findViewById(R.id.btnViewExpenses);
        btnViewStatistics = view.findViewById(R.id.btnViewStatistics);


        db = FirebaseFirestore.getInstance();

        setupWelcomeMessage();
        setupTodayExpense();
        tvHomeInfo.setText("ยินดีต้อนรับเข้าสู่ระบบจัดการค่าใช้จ่าย");

        // กำหนด event ของ 3 ปุ่มหลัก
        btnAddExpense.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddExpenseFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnViewExpenses.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ExpenseListFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnViewStatistics.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new StatisticFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    /**
     * ดึงอีเมลของผู้ใช้จาก FirebaseAuth และแสดงใน tvWelcome
     */
    private void setupWelcomeMessage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            tvWelcome.setText("สวัสดี, " + currentUser.getEmail());
        } else {
            tvWelcome.setText("สวัสดี, ผู้เยี่ยมชม");
        }
    }

    /**
     * ดึงยอดใช้จ่ายวันนี้จาก Firestore
     * สมมุติว่าใน collection "expenses" มี field "timestamp" กับ "amount"
     */
    private void setupTodayExpense() {
        // กำหนดช่วงเวลาของวันนี้
        long now = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now);
        // ตั้งต้นวัน: 0:00:00
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startOfDay = cal.getTime();

        // ตั้งสิ้นวัน: 23:59:59
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date endOfDay = cal.getTime();

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            tvTodayExpense.setText("วันนี้ใช้ไป: 0 บาท");
            return;
        }

        db.collection("expenses")
                .whereEqualTo("uid", userId)
                .whereGreaterThanOrEqualTo("timestamp", new Timestamp(startOfDay))
                .whereLessThanOrEqualTo ("timestamp", new Timestamp(endOfDay))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    float sum = 0f; // ประกาศใน lambda scope
                    for (var doc : queryDocumentSnapshots.getDocuments()) {
                        Number amtNum = doc.getLong("amount");
                        if (amtNum == null) {
                            amtNum = doc.getDouble("amount");
                        }
                        float amt = (amtNum != null) ? amtNum.floatValue() : 0f;
                        sum += amt;
                    }
                    tvTodayExpense.setText("วันนี้ใช้ไป: " + sum + " บาท");
                })
                .addOnFailureListener(e -> {
                    tvTodayExpense.setText("วันนี้ใช้ไป: ");
                    Log.e(TAG, "Error fetching today's expense: ", e);
                });
    }
}
