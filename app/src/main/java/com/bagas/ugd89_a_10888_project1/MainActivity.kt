package com.bagas.ugd89_a_10888_project1

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    lateinit var sensorStatusTV : TextView
    lateinit var proximitySensor : Sensor
    lateinit var sensorManager : SensorManager

    private var mCamera : Camera? = null
    private var mCameraView : CameraView? = null
    private var currentCameraId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // on below line we are initializing our all variables.
        sensorStatusTV = findViewById(R.id.idTVSensorStatus)
        // on below line we are initializing our sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // on below line we are initializing our proximity sensor variable
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        // on below line we are checking if the proximity sensor is null
        if (proximitySensor == null) {
            // on below line we are displaying a toast if no sensor is available
            Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        } else {

            try {
                // on below line we are registering
                // our sensor with sensor manager
                sensorManager.registerListener(
                    proximitySensorEventListener,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                mCamera = Camera.open();

            }catch (e:Exception) {
                Log.d("Error", "Failed to get Camera" + e.message)
            }
        }

        if (mCamera != null) {
            mCameraView = CameraView(this@MainActivity, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }
        @SuppressLint("MissingInflatedId", "LocalSuppress") val imageClose =
            findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener { view:View? -> System.exit(0) }
    }


    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // method to check accuracy changed in sensor.
        }

        override fun onSensorChanged(event: SensorEvent?) {
            // check if the sensor type is proximity sensor.
            if (event != null) {
                if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                    if (event.values[0] == 0f) {
                        mCamera?.release()
                        // here we are setting our status to our textview..
                        // if sensor event return 0 then object is closed
                        // to sensor else object is away from sensor.
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

                        mCamera = Camera.open(currentCameraId);

                        if (mCamera != null) {

                            mCameraView = CameraView(this@MainActivity, mCamera!!)
                            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
                            camera_view.addView(mCameraView)
                        }
                    } else {
                        mCamera?.release()

                        // on below line we are setting text for text view
                        // as object is away from sensor.
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

                        mCamera = Camera.open(currentCameraId);

                        if (mCamera != null) {

                            mCameraView = CameraView(this@MainActivity, mCamera!!)
                            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
                            camera_view.addView(mCameraView)
                        }
                    }
                }
            }
        }
    }
}