package control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Administrator
 */
public class ImageCluster2 {

    private Pixel[] matrix;
    private Pixel[] center;
    private Pixel[][] result;
    //private double[][] u;
    //private double[][] e;
    private double[][] a;
    static int N;
    int row;
    int column;
    private int k;

    public Pixel[] readData(String filename) {
        try {
            File input = new File(filename);
            Scanner scan = new Scanner(input);
            int count = 0;
            row = Integer.parseInt(scan.nextLine());
            column = Integer.parseInt(scan.nextLine());
            N = row * column;
            matrix = new Pixel[row * column];

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] array = line.split(",");

                for (String array1 : array) {
                    String[] a = array1.split(" ");
                    Pixel pi = new Pixel(Double.parseDouble(a[0]),
                            Double.parseDouble(a[1]), Double.parseDouble(a[2]));
                    matrix[count] = pi;
                    count++;
                }
            }

        } catch (FileNotFoundException e) {
        }
        return matrix;
    }

    public double[][] readMatrix(String filename) {
        //a = new double[10000][4];
        //System.out.println(row);
        //System.out.println(FC_PFS2.getC());
        setK(FC_PFS2.getC());
        a = new double[row * column][getK()];
        try {
            File file = new File(filename);
            Scanner scan = new Scanner(file);
            int count = 0;

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] array = line.split(" ");

                for (int i = 0; i < getK(); i++) {
                    a[count][i] = Double.parseDouble(array[i]);

                }
                count++;
            }
        } catch (FileNotFoundException | NumberFormatException err) {
            err.getStackTrace();
        }
        return a;
    }

    public Pixel[] readCenter(String filename) {
        center = new Pixel[getK()];
        try {
            File file = new File(filename);
            Scanner scan = new Scanner(file);
            int count = 0;
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] array = line.split(",");
                Pixel pi = new Pixel(Double.parseDouble(array[0]),
                        Double.parseDouble(array[1]), Double.parseDouble(array[2]));
                center[count] = pi;
                count++;

            }
        } catch (FileNotFoundException | NumberFormatException e) {
            e.getStackTrace();
        }
        return center;
    }

    public Pixel[][] phancum(Pixel[] arr, double[][] u, double[][] e, Pixel[] center,int id) {

        for (int i = 0; i < arr.length; i++) {
            int t = 0;
            double m,max = 0;
            for (int j = 0; j < Define.NUM_CLUSTER; j++) {

                m = u[i][j] * (2 - e[i][j]);
                if (m > max) {
                    max = m;
                    t = j; // luu lai chi so cum.
                }
            }
            // Set lai mau cho cac cum,moi cum 1 mau.
            //Biet co bao nhieu cum de cho t == ??
            
            arr[i] = new Pixel(center[t].getRed(), center[t].getGreen(), center[t].getBlue());
        }
        
        FC_PFS2.writeFile(Define.BASEDPATH +"/result_id"+ id +".txt", arr);
        result = new Pixel[row][column];
        int count = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (count < arr.length) {
                    result[j][i] = arr[count];
                    count++;
                }
            }
        }
//        Data.setImage(result);
        return result;
    }

    /**
     * @return the k
     */
    public int getK() {
        return k;
    }

    /**
     * @param k the k to set
     */
    public void setK(int k) {
        this.k = k;
    }

}
