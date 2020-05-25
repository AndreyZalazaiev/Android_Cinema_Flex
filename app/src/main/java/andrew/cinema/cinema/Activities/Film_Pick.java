package andrew.cinema.cinema.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import andrew.cinema.cinema.Entities.Film;
import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Utils.Storage;
import andrew.cinema.cinema.Menu.Settings;
import andrew.cinema.cinema.Menu.about_account;
import andrew.cinema.cinema.Menu.bonuses;
import andrew.cinema.cinema.Menu.buyed_tickets;
import andrew.cinema.cinema.Menu.gift_for_dob;
import andrew.cinema.cinema.Repos.FilmRepos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Film_Pick extends AppCompatActivity {
    private static FilmRepos filmApi;
    private Retrofit retrofit;
    private AppBarConfiguration mAppBarConfiguration;
    private int width;
    String soon;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPref;
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        soon= sPref.getString("Soon", "true");
        if (mode.equals("true"))
            setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film__pick);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        height -= getSupportActionBar().getHeight();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        LoadFilms();
        FillMenu();

    }

    public void FillMenu() {
        final double SIZE_CONST = 0.15;
        //Header
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navView = navigationView.getHeaderView(0);
        ImageView img = navView.findViewById(R.id.userPic);
        TextView email = navView.findViewById(R.id.Email);
        TextView name = navView.findViewById(R.id.Name);
        Menu menu = navigationView.getMenu();
        MenuItem nav_bonuses = menu.findItem(R.id.nav_bonuses);
        nav_bonuses.setTitle(Html.fromHtml("Bonuses: <b>" + Storage.bonus + "</b>"));
        email.setText(Storage.email);
        name.setText(Storage.name);
        //setup pic
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp((float) (width / 2 * SIZE_CONST))
                .oval(false)
                .build();
        if (Storage.picture.length() < 5) {
            Picasso.get()
                    .load(R.drawable.default_user_pic)
                    .resize((int) (width * SIZE_CONST), (int) (width * SIZE_CONST))
                    .transform(transformation)
                    .into(img);
        } else {
            Picasso.get()
                    .load(Storage.picture)
                    .resize((int) (width * SIZE_CONST), (int) (width * SIZE_CONST))
                    .transform(transformation)
                    .placeholder(R.drawable.default_user_pic)
                    .into(img);
        }
        //------------------------------------------------------------------------------Menu
        MenuItem nav_change_cinema = menu.findItem(R.id.nav_change_cinema);//change cinema
        nav_change_cinema.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Film_Pick.this, Cinema_Pick.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        //--------------------------------------------------Log out
        MenuItem nav_exit = menu.findItem(R.id.nav_exit);
        nav_exit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Film_Pick.this, Authentication.class);
                SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.remove("idaccount");
                Storage.idaccount = null;
                Storage.picture = null;
                Storage.bonus = null;
                Storage.name = null;
                Storage.doB = null;
                ed.commit();
                intent.putExtra("mode", "signout");
                startActivity(intent);
                finish();
                return false;
            }
        });
        MenuItem nav_tickets = menu.findItem(R.id.nav_tickets);
        nav_tickets.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Film_Pick.this, buyed_tickets.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        MenuItem nav_dob = menu.findItem(R.id.nav_dob);
        nav_dob.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Film_Pick.this, gift_for_dob.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        MenuItem nav_support = menu.findItem(R.id.nav_support);
        nav_support.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/bergmah"));
                startActivity(browserIntent);
                return false;
            }
        });
        nav_bonuses.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Film_Pick.this, bonuses.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        MenuItem nav_account = menu.findItem(R.id.nav_infoinfo);
        nav_account.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Film_Pick.this, about_account.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        MenuItem nav_setting = menu.findItem(R.id.nav_setting);
        nav_setting.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Film_Pick.this, Settings.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        MenuItem nav_web = menu.findItem(R.id.nav_web);
        nav_web.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ci81503.tmweb.ru/"));
                startActivity(browserIntent);
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.film__pick, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void LoadFilms() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        filmApi = retrofit.create(FilmRepos.class); //Создаем объект, при помощи которого будем выполнять запросы
        if(soon.equals("true"))
        filmApi.getData().enqueue(new Callback<List<Film>>() {
            @Override
            public void onResponse(Call<List<Film>> call, Response<List<Film>> response) {
                Log.v("Response result:", response.body().toString());
                DrawFilm(response.body());
            }

            @Override
            public void onFailure(Call<List<Film>> call, Throwable t) {
                Log.v("Response result:", "Eror");
            }
        });
        else
            filmApi.getActual().enqueue(new Callback<List<Film>>() {
                @Override
                public void onResponse(Call<List<Film>> call, Response<List<Film>> response) {
                    Log.v("Response result:", response.body().toString());
                    DrawFilm(response.body());
                }

                @Override
                public void onFailure(Call<List<Film>> call, Throwable t) {
                    Log.v("Response result:", "Eror");
                }
            });
    }

    public void DrawFilm(List<Film> films) {
        HorizontalScrollView hsv = findViewById(R.id.hsv);
        hsv.setPadding(0, getSupportActionBar().getHeight(), 0, 0);
        final Animation anim = AnimationUtils.loadAnimation(Film_Pick.this, R.anim.alpha);
        final double heightCoef = 0.64;
        final double widthCoef = 0.64;
        final double textCoef = 0.15;
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();//Это ложь
        LinearLayout container = findViewById(R.id.Container);
        Storage.films = films;

        for (final Film film : films
        ) {
            final ImageView img = new ImageView(getApplicationContext());
            TextView name = new TextView(getApplicationContext());
            ScrollView textScroll = new ScrollView(this);
            name.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * textCoef)));
            textScroll.addView(name);
            name.setText(film.getName() + "  ");
            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
            name.setTextAlignment(container.TEXT_ALIGNMENT_CENTER);
            name.setTextColor(Color.rgb(158, 158, 158));
            name.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            if (film.getImage().length() < 5) {
                Picasso.get()
                        .load(R.drawable.film_placeholder)
                        .resize((int) Math.round(width * widthCoef), (int) Math.round(height * heightCoef))//Костыль дореволюционный
                        .into(img);
            } else {
                Picasso.get()
                        .load(film.getImage())
                        .placeholder(R.drawable.film_placeholder)
                        .error(R.drawable.film_placeholder)
                        .resize((int) Math.round(width * widthCoef), (int) Math.round(height * heightCoef))
                        .into(img);
            }
            LinearLayout ln = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (width * 0.92), (int) (height * (heightCoef + textCoef)));
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(30, 0, 30, 0);

            ln.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Film_Pick.this, film_page.class);
                    intent.putExtra("idfilm", "" + film.getIdfilm());
                    startActivity(intent);
                    finish();
                }
            });
            ln.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    img.startAnimation(anim);
                    return false;
                }
            });

            ln.setLayoutParams(layoutParams);
            ln.setOrientation(LinearLayout.VERTICAL);
            ln.setBackgroundResource(R.drawable.films_drawable_back);
            ln.setPadding(0, (int) (width * 0.05), 0, 0);//отступ внутри картинки

            ln.addView(img);
            ln.addView(textScroll);
            container.addView(ln);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
