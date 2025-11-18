package ru.mirea.reznikap.listviewapp;

import static java.util.Map.entry;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<String, String> books = Map.ofEntries(
            entry("Война и мир", "Лев Толстой"),
            entry("Преступление и наказание", "Фёдор Достоевский"),
            entry("Мастер и Маргарита", "Михаил Булгаков"),
            entry("1984", "Джордж Оруэлл"),
            entry("Улисс", "Джеймс Джойс"),
            entry("Лолита", "Владимир Набоков"),
            entry("Шум и ярость", "Уильям Фолкнер"),
            entry("Человек-невидимка", "Герберт Уэллс"),
            entry("На маяк", "Вирджиния Вулф"),
            entry("Илиада", "Гомер"),
            entry("Божественная комедия", "Данте Алигьери"),
            entry("Гамлет", "Уильям Шекспир"),
            entry("Над пропастью во ржи", "Джером Сэлинджер"),
            entry("Великий Гэтсби", "Ф. Скотт Фицджеральд"),
            entry("Сто лет одиночества", "Габриэль Гарсиа Маркес"),
            entry("Моби Дик", "Герман Мелвилл"),
            entry("Дон Кихот", "Мигель де Сервантес"),
            entry("Мадам Бовари", "Гюстав Флобер"),
            entry("Гордость и предубеждение", "Джейн Остин"),
            entry("Приключения Гекльберри Финна", "Марк Твен"),
            entry("Анна Каренина", "Лев Толстой"),
            entry("Алиса в Стране чудес", "Льюис Кэрролл"),
            entry("Гроздья гнева", "Джон Стейнбек"),
            entry("Посторонний", "Альбер Камю"),
            entry("Процесс", "Франц Кафка"),
            entry("Красное и чёрное", "Стендаль"),
            entry("Отверженные", "Виктор Гюго"),
            entry("Милый друг", "Ги де Мопассан"),
            entry("Портрет Дориана Грея", "Оскар Уайльд"),
            entry("Фауст", "Иоганн Вольфганг Гёте")
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<Map.Entry<String, String>> listData = new ArrayList<>(books.entrySet());


        ArrayAdapter<Map.Entry<String, String>> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_2,android.R.id.text1, listData) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                Map.Entry<String, String> entry = getItem(position);
                if (entry != null) {
                    text1.setText(entry.getKey());
                    text2.setText(entry.getValue());
                }
                return view;
            }
        };
        ListView countriesList = findViewById(R.id.country_list_view);
        countriesList.setAdapter(adapter);
    }
}