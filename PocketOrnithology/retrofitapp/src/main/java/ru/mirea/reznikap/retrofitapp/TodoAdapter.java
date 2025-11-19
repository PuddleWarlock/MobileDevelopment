package ru.mirea.reznikap.retrofitapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private Context context;
    private List<Todo> todos;
    private OnTodoClickListener onTodoClickListener;

    // Интерфейс для передачи клика в Activity
    public interface OnTodoClickListener {
        void onTodoClick(Todo todo, int position);
    }

    public TodoAdapter(Context context, List<Todo> todos, OnTodoClickListener onTodoClickListener) {
        this.context = context;
        this.todos = todos;
        this.onTodoClickListener = onTodoClickListener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todos.get(position);
        holder.textViewTitle.setText(todo.getTitle());

        // Важно: сначала удаляем слушатель, чтобы избежать циклического вызова при прокрутке
        holder.checkBoxCompleted.setOnCheckedChangeListener(null);
        holder.checkBoxCompleted.setChecked(todo.getCompleted());

        // Задание 2: Picasso
        // Так как в JSONPlaceholder нет картинок, генерируем случайную картинку по ID
        String imageUrl = "https://picsum.photos/200?random=" + todo.getId();

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground) // Заглушка (должна быть в ресурсах)
                .error(R.drawable.ic_launcher_background)       // Ошибка (должна быть в ресурсах)
                .resize(150, 150)                       // Изменение размера
                .centerCrop()                                   // Обрезка
                .into(holder.imageView);

        // Задание: Обновление CheckBox
        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            todo.setCompleted(isChecked);
            onTodoClickListener.onTodoClick(todo, position);
        });
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        CheckBox checkBoxCompleted;
        ImageView imageView;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
