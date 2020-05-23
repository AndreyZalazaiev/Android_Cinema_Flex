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
    public static boolean isBirthday() {
        if (Storage.doB != null) {
            Date now = new Date();
            Integer day = now.getDate();
            Integer month = now.getMonth();
            String date = Storage.doB;
            String[] temp = date.split("T");
            String[] curent = temp[0].split("-");
            return Integer.parseInt(curent[1]) == (month + 1) && Integer.parseInt(curent[2]) == day;
        } else return false;
    }
}
