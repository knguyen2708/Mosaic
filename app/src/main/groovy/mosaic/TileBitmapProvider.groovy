package com.example.knguyen.mosaic

import android.graphics.*
import groovy.transform.CompileStatic

/**
 * Provides an image with a letter in the middle, and uses sleep to simulate network delay (and to make it cool).
 *
 */
@CompileStatic
class TileBitmapProvider {
    static String string = "I fucking ❤ AUSTRALIA! "
    static int index = 0
    static Random rand = new Random()

    /**
     * Provides an bitmap with a letter in the middle.
     * A random delay is added to demonstrate async, and to make it cooler.
     *
     * Can be replaced with some sort of cloud services.
     *
     * @param color The background color of the bitmap
     * @return A bitmap
     */
    static Bitmap get(int color) {
        int width = 30
        int height = 30
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas canvas = new Canvas(bitmap)

        Paint paint = new Paint()

        paint.color = color
        paint.style = Paint.Style.FILL
        canvas.drawRect(0, 0, width - 1, height - 1, paint)

        paint.style = Paint.Style.FILL

        paint.color = Color.WHITE
        paint.antiAlias = true
        paint.textSize = 14
        paint.setTextAlign(Paint.Align.CENTER)

        def c = string[index]
        canvas.drawText(c, width / 2 , height / 2 - (paint.descent() + paint.ascent()) / 2, paint)

        index = (index + 1) % string.length()

        Thread.sleep(250 + rand.nextInt(750)) // Sleep a random period

        System.out.println("returning bitmap ...")
        return bitmap
    }
}