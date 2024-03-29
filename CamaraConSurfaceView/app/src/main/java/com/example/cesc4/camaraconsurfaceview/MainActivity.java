package com.example.cesc4.camaraconsurfaceview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA=1;
    private String token = "";
    private String tokenanterior = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        initQR();
    }

    public void initQR() {

        // creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // creo la camara
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true)//you should add this feature
                .build();

        // listener de ciclo de vida de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de ANdroid que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    return;
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // preparo el detector de QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
        public void receiveDetections(Detector.Detections<Barcode> detections) {
            final SparseArray<Barcode> barcodes = detections.getDetectedItems();

            if (barcodes.size() > 0) {

                // obtenemos el token
                token = barcodes.valueAt(0).displayValue.toString();

                // verificamos que el token anterior no se igual al actual
                // esto es util para evitar multiples llamadas empleando el mismo token
                if (!token.equals(tokenanterior)) {

                    // guardamos el ultimo token proceado
                    tokenanterior = token;
                    Log.i("token", token);
                    Log.d("HERE","Echar ojo x aqui");

                    if (URLUtil.isValidUrl(token)) {
                        // si es una URL valida abre el navegador
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(token));
                        startActivity(browserIntent);
                    } else {
                        // comparte en otras apps
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, token);
                        shareIntent.setType("text/plain");
                        startActivity(shareIntent);
                    }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(2000);
                                        // limpiamos el token
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }
            }
        });

    }
//    public void onStart(final String node) {
//        Looper.prepare();
//        Toast.makeText(MainActivity.this,"El resultado es: "+ token.toString(), Toast.LENGTH_SHORT).show();
//
//    }


//    private android.hardware.Camera mCamera = null;
//    private CameraView mCameraView = null;
//    public final int PERMISSION_CAMERA = 1;
//    private String token = "";
//    private String tokenanterior = "";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        getQR();
//    }
//public  void  getQR(){
//    BarcodeDetector barcodeDetector =
//            new BarcodeDetector.Builder(this)
//                    .setBarcodeFormats(Barcode.ALL_FORMATS)
//                    .build();
//    try{
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,}, PERMISSION_CAMERA);
//        }else{
//            mCamera = android.hardware.Camera.open();//you can use open(int) to use different cameras
//        }
//
//    } catch (Exception e){
//        Log.d("ERROR", "Failed to get camera: " + e.getMessage());
//    }
//
//    if(mCamera != null) {
//        mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
//        FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
//        camera_view.addView(mCameraView);//add the SurfaceView to the layout
//    }
//
//    //btn to close the application
//    ImageButton imgClose = (ImageButton)findViewById(R.id.imgClose);
//    imgClose.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            System.exit(0);
//        }
//    });
//
//    barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
//        @Override
//        public void release() {
//
//        }
//
//        @Override
//        public void receiveDetections(Detector.Detections<Barcode> detections) {
//            final SparseArray<Barcode> barcodes = detections.getDetectedItems();
//
//            if (barcodes.size() > 0) {
//
//                // obtenemos el token
//                token = barcodes.valueAt(0).displayValue.toString();
//
//                // verificamos que el token anterior no se igual al actual
//                // esto es util para evitar multiples llamadas empleando el mismo token
//                if (!token.equals(tokenanterior)) {
//
//                    // guardamos el ultimo token proceado
//                    tokenanterior = token;
//                    Log.i("token", token);
//                    Log.d("HERE","Echar ojo x aqui");
//
////                    if (URLUtil.isValidUrl(token)) {
////                        // si es una URL valida abre el navegador
////                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(token));
////                        startActivity(browserIntent);
////                    } else {
////                        // comparte en otras apps
////                        Intent shareIntent = new Intent();
////                        shareIntent.setAction(Intent.ACTION_SEND);
////                        shareIntent.putExtra(Intent.EXTRA_TEXT, token);
////                        shareIntent.setType("text/plain");
////                        startActivity(shareIntent);
////                    }
////
//                   new Thread(new Runnable() {
//                        public void run() {
//                            try {
//                                synchronized (this) {
//                                    wait(5000);
//                                    // limpiamos el token
//                                    tokenanterior = "";
//                                }
//                            } catch (InterruptedException e) {
//                                // TODO Auto-generated catch block
//                                Log.e("Error", "Waiting didnt work!!");
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//                    }).start();
//
//
//                }
//            }
//        }
//    });
//
//}

    public void salir(View view){
        finish();
    }

}
