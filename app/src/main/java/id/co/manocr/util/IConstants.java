package id.co.manocr.util;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by KrisnaPutra on 1/20/2016.
 */
public interface IConstants {
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_PICK_PHOTO = 2;
    public static Gson GSON=new Gson();
    public interface IBundleKey{
        public static String BK_STRATEGY_GET_IMAGE="strategiGetImage";
        public static String BK_IMAGE_URI="IMAGE_URI";
        public static String BK_IMAGE_PATH="PATH_IMAGE";
        public static String BK_RESULT_OCR="RESULT_OCR";

        public static Type TYPE_REFLECT_URI=new TypeToken<Uri>(){}.getType();
    }
}
