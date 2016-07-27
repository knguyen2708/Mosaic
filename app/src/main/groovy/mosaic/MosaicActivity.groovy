package com.example.knguyen.mosaic

import android.app.*
import android.graphics.*
import android.os.Bundle
import android.view.*
import android.widget.*
import groovy.transform.CompileStatic

@CompileStatic
public class MosaicActivity extends Activity {

    // The mosaic grid size
    private int GridRows = 15
    private int GridColumns = 15

    // Keep track of the tile
    private ArrayList<MosaicTile> mTiles = []

    // View groups
    private ViewGroup mContainer
    private ViewGroup mMosaicContainer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mosaic)

        mContainer = (ViewGroup)findViewById(R.id.container)
        mContainer.post {
            // Make sure that mMosaicContainer is in the middle of the screen
            // by adjusting its width / height.
            // (It's already centered using other layout parameters)

            // This must be done in post() to make sure that mContainer has already been laid out
            def size = Math.min(mContainer.width, mContainer.height)
            mMosaicContainer.layoutParams.width = size - 30
            mMosaicContainer.layoutParams.height = size - 30

            def bitmap = (Bitmap)intent.getParcelableExtra("image")
            buildTiles(bitmap)
            getTileBitmaps()
        }

        mMosaicContainer = (ViewGroup)findViewById(R.id.mosaic_container)
    }

    /// Construct the tiles, where they are, their color, and their corresponding image views
    /// (Each tile is displayed using an image view, and positioned absolutely in a relative layout)
    private void buildTiles(Bitmap bitmap) {
        for (def row = 0; row < GridRows; row++) {
            for (def col = 0; col < GridColumns; col++) {
                // Create the tile
                def tile = new MosaicTile()
                tile.row = row
                tile.column = col

                // Calculate color
                def tileWidth = (double)bitmap.getWidth() / GridRows
                def tileHeight = (double)bitmap.getHeight() / GridColumns

                def x0 = (int)Math.round((double)col * tileWidth)
                def x1 = (int)Math.round(x0 + tileWidth) - 1
                def y0 = (int)Math.round((double)row * tileHeight)
                def y1 = (int)Math.round(y0 + tileHeight) - 1
                tile.color = getAverageColor(bitmap, x0, x1, y0, y1)

                // Create the image view
                def view = tile.view = new ImageView(this)
                view.scaleType = ImageView.ScaleType.FIT_XY
                view.adjustViewBounds = true

                // Layout the image view inside the relative layout
                double dx = mMosaicContainer.layoutParams.width / GridColumns // A tile's width
                double dy = mMosaicContainer.layoutParams.height / GridRows // A tile's height
                def params = new RelativeLayout.LayoutParams((int)dx, (int)dy)
                params.leftMargin = (int)(col * dx)
                params.topMargin = (int)(row * dy)
                view.layoutParams = params

                mMosaicContainer.addView(view)

                //
                mTiles.add(tile)
            }
        }
    }

    /// Returns average of colors of a region of the bitmap
    private int getAverageColor(Bitmap bitmap, int x0, int x1, int y0, int y1) {
        double red = 0, green = 0, blue = 0
        double n = (x1 - x0) * (y1 - y0)

        for (def x = x0; x < x1; x++) {
            for (def y = y0; y < y1; y++) {
                int c = bitmap.getPixel(x, y)
                int r = (c >> 16) & 0xFF
                int g = (c >> 8) & 0xFF
                int b = (c >> 0) & 0xFF
                red += r / n
                green += g / n
                blue += b / n
            }
        }

        return Color.rgb((int)red, (int)green, (int)blue)
    }

    /// Start getting the tiles
    private void getTileBitmaps() {
        // Run up to 10 simultaneous threads
        // (It's up to the executor to decide how many are actually spawn)
        // Whenever a bitmap is returned for a tile, the next unprocessed tile is picked up
        mTiles.take(10).each {
            processTile(it)
        }
    }

    /// Pick up the next unprocessed tile, or stop if they're all done
    private void pickupNextTile() {
        def tile = mTiles.find { it.state == MosaicTile.STATE_NOT_STARTED }
        if (!tile) { return }

        processTile(tile)
    }

    /// Get the bitmap for a tile, and pickup the next one when it's done.
    private void processTile(MosaicTile tile) {
        tile.state = MosaicTile.STATE_PROCESSING

        Async.work {
            return TileBitmapProvider.get(tile.color)

        }.then { Bitmap bitmap ->
            tile.bitmap = bitmap
            tile.view.imageBitmap = bitmap
            tile.state = MosaicTile.STATE_DONE

            pickupNextTile()
        }
    }
}

/**
 * A tile's information
 */
@CompileStatic
public class MosaicTile {
    // Where it is
    int row
    int column

    // The tile's background color
    int color

    // The bitmap for the tile, acquired in `getTileBitmaps`
    Bitmap bitmap

    // The image view that will display the tile
    ImageView view

    // State of the bitmap
    int state = STATE_NOT_STARTED

    static int STATE_NOT_STARTED = 0 // Not started getting bitmap
    static int STATE_PROCESSING = 1 // Getting bitmap
    static int STATE_DONE = 2 // Bitmap acquired
}