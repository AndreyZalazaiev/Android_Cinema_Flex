package andrew.cinema.cinema.Utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Date;

public class Util {
    public static Bitmap TextToImageEncode(String Value, int QRcodeWidth) throws WriterException {

        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
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
                        Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, QRcodeWidth, 0, 0, bitMatrixWidth, bitMatrixHeight);

        return bitmap;
    }

    public static boolean isBirthday() {//повертає true, якщо в користувача день народження
        if (Storage.doB != null) {//чи встановлена дата в користувача
            Date now = new Date();//актуальна дата
            Integer day = now.getDate();
            Integer month = now.getMonth();
            String date = Storage.doB;
            String[] temp = date.split("T");//ковертування дати
            String[] curent = temp[0].split("-");
            return Integer.parseInt(curent[1]) == (month + 1) && Integer.parseInt(curent[2]) == day;
        } else return false;
    }

    public static String stringFormat(String input) {
        int a = 0;
        try {
            a = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (a < 10) {
            return a + " ";
        } else if (a < 100) {
            return a + "  ";
        } else return a + "";
    }
}
