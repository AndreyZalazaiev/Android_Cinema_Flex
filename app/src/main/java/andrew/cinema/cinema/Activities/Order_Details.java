package andrew.cinema.cinema.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.widget.NestedScrollView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Repos.AccountRepos;
import andrew.cinema.cinema.Repos.TicketRepos;
import andrew.cinema.cinema.Utils.Config;
import andrew.cinema.cinema.Utils.Storage;
import andrew.cinema.cinema.Utils.Util;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Order_Details extends AppCompatActivity {
    public static final int BONUSES_CONST = 50;
    private static int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    private String rowsStr;
    private Retrofit retrofit;
    private TicketRepos ticketApi;
    private int TEXT_SIZE = 18;
    private AccountRepos accApi;
    private String placesStr;
    private String filmName;
    private String type;
    private int idsession;
    private double totalPrice = 0;
    private int totalBonuses = 0;
    private double baseprice;
    private String date;
    private Integer sent = 1;
    private List<Double> prices = new ArrayList<>();
    private int bonusItemes = 0;
    private int width;
    private int height;
    String notif;
    private boolean night = false;
    private int TEXT_COLOR = Color.BLACK;
    private Date formLaucnh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__details);
        formLaucnh= new Date();
        init();
        Draw();
        AddManyTickets(rowsStr, placesStr, 0,1);
    }

    public void Draw() {
        LinearLayout main = findViewById(R.id.Main);
        final TextView filmDesc = new TextView(getApplicationContext());
        NestedScrollView nsc = new NestedScrollView(getApplicationContext());
        LinearLayout scrollContainer = new LinearLayout(getApplicationContext());
        LinearLayout container = new LinearLayout(getApplicationContext());

        LinearLayout descTopContainer = new LinearLayout(getApplicationContext());
        ScrollView scDesc = new ScrollView(getApplicationContext());
        LinearLayout descContainer= new LinearLayout(getApplicationContext());
        descTopContainer.addView(scDesc);
        descContainer.addView(filmDesc);
        scDesc.addView(descContainer);

        final String[] dateParse = date.split("T");

        nsc.addView(scrollContainer);
        main.addView(descTopContainer);
        container.addView(nsc);
        main.addView(container);


        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.54)));
        descTopContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.25)));

        scrollContainer.setOrientation(LinearLayout.VERTICAL);
        String[] places = placesStr.split(",");
        String[] rows = rowsStr.split(",");
        for (int i = 0; i < places.length; i++)//Цикл по билетам
        {
            prices.add(baseprice);
            LinearLayout.LayoutParams margTickets = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            margTickets.setMargins(0, 50, 50, 50);
            LinearLayout.LayoutParams marg = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marg.setMargins(0, 0, 50, 0);

            LinearLayout ticket = new LinearLayout(getApplicationContext());
            ticket.setPadding(50, 50, 50, 50);
            if(night)
                ticket.setBackgroundResource(R.drawable.films_drawable_back);
            else ticket.setBackgroundResource(R.drawable.background_gray);
            ticket.setLayoutParams(margTickets);
            ticket.setOrientation(LinearLayout.HORIZONTAL);
            TextView info = new TextView(getApplicationContext());
            info.setTextColor(Color.BLACK);
            info.setLayoutParams(marg);
            info.setText(Html.fromHtml("Row: " + Util.stringFormat(rows[i]) + "<br/>Place: " + Util.stringFormat(places[i])));
            final TextView price = new TextView(getApplicationContext());
            price.setTextColor(Color.BLACK);
            price.setLayoutParams(marg);
            price.setText("Base price:\n" + "$" + baseprice);
            totalPrice += baseprice;
            TextView discount = new TextView(getApplicationContext());
            discount.setTextColor(Color.BLACK);
            discount.setLayoutParams(marg);
            discount.setText("Get discount 50%\nfor " + (int) (baseprice * BONUSES_CONST) + " bonuses:");
            Switch sw = new Switch(getApplicationContext());
            sw.setBackgroundResource(R.drawable.btn_background_buyed);
            sw.setChecked(false);
            final int finalI = i;
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        bonusItemes++;
                        prices.set(finalI, baseprice / 2);
                        price.setText("Base price:\n" + "$" + baseprice / 2);
                        totalPrice -= baseprice / 2;
                        totalBonuses += baseprice * BONUSES_CONST;
                        filmDesc.setTextSize(TEXT_SIZE);
                        filmDesc.setText(Html.fromHtml("<b>" + filmName + "</b><br/>Date: "
                                + dateParse[0]
                                + "<br/>Time: "
                                + dateParse[1].split("\\.")[0] + "<br/>Type: "
                                + type + "<br/><br/>You have "
                                + Storage.bonus + " bonuses in account <br/><b>Total  price: "
                                + totalPrice + "</b><br/><b>Total bonuses: "
                                + totalBonuses + "</b><br/>"));
                        filmDesc.setTextColor(TEXT_COLOR);
                    } else {
                        bonusItemes--;
                        prices.set(finalI, baseprice * 2);
                        price.setText("Base price:\n" + "$" + baseprice);
                        totalPrice += baseprice / 2;
                        totalBonuses -= baseprice * BONUSES_CONST;
                        filmDesc.setTextSize(TEXT_SIZE);
                        filmDesc.setText(Html.fromHtml("<b>" + filmName + "</b><br/>Date: "
                                + dateParse[0]
                                + "<br/>Time: "
                                + dateParse[1].split("\\.")[0] + "<br/>Type: "
                                + type + "<br/><br/>You have "
                                + Storage.bonus + " bonuses in account <br/><b>Total  price: "
                                + totalPrice + "</b><br/><b>Total bonuses: "
                                + totalBonuses + "</b><br/>"));
                        filmDesc.setTextColor(TEXT_COLOR);
                    }
                }
            });
            HorizontalScrollView horizontal = new HorizontalScrollView(getApplicationContext());
            LinearLayout horizontalContainer = new LinearLayout(getApplicationContext());
            horizontal.addView(horizontalContainer);

            ticket.addView(horizontal);
            scrollContainer.addView(ticket);
            horizontalContainer.addView(info);
            horizontalContainer.addView(price);
            horizontalContainer.addView(discount);
            horizontalContainer.addView(sw);
        }
        filmDesc.setTextSize(TEXT_SIZE);
        filmDesc.setPadding(25,0,0,0);
        filmDesc.setText(Html.fromHtml("<b>" + filmName + "</b><br/>Date: "
                + dateParse[0]
                + "<br/>Time: "
                + dateParse[1].split("\\.")[0] + "<br/>Type: "
                + type + "<br/><br/>You have "
                + Storage.bonus + " bonuses in account <br/><b>Total  price: "
                + totalPrice + "</b><br/><b>Total bonuses: "
                + totalBonuses + "</b><br/>"));
        filmDesc.setTextColor(TEXT_COLOR);


    }

    public void settings() {
        SharedPreferences sPref;
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        sent = Integer.parseInt(sPref.getString("Sent", "1"));
        notif = sPref.getString("Notifications", "true");
        if (mode.equals("true")) {
            setTheme(R.style.Theme_AppCompat);
            TEXT_COLOR = Color.WHITE;
            night = true;
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void init() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        rowsStr = getIntent().getStringExtra("rows");
        placesStr = getIntent().getStringExtra("places");
        filmName = getIntent().getStringExtra("filmName");
        baseprice = Double.parseDouble(getIntent().getStringExtra("baseprice"));
        idsession = Integer.parseInt(getIntent().getStringExtra("idsession"));

        date = getIntent().getStringExtra("date");
        type = getIntent().getStringExtra("type");
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order confirmation");
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(Order_Details.this, Place_Picker.class);
        intent.putExtra("idsession", "" + idsession);
        intent.putExtra("baseprice", "" + baseprice);
        intent.putExtra("idfilm", getIntent().getStringExtra("idfilm"));
        intent.putExtra("type", type);
        intent.putExtra("date", date);

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(Order_Details.this, Place_Picker.class);
                intent.putExtra("idsession", "" + idsession);
                intent.putExtra("baseprice", "" + baseprice);
                intent.putExtra("idfilm", getIntent().getStringExtra("idfilm"));
                intent.putExtra("type", type);
                intent.putExtra("date", date);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    if(notif.equals("true"))
                    SendNotification("Pepega");
                    AddManyTickets(rowsStr, placesStr, Storage.CalculateBonuses(baseprice) * (prices.size() - bonusItemes),0);

                    if (!Util.isBirthday())
                        setIsResived(Storage.idaccount);
                } else if (resultCode == PaymentActivity.RESULT_CANCELED) {
                    Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void AddManyTickets(String row, String place, int bonus, final int reserve) {
        StringBuilder sb = new StringBuilder();
        for (Double p : prices
        ) {
            sb.append(p + ",");
        }
        Storage.bonus += bonus;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ticketApi = retrofit.create(TicketRepos.class);
        ticketApi.addTickets(Storage.idaccount, idsession, sb.toString().substring(0, sb.length() - 1), place, row, bonus, sent,reserve)
                .enqueue(new Callback<String>() {
                    @SneakyThrows
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Tickets result:", "Works");
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Tickets result:", "need registration");
                    }
                });
        if(reserve==0) {
            Intent intent = new Intent(Order_Details.this, Film_Pick.class);
            startActivity(intent);
            finish();
        }
    }

    public void SendNotification(String ChannelId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ChannelId)
                .setSmallIcon(R.drawable.ic_local_activity_black_24dp)
                .setContentTitle("Поздравляем с покупкой")
                .setWhen(new Date().getTime() + 100000)
                .setContentText("Проверьте биллеты в приложении!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Проверьте билеты в приложении!"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    void ProcessPayment(Double price) {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(price), "USD", "Pay for tickets:", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    void setIsResived(final String id) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        accApi = retrofit.create(AccountRepos.class);
        accApi.updateIsresived(id, 0)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Response result:", "Works");
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Response result:", "smth gone wrong");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    public void onPayClick(View v) {
        if (totalBonuses <= Storage.bonus) {
            Date cur = new Date();
            if (formLaucnh.getTime()+900000<cur.getTime())//15 min
            {
                Toast.makeText(getApplicationContext(), "Reservation time is over,please select places again", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Order_Details.this, Place_Picker.class);
                intent.putExtra("idsession", "" + idsession);
                intent.putExtra("baseprice", "" + baseprice);
                intent.putExtra("idfilm", getIntent().getStringExtra("idfilm"));
                intent.putExtra("type", type);
                intent.putExtra("date", date);

                startActivity(intent);
                finish();
            }
            else
                ProcessPayment(totalPrice);
        }
        else
            Toast.makeText(getApplicationContext(), "Not enough bonuses", Toast.LENGTH_SHORT).show();
    }


}
