package andrew.cinema.cinema.Menu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DecimalFormat;

import andrew.cinema.cinema.Utils.Storage;
import andrew.cinema.cinema.Activities.Film_Pick;
import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Repos.AccountRepos;
import andrew.cinema.cinema.Utils.Util;
import lombok.val;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class about_account extends AppCompatActivity {
    private int width;
    private int height;
    final double SIZE_CONST = 0.15;
    private Retrofit retrofit;
    private AccountRepos accApi;
    private int color;
    private LinearLayout.LayoutParams marg;
    boolean night = false;
    String qrMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        val sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        qrMode= sPref.getString("QR", "true");
        if (mode.equals("true")) {
            setTheme(R.style.Theme_AppCompat);
            night = true;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_account);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        getSupportActionBar().setTitle(Storage.name + "`s account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Draw();
    }

    public void Draw() {
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp((float) (width / 2 * SIZE_CONST))
                .oval(false)
                .build();
        ImageView img = new ImageView(getApplicationContext());
        Picasso.get()
                .load(Storage.picture)
                .placeholder(R.drawable.melon_placeholder)
                .resize((int) Math.round(width * 0.3), (int) Math.round(width * 0.3))
                .transform(transformation)
                .into(img);
        LinearLayout main = findViewById(R.id.Main);
        TextView tv = new TextView(getApplicationContext());
        tv.setTextSize(25);
        String dob;
        if (Storage.doB == null)
            dob = "unset";
        else dob = Storage.doB.split("T")[0];
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        double rnd = Math.random();
        String cake;
        if (rnd > 0.8)
            cake = "birthday: ";
        else
            cake = "bonuses: ";
        marg = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        marg.setMargins(10, 50, 10, 50);
        View ruler2 = new View(getApplicationContext());
        color = R.color.colorPrimary;
        ruler2.setBackgroundColor(color);
        ruler2.setLayoutParams(marg);
        View ruler3 = new View(getApplicationContext());
        ruler3.setBackgroundColor(color);
        ruler3.setLayoutParams(marg);
        ;
        View ruler4 = new View(getApplicationContext());
        ruler4.setBackgroundColor(color);
        ruler4.setLayoutParams(marg);
        TextView tv1, tv2, tv3;
        tv1 = new TextView(getApplicationContext());
        tv2 = new TextView(getApplicationContext());
        tv3 = new TextView(getApplicationContext());
        //Да простят боги мои костыли, у меня не было выбора
        tv.setText(Html.fromHtml("<br/>Name: <b>" + Storage.name + "</b>"));
        tv1.setText(Html.fromHtml("<br/>Email: " + Storage.email + "<br/>"));
        tv2.setText(Html.fromHtml("<br/>Date of birthday: " + dob + "<br/>"));
        tv3.setText(Html.fromHtml("<br/>Amount of  " + cake + Storage.bonus + "<br/>"));

        tv1.setTextSize(25);
        tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv2.setTextSize(25);

        tv3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv3.setTextSize(25);
        if (!night) {
            tv.setTextColor(Color.BLACK);
            tv1.setTextColor(Color.BLACK);
            tv2.setTextColor(Color.BLACK);
            tv3.setTextColor(Color.BLACK);
        } else {
            tv.setTextColor(Color.WHITE);
            tv1.setTextColor(Color.WHITE);
            tv2.setTextColor(Color.WHITE);
            tv3.setTextColor(Color.WHITE);
        }
        main.setGravity(Gravity.CENTER);
        main.addView(img);
        main.addView(tv);
        main.addView(tv1);
        main.addView(ruler2,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
        main.addView(tv2);
        main.addView(ruler3,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
        main.addView(tv3);
        main.addView(ruler4,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
        getHours(Storage.idaccount);
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(about_account.this, Film_Pick.class);
        startActivity(intent2);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent2 = new Intent(about_account.this, Film_Pick.class);
                startActivity(intent2);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void getHours(final String id) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        accApi = retrofit.create(AccountRepos.class);
        accApi.getHours(id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        View ruler = new View(getApplicationContext());
                        ruler.setBackgroundColor(color);
                        ruler.setLayoutParams(marg);
                        Log.w("Response result:", "Works");
                        LinearLayout main = findViewById(R.id.Main);
                        TextView tv = new TextView(getApplicationContext());
                        if (!night)
                            tv.setTextColor(Color.BLACK);
                        else
                            tv.setTextColor(Color.WHITE);
                        tv.setTextSize(25);
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        DecimalFormat df = new DecimalFormat("###.#");
                        if (response.body() != null) {
                            Storage.setRank(Double.parseDouble(response.body()));
                            tv.setText(Html.fromHtml("<br/>Viewed hours: " + df.format(Double.parseDouble(response.body())) + "<br/><br/><b> Rank: " + Storage.rank + "</b>"));
                        } else tv.setText("\nViewed hours: " + "0");
                        main.addView(ruler,
                                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
                        main.addView(tv);

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                    }
                });
    }

    public void showQR(View v) throws WriterException, Settings.SettingNotFoundException {
        int qrSize =0 ;
        if(qrMode.equals("true"))
            qrSize = (int) (width * 0.4);
        else
            qrSize = (int) (width * 0.1);
        final val qr = Util.TextToImageEncode(Storage.idaccount, qrSize);
        final val temp = new ImageView(getApplicationContext());
        temp.setImageBitmap(qr);
        temp.setMinimumHeight((int) (width * 0.8));
        temp.setMinimumWidth((int) (width * 0.8));
        final int curBrightnessValue = android.provider.Settings.System.getInt(
                getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS);
        try {
            android.provider.Settings.System.putInt(getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS,
                    255);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(about_account.this).
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
}
