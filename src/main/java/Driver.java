import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Michael on 2/1/17.
 */
@SuppressWarnings("ALL")
public class Driver {

    private static Scanner kb = new Scanner(System.in);
    private static final Integer NUMBER_OF_TRAINING_POINTS = 20; // Number of datapoints for training
    private static Double LEARNING_RATE = .1; // Value by which the weights are adjusted
    private static Double BIAS_RATE = .05;
    private static Double[] x = new Double[NUMBER_OF_TRAINING_POINTS]; // Point X cords
    private static Double[] y = new Double[NUMBER_OF_TRAINING_POINTS]; // Point Y cords
    private static final Integer MAX_ITERATIONS = 5000; // Max iterations (if no linear seperation is found)
    private static Double[] W = new Double[3]; // 2 dimensions and bias
    private static Integer[] TRUE_VALUE = new Integer[NUMBER_OF_TRAINING_POINTS]; // Actual classification
    private static final Integer TEST_CASES = 1000; // How many test points to be tested
    private static Double avg; // Decision boundry accuracy on tests

    /*  ZERO  */
    private static final double zeroXmin = -1.0;
    private static final double zeroXmax = 1;
    private static final double zeroYmin = -1.0;
    private static final double zeroYmax = -.4;
    /*  ONE   */
    private static final double oneXmin = -1;
    private static final double oneXmax = 1;
    private static final double oneYmin = -.41;
    private static final double oneYmax = 1;
    /*  SWITCH */
    private static boolean chartingSwitch = true;
    private static boolean debug = false;

    /*  METRIC TESTING  */
    // Add section here for testing learning rates vs average time to boundry found?
    // Or is this a silly metric?

    public static void main(String args[]){
        ChartFrame trainingPlot, testPlot;
        String go = "y";
        while (!go.toLowerCase().contains("n")) {

            // Generate and chart the training data
            trainingPlot = new ChartFrame("Learning", chartTrainingData());
            trainingPlot.pack();
            trainingPlot.setVisible(chartingSwitch);

            // Train the perfecptron
            perceptron(); // Train the model

            // Test the decision boundry and plot the tests and their classifications
            testPlot = new ChartFrame("Testing", chartTestPlot());
            testPlot.pack();
            testPlot.setVisible(chartingSwitch);
            testPlot.setLocation(700, 0); // Offset on side of screen

            // Present run again option
            System.out.println("Run Again?");
            go = kb.nextLine();
            trainingPlot.setVisible(false); // Hide chart to block multiple windows
            testPlot.setVisible(false); // Hide chart to block multiple windows
        }
    }

    public static void perceptron(){
        if (debug){
            System.out.println("Training on " + NUMBER_OF_TRAINING_POINTS + " training points...");
        }
        int updateCount = 0;
        int totalError, iterationIndex = 0, comp_code=0;// Init variables
        while(true){
            totalError = 0; // Each EPOCH set total error to 0
            for(int i= 0; i< NUMBER_OF_TRAINING_POINTS; i++) { // for each training point...
                int actual = TRUE_VALUE[i]; // Get the actual binary value for the current node
                int predicted = 0; // Initialize var
                if (debug){
                    System.out.println("Prediction val: " + (x[i]*W[0]) + (y[i]*W[1]) + W[2]);
                }
                if ((double) ( (x[i]*W[0]) + (y[i]*W[1]) + W[2]) >= 0){ // Classify function
                    predicted = 1; // Get the predicted value
                } else predicted = 0;
                int error = actual - predicted; //  1 , 0 or -1
                if (debug){
                    System.out.println("Point " + x[i] + ","+ y[i] + " || Predicted: " + predicted + " Actual: " +actual + " Error: " + error + "Iteration: " + iterationIndex);
                }
                if (error != 0){
                    updateCount++;
                }
                W[0] += x[i] * LEARNING_RATE * error; // adjust line if wrong by learning rate
                W[1] += y[i] * LEARNING_RATE * error; // in positive or negative direction
                W[2] += error * BIAS_RATE; // Bias, only allow small adjustment
                if (error != 0){
                    totalError++; // Count # of errors in the epoch
                }
            }
            // Break if boundry is found
            if (totalError == 0){
                System.out.println(" -------\n" +
                        "Linear Separating Boundary: " + W[0] +"x "+ W[1] +"y +"+ W[2] +"\n" +
                        "Found at Iteration: " + iterationIndex);
                System.out.println("Total Line Updates: " + updateCount);
                break;
            }
            // Or break if at max iteratons
            if (iterationIndex >= MAX_ITERATIONS){
                System.out.println("Hit Max Iterations");
                System.out.println("Final boundry line: " + W[0] + " " + W[1] + " " + W[2]);
                break;
            }
            iterationIndex++;
        }
        System.out.println("Complete.");
    }

