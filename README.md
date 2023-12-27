# Content-Based-Image-Retrieval-System
## Part 1: Color and Intensity Conten-Based Image Retrieval
This project is to implment a simple Content-Based Image Retrieval system based on two different color histogram comparison methods.
### 1. Test Image Database
This test image database includes 100 true-color images in .jpg format.
### 2. Color Histogram
Color histogram comparison is a simple but effective apporach in CBIR systems.
Here are two ways to combine the information from 3 color channels (R, G, B):

**A. Intensity Method**
By this way, the 24-bit of RGB (8 bits for each color channel) color intensities can be transformed into a single 8-bit value. The histogram bins selected for this case are listed below:
H 1 : I[0,10]; 

H 2 : I[10,20); 

H 3 : I[20,30);

H 4 : I[30,40)...... ...... ...... H 6 : [50,60);

... ... ...

H 25 : [240,255];

**B. Color-Code Method**
The 24-bit of RGB color intensities can be transformed into a 6-bit color code,
composed from the most significant 2 bits of each of the three color
components, as illustrated in the following figure.
<img alt="RBG Color" src="https://github.com/chloeNgo99/Content-Based-Image-Retrieval-System/blob/main/Demo%20Pics/6.png">

For example, the R, G, and B values for a pixel are 128, 0, and 255 respectively. So the bit representations of them are 10000000, 00000000, and 11111111. Then the 6-bit color code value will be 100011.
In color code, there will be 64 bins with H1: 000000, H2: 000001, H3: 000010, ... H64: 111111

### 3. Histogram Comparison
You need to implement the distance metrics for histogram comparison. Let Hi(j) denote the number of pixels in j th bin for the ith image. Then the difference between the ith image and kth image can be given by the following distance metric:
<img alt="distance fomular" src="https://github.com/chloeNgo99/Content-Based-Image-Retrieval-System/blob/main/Demo%20Pics/7.png">

## Part 2: Relevant Feedback for Color and Intensity

### 4. Intensity + color-code (use both intensity and color-code features together for retrieval) with Relevance Feedback
**C. Relevance Feedback**

Implement system based on the RF algorithm.
Detailed requirements:

1. Use intensity method based histogram together with color-code based histogram as the feature set for each image.

2. If histogram is in “# of counts,” divide each one by its corresponding image size

3. Use Gaussian normalization for feature normalization
<img alt="Gaussian Normalization Fomular" src="https://github.com/chloeNgo99/Content-Based-Image-Retrieval-System/blob/main/Demo%20Pics/8.png">

4. Use simplified RF version
    a. Use the normalized feature matrix and initial weights (no-bias weights) to return initial query results

    b. On the GUI, only two levels of relevance are required: relevant and non-relevant

    c. Collect user’s feedback, update the feature weights

    d. Return updated query results and go through iterations (step c & d)

    e. Distance metric:
      <img alt="Distance metric" src="https://github.com/chloeNgo99/Content-Based-Image-Retrieval-System/blob/main/Demo%20Pics/9.png">

NOTE: In updating the weights, if standard deviation sti for a feature i of all the relevant images is 0.

  a. and its mean value mi is not 0, set sti to be 0.5*min(non-zero standard deviations of all thefeatures). Then calculate the feature weight Wi
  
  b. and mi is 0, set Wi = 0

### Demo to Peer:
1. User query interface: Similar to Project 1 except for an additional option: Intensity + color-code (use both intensity and color-code features together for retrieval) with Relevance Feedback. When this option is selected, the interface should allow users to provide feedback (i.e., to indicate Relevant or Not Relevant) to each retrieved image. Retrieved images should be presented in a decent way: the entire image should be visible to users.

2. Demo the query results using relevance feedback for several iterations.

3. Users are allowed to switch among different query methods (intensity, color-code, and RF) within the same application.

4. Any other items you like.

## GUI Demo
1. Pick any image display in the grid
<img alt="step 1" src="https://github.com/chloeNgo99/Content-Based-Image-Retrieval-System/blob/main/Demo%20Pics/1.png">
2. Try the intensity button
<img alt="step 2" src="https://github.com/chloeNgo99/Content-Based-Image-Retrieval-System/blob/main/Demo%20Pics/2.png">
3. Try the color code
<img alt="step 3" src="https://github.com/chloeNgo99/Content-Based-Image-Retrieval-System/blob/main/Demo%20Pics/3.png">
3. Try the Intensity + Color button
<img alt="step 4" src="https://github.com/chloeNgo99/Content-Based-Image-Retrieval-System/blob/main/Demo%20Pics/4.png">
4. Click on the relevant checkbox then select any images that related to the main image.
5. Re-click the Intensity + Color button
<img alt="step 4" src="https://github.com/chloeNgo99/Content-Based-Image-Retrieval-System/blob/main/Demo%20Pics/5.png">
