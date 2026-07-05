package at.fhj.peakflowmate.ui.diary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import at.fhj.peakflowmate.R;
import at.fhj.peakflowmate.data.model.Measurement;
import at.fhj.peakflowmate.data.repository.MeasurementRepository;
import at.fhj.peakflowmate.utils.ZoneSettings;

/**
 * Aktivität zur Anzeige des Peak-Flow-Tagebuchs.
 * <p>
 * Diese Aktivität stellt gespeicherte Peak-Flow-Messungen in Form einer
 * Liste und eines Liniendiagramms dar. Zusätzlich werden statistische
 * Kennwerte berechnet sowie ein Export der Messdaten als CSV-Datei
 * bereitgestellt.
 */
public class DiaryActivity extends AppCompatActivity {

    private MeasurementRepository repository;
    private MeasurementAdapter adapter;
    private LineChart chart;
    private TextView tvLast, tvAvg, tvMax, tvMin;
    private int currentDays = 7;

    private LiveData<List<Measurement>> currentDataLiveData;
    private LiveData<Integer> personalBestLiveData;

    /**
     * Initialisiert die Benutzeroberfläche des Tagebuchs.
     * <p>
     * Richtet die Liste der Messungen, das Diagramm sowie die
     * Schaltflächen zur Auswahl des Zeitraums und zum Export der
     * Messdaten ein.
     *
     * @param savedInstanceState enthält den zuvor gespeicherten Zustand der
     *                           Aktivität oder {@code null}, falls keiner
     *                           vorhanden ist.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diary);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new MeasurementRepository(this);
        adapter = new MeasurementAdapter();
        chart = findViewById(R.id.chart);
        tvLast = findViewById(R.id.tvLast);
        tvAvg = findViewById(R.id.tvAvg);
        tvMax = findViewById(R.id.tvMax);
        tvMin = findViewById(R.id.tvMin);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        setupChart();

        personalBestLiveData = repository.getPersonalBest();
        personalBestLiveData.observe(this, pb -> {
            if (pb != null) adapter.setPersonalBest(pb);
        });

        findViewById(R.id.btn7d).setOnClickListener(v -> loadData(7));
        findViewById(R.id.btn30d).setOnClickListener(v -> loadData(30));
        findViewById(R.id.btnAll).setOnClickListener(v -> loadData(0));
        findViewById(R.id.btnExport).setOnClickListener(v -> exportCsv());

        loadData(7);
    }

    /**
     * Lädt Messdaten für den angegebenen Zeitraum.
     * <p>
     * Die geladenen Daten werden in der Liste und im Diagramm angezeigt.
     * Zusätzlich werden die statistischen Kennwerte aktualisiert.
     *
     * @param days Anzahl der zurückliegenden Tage. Der Wert {@code 0}
     *             lädt sämtliche gespeicherten Messungen.
     */
    private void loadData(int days) {
        currentDays = days;

        currentDataLiveData = (days > 0) ? repository.getSince(days) : repository.getAll();

        if (currentDataLiveData != null) {
            currentDataLiveData.removeObservers(this);
        }

        currentDataLiveData.observe(this, measurements -> {
            if (measurements == null || measurements.isEmpty()) {
                tvLast.setText("–");
                tvAvg.setText("–");
                tvMax.setText("–");
                tvMin.setText("–");
                adapter.setItems(new ArrayList<>());
                chart.clear();
                return;
            }

            adapter.setItems(measurements);
            updateStats(measurements);
            updateChart(measurements);
        });
    }

    /**
     * Berechnet und aktualisiert die statistischen Kennwerte.
     * <p>
     * Angezeigt werden der letzte Messwert, der Durchschnitts-, der
     * Minimal- und der Maximalwert.
     *
     * @param list Liste der auszuwertenden Messungen.
     */
    private void updateStats(List<Measurement> list) {
        int last = list.get(0).getValue();
        int max = last, min = last;
        long sum = 0;

        for (Measurement m : list) {
            int v = m.getValue();
            if (v > max) max = v;
            if (v < min) min = v;
            sum += v;
        }

        tvLast.setText(String.valueOf(last));
        tvAvg.setText(String.valueOf(Math.round((float) sum / list.size())));
        tvMax.setText(String.valueOf(max));
        tvMin.setText(String.valueOf(min));
    }

