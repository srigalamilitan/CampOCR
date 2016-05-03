package id.co.manocr;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static id.co.manocr.util.IConstants.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mButtonGallery, mButtonCamera,mButtonHelp;
    private String mCurrentPhotoPath;
    Uri mImageCaptureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * Inisiasi Component pada Java Code.
         * Agar Dapat digunakan di Java
         */
        mButtonGallery = (Button) findViewById(R.id.bt_gallery);
        mButtonGallery.setOnClickListener(this);
        mButtonCamera = (Button) findViewById(R.id.bt_camera);
        mButtonCamera.setOnClickListener(this);
        mButtonHelp=(Button)findViewById(R.id.bt_help);
        mButtonHelp.setOnClickListener(this);

    }

    /**
     * Method onclick digunakan untuk menangkap Aksi Click pada Layar
     * @param v
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.bt_gallery:
                pickPhoto();
                break;
            case R.id.bt_camera:
                takeAPicFromCamera();
                break;
            case R.id.bt_help:
                help();
                break;
        }
    }

    /**
     * Menampilkan Halaman Help
     */
    private void help(){
        Intent intent=new Intent(this,HelpActivity.class);
        startActivity(intent);
    }
    // Menampilkan Galery
    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    /**
     * Ketika Photo telah terpilih dari galery ataupun sudah selesai ambil gambar dari camera
     * maka method OnActivityResult Akan di Panggil.
     * Dalam method ini kita akan memilih/ menentukan prilaku/tugas setelahnya
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK) {
            //Bundle Merupakan Object yang dapat dikirim ke halaman/activity berikutnya
            Bundle bundle = new Bundle();
            //pengecekan Process
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                //Gambar diambil dari kamera
                // maka kita set path dari gambar yang telah kita ambil
                bundle.putString(IBundleKey.BK_IMAGE_URI, mImageCaptureUri.toString());
            } else if (requestCode == REQUEST_PICK_PHOTO && resultCode == Activity.RESULT_OK) {
                //Gambar diambil dari Galery maka kita set gambar nyaa.
                Uri uri = data.getData();
                if (uri != null) {
                    bundle.putString(IBundleKey.BK_IMAGE_URI, uri.toString());
                }
            }
            //persiapan Ganti halaman
            bundle.putInt(IBundleKey.BK_STRATEGY_GET_IMAGE, requestCode);
            changeActivity(bundle);
        }
    }

    /**
     * Method ini digunakan untuk penggantian Halaman
     * @param bundle
     */
    public void changeActivity(Bundle bundle) {
        Intent intent = new Intent(this, ProcessActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * method ini digunakan untuk Mengambil gambar dari Kamera
     */
    private void takeAPicFromCamera() {
        //http://agusharyanto.net/wordpress/?p=537
        // Set intent untuk Pengaktifan Kamera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Set Tempat kita Menaruh File
        File file= new File(Environment.getExternalStorageDirectory(),
                "CampOCR-" + String.valueOf(System.currentTimeMillis()) + ".jpg");

        try {
            file.createNewFile();
            mImageCaptureUri = Uri.fromFile(file);
            // set pada intent bahwa kita akan menempatkan gambar pada alamat yang kita beri mImageCaptureUri
//            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}