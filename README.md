# Mosaic

Demonstrates some Android technologies.

1. Using Groovy in place of Java
2. Using AsyncTask for parallelism, along with Groovy closures to reduce boilerplate code, such as

```
Async.work {
    // Executed on background thread
    def input = new URL(url).openStream()
    def bitmap = BitmapFactory.decodeStream(input)
    return bitmap
}.then {
    // Executed on UI thread
    mImageView.imageBitmap = bitmap
}
```

3. Using relative layout for absolutely positioning its child views.
4. Generating bitmaps on-the-fly using Paint/Canvas.