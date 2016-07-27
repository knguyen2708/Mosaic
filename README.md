# Mosaic

Demonstrates some Android technologies.

- Using Groovy in place of Java
- Using AsyncTask for parallelism, along with Groovy closures to reduce boilerplate code, such as

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

- Using relative layout for absolutely positioning its child views.
- Generating bitmaps on-the-fly using Paint/Canvas.