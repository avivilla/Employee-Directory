package com.example.EmployeeDirectory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button addbtn;
    private Button showall;
    DbHelper myDB;
    private static final int SELECT_DATA = 42;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new DbHelper(this);
        addbtn = (Button)findViewById(R.id.addemployee);
        showall = (Button) findViewById(R.id.showemployee);
        addEmployee();
        showEmployee();

    }
    public void addEmployee()
    {

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,AddEmployee.class);
                startActivity(intent);
            }
        });
    }
    public void showEmployee()
    {
        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,ShowEmployeeList.class);
                //  intent.putExtra("db_instance" , (Parcelable) myDB);
                startActivity(intent);
            }
        });
    }
     /////////////////////////      exports the databse into a csv file  and shows the file name and path ///////////////////
    public void  exportCSV(View v) throws IOException {
        CsvHelper CSV = new CsvHelper(this);
        String msg = CSV.WriteCSV();
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();

    }
    /////////////////   select a .csv file and imports data from it to the database
    public void importCSV(View v)
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, SELECT_DATA);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataReturnedIntent) {

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            super.onActivityResult(requestCode, resultCode, dataReturnedIntent);
            switch (requestCode) {
                case SELECT_DATA:
                    if (resultCode == RESULT_OK) {
                        Uri DATA_URI = dataReturnedIntent.getData();
                        String dataPath = DATA_URI.getPath().split(":")[1];
                        CsvHelper CSV =new CsvHelper(this);
                        try {
                            int tot = CSV.readCSV(dataPath);
                            Toast.makeText(MainActivity.this,tot + " DATA IMPORTED FROM " + dataPath,Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
            }
        }

    }




    /////////////////////////////////////           API PERMISSSION CODE        ///////////////////////////////////////////////////////////



    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }



    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(MainActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

}
