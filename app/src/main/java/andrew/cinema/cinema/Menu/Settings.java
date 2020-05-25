package andrew.cinema.cinema.Menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import andrew.cinema.cinema.Activities.Film_Pick;
import andrew.cinema.cinema.R;
import lombok.val;

public class Settings extends AppCompatActivity {
    boolean night =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        val sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        String emails = sPref.getString("Sent", "1");
        String soon = sPref.getString("Soon", "true");
        String notif = sPref.getString("Notifications", "true");
        if(mode.equals("true")) {
            night=true;
            setTheme(R.style.Theme_AppCompat);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchState(mode, emails,soon,notif);
        ExtraParams();
        EmailsSetting();
        DayNightMode();
        Soon();
        Notifications();
    }

    private void switchState(String mode, String emails,String soon,String nottif) {
        if(mode.equals("true"))
        {
            Switch sw = findViewById(R.id.NightMode);
            sw.setTextColor(Color.WHITE);
            sw.setChecked(true);
        }
        if(emails.equals("1")) {
            Switch em = findViewById(R.id.Emails);
            em.setChecked(true);
        }
        if(soon.equals("true"))
        {
            Switch sn = findViewById(R.id.Soon);
            sn.setChecked(true);
        }
        if(nottif.equals("true"))
        {
            Switch sn = findViewById(R.id.Notify);
            sn.setChecked(true);
        }
    }

    private void ExtraParams() {
        TextView version = findViewById(R.id.Version);
        Button support = findViewById(R.id.Support);
        support.setBackgroundResource(R.drawable.btn_background);
        support.setPadding(20,20,20,20);
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/bergmah"));
                startActivity(browserIntent);
            }
        });
        if(night)
        {
            version.setTextColor(Color.WHITE);
        }
    }

    private void EmailsSetting() {
        final Switch sw = findViewById(R.id.Emails);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = getApplication().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if(!sw.isChecked()) {
                    if(isChecked)
                    editor.putString("Sent", ""+1);
                    else
                        editor.putString("Sent", ""+0);

                } else {
                    editor.remove("Sent");
                }
                editor.commit();
            }
        });
    }

    public void DayNightMode() {
        final Switch sw = findViewById(R.id.NightMode);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = getApplication().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if(!sw.isChecked()) {
                    editor.putString("DayNightMode", ""+isChecked);
                } else {
                    editor.remove("DayNightMode");
                }
                editor.commit();
            }
        });
    }
    public void Notifications() {
        final Switch notif = findViewById(R.id.Notify);
        notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = getApplication().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if(!notif.isChecked()) {
                    editor.putString("Notifications", ""+isChecked);
                } else {
                    editor.remove("Notifications");
                }
                editor.commit();
            }
        });
    }
    public void Soon() {
        final Switch sw = findViewById(R.id.Soon);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = getApplication().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if(!sw.isChecked()) {
                    editor.putString("Soon", ""+isChecked);
                } else {
                    editor.remove("Soon");
                }
                editor.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(Settings.this, Film_Pick.class);
        startActivity(intent2);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent2 = new Intent(Settings.this, Film_Pick.class);
                startActivity(intent2);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void Drin(View v)
    {
        Intent intent2 = new Intent(Settings.this, Film_Pick.class);
        startActivity(intent2);
        finish();
    }
}
