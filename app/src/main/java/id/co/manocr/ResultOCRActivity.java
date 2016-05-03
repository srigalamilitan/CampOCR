package id.co.manocr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import id.co.manocr.util.ClassUtil;
import id.co.manocr.util.IConstants;

public class ResultOCRActivity extends AppCompatActivity {
    TextView textView;
    Button btnCopy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_ocr);
        textView=(TextView)findViewById(R.id.txtResultViewer);
        textView.setText(ClassUtil.getAttibuteBundle(this, IConstants.IBundleKey.BK_RESULT_OCR));
        btnCopy=(Button)findViewById(R.id.btnCopy);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", textView.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(),"Texts have been copied",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
