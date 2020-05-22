package andrew.cinema.cinema.Menu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import andrew.cinema.cinema.Entities.Storage;
import andrew.cinema.cinema.Film_Pick;
import andrew.cinema.cinema.R;
import lombok.val;

public class bonuses extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        val sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        if(mode.equals("true"))
            setTheme(R.style.Theme_AppCompat);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonuses);
        getSupportActionBar().setTitle("Your bonuses: "+ Storage.bonus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        TextView tv = new TextView(getApplicationContext());
        tv.setText("lLorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean mollis risus arcu, vitae congue leo porttitor et. Aenean libero leo, tempor ac interdum vestibulum, pellentesque sed odio. Phasellus egestas urna at nisl iaculis, non tristique orci efficitur. Morbi eu purus at massa laoreet egestas. Sed vehicula risus eu leo sodales luctus. Aenean vestibulum varius diam. Sed porta nibh ut euismod lobortis.\n" +
                "\n" +
                "Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\n" +
                "\n");
        tv.setPadding(80,80,80,80);
        tv.setTextSize(25);
        tv.setTextColor(Color.BLACK);
        LinearLayout ln = findViewById(R.id.Main);
        LinearLayout content = findViewById(R.id.Info);
        ln.setLayoutParams(new LinearLayout.LayoutParams((int)(width*0.9),(int)(height*0.85)));
        ln.setBackgroundResource(R.drawable.films_drawable_back);
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
