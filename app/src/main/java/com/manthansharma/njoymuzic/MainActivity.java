package com.manthansharma.njoymuzic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkRunTimePermission();

    }

    public void checkRunTimePermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //Permission Granted
            }
            else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                Toast.makeText(MainActivity.this, "Go to Settings and Grant the Storage permission to use this feature.", Toast.LENGTH_SHORT).show();
                // User selected the Never Ask Again Option
                this.finishAffinity();
            }
            else{
                Toast.makeText(this, "Please grant Storage permission to use this app", Toast.LENGTH_SHORT).show();
                this.finishAffinity();
            }

    }
}