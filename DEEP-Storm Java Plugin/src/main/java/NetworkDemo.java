/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

import UI.viewNetwork;
import net.imagej.Dataset;
import net.imagej.ImageJ;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.ejml.simple.SimpleMatrix;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.springframework.core.io.ClassPathResource;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * A very simple plugin.
 * <p>
 * The annotation {@code @Plugin} lets ImageJ know that this is a plugin. There
 * are a vast number of possible plugins; {@code Command} plugins are the most
 * common one: they take inputs and produce outputs.
 * </p>
 * <p>
 * A {@link Command} is most useful when it is bound to a menu item; that is
 * what the {@code menuPath} parameter of the {@code @Plugin} annotation does.
 * </p>
 * <p>
 * Each input to the command is specified as a field with the {@code @Parameter}
 * annotation. Each output is specified the same way, but with a
 * {@code @Parameter(type = ItemIO.OUTPUT)} annotation.
 * </p>
 *
 * @author Johannes Schindelin
 * @author Curtis Rueden
 */
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Deep-STORM>Demo")
public class NetworkDemo implements Command, ActionListener {


    @Parameter(type = ItemIO.INPUT)
    private Dataset input;

    private viewNetwork viewNetwork = new viewNetwork();


    //When an image is loaded, run your network
    @Override
    public void run() {
        viewNetwork.setVisible(true);
    }

    //main method for testing purpose.
    public static void main(final String... args) {
        // Launch ImageJ as usual.
        final ImageJ ij = new ImageJ();
        ij.launch(args);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (viewNetwork.getModelPath()==null || viewNetwork.getCsvPath()==null){
            JOptionPane.showMessageDialog(null, "Please enter files before pressing run my net button",
                    "warning",JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        viewNetwork.setVisible(false);

        MultiLayerNetwork model;
        try {
            //opening the saved model.
            String simpleMlp = new ClassPathResource(viewNetwork.getModelPath()).getFile().getPath();
            model = KerasModelImport.importKerasSequentialModelAndWeights(simpleMlp);
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(null, "Something went wrong trying to open your model.\n" +
                            "Are you sure you chose the right file?",
                    "warning",JOptionPane.INFORMATION_MESSAGE);
            viewNetwork.setVisible(true);
            return;
        }

        double[] meanStd= new double[2];
        try (
                Reader reader = Files.newBufferedReader(Paths.get(viewNetwork.getCsvPath()));
                CSVReader csvReader = new CSVReader(reader);
        ) {
            // Reading Records One by One in a String array
            String[] firstLine=csvReader.readNext();
            if (firstLine==null){
                popCsvErrorMessage();
            }

            String[]secondLine= csvReader.readNext();
            if(secondLine==null || secondLine.length<2){
                popCsvErrorMessage();
            }

            meanStd[0]= Double.parseDouble(secondLine[0]);
            meanStd[1]= Double.parseDouble(secondLine[1]);

        }catch (Exception exc){
            popCsvErrorMessage();
        }

        ArrayList<SimpleMatrix> images;
        try {
            images = Utilities.ReadStackFromTiff(this.input.getSource());
        } catch (Exception exc) {
            System.out.println(exc);
            return;
        }

        INDArray modelData= Utilities.createModelData(images);
        int [] prediction= model.predict(modelData);

    }

    private void popCsvErrorMessage(){
        JOptionPane.showMessageDialog(null, "Something went wrong trying to open your csv file.\n" +
                        "Are you sure you chose the right file?",
                "warning",JOptionPane.INFORMATION_MESSAGE);
        viewNetwork.setVisible(true);
    }
}
