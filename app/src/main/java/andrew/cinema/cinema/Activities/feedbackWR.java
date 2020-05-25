package andrew.cinema.cinema.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import andrew.cinema.cinema.R;
import andrew.cinema.cinema.Repos.AccountRepos;
import andrew.cinema.cinema.Repos.ReviewRepos;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class feedbackWR extends AppCompatActivity {
    private Retrofit retrofit;
    private static ReviewRepos revApi;
    private static AccountRepos accApi;
    private String revText="";
    private int idreview;
    private int mark;
    private int idfilm;
    private String idaccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPref;
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sPref.getString("DayNightMode", "true");
        if(mode.equals("true"))
            setTheme(R.style.Theme_AppCompat);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_w_r);
        idfilm=Integer.parseInt(getIntent().getStringExtra("idfilm"));
        String isEdit = getIntent().getStringExtra("edit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit/Write");
        if(isEdit.equals("true"))
        {
            idreview = Integer.parseInt(getIntent().getStringExtra("idreview"));
            revText = getIntent().getStringExtra("text");
            mark=Integer.parseInt(getIntent().getStringExtra("mark"));
            EditText text = findViewById(R.id.text);
            RatingBar rb = findViewById(R.id.rb);
            rb.setStepSize(1);
            OnEdit();
        }
        else
        {
             idaccount = getIntent().getStringExtra("idaccount");
            idfilm = Integer.parseInt(getIntent().getStringExtra("idfilm"));
            EditText text = findViewById(R.id.text);
            RatingBar rb = findViewById(R.id.rb);
            LayerDrawable stars = (LayerDrawable) rb.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            rb.setStepSize(1);
            OnWrite();
        }
    }

    public void OnEdit() {
        final EditText text = findViewById(R.id.text);
        final RatingBar rb = findViewById(R.id.rb);
        rb.setStepSize(1);
        LayerDrawable stars = (LayerDrawable) rb.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        text.setText(revText);
        rb.setRating(mark);
        LinearLayout container = findViewById(R.id.Container);
        Button btn = new Button(this);
        btn.setText("Update");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateReview(idreview,text.getText().toString(), (int) rb.getRating());
            }
        });
        container.addView(btn);
    }
    public void OnWrite(){
        LinearLayout container = findViewById(R.id.Container);
        Button btn = new Button(this);
        btn.setText("Write review");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = findViewById(R.id.text);
                RatingBar rb = findViewById(R.id.rb);
                InsertReview(idaccount,idfilm,text.getText().toString(),(int)rb.getRating());
            }
        });
        container.addView(btn);


    }
    public void UpdateReview(int idreview, String text, int mark) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        revApi = retrofit.create(ReviewRepos.class);
        revApi.Update(idreview, text, mark)
                .enqueue(new Callback<String>() {
                    @SneakyThrows
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Response result:", "Works");
                        String penis = response.body();
                        BackToReviews();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Response result:", "need registration");
                        BackToReviews();
                    }
                });
    }
    public void InsertReview(String idaccount, Integer idfilm, String text, Integer mark) {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        revApi = retrofit.create(ReviewRepos.class);
        revApi.addRev(idfilm, idaccount, text, mark)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Review:", "added");
                        BackToReviews();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Review:", "eror");
                        BackToReviews();
                    }
                });
    }
    public void BackToReviews()
    {
        Intent intent = new Intent(feedbackWR.this,Feedback.class);
        intent.putExtra("idfilm",""+idfilm);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(feedbackWR.this,Feedback.class);
                intent.putExtra("idfilm",""+idfilm);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        BackToReviews();
    }
}
