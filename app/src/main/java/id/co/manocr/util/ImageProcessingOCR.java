package id.co.manocr.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

/**
 * Created by KrisnaPutra on 1/17/2016.
 */
public class ImageProcessingOCR {
    private TessBaseAPI mTess;
    public ImageProcessingOCR( String p_packageName) {
        // TODO Auto-generated constructor stub
        mTess = new TessBaseAPI();
        String language = "eng";
        mTess.init(p_packageName, language);
    }

    public String getOCRResult(Bitmap bitmap) {
        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();
        Log.d("+++++++++++Result ",result);
        return result;
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }
}
