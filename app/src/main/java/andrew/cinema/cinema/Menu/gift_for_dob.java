package andrew.cinema.cinema.Menu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

import andrew.cinema.cinema.Utils.Storage;
import andrew.cinema.cinema.Activities.Film_Pick;
import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Repos.AccountRepos;
import andrew.cinema.cinema.UI.fragments.DatePickerFragment;
import andrew.cinema.cinema.Utils.Util;
import lombok.SneakyThrows;
import lombok.val;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class gift_for_dob extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public static final int TEXT_COLOR = Color.BLUE;
    public static final int TEXT_SIZE = 21;
    private Retrofit retrofit;
    private AccountRepos accApi;
    private int width;
    private int height;
    private int BONUSES_VALUE=Storage.CalculateBonuses(150);

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        val sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        if (mode.equals("true"))
            setTheme(R.style.Theme_AppCompat);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_for_dob);
        getSupportActionBar().setTitle("Gift for birthday");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        String a = Storage.doB;
        if (Storage.doB == null) {
            DateUnPickedUI();
        } else {
            if (Util.isBirthday()) {
                getGiftstatus(Storage.idaccount, true);
            } else {//Non birthday
                getGiftstatus(Storage.idaccount, false);
            }
        }
    }

    public void BirthdayUI(String response) {
        TextView tv = new TextView(getApplicationContext());
        TextView p1 = new TextView(getApplicationContext());
        p1.setText(Html.fromHtml("1.Дата рождения введена<br/>"));
        p1.setTextColor(Color.GREEN);
        p1.setTextSize(TEXT_SIZE);
        tv.setTextColor(Color.GREEN);
        tv.setTextSize(TEXT_SIZE);
        tv.setText("\nВаша дата рождения:\n" + Storage.doB.split("T")[0] + "\n");
        TextView p2 = new TextView(getApplicationContext());
        p2.setTextSize(TEXT_SIZE);
        p2.setTextColor(Color.GREEN);
        p2.setText(Html.fromHtml("2.Дождатся дня рожения!<br/>"));

        TextView p3 = new TextView(getApplicationContext());
        p3.setTextColor(TEXT_COLOR);
        p3.setTextSize(TEXT_SIZE);


        LinearLayout ln = findViewById(R.id.main);
        ln.setLayoutParams(new LinearLayout.LayoutParams((int) (width * 0.9), (int) (height * 0.85)));
        ln.setBackgroundResource(R.drawable.films_drawable_back);
        ln.setPadding(60, 60, 60, 0);
        ln.setOrientation(LinearLayout.VERTICAL);
        ln.addView(p1);
        ln.addView(tv);
        ln.addView(p2);

        ln.addView(p3);
        if (response == "0")//подарок не получали
        {
            p3.setText(Html.fromHtml("<br/><b>3.Получить подарок</b>" +
                    "<br/><small>Ваш подарок:" +BONUSES_VALUE + " бонусов </small>"));
            Button btn = new Button(getApplicationContext());
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setText("Получить подарок");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    giveBonuses(Storage.idaccount);
                }
            });
            ln.addView(btn);
        } else {
            p3.setTextColor(Color.GREEN);
            p3.setTextSize(TEXT_SIZE);
            p3.setText(Html.fromHtml("<br/>3.Получить подарок<br/><small>Вы уже получили подарок</small>"));

        }
    }

    private void NonBirthdayUI(String response) {
        TextView tv = new TextView(getApplicationContext());
        TextView p1 = new TextView(getApplicationContext());
        p1.setText(Html.fromHtml("1.Дата рождения введена<br/>"));
        p1.setTextColor(Color.GREEN);
        p1.setTextSize(TEXT_SIZE);
        tv.setTextColor(TEXT_COLOR);
        tv.setTextSize(TEXT_SIZE);
        tv.setText("\nВаша дата рождения:\n" + Storage.doB.split("T")[0] + "\n");
        TextView p2 = new TextView(getApplicationContext());
        p2.setTextSize(TEXT_SIZE);
        p2.setTextColor(TEXT_COLOR);
        p2.setText(Html.fromHtml("<b>2.Дождатся дня рожения!<br/></b>" +
                "<small>Для получения бонуса у Вас должна быть совершена покупка в течении 12 месяцев до дня рождения</small>"));
        ImageView img = new ImageView(getApplicationContext());
        img.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (response == "0") {
            img.setImageResource(R.drawable.ic_check_circle_green_24dp);
        } else {
            img.setImageResource(R.drawable.ic_cancel_red_24dp);
        }
        TextView p3 = new TextView(getApplicationContext());
        p3.setText("\n3.Получить подарок");
        p3.setTextColor(TEXT_COLOR);
        p3.setTextSize(TEXT_SIZE);
        LinearLayout ln = findViewById(R.id.main);
        ln.setLayoutParams(new LinearLayout.LayoutParams((int) (width * 0.9), (int) (height * 0.85)));
        ln.setBackgroundResource(R.drawable.films_drawable_back);
        ln.setPadding(60, 60, 60, 0);
        ln.setOrientation(LinearLayout.VERTICAL);
        ln.addView(p1);
        ln.addView(tv);
        ln.addView(p2);
        ln.addView(img);
        ln.addView(p3);

    }

    private void DateUnPickedUI() {
        TextView tv = new TextView(getApplicationContext());
        tv.setTextColor(TEXT_COLOR);
        tv.setTextSize(TEXT_SIZE);
        tv.setText(Html.fromHtml("<b>1.Чтобы получать бонусы к дню рождения необходимо ввести дату рождения!</b><br/>"));
        TextView p2 = new TextView(getApplicationContext());
        p2.setText("\n2.Дождатся дня рожения!");
        p2.setTextSize(TEXT_SIZE);
        p2.setTextColor(TEXT_COLOR);
        TextView p3 = new TextView(getApplicationContext());
        p3.setText("\n3.Получить подарок");
        p3.setTextColor(TEXT_COLOR);
        p3.setTextSize(TEXT_SIZE);
        Button btn = new Button(getApplicationContext());
        btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn.setText("Выбор даты");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatePicker();
            }
        });
        LinearLayout ln = findViewById(R.id.main);
        ln.setLayoutParams(new LinearLayout.LayoutParams((int) (width * 0.9), (int) (height * 0.85)));
        ln.setBackgroundResource(R.drawable.films_drawable_back);
        ln.setPadding(60, 60, 60, 0);
        ln.setOrientation(LinearLayout.VERTICAL);
        ln.addView(tv);
        ln.addView(btn);
        ln.addView(p2);
        ln.addView(p3);
    }

    public void ShowDatePicker() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(gift_for_dob.this, Film_Pick.class);
        startActivity(intent2);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent2 = new Intent(gift_for_dob.this, Film_Pick.class);
                startActivity(intent2);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SneakyThrows
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        StringBuilder result = new StringBuilder();
        result.append(c.get(Calendar.YEAR) + "-");
        if (c.get(Calendar.MONTH) < 10) {
            result.append("0");
        }
        result.append((c.get(Calendar.MONTH) + 1) + "-");
        if (c.get(Calendar.DAY_OF_MONTH) < 10) {
            result.append("0");
        }
        result.append(c.get(Calendar.DAY_OF_MONTH));
        updateDate(Storage.idaccount, result.toString());
        Storage.doB = result.toString();
        Intent intent2 = new Intent(gift_for_dob.this, Film_Pick.class);
        startActivity(intent2);
        finish();
    }



    void updateDate(final String id, final String dob) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        accApi = retrofit.create(AccountRepos.class);
        accApi.updateDob(id, dob)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Response result:", "Works");
                        Intent intent = new Intent(gift_for_dob.this, Film_Pick.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                    }
                });
    }


    void getGiftstatus(final String id, final boolean mode) {//mode - показывать кнопку получения подарка или проверить isResived
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        accApi = retrofit.create(AccountRepos.class);
        accApi.getIsresived(id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Response result:", "Works");
                        if (mode) {
                            BirthdayUI(response.body());
                        } else {
                            NonBirthdayUI(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                    }
                });
    }

    void giveBonuses(final String id) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        accApi = retrofit.create(AccountRepos.class);
        accApi.updateBonuses(id, BONUSES_VALUE)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Response result:", "Works");
                        setIsResived(id);
                        Storage.bonus += BONUSES_VALUE;

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                    }
                });
    }

    void setIsResived(final String id) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        accApi = retrofit.create(AccountRepos.class);
        accApi.updateIsresived(id, 1)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Response result:", "Works");
                        Intent intent = new Intent(gift_for_dob.this, Film_Pick.class);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                    }
                });
    }
}