    /**
     * Konfiguriert das Liniendiagramm zur Darstellung der Messwerte.
     * <p>
     * Legt unter anderem die Achsen, die Darstellung sowie die
     * Interaktionseigenschaften des Diagramms fest.
     */
    private void setupChart() {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
    }

    /**
     * Aktualisiert das Diagramm mit den angegebenen Messdaten.
     * <p>
     * Erstellt die Datenreihe für das Diagramm und zeichnet zusätzlich
     * die Grenzlinien der Peak-Flow-Zonen.
     *
     * @param list Liste der darzustellenden Messungen.
     */
    private void updateChart(List<Measurement> list) {
        List<Measurement> sorted = new ArrayList<>(list);
        Collections.reverse(sorted);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            entries.add(new Entry(i, sorted.get(i).getValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(0xFF1D9E75);
        dataSet.setCircleColor(0xFF1D9E75);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        chart.setData(new LineData(dataSet));

        int greenMin = ZoneSettings.getGreenMin(this);
        int yellowMin = ZoneSettings.getYellowMin(this);

        chart.getAxisLeft().removeAllLimitLines();
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setAxisMaximum(900f);

        LimitLine llGreen = new LimitLine(greenMin, "");
        llGreen.setLineColor(0x331D9E75);
        llGreen.setLineWidth(0f);

        LimitLine llYellow = new LimitLine(yellowMin, "");
        llYellow.setLineColor(0x33EF9F27);
        llYellow.setLineWidth(1f);
        llYellow.enableDashedLine(8f, 4f, 0f);
        llYellow.setLineColor(0xFFEF9F27);

        LimitLine llGreenLine = new LimitLine(greenMin, "");
        llGreenLine.setLineWidth(1f);
        llGreenLine.enableDashedLine(8f, 4f, 0f);
        llGreenLine.setLineColor(0xFF1D9E75);

        chart.getAxisLeft().addLimitLine(llYellow);
        chart.getAxisLeft().addLimitLine(llGreenLine);
        chart.invalidate();
    }

    /**
     * Exportiert sämtliche gespeicherten Messungen als CSV-Datei.
     * <p>
     * Die erzeugte Datei wird im anwendungsspezifischen Speicher
     * abgelegt und kann anschließend über den Android-Freigabedialog
     * mit anderen Anwendungen geteilt werden.
     */
    private void exportCsv() {
        LiveData<List<Measurement>> exportSource = repository.getAll();

        exportSource.observe(this, new androidx.lifecycle.Observer<List<Measurement>>() {
            @Override
            public void onChanged(List<Measurement> measurements) {
                exportSource.removeObserver(this);

                if (measurements == null || measurements.isEmpty()) {
                    Toast.makeText(DiaryActivity.this, R.string.keine_daten_zum_exportieren, Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuilder sb = new StringBuilder();
                sb.append(getString(R.string.datum_uhrzeit_wert_l_min_technik));

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                Date reusableDate = new Date();

                for (Measurement m : measurements) {
                    reusableDate.setTime(m.getTimestamp());
                    sb.append(dateFormat.format(reusableDate)).append(",");
                    sb.append(timeFormat.format(reusableDate)).append(",");
                    sb.append(m.getValue()).append(",");
                    sb.append(m.getTechniqueQuality() != null ? m.getTechniqueQuality() : "").append("\n");
                }

                File file = new File(getExternalFilesDir(null), "peak_flow_export.csv");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(sb.toString().getBytes());
                } catch (Exception e) {
                    Toast.makeText(DiaryActivity.this, R.string.export_fehlgeschlagen, Toast.LENGTH_SHORT).show();
                    return;
                }

                Uri uri = FileProvider.getUriForFile(DiaryActivity.this, getPackageName() + ".fileprovider", file);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/csv");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, getString(R.string.export_als_csv)));
            }
        });
    }
}
