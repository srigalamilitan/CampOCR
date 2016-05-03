package id.co.manocr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Kelas ini merupakan kelas yang digunakan untuk Splash screen.
 * Dimana kelas ini akan mengecek ketersediaan library *.traineddata
 * yang akan digunakan untuk processing data OCR.
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Waktu Delay Setelah Selesai copy library data OCR (*.traineddata)
     */
    private final static int SPLASH_TIME_OUT = 1500;

    /**
     * Method yang pertama dipanggil ketika program running
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Set Layout yang akan digunakan
        setContentView(R.layout.activity_splash);
        // setting Full Screen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * Method yang akan menjalankan process checking dan
     * ketika process checking telah selesai akan
     * mengganti screen secara otomatis
     *
     */
    @Override
    protected void onStart(){
        super.onStart();
        new Handler().postDelayed(
                //setting Thread
                new Runnable() {
            @Override
            public void run() {
                //pengecekan data trainedData
                MoveFileRawToCacheDir();
                //Mempersiapkan intent untuk perpindahan Screen
                Intent main = new Intent(SplashActivity.this, MainActivity.class);
                //pindahkan Screen/activity
                startActivity(main);
                finish();//set activity telah selesai digunakan
            }
        }, SPLASH_TIME_OUT);
    }

    /**
     * Method ini adalah method yang digunakan untuk
     * Checking folder (tessdata,tessImage) dan file eng.traineddata
     */
    void MoveFileRawToCacheDir(){
        //check folder tessdata kalau tidak ada create folder
        File checkingFolder = new File(getCacheDir()+"/tessdata");
        if(!checkingFolder.exists()){
            checkingFolder.mkdir();//create folder
        }
        //check folder tessImage kalo tidak ada create folder
        File checkingFolderTempImage = new File(Environment.getExternalStorageDirectory()+"/tessImage");
        if(!checkingFolderTempImage.exists()){
            checkingFolderTempImage.mkdir();//create folder
        }

        //check file eng.traineddata kalau tidak ada
        //copy dari folder raw
        File f = new File(getCacheDir()+"/tessdata/eng.traineddata");
        if (!f.exists())
            try {

                InputStream is = getResources().openRawResource(R.raw.eng);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) { throw new RuntimeException(e); }

    }
}
