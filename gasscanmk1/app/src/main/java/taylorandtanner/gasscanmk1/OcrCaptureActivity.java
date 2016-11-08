/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package taylorandtanner.gasscanmk1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import taylorandtanner.gasscanmk1.ui.camera.CameraSource;
import taylorandtanner.gasscanmk1.ui.camera.CameraSourcePreview;
import taylorandtanner.gasscanmk1.ui.camera.GraphicOverlay;

import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

/**
 * Activity for the Ocr Detecting app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public final class OcrCaptureActivity extends AppCompatActivity{
    private static final String TAG = "OcrCaptureActivity";
    public  final static String SER_KEY = "taylorandtanner.gasscanmk1.ser";
    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    // A TextToSpeech engine for speaking a String value.
    private TextToSpeech tts;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ocr_capture);

        Button next = (Button) findViewById(R.id.button3);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
        TextView tv1 = (TextView) findViewById(R.id.forStation);
        TextView tv1a = (TextView) findViewById(R.id.station);
        TextView tv2 = (TextView) findViewById(R.id.forPrice);
        TextView tv2a = (TextView) findViewById(R.id.totalPrice);
        TextView tv3 = (TextView) findViewById(R.id.forGallons);
        TextView tv3a = (TextView) findViewById(R.id.gallons);
        TextView tv4 = (TextView) findViewById(R.id.forPriceGal);
        TextView tv4a = (TextView) findViewById(R.id.priceGal);
        TextView tv5 = (TextView) findViewById(R.id.forMileage);
        TextView tv5a = (TextView) findViewById(R.id.mileage);

        tv1.setTextColor(Color.RED);
        tv1a.setTextColor(Color.BLACK);
        //Set others black
        tv3.setTextColor(Color.argb(0,0,0,0));
        tv4.setTextColor(Color.argb(0,0,0,0));
        tv5.setTextColor(Color.argb(0,0,0,0));
        tv2.setTextColor(Color.argb(0,0,0,0));
        tv3a.setTextColor(Color.argb(0,0,0,0));
        tv4a.setTextColor(Color.argb(0,0,0,0));
        tv5a.setTextColor(Color.argb(0,0,0,0));
        tv2a.setTextColor(Color.argb(0,0,0,0));

        final Button acceptOCRButton = (Button)findViewById(R.id.acceptOCRButton);
        acceptOCRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICKED!!1");

                String outputGal = ((TextView)findViewById(R.id.forGallons)).getText().toString();
                String outputPriceGal = ((TextView)findViewById(R.id.forPriceGal)).getText().toString();
                String outputMileage = ((TextView)findViewById(R.id.forMileage)).getText().toString();
                String outputPrice = ((TextView)findViewById(R.id.forPrice)).getText().toString();
                String outputStation = ((TextView)findViewById(R.id.forStation)).getText().toString();
                ReceiptEntry outputReceipt = new ReceiptEntry(outputPrice, outputGal, outputPriceGal,
                                                                outputMileage, "unassigned", outputStation);

                SerializeMethod(outputReceipt);
               // Intent activityChangeIntent = new Intent(OcrCaptureActivity.this, ReceiptForm.class);
               // OcrCaptureActivity.this.startActivity(activityChangeIntent);

            }
        });

        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

                TextView t1 = (TextView) findViewById(R.id.forStation);
                TextView t1a = (TextView) findViewById(R.id.station);
                TextView t2 = (TextView) findViewById(R.id.forPrice);
                TextView t2a = (TextView) findViewById(R.id.totalPrice);
                TextView t3 = (TextView) findViewById(R.id.forGallons);
                TextView t3a = (TextView) findViewById(R.id.gallons);
                TextView t4 = (TextView) findViewById(R.id.forPriceGal);
                TextView t4a = (TextView) findViewById(R.id.priceGal);
                TextView t5 = (TextView) findViewById(R.id.forMileage);
                TextView t5a = (TextView) findViewById(R.id.mileage);


                if(seekBar.getProgress() >= 0 && seekBar.getProgress() < 20){
                    if(t1.getText().toString().equals("00"))
                        t1.setTextColor(Color.RED);
                    else
                        t1.setTextColor(Color.GREEN);
                    t1a.setTextColor(Color.BLACK);
                    //Set others black
                    t3.setTextColor(Color.argb(0,0,0,0));
                    t4.setTextColor(Color.argb(0,0,0,0));
                    t5.setTextColor(Color.argb(0,0,0,0));
                    t2.setTextColor(Color.argb(0,0,0,0));
                    t3a.setTextColor(Color.argb(0,0,0,0));
                    t4a.setTextColor(Color.argb(0,0,0,0));
                    t5a.setTextColor(Color.argb(0,0,0,0));
                    t2a.setTextColor(Color.argb(0,0,0,0));
                }
                if(seekBar.getProgress() >= 20 && seekBar.getProgress() < 40){
                    if(t2.getText().toString().equals("00"))
                        t2.setTextColor(Color.RED);
                    else
                        t2.setTextColor(Color.GREEN);
                    t2a.setTextColor(Color.BLACK);
                    //Set others black
                    t3.setTextColor(Color.argb(0,0,0,0));
                    t4.setTextColor(Color.argb(0,0,0,0));
                    t5.setTextColor(Color.argb(0,0,0,0));
                    t1.setTextColor(Color.argb(0,0,0,0));
                    t3a.setTextColor(Color.argb(0,0,0,0));
                    t4a.setTextColor(Color.argb(0,0,0,0));
                    t5a.setTextColor(Color.argb(0,0,0,0));
                    t1a.setTextColor(Color.argb(0,0,0,0));
                }
                else if(seekBar.getProgress() >= 40 && seekBar.getProgress() < 60){
                    if(t3.getText().toString().equals("00"))
                        t3.setTextColor(Color.RED);
                    else
                        t3.setTextColor(Color.GREEN);
                    t3a.setTextColor(Color.BLACK);
                    //Set others black
                    t2.setTextColor(Color.argb(0,0,0,0));
                    t4.setTextColor(Color.argb(0,0,0,0));
                    t5.setTextColor(Color.argb(0,0,0,0));
                    t1.setTextColor(Color.argb(0,0,0,0));
                    t2a.setTextColor(Color.argb(0,0,0,0));
                    t4a.setTextColor(Color.argb(0,0,0,0));
                    t5a.setTextColor(Color.argb(0,0,0,0));
                    t1a.setTextColor(Color.argb(0,0,0,0));
                }
                else if(seekBar.getProgress() >= 60 && seekBar.getProgress() < 80){
                    if(t4.getText().toString().equals("00"))
                        t4.setTextColor(Color.RED);
                    else
                        t4.setTextColor(Color.GREEN);
                    t4a.setTextColor(Color.BLACK);
                    //Set others black
                    t2.setTextColor(Color.argb(0,0,0,0));
                    t3.setTextColor(Color.argb(0,0,0,0));
                    t5.setTextColor(Color.argb(0,0,0,0));
                    t1.setTextColor(Color.argb(0,0,0,0));
                    t2a.setTextColor(Color.argb(0,0,0,0));
                    t3a.setTextColor(Color.argb(0,0,0,0));
                    t5a.setTextColor(Color.argb(0,0,0,0));
                    t1a.setTextColor(Color.argb(0,0,0,0));
                }
                else if(seekBar.getProgress() >= 80 && seekBar.getProgress() <= 100){
                    if(t5.getText().toString().equals("00"))
                        t5.setTextColor(Color.RED);
                    else
                        t5.setTextColor(Color.GREEN);
                    t5a.setTextColor(Color.BLACK);
                    //Set others black
                    t2.setTextColor(Color.argb(0,0,0,0));
                    t4.setTextColor(Color.argb(0,0,0,0));
                    t3.setTextColor(Color.argb(0,0,0,0));
                    t1.setTextColor(Color.argb(0,0,0,0));
                    t2a.setTextColor(Color.argb(0,0,0,0));
                    t4a.setTextColor(Color.argb(0,0,0,0));
                    t3a.setTextColor(Color.argb(0,0,0,0));
                    t1a.setTextColor(Color.argb(0,0,0,0));
                }


            }
        });


        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);

        // Set good defaults for capturing text.
        boolean autoFocus = true;
        boolean useFlash = false;

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        /*Snackbar.make(mGraphicOverlay, "Tap the text to place into the desired field. \n Select" +
                " 'confirm' when you are happy with the selection.",
                Snackbar.LENGTH_LONG)
                .show();*/

        // Set up the Text To Speech engine.
       /* TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Log.d("OnInitListener", "Text to speech engine started successfully.");
                            tts.setLanguage(Locale.US);
                        } else {
                            Log.d("OnInitListener", "Error starting the text to speech engine.");
                        }
                    }
                };
        tts = new TextToSpeech(this.getApplicationContext(), listener);*/
    }

    public void SerializeMethod(ReceiptEntry ocrReceipt){
        Intent mIntent = new Intent(this, ReceiptForm.class);
        Bundle mBundle = new Bundle();
        System.out.println("TEST VALUE : " + ocrReceipt.getGallons());
        mBundle.putSerializable(SER_KEY, ocrReceipt);
        mIntent.putExtras(mBundle);

        startActivity(mIntent);
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A text recognizer is created to find text.  An associated multi-processor instance
        // is set to receive the text recognition results, track the text, and maintain
        // graphics for each text block on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each text block.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(0.5f)
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }



        /**
         * onTap is called to speak the tapped TextBlock, if any, out loud.
         *
         * @param rawX - the raw position of the tap
         * @param rawY - the raw position of the tap.
         * @return true if the tap was on a TextBlock
         */
        private boolean onTap(float rawX, float rawY) {
            OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);

            TextBlock text = null;


            if (graphic != null) {
                text = graphic.getTextBlock();
                if (text != null && text.getValue() != null) {
                    // Log.d(TAG, "text data is being spoken! " + text.getValue());
                    //Speak the string.

                  //  MainActivity obj = new MainActivity();
                  //  TextView textView = obj.getTextView();
                    int comparison = Color.RED;
                    String storage = text.getValue();

                    TextView t1 = (TextView) findViewById(R.id.forStation);
                    TextView t2 = (TextView) findViewById(R.id.forPrice);
                    TextView t3 = (TextView) findViewById(R.id.forGallons);
                    TextView t4 = (TextView) findViewById(R.id.forPriceGal);
                    TextView t5 = (TextView) findViewById(R.id.forMileage);

                    if(t1.getCurrentTextColor() == comparison){
                        t1.setText(storage);
                    }
                    else if(t2.getCurrentTextColor() == comparison){
                        t2.setText(storage);
                    }
                    else if(t3.getCurrentTextColor() == comparison){
                        t3.setText(storage);
                    }
                    else if(t4.getCurrentTextColor() == comparison){
                        t4.setText(storage);
                    }
                    else if(t5.getCurrentTextColor() == comparison){
                        t5.setText(storage);
                    }
                   // storage = storage.replace("[^\\d.]", "");
                   // TextView t1 = (TextView) findViewById(R.id.forStorage);
                   // t1.setText(storage);

                    /*String separated [] = storage.split("\\r?\\n");

                    TextView t2 = (TextView) findViewById(R.id.forMPG);
                    t2.setText(separated[0]);

                    TextView t3 = (TextView) findViewById(R.id.forNet);
                    t3.setText(separated[1]);

                    TextView t4 = (TextView) findViewById(R.id.forQTY);
                    t4.setText(separated[2]);

                    TextView t5 = (TextView) findViewById(R.id.Total);
                    t5.setText(separated[3]);*/

                    final ReceiptEntry ocrEntry = new ReceiptEntry("1","2","3","unassigned","4");

                    //


                    /*mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //     Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                            switch (position) {
                                case 0:
                                    Intent myIntent = new Intent(view.getContext(), SignInActivity.class);
                                    startActivityForResult(myIntent, 0);
                                    break;
                                case 1:
                                    myIntent = new Intent(view.getContext(), Settings.class);
                                    startActivityForResult(myIntent, 0);
                                    break;
                                case 2:
                                    myIntent = new Intent(view.getContext(), ViewLogs.class);
                                    startActivityForResult(myIntent, 0);
                                    break;
                                case 3:
                                    createBlankSlate();
                                    break;
                                case 4:
                                    myIntent = new Intent(view.getContext(), ReceiptForm.class);
                                    startActivityForResult(myIntent, 0);
                                    break;
                                default:
                                    return;
                            }*/
                    //trying to push string from one activity to another
                 //   Intent i = new Intent(this, MainActivity.class);
                 //   Bundle bundle = new Bundle();
               //     String check = "false";
                //    bundle.putString("one",storage);
                  //  bundle.putBoolean("Check",false);
                 //   i.putExtras(bundle);

                } else {
                    Log.d(TAG, "text data is null");
                }
            } else {
                Log.d(TAG, "no text detected");
            }
            return text != null;
        }







    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (mCameraSource != null) {
                mCameraSource.doZoom(detector.getScaleFactor());
            }
        }
    }
}
