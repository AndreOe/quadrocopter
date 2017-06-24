package oeschger.andre.quadrocopter.util;

import android.util.Log;

/**
 * Created by andre on 24.01.16.
 */
public abstract class LinAlgUtils {

    private static final String TAG = "LinAlgUtils";

    // cross product
    public static final void crossProduct(final double[] a, final double[] b, final double[] r){

        if(a.length != 3 || b.length != 3){
            throw new IllegalArgumentException("a and b must have length 3");
        }

        r[0] = a[1]*b[2]-a[2]*b[1];
        r[1] = a[2]*b[0]-a[0]*b[2];
        r[2] = a[0]*b[1]-a[1]*b[0];
    }

    //norm
    public static final double norm(final double[] a){
        double b = 0;
        for(int i=0; i<a.length;i++){
            b += a[i]*a[i];
        }
        return Math.sqrt(b);
    }

    //normalize
    public static final void normalize(final double[] a){
        double b = norm(a);
        for(int i=0; i<a.length;i++){
            a[i] /= b;
        }
    }


    //Matrix Multiplication
    public static final void matrixMultiply(final double[][] a, final double[][]b, final double[][]r) {
        final int aRows = a.length;
        final int aColumns = a[0].length;
        final int bRows = b.length;
        final int bColumns = b[0].length;
        final int rRows = r.length;
        final int rColumns = r[0].length;


        if (aColumns != bRows || aRows != bColumns || aRows != rRows || bColumns != rColumns) {
            throw new IllegalArgumentException("Dimension mismatch");
        }

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                r[i][j] = 0.0;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    r[i][j] += a[i][k] * b[k][j];
                }
            }
        }
    }

    public static final void crossProductSkewMatrix(final double[] a, final double[][] r){
        if(a.length != 3 || r.length !=3 || r[0].length != 3){
            throw new IllegalArgumentException("a must have length 3");
        }

        if(r.length !=3 || r[0].length != 3){
            throw new IllegalArgumentException("r must have dimension 3x3");
        }

        r[0][0] = 0;
        r[0][1] = -a[2];
        r[0][2] = a[1];
        r[1][0] = a[2];
        r[1][1] = 0;
        r[1][2] = -a[0];
        r[2][0] = -a[1];
        r[2][1] = a[0];
        r[2][2] = 0;
    }


    public static final void transpose(final double[][] a, final double[][] r){

        final int aRows = a.length;
        final int aColumns = a[0].length;

        if(aRows != r[0].length || aColumns != r.length){
            throw new IllegalArgumentException("Dimension mismatch");
        }

        for(int i = 0;i<aRows;i++){
            for(int j=0;j<aColumns;j++){
                r[j][i] = a[i][j];
            }
        }

    }

}
