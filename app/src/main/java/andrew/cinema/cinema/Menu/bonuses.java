package andrew.cinema.cinema.Menu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import andrew.cinema.cinema.Utils.Storage;
import andrew.cinema.cinema.Activities.Film_Pick;
import andrew.cinema.cinema.R;
import lombok.val;

public class bonuses extends AppCompatActivity {
private Boolean night=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        val sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        if(mode.equals("true")){
            night=true;
            setTheme(R.style.Theme_AppCompat);}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonuses);
        getSupportActionBar().setTitle("Your bonuses: "+ Storage.bonus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        TextView tv = new TextView(getApplicationContext());
        tv.setText(Html.fromHtml("<H1>Бонусная система</H1><br/>" +
                "При покупке билетов у Вас есть возможность получить бонусы в зависимости от общей стоимости покупки и вашего ранга, ваш ранг увеличивается от покупок и чем выше он, тем приятнее бонусы!<br/>" +
                "Вы можете потратить бонусы, чтобы получить 50% скидку на покупку билета(стоимость скидки в бонусах зависит от стоимости билета)<br/>" +
                "<br/><small>При покупке билетов с использование скидки, бонусы не начисляются</small><br/>" +
                "Не забывайте, что на Ваш день рожденья вы получите приятный подарок!(Заполните дату рождения в профиле)<br/><small>Размер подарка также зависит от ранга</small>"));
        tv.setPadding(80,80,80,80);
        tv.setTextSize(25);
        tv.setTextColor(Color.BLACK);
        LinearLayout ln = findViewById(R.id.Main);
        LinearLayout content = findViewById(R.id.Info);
        ln.setLayoutParams(new LinearLayout.LayoutParams((int)(width*0.9),(int)(height*0.85)));
        if(night)
        ln.setBackgroundResource(R.drawable.films_drawable_back);
        else ln.setBackgroundResource(R.drawable.background_gray);
        ln.setOrientation(LinearLayout.VERTICAL);
        ln.setGravity(Gravity.CENTER);
        content.addView(tv);
    }
    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(bonuses.this, Film_Pick.class);
        startActivity(intent2);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent2 = new Intent(bonuses.this, Film_Pick.class);
                startActivity(intent2);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
