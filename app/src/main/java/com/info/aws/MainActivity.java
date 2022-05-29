package com.info.aws;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    Context context;
    String BUCKET_NAME="maha-bucket";
    String IDENTITY_POOL_ID="us-east-1:6a66580c-8bfb-4b33-8ed4-7cca523be6f8";
    File file ;
    String filePath;
    Button uploadBtn;
    private static final int PICK_IMAGES_CODE=0;
    Uri imageUri;
    ImageView cover;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadBtn=findViewById(R.id.uploadBtn);
        cover=findViewById(R.id.cover_image);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageIntent();
            }
        });

    }
    private void pickImageIntent(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select iamge"),PICK_IMAGES_CODE);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==PICK_IMAGES_CODE){
            if (resultCode== Activity.RESULT_OK){
                imageUri=data.getData();
                filePath= imageUri.getPath();
                cover.setImageURI(imageUri);
                file = new File(filePath);

                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(getApplicationContext(),
                        IDENTITY_POOL_ID, Regions.US_EAST_1);
                AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                final TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
                final TransferObserver observer = transferUtility.upload("maha-bucket",file.getName(), file, CannedAccessControlList.PublicRead);
                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED == state) {
                            // Handle a completed upload.
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        // Handle errors
                    }
                });


            }





        }
    }
}