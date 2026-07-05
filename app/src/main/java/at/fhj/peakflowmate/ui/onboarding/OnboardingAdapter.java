package at.fhj.peakflowmate.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import at.fhj.peakflowmate.R;

/**
 * Adapter zur Darstellung der Onboarding-Seiten.
 * <p>
 * Der Adapter stellt die einzelnen Einführungsseiten für den
 * {@link androidx.viewpager2.widget.ViewPager2} bereit. Jede Seite
 * enthält einen Titel, eine Beschreibung sowie ein Symbol zur
 * Erläuterung der jeweiligen Funktion der Anwendung.
 */
public class OnboardingAdapter
        extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {

    private final int[] titles = {
            R.string.onboarding_title_1,
            R.string.onboarding_title_2,
            R.string.onboarding_title_3
    };

    private final int[] descriptions = {
            R.string.onboarding_desc_1,
            R.string.onboarding_desc_2,
            R.string.onboarding_desc_3
    };

    private final int[] icons = {
            android.R.drawable.ic_btn_speak_now,
            android.R.drawable.ic_menu_camera,
            android.R.drawable.ic_menu_recent_history
    };

    /**
     * Erstellt einen neuen ViewHolder für eine Onboarding-Seite.
     *
     * @param parent übergeordnete ViewGroup.
     * @param viewType Typ des anzuzeigenden Elements.
     * @return ein neuer {@link ViewHolder}.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Bindet die Inhalte einer Onboarding-Seite an den ViewHolder.
     *
     * @param holder ViewHolder, der aktualisiert werden soll.
     * @param position Position der anzuzeigenden Seite.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTitle.setText(titles[position]);
        holder.tvDesc.setText(descriptions[position]);
        holder.ivIcon.setImageResource(icons[position]);
    }

    /**
     * Gibt die Anzahl der Onboarding-Seiten zurück.
     *
     * @return Anzahl der verfügbaren Seiten.
     */
    @Override
    public int getItemCount() { return titles.length; }

    /**
     * ViewHolder für eine einzelne Onboarding-Seite.
     * <p>
     * Enthält die Views zur Darstellung des Titels, der Beschreibung
     * sowie des zugehörigen Symbols.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        ImageView ivIcon;

        /**
         * Erstellt einen neuen ViewHolder.
         *
         * @param v View einer Onboarding-Seite.
         */
        ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvOnboardingTitle);
            tvDesc = v.findViewById(R.id.tvOnboardingDesc);
            ivIcon = v.findViewById(R.id.ivOnboardingIcon);
        }
    }
}
