/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
import javax.imageio.*;
import UI.viewNetwork;
import com.twelvemonkeys.imageio.metadata.exif.TIFF;
import ij.ImagePlus;
import net.imagej.Data;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.List;
import java.util.ArrayList;

import net.imagej.ImgPlus;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.ejml.simple.SimpleMatrix;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.springframework.core.io.ClassPathResource;


import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.opencsv.CSVReader;

import java.awt.image.BufferedImage;
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

    @Parameter(type = ItemIO.OUTPUT)
    private Dataset output;

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

        images= Utilities.projectImageBetween0and1(images);
        images= Utilities.normalizeImage(images,meanStd[0], meanStd[1]);

        INDArray modelData= Utilities.createModelData(images);
        int [] prediction= model.predict(modelData);

        int height=images.get(0).numRows(), width=images.get(0).numCols();
        ArrayList<BufferedImage> pImages= Utilities.int2DToImage(prediction,height,width);

        try{
            Utilities.SaveImagesTiff(pImages, "OutPutTiff.tiff");
        }
        catch (Exception exc){
            System.out.println("Could not save images.");
        }

        DisplayImage(pImages);

       // output= pImages;

    }



    private void DisplayImage(ArrayList<BufferedImage> images){

        JLabel jLabel = new JLabel(new ImageIcon(images.get(0)));

        JPanel jPanel = new JPanel();
        jPanel.add(jLabel);
        jPanel.setVisible(true);
        for (int i=1; i<=images.size(); i++){
            try {
                Thread.sleep(1000);
            }
            catch (Exception e){

            }

            jLabel= new JLabel(new ImageIcon(images.get(i)));

        }
    }

    private void popCsvErrorMessage(){
        JOptionPane.showMessageDialog(null, "Something went wrong trying to open your csv file.\n" +
                        "Are you sure you chose the right file?",
                "warning",JOptionPane.INFORMATION_MESSAGE);
        viewNetwork.setVisible(true);
    }
}
