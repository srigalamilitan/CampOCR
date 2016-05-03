package id.co.manocr.util;


import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;

public class ClassUtil {
    //Method yang digunakan untuk substring String yang panjangnya lebih dari 31 character
    public  static String dataSubstring(String _string)
    {
        if(_string.length()>31)
        {
            return _string.substring(0, 28)+" ...";
        }
        else
            return _string;



    }
    public static void showOKDialog(Activity _acActivity,String _message){
        AlertDialog alertDialog = new AlertDialog.Builder(_acActivity).create();
        alertDialog.setMessage(_message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
    public static String getAttibuteBundle(Activity act,String key)
    {
        String _result="";
        if(act.getIntent().getExtras().getString(key)!=null)
        {
            _result=act.getIntent().getExtras().getString(key);
        }
        return _result;
    }
    public static Integer getAttibuteBundleInteger(Activity act,String key)
    {
        Integer _result=0;
        if(act.getIntent().getExtras().getInt(key)>0)
        {
            _result=act.getIntent().getExtras().getInt(key);
        }
        return _result;
    }
    public static String getStringFromObject(Object ob, int type)
    {
        String _result="-";
        if(type==1)//String
        {
            _result=(ob!=null ? ""+ob :_result);
        }
        if(type==2)//date
        {
            _result=(ob!=null ? ((Date)ob).toLocaleString() :_result);
        }

        return _result;
    }
//    public static void PutPrefrences(Activity _act,String _key,String _value)
//    {
//        SharedPreferences settings = _act.getSharedPreferences(IConstans.PREFS_NAME, 0);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(_key, _value);
//        editor.commit();
//    }
//    public static String getPreference(Activity _act,String _key,String _defaultvalue)
//    {
//        SharedPreferences settings = _act.getSharedPreferences(IConstans.PREFS_NAME, 0);
//        return settings.getString(_key, _defaultvalue);
//    }
//    public static Object toObject(int _typeint,String data)
//    {
//        Gson son=IConstans.GSON;
//        Object _obj=null;
//        try {
//
//            if(_typeint==IConstans.TypeReflectModel.USER_TYPE_KEY){
//                _obj=son.fromJson(data, IConstans.TypeReflectModel.USER_TYPE);
//            }
//            else if(_typeint==IConstans.TypeReflectModel.LOG_TRIP_HEADER_TYPE_KEY)
//            {
//                _obj=son.fromJson(data, IConstans.TypeReflectModel.LOG_TRIP_HEADER_TYPE);
//            }
//            else if(_typeint==IConstans.TypeReflectModel.DEVICE_TYPE_KEY)
//            {
//                _obj=son.fromJson(data, IConstans.TypeReflectModel.DEVICE_TYPE);
//            }
//
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//
//        return _obj;
//
//    }
    //	 <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    public static String getMyPhoneNumber(Activity _act)
    {
        return ((TelephonyManager) _act.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
    }
    //	 <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    public static String getMyPhoneImei(Activity _act)
    {
        return ((TelephonyManager) _act.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }
    public static void showOKDialogExit(Activity _acActivity,String _message){
        AlertDialog alertDialog = new AlertDialog.Builder(_acActivity).create();
        alertDialog.setMessage(_message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    // Parameter For Update Location
    public static boolean RUN_THREAD=false;
    public static int idHeader=0;

}