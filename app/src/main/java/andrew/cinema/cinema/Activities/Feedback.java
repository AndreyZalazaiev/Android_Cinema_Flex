package andrew.cinema.cinema.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import andrew.cinema.cinema.Entities.Account;
import andrew.cinema.cinema.Entities.Review;
import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Utils.Storage;
import andrew.cinema.cinema.Repos.AccountRepos;
import andrew.cinema.cinema.Repos.ReviewRepos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Feedback extends AppCompatActivity {
    private static ReviewRepos revApi;
    private static AccountRepos accApi;
    private Retrofit retrofit;
    private List<Review> reviews;
    private int idfilm;
    private boolean night = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPref;
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        if (mode.equals("true")) {
            setTheme(R.style.Theme_AppCompat);
            night = true;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        idfilm = Integer.parseInt(getIntent().getStringExtra("idfilm"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reviews");
        LoadReviews(idfilm);

        ///На теелефоне добавление в уведомлении не работает
        //UpdateReview(20, "Ono update-it", 5);

    }

    public void LoadReviews(int id) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        revApi = retrofit.create(ReviewRepos.class);
        revApi.findRev(id)
                .enqueue(new Callback<List<Review>>() {
                    @Override
                    public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                        Log.w("Response result:", "Works");
                        reviews = response.body();
                        LoadAcc();
                    }

                    @Override
                    public void onFailure(Call<List<Review>> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                    }
                });
    }//не смотреть защишено копирайтом

    public void LoadAcc() //Асинхронно-синхронная загрузка La Kostil(c)
    {
        // DistinctReviews();//- дублирование записей в отзывах
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        accApi = retrofit.create(AccountRepos.class);
        accApi.shortAccounts()
                .enqueue(new Callback<List<Account>>() {
                    @Override
                    public void onResponse(Call<List<Account>> call, Response<List<Account>> response) {
                        Log.w("Response result:", "Works");
                        DrawReviews(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Account>> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                    }
                });
    }

    public void DrawReviews(List<Account> acc) {
        final Animation anim = AnimationUtils.loadAnimation(Feedback.this, R.anim.toptodown);
        FirstReview();//отзыв пользователя будет первым
        LinearLayout container = findViewById(R.id.Container);
        container.setPadding(0,0,0,50);
        if (reviews.size() > 0) {
            for (Review rev : reviews
            ) {
                //region Setup
                LinearLayout starsContainer = new LinearLayout(getApplicationContext());
                LinearLayout vertical = new LinearLayout(getApplicationContext());
                vertical.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                starsContainer.setLayoutParams(lp);
                LinearLayout picture = new LinearLayout(getApplicationContext());
                RatingBar rb = new RatingBar(getApplicationContext());
                rb.setIsIndicator(true);
                rb.setNumStars(5);
                LayerDrawable stars = (LayerDrawable) rb.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                rb.setStepSize((float) 1);
                picture.setOrientation(LinearLayout.HORIZONTAL);
                Account currentAccount = findAccount(acc, rev.getIdaccount());

                rb.setRating((float) rev.getMark());
                TextView name = new TextView(getApplicationContext());
                name.setText(" " + currentAccount.getName());
                name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                TextView text = new TextView(getApplicationContext());
                text.setText(rev.getText() + "\n");
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
                ImageView userPic = new ImageView(getApplicationContext());

                if (currentAccount.getPicture().length() < 5) {
                    Picasso.get()
                            .load(R.drawable.default_user_pic)
                            .resize(200, 200)
                            .into(userPic);
                } else {
                    Picasso.get()
                            .load(currentAccount.getPicture())
                            .placeholder(R.drawable.default_user_pic)
                            .resize(200, 200)
                            .into(userPic);
                }
                //endregion
                if (!night) {
                    name.setTextColor(Color.BLACK);
                    text.setTextColor(Color.BLACK);
                } else {
                    name.setTextColor(Color.WHITE);
                    text.setTextColor(Color.WHITE);
                }
                //Размещение
                starsContainer.addView(rb);
                picture.addView(userPic);
                picture.addView(name);
                vertical.addView(picture);
                vertical.addView(starsContainer);
                vertical.addView(text);
                container.addView(vertical);
                vertical.startAnimation(anim);
            }
        } else {
            TextView tv = new TextView(getApplicationContext());
            tv.setText("No reviews, do you want to be the first?");
            if (!night)
                tv.setTextColor(Color.BLACK);
            else
                tv.setTextColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            container.addView(tv);
        }

    }

    public Account findAccount(List<Account> acc, String id) {
        for (Account account : acc
        ) {
            if (account.getIdaccount().equals(id)) {
                return account;
            }
        }
        return null;//возможный костыль, но пока работает
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(Feedback.this, film_page.class);
        intent.putExtra("idfilm", "" + idfilm);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(Feedback.this, film_page.class);
                intent.putExtra("idfilm", "" + idfilm);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void OnWriteRev(View v) {
        // UpdateReview((int) reviews.get(0).getIdreview(), input.getText().toString(), (int) rb.getRating());
        // InsertReview(Storage.idaccount, idfilm, input.getText().toString(), (int) rb.getRating());
        Boolean editMode = false;
        Intent intent = new Intent(Feedback.this, feedbackWR.class);
        if (reviews.size() > 0) {
            if (reviews.get(0).getIdaccount().equals(Storage.idaccount)) {
                editMode = true;
                intent.putExtra("idreview", "" + reviews.get(0).getIdreview());
                intent.putExtra("edit", editMode.toString());
                intent.putExtra("text", reviews.get(0).getText());
                intent.putExtra("mark", "" + reviews.get(0).getMark());
                intent.putExtra("idfilm", "" + idfilm);
                startActivity(intent);
                this.finish();
            }
        }
        intent.putExtra("edit", editMode.toString());
        intent.putExtra("idaccount", Storage.idaccount);
        intent.putExtra("idfilm", "" + idfilm);
        startActivity(intent);
        this.finish();
    }

    void FirstReview() {
        Review temp = null;
        for (Review rev : reviews
        ) {
            if (rev.getIdaccount().equals(Storage.idaccount))// == не работает,я не хотел
            {
                temp = rev;
                reviews.remove(rev);
                break;
            }
        }
        if (temp != null) {
            Collections.reverse(reviews);//я этого не делал
            reviews.add(temp);
            Collections.reverse(reviews);//простите
        }
    }

    public void RefreshButton(View v) {
        Refresh();
    }

    public void Refresh() {
        Intent intent = new Intent(Feedback.this, Feedback.class);
        intent.putExtra("idfilm", "" + idfilm);
        startActivity(intent);
        finish();

    }
}
