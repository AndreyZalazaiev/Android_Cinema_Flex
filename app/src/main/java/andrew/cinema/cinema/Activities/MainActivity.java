package andrew.cinema.cinema.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import andrew.cinema.cinema.R;
import lombok.SneakyThrows;

public class MainActivity extends AppCompatActivity     {
    String res = "";
    Boolean get = false;
    private AppBarConfiguration mAppBarConfiguration;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPref;
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        if(mode.equals("true"))
            setTheme(R.style.Theme_AppCompat_NoActionBar);
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
