package andrew.cinema.cinema.Menu;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.zxing.WriterException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import andrew.cinema.cinema.Activities.Film_Pick;
import andrew.cinema.cinema.Entities.Tickets;
import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Repos.AccountRepos;
import andrew.cinema.cinema.Repos.TicketRepos;
import andrew.cinema.cinema.Utils.Storage;
import andrew.cinema.cinema.Utils.Util;
import lombok.SneakyThrows;
import lombok.val;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class buyed_tickets extends AppCompatActivity {
    private Retrofit retrofit;
    private TicketRepos ticketApi;
    private AccountRepos accApi;
    private int height;
    private int width;
    private int ticketHeight;
    private List<Tickets> tickets = new ArrayList<>();
    private boolean night = false;
    String qrMode;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        val sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        qrMode = sPref.getString("QR", "true");
        if (mode.equals("true")) {
            setTheme(R.style.Theme_AppCompat);
            night = true;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyed_tickets);
        getSupportActionBar().setTitle("Your tickets");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        ticketHeight = (int) (width * 0.35);
        LoadAllTickets(Storage.idaccount);


    }

    public void Draw() {
        LinearLayout containerMain = findViewById(R.id.Container);//https://media.discordapp.net/attachments/688087582747525221/701378433233584148/unknown.png

        if (tickets.size() > 0) {
            for (Tickets tk : tickets
            ) {
                LinearLayout ticketContainer = new LinearLayout(getApplicationContext());
                ticketContainer.setOrientation(LinearLayout.VERTICAL);
                ticketContainer.setGravity(Gravity.LEFT);
                LinearLayout.LayoutParams margins = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                margins.setMargins(30, 0, 0, 40);
                ticketContainer.setLayoutParams(margins);
                LinearLayout items = new LinearLayout(getApplicationContext());
                items.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout item1 = new LinearLayout(getApplicationContext());
                LinearLayout item2 = new LinearLayout(getApplicationContext());
                LinearLayout content = new LinearLayout(getApplicationContext());
                content.setGravity(Gravity.LEFT);
                content.setPadding(0, 10, 0, 10);
                content.setLayoutParams(new LinearLayout.LayoutParams((int) (width * 0.83), ViewGroup.LayoutParams.WRAP_CONTENT));
                ImageView img = new ImageView(getApplicationContext());
                item1.addView(content);
                Picasso.get()
                        .load(R.drawable.qr_image)
                        .resize(200, 200)
                        .into(img);
                item1.addView(img);
                item1.setGravity(Gravity.RIGHT);
                setupContent(content, tk);
                setupTicket(item1, item2, items, img, ticketContainer, tk);

                ticketContainer.addView(item1);
                ticketContainer.addView(items);
                containerMain.addView(ticketContainer);
            }
        } else {
            TextView tv = new TextView(getApplicationContext());
            if (!night) {
                tv.setTextColor(Color.BLACK);
            } else {
                tv.setTextColor(Color.WHITE);
            }
            tv.setText("You have no tickets");

            containerMain.addView(tv);

        }

    }

    public void setupContent(LinearLayout content, final Tickets tk) {
        NestedScrollView sc = new NestedScrollView(getApplicationContext());
        LinearLayout scContainer = new LinearLayout(getApplicationContext());
        scContainer.setOrientation(LinearLayout.VERTICAL);
        ImageView img = new ImageView(getApplicationContext());
        if (tk.getImage().length() > 5) {
            Picasso.get()
                    .load(tk.getImage())
                    .resize((int) (width * 0.42), (int) (height * 0.37))
                    .into(img);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.film_placeholder)
                    .resize((int) (width * 0.42), (int) (height * 0.37))
                    .into(img);
        }
        TextView tv = new TextView(getApplicationContext());
        TextView TitleName = new TextView(getApplicationContext());
        TitleName.setText(tk.getFilmname().toUpperCase());
        TitleName.setTextSize(16);
        TitleName.setTextColor(Color.BLACK);
        TitleName.setTypeface(null, Typeface.BOLD);

        //String a = tk.getStart().toString();
        tv.setTextColor(Color.BLACK);
        tv.setText(Html.fromHtml("Hall name: <b>" + tk.getHallname() + "</b><br/>" +
                "Row: <u>" + tk.getRow() + "</u> Place: <u>" + tk.getPlace() + "</u><br/><b>" +
                tk.getStart().toString().substring(tk.getStart().toString().length() - 4) + " " + tk.getStart().toString().substring(0, 11) + "<br/>" +
                tk.getStart().toString().substring(11, 19) + "</b>-<b>" + tk.getEnd().toString().substring(11, 19) + "</b>" +
                "<br/>Price: <u>" + tk.getPrice() + "</u><br/>" +
                "Hall type:<b>" + tk.getHalltype() + "</b><br/>" +
                "Cinema: " + tk.getCinemaname() + "<br/>" +
                "Adress: " + tk.getCinemaadress() + "<br/>"
        ));
        scContainer.addView(TitleName);
        scContainer.addView(tv);
        sc.addView(scContainer);
        content.addView(img);
        content.addView(sc);


    }

    public void setupTicket(final LinearLayout item1, final LinearLayout item2, final LinearLayout items, final ImageView icon, final LinearLayout ticketContainer, final Tickets tk) {
        final Animation anim = AnimationUtils.loadAnimation(buyed_tickets.this, R.anim.toptodown);

        item1.setLayoutParams(new LinearLayout.LayoutParams((int) (width * 0.95), ticketHeight + 50));
        item1.setBackgroundResource(R.drawable.qr_code_background);
        final LinearLayout temp = new LinearLayout(getApplicationContext());
        temp.setGravity(Gravity.TOP);

        icon.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View v) {
                setupQr(item2, tk);
                temp.removeAllViews();
                items.removeView(temp);
                items.removeView(item2);
                LinearLayout smallContainer = new LinearLayout(getApplicationContext());
                smallContainer.setGravity(Gravity.CENTER);
                smallContainer.setBackgroundResource(R.drawable.qr_code_background);
                smallContainer.setLayoutParams(new LinearLayout.LayoutParams((int) (width * 0.95 - height * 0.23), (int) (height * 0.23)));
                smallContainer.setPadding(15, 15, 15, 15);
                ImageView small = new ImageView(getApplicationContext());
                Picasso.get()
                        .load(SelectImage(tk.getIdticket()))
                        .resize((int) (width * 0.58), (int) (height * 0.21))
                        .into(small);
                smallContainer.addView(small);
                items.addView(item2);
                temp.addView(smallContainer);
                items.setGravity(Gravity.TOP);
                items.addView(temp);
                items.startAnimation(anim);
                icon.setEnabled(false);
            }
        });
    }

    public void setupQr(LinearLayout item2, final Tickets tk) throws WriterException {
        item2.removeAllViews();
        item2.setLayoutParams(new LinearLayout.LayoutParams((int) (height * 0.23), (int) (height * 0.23)));
        ImageView img = new ImageView(getApplicationContext());
        int qrSize =0 ;
        if(qrMode.equals("true"))
            qrSize = (int) (width * 0.7);
        else
            qrSize = (int) (width * 0.35);
        final val qr = Util.TextToImageEncode("Row:" + tk.getRow() + ". Place:" + tk.getPlace() + "." + "User id:" + tk.getIdaccount() + ". Price:" + tk.getPrice(), qrSize);
        img.setLayoutParams(new LinearLayout.LayoutParams((int) (height * 0.2), (int) (height * 0.2)));
        img.setImageBitmap(qr);


        img.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View v) {
                final val temp = new ImageView(getApplicationContext());
                temp.setImageBitmap(qr);
                temp.setMinimumHeight((int) (width * 0.8));
                temp.setMinimumWidth((int) (width * 0.8));
                final int curBrightnessValue = android.provider.Settings.System.getInt(
                        getContentResolver(),android.provider.Settings.System.SCREEN_BRIGHTNESS);
                try {
                    Settings.System.putInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS,
                            255);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(buyed_tickets.this).
                                setMessage("Scan the code").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        try {
                                            android.provider.Settings.System.putInt(getContentResolver(),
                                                    android.provider.Settings.System.SCREEN_BRIGHTNESS,
                                                    curBrightnessValue);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                })
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        try {
                                            android.provider.Settings.System.putInt(getContentResolver(),
                                                    android.provider.Settings.System.SCREEN_BRIGHTNESS,
                                                    curBrightnessValue);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setView(temp);
                builder.create().show();
            }
        });
        item2.setGravity(Gravity.CENTER);
        item2.setBackgroundResource(R.drawable.qr_code_background);
        // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(QRcodeWidth+50, QRcodeWidth+50);
        // item2.setLayoutParams(lp);
        item2.addView(img);
    }

    void LoadAllTickets(final String id) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        accApi = retrofit.create(AccountRepos.class);
        accApi.loadAll(id)
                .enqueue(new Callback<String>() {
                    @SneakyThrows
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Response result:", "Works");
                        String a = response.body();
                        ParseResult(response.body());
                        Draw();

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                    }
                });
    }

    public void ParseResult(String input) {
        Pattern regex = Pattern.compile("\\[{1}(.*?)\\]{1}");
        input = input.substring(1, input.length() - 1);
        Matcher matcher = regex.matcher(input);
        while (matcher.find()) {
            String[] res = matcher.group().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "").split(",");
            Tickets tk = new Tickets();
            tk.setIdticket(Integer.parseInt(res[0]));
            tk.setPrice(Double.parseDouble(res[1]));
            tk.setRow(Integer.parseInt(res[2]));
            tk.setPlace(Integer.parseInt(res[3]));
            tk.setIdsession(Integer.parseInt(res[4]));
            tk.setIdaccount(res[5]);
            tk.setStart(res[6]);
            tk.setEnd(res[7]);
            tk.setImage(res[8]);
            tk.setFilmname(res[9]);
            tk.setHallname(res[10]);
            tk.setHalltype(res[11]);
            tk.setCinemaname(res[12]);
            tk.setCinemaadress(res[13]);
            tickets.add(tk);
        }
    }

    public void Back(View v) {
        Intent intent2 = new Intent(buyed_tickets.this, Film_Pick.class);
        startActivity(intent2);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(buyed_tickets.this, Film_Pick.class);
        startActivity(intent2);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent2 = new Intent(buyed_tickets.this, Film_Pick.class);
                startActivity(intent2);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public int SelectImage(int id) {
        if (id % 8 == 1) {
            return R.drawable.sm_2;
        }
        if (id % 8 == 2) {
            return R.drawable.sm_3;
        }
        if (id % 8 == 3) {
            return R.drawable.sm_4;
        }
        if (id % 8 == 4) {
            return R.drawable.sm_5;
        }
        if (id % 8 == 5) {
            return R.drawable.sm_6;
        }
        if (id % 8 == 6) {
            return R.drawable.sm_7;
        }
        if (id % 8 == 7) {
            return R.drawable.sm_8;
        } else return R.drawable.sm_1;
    }
}
