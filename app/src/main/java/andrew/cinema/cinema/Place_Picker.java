package andrew.cinema.cinema;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import andrew.cinema.cinema.Entities.Halls;
import andrew.cinema.cinema.Entities.Session;
import andrew.cinema.cinema.Entities.Storage;
import andrew.cinema.cinema.Entities.Ticket;
import andrew.cinema.cinema.Repos.HallRepos;
import andrew.cinema.cinema.Repos.SessionRepos;
import andrew.cinema.cinema.Repos.TicketRepos;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Query;

public class Place_Picker extends AppCompatActivity {
    private String cinemaType;
    private int rows=0;
    private List<Integer> places = new ArrayList<>();
    private int place=1;
    private Retrofit retrofit;
    private TicketRepos ticketApi;
    private List<Ticket> tickets;
    private float baseprice=0;
    private int width;
    private int height;
    private int idsession;
    private double hallCoef=0.06;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_picker);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        baseprice=Float.parseFloat(getIntent().getStringExtra("baseprice"));
        idsession=Integer.parseInt(getIntent().getStringExtra("idsession"));
        LoadTickets(idsession);
    }
    public void Place_Picker(String cinemaType)
    {
        this.cinemaType=cinemaType;
    }
    public void setPlaces(String type)
    {
        switch (type)
        {
            case"3D":
                hallCoef=0.06;
            FillRow(3,3);
            FillRow(6,2);
            FillRow(7,1);
            FillRow(8,1);
            FillRow(8,1);
            break;
            case"2D":
                hallCoef=0.07;
                FillRow(2,1);
                FillRow(4,1);
                FillRow(6,1);
                FillRow(6,1);
                FillRow(7,1);
                break;
            case"4D":
                hallCoef=0.07;
                FillRow(4,1);
                FillRow(6,1);
                FillRow(8,1);
                FillRow(8,1);
                FillRow(8,1);
                break;
            case"IMAX":
                hallCoef=0.05;
                FillRow(5,1);
                FillRow(7,1);
                FillRow(8,1);
                FillRow(8,1);
                FillRow(8,1);
                break;
            default://что-то
        }
    }
    public void FillRow(int length,int skip)//Создание ряда
    {
        LinearLayout container = (LinearLayout)findViewById(R.id.container4);
        LinearLayout horizontal = new LinearLayout(Place_Picker.this);
        horizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        horizontal.setOrientation(LinearLayout.HORIZONTAL);
        horizontal.setTransitionName("Line"+rows);
        horizontal.setPadding(0,20,0,20);
        rows++;
        container.addView(horizontal);
        CreateButtons(length,horizontal,rows,skip);
    }
    public void CreateButtons(int count,LinearLayout container,int rowNum,int skip) { //Создать кнопки в ряду  *количество*,*ряд*
        TextView number= new TextView(Place_Picker.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)(height*hallCoef),(int)(height*hallCoef));
        layoutParams.setMargins(15, 0, 15, 0);
        number.setText("Ряд:"+rows+"  ");
        container.addView(number);
        for(int i=0;i<skip;++i)
        {
            Button skpBtn = new Button(Place_Picker.this);
            skpBtn.setEnabled(false);
            skpBtn.setVisibility(View.INVISIBLE);
            container.addView(skpBtn,layoutParams);

        }
        for (int i = 0; i < count; i++) {
            Button btn = new Button(Place_Picker.this);
            btn.setId(rowNum*100+(i+1));
            switch (isBuyed(rowNum,(i+1)))//состояние места
            {
                case 0:
                    btn.setTextColor(Color.parseColor("white"));
                    btn.setBackground(getResources().getDrawable(R.drawable.btn_background));
                    break;
                case 1:
                    btn.setTextColor(Color.parseColor("white"));
                    btn.setBackground(getResources().getDrawable(R.drawable.btn_background_buyed));
                    btn.setEnabled(false);
                    break;
                case 2:
                    btn.setTextColor(Color.parseColor("white"));
                    btn.setBackground(getResources().getDrawable(R.drawable.btn_background_buyed_byuser));
                    btn.setEnabled(false);
                    break;
            }
            btn.setText(""+(i+1));
            place++;
            final int index = i;
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("Place num:",""+v.getId());
                    ChangeColor(v.getId());
                }
            });
            container.addView(btn,layoutParams);
        }
    }
    public void ChangeColor(int id)//Меняет цвет после нажатия на кнопку с местом и записывает в List places
    {
        Button btn = (Button) findViewById(id);
        if(places.contains(id)) {
            places.remove(places.indexOf(id));
            btn.setBackground(getResources().getDrawable(R.drawable.btn_background));
        }
        else
        {
            places.add(id);
            btn.setBackground(getResources().getDrawable(R.drawable.btn_bought));//Без паники,оно работает
        }
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(Place_Picker.this, session_pick.class);
        intent.putExtra("idfilm", getIntent().getStringExtra("idfilm"));
        startActivity(intent);
        finish();
    }
    public void LoadTickets(Integer idsession)
    {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ticketApi= retrofit.create(TicketRepos.class);
        ticketApi.getTickets(idsession)
                .enqueue(new Callback<List<Ticket>>() {
                    @Override
                    public void onResponse(Call<List<Ticket>> call, Response<List<Ticket>> response) {
                        Log.w("Session result:", "Works");
                        tickets=response.body();
                        setPlaces(getIntent().getStringExtra("type"));
                    }

                    @Override
                    public void onFailure(Call<List<Ticket>> call, Throwable t) {
                        Log.w("Session result:", "need registration");
                    }
                });
    }
    public int isBuyed(Integer row,Integer place)
    {
        for (Ticket tk:tickets){
            if(tk.getRownum().equals(row)&&tk.getPlace().equals(place))
            {
                if(tk.getIdaccount().equals(Storage.idaccount))
                {
                    return 2;//билет куплен и принадлежит текущему пользователю
                }
                return 1;//место занято
            }
        }
        return 0;//не куплен
    }
    public void onBuy(View v)
    {
        String row = "";
        String place="";
        for (int i = 0;i<places.size();i++) {
           if(i!=places.size()-1) {
               place += (places.get(i) % 100) + ",";
               row += ((places.get(i) - places.get(i) % 100) / 100) + ",";
           }
           else {
               place += (places.get(i) % 100) ;
               row += ((places.get(i) - places.get(i) % 100) / 100) ;
           }
        }
        AddManyTickets(row,place);

    }
    public void AddManyTickets(String row,String place)
    {
        String [] temp = row.split(",");
        StringBuilder sb = new StringBuilder();
        for(int i =0 ;i<temp.length;i++)
        {
            if(i!=temp.length-1) {
                sb.append("220,");
            }
            else
            sb.append("220");//Заглушка для цены//поменять
        }
        retrofit = new Retrofit.Builder()
                .baseUrl("https://restapicinema.herokuapp.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ticketApi = retrofit.create(TicketRepos.class);
        ticketApi.addTickets(Storage.idaccount,idsession,sb.toString(),place,row)
                .enqueue(new Callback<String>() {
                    @SneakyThrows
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Tickets result:", "Works");
                        int a =3;
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Tickets result:", "need registration");
                    }
                });
        Intent intent = new Intent(Place_Picker.this, session_pick.class);
        intent.putExtra("idfilm", getIntent().getStringExtra("idfilm"));
        startActivity(intent);
        finish();
    }
}
