package com.example.EmployeeDirectory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AddEmployee<DatabaseHelper> extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    DbHelper myDb;
    EditText name_txt, age_txt;
    Spinner gender_txt;
    ImageView imageView;
    Bitmap imagebmp;
    String image;
    String Gender;
    private static final int SELECT_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        myDb = new DbHelper(this);
        name_txt = (EditText) findViewById(R.id.name);
        age_txt = (EditText) findViewById(R.id.age);
        gender_txt = (Spinner) findViewById(R.id.gender);
        imagebmp = null;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_txt.setAdapter(adapter);
        Gender ="Male";
        gender_txt.setOnItemSelectedListener(this);
    }

    public void AddData(View v) {
        if(name_txt.getText().toString().isEmpty())
        {
            Toast.makeText(AddEmployee.this, "Name is required", Toast.LENGTH_LONG).show();
        }
        else if(age_txt.getText().toString().isEmpty())
        {
            Toast.makeText(AddEmployee.this, "Age is required", Toast.LENGTH_LONG).show();
        }
        else if( imagebmp==null)
        {
            Toast.makeText(AddEmployee.this, "Select an image", Toast.LENGTH_LONG).show();
        }
        else {
            int id=myDb.getSize();
            image = saveToInternalStorage(imagebmp,id);
            Toast.makeText(AddEmployee.this, "Here", Toast.LENGTH_LONG).show();
            boolean isInserted = myDb.insertData(name_txt.getText().toString(),
                    Integer.parseInt(age_txt.getText().toString()),
                    Gender , image );
            if (isInserted == true) {
                Toast.makeText(AddEmployee.this, "Data Inserted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddEmployee.this,MainActivity.class);
                startActivity(intent);
            }
            else
                Toast.makeText(AddEmployee.this, "Data not Inserted", Toast.LENGTH_LONG).show();
        }



    }

    ////////////////////     select image button ON CLICK Inten      //////////////////////////////////////////
    public void selectImage(View v) {
//        private static final int REGUEST_CODE = 100;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

    }

     ////////////////////////////////////////////////         Activity to upload a image      /////////////////////
    /*
             URI -------> bitmap -------------> byte[]
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            switch (requestCode) {
                case SELECT_PHOTO:
                    if (resultCode == RESULT_OK) {
                        Uri IMAGE_URI = imageReturnedIntent.getData();
                        Bitmap bitmap =null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), IMAGE_URI);
                        } catch (Exception e) {
                           e.printStackTrace();
                        }
                        if(bitmap==null) Toast.makeText(AddEmployee.this, "No Image Found",Toast.LENGTH_LONG).show();
                        imagebmp =bitmap;

                    }
            }
        }

    }





     ////////////////////////////////// store image in internal  DB ///////////////////////////////////
     private String saveToInternalStorage(Bitmap bitmapImage,int id){
         ContextWrapper cw = new ContextWrapper(getApplicationContext());
         // path to /data/data/yourapp/app_data/imageDir
         File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
         // Create imageDir
         File mypath=new File(directory,"image"+id+".jpg");

         FileOutputStream fos = null;
         try {
             fos = new FileOutputStream(mypath);
             // Use the compress method on the BitMap object to write image to the OutputStream
             bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             try {
                 fos.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
         return directory.getAbsolutePath();
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
                    Toast.makeText(AddEmployee.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Gender = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }





}
