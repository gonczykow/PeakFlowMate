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

/**
 * Adapter zur Darstellung gespeicherter Peak-Flow-Messungen in einem
 * {@link RecyclerView}.
 * <p>
 * Der Adapter zeigt den Messwert, das Datum der Messung sowie eine
 * farbliche Kennzeichnung der entsprechenden Peak-Flow-Zone anhand
 * des persönlichen Bestwerts an.
 */
public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.ViewHolder> {

    private List<Measurement> items = new ArrayList<>();
    private int personalBest = 500;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    /**
     * Erstellt einen neuen Adapter für die Anzeige von Messungen.
     */
    public MeasurementAdapter() {
    }

    /**
     * Aktualisiert die anzuzeigenden Messungen.
     *
     * @param items Liste der darzustellenden Messungen.
     */
    public void setItems(List<Measurement> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    /**
     * Legt den persönlichen Bestwert fest.
     * <p>
     * Der Bestwert wird zur farblichen Bewertung der einzelnen
     * Messungen verwendet.
     *
     * @param pb persönlicher Bestwert in l/min.
     */
    public void setPersonalBest(int pb) {
        this.personalBest = pb;
        notifyDataSetChanged();
    }

    /**
     * Erstellt einen neuen ViewHolder für einen Listeneintrag.
     *
     * @param parent übergeordnete ViewGroup.
     * @param viewType Typ des anzuzeigenden Elements.
     * @return ein neuer {@link ViewHolder}.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_measurement, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Bindet die Daten einer Messung an einen ViewHolder.
     *
     * @param holder ViewHolder, der aktualisiert werden soll.
     * @param position Position der Messung in der Liste.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Measurement m = items.get(position);

        holder.tvValue.setText(String.valueOf(m.getValue()));
        holder.tvDate.setText(dateFormat.format(new Date(m.getTimestamp())));

        float percent = (float) m.getValue() / personalBest * 100;
        int color;
        if (percent >= 80) color = 0xFF1D9E75;
        else if (percent >= 60) color = 0xFFEF9F27;
        else color = 0xFFE24B4A;
        holder.zoneIndicator.setBackgroundColor(color);
    }

    /**
     * Gibt die Anzahl der anzuzeigenden Messungen zurück.
     *
     * @return Anzahl der Listeneinträge.
     */
    @Override
    public int getItemCount() { return items.size(); }

    /**
     * ViewHolder für einen einzelnen Eintrag der Messungsliste.
     * <p>
     * Enthält die Views zur Darstellung des Messwerts, des Datums
     * sowie der farblichen Zonenkennzeichnung.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvValue, tvDate;
        View zoneIndicator;

        /**
         * Erstellt einen neuen ViewHolder.
         *
         * @param v View eines Listeneintrags.
         */
        ViewHolder(View v) {
            super(v);
            tvValue = v.findViewById(R.id.tvItemValue);
            tvDate = v.findViewById(R.id.tvItemDate);
            zoneIndicator = v.findViewById(R.id.zoneIndicator);
        }
    }
}
