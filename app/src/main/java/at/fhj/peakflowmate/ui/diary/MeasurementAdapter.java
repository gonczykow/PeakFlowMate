package at.fhj.peakflowmate.ui.diary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import at.fhj.peakflowmate.R;
import at.fhj.peakflowmate.data.model.Measurement;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.ViewHolder> {

    private List<Measurement> items = new ArrayList<>();
    private int personalBest = 500;

    public MeasurementAdapter() {;
    }

    public void setItems(List<Measurement> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setPersonalBest(int pb) {
        this.personalBest = pb;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_measurement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Measurement m = items.get(position);

        holder.tvValue.setText(String.valueOf(m.getValue()));

        String date = new SimpleDateFormat("dd.MM.yyyy HH:mm",
                Locale.getDefault()).format(new Date(m.getTimestamp()));
        holder.tvDate.setText(date);

        float percent = (float) m.getValue() / personalBest * 100;
        int color;
        if (percent >= 80) color = 0xFF1D9E75;
        else if (percent >= 60) color = 0xFFEF9F27;
        else color = 0xFFE24B4A;
        holder.zoneIndicator.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvValue, tvDate;
        View zoneIndicator;

        ViewHolder(View v) {
            super(v);
            tvValue = v.findViewById(R.id.tvItemValue);
            tvDate = v.findViewById(R.id.tvItemDate);
            zoneIndicator = v.findViewById(R.id.zoneIndicator);
        }
    }
}
