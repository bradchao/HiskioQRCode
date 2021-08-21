package com.bradchao.hiskioqrcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final String TAG = "bradlog";
    private ZXingScannerView mScannerView;
    private TextView resultText;
    private Button goBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            init();
        }else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                init();
            }else{
                finish();
            }
        }
    }

    private void init(){
        mScannerView = findViewById(R.id.scanner);
        resultText = findViewById(R.id.resultText);
        goBtn = findViewById(R.id.goBtn);
    }

    @Override
    public void onResume() {
        super.onResume();

        goBtn.setEnabled(false);
        resultText.setText("");

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
//        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        String result = rawResult.getText();
        String[] data = result.split(":");
        if (data != null && data.length == 3 && data[0].equals("SMSTO")){
            resultText.setText(result);
            goBtn.setEnabled(true);
        }else{
            mScannerView.resumeCameraPreview(this);
        }

    }

    public void go(View view) {
        String result = resultText.getText().toString();
        String[] data = result.split(":");

        Uri uri = Uri.parse("smsto:" + data[1]);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", data[2]);
        startActivity(intent);
    }
}