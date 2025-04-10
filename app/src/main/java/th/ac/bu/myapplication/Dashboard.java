package th.ac.bu.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Dashboard extends AppCompatActivity {

    private TextView tvDailyExpense, tvMonthlyExpense;
    private Button btnAddExpense, btnExpenseList;  // ประกาศตัวแปรปุ่มใหม่

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // ผูก View กับ Layout
        tvDailyExpense = findViewById(R.id.tv_daily_expense);
        tvMonthlyExpense = findViewById(R.id.tv_monthly_expense);
        btnAddExpense = findViewById(R.id.btn_add_expense);
        btnExpenseList = findViewById(R.id.btn_expense_list); // ผูกปุ่มกับ layout

        // ตัวอย่างการแสดงข้อมูล
        tvDailyExpense.setText("ยอดค่าใช้จ่ายรายวัน: 1,000 บาท");
        tvMonthlyExpense.setText("ยอดค่าใช้จ่ายรายเดือน: 30,000 บาท");

        // เมื่อกดปุ่ม "เพิ่มรายการค่าใช้จ่าย"
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, AddExpenseFragment.class));
            }
        });

        // เมื่อกดปุ่ม "ดูรายการค่าใช้จ่าย"
        btnExpenseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, ExpenseListFragment.class));
            }
        });
    }
}
