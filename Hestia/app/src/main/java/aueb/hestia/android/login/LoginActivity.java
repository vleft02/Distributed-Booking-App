package aueb.hestia.android.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import aueb.hestia.R;
import aueb.hestia.android.search.SearchRoomsActivity;

public class LoginActivity extends AppCompatActivity implements LoginView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        LoginViewModel viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.getPresenter().setView(this);
        if (savedInstanceState == null){
            Intent intent = getIntent();
        }


        findViewById(R.id.BookButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = viewModel.getPresenter().authenticate();
                if (username != null)
                {
                    login(username);
                }

            }
        });
    }


    public void login(String username){
        Intent intent = new Intent(LoginActivity.this, SearchRoomsActivity.class);
        intent.putExtra("username",username);
        startActivity(intent);
    }

    @Override
    public String ExtractUsername() {
        return ((EditText)findViewById(R.id.PersonsField)).getText().toString().trim();
    }

    @Override
    public void showErrorMessage(String title, String message) {
        new AlertDialog.Builder(LoginActivity.this)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null).create().show();
    }
}