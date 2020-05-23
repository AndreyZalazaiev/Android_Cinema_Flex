package andrew.cinema.cinema.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


import andrew.cinema.cinema.Entities.Cinema;
import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Utils.Storage;
import andrew.cinema.cinema.Repos.CinemaRepos;
import lombok.SneakyThrows;
import lombok.val;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Cinema_Pick extends AppCompatActivity {

    private static CinemaRepos cinemaApi;
    private Retrofit retrofit;
    private List<Cinema> cinemas;
    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        val sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        if(mode.equals("true"))
            setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cinema_pick);
        LoadCinema();
    }
    public void OnNextClick(View v)
    {
        Integer id=3;
        Spinner spinner = findViewById(R.id.spinner);
        String selected=spinner.getSelectedItem().toString();
        String []splited =selected.split(":");
        for (Cinema cnm: cinemas
             ) {
            if(splited[0].equals(cnm.getName()))
            {
                id=cnm.getIdcinema();
                break;
            }
        }
        Log.v("Selected cinema:",""+id);
        Intent intent = new Intent(Cinema_Pick.this, Film_Pick.class);
        Storage.idcinema=id;
        //save id
        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("idcinema", ""+id);
        ed.commit();
        //
        startActivity(intent);

    }
    public void DrawCinema(List<Cinema> cinema)
    {
        cinemas=cinema;
        ImageView img = findViewById(R.id.imageView7);
        Spinner spinner = findViewById(R.id.spinner);
        img.setImageResource(R.drawable.logo);
        List<String> values= new ArrayList<String>();
        for (Cinema cnm:cinema
             ) {
            values.add(cnm.getName()+":"+ cnm.getAdress());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.getSelectedItem();
    }
    public void LoadCinema()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        cinemaApi = retrofit.create(CinemaRepos.class); //Создаем объект, при помощи которого будем выполнять запросы
        cinemaApi.getData().enqueue(new Callback<List<Cinema>>() {
            @Override
            public void onResponse(Call<List<Cinema>> call, Response<List<Cinema>> response) {
                Log.v("Response result:",response.body().toString());
                DrawCinema(response.body());
            }
            @Override
            public void onFailure(Call<List<Cinema>> call, Throwable t) {
                Log.v("Response result:","Eror");
            }
        });
    }
    public static CinemaRepos getApi() {
        return cinemaApi;
    }
    public void ToFilmPick(View w)
    {
        Intent intent = new Intent(Cinema_Pick.this, Film_Pick.class);

        startActivity(intent);
    }
    @Override
    public void onBackPressed() {

    }
}
