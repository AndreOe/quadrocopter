package oeschger.andre.quadrocopter.util;

import android.util.Log;

/**
 * Created by andre on 24.01.16.
 */
public class LinAlgUtils {

    private final String TAG = "LinAlgUtils";

    // cross product

    public float[] crossProduct(float[] a, float[] b){

        if(!(a.length == 3 && b.length == 3)){
            throw new IllegalArgumentException("a and b must have length 3");
        }

        float[] c = new float[3];

        c[0] = a[1]*b[2]-a[2]*b[1];
        c[1] = a[2]*b[0]-a[0]*b[2];
        c[2] = a[0]*b[1]-a[1]*b[0];

        return c;
    }

    public double[] crossProduct(double[] a, double[] b){

        if(!(a.length == 3 && b.length == 3)){
            throw new IllegalArgumentException("a and b must have length 3");
        }

        double[] c = new double[3];

        c[0] = a[1]*b[2]-a[2]*b[1];
        c[1] = a[2]*b[0]-a[0]*b[2];
        c[2] = a[0]*b[1]-a[1]*b[0];

        return c;
    }

    public int[] crossProduct(int[] a, int[] b){

        if(!(a.length == 3 && b.length == 3)){
            throw new IllegalArgumentException("a and b must have length 3");
        }

        int[] c = new int[3];

        c[0] = a[1]*b[2]-a[2]*b[1];
        c[1] = a[2]*b[0]-a[0]*b[2];
        c[2] = a[0]*b[1]-a[1]*b[0];;

        return c;
    }

    public long[] crossProduct(long[] a, long[] b){

        if(!(a.length == 3 && b.length == 3)){
            throw new IllegalArgumentException("a and b must have length 3");
        }

        long[] c = new long[3];

        c[0] = a[1]*b[2]-a[2]*b[1];
        c[1] = a[2]*b[0]-a[0]*b[2];
        c[2] = a[0]*b[1]-a[1]*b[0];

        return c;
    }


    //norm

    /*public float norm(float[] a){
        float b = 0;
        for(int i=0; i<a.length;i++){
            b = b + a[i]*a[i];
        }
        return (float)Math.sqrt(b);
    }*/

    public double norm(double[] a){
        double b = 0;
        for(int i=0; i<a.length;i++){
            b = b + a[i]*a[i];
        }
        return Math.sqrt(b);
    }

    /*public float[] normalize(float[] a){
        float b = norm(a);
        float[] c = new float[a.length];
        for(int i=0; i<a.length;i++){
            c[i] = a[i]/b;
        }

        return c;
    }*/

    public double[] normalize(double[] a){
        double b = norm(a);
        double[] c = new double[a.length];
        for(int i=0; i<a.length;i++){
            c[i] = a[i]/b;
        }

        return c;
    }


    //Matrix Multiplication

    public float[][] matrixMultiply(float[][] a, float[][]b){
        int aRows = a.length;
        int aColumns = a[0].length;
        int bRows = b.length;
        int bColumns = b[0].length;

        if (aColumns != bRows || aRows != bColumns) {
            throw new IllegalArgumentException("Dimension mismatch");
        }

        float[][] c = new float[aRows][bColumns];

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                c[i][j] = 0.0f;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    c[i][j] = c[i][j] + a[i][k] * b[k][j];
                }
            }
        }

        return c;

    }

    public double[][] matrixMultiply(double[][] a, double[][]b) {
        int aRows = a.length;
        int aColumns = a[0].length;
        int bRows = b.length;
        int bColumns = b[0].length;

        if (aColumns != bRows || aRows != bColumns) {
            throw new IllegalArgumentException("Dimension mismatch");
        }

        double[][] c = new double[aRows][bColumns];

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                c[i][j] = 0.0;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    c[i][j] = c[i][j] + a[i][k] * b[k][j];
                }
            }
        }

        return c;

    }

    public int[][] matrixMultiply(int[][] a, int[][]b) {
        int aRows = a.length;
        int aColumns = a[0].length;
        int bRows = b.length;
        int bColumns = b[0].length;

        if (aColumns != bRows || aRows != bColumns) {
            throw new IllegalArgumentException("Dimension mismatch");
        }

        int[][] c = new int[aRows][bColumns];

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                c[i][j] = 0;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    c[i][j] = c[i][j] + a[i][k] * b[k][j];
                }
            }
        }

        return c;

    }

    public long[][] matrixMultiply(long[][] a, long[][]b) {
        int aRows = a.length;
        int aColumns = a[0].length;
        int bRows = b.length;
        int bColumns = b[0].length;

        if (aColumns != bRows || aRows != bColumns) {
            throw new IllegalArgumentException("Dimension mismatch");
        }

        long[][] c = new long[aRows][bColumns];

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                c[i][j] = 0;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    c[i][j] = c[i][j] + a[i][k] * b[k][j];
                }
            }
        }

        return c;

    }

    public float[][] crossProductSkewMatrix(float[] a){
        if(a.length != 3){
            throw new IllegalArgumentException("a and b must have length 3");
        }

        float[][] b = new float[3][3];

        b[0][0] = 0;
        b[0][1] = -a[2];
        b[0][2] = a[1];
        b[1][0] = a[2];
        b[1][1] = 0;
        b[1][2] = -a[0];
        b[2][0] = -a[1];
        b[2][1] = a[0];
        b[2][2] = 0;

        return b;
    }

    public double[][] crossProductSkewMatrix(double[] a){
        if(a.length != 3){
            throw new IllegalArgumentException("a and b must have length 3");
        }

        double[][] b = new double[3][3];

        b[0][0] = 0;
        b[0][1] = -a[2];
        b[0][2] = a[1];
        b[1][0] = a[2];
        b[1][1] = 0;
        b[1][2] = -a[0];
        b[2][0] = -a[1];
        b[2][1] = a[0];
        b[2][2] = 0;

        return b;
    }

    public int[][] crossProductSkewMatrix(int[] a){
        if(a.length != 3){
            throw new IllegalArgumentException("a and b must have length 3");
        }

        int[][] b = new int[3][3];

        b[0][0] = 0;
        b[0][1] = -a[2];
        b[0][2] = a[1];
        b[1][0] = a[2];
        b[1][1] = 0;
        b[1][2] = -a[0];
        b[2][0] = -a[1];
        b[2][1] = a[0];
        b[2][2] = 0;

        return b;
    }

    public long[][] crossProductSkewMatrix(long[] a){
        if(a.length != 3){
            throw new IllegalArgumentException("a and b must have length 3");
        }

        long[][] b = new long[3][3];

        b[0][0] = 0;
        b[0][1] = -a[2];
        b[0][2] = a[1];
        b[1][0] = a[2];
        b[1][1] = 0;
        b[1][2] = -a[0];
        b[2][0] = -a[1];
        b[2][1] = a[0];
        b[2][2] = 0;

        return b;
    }

    public double[][] transpose(double[][] a){

        int aRows = a.length;
        int aColumns = a[0].length;

        double[][] b = new double[aColumns][aRows];

        for(int i = 0;i<aRows;i++){
            for(int j=0;j<aColumns;j++){
                b[j][i] = a[i][j];
            }
        }

        return b;
    }

}
