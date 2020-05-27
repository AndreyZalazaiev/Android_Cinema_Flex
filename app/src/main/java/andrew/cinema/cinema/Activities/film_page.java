package andrew.cinema.cinema.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import andrew.cinema.cinema.Entities.Film;
import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Repos.FilmRepos;
import andrew.cinema.cinema.Utils.Storage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class film_page extends AppCompatActivity {
    public static int TEXT_COLOR = Color.BLACK;
    private Retrofit retrofit;
    private FilmRepos filmApi;
    private Boolean night = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPref;
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        TEXT_COLOR = Color.BLACK;
        if (mode.equals("true")) {
            setTheme(R.style.Theme_AppCompat);
            night = true;
            TEXT_COLOR = Color.WHITE;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getRating(Integer.parseInt(getIntent().getStringExtra("idfilm")));
        DrawPage();

    }

    public void DrawPage() {
        final Animation anim = AnimationUtils.loadAnimation(film_page.this, R.anim.zoomin);
        final Animation animRev = AnimationUtils.loadAnimation(film_page.this, R.anim.zoomout);

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getStringExtra("idfilm"));
        final Film currentFilm = Storage.getFilmById(id);
        final ImageView img = new ImageView(getApplicationContext());
        LinearLayout container = findViewById(R.id.Container);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFilm.getTrailer() != null)
                    if (currentFilm.getTrailer().length() > 10) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentFilm.getTrailer()));
                        startActivity(browserIntent);
                    }
            }
        });
        double size = 1.5;
        if (currentFilm.getImage().length() < 5) {
            Picasso.get()
                    .load(R.drawable.film_placeholder)
                    .resize((int) Math.round(width * 0.8 / size), (int) Math.round(height * 0.7 / size))//Костыль дореволюционный
                    .into(img);
        } else {
            Picasso.get()
                    .load(currentFilm.getImage())
                    .placeholder(R.drawable.film_placeholder)
                    .resize((int) Math.round(width * 0.8 / size), (int) Math.round(height * 0.7 / size))
                    .into(img);
        }
        LinearLayout imgLay = findViewById(R.id.imageCont);
        imgLay.addView(img);

        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                img.startAnimation(animRev);
                return false;
            }
        });

        TextView title = new TextView(getApplicationContext());
        title.setText(Html.fromHtml("<b>" + currentFilm.getName() + "</b>"));
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        TextView setup = new TextView(getApplicationContext());
        setup.setText("Genre: " + currentFilm.getGenre() + "\n"
                + "Age limitations: " + currentFilm.getAgeLimit());
        setup.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
        TextView description = new TextView(getApplicationContext());
        description.setPadding(10,10,10,10);
        description.setText("Description:\n" + currentFilm.getDescription() + "\n");
        description.setPadding(0,0,0,50);
        description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);

        setup.setTextColor(TEXT_COLOR);
        title.setTextColor(TEXT_COLOR);
        description.setTextColor(TEXT_COLOR);

        getSupportActionBar().setTitle(currentFilm.getName());
        imgLay.addView(title);
        container.addView(setup);
        container.addView(description);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(film_page.this, Film_Pick.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(film_page.this, Film_Pick.class);
        startActivity(intent);
        finish();
    }

    public void BackToFilms(View v) {
        Intent intent = new Intent(film_page.this, Film_Pick.class);
        startActivity(intent);
        finish();
    }

    public void ToSessionPick(View v) {
        Intent intent = new Intent(film_page.this, session_pick.class);
        intent.putExtra("idfilm", getIntent().getStringExtra("idfilm"));
        startActivity(intent);
        finish();
    }

    public void ToReviews(View view) {

        int idfilm = Integer.parseInt(getIntent().getStringExtra("idfilm"));
        Intent intent = new Intent(film_page.this, Feedback.class);
        intent.putExtra("idfilm", "" + idfilm);
        startActivity(intent);
        finish();
    }

    void getRating(Integer id) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        filmApi = retrofit.create(FilmRepos.class);
        filmApi.getRating(id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Response result:", "Works");
                        LinearLayout starsContainer = new LinearLayout(getApplicationContext());
                        starsContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        RatingBar rb = new RatingBar(getApplicationContext());
                        rb.setRating(Float.parseFloat(response.body()));
                        rb.setIsIndicator(true);
                        LayerDrawable stars = (LayerDrawable) rb.getProgressDrawable();
                        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
                        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        starsContainer.addView(rb);
                        LinearLayout ln = findViewById(R.id.imageCont);
                        ln.addView(starsContainer);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                    }
                });
    }
}
