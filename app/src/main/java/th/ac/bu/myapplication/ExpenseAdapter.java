package th.ac.bu.myapplication;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;


/** Adapter แสดงรายการค่าใช้จ่าย (ใช้กับ RecyclerView) */
public class ExpenseAdapter
        extends RecyclerView.Adapter<ExpenseAdapter.ExpenseVH> {

    /* ---------- field ---------- */
    private final List<Expense> expenses = new ArrayList<>();

    private final OnExpenseEditListener   editListener;
    private final OnExpenseDeleteListener deleteListener;

    /* ---------- listener ---------- */
    public interface OnExpenseEditListener   { void onExpenseEdit  (@NonNull Expense exp);               }
    public interface OnExpenseDeleteListener { void onExpenseDelete(@NonNull Expense exp,int position); }

    public ExpenseAdapter(@NonNull List<Expense> data,
                          @NonNull OnExpenseEditListener   editListener,
                          @NonNull OnExpenseDeleteListener deleteListener) {

        this.expenses.addAll(data);       // copy รายการเริ่มต้น
        this.editListener   = editListener;
        this.deleteListener = deleteListener;
    }

    /* ---------- public helper ---------- */
    /** ใช้ตอน Snapshot‑Listener ส่งข้อมูลใหม่มา */
    public void replaceData(@NonNull List<Expense> newData) {
        expenses.clear();
        expenses.addAll(newData);
        notifyDataSetChanged();
    }

    /* ---------- binding ---------- */
    @NonNull @Override
    public ExpenseVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseVH h, int pos) {
        Expense exp = expenses.get(pos);

        h.tvName.setText(exp.getName());
        h.tvAmount.setText(String.valueOf(exp.getAmount()));
        h.tvCategory.setText(
                TextUtils.isEmpty(exp.getCategory()) ? "ไม่ระบุ" : exp.getCategory());
        String formattedDate = "ไม่ระบุวันที่";
        if (!TextUtils.isEmpty(exp.getDate())) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                formattedDate = outputFormat.format(inputFormat.parse(exp.getDate()));
            } catch (Exception e) {
                formattedDate = exp.getDate();  // fallback
            }
        }
        String category = exp.getCategory();
        if (category == null || category.trim().isEmpty()) {
            category = "Other"; // ← ตรงนี้จะทำให้โชว์ Other แทน "ไม่ระบุ"
        }
        h.tvCategory.setText(category);

        h.tvDate.setText(formattedDate);

        h.tvDate.setText(formattedDate);
        /* ---------- click ---------- */
        h.btnEdit  .setOnClickListener(_v -> editListener  .onExpenseEdit  (exp));
        h.btnDelete.setOnClickListener(_v -> deleteListener.onExpenseDelete(exp, h.getAdapterPosition()));

    }

    @Override public int getItemCount() { return expenses.size(); }

    /* ---------- View‑Holder ---------- */
    static class ExpenseVH extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView  tvName, tvAmount, tvCategory, tvDate;
        Button    btnEdit, btnDelete;

        ExpenseVH(@NonNull View v) {
            super(v);
            tvName     = v.findViewById(R.id.tvExpenseName);
            tvAmount   = v.findViewById(R.id.tvExpenseAmount);
            tvCategory = v.findViewById(R.id.tvExpenseType); // id เดิมใช้ต่อได้
            tvDate = v.findViewById(R.id.tvExpenseDate);
            btnEdit    = v.findViewById(R.id.btnEdit);
            btnDelete  = v.findViewById(R.id.btnDelete);
        }
    }
}
