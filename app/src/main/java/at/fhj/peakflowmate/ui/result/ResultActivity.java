package at.fhj.peakflowmate.ui.result;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.fhj.peakflowmate.MainActivity;
import at.fhj.peakflowmate.R;
import at.fhj.peakflowmate.data.model.Measurement;
import at.fhj.peakflowmate.data.repository.MeasurementRepository;
import at.fhj.peakflowmate.utils.ZoneSettings;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int value = getIntent().getIntExtra("value", 0);
        String techniqueQuality = getIntent().getStringExtra("technique_quality");
        MeasurementRepository repository = new MeasurementRepository(this);

        TextView tvValue = findViewById(R.id.tvValue);
        TextView tvZone = findViewById(R.id.tvZone);
        TextView tvTechnique = findViewById(R.id.tvTechnique);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnBack = findViewById(R.id.btnBack);

        tvValue.setText(String.valueOf(value));

        repository.getPersonalBest().observe(this, personalBest -> {

            int pb = (personalBest != null) ? personalBest : value;

            int greenMin = ZoneSettings.getGreenMin(this);
            int yellowMin = ZoneSettings.getYellowMin(this);

            if (value >= greenMin) {
                tvZone.setText(getString(R.string.gr_ne_zone) + value + getString(R.string.slash1) + pb + getString(R.string.parenthesis1));
                tvZone.setBackgroundColor(0xFF1D9E75);
            } else if (value >= yellowMin) {
                tvZone.setText(getString(R.string.gelbe_zone) + value + getString(R.string.slash2) + pb + getString(R.string.parenthesis2));
                tvZone.setBackgroundColor(0xFFEF9F27);
            } else {
                tvZone.setText(getString(R.string.rote_zone) + value + getString(R.string.slash3) + pb + getString(R.string.parenthesis3));
                tvZone.setBackgroundColor(0xFFE24B4A);
            }

            tvZone.setTextColor(0xFFFFFFFF);
        });

        if ("good".equals(techniqueQuality)) {
            tvTechnique.setText(R.string.ausatemtechnik_gut);
            tvTechnique.setTextColor(0xFF1D9E75);
        } else if ("weak".equals(techniqueQuality)) {
            tvTechnique.setText(R.string.ausatemtechnik_schwach_bitte_kr_ftiger_ausatmen);
            tvTechnique.setTextColor(0xFFEF9F27);
        } else {
            tvTechnique.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> {
            Measurement measurement = new Measurement(
                    value,
                    System.currentTimeMillis(),
                    techniqueQuality != null ? techniqueQuality : "unknown",
                    null
            );
            new MeasurementRepository(this).insert(measurement);

            Toast.makeText(this, R.string.gespeichert1, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}