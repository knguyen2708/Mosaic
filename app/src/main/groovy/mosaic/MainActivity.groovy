package com.example.knguyen.mosaic

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import groovy.transform.CompileStatic

@CompileStatic
public class MainActivity extends Activity {

    private ImageView mImageView
    private Button mCreateMosaicButton
    private Bitmap mBitmap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mImageView = (ImageView)findViewById(R.id.image_view)

        def getImageButton = (Button)findViewById(R.id.get_image_button)
        getImageButton.onClickListener = {
            // getImage('http://lorempixel.com/200/200/') // Blocks sometimes, for whatever reason
            getImage('https://pixabay.com/static/uploads/photo/2015/10/01/21/39/background-image-967820_960_720.jpg')
            // getImage('http://i.stack.imgur.com/aCJrp.png') // Pretty girl's image
        }

        mCreateMosaicButton = (Button)findViewById(R.id.create_mosaic_button)
        mCreateMosaicButton.enabled = false
        mCreateMosaicButton.onClickListener = {
            // Resize to 200 * 200, don't need hi-res image
            def bitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200, true)

            def intent = new Intent(this, MosaicActivity.class)
            intent.putExtra("image", bitmap)
            startActivity(intent)
        }

    }

    /// Download the image & show it
    private void getImage(String url) {
        def dialog = ProgressDialog.show(this, "", "Downloading...")

        Async.work {
            try {
                System.out.println("Getting bitmap, url = ${url}")
                def input = new URL(url).openStream()
                def bitmap = BitmapFactory.decodeStream(input)
                System.out.println("Got bitmap")
                return bitmap

            } catch (e) {
                e.printStackTrace()
                return null
            }

        }.then { Bitmap bitmap ->
            dialog.dismiss()

            if (!bitmap) {
                new AlertDialog.Builder(this)
                .setTitle("ERROR")
                .setMessage("Could not get image. Make sure you are connected to Internet")
                .setCancelable(true)
                .setNeutralButton("OK", null)
                .show()

                return
            }

            mBitmap = bitmap
            mImageView.setImageBitmap(bitmap)
            mCreateMosaicButton.enabled = true
            mCreateMosaicButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
        }

    }
}