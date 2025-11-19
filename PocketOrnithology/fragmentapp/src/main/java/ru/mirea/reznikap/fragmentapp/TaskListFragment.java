package ru.mirea.reznikap.fragmentapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvStudentNumber = view.findViewById(R.id.tvStudentNumber);

        if (getArguments() != null) {
            int myNumber = getArguments().getInt("my_number_student");
            tvStudentNumber.setText("Мой номер по списку: " + myNumber);
            Log.d("TaskListFragment", "Получен номер: " + myNumber);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTasks);

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Сделать практическую работу №6", false));
        tasks.add(new Task("Изучить жизненный цикл Fragment", true));
        tasks.add(new Task("Разобраться с FragmentManager", false));
        tasks.add(new Task("Сдать контрольное задание", false));
        tasks.add(new Task("Выпить кофе", true));

        TaskAdapter adapter = new TaskAdapter(tasks);
        recyclerView.setAdapter(adapter);
    }
}
