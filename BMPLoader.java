import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BMPLoader {
    //function to read from windows bmp file
    List<Color> colors = new ArrayList<Color>();

    //function to return byte array of loaded image
    private byte[] readFile(String path){
        byte[] allBytes = null;
        try{
            allBytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return allBytes;
    }

    //function to return array of colours
    public Color[] getColors(){
        return colors.toArray(new Color[colors.size()]);
    }

    //function to load image from file and return integer array for rgb value of each pixel
    public int[][] loadImage(String path){
        byte[] imageData = readFile(path);
        //extracting image header data
        int newWidth = (imageData[18] & 0xFF) | ((imageData[19] << 8) & 0xFF);
        int newHeight = (imageData[22] & 0xFF) | ((imageData[23] << 8) & 0xFF);
        int dataLocation = (imageData[10] & 0xFF) | ((imageData[11] << 8) & 0xFF);
        int bitsPerPixel = imageData[28] & 0xFF;
        int bytesPerRow = (int)Math.ceil(newWidth * bitsPerPixel / 8);
        int paddedBytesPerRow = (int)Math.ceil((float)bytesPerRow/4) * 4; //account for extra zeros after the row has been represented
        int pixelIndex = dataLocation;
        int[][] newImage = new int[newWidth][newHeight];
        colors = new ArrayList<Color>();
        for (int y = newHeight - 1; y >= 0; y--) {
            for (int x = 0; x < newWidth; x++) {
                int blue = 255;
                int green = 255;
                int red = 255;
                int pixelData;
                int paletteIndex;
                //handle separate cases for different bits per pixel
                switch(bitsPerPixel){
                    case 1:
                        //find the byte and bit index corresponding to this pixel
                        int byteIndex = x * bitsPerPixel / 8;
                        int bitShift = (8 - ((x * bitsPerPixel) % 8) - bitsPerPixel) % 8;
                        int pixelByte = imageData[pixelIndex + byteIndex] & 0xFF;
                        pixelData = (pixelByte >> bitShift) & ((1 << bitsPerPixel) - 1);
                        
                        //set colour values to white or black depending on value of pixel data
                        red = green = blue = pixelData * 255;
                        break;
                    case 4:
                        //find byte and bit index corresponding to this pixel
                        byteIndex = pixelIndex + (x / 2); 
                        int bitIndex = (x % 2) * 4;
                        pixelData = (imageData[byteIndex] >> (4 - bitIndex)) & 0x0F;

                        //find stored colours in bmp colour palette extract them
                        paletteIndex = 54 + pixelData * 4;
                        blue = imageData[paletteIndex] & 0xFF;
                        green = imageData[paletteIndex + 1] & 0xFF;
                        red = imageData[paletteIndex + 2] & 0xFF;
                        break;
                    case 8:
                        //find byte index of current pixel
                        byteIndex = pixelIndex + x;
                        pixelData = imageData[byteIndex] & 0xFF;

                        //find stored colours in bmp colour palette extract them
                        paletteIndex = 54 + pixelData * 4;
                        blue = imageData[paletteIndex] & 0xFF;
                        green = imageData[paletteIndex + 1] & 0xFF;
                        red = imageData[paletteIndex + 2] & 0xFF;
                        break;
                    //treat 24 and 32 bits per pixel the same way (ignore alpha values)
                    case 24: case 32:
                        //extract rgb values for each pixel, also need to add support for different bit images
                        blue = imageData[pixelIndex + x * (bitsPerPixel / 8)] & 0xFF;
                        green = imageData[pixelIndex + x * (bitsPerPixel / 8) + 1] & 0xFF;
                        red = imageData[pixelIndex + x * (bitsPerPixel / 8) + 2] & 0xFF;
                        break;
                    default:
                        //throw exception if unsupported bits per pixel case was found
                        throw new IllegalArgumentException("Unsupported bits per pixel: " + bitsPerPixel);
                }
                //add colour to colour palette if it hasn't already been added
                Color col = new Color(red, green, blue);
                if (!colors.contains(col)) {
                    colors.add(col);
                }
                newImage[x][y] = col.getRGB();
            }
            //move to the next row in the pixel data
            pixelIndex += paddedBytesPerRow;
        }
        return newImage;
    }
}
