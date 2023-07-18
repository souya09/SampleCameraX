package jp.ac.cm0107.samplecamerax;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.os.Bundle;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private ImageCapture imageCapture;
    PreviewView previewView;
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);

        cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        // 余力のある人は「ラムダ式」を調べて導入してみよう
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // Camera provider is now guaranteed to be available
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    // Set up the view finder use case to display camera preview
                    Preview preview = new Preview.Builder().build();

                    // Choose the camera by requiring a lens facing
                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();

                    // Attach use cases to the camera with the same lifecycle owner
                    Camera camera = cameraProvider.bindToLifecycle(
                            ((LifecycleOwner) MainActivity.this),
                            cameraSelector,
                            preview);

                    // Connect the preview use case to the previewView
                    preview.setSurfaceProvider(
                            previewView.getSurfaceProvider());


                } catch (InterruptedException | ExecutionException e) {
                    // Currently no exceptions thrown. cameraProviderFuture.get()
                    // shouldn't block since the listener is being called, so no need to
                    // handle InterruptedException.
                }
            }
        }, ContextCompat.getMainExecutor(this));

    }
}