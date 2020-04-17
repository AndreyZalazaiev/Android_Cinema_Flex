package andrew.cinema.cinema;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.net.Socket;
import java.util.List;


import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity  {
    OkHttpClient client = new OkHttpClient();
    String res = "";
    Boolean get = false;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;
    TextView textView;

    @SuppressLint("ResourceType")
    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    public void ButtonOnClick(View v) {
        Intent intent = new Intent(MainActivity.this, Place_Picker.class);
        startActivity(intent);
        finish(); //каким-то образом через PutExtra передать тип зала и купленные места из бд
    }

    public void AuthOnClick(View v) {
        Intent intent2 = new Intent(MainActivity.this, Authentication.class);
        startActivity(intent2);
        finish();
    }

    public void OnCinemaPick(View v) {
        Intent intent2 = new Intent(MainActivity.this, Cinema_Pick.class);
        intent2.putExtra("CinemaJSON", res);
        startActivity(intent2);
        finish();
    }

    public void OnFilmPick(View w) {
        Intent intent2 = new Intent(MainActivity.this, Film_Pick.class);
        startActivity(intent2);
        finish();
    }
}
