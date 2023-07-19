package jp.ac.cm0107.samplecamerax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ImageCapture imageCapture;
    PreviewView previewView;
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Button btnCapture;
    private Button btnClose;
    private String TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        TAG = "MainActivity";



        previewView = findViewById(R.id.previewView);
        btnCapture = findViewById(R.id.btnCapture);
        btnClose = findViewById(R.id.btnClose);
        btnCapture.setOnClickListener(new capture());
        startCamera();
    }


    private void startCamera() {
        cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        // 余力のある人は「ラムダ式」を調べて導入してみよう
        cameraProviderFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // Set up the view finder use case to display camera preview
                Preview preview = new Preview.Builder().build();

                // Choose the camera by requiring a lens facing
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                imageCapture = new ImageCapture.Builder().build();

                // Attach use cases to the camera with the same lifecycle owner
                Camera camera = cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) MainActivity.this),
                        cameraSelector,
                        preview,imageCapture);

                // Connect the preview use case to the previewView
                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider());


            } catch (InterruptedException | ExecutionException e) {
                // Currently no exceptions thrown. cameraProviderFuture.get()
                // shouldn't block since the listener is being called, so no need to
                // handle InterruptedException.
            }
        }, ContextCompat.getMainExecutor(this));

    }

    private class capture implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            File file = new File(getFilesDir(),getNowDate()+".jpg");
            ImageCapture.OutputFileOptions outputFileOptions =
                    new ImageCapture.OutputFileOptions.Builder(file).build();
            imageCapture.takePicture(outputFileOptions, Executors.newSingleThreadExecutor(),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Log.i(TAG,"onImageSave");
                            Log.i(TAG,outputFileResults.getSavedUri().toString());
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException error) {
                            Log.i(TAG,"onError");
                            Log.i(TAG,error.getMessage());
                        }
                    });
        }
    }
    public static String getNowDate() {
        @SuppressLint("SimpleDateFormat") final DateFormat df =
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date  = new Date(System.currentTimeMillis());
        return df.format(date);
    }
}
