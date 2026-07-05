package at.fhj.peakflowmate.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import at.fhj.peakflowmate.MainActivity;
import at.fhj.peakflowmate.R;

/**
 * Aktivität zur Einführung neuer Benutzer in die Anwendung.
 * <p>
 * Diese Aktivität präsentiert die wichtigsten Funktionen von PeakFlowMate
 * mithilfe eines ViewPager2. Nach Abschluss oder Überspringen des
 * Onboardings wird der entsprechende Status gespeichert, sodass die
 * Einführung bei zukünftigen Starts der Anwendung nicht erneut angezeigt
 * wird.
 */
public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext;
    private Button btnSkip;

    /**
     * Initialisiert die Onboarding-Aktivität.
     * <p>
     * Richtet den ViewPager mit den einzelnen Einführungsseiten ein und
     * konfiguriert die Navigation über die Schaltflächen zum Fortfahren
     * oder Überspringen des Onboardings.
     *
     * @param savedInstanceState enthält den zuvor gespeicherten Zustand der
     *                           Aktivität oder {@code null}, falls keiner
     *                           vorhanden ist.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        View root = findViewById(R.id.root);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    bars.left,
                    bars.top,
                    bars.right,
                    bars.bottom
            );

            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        viewPager.setAdapter(new OnboardingAdapter());

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < 2) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                finishOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> finishOnboarding());

        viewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        if (position == 2) {
                            btnNext.setText(R.string.los_geht_s);
                            btnSkip.setVisibility(View.GONE);
                        } else {
                            btnNext.setText(R.string.weiter);
                            btnSkip.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    /**
     * Beendet das Onboarding.
     * <p>
     * Speichert den erfolgreichen Abschluss des Onboardings in den
     * Anwendungseinstellungen und öffnet anschließend die Hauptaktivität.
     */
    private void finishOnboarding() {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("onboarding_done", true)
                .commit();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
