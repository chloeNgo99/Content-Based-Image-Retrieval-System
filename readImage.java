/* Project 2
* Group 2: Doan Thi Cuc, Chloe Ngo
*/

import java.awt.Color;
//import java.lang.Object.*;
//import javax.swing.*;
import java.io.*;
//import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;
import java.awt.Graphics;

public class readImage {
  int imageCount = 0;
  double intensityBins[] = new double[26];
  double intensityMatrix[][] = new double[100][26];
  double colorCodeBins[] = new double[64];
  double colorCodeMatrix[][] = new double[100][64];

  /*
   * Each image is retrieved from the file. The height and width are found for the
   * image and the getIntensity and getColorCode methods are called.
   * converts an ImageIcon to a BufferedImage.
   * The class takes an ImageIcon as input, creates a BufferedImage with
   * the same width and height as the ImageIcon, creates a graphics object
   * from the BufferedImage, and paints the Icon on the graphics object.
   * The Graphics object is then disposed, and the resulting BufferedImage
   * is returned as output.
   */


  public readImage() {

    while (imageCount < 100) {
      ImageIcon icon;
      icon = new ImageIcon(getClass().getResource("images/" + (imageCount + 1) + ".jpg"));
      
      //convert ImageIcon to bufferedImage
      BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
      Graphics g = image.createGraphics();
      g.drawImage(icon.getImage(), 0, 0, null);
      g.dispose();

      if (image != null) {
        int width = image.getWidth();
        int height = image.getHeight();

        getIntensity(image, height, width);
        getColorCode(image, height, width);

        // place the new feature value of intensity matrix
        for (int i = 0; i < 26; i++) {
          intensityMatrix[imageCount][i] = intensityBins[i];
          intensityBins[i] = 0;
        }
        // place the new feature value of colorCode matrix
        for (int i = 0; i < 64; i++) {
          colorCodeMatrix[imageCount][i] = colorCodeBins[i];
          colorCodeBins[i] = 0;
        }
      }
      imageCount++;
    }

    writeIntensity();
    writeColorCode();

  }
  
  // intensity method
  /*
   * receive the image and its heigh and width.
   * input the size of the image to the first bins
   * then loop through the heigh and width of the image, compute the
   * intensity using the intensity = 0.2999 * red + 0.587 * green + 0.114 * blue
   * formular, and add each value to it correct bin
   */

  public void getIntensity(BufferedImage image, int height, int width) {
    // first index store the size of the image
    intensityBins[0] = height * width;

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        Color color = new Color(image.getRGB(i, j));
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        // calculate the intensity and the bin number
        // store it into the intensityBins matrix
        double intensity = 0.2999 * red + 0.587 * green + 0.114 * blue;
        int bin = (int) intensity / 10 + 1;
        if (bin > 25) {
          bin = 25;
        }
        intensityBins[bin]++;
      }
    }

  }

  // color code method
  /*
   * receive the image and its heigh and width.
   * Loop through the heigh and width of the image, get the first 2 digits of each
   * rbg binary number,
   * then convert the binary number back to integer and place it in the correct
   * bin
   */
  public void getColorCode(BufferedImage image, int height, int width) {
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        Color color = new Color(image.getRGB(i, j));
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        // convert into binary
        String redbits = getString(red);
        String greenbits = getString(green);
        String bluebits = getString(blue);
        String binary = redbits.substring(0, 2) +
            greenbits.substring(0, 2) +
            bluebits.substring(0, 2);
        int num = Integer.parseInt(binary, 2);
        colorCodeBins[num]++;
      }
    }
  }

  public String getString(int num) {
    String s = Integer.toBinaryString(num);
    for (int i = 0; i < 8 - s.length(); i++) {
      s = "0" + s;
    }
    return s;
  }

  // This method writes the contents of the colorCode matrix to a file named
  // colorCodes.txt.
  public void writeColorCode() {
    try {
      String path = Paths.get(".").toAbsolutePath().normalize().toString();
      String file_name = path + "/colorCodes.txt";
      FileWriter file = new FileWriter(file_name);
      BufferedWriter line = new BufferedWriter(file);

      for (int i = 0; i < 100; i++) {
        for (int j = 0; j < 64; j++) {
          line.write(String.valueOf(colorCodeMatrix[i][j]) + " ");
        }
        line.newLine();
      }

      line.close();
      file.close();
      System.out.println("colorCodes.txt file written successfully");
    } catch (IOException e) {
      System.out.println("Cannot create colorCodes.txt file" + e.getMessage());
    }
  }

  // This method writes the contents of the intensity matrix to a file called
  // intensity.txt
  public void writeIntensity() {
    try {
      String path = Paths.get(".").toAbsolutePath().normalize().toString();
      String file_name = path + "/intensity.txt";
      FileWriter file = new FileWriter(file_name);
      BufferedWriter line = new BufferedWriter(file);

      for (int i = 0; i < 100; i++) {
        for (int j = 0; j < 26; j++) {
          line.write(String.valueOf(intensityMatrix[i][j]) + " ");
        }
        line.newLine();
      }

      line.close();
      file.close();
      System.out.println("intensity.txt file written successfully");
    } catch (IOException e) {
      System.out.println("Cannot create intensity.txt file" + e.getMessage());
    }
  }

  public static void main(String[] args) {
    new readImage();
  }

}
