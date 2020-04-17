package andrew.cinema.cinema.Entities;

import java.security.PublicKey;
import java.util.List;

public  class Storage {
    public static String idaccount;
    public static String name;
    public static String picture;
    public static String email;
    public static Integer bonus;
    public static String doB;
    public static List<Film> films;
    public static Integer idfilm;
    public static Integer idcinema =3;
    public static Film getFilmById(int id)
    {
        for (Film films:films
             ) {
            if(films.idfilm==id)
            {
                return films;
            }
        }
        return null;
    }

}
