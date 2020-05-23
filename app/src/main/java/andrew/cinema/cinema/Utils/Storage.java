package andrew.cinema.cinema.Utils;

import java.util.List;

import andrew.cinema.cinema.Entities.Film;

public class Storage {
    public static String idaccount;
    public static String name;
    public static String picture;
    public static String email;
    public static Integer bonus;
    public static String doB;
    public static List<Film> films;
    public static Integer idfilm;
    public static Integer idcinema;
    public static Rank rank;

    public static void setRank(Double hours) {
        if (hours == 0) {
            rank = Rank.Nowby;
        } else if (hours < 10) {
            rank = Rank.Newby;
        } else if (hours < 30) {
            rank = Rank.Someone;
        } else if (hours < 50) {
            rank = Rank.HuMan;
        } else if (hours < 100) {
            rank = Rank.VIP;
        } else if (hours < 150) {
            rank = Rank.Legend;
        } else {
            rank = Rank.Flexer;
        }
    }

    public static int CalculateBonuses(double price) {
        price*=10;
        switch (rank) {
            case Nowby:
                return 0;
            case Newby:
                return (int) (price / 4);
            case Someone:
                return (int) (price / 2);
            case HuMan:
                return (int) (price / 4 * 3);
            case VIP:
                return (int) price;
            case Legend:
                return (int) ((int) price * 1.5);
            case Flexer:
                return (int) (price * 2);
            default:
                return 0;
        }
    }

    public static Film getFilmById(int id) {
        for (Film films : films
        ) {
            if (films.getIdfilm() == id) {
                return films;
            }
        }
        return null;
    }

    public enum Rank {

        Nowby,
        Newby,
        Someone,
        HuMan,
        VIP,
        Legend,
        Flexer
    }


}
