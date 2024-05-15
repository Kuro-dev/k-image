# What does this project do?

Short answer: edit images while only relying on RAM, and not writing to the disk.

# What can you do so far?

- [x] Decode and load a simple png file
- [x] Draw Lines
- [x] Draw Rectangles (filled and hollow)
- [x] Draw Circles (filled and hollow)
- [ ] Draw Text

# Current goals:

Implement a way to decode TTF files to extract the glyphs and draw text to the screen using various fonts.

Feel free to help me implement and improve this functionality.
it doesn't have to be perfect or support every functionality, it can be improved iteratively.

# what I need help with

I am struggling with extracting the glyph data as I seem to be skipping or over-reading some bytes somewhere. 
Some glyphs have their coordinates messed up and I would like to reimplement that functionality from scratch. 
no prior experience with TTF files necessary, 
I can explain everything I know and theres extensive documentation available as well.

# How to use

```java
import org.kurodev.kimage.kimage.draw.DrawableImage;

import java.awt.Color;

public static void main(String[] args) {
    DrawableImage img = new DrawableImage(1920, 1080);
    img.fillRect(300, 300, 400, 550, Color.BLACK);
    img.drawRect(350, 350, 300, 450, Color.WHITE);
    img.drawCircle(400, 400, 25, Color.WHITE);

    //to save the image you simply call "encode" and flush the resulting byte array down the desired OutputStream
    Files.write(Path.of("./example.png"), img.encode());
}
```