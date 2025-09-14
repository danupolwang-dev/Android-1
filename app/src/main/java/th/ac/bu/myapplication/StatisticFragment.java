package th.ac.bu.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class StatisticFragment extends Fragment {
    private PieChart pieChart;
    private TextView tvSummary;
    private RecyclerView rvDetails;
    private FirebaseFirestore db;

    private final List<DetailItem> fullDetailList = new ArrayList<>();
    private Spinner spinnerPeriod;
    private String selectedCategory = "All";
    private String selectedPeriod = "daily";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChart = view.findViewById(R.id.pieChart);
        tvSummary = view.findViewById(R.id.tvSummary);
        rvDetails = view.findViewById(R.id.rvDetails);
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod);

        rvDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDetails.setAdapter(new DetailAdapter(new ArrayList<>()));

        db = FirebaseFirestore.getInstance();
        setupPieChart();

        String[] periodOptions = {"daily", "weekly", "monthly", "yearly", "all"};
        String[] displayPeriods = {"รายวัน", "รายสัปดาห์", "รายเดือน", "รายปี", "ทั้งหมด"};
        spinnerPeriod.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, displayPeriods));
        spinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPeriod = periodOptions[position];
                loadSummary();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadSummary();
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);
        pieChart.setCenterText("สถิติการใช้จ่าย");
        pieChart.setCenterTextSize(18f);
        pieChart.animateY(1000);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
    }

    private Date[] getDateRange(String period) {
        Calendar sc = Calendar.getInstance();
        Calendar ec = Calendar.getInstance();

        switch (period) {
            case "daily":
                sc.set(Calendar.HOUR_OF_DAY, 0); sc.set(Calendar.MINUTE, 0); sc.set(Calendar.SECOND, 0); sc.set(Calendar.MILLISECOND, 0);
                ec.set(Calendar.HOUR_OF_DAY, 23); ec.set(Calendar.MINUTE, 59); ec.set(Calendar.SECOND, 59); ec.set(Calendar.MILLISECOND, 999);
                break;
            case "weekly":
                sc.set(Calendar.DAY_OF_WEEK, sc.getFirstDayOfWeek());
                sc.set(Calendar.HOUR_OF_DAY, 0); sc.set(Calendar.MINUTE, 0); sc.set(Calendar.SECOND, 0); sc.set(Calendar.MILLISECOND, 0);
                ec.setTime(sc.getTime());
                ec.add(Calendar.DAY_OF_MONTH, 6);
                ec.set(Calendar.HOUR_OF_DAY, 23); ec.set(Calendar.MINUTE, 59); ec.set(Calendar.SECOND, 59); ec.set(Calendar.MILLISECOND, 999);
                break;
            case "monthly":
                sc.set(Calendar.DAY_OF_MONTH, 1);
                sc.set(Calendar.HOUR_OF_DAY, 0); sc.set(Calendar.MINUTE, 0); sc.set(Calendar.SECOND, 0); sc.set(Calendar.MILLISECOND, 0);
                ec.set(Calendar.DAY_OF_MONTH, ec.getActualMaximum(Calendar.DAY_OF_MONTH));
                ec.set(Calendar.HOUR_OF_DAY, 23); ec.set(Calendar.MINUTE, 59); ec.set(Calendar.SECOND, 59); ec.set(Calendar.MILLISECOND, 999);
                break;
            case "yearly":
                sc.set(Calendar.MONTH, Calendar.JANUARY); sc.set(Calendar.DAY_OF_MONTH, 1);
                sc.set(Calendar.HOUR_OF_DAY, 0); sc.set(Calendar.MINUTE, 0); sc.set(Calendar.SECOND, 0); sc.set(Calendar.MILLISECOND, 0);
                ec.set(Calendar.MONTH, Calendar.DECEMBER); ec.set(Calendar.DAY_OF_MONTH, 31);
                ec.set(Calendar.HOUR_OF_DAY, 23); ec.set(Calendar.MINUTE, 59); ec.set(Calendar.SECOND, 59); ec.set(Calendar.MILLISECOND, 999);
                break;
            case "all":
                sc.setTimeInMillis(0);
                ec.setTimeInMillis(System.currentTimeMillis());
                ec.set(Calendar.HOUR_OF_DAY, 23); ec.set(Calendar.MINUTE, 59); ec.set(Calendar.SECOND, 59); ec.set(Calendar.MILLISECOND, 999);
                break;
        }
        return new Date[]{sc.getTime(), ec.getTime()};
    }

    private void loadSummary() {
        Date[] range = getDateRange(selectedPeriod);
        if (range == null) return;
        Date startDate = range[0];
        Date endDate = range[1];

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference expensesRef = db.collection("expenses");

        expensesRef.whereEqualTo("uid", uid)
                .whereGreaterThanOrEqualTo("timestamp", new Timestamp(startDate))
                .whereLessThanOrEqualTo("timestamp", new Timestamp(endDate))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, Float> categoryTotals = new HashMap<>();
                    fullDetailList.clear();
                    float totalAmount = 0f;
                    int colorIndex = 0;

                    for (var doc : querySnapshot.getDocuments()) {
                        String cat = doc.getString("category");
                        if (cat == null) cat = "ไม่ระบุ";
                        if (!selectedCategory.equals("All") && !selectedCategory.equals(cat)) continue;

                        Number amtNum = doc.getDouble("amount");
                        if (amtNum == null) amtNum = doc.getLong("amount");
                        float amt = (amtNum != null) ? amtNum.floatValue() : 0f;
                        categoryTotals.put(cat, categoryTotals.getOrDefault(cat, 0f) + amt);
                    }

                    List<PieEntry> entries = new ArrayList<>();
                    for (Map.Entry<String, Float> e : categoryTotals.entrySet()) {
                        entries.add(new PieEntry(e.getValue(), e.getKey()));
                        int color = ColorTemplate.MATERIAL_COLORS[colorIndex % ColorTemplate.MATERIAL_COLORS.length];
                        fullDetailList.add(new DetailItem(e.getKey(), e.getValue(), color));
                        totalAmount += e.getValue();
                        colorIndex++;
                    }

                    if (entries.isEmpty()) {
                        Toast.makeText(getContext(), "ไม่มีข้อมูลค่าใช้จ่ายในช่วงนี้", Toast.LENGTH_SHORT).show();
                        pieChart.clear();
                        tvSummary.setText("");
                        ((DetailAdapter) rvDetails.getAdapter()).updateData(new ArrayList<>());
                        return;
                    }

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    dataSet.setValueTextColor(Color.WHITE);
                    dataSet.setValueTextSize(12f);
                    dataSet.setValueFormatter(new PercentFormatter(pieChart));
                    dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                    dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                    dataSet.setValueLinePart1Length(0f);
                    dataSet.setValueLinePart2Length(0f);
                    dataSet.setValueLineColor(Color.TRANSPARENT);
                    dataSet.setSliceSpace(0f);
                    pieChart.setDrawHoleEnabled(false);

                    pieChart.setData(new PieData(dataSet));
                    pieChart.invalidate();

                    tvSummary.setText("รวม: " + totalAmount + " บาท");

                    // อัปเดตรายการใน RecyclerView
                    ((DetailAdapter) rvDetails.getAdapter()).updateData(fullDetailList);
                });
    }

    class DetailItem {
        String category;
        float amount;
        int color;
        DetailItem(String category, float amount, int color) {
            this.category = category;
            this.amount = amount;
            this.color = color;
        }
    }

    class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {
        private List<DetailItem> detailList;

        DetailAdapter(List<DetailItem> detailList) {
            this.detailList = detailList;
        }

        void updateData(List<DetailItem> newData) {
            this.detailList = newData;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
            return new DetailViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
            DetailItem item = detailList.get(position);
            holder.tvCategory.setText(item.category);
            holder.tvAmount.setText(String.format(Locale.getDefault(), "%.1f บาท", item.amount));
            holder.viewColor.setBackgroundColor(item.color);
        }

        @Override
        public int getItemCount() {
            return detailList.size();
        }

        class DetailViewHolder extends RecyclerView.ViewHolder {
            View viewColor;
            TextView tvCategory, tvAmount;

            DetailViewHolder(@NonNull View itemView) {
                super(itemView);
                viewColor = itemView.findViewById(R.id.viewColor);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvAmount = itemView.findViewById(R.id.tvCategoryAmount);
            }
        }
    }
}
