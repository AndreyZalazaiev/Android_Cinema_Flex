package andrew.cinema.cinema;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.ViewById;

import java.util.List;

import andrew.cinema.cinema.Entities.Film;
import andrew.cinema.cinema.Entities.Storage;

public class film_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_page);
        DrawPage();
    }

    public void DrawPage() {
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getStringExtra("idfilm"));
        final Film currentFilm = Storage.getFilmById(id);
        ImageView img = new ImageView(getApplicationContext());
        LinearLayout container = findViewById(R.id.container);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFilm.getTrailer().length()>10) {
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
        TextView title = new TextView(getApplicationContext());
        title.setText(currentFilm.getName());
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
        title.setTextColor(Color.BLACK);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        TextView setup = new TextView(getApplicationContext());
        setup.setText("Genre: " + currentFilm.getGenre() + "\n"
                + "Age limitations: " + currentFilm.getAgeLimit());
        setup.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
        setup.setTextColor(Color.BLACK);
        TextView description = new TextView(getApplicationContext());
        description.setText("Description:\n " + currentFilm.getDescription() + "\n");
        description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
        description.setTextColor(Color.BLACK);

        container.addView(img);
        container.addView(title);
        container.addView(setup);
        container.addView(description);


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
    public void ToSessionPick(View  v )
    {
        Intent intent = new Intent(film_page.this, session_pick.class);
        intent.putExtra("idfilm",getIntent().getStringExtra("idfilm"));
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
}
