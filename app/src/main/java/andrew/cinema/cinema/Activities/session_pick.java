package andrew.cinema.cinema.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import andrew.cinema.cinema.Entities.Film;
import andrew.cinema.cinema.Entities.Halls;
import andrew.cinema.cinema.Entities.Session;
import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Utils.Storage;
import andrew.cinema.cinema.Repos.HallRepos;
import andrew.cinema.cinema.Repos.SessionRepos;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class session_pick extends AppCompatActivity {
    private Retrofit retrofit;
    private SessionRepos sessionApi;
    private HallRepos hallsApi;
    private List<Session> ss;
    private List<Halls> hl;
    private final int TempCinema = Storage.idcinema;
    boolean night = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPref;
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        if(mode.equals("true")) {
            night=true;
            setTheme(R.style.Theme_AppCompat);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_pick);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select session");
        LoadSessoins(Integer.parseInt(getIntent().getStringExtra("idfilm")));
    }

    public void LoadSessoins(int idfilm) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sessionApi = retrofit.create(SessionRepos.class);
        sessionApi.Sessions(idfilm)
                .enqueue(new Callback<List<Session>>() {
                    @Override
                    public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                        Log.w("Session result:", "Works");
                        ss = response.body();
                        LoadHalls(response.body());

                    }

                    @Override
                    public void onFailure(Call<List<Session>> call, Throwable t) {
                        Log.w("Session result:", "need registration");
                    }
                });
    }

    public void SessionDraw(List<Session> sessions, List<Halls> halls) throws ParseException {
        Display display = getWindowManager().getDefaultDisplay();
        final int width = display.getWidth();
        int height = display.getHeight();
        double size = 1.7;
        hl=halls;

        sessions = FilterSessions(sessions, halls);
        Film currentFilm = Storage.getFilmById(Integer.parseInt(getIntent().getStringExtra("idfilm")));
        final LinearLayout container = findViewById(R.id.Container);
        final LinearLayout scrollSessionContainer = new LinearLayout(getApplicationContext());//внутренний контейнер скрола
        final HorizontalScrollView scrollSession = new HorizontalScrollView(getApplicationContext());//сам скрол
        //Initialize
        ImageView img = new ImageView(this);
        final TextView text = new TextView(this);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setTypeface(null, Typeface.BOLD);

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
        container.addView(img);
        text.setText(currentFilm.getName());
        if(night)
        text.setTextColor(Color.WHITE);
        else
            text.setTextColor(Color.BLACK);
        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
        container.addView(text);
        if (sessions.size() > 0  ) {

            LinearLayout scrollDaysContainer = new LinearLayout(this);
            HorizontalScrollView scrollDays = new HorizontalScrollView(this);
            ImageView hallPick = new ImageView(this);

            scrollDaysContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (height * 0.1)));
            final List<Date> dates = Dates(sessions);
            for (final Date dt : dates //Он клик для даты
            ) {
                Button btn = new Button(this);
                btn.setTag(dt);
                LinearLayout.LayoutParams lnP= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lnP.setMargins(20,0,20,0);
                btn.setLayoutParams(lnP);
                btn.setTextColor(Color.WHITE);
                btn.setBackground(getResources().getDrawable(R.drawable.date_btn_background));
                btn.setAllCaps(false);
                btn.setText(getDayOfWeek(dt.getDay()) + " " + dt.getDate() + " " + getMonthName(dt.getMonth()));
                btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                btn.setPadding(40,0,40,0);
                btn.setOnClickListener(new View.OnClickListener() {
                    @SneakyThrows
                    @Override
                    public void onClick(View v) {
                        //todo OnClick event
                        scrollSessionContainer.removeAllViews();
                        for (final Session sn : SessionByDate(ss, dt)
                        ) {
                            final String strStart = sn.getStart();
                            String strEnd = sn.getEnd();
                            final TextView dateStart = new TextView(getApplicationContext());
                            final TextView dateEnd = new TextView(getApplicationContext());
                            TextView hallName= new TextView(getApplicationContext());

                            dateStart.setTextColor(Color.BLACK);
                            dateStart.setText(ExcludeTime(strStart) + "-");
                            dateEnd.setText(ExcludeTime(strEnd)+"    ");
                            dateEnd.setTextColor(Color.BLACK);
                            hallName.setTextColor(Color.BLACK);
                            hallName.setText(getHallBySession(sn).getName());

                            dateEnd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                            dateStart.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                            hallName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);


                            ImageView img2 = new ImageView(getApplicationContext());
                            SetHallPicture(img2,getHallBySession(sn).getType());

                            LinearLayout localContainer = new LinearLayout(getApplicationContext());
                            LinearLayout textContainer = new LinearLayout(getApplicationContext());
                            localContainer.setOrientation(LinearLayout.VERTICAL);
                            textContainer.addView(dateStart);
                            textContainer.addView(dateEnd);
                            textContainer.addView(hallName);
                            localContainer.addView(textContainer);
                            if(night)
                                localContainer.setBackgroundResource(R.drawable.films_drawable_back);
                            else localContainer.setBackgroundResource(R.drawable.background_gray);
                            localContainer.setPadding(50,0,50,50);
                            localContainer.addView(img2);
                            localContainer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(session_pick.this, Place_Picker.class);
                                    intent.putExtra("idfilm",getIntent().getStringExtra("idfilm"));
                                    intent.putExtra("idsession",""+sn.getIdsession());
                                    intent.putExtra("baseprice",""+sn.getBaseprice());
                                    intent.putExtra("type",""+getHallBySession(sn).getType());
                                    intent.putExtra("date",""+sn.getStart());
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            LinearLayout.LayoutParams lnP= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lnP.setMargins(50,0,50,0);
                            localContainer.setLayoutParams(lnP);

                            scrollSessionContainer.addView(localContainer);
                        }
                    }
                });
                scrollDaysContainer.addView(btn);
            }
            for (final Session sn : SessionByDate(ss, dates.get(0))//копипаст,простите меня,мне стыдно,честно.Я был вынужден
            ) {
                String strStart = sn.getStart();
                String strEnd = sn.getEnd();
                TextView dateStart = new TextView(getApplicationContext());
                TextView dateEnd = new TextView(getApplicationContext());
                TextView hallName= new TextView(getApplicationContext());

                dateStart.setTextColor(Color.BLACK);
                dateStart.setText(ExcludeTime(strStart) + "-");
                dateEnd.setText(ExcludeTime(strEnd)+"    ");
                dateEnd.setTextColor(Color.BLACK);
                hallName.setTextColor(Color.BLACK);
                hallName.setText(getHallBySession(sn).getName());

                dateEnd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                dateStart.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                hallName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);


                ImageView img2 = new ImageView(getApplicationContext());
                SetHallPicture(img2,getHallBySession(sn).getType());

                LinearLayout localContainer = new LinearLayout(getApplicationContext());
                LinearLayout textContainer = new LinearLayout(getApplicationContext());
                localContainer.setOrientation(LinearLayout.VERTICAL);
                textContainer.addView(dateStart);
                textContainer.addView(dateEnd);
                textContainer.addView(hallName);
                localContainer.addView(textContainer);
                localContainer.addView(img2);
                if(night)
                localContainer.setBackgroundResource(R.drawable.films_drawable_back);
                else localContainer.setBackgroundResource(R.drawable.background_gray);
                localContainer.setPadding(50,0,50,50);
                localContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(session_pick.this, Place_Picker.class);
                        intent.putExtra("idfilm",getIntent().getStringExtra("idfilm"));
                        intent.putExtra("idsession",""+sn.getIdsession());
                        intent.putExtra("type",""+getHallBySession(sn).getType());
                        intent.putExtra("baseprice",""+sn.getBaseprice());
                        intent.putExtra("date",""+sn.getStart());
                        startActivity(intent);
                        finish();
                    }
                });
                LinearLayout.LayoutParams lnP= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lnP.setMargins(50,0,50,0);
                localContainer.setLayoutParams(lnP);

                scrollSessionContainer.addView(localContainer);
            }
            scrollDays.addView(scrollDaysContainer);
            //Insert


            container.addView(scrollDays);
            scrollSession.addView(scrollSessionContainer);
            container.addView(scrollSession);
        } else {
            TextView eror = new TextView(this);
            eror.setText("No sessions for today :(");
            eror.setPadding(50,50,50,50);
            if(night)
                eror.setTextColor(Color.WHITE);
            else
                eror.setTextColor(Color.BLACK);
            eror.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
            container.addView(eror);
        }


    }

    public Halls getHallBySession(Session sn)
    {
        for (Halls h:hl
             ) {
            if(h.getIdhall().equals(sn.getIdhall()))
            {
                return h;
            }
        }
            return null;
    }
    public static String ExcludeTime(String date) {
        String[] a = date.split("T");
        String[] b = a[1].split(":");
        return b[0] + ":" + b[1];

    }

    public List<Session> SessionByDate(List<Session> sessions, Date date) throws ParseException {
        SimpleDateFormat formatterFirst = new SimpleDateFormat("yyyy-MM-dd");
        List<Session> temp = new ArrayList<Session>();
        for (Session sn : sessions
        ) {
            Date current = null;
            current = (Date) formatterFirst.parse(sn.getStart());
            Date dt = new Date(current.getYear(), current.getMonth(), current.getDate());
            if (date.equals(dt)) {
                temp.add(sn);
            }
        }
        temp=FilterSessions(temp,hl);
        return temp;
    }

    public List<Date> Dates(List<Session> sessions) throws ParseException {
        SimpleDateFormat formatterFirst = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> out = new ArrayList<Date>();
        for (Session sn : sessions
        ) {
            Date date = null;
            date = (Date) formatterFirst.parse(sn.getStart());
            Date curent = new Date();
            if (date.after(new Date())) {
                if(!out.contains(date)){
                out.add(date);}
            }
        }
        Collections.sort(out);
        return out;
    }

    public List<Session> FilterSessions(List<Session> sessions, List<Halls> halls) {
        List<Session> temp = new ArrayList<Session>();
        for (Session sn : sessions
        ) {
            for (Halls hl : halls
            ) {
                if (sn.getIdhall().equals(hl.getIdhall())) {
                    temp.add(sn);
                    break;
                }
            }
        }
        return temp;
    }

    public void LoadHalls(final List<Session> sessions) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        hallsApi = retrofit.create(HallRepos.class);
        hallsApi.Halls(TempCinema)
                .enqueue(new Callback<List<Halls>>() {
                    @SneakyThrows
                    @Override
                    public void onResponse(Call<List<Halls>> call, Response<List<Halls>> response) {
                        Log.w("Halls result:", "Works");
                        SessionDraw(sessions, response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Halls>> call, Throwable t) {
                        Log.w("Halls result:", "need registration");
                    }
                });
    }

    public String getDayOfWeek(int day) {
        switch (day) {
            case 1:
                return "Monday";
            case 2:
                return "Thuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "Aliens";
        }
    }

    public String getMonthName(int m) {
        switch (m) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "Aliens";
        }
    }
    public void SetHallPicture(ImageView img, String type)
    {
        switch (type)
        {
            case "2D":
                PicasssoDrawHallImage(R.drawable.h2d,img);
                break;
            case "3D":
                PicasssoDrawHallImage(R.drawable.h3d,img);
                break;
            case "4D":
                PicasssoDrawHallImage(R.drawable.h4d,img);
                break;
            case "IMAX":
                PicasssoDrawHallImage(R.drawable.himax,img);
                break;
            default:
                PicasssoDrawHallImage(R.drawable.melon_placeholder,img);

        }

    }
    public void PicasssoDrawHallImage(int i,ImageView img)
    {
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        Picasso.get()
                .load(i)
                .placeholder(R.drawable.melon_placeholder)
                .resize((int) Math.round(width * 0.7 ), (int) Math.round(height * 0.25 ))
                .into(img);
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(session_pick.this, film_page.class);
        intent.putExtra("idfilm", getIntent().getStringExtra("idfilm"));
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(session_pick.this, film_page.class);
                intent.putExtra("idfilm", getIntent().getStringExtra("idfilm"));
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
