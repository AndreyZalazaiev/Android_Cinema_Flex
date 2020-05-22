package andrew.cinema.cinema.Entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

@Data
public class Tickets {
    private int idticket;
    private double price;
    private int place;
    private int row;
    private int idsession;
    private String idaccount;
    private Date start;
    private String image;
    private String filmname;
    private String hallname;
    private String halltype;
    private String cinemaname;
    private String cinemaadress;
    private Date end;

    public void setStart(String input) {
        SimpleDateFormat formatterFirst = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        Date date = null;
        try {
            date = formatterFirst.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.start = date;


    }
    public void setEnd(String input) {
        SimpleDateFormat formatterFirst = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        Date date = null;
        try {
            date = formatterFirst.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.end = date;


    }

}
