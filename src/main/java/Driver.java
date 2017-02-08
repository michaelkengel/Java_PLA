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

    private static final Integer NUMBER_OF_TRAINING_POINTS = 20;
    private static final Double LEARNING_RATE = .01;
    private static Double[] x = new Double[NUMBER_OF_TRAINING_POINTS];
    private static Double[] y = new Double[NUMBER_OF_TRAINING_POINTS];
    private static final Integer MAX_ITERATIONS = 5000;
    private static Double[] WEIGHTS = new Double[3]; // 2 dimensions and bias
    private static Integer[] TRUE_VALUE = new Integer[NUMBER_OF_TRAINING_POINTS];
    private static final Integer TEST_CASES = 1000;
    private static double avg;

    private static final double zeroXmin = -1.0;
    private static final double zeroXmax = 1;
    private static final double zeroYmin = -1.0;
    private static final double zeroYmax = -.6;

    private static final double oneXmin = -1;
    private static final double oneXmax = 1;
    private static final double oneYmin = -.61;
    private static final double oneYmax = 1;
    private static Scanner kb = new Scanner(System.in);


    public static void main(String args[]){
        String go = "y";
        while(!go.toLowerCase().contains("n")){
            chartTrainingData(); // Create the data points, chart the data
            perceptron(); // Train the model
            chartTestPlot(); // Chart the tests
            System.out.println("Run Again?");
            go = kb.nextLine();
        }
    }

    public static void perceptron(){
        System.out.println("Training on " + NUMBER_OF_TRAINING_POINTS + " training points...");
        int totalError, iterationIndex = 0;
        do{
            totalError = 0;
            for(int i= 0; i< NUMBER_OF_TRAINING_POINTS ;i++) {
                int actual = TRUE_VALUE[i]; // Get the actual / expected value for the current node
                int predicted = 0;
                if ((double) (x[i]*WEIGHTS[0] + y[i]*WEIGHTS[1] + WEIGHTS[2]) >= 0){
                    predicted = 1;
                } else predicted = 0;
                int error = actual - predicted; //  1 , 0 or -1
                System.out.println("Point " + x[i] + ","+ y[i] + " || Predicted: " +predicted + " Actual: " +actual + " Error: " + error);
                WEIGHTS[0] += x[i] * LEARNING_RATE * error; // adjust line if wrong
                WEIGHTS[1] += y[i] * LEARNING_RATE * error;
                WEIGHTS[2] += LEARNING_RATE * error;
                if (error != 0){
                    totalError++; // Count # of errors in the epoch
                }
            }
            if (totalError == 0){
                System.out.println(" -------\n" +
                        "Linear Separating Boundary: " + WEIGHTS[0] +"x "+ WEIGHTS[1] +"y "+ WEIGHTS[2] +"b\n" +
                        "Found at Iteration: " + iterationIndex);
                break;
            }
            if (iterationIndex >= MAX_ITERATIONS){
                System.out.println("Hit Max Iterations");
                break;
            }
            iterationIndex++;
        }while(true);

        System.out.println("Complete.");
        System.out.println("Final boundry line: " + WEIGHTS[0] + " " + WEIGHTS[1] + " " + WEIGHTS[2]);
    }

    public static void chartTrainingData(){
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
        ChartFrame frame = new ChartFrame("Learning", chart);
        frame.pack();
        frame.setVisible(true);
    }

    public static void chartTestPlot(){
        JFreeChart chart = ChartFactory.createScatterPlot(
                 TEST_CASES + " Decision Boundry Tests", // chart title
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
        ChartFrame frame = new ChartFrame("Testing", chart);
        frame.pack();
        frame.setVisible(true);
    }

    private static XYDataset createLearningDataset(){

        for (int i =0; i<2; i++){ // Set random weights
            WEIGHTS[i] = new Random().doubles(0,.5).findFirst().getAsDouble(); // small initial weights
        }
        WEIGHTS[2] = 1.0; // bias

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
        XYSeries miss = new XYSeries("Misclassiefied");
        XYSeries ones = new XYSeries("Predicted 1");
        XYSeries zeros = new XYSeries("Predicted 0");

        double testX = 0;
        double testY = 0;
        for(int i = 0; i < TEST_CASES ; i++){ // TEST 1's
            testX = new Random().doubles(oneXmin,oneXmax).findFirst().getAsDouble(); // 1- class x
            testY = new Random().doubles(oneYmin,oneYmax).findFirst().getAsDouble(); // 1- class y
            if ((double) (testX*WEIGHTS[0] + testY*WEIGHTS[1] + WEIGHTS[2]) >= 0){ // Expect a 1
                ones.add(testX,testY); // accept if >= 0
            } else miss.add(testX,testY); // else miss

        }

        for(int i = 0; i < TEST_CASES ; i++){ // TEST 0's
           testX = new Random().doubles(zeroXmin,zeroXmax).findFirst().getAsDouble(); // 0- class x
           testY = new Random().doubles(zeroYmin,zeroYmax).findFirst().getAsDouble(); // 0- class y
            if ((double) (testX*WEIGHTS[0] + testY*WEIGHTS[1] + WEIGHTS[2]) >= 0){ // Expect < 0
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
