package ir.radical_app.radical.activities;

import androidx.appcompat.app.AppCompatActivity;
import ir.radical_app.radical.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

public class QrcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler  {

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_qrcode);

        readBarcode();

    }

    private void readBarcode() {
        mScannerView = new ZXingScannerView(this);
        mScannerView.setAutoFocus(true);
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(list);
        setContentView(mScannerView);
        snackBar(mScannerView);
        mScannerView.setResultHandler(QrcodeActivity.this);
        mScannerView.startCamera();
    }

    private void snackBar(View v){
        Snackbar.make(v,getString(R.string.txt_qr),Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void handleResult(Result rawResult) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("qrCode",rawResult.getText());
            setResult(Activity.RESULT_OK,returnIntent);
            QrcodeActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED,returnIntent);
            QrcodeActivity.this.finish();

    }
}
