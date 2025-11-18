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

    // Метод для обновления данных в адаптере
    public void setObservations(List<Observation> observations) {
        this.observations = observations;
        notifyDataSetChanged(); // Уведомляем RecyclerView об обновлении
    }

    @NonNull
    @Override
    public ObservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_observation, parent, false);
        return new ObservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservationViewHolder holder, int position) {
        holder.bind(observations.get(position));
    }

    @Override
    public int getItemCount() {
        return observations.size();
    }

    // ViewHolder хранит ссылки на View элементы
    static class ObservationViewHolder extends RecyclerView.ViewHolder {
        private final ImageView birdImageView;
        private final TextView birdNameTextView;
        private final TextView dateTextView;

        public ObservationViewHolder(@NonNull View itemView) {
            super(itemView);
            birdImageView = itemView.findViewById(R.id.bird_image_view);
            birdNameTextView = itemView.findViewById(R.id.bird_name_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
        }

        public void bind(Observation observation) {
            birdNameTextView.setText(observation.birdName);

            // Форматирование даты
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            dateTextView.setText(sdf.format(new Date(observation.timestamp)));

            // Загрузка изображения (если путь есть)
            if (observation.photoPath != null && !observation.photoPath.isEmpty()) {
                File imgFile = new File(observation.photoPath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    birdImageView.setImageBitmap(myBitmap);
                } else {
                    birdImageView.setImageResource(R.drawable.ic_launcher_foreground); // Заглушка
                }
            }
        }
    }
}
