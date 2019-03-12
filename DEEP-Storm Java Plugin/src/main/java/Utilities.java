

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import org.ejml.simple.SimpleMatrix;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;

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


        //Convert frmaes to simple matrix
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

    public static INDArray createModelData(List<SimpleMatrix> mats){
        SimpleMatrix mat= mats.get(0);
        INDArray arr= simpleMatrixToNDArray(mat);
        for (int i=1; i< mats.size(); i++){
            arr = Nd4j.concat(0, arr,simpleMatrixToNDArray(mats.get(i)));
        }

        return arr;
    }

    public static ArrayList<BufferedImage> nd4jToImage(INDArray[] array){
        ArrayList<BufferedImage> images= new ArrayList<BufferedImage>();
        for (INDArray image:array) {
            DataBuffer dataBuffer = image.data();
            int[] intArray = dataBuffer.asInt();
            images.add(int2DToImage(intArray, image.columns(), image.rows()));

        }

        return images;
    }

    public static BufferedImage int2DToImage(int[] array, int height, int width){

                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
                WritableRaster raster = image.getRaster();
                raster.setSamples(0, 0, width, height, 0, array);
                return image;

    }

    private static NDArray simpleMatrixToNDArray(SimpleMatrix mat){
        long[] shape= {1, mat.numCols(),mat.numRows()};
        return new NDArray(mat.getMatrix().data, shape,0, 'C');
    }

    public static void SaveImagesTiff(ArrayList<BufferedImage> images, String tiffFileName) throws IOException {
        if (tiffFileName== "" || tiffFileName==null) {
        tiffFileName = "DeepSTORMed image";
        }
            ImageWriter imageWriter = ImageIO
                    .getImageWritersByFormatName("tiff").next();


        ImageOutputStream ios = ImageIO.createImageOutputStream(new File(tiffFileName));
        imageWriter.setOutput(ios);

        imageWriter.prepareWriteSequence(null);
        for (BufferedImage image : images) {
            imageWriter.writeToSequence(new IIOImage(image, null, null), null);
        }
        imageWriter.endWriteSequence();

        imageWriter.dispose();
        ios.flush();
        ios.close();

    }

}
