package andrew.cinema.cinema.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import andrew.cinema.cinema.Entities.Account;
import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Utils.Storage;
import andrew.cinema.cinema.Repos.AccountRepos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class google_sign extends AppCompatActivity {
    private static AccountRepos accApi;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPref;
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        if (mode.equals("true"))
            setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            Storage.name = personName;
            Storage.email = personEmail;
            Storage.idaccount = personId;
            if (personPhoto == null) {
                Storage.picture = "https://i.stack.imgur.com/34AD2.jpg";
            } else
                Storage.picture = personPhoto.toString();
            isRegistered(personId);
        }
    }

    void isRegistered(final String id) {
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
                        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("idaccount", "" + id);
                        ed.commit();
                        Intent intent = new Intent(google_sign.this, Start.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Account> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                        RegisterUser(Storage.name, Storage.email, Storage.idaccount, Storage.picture);
                    }
                });
    }

    void RegisterUser(String name, String email, final String id, String pic) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        accApi = retrofit.create(AccountRepos.class);
        accApi.registerUser(id, pic, 0, name, email, "").enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.v("Response result:", "registered");
                //save id
                SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("idaccount", "" + id);
                ed.commit();
                Intent intent = new Intent(google_sign.this, Start.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.v("Response result:", "Eror while register");
            }
        });
    }
}
