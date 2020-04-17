package andrew.cinema.cinema;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.List;


import andrew.cinema.cinema.Entities.Film;
import andrew.cinema.cinema.Entities.Storage;
import andrew.cinema.cinema.Repos.FilmRepos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Film_Pick extends AppCompatActivity {
    private static FilmRepos filmApi;
    private Retrofit retrofit;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_pick);
        LoadFilm();
    }
    public void LoadFilm()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        filmApi = retrofit.create(FilmRepos.class); //Создаем объект, при помощи которого будем выполнять запросы
        filmApi.getData().enqueue(new Callback<List<Film>>() {
            @Override
            public void onResponse(Call<List<Film>>call, Response<List<Film>>response) {
                Log.v("Response result:",response.body().toString());
                DrawFilm(response.body());
            }
            @Override
            public void onFailure(Call<List<Film>> call, Throwable t) {
                Log.v("Response result:","Eror");
            }
        });
    }
    public void DrawFilm(List<Film> films)
    {
        final double heightCoef=0.60;
        final double widthCoef=0.70;
        final double textCoef=0.20;
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();//Это ложь
        LinearLayout container = findViewById(R.id.Container);
        Storage.films=films;

        for (final Film film: films
             ) {
            ImageView img = new ImageView(getApplicationContext());
            TextView name = new TextView(getApplicationContext());
            ScrollView  textScroll = new ScrollView(this);
            name.setLayoutParams(new LinearLayout.LayoutParams(width,(int)(height*textCoef)));
            textScroll.addView(name);
            name.setText(film.getName());
            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
            name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            name.setTextColor(Color.rgb(255,255,255));
            name.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);


            if(film.getImage().length()<5)
            {
                Picasso.get()
                        .load(R.drawable.film_placeholder)
                        .resize((int)Math.round(width*widthCoef), (int)Math.round(height*heightCoef))//Костыль дореволюционный
                        .into(img);
            }
            else {
                Picasso.get()
                        .load(film.getImage())
                        .placeholder(R.drawable.film_placeholder)
                        .resize((int)Math.round(width*widthCoef), (int)Math.round(height*heightCoef))
                        .into(img);
            }
                LinearLayout ln = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)(width*0.95), (int)(height*(heightCoef+textCoef)));
                layoutParams.gravity=Gravity.CENTER;
                layoutParams.setMargins(30, 0, 30, 0);
                ln.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =new Intent(Film_Pick.this,film_page.class);
                        intent.putExtra("idfilm",""+film.getIdfilm());
                        startActivity(intent);finish();
                    }
                });
                ln.setLayoutParams(layoutParams);
                ln.setOrientation(LinearLayout.VERTICAL);
                ln.setBackgroundResource(R.drawable.films_background);
                ln.setPadding(0,(int)(width*0.05),0,0);//отступ внутри картинки

                ln.addView(img);
                ln.addView(textScroll);
                container.addView(ln);
        }
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(Film_Pick.this,MainActivity.class);
        startActivity(intent);finish();
    }
}
