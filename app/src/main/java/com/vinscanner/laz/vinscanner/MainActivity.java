package com.vinscanner.laz.vinscanner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Class<?> mClss;

    private String TAG = MainActivity.class.getSimpleName();

    private String m_Text;

    private String lastText;
    private String firstChar;

    // URL to get contacts JSON
    private static String url = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevinextended/5yjsa1cn8dfp26585?format=json&modelyear=2013";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

    }

    public void autoScan(View view) {
        new IntentIntegrator(this).initiateScan();
    }

    public void manualScan(View view) {

        Log.e(TAG, "Button Pressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter VIN Number");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();


                Intent i = new Intent(MainActivity.this, Json.class);
                i.putExtra("key", m_Text);
                startActivity(i);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String VINNumber = "";
                lastText = result.getContents();
                firstChar = lastText.substring(0,1);
                if (firstChar.equals("I")) {
                    VINNumber = lastText.substring(1, (lastText.length()));
                    // Get the first character of a VIN and omit it if it matches an I
                }
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + VINNumber, Toast.LENGTH_LONG).show();

                m_Text = VINNumber;

                Intent i = new Intent(MainActivity.this, Json.class);
                i.putExtra("key", m_Text);
                startActivity(i);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}


