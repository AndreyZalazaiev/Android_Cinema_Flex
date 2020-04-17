package andrew.cinema.cinema;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.BoringLayout;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import andrew.cinema.cinema.Entities.Account;
import andrew.cinema.cinema.Entities.Review;
import andrew.cinema.cinema.Entities.Storage;
import andrew.cinema.cinema.Repos.AccountRepos;
import andrew.cinema.cinema.Repos.ReviewRepos;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Feedback extends AppCompatActivity {
    private static ReviewRepos revApi;
    private static AccountRepos accApi;
    private Retrofit retrofit;
    private List<Review> reviews;
    private int idfilm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        idfilm=Integer.parseInt(getIntent().getStringExtra("idfilm"));
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
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();//Это ложь
        FirstReview();//отзыв пользователя будет первым ///////метод не смотреть
        LinearLayout container = findViewById(R.id.container);
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
                rb.setStepSize((float) 1);
                picture.setOrientation(LinearLayout.HORIZONTAL);
                Account currentAccount = findAccount(acc, rev.getIdaccount());

                rb.setRating((float) rev.getMark());
                TextView name = new TextView(getApplicationContext());
                name.setText(" " + currentAccount.getName());
                name.setTextColor(Color.BLACK);
                name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                TextView text = new TextView(getApplicationContext());
                text.setText(rev.getText() + "\n");
                text.setTextColor(Color.BLACK);
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
                //Размещение
                starsContainer.addView(rb);
                picture.addView(userPic);
                picture.addView(name);
                vertical.addView(picture);
                vertical.addView(starsContainer);
                vertical.addView(text);
                container.addView(vertical);
            }
        } else {
            TextView tv = new TextView(getApplicationContext());
            tv.setText("No reviews, do you want to be the first?");
            tv.setTextColor(Color.BLACK);
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
        intent.putExtra("idfilm",""+idfilm);
        startActivity(intent);
        finish();
    }
    public void OnWriteRev(View v)
    {
        // UpdateReview((int) reviews.get(0).getIdreview(), input.getText().toString(), (int) rb.getRating());
        // InsertReview(Storage.idaccount, idfilm, input.getText().toString(), (int) rb.getRating());
        Boolean editMode=false;
        Intent intent = new Intent(Feedback.this,feedbackWR.class);
        if(reviews.size()>0) {
            if(reviews.get(0).getIdaccount().equals(Storage.idaccount)) {
                editMode=true;
                intent.putExtra("idreview",""+reviews.get(0).getIdreview());
                intent.putExtra("edit",editMode.toString());
                intent.putExtra("text",reviews.get(0).getText());
                intent.putExtra("mark",""+reviews.get(0).getMark());
                intent.putExtra("idfilm",""+idfilm);
                startActivity(intent);
                this.finish();
            }
        }
            intent.putExtra("edit",editMode.toString());
            intent.putExtra("idaccount",Storage.idaccount);
            intent.putExtra("idfilm",""+idfilm);
        startActivity(intent);
        this.finish();
    }
    /*
    public void OnWriteRev(View v) {
        //region Initialize interface
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your review");

        // Set up the input
        final TextView eror = new TextView(this);
        eror.setText("");
        eror.setTextColor(Color.RED);
        LinearLayout rating = new LinearLayout(this);
        rating.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        final LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout text = new LinearLayout(this);
        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        final EditText input = new EditText(this);
        final RatingBar rb = new RatingBar(this);
        rb.setNumStars(5);
        rb.setStepSize(1);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        if (reviews.size() > 0) {//if review is already done
            if (reviews.get(0).getIdaccount().equals(Storage.idaccount)) {
                builder.setTitle("Edit review");
                input.setText(reviews.get(0).getText());
                rb.setRating((float) reviews.get(0).getMark());
                Integer idfilm = Integer.parseInt(getIntent().getStringExtra("idfilm"));
            }
        }

        rating.addView(rb);
        text.addView(input);
        container.addView(eror);
        container.addView(rating);
        container.addView(text);

        builder.setView(container);

        // Set up the buttons
        //endregion
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Setup
                String text = input.getText().toString();
                Integer rating = (int) rb.getRating();
                Boolean edit = false;
                if (reviews.size() > 0) {
                    if (reviews.get(0).getIdaccount().equals(Storage.idaccount)) {
                        edit = true;
                    }
                }
                //Wtrite new or rewrite
                if (text.length() < 5 || rating.equals(0)) {
                    eror.setText("Wrong input");
                } else if (edit) {
                    UpdateReview((int) reviews.get(0).getIdreview(), input.getText().toString(), (int) rb.getRating());
                    Refresh();
                } else {
                    Integer idfilm = Integer.parseInt(getIntent().getStringExtra("idfilm"));
                    InsertReview(Storage.idaccount, idfilm, input.getText().toString(), (int) rb.getRating());
                    Refresh();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }//Depreceated
    *///захоронение

    /*void DistinctReviews() {
        List<String> idaccount = new ArrayList<String>();
        for (int i = 0; i < reviews.size(); i++) {
            if (idaccount.contains(reviews.get(i).getIdaccount())) {
                reviews.remove(i);
                i--;
            } else idaccount.add(reviews.get(i).getIdaccount());
        }
    }
*/
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


    public void RefreshButton(View v)
    {
        Refresh();
    }
    public void Refresh()
    {
        Intent intent = new Intent(Feedback.this, Feedback.class);
        intent.putExtra("idfilm", "" + idfilm);
        startActivity(intent);
        finish();

    }
}
