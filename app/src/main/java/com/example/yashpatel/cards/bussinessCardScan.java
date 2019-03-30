package com.example.yashpatel.cards;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.sql.SQLTransactionRollbackException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class bussinessCardScan extends AppCompatActivity {


    EditText mResultEt;
    ImageView mPreviewIv;


    TextView displayText;
    TextView displayEmail;
    TextView displayPhone;
    TextView displayName;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bussiness_card_scan);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("click Image icon to add image");
        Button openContacts = (Button) findViewById(R.id.submitButton);

        mResultEt = findViewById(R.id.resultEt);
        mPreviewIv = findViewById(R.id.imageIv);


        // extracted info
        displayName = (TextView) findViewById(R.id.textView5);
        displayPhone = (TextView) findViewById(R.id.textView4);
        displayEmail = (TextView) findViewById(R.id.textView3);



        // camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // storage permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        // submit button code
        openContacts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                extractName(mResultEt.getText().toString());
                extractEmail(mResultEt.getText().toString());
                extractPhone(mResultEt.getText().toString());


                addToContacts();
            }
        });




    }// on create ends


    // action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate items
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    // handel actionbar item clicks here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.addImage){
            showImageImportDialog();
        }
        if(id == R.id.settings){
            Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {
     // items to display in dialog
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        // set Title
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if( which == 0 ){
                    // camer aoption clicked

                    if(!checkCameraPermission()){

                        requestCameraPermission();
                    }
                    else{
                        // permission allowed take the picture
                        pickCamera();

                    }


                }
                if(which == 1){
                    // gallery action clicked

                    if(!checkStoragePermission()){

                        requeststoragePermission();
                    }
                    else{
                        // permission allowed take the picture
                        pickGallery();

                    }


                }
            }
        });
        dialog.create().show();


    }

    private void pickGallery() {
        // intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        // set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);



    }

    private void pickCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New pic");//tit;e of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");//description
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);


    }

    private void requeststoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
         return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);


    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean resultl = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && resultl;
    }

    // handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
        case CAMERA_REQUEST_CODE:
          if(grantResults.length > 0){
              boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
              boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
              if(cameraAccepted && writeStorageAccepted){
                  pickCamera();
              }
              else {Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();}
          }break;

          case STORAGE_REQUEST_CODE:

              if(grantResults.length > 0){
                  boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                  if( writeStorageAccepted){
                      pickGallery();
                  }
                  else {Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();}
              }break;

    }

    }

    // handle the image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                // got image from gallery now corp it

                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);

            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                // got image from camera now corp it

                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);


            }

        }
        // get cropped image
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if( resultCode == RESULT_OK){

                Uri resultUri = result.getUri();// get image uri
                // set Image to image view
                mPreviewIv.setImageURI(resultUri);

                // get drawable bitmap for text recognization
                BitmapDrawable bitmapDrawable = (BitmapDrawable)mPreviewIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if(!recognizer.isOperational()){
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
                }
                else{
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    // get text from sb untill there is no text
                    for(int i =0; i<items.size();i++){
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");

                    }
                    // set the text to edit text
                    mResultEt.setText(sb.toString());
                }

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // if there is any error show it
               Exception error = result.getError();
               Toast.makeText(this,""+error,Toast.LENGTH_SHORT).show();
            }


        }


    }

    // parse the content
    private void addToContacts(){

        // Creates a new Intent to insert a contact
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        // Sets the MIME type to match the Contacts Provider
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        //Checks if we have the name, email and phone number...
        if(displayName.getText().length() > 0 && ( displayPhone.getText().length() > 0 || displayEmail.getText().length() > 0 )){
            //Adds the name...
            intent.putExtra(ContactsContract.Intents.Insert.NAME, displayName.getText());

            //Adds the email...
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, displayEmail.getText());
            //Adds the email as Work Email
            intent .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);

            //Adds the phone number...
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, displayPhone.getText());
            //Adds the phone number as Work Phone
            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);

            //starting the activity...
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "No information to add to contacts!", Toast.LENGTH_LONG).show();
        }


    }

    public void extractName(String str){
        System.out.println("Getting the Name");
        final String NAME_REGEX = "^([A-Z]([a-z]*|\\.) *){1,2}([A-Z][a-z]+-?)+$";
        Pattern p = Pattern.compile(NAME_REGEX, Pattern.MULTILINE);
        Matcher m =  p.matcher(str);
        if(m.find()){
            System.out.println(m.group());
            displayName.setText(m.group());
        }
    }

    public void extractEmail(String str) {
        System.out.println("Getting the email");
        final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern p = Pattern.compile(EMAIL_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);   // get a matcher object
        if(m.find()){
            System.out.println(m.group());
            displayEmail.setText(m.group());
        }
    }

    public void extractPhone(String str){
        System.out.println("Getting Phone Number");
        final String PHONE_REGEX="(?:^|\\D)(\\d{3})[)\\-. ]*?(\\d{3})[\\-. ]*?(\\d{4})(?:$|\\D)";
        Pattern p = Pattern.compile(PHONE_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);   // get a matcher object
        if(m.find()){
            System.out.println(m.group());
            displayPhone.setText(m.group());
        }
    }













}//class ends
