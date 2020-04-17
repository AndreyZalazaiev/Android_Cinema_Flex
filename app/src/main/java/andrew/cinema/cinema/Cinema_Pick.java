package andrew.cinema.cinema;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import andrew.cinema.cinema.Entities.Cinema;
import andrew.cinema.cinema.Entities.Storage;
import andrew.cinema.cinema.Repos.CinemaRepos;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Cinema_Pick extends AppCompatActivity {
    private static CinemaRepos cinemaApi;
    private Retrofit retrofit;
    private List<Cinema> cinemas;
    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cinema_pick);
        LoadCinema();
    }
    public void OnNextClick(View v)
    {
        Integer id=3;
        Spinner spinner = findViewById(R.id.spinner);
        String selected=spinner.getSelectedItem().toString();
        String []splited =selected.split(":");
        for (Cinema cnm: cinemas
             ) {
            if(splited[0].equals(cnm.getName()))
            {
                id=cnm.getIdcinema();
                break;
            }
        }
        Log.v("Selected cinema:",""+id);
        Intent intent = new Intent(Cinema_Pick.this,Film_Pick.class);
        Storage.idcinema=id;
        startActivity(intent);

    }
    public void DrawCinema(List<Cinema> cinema)
    {
        cinemas=cinema;
        ImageView img = findViewById(R.id.imageView7);
        Spinner spinner = findViewById(R.id.spinner);
        img.setImageResource(R.drawable.logo);
        List<String> values= new ArrayList<String>();
        for (Cinema cnm:cinema
             ) {
            values.add(cnm.getName()+":"+ cnm.getAdress());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.getSelectedItem();
    }
    public void LoadCinema()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        cinemaApi = retrofit.create(CinemaRepos.class); //Создаем объект, при помощи которого будем выполнять запросы
        cinemaApi.getData().enqueue(new Callback<List<Cinema>>() {
            @Override
            public void onResponse(Call<List<Cinema>> call, Response<List<Cinema>> response) {
                Log.v("Response result:",response.body().toString());
                DrawCinema(response.body());
            }
            @Override
            public void onFailure(Call<List<Cinema>> call, Throwable t) {
                Log.v("Response result:","Eror");
            }
        });
    }
    public static CinemaRepos getApi() {
        return cinemaApi;
    }
    public void ToFilmPick(View w)
    {
        Intent intent = new Intent(Cinema_Pick.this,Film_Pick.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }
    /*void DrawCinema(List<Cinema>cinema) {
        LinearLayout container = findViewById(R.id.Container);
        for (Cinema cnm : cinema
        ) {
            LinearLayout horizontal = new LinearLayout(Cinema_Pick.this);
            horizontal.setId(cnm.getIdcinema());
            horizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            horizontal.setOrientation(LinearLayout.HORIZONTAL);
            ImageView img = new ImageView(Cinema_Pick.this);
            img.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            img.setImageResource(R.drawable.baseline_apartment_black_48dp);
            TextView description = new TextView(Cinema_Pick.this);
            description.setText(cnm.getName() + ":\n" + cnm.getAdress());
            description.setTextColor(Color.rgb(65, 105, 225));
            description.setTextSize(18);
            //description.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            description.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Cinema_Pick.this, Place_Picker.class);
                    startActivity(intent);
                    finish();
                }
            });
            container.addView(horizontal);
            horizontal.addView(img);
            horizontal.addView(description);
            Log.v("Container", "" + cnm.getIdcinema());
        }
    }
*/
/*
List<Cinema> parseJSON(String input) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<Cinema>cinema = objectMapper.readValue(input, new TypeReference<List<Cinema>>(){});
    return cinema;
}
public class setupCinema extends AsyncTask<Void, Void, List<Cinema>>
{
    @Override
    protected void onPreExecute() {
    }

    @SneakyThrows
    @Override
    protected List<Cinema> doInBackground(Void... params) {
        res=null;
        getCinema("https://restapicinema.herokuapp.com/cinema");
        List<Cinema> cinema;
        cinema=parseJSON(res);
        return cinema;
    }

    @SneakyThrows
    @Override
    protected void onPostExecute(List<Cinema> cinema) {
        DrawCinema(cinema);
    }
}
    void DrawCinema(List<Cinema>cinema)
    {
        LinearLayout container = findViewById(R.id.Container);
        for (Cinema cnm:cinema
             ) {
            LinearLayout horizontal = new LinearLayout(Cinema_Pick.this);
            horizontal.setId(cnm.getIdcinema());
            horizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            horizontal.setOrientation(LinearLayout.HORIZONTAL);
            ImageView img = new ImageView(Cinema_Pick.this);
            img.setLayoutParams(new LinearLayout.LayoutParams(200,200));
            img.setImageResource(R.drawable.baseline_apartment_black_48dp);
            TextView description = new TextView(Cinema_Pick.this);
            description.setText(cnm.getName()+":\n"+cnm.getAdress());
            description.setTextColor(Color.rgb(65,105,225));
            description.setTextSize(18);
            //description.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            description.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Cinema_Pick.this,Place_Picker.class);
                    startActivity(intent);finish();
                }
            });
            container.addView(horizontal);
            horizontal.addView(img);
            horizontal.addView(description);
            Log.v("Container",""+cnm.getIdcinema());
        }

    }
    private final class Operation extends AsyncTask<Void, Void, String> {

        @SneakyThrows
        @Override
        protected String doInBackground(Void... params) {
                doGetRequest("https://restapicinema.herokuapp.com/cinema");
            publishProgress ();
            return "Executed";
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                cinema = objectMapper.readValue(res, new TypeReference<List<Cinema>>() {
                });
            }
            catch (Exception e)
            {
                Log.v("Erors",e.toString());
            }
            DrawCinema();//как с этим жить
        }
        @Override
        protected void onPostExecute(String result) {

        }
    }
   void getCinema(String url) throws IOException {
       OkHttpClient client = new OkHttpClient();

       Request request = new Request.Builder()
               .url(url)
               .build();
       client.newCall(request)
               .enqueue(new Callback() {
                   @Override
                   public void onFailure(final Call call, IOException e) {
                       Log.v("DoGetRequest result", "Eror");
                   }

                   @Override
                   public void onResponse(Call call, final Response response) throws IOException {
                       res = response.body().string();
                       Log.v("DoGetRequest result", res);
                   }
               });
   }*/
}
