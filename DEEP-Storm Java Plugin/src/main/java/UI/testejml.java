package UI;

import org.bytedeco.javacpp.FlyCapture2;
import org.ejml.simple.SimpleMatrix;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.shape.Concat;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.util.ArrayUtil;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class testejml {

    public static void main(final String... args) {
//        double[][] a= new double[2][2];
//        double[][] b= new double[2][2];
//        a[0][0] =1;
//        a[1][1] = 2;
//        a[0][1] =3;
//        b[0][1] = 1;
//        b[1][0] = 2;
//
//        SimpleMatrix aS = new SimpleMatrix(a);
//        SimpleMatrix bS = new SimpleMatrix(b);
//
//        long[] aShape = {aS.numCols(),aS.numRows()};
//        long[] bShape = {bS.numCols(), bS.numRows()};
//
//        INDArray arrA = new NDArray(aS.getMatrix().data,aShape,0,'C');
//        INDArray arrB = new NDArray(bS.getMatrix().data,bShape,0,'C');
//
//
//        INDArray arr = arrA.reshape(new int[]{1,2,2});
//        System.out.println(arr);
//        arr = Nd4j.concat(0,arr,arrB.reshape(new int[]{1,2,2}));
//
//        System.out.println(aS);
//        System.out.println(arr);
        try {
            testSavingAsTiff();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private static void testSavingAsTiff() throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(new File("D:\\repositories\\Deep-STORM\\demo 2 - Real Microtubules\\testStack_RealMicrotubules.tif"));
        ImageReader reader = ImageIO.getImageReaders(iis).next();
        reader.setInput(iis);
        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
        for (int i = 0; i < reader.getNumImages(false); i++) {
            images.add(reader.read(i));
        }

    }


//    private static void convetIntArrayToBufferedImage(int[] array, int height, int width){
//        ArrayList<BufferedImage> images= new ArrayList<BufferedImage>();
//        int span= height*width;
//        if (array.length%span==0){
//            for (int i=0; i<array.length; i+=span) {
//                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
//                WritableRaster raster = image.getRaster();
//                raster.setSamples(0, 0, width, height, i, array);
//                images.add(image);
//            }
//        }
//
//
//    }
}
