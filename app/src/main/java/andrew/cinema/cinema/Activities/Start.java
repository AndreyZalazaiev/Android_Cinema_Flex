package andrew.cinema.cinema.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import andrew.cinema.cinema.Entities.Account;
import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Utils.Storage;
import andrew.cinema.cinema.Repos.AccountRepos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Start extends AppCompatActivity {
    private SharedPreferences sPref;
    private Retrofit retrofit;
    private AccountRepos accApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        if (mode.equals("true"))
            setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        String idaccount = sPref.getString("idaccount", "noAcc");
        if (idaccount == "noAcc") {
            Intent intent = new Intent(Start.this, Authentication.class);
            startActivity(intent);
            finish();
        } else
            LoadAccount(idaccount);
    }

    public void LoadAccount(final String id) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        accApi = retrofit.create(AccountRepos.class);
        accApi.isExist(id)
                .enqueue(new Callback<Account>() {
                    @Override
                    public void onResponse(Call<Account> call, Response<Account> response) {
                        Log.w("Response result:", "Works");
                        getHours(id);
                        Storage.name = response.body().getName();
                        Storage.email = response.body().getEmail();
                        Storage.idaccount = response.body().getIdaccount();
                        if (response.body().getPicture() == null) {
                            Storage.picture = "https://i.stack.imgur.com/34AD2.jpg";
                        } else
                            Storage.picture = response.body().getPicture();
                        Storage.doB = response.body().getDoB();
                        Storage.bonus = response.body().getBonus();

                        String idcinema = sPref.getString("idcinema", "noCinema");
                        if (idcinema == "noCinema") {
                            Intent intent = new Intent(Start.this, Cinema_Pick.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(Start.this, Film_Pick.class);
                            Storage.idcinema = Integer.parseInt(idcinema);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Account> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                        Intent intent = new Intent(Start.this, Authentication.class);
                        startActivity(intent);
                        finish();
                    }
                });
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
                        if (response.body() != null) {
                            Storage.setRank(Double.parseDouble(response.body()));
                        } else Storage.setRank(0.0);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                    }
                });
    }
}
