package id.co.manocr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import id.co.manocr.adapter.CropOption;
import id.co.manocr.adapter.CropOptionAdapter;
import id.co.manocr.util.ClassUtil;
import id.co.manocr.util.IConstants;
import id.co.manocr.util.ImageProcessingOCR;

/**
 * Kelas ProcessActivity ini digunakan untuk Menampilkan
 * Image yang diambil dari galeri ataupun kamera
 */
public class ProcessActivity extends AppCompatActivity {
    private static final int CROP_FROM_CAMERA = 2;
    /**
     * Deklarasi  Variable Component
     */
    ImageView imageView;
    Button btnProcess,btnCrop,btnRotate;
    Uri mUriImage;
    Uri duplicateUri;
    String pathImage;

    int rotation=0;
    int widthImageView,heightImageView;
    float contrast=1,brightness=1;

    private Bitmap bitmap;
    private ProgressDialog progressDialog;
    int getImageFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contrast=1f;
        brightness=1f;
        super.onCreate(savedInstanceState);
        /**
         * Menggunakan Layout activity_process.xml
         * dan menggunakan component yang di dalamnya
         */
        setContentView(R.layout.activity_process);
        imageView=(ImageView)findViewById(R.id.imageView);
        btnProcess=(Button)findViewById(R.id.btnProcess);
        btnCrop=(Button)findViewById(R.id.btnCrop);
        btnRotate=(Button)findViewById(R.id.btnRotate);
        /**
         * Berbeda dengan di Class MainActivity
         * Untuk Handling Aksi Click pada Layar
         * Tapi Kegunaannya sama.
         *
         */
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCrop();
            }
        });
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRotate();
            }
        });
        btnProcess.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new ProcessingOCR().execute();
            }
        });
        /*
            Parameter yang dikirim dari Activity Sebelumnya
         */
        getImageFrom= ClassUtil.getAttibuteBundleInteger(this, IConstants.IBundleKey.BK_STRATEGY_GET_IMAGE);
        mUriImage=Uri.parse(ClassUtil.getAttibuteBundle(this, IConstants.IBundleKey.BK_IMAGE_URI));
        Log.d("ULD",ClassUtil.getAttibuteBundle(this, IConstants.IBundleKey.BK_IMAGE_URI));
        //Reset Foto
        doReset();
    }

    /**
     * Method ini digunakan untuk menampilkan gambar dari Uri
     * @param
     */
    public void showImageFromUri(String pathImage){
        /**
         * Apa Itu URI
         * --> https://buggzilla.wordpress.com/2012/11/10/perbedaan-uri-dan-url/
         */

        if (pathImage != null) { //Check Uri null atau tidak
            InputStream is = null;
            try {
                Bitmap myBitmap = BitmapFactory.decodeFile(pathImage);
                imageView.setImageBitmap(myBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * Method ini digunakan untuk Cropping Image
     */
    private void doCrop() {

        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        /**
         * Set Intent CROP
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        /**
         * Check APakah Handphone Mempunyai Fasilitas Cropping
         */
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {
            //SEt Data untuk dilakukan Cropping
            intent.setData(duplicateUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i 		= new Intent(intent);
                ResolveInfo res	= list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent= new Intent(intent);

                    co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int item ) {
                        startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel( DialogInterface dialog ) {

                        if (mUriImage != null ) {
                            getContentResolver().delete(mUriImage, null, null );
                            mUriImage = null;
                        }
                    }
                } );

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

    /**
     * Kegunaaan MEthod ini telah dijelaskan pada MainActivity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {


            case CROP_FROM_CAMERA:
                // Set Image Dari Hasil Cropping Gambar
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    saveBitMapOnDisk(photo);// Save Hasil Cropping Pada Disk
                    imageView.setImageBitmap(photo);//Set Image Pada Layar
                }

                break;

        }
    }

    /**
     * Method Ini digunakan Untuk Melakukan Rotasi Pada Gambar
     */
    public void doRotate(){
        try {
            Matrix matrix;
            InputStream is = getContentResolver().openInputStream(duplicateUri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            //Create object of new Matrix.
            matrix = new Matrix();

            //Melakukan Rotasi 45 derajat sekali Click
            matrix.postRotate(getRotation());

            //Create bitmap with new values.
            Bitmap bMapRotate = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            saveBitMapOnDisk(bMapRotate);// Save Gambar Hasil Rotasi Pada Disk
            imageView.setImageBitmap(bMapRotate);//Set Image Rotasi pada Layar
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method ini digunakan untuk Menyimpan Gambar Kembali pada disk
     * @param p_Bitmap
     */
    public void saveBitMapOnDisk(Bitmap p_Bitmap){
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        /**
         * Jika Gambar Telah ada pada Disk
         * Maka Harus dihapus terlebih dahulu sebelum di create ulang
         */
        File f = new File(duplicateUri.getPath());
        if (f.exists()){
            f.delete();
            Log.d("DELETE","Suksesss");
        }
        /**
         * Create Ulang Gambar
         */
        File file = new File(duplicateUri.getPath());
        try {
            outStream = new FileOutputStream(file);
            p_Bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * MEthod ini akan memberikan derajat Rotasi
     * @return
     */
    public int getRotation(){
        if(rotation==360){
            rotation=0;
        }
        rotation+=90;
        return rotation;
    }

    /**
     * Private Class Untuk menampilkan Process Dialog
     * dan Memproses OCR di Background Process
     */
    private class ProcessingOCR extends AsyncTask<Void,Integer,String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog = new ProgressDialog(ProcessActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Processing...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        }
        /*
            Process untuk melakukan OCR dilakukan di Background
            agar aplikasi tidak kelihatan Hang
         */
        @Override
        protected String doInBackground(Void... params) {
            ImageProcessingOCR ocr=new ImageProcessingOCR(getCacheDir().getPath());
            String Hasil=ocr.getOCRResult(bitmap);
            ocr.onDestroy();
            Log.d("LogDD",""+Hasil);
            return Hasil;
        }

        /**
         * Method inti adalah method
         * akan akan di jalankan ketika process dialog telah selesai
         * @param result
         */
        @Override
        protected void onPostExecute(String result){
            progressDialog.dismiss();
            if(null!=result && !result.trim().equals("")) {

                Bundle bundle = new Bundle();
                //persiapan Ganti halaman
                bundle.putString(IConstants.IBundleKey.BK_RESULT_OCR, result);
                Intent intent = new Intent(ProcessActivity.this, ResultOCRActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }else {
                new AlertDialog.Builder(ProcessActivity.this)
                        .setTitle("ManOCR")
                        .setMessage("Do not get the characters from the image!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })

                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }

    /**
     * Method untuk create Menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionmode, menu);
        return true;
    }
    //jika Menu di click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.mnProcesOCR:new ProcessingOCR().execute();break;
            case R.id.mnRotate:doRotate();break;
            case R.id.mnCrop:doCrop();break;
            case R.id.mnAddBrightness:{
                /**
                 * Jika Menu Add BrightNess maka akan menambah brighness point jadi 2
                 */
                                        brightness=brightness+0.5f;
                                        doManipulateImage();
                                       }break;
            case R.id.mnMinBrightness:{
                /**
                 * Jika Menu Min BrightNess maka akan menambah brighness point jadi -2
                 */
                                        brightness=brightness-0.5f;
                                        doManipulateImage();
                                       }break;
            case R.id.mnAddContras:{
                /**
                 * Jika Menu add contrass maka akan menambah contras point jadi 0.5
                 */
                                    contrast=contrast+0.5f;
                                    doManipulateImage();
                                    }break;
            case R.id.mnMinCotras:{
                /**
                 * Jika Menu add contrass maka akan menambah contras point jadi 0.5
                 */
                                    contrast=contrast-0.5f;
                                    doManipulateImage();
            }break;
                    case R.id.mnReset:doReset();break;//reset
            default:{}break;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method ini digunakan untuk menambahkan
     * contrass / brighness dari gambar
     *
     */
    public void doManipulateImage(){
        try {
            contrast=(contrast<0 ? 0:contrast);
            contrast=(contrast>10 ? 10:contrast);
            brightness=(brightness<-255 ? -255: brightness);
            brightness=(brightness>255 ? 255: brightness);

            InputStream is = getContentResolver().openInputStream(duplicateUri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            Bitmap manipulate=contrastAndBrightnessControler(bitmap,  contrast,  brightness);
            saveBitMapOnDisk(manipulate);
            imageView.setImageBitmap(manipulate);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param bitmap input bitmap
     * @param contrast 0..10 1 is default
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    public static Bitmap contrastAndBrightnessControler(Bitmap bitmap, float contrast, float brightness)
    {
        ColorMatrix cmatrix = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });
        Bitmap ret =Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cmatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return ret;
    }

    /**
     * Method Reset
     */
    public void doReset(){
        //set 0
        contrast=1;
        brightness=0;
        rotation=0;

        File f =null;
        // jika gambar dari Camera
        if(getImageFrom==IConstants.REQUEST_TAKE_PHOTO){
            String[] fileNameFromUri=mUriImage.getPath().split("/");
           f=new File(Environment.getExternalStorageDirectory()+"/"+fileNameFromUri[fileNameFromUri.length-1]);
        }else{
            f=new File(getRealPathFromURI(mUriImage));
        }
        //Copy file asli dan di letakan di folder tessImage
        // agar menjaga original file asli
        Log.d("Test ",  f.getAbsolutePath());
        String[] fileName=f.getAbsolutePath().split("/");
        Log.d("Test ",fileName[fileName.length-1]);
        File file=new File(Environment.getExternalStorageDirectory()+"/tessImage/"+fileName[fileName.length-1]);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
            copyFile(f, file);
            duplicateUri=Uri.fromFile(file);

        } catch(Exception e) {
            e.printStackTrace();
        }
        pathImage=file.getAbsolutePath();
        showImageFromUri(pathImage);
    }
    // Method yang digunakan untuk copy file
    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }


    }
    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
