/* Project 2
* Group 2: Doan Thi Cuc, Chloe Ngo
*/

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.DoubleToIntFunction;

import javax.swing.AbstractAction;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.*;

public class CBIR extends JFrame {

  private JLabel photographLabel = new JLabel(); // container to hold a large
  private JButton[] button; // creates an array of JButtons
  private int[] buttonOrder = new int[101]; // creates an array to keep up with the image order
  private double[] imageSize = new double[100]; // keeps up with the image sizes
  private GridLayout gridLayout1;
  private GridLayout gridLayout2;
  private GridLayout gridLayout3;

  private JPanel panelBottom1;
  private JPanel rightPanel;
  private JPanel leftPanel;
  // private JPanel bigPhotoPanel;
  private Double[][] intensityMatrix = new Double[101][26];
  private Double[][] colorCodeMatrix = new Double[100][64];
  private Double[][] featureMatrix = new Double [100][89];
  private Double[] avg = new Double[89];
  private Double [] std = new Double[89];
  private Double [] weight = new Double[89];
  private Set<Integer> relevant_images = new HashSet<>();
  private Map<Double, LinkedList<Integer>> map;
  int picNo = 0;
  int imageCount = 1; // keeps up with the number of images displayed since the first page.
  int pageNo = 1;

  public static void main(String args[]) {

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        CBIR app = new CBIR();
        app.setVisible(true);
      }
    });
  }

  public CBIR() {
    setTitle("Icon Demo: Please Select an Image");
    panelBottom1 = new JPanel();
    rightPanel = new JPanel();
    leftPanel = new JPanel();
    // main photo grid
    gridLayout1 = new GridLayout(4, 5, 5, 5);
    // left panel button grid
    gridLayout2 = new GridLayout(15, 1, 15, 25);
    // right panel grid
    gridLayout3 = new GridLayout(2, 1, 5, 5);

    // set frame size
    setSize(1000, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // leftPanel.setBackground(Color.GREEN);
    // rightPanel.setBackground(Color.MAGENTA);
    add(leftPanel, BorderLayout.WEST);
    add(rightPanel, BorderLayout.CENTER);
    leftPanel.setPreferredSize(new Dimension(getWidth() / 5, getHeight()));
    rightPanel.setPreferredSize(new Dimension(getWidth() * 4 / 5, getHeight()));

    rightPanel.setLayout(gridLayout3);
    rightPanel.setBackground(Color.decode("#FFFFFF"));
    panelBottom1.setLayout(gridLayout1);
    rightPanel.add(photographLabel);
    rightPanel.add(panelBottom1);

    photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
    photographLabel.setHorizontalTextPosition(JLabel.CENTER);
    photographLabel.setHorizontalAlignment(JLabel.CENTER);
    photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    JButton previousPage = new JButton("Previous Page");
    previousPage.setForeground(Color.decode("#AC422A"));
    JButton nextPage = new JButton("Next Page");
    nextPage.setForeground(Color.decode("#AC422A"));
    JButton intensity = new JButton("Intensity");
    intensity.setForeground(Color.decode("#79B5E0"));
    JButton colorCode = new JButton("Color Code");
    colorCode.setForeground(Color.decode("#E18A8A"));
    JButton intensityAndColor = new JButton("Intensity + Color");
    intensityAndColor.setForeground(Color.decode("#66C698"));
    JCheckBox relavantBtn = new JCheckBox("relevant");
    relavantBtn.setForeground(Color.decode("#BB76BC"));

    leftPanel.setBackground(Color.decode("#FFFAF5"));
    leftPanel.setLayout(gridLayout2);
    leftPanel.add(nextPage);
    leftPanel.add(previousPage);
    leftPanel.add(intensity);
    leftPanel.add(colorCode);
    leftPanel.add(intensityAndColor);
    leftPanel.add(relavantBtn);

    setLocationRelativeTo(null);

    nextPage.addActionListener(new nextPageHandler());
    previousPage.addActionListener(new previousPageHandler());
    intensity.addActionListener(new intensityHandler());
    colorCode.addActionListener(new colorCodeHandler());
    relavantBtn.addActionListener(new relevantHandler());
    intensityAndColor.addActionListener(new intensityAndColorHandler());

    setSize(1100, 750);
    // this centers the frame on the screen
    setLocationRelativeTo(null);

    button = new JButton[101];
    /*
     * This for loop goes through the images in the database and stores them as
     * icons and adds
     * the images to JButtons and then to the JButton array
     * then add another checkbox component into the button image and set the
     * visible to false
     */
    for (int i = 1; i < 101; i++) {
      ImageIcon icon;
      icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));

      if (icon != null) {
        button[i] = new JButton(icon);
        JButton btn = button[i];
        JCheckBox cb = new JCheckBox();
        btn.add(cb);
        cb.setVisible(false);
        // panelBottom1.add(button[i]);
        button[i].addActionListener(new IconButtonHandler(i, icon));
        buttonOrder[i] = i;
      }
    }
    for(int i = 0; i < 89; i++){
      weight[i] =(double) 1/89; // initial weight.
    }
    readIntensityFile();
    readColorCodeFile();
    nomalizedFeature();
    displayFirstPage();
  }

  /*
   * This method opens the intensity text file containing the intensity matrix
   * with the histogram bin values for each image.
   * The contents of the matrix are processed and stored in a two dimensional
   * array called intensityMatrix.
   */
  public void readIntensityFile() {
    // System.out.println("Hello");
    StringTokenizer token;
    Scanner read;
    Double intensityBin;
    String line = "";
    int lineNumber = 0;
    try{
           read =new Scanner(new File ("intensity.txt"));
           while (read.hasNextLine()){
            line = read.nextLine();
            token = new StringTokenizer(line, " ");
            int i = 0;
            
            while (token.hasMoreTokens()){
              intensityBin = Double.parseDouble(token.nextToken());
              intensityMatrix[lineNumber][i] = intensityBin;
              i++;
              
            }
            imageSize[lineNumber]= intensityMatrix[lineNumber][0];
            lineNumber++;
           }
         } catch (FileNotFoundException EE) {
      System.out.println("The file intensity.txt does not exist");
    }

  }

  /*
   * This method opens the color code text file containing the color code matrix
   * with the histogram bin values for each image.
   * The contents of the matrix are processed and stored in a two dimensional
   * array called colorCodeMatrix.
   */
  private void readColorCodeFile() {
    StringTokenizer token;
    Scanner read;
    Double colorCodeBin;
    int lineNumber = 0;
    try{
           read =new Scanner(new File ("colorCodes.txt"));
           while(read.hasNextLine()){
            token = new StringTokenizer(read.nextLine(), " ");
            int i = 0;
            
            while(token.hasMoreTokens()){
              colorCodeBin = Double.parseDouble(token.nextToken());
              colorCodeMatrix[lineNumber][i]= colorCodeBin;
              
              i++;
            }
            lineNumber ++;
           }
         } catch (FileNotFoundException EE) {
      System.out.println("The file intensity.txt does not exist");
    }

  }
  public void readRelevantFile(){
    StringTokenizer token;
    Scanner read;
    try{
      read = new Scanner(new File ("relevant.txt"));
      while(read.hasNextLine()){
        token = new StringTokenizer(read.nextLine());
        while(token.hasMoreTokens()){
          String image_name = token.nextToken();
          if(image_name.length()>0){
            String index = image_name.substring(image_name.indexOf("/")+1, image_name.indexOf("."));
            relevant_images.add(Integer.parseInt(index)-1);
          }
        }
      }
    } catch (FileNotFoundException EE){
      System.out.println("The file relevant.txt does not exist");
    }

  }

  // Put all features together and devide by image size
  public void updateFeature(){
    //Update features with intensity and color code
      
    for(int i = 0; i < 100; i++){
      int pos = 0;
      for (int j = 1; j < 26; j++){
        featureMatrix[i][pos] = intensityMatrix[i][j];
        if(intensityMatrix[i][j].isNaN())
          System.out.println(intensityMatrix[i][j] + " " + i + " " + j);
        pos++;
      }
    }
    
    for(int i = 0; i < 100; i++){
      int pos = 25;
      for(int j = 0; j < 64; j++){
        featureMatrix[i][pos] = colorCodeMatrix[i][j];
        if(colorCodeMatrix[i][j].isNaN())
        System.out.println(colorCodeMatrix[i][j] + " " + i + " " + j);
        pos++;
      }
    }

    //Update feature for each image by deviding by this image size.
    for(int i = 0; i < 100; i++){
      for (int j = 0; j < 89; j++){
        if(featureMatrix[i][j].isNaN()) System.out.println("feature = " + featureMatrix[i][j]);
        featureMatrix[i][j] = featureMatrix[i][j]/imageSize[i];
      }
    }
  }

  public void findAverage() {
    updateFeature();
    for(int feature = 0; feature < 89; feature++){
      double sum = 0;

      for(int line = 0; line < 100; line++){
        if(featureMatrix[line][feature]==null) {
          continue;
        }
        sum = sum + featureMatrix[line][feature];
      } 
      
      avg[feature] = (double) (sum/100);
      
    }
  }

 public void findStdevt(){
  
    findAverage();
    for(int feature = 0; feature < 89; feature++){
      double stdev = 0;
      for(int line = 0; line < 100; line++){
        double num = featureMatrix[line][feature];
        stdev += Math.pow(num - avg[feature],2);
      }
      std[feature] = Math.sqrt(stdev/99);
    }

    //Revised standard deviation in special case
    double min_stdev = std[0]; //the min value of standard deviation
    for(int i = 1; i < 89; i++){
      double num = std[i];
      if(num != 0 && min_stdev > num) min_stdev = num;
    }

    for(int i = 0; i< 89; i++){
      if(std[i]==0 && avg[i]!= 0){
        std[i] = 0.5 * min_stdev;
      }
    }

 }



  public void nomalizedFeature(){
      findStdevt();
        //Nomalized feature
      for(int feature = 0; feature < 89; feature++){
        for(int line = 0; line < 100; line++){
          double num = featureMatrix[line][feature];
          if(std[feature]!= 0)
          featureMatrix[line][feature] = (num -avg[feature])/std[feature];
        }
      }
  }

  /*
   * This method displays the first twenty images in the panelBottom. The for loop
   * starts at number one and gets the image
   * number stored in the buttonOrder array and assigns the value to imageButNo.
   * The button associated with the image is
   * then added to panelBottom1. The for loop continues this process until twenty
   * images are displayed in the panelBottom1
   */
  private void displayFirstPage() {
    int imageButNo = 0;
    panelBottom1.removeAll();
    for (int i = 1; i < 21; i++) {
      // System.out.println(button[i]);
      imageButNo = buttonOrder[i];
      panelBottom1.add(button[imageButNo]);
      imageCount++;
    }
    panelBottom1.revalidate();
    panelBottom1.repaint();

  }

  // This method is to update the order of the images based on the distance
  // between the selected image and other images from most similar to
  // least similar.
  // The map is to store distances as keys and list of images that have
  // the same distance.
  private void updateOrder() {
    Set<Double> S = map.keySet();
    LinkedList<Double> list = new LinkedList<>();
    for (double dist : S) {
      list.add(dist);
    }
    Collections.sort(list);
    buttonOrder[1] = picNo;
    int pos = 2;

    //Relevant images should appear first.
    for (int index: relevant_images){
      if(index + 1 == picNo) continue;
      buttonOrder[pos] = index + 1;
      pos++;

    }
    pos = Math.max(2, relevant_images.size() +1);
    for (int i = 0; i < list.size(); i++) {
      double dist = list.get(i);
      LinkedList<Integer> l = map.get(dist);
      for (int j = 0; j < l.size(); j++) {
        int index = l.get(j);
        if(relevant_images.contains(index)) continue;
        buttonOrder[pos] = index + 1;
        pos++;
      }

    }
  }

  /*
   * This class implements an ActionListener for each iconButton. When an icon
   * button is clicked, the image on the
   * the button is added to the photographLabel and the picNo is set to the image
   * number selected and being displayed.
   */
  private class IconButtonHandler implements ActionListener {
    int pNo = 0;
    ImageIcon iconUsed;

    IconButtonHandler(int i, ImageIcon j) {
      pNo = i;
      iconUsed = j; // sets the icon to the one used in the button
    }

    public void actionPerformed(ActionEvent e) {
      photographLabel.setIcon(iconUsed);
      picNo = pNo;
    }

  }

  /*
   * This class implements an ActionListener for the nextPageButton. The last
   * image number to be displayed is set to the
   * current image count plus 20. If the endImage number equals 101, then the next
   * page button does not display any new
   * images because there are only 100 images to be displayed. The first picture
   * on the next page is the image located in
   * the buttonOrder array at the imageCount
   */
  private class nextPageHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      int imageButNo = 0;
      int endImage = imageCount + 20;
      if (endImage <= 101) {
        panelBottom1.removeAll();
        for (int i = imageCount; i < endImage; i++) {
          imageButNo = buttonOrder[i];
          panelBottom1.add(button[imageButNo]);
          imageCount++;

        }

        panelBottom1.revalidate();
        panelBottom1.repaint();
      }
    }

  }

  /*
   * This class implements an ActionListener for the previousPageButton. The last
   * image number to be displayed is set to the
   * current image count minus 40. If the endImage number is less than 1, then the
   * previous page button does not display any new
   * images because the starting image is 1. The first picture on the next page is
   * the image located in
   * the buttonOrder array at the imageCount
   */
  private class previousPageHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      int imageButNo = 0;
      int startImage = imageCount - 40;
      int endImage = imageCount - 20;
      if (startImage >= 1) {
        panelBottom1.removeAll();
        /*
         * The for loop goes through the buttonOrder array starting with the startImage
         * value
         * and retrieves the image at that place and then adds the button to the
         * panelBottom1.
         */
        for (int i = startImage; i < endImage; i++) {
          imageButNo = buttonOrder[i];
          panelBottom1.add(button[imageButNo]);
          imageCount--;

        }

        panelBottom1.revalidate();
        panelBottom1.repaint();
      }
    }

  }

  /*
   * This class implements an ActionListener when the user selects the
   * intensityHandler button. The image number that the
   * user would like to find similar images for is stored in the variable pic. pic
   * takes the image number associated with
   * the image selected and subtracts one to account for the fact that the
   * intensityMatrix starts with zero and not one.
   * The size of the image is retrieved from the imageSize array. The selected
   * image's intensity bin values are
   * compared to all the other image's intensity bin values and a score is
   * determined for how well the images compare.
   * The images are then arranged from most similar to the least.
   */
  private class intensityHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      double[] distance = new double[101];
      map = new HashMap<Double, LinkedList<Integer>>();
      double d = 0;
      int pic = (picNo - 1);
      System.out.println("Selected image: " + picNo);
      double picSize = imageSize[pic];

      // Update the distances between selected image and other ones.
      for (int i = 0; i < 100; i++) {
        double compare_size = imageSize[i];
        for (int j = 1; j < intensityMatrix[0].length; j++) {
          d += Math.abs(intensityMatrix[pic][j] / picSize
              - intensityMatrix[i][j] / compare_size);
        }
        distance[i] = d;
        d = 0;
      }

      // Store distance - list of images that have the same distance
      for (int i = 0; i < 100; i++) {
        if (i == pic)
          continue;
        d = distance[i];
        if (map.containsKey(d)) {
          map.get(d).add(i);
        } else {
          map.put(d, new LinkedList<Integer>());
          map.get(d).add(i);
        }
      }
      updateOrder(); // Update the order of images based on distances
      System.out.println("New order is :");
      for(int i = 1; i < buttonOrder.length; i++){
        System.out.print(buttonOrder[i] + "    ");
      }
      System.out.println();
      System.out.println("-------------------");
      imageCount = 1; // Reset imageCount
      displayFirstPage(); // Display first page

    }
  }

  /*
   * This class implements an ActionListener when the user selects the colorCode
   * button. The image number that the
   * user would like to find similar images for is stored in the variable pic. pic
   * takes the image number associated with
   * the image selected and subtracts one to account for the fact that the
   * intensityMatrix starts with zero and not one.
   * The size of the image is retrieved from the imageSize array. The selected
   * image's intensity bin values are
   * compared to all the other image's intensity bin values and a score is
   * determined for how well the images compare.
   * The images are then arranged from most similar to the least.
   */
  private class colorCodeHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      double[] distance = new double[101];
      map = new HashMap<Double, LinkedList<Integer>>();
      double d = 0;
      int pic = (picNo - 1);
      System.out.println("Selected image: "+ picNo);
      double picSize = imageSize[pic];

      // Update the distances between selected image and other ones.
      for (int i = 0; i < 100; i++) {
        double compare_size = imageSize[i];
        for (int j = 1; j < colorCodeMatrix[0].length; j++) {
          d += Math.abs(colorCodeMatrix[pic][j] / picSize - colorCodeMatrix[i][j] / compare_size);
        }
        distance[i] = d;
        d = 0;
      }
      // Store distance - list of images that have the same distance
      for (int i = 0; i < 100; i++) {
        if (i == pic)
          continue;
        d = distance[i];
        if (map.containsKey(d)) {
          map.get(d).add(i);
        } else {
          map.put(d, new LinkedList<Integer>());
          map.get(d).add(i);
        }
      }
      updateOrder(); // Update the order of images based on distances
      imageCount = 1; // Reset imageCount
      System.out.println("New order is :");
      for(int i = 1; i < buttonOrder.length; i++){
        System.out.print(buttonOrder[i] + "    ");
      }
      System.out.println();
      System.out.println("-------------------");
      displayFirstPage(); // Display first page
    }
  }

  /*
   * This class implements an ActionListener when the user selects the intensity
   * and color button. The image number that the user would like to find 
   * similar images for is stored in the variable pic. 
   * pic takes the image number associated with the image selected and 
   * subtracts one to account for the fact that the intensityMatrix 
   * starts with zero and not one.
   * Relevant feedback file is read to re-calculate the distance between
   * selected image with other images, then update the order of the images again
   * from most similar ones to least similar ones.
   * The distances are computed based on normalized feature and normalized weight.
   */
  private class intensityAndColorHandler implements ActionListener {
    public void actionPerformed(ActionEvent e){
      double[] distance = new double[101];
      map = new HashMap<Double, LinkedList<Integer>>();
      double d = 0;
      int pic = (picNo - 1);
      System.out.println("Selected image: " + picNo);
      readRelevantFile();
      if(relevant_images.size()!=0) { // after user's feedback
          // Updated standard deviation
          //Find average value and standard deviation for each feature.
        for(int feature = 0; feature < 89; feature++){
          double sum = 0;
          double stdev = 0;
          for(int line: relevant_images){
            sum += featureMatrix[line][feature];
          }
          avg[feature] = sum/100;
          for(int line: relevant_images){
            double num = featureMatrix[line][feature];
            stdev += Math.pow(num - avg[feature],2);
          }
          std[feature] = Math.sqrt(stdev);
          if(std[feature].isNaN()) 
            System.out.println("std "+ feature + "nan");
        }

        //Revised standard deviation in special case

        double min_stdev = std[0]; //the min value of standard deviation
        for(int x = 1; x < 89; x++){
          double num = std[x];
          if(num != 0 && min_stdev > num) min_stdev = num;
        }

        for(int u = 0; u< 89; u++){
          if(std[u]==0 && avg[u]!= 0){
            std[u] = 0.5 * min_stdev;
          }
        }


        //Updated weight
        double sum_weight = 0;
        for(int i = 0; i < 89; i++){
          if(std[i]==0) {
            weight[i] = 0.0;
          }
          else{
            weight[i] = (double) 1/std[i];
          }
          if(weight[i].isNaN()) System.out.println("weight " + i + " NaN");
          sum_weight += weight[i];
        }

        //normalize weight
        for(int i = 0; i< 89; i++){
          weight[i] = weight[i]/sum_weight;
        }
      }
      //Find distance between selected image and other ones.
      for(int i = 0; i < 100; i++){
        for(int j = 0; j < 89; j++){
          d += weight[j] * Math.abs(featureMatrix[pic][j] - featureMatrix[i][j]);
        }
        distance[i] = d;
        d = 0;
      }
      // Store distance - list of images that have the same distance
      for (int i = 0; i < 100; i++) {
        if (i == pic)
          continue;
        d = distance[i];
        if (map.containsKey(d)) {
          map.get(d).add(i);
        } else {
          map.put(d, new LinkedList<Integer>());
          map.get(d).add(i);
        }
      }
      updateOrder(); // Update the order of images based on distances
      imageCount = 1; // Reset imageCount
      System.out.println("New order is :");
      for(int i = 1; i < buttonOrder.length; i++){
        System.out.print(buttonOrder[i] + "    ");
      }
      System.out.println();
      System.out.println("-------------------");
      displayFirstPage(); // Display first page
      relevant_images.clear();

    }
  }

  /*
   * when the checkbox is selected it will loop throught button array and set each
   * button's checkbox component to be visibled and repaint the button. Otherwise
   * set the button's checkbox to invisible.
   */
  private class relevantHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      JCheckBox checkbox = (JCheckBox) e.getSource();
      if (checkbox.isSelected()) {
        // if the checkbox is selected, add a checkbox to each button in the array
        for (int i = 1; i < 101; i++) {
          JCheckBox cb = (JCheckBox) button[i].getComponent(0);
          cb.setVisible(true);
          button[i].repaint();
          cb.addActionListener(new selectedImg());
        }

      } else {
        // if the checkbox is deselected, remove the checkboxes from each button
        for (int i = 1; i < 101; i++) {
          JCheckBox cb = (JCheckBox) button[i].getComponent(0);
          cb.setSelected(false);
          cb.setVisible(false);
          button[i].repaint();
          File f = new File("relevant.txt");
          if(f != null){
            f.delete();
          }
        }
      }
      readRelevantFile();
    }

  }

  /*
   * The selectedImg function is part of a relevant handler and is responsible 
   * for handling checkboxes in an array of images. When a user selects a checkbox, 
   * the function is triggered and creates a text file. The first line of the text 
   * file stores the main selected image, while each subsequent line stores the images 
   * selected by the checkboxes. The text file is overwritten each time the user makes 
   * changes to their checkbox selections, ensuring that only the latest version is stored.
   */
  private class selectedImg implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      try {
        String path = Paths.get(".").toAbsolutePath().normalize().toString();
        String file_name = path + "/relevant.txt";
        FileWriter file = new FileWriter(file_name);
        BufferedWriter line = new BufferedWriter(file);
        // first line in the txt store the main picture
        line.write("images/" + (picNo)+ ".jpg");
        line.newLine();
        // after the first line store the relevant pic names
        for (int i = 1; i < 101; i++) {
          JCheckBox cb = (JCheckBox) button[i].getComponent(0);
          if (cb.isSelected()) {
            line.write("images/" + (i) + ".jpg");
            line.newLine();
          }
        }

        line.close();
        file.close();
        System.out.println("relevant.txt file written successfully");
      } catch (IOException err) {
        System.out.println("Cannot create relevant.txt file" + err.getMessage());
      }

    }
  }
}
