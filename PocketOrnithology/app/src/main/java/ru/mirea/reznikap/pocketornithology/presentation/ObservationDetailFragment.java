package ru.mirea.reznikap.pocketornithology.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import ru.mirea.reznikap.pocketornithology.R;
import ru.mirea.reznikap.pocketornithology.presentation.factories.ViewModelFactory;
import ru.mirea.reznikap.pocketornithology.presentation.viewmodels.ObservationDetailsViewModel;

public class ObservationDetailFragment extends Fragment {

    private static final String ARG_OBSERVATION_ID = "obs_id";

    public static ObservationDetailFragment newInstance(int observationId) {
        ObservationDetailFragment fragment = new ObservationDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_OBSERVATION_ID, observationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_observation_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int id = getArguments().getInt(ARG_OBSERVATION_ID);

        ImageView imageView = view.findViewById(R.id.detailImageView);
        TextView nameView = view.findViewById(R.id.detailBirdName);
        TextView dateView = view.findViewById(R.id.detailDate);
        TextView descView = view.findViewById(R.id.detailDescription);
        ImageView wikiImageView = view.findViewById(R.id.wikiImageView);

        ViewModelFactory factory = new ViewModelFactory(requireContext());
        ObservationDetailsViewModel viewModel = new ViewModelProvider(this, factory).get(ObservationDetailsViewModel.class);

        viewModel.loadObservation(id);

        viewModel.getCombinedDetails().observe(getViewLifecycleOwner(), observation -> {
            if (observation == null) return;

            nameView.setText(observation.birdName);
            descView.setText(observation.description);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            dateView.setText(sdf.format(new Date(observation.timestamp)));

            if (observation.photoPath != null) {
                Glide.with(this).load(new File(observation.photoPath)).into(imageView);
            }

            if (observation.wikiImageUrl != null && !observation.wikiImageUrl.isEmpty()) {
                GlideUrl urlWithHeaders = new GlideUrl(observation.wikiImageUrl, new LazyHeaders.Builder()
                        .addHeader("User-Agent", "PocketOrnithology/1.0 (test@example.com)")
                        .build());

                Glide.with(this)
                        .load(urlWithHeaders)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(wikiImageView);
            }
        });
    }
}