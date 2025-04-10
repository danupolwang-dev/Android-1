package th.ac.bu.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.tvExpenseName.setText(expense.getName());
        holder.tvExpenseAmount.setText(String.valueOf(expense.getAmount())); // แปลง Long เป็น String
        holder.tvExpenseCategoryDate.setText(expense.getCategory() + " | " + expense.getDate());
        holder.tvExpenseNote.setText(expense.getNote());
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvExpenseName, tvExpenseAmount, tvExpenseCategoryDate, tvExpenseNote;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExpenseName = itemView.findViewById(R.id.tvExpenseName);
            tvExpenseAmount = itemView.findViewById(R.id.tvExpenseAmount);
            tvExpenseCategoryDate = itemView.findViewById(R.id.tvExpenseCategoryDate);
            tvExpenseNote = itemView.findViewById(R.id.tvExpenseNote);
        }
    }
<<<<<<< HEAD
<<<<<<< HEAD
}
=======
}
>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d
=======
}
>>>>>>> 7e612c6dae86244e0e689f262b6167a85841303d
