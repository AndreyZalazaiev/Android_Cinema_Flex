package andrew.cinema.cinema;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.androidannotations.annotations.FromHtml;

import java.math.BigDecimal;
import java.security.spec.DSAParameterSpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import andrew.cinema.cinema.Entities.Film;
import andrew.cinema.cinema.Entities.Storage;
import andrew.cinema.cinema.Entities.Ticket;
import andrew.cinema.cinema.Repos.TicketRepos;
import lombok.SneakyThrows;
import lombok.val;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Place_Picker extends AppCompatActivity {
    private String cinemaType;
    private int rows = 0;
    private List<Integer> places = new ArrayList<>();
    private int place = 1;
    private static int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    private Retrofit retrofit;
    private TicketRepos ticketApi;
    private List<Ticket> tickets;
    private float baseprice = 0;
    private int width;
    private int height;
    private int idsession;
    private double hallCoef = 0.06;
    private Integer sent = 1;
    private boolean night = false;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPref;
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        sent = Integer.parseInt(sPref.getString("Sent", "1"));
        if (mode.equals("true")) {
            setTheme(R.style.Theme_AppCompat);
            night = true;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_picker);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//ошибки в мусорку ты свои перенаправь, user expirence - это не про нас
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        baseprice = Float.parseFloat(getIntent().getStringExtra("baseprice"));
        idsession = Integer.parseInt(getIntent().getStringExtra("idsession"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pick places");
        LoadTickets(idsession);
    }

    public void setPlaces(String type) {
        Film current = Storage.getFilmById(Integer.parseInt(getIntent().getStringExtra("idfilm")));
        TextView tv = new TextView(getApplicationContext());
        tv.setTextSize(24);
        tv.setTextColor(Color.BLACK);
        tv.setText(Html.fromHtml(current.getName() + "<br/> Type of Hall:" + type + "</br>"));
        LinearLayout ln = findViewById(R.id.Title);
        ln.setGravity(Gravity.TOP);
        ln.addView(tv);

        switch (type) {
            case "3D":
            case "2D":
                hallCoef = 0.085;
                FillRow(13, 3);
                FillRow(14, 2);
                FillRow(15, 1);
                FillRow(15, 1);
                FillRow(15, 1);
                FillRow(15, 1);
                FillRow(15, 1);
                FillRow(15, 1);
                FillRow(15, 1);
                FillRow(15, 1);
                FillRow(15, 1);
                break;
            case "4D":
                hallCoef = 0.085;
                FillRow(11, 3);
                FillRow(12, 2);
                FillRow(13, 1);
                FillRow(13, 1);
                FillRow(13, 1);
                FillRow(13, 1);
                FillRow(13, 1);
                FillRow(13, 1);
                FillRow(13, 1);
                FillRow(13, 1);
                break;
            case "IMAX":
                hallCoef = 0.085;
                FillRow(18, 2);
                FillRow(19, 1);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);
                FillRow(20, 0);

                break;
            default://что-то
        }
    }

    public void FillRow(int length, int skip)//Создание ряда
    {
        LinearLayout container = (LinearLayout) findViewById(R.id.container4);
        LinearLayout horizontal = new LinearLayout(Place_Picker.this);
        horizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        horizontal.setOrientation(LinearLayout.HORIZONTAL);
        horizontal.setTransitionName("Line" + rows);
        horizontal.setPadding(0, 20, 0, 20);
        rows++;
        container.addView(horizontal);
        CreateButtons(length, horizontal, rows, skip);
    }

    public void CreateButtons(int count, LinearLayout container, int rowNum, int skip) { //Создать кнопки в ряду  *количество*,*ряд*
        LinearLayout rowsContainer = findViewById(R.id.Rows);
        Button number = new Button(getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (height * hallCoef), (int) (height * hallCoef));
        layoutParams.setMargins(15, 0, 15, 0);
        if (rows < 10)
            number.setText("Ряд:" + rows);
        else
            number.setText("Ряд:" + rows);
        if (night) {
            number.setTextColor(Color.BLACK);
        }
        LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (height * hallCoef));
        rowParam.setMargins(0, 20, 0, 20);
        number.setLayoutParams(rowParam);
        number.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        number.setBackground(getResources().getDrawable(R.drawable.btn_transparent));
        rowsContainer.addView(number);
        for (int i = 0; i < skip; ++i) {
            Button skpBtn = new Button(Place_Picker.this);
            skpBtn.setEnabled(false);
            skpBtn.setVisibility(View.INVISIBLE);
            container.addView(skpBtn, layoutParams);

        }
        for (int i = 0; i < count; i++) {
            Button btn = new Button(Place_Picker.this);
            btn.setId(rowNum * 100 + (i + 1));
            switch (isBuyed(rowNum, (i + 1)))//состояние места
            {
                case 0:
                    btn.setTextColor(Color.parseColor("white"));
                    btn.setBackground(getResources().getDrawable(R.drawable.btn_background));
                    break;
                case 1:
                    btn.setTextColor(Color.parseColor("white"));
                    btn.setBackground(getResources().getDrawable(R.drawable.btn_background_buyed));
                    btn.setEnabled(false);
                    break;
                case 2:
                    btn.setTextColor(Color.parseColor("white"));
                    btn.setBackground(getResources().getDrawable(R.drawable.btn_background_buyed_byuser));
                    btn.setEnabled(false);
                    break;
            }
            btn.setText("" + (i + 1));
            place++;
            final int index = i;
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("Place num:", "" + v.getId());
                    ChangeColor(v.getId());
                    TextView total = findViewById(R.id.total);
                    total.setText("Total price: " + places.size() * baseprice + "\n");
                    total.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    total.setTextColor(Color.BLACK);
                }
            });
            container.addView(btn, layoutParams);
        }
    }

    public void ChangeColor(int id)//Меняет цвет после нажатия на кнопку с местом и записывает в List places
    {
        Button btn = (Button) findViewById(id);
        if (places.contains(id)) {
            places.remove(places.indexOf(id));
            btn.setBackground(getResources().getDrawable(R.drawable.btn_background));
        } else {
            places.add(id);
            btn.setBackground(getResources().getDrawable(R.drawable.btn_bought));//Без паники,оно работает
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(Place_Picker.this, session_pick.class);
        intent.putExtra("idfilm", getIntent().getStringExtra("idfilm"));
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(Place_Picker.this, session_pick.class);
                intent.putExtra("idfilm", getIntent().getStringExtra("idfilm"));
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoadTickets(Integer idsession) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ticketApi = retrofit.create(TicketRepos.class);
        ticketApi.getTickets(idsession)
                .enqueue(new Callback<List<Ticket>>() {
                    @Override
                    public void onResponse(Call<List<Ticket>> call, Response<List<Ticket>> response) {
                        Log.w("Session result:", "Works");
                        tickets = response.body();
                        setPlaces(getIntent().getStringExtra("type"));
                    }

                    @Override
                    public void onFailure(Call<List<Ticket>> call, Throwable t) {
                        Log.w("Session result:", "need registration");
                    }
                });
    }

    public int isBuyed(Integer row, Integer place) {
        for (Ticket tk : tickets) {
            if (tk.getRownum().equals(row) && tk.getPlace().equals(place)) {
                if (tk.getIdaccount().equals(Storage.idaccount)) {
                    return 2;//билет куплен и принадлежит текущему пользователю
                }
                return 1;//место занято
            }
        }
        return 0;//не куплен
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void onBuy(View v) {
        if(places.size()!=0) {
            ProcessPayment(places.size() * baseprice * 1.0);
        }
        else {
            Toast.makeText(this, "Pick at least one place", Toast.LENGTH_SHORT).show();
        }
    }//при нажатии кнопки купить

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    String row = "";
                    String place = "";
                    for (int i = 0; i < places.size(); i++) {
                        if (i != places.size() - 1) {
                            place += (places.get(i) % 100) + ",";
                            row += ((places.get(i) - places.get(i) % 100) / 100) + ",";
                        } else {
                            place += (places.get(i) % 100);
                            row += ((places.get(i) - places.get(i) % 100) / 100);
                        }
                    }
                    SendNotification("Pepega");
                    AddManyTickets(row, place);
                } else if(resultCode==PaymentActivity.RESULT_CANCELED) {
                    Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                }
                else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
    public void AddManyTickets(String row, String place)
    {
        String [] temp = row.split(",");
        StringBuilder sb = new StringBuilder();
        int bonus=0;
        for(int i =0 ;i<temp.length;i++)
        {
            bonus+=Storage.CalculateBonuses(baseprice);
            if(i!=temp.length-1) {
                sb.append(baseprice);
                sb.append(",");
            }
            else
            sb.append(baseprice);//Заглушка для цены//поменять
        }
        Storage.bonus+=bonus;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ticketApi = retrofit.create(TicketRepos.class);
        ticketApi.addTickets(Storage.idaccount,idsession,sb.toString(),place,row,bonus,sent)
                .enqueue(new Callback<String>() {
                    @SneakyThrows
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Tickets result:", "Works");
                        int a =3;
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Tickets result:", "need registration");
                    }
                });
        Intent intent = new Intent(Place_Picker.this, session_pick.class);
        intent.putExtra("idfilm", getIntent().getStringExtra("idfilm"));
        startActivity(intent);
        finish();
    }
    public void SendNotification(String ChannelId)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ChannelId)
                .setSmallIcon(R.drawable.ic_local_activity_black_24dp)
                .setContentTitle("Поздравляем с покупкой")
                .setWhen(new Date().getTime()+100000)
                .setContentText("Проверьте биллеты в приложении!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(":)"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
    public void onLegendClick(View v)
    {
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.legend);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this).
                        setMessage("Legend").
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        setView(image);
        builder.create().show();
    }
    void ProcessPayment(Double price)
    {
        PayPalPayment payPalPayment= new PayPalPayment(new BigDecimal(price),"USD","Pay for tickets:",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }
}
