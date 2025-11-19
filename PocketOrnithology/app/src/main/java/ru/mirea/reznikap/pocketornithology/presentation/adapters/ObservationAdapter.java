package ru.mirea.reznikap.pocketornithology.presentation.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.mirea.reznikap.domain.models.Observation;
import ru.mirea.reznikap.pocketornithology.R;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder> {

    private List<Observation> observations = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Observation observation);
    }

    public ObservationAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ObservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_observation, parent, false);
        return new ObservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservationViewHolder holder, int position) {
        holder.bind(observations.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return observations.size();
    }

    static class ObservationViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, date, location;

        public ObservationViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.bird_image_view);
            name = itemView.findViewById(R.id.bird_name_text_view);
            date = itemView.findViewById(R.id.date_text_view);
            location = itemView.findViewById(R.id.location_text_view);
        }

        public void bind(Observation item, OnItemClickListener listener) {
            name.setText(item.birdName);
            location.setText("Москва");  

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            date.setText(sdf.format(new Date(item.timestamp)));

            if (item.photoPath != null) {
                File imgFile = new File(item.photoPath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    image.setImageBitmap(myBitmap);
                }
            }

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
