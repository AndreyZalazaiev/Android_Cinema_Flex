package andrew.cinema.cinema.Entities;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

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
    public static Integer idcinema;
    public static Rank rank;
    public enum Rank{

        Nowby,
        Newby,
        Someone,
        HuMan,
        VIP,
        Legend,
        Flexer
    }
    public static void setRank(Double hours)
    {
        if(hours==0)
        {
            rank=Rank.Nowby;
        }
        else if(hours<10)
        {
            rank=rank.Newby;
        }
        else if(hours<30)
        {
            rank=Rank.Someone;
        }
        else if(hours<50)
        {
            rank=Rank.HuMan;
        }
        else if(hours<100)
        {
            rank=Rank.VIP;
        }
        else if(hours<150)
        {
            rank=Rank.Legend;
        }
        else {
            rank=Rank.Flexer;
        }
    }
    public static int CalculateBonuses(double price)
    {
        switch (rank)
        {
            case Nowby:
                return 0;
            case Newby:
                return (int)(price/4);
            case Someone:
                return (int)(price/2);
            case HuMan:
                return (int)(price/4*3);
            case VIP:
                return (int)price;
            case Legend:
                return (int) ((int)price*1.5);
            case Flexer:
                return (int)(price*2);
            default:return 0;
        }
    }
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
    public static Bitmap TextToImageEncode(String Value,int QRcodeWidth) throws WriterException {

        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth,  QRcodeWidth,null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        Color.BLACK :Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, (int)(QRcodeWidth), 0, 0, bitMatrixWidth, bitMatrixHeight);

        return bitmap;
    }

}