    private static JFreeChart chartTrainingData(){
        System.out.println("Building random plot of " + NUMBER_OF_TRAINING_POINTS);
        JFreeChart chart = ChartFactory.createScatterPlot(
                NUMBER_OF_TRAINING_POINTS + " Training Points", // chart title
                "", // x axis label
                "", // y axis label
                createLearningDataset(), // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );

        XYPlot xyPlot = (XYPlot) chart.getPlot();
        xyPlot.setDomainCrosshairVisible(false);
        xyPlot.setRangeCrosshairVisible(false);
        XYItemRenderer renderer = xyPlot.getRenderer();
        ValueAxis domain = xyPlot.getDomainAxis();
        domain.setRange(-1.00, 1.0);
        ValueAxis range = xyPlot.getRangeAxis();
        range.setRange(-1.00,1.0);
        return chart;
    }

    private static JFreeChart chartTestPlot(){
        JFreeChart chart = ChartFactory.createScatterPlot(
                 TEST_CASES + " Decision Boundary Tests", // chart title
                "", // x axis label
                "", // y axis label
                createTestDataset(), // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false// urls
        );
        XYPlot xyPlot = (XYPlot) chart.getPlot();
        xyPlot.setDomainCrosshairVisible(false);
        xyPlot.setRangeCrosshairVisible(false);
        XYItemRenderer renderer = xyPlot.getRenderer();
        ValueAxis domain = xyPlot.getDomainAxis();
        domain.setRange(-1.00, 1.0);
        ValueAxis range = xyPlot.getRangeAxis();
        range.setRange(-1.00,1.0);
        return chart;
    }

    private static XYDataset createLearningDataset(){

        for (int i =0; i<2; i++){ // Set random weights
            W[i] = new Random().doubles(-1,1).findFirst().getAsDouble(); // small initial weights
        }
        W[2] = 1.0; // bias

        XYSeriesCollection result = new XYSeriesCollection();
        XYSeries ones = new XYSeries("1 Point");
        XYSeries zeros = new XYSeries("0 Point");

        for (int i = 0; i < NUMBER_OF_TRAINING_POINTS/2; i++) {
            x[i] = new Random().doubles(zeroXmin,zeroXmax).findFirst().getAsDouble();
            y[i] = new Random().doubles(zeroYmin,zeroYmax).findFirst().getAsDouble();
            zeros.add(x[i], y[i]);
            TRUE_VALUE[i] = 0;
        }
        for (int i = (NUMBER_OF_TRAINING_POINTS/2); i < NUMBER_OF_TRAINING_POINTS; i++) {
            x[i] = new Random().doubles(oneXmin,oneXmax).findFirst().getAsDouble();
            y[i] = new Random().doubles(oneYmin,oneYmax).findFirst().getAsDouble();
            ones.add(x[i], y[i]);
            TRUE_VALUE[i] = 1;
        }
        result.addSeries(ones);
        result.addSeries(zeros);
        return result;
    }

    private static XYDataset createTestDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries miss = new XYSeries("Misclassified");
        XYSeries ones = new XYSeries("Predicted 1");
        XYSeries zeros = new XYSeries("Predicted 0");

        double testX = 0;
        double testY = 0;
        for(int i = 0; i < TEST_CASES ; i++){ // TEST 1's
            testX = new Random().doubles(oneXmin,oneXmax).findFirst().getAsDouble(); // 1- class x
            testY = new Random().doubles(oneYmin,oneYmax).findFirst().getAsDouble(); // 1- class y
            if ((double) (testX* W[0] + testY* W[1] + W[2]) >= 0){ // Expect a 1
                ones.add(testX,testY); // accept if >= 0
            } else miss.add(testX,testY); // else miss

        }

        for(int i = 0; i < TEST_CASES ; i++){ // TEST 0's
           testX = new Random().doubles(zeroXmin,zeroXmax).findFirst().getAsDouble(); // 0- class x
           testY = new Random().doubles(zeroYmin,zeroYmax).findFirst().getAsDouble(); // 0- class y
            if ((double) (testX* W[0] + testY* W[1] + W[2]) >= 0){ // Expect < 0
                miss.add(testX,testY); // miss if 0 or greater
            } else zeros.add(testX,testY); // correct if < 0
        }

        int k = miss.getItemCount();
        avg = ((double) (TEST_CASES-k)/TEST_CASES) * 100;
        System.out.println("Predicted " + (TEST_CASES-k) + " out of " + TEST_CASES+ " - " +avg + "%");

        dataset.addSeries(ones);
        dataset.addSeries(zeros);
        dataset.addSeries(miss);
        return dataset;
    }
}
