package ru.mirea.reznikap.pocketornithology.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mirea.reznikap.data.repository.AuthRepositoryImpl;
import ru.mirea.reznikap.domain.repository.AuthRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;
import ru.mirea.reznikap.domain.usecase.LoginUseCase;
import ru.mirea.reznikap.domain.usecase.RegisterUseCase;
import ru.mirea.reznikap.pocketornithology.R;

public class AuthActivity extends BaseActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    private LoginUseCase loginUseCase;
    private RegisterUseCase registerUseCase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainScreen();
            return;
        }


        setContentView(R.layout.activity_auth);



        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        Button registerButton = findViewById(R.id.register);


        AuthRepository authRepository = new AuthRepositoryImpl();
        loginUseCase = new LoginUseCase(authRepository);
        registerUseCase = new RegisterUseCase(authRepository);


        loginButton.setOnClickListener(v -> handleLogin());
        registerButton.setOnClickListener(v -> handleRegister());
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        loginUseCase.execute(email, password, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {

                Toast.makeText(AuthActivity.this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
                navigateToMainScreen();
            }

            @Override
            public void onFailure(Exception e) {

                Toast.makeText(AuthActivity.this, "Ошибка входа: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleRegister() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        registerUseCase.execute(email, password, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                Toast.makeText(AuthActivity.this, "Регистрация успешна. Выполнен вход.", Toast.LENGTH_SHORT).show();
                navigateToMainScreen();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AuthActivity.this, "Ошибка регистрации: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Введите email");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Введите пароль");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Пароль должен быть не менее 6 символов");
            return false;
        }
        return true;
    }

    private void navigateToMainScreen() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}