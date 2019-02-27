

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;
import org.ejml.simple.SimpleMatrix;

public class Utilities {

    /*
    Reads frames from tiff and converts each frame to a "simple matrix" object.
    */
    public static ArrayList<SimpleMatrix> ReadStackFromTiff(String directory) throws IOException {

        ImageInputStream iis = ImageIO.createImageInputStream(new File(directory));
        ImageReader reader = ImageIO.getImageReaders(iis).next();
        reader.setInput(iis);
        BufferedImage[] frames = new BufferedImage[reader.getNumImages(true)];
        for (int i = 0; i < reader.getNumImages(false); i++) {
            frames[i] = reader.read(i);
        }

        //Convert frmaes to openCV matrix
        int imageNum = reader.getNumImages(true);

        ArrayList<SimpleMatrix> matrices = new ArrayList<SimpleMatrix>();

        for (int i = 0; i < imageNum; i++) {
            matrices.add(convertImageToMatrix(reader.read(i)));
        }

        rescaleImages(matrices, 8);

        return matrices;

    }

    private static SimpleMatrix convertImageToMatrix(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        double[][] frame = new double[width][height];
        Raster raster = inputImage.getRaster();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                frame[j][i] = raster.getPixel(i, j, new int[3])[0];
            }

        }

        return new SimpleMatrix(frame);
    }

    private static ArrayList<SimpleMatrix> rescaleImages(ArrayList<SimpleMatrix> images,
                                                         int upsamplingFactor) {

        ArrayList<SimpleMatrix> rescalesImages = new ArrayList<SimpleMatrix>();
        SimpleMatrix ones = new SimpleMatrix(upsamplingFactor, upsamplingFactor);
        for (int i = 0; i < upsamplingFactor; i++) {
            for (int j = 0; j < upsamplingFactor; j++) {
                ones.set(i, j, 1);
            }

        }

        for (int i = 0; i < images.size(); i++) {
            SimpleMatrix matrix = images.get(i);
            matrix = matrix.kron(ones);
            rescalesImages.add(matrix);
        }

        return rescalesImages;
    }

    public static ArrayList<SimpleMatrix> projectImageBetween0and1
            (ArrayList<SimpleMatrix> images) {
        ArrayList<SimpleMatrix> array = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            SimpleMatrix mat = images.get(i);

            //double min= getMinValue(mat);

//            double min = mat.elementMinAbs();
//            double max = mat.elementMaxAbs();
//            mat.minus(min);
//            mat.divide(max - min);
            array.add(mat);

        }
        return array;
    }

    public static ArrayList<SimpleMatrix> normalizeImage(ArrayList<SimpleMatrix> images,
                                                         double mean, double std) {
        ArrayList<SimpleMatrix> normalized = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            SimpleMatrix mat = images.get(i);
            //mat.minus(mean);
            mat.divide(std);
            normalized.add(mat);
        }

        return normalized;
    }

    //Reads the given csv file. This file should contain mean and std.
    public static double[] readMeanStd(String csvName) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(csvName));
        CSVReader csvReader = new CSVReader(reader);
        String[] header= csvReader.readNext();
        if (header.length!=2)
            return null;
        String[] stringValues = csvReader.readNext();
        double[] values= new double[2];
        values[0]= Double.parseDouble(stringValues[0]);
        values[1]= Double.parseDouble(stringValues[1]);
        return values;
    }


}
