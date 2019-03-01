package UI;
import org.ejml.simple.SimpleMatrix;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.shape.Concat;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

public class testejml {

    public static void main(final String... args){
        double[][] a= new double[2][2];
        double[][] b= new double[2][2];
        a[0][0] =1;
        a[1][1] = 2;
        a[0][1] =3;
        b[0][1] = 1;
        b[1][0] = 2;

        SimpleMatrix aS = new SimpleMatrix(a);
        SimpleMatrix bS = new SimpleMatrix(b);

        long[] aShape = {aS.numCols(),aS.numRows()};
        long[] bShape = {bS.numCols(), bS.numRows()};

        INDArray arrA = new NDArray(aS.getMatrix().data,aShape,0,'C');
        INDArray arrB = new NDArray(bS.getMatrix().data,bShape,0,'C');


        INDArray arr = arrA.reshape(new int[]{1,2,2});
        System.out.println(arr);
        arr = Nd4j.concat(0,arr,arrB.reshape(new int[]{1,2,2}));

        System.out.println(aS);
        System.out.println(arr);


    }
}
