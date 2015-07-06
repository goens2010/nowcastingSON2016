package control;

//package weather_nowcasting;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class FCM {

    //private static final String FILE_PAR = "out_put2.txt";

    public static int N = 4; // number of elements
    public int d; // dimension.
    public static int C = 4; // number of clusters
    public static int M = 2; // the fuzzifier.
    public static double eps = 0.01; // epsilon.
    public static double maxStep = 1000; // maximun iteration.
    static Pixel[] matrix;
   

    public FCM() {
        this.N = 10000;
        this.C = 4;
        this.M = 2;
        this.eps = 0.01;
        this.maxStep = 1000;
    }

    public FCM(int c) {
        this.C = c;
    }

    public static int getN() {
        return N;
    }

    public static int getC() {
        return C;
    }

    public static int getM() {
        return M;
    }

    public static double getEps() {
        return eps;
    }

    public static double getMaxStep() {
        return maxStep;
    }
    // Doc mang gia tri pixel tu tep txt

    private static Scanner scan;

    public static Pixel[] readData(String filename) {
        try {
            File input = new File(filename);
            scan = new Scanner(input);
            int count = 0;
            int row = Integer.parseInt(scan.nextLine());
            int column = Integer.parseInt(scan.nextLine());
            N = row * column;
            matrix = new Pixel[row * column];
            //matrix =  new Pixel[100*100];
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] array = line.split(",");

                for (String array1 : array) {
                    String[] a = array1.split(" ");
                    String m = a[0];
                    String n = a[1];
                    String p = a[2];
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

    // Khoang cach giua 2 vecto.
    public double distance(Pixel a, Pixel b) {
        double m = (a.getRed() - b.getRed()) * (a.getRed() - b.getRed());
        double n = (a.getGreen() - b.getGreen()) * (a.getGreen() - b.getGreen());
        double p = (a.getBlue() - b.getBlue()) * (a.getBlue() - b.getBlue());
        double tong = m + n + p;
        return tong;
    }

    public Pixel nhanPixel(double a, Pixel v) {
        double m = a * v.getRed();
        double n = a * v.getGreen();
        double p = a * v.getBlue();
        Pixel t = new Pixel(m, n, p);
        return t;
    }

    public Pixel chiaPixel(Pixel v, double a){
        double m = v.getRed() / a;
        double n = v.getGreen() / a;
        double p = v.getBlue() / a;
        Pixel t = new Pixel(m, n, p);
        return t;
    }

    public Pixel congPixel(Pixel a, Pixel b) {
        double m = a.getRed() + b.getRed();
        double n = a.getGreen() + b.getGreen();
        double p = a.getBlue() + b.getBlue();
        Pixel t = new Pixel(m, n, p);
        return t;

    }

    public void FCM(int id) {
        Pixel[] matrix = readData(Define.BASEDPATH +"/out_put_id"+ id+ ".txt");
        int N = matrix.length;

        double[][] u = new double[N][C];
       // double[][] y = new double[N][C];
       // double[][] e = new double[N][C];
        Pixel[] center = new Pixel[C];
        double tong, b, a = 0, a1 = 0;
        double R, G, B;
        int t = 0;
        Random randomGenerator = new Random();

        //maxStep = 1000;
        // Random cac gia tri degree of members.
        for (int k = 0; k < N; k++) {
            for (int j = 0; j < C; j++) {
                u[k][j] = (randomGenerator.nextDouble() * 1);
//                y[k][j] = (randomGenerator.nextDouble() * (1 - u[k][j]));
//                e[k][j] = (randomGenerator.nextDouble() * (1 - u[k][j] - y[k][j]));
            }
        }

        double max1, max2, max3;
        do {

            t = t + 1;

            // Tinh trung tam cum Vj.
            for (int j = 0; j < C; j++) {
                Pixel v = new Pixel();
                double sum = 0;
                for (int k = 0; k < N; k++) {
                    b = Math.pow(u[k][j], M);
                    sum = sum + b;
                    Pixel p = nhanPixel(b, matrix[k]);
                    v = congPixel(v, p);
                }
                if (sum == 0) {
                    center[j] = new Pixel();
                } else {
                    center[j] = chiaPixel(v, sum);
                }
            }
            /*
             * for(Pixel pi : center){ System.out.println(pi.toString()); }
             */

            // Tih u_kj
            max1 = 0;
           

            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    a = distance(matrix[k], center[j]);
                    tong = 0;
                    for (int i = 0; i < C; i++) {
                        b = distance(matrix[k], center[i]);
                        if (b == 0) {
//                            System.out.println("Matrix" + k + " - " + matrix[k].toString() + "   k" + k);
                            //System.out.println("Center" + i + " - " +center[i].toString());
//                            System.out.println("");

                            System.out.println("Division by zero");
                            tong = tong + 0;
                        } else {
                            tong = tong + Math.pow(Math.abs(a / b), 1 / (M - 1));
                        }

                    }
                    if (tong < 1) {
                        System.out.println("division by zero in ..");
                        tong = 1 + eps;
                    }
                    if (k == 0 && j == 0) {
                       // System.out.println("tong " + tong);
                    }
                    a1 = 1 / tong;
                    a = Math.abs(a1 - u[k][j]);
                    if (max1 < a) {
                        max1 = a;
                    }
                    u[k][j] = a1;
                }
            }
            
            /*if (t % 10 == 0) {
                System.out.println("lan " + t + "  max = " + max1 + "  eps = " + eps);
            }*/

        } while (max1 > eps && t < maxStep);
         
        String filename = Define.BASEDPATH +"/center_id" + id + ".txt";
        writeFile(filename, center);

        filename = Define.BASEDPATH +"/matranU_id"+ id + ".txt";
        writeFile(filename, u, N);

    }

    public void writeFile(String filename, Pixel[] v) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter o = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            PrintWriter pw = new PrintWriter(o, true);
            for (int row = 0; row < v.length; row++) {
                pw.print(v[row].getRed() + ",");
                pw.print(v[row].getGreen() + ",");
                pw.print(v[row].getBlue() + ",");
                pw.println();
            }
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String filename, double[][] arr, int n) {
        OutputStream fstream;
        Writer pw;
        String pattern = "#.###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String str = "";
        try {
            fstream = new FileOutputStream(filename);
            pw = new OutputStreamWriter(fstream, "UTF-8");

            for (int row = 0; row < n; row++) {
                str = decimalFormat.format(arr[row][0]);
                for (int j = 0; j < C - 1; j++) {
                    str += " " + decimalFormat.format(arr[row][j + 1]);
                }
                pw.write(str + "\n");
            }
            pw.close();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

   /* public void writeFile(String filename, double[][] arr, double[][] y, double[][] e, int n) {
        OutputStream fstream;
        Writer pw;
        String pattern = "#.###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String str = "";
        try {
            fstream = new FileOutputStream(filename);
            pw = new OutputStreamWriter(fstream, "UTF-8");

            for (int row = 0; row < n; row++) {
                str = decimalFormat.format(arr[row][0]);
                str += " " + decimalFormat.format(arr[row][1]);
                str += " " + decimalFormat.format(arr[row][2]);
                str += " " + decimalFormat.format(arr[row][3]);
                pw.write(str + "\n");
            }
            pw.write("\n");
            for (int row = 0; row < n; row++) {
                str = decimalFormat.format(arr[row][0]);
                str += " " + decimalFormat.format(y[row][1]);
                str += " " + decimalFormat.format(y[row][2]);
                str += " " + decimalFormat.format(y[row][3]);
                pw.write(str + "\n");
            }
            pw.write("\n");
            for (int row = 0; row < n; row++) {
                str = decimalFormat.format(arr[row][0]);
                str += " " + decimalFormat.format(e[row][1]);
                str += " " + decimalFormat.format(e[row][2]);
                str += " " + decimalFormat.format(e[row][3]);
                pw.write(str + "\n");
            }
            pw.close();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }*/

    public static void main(String[] args) {
        FCM fcm = new FCM();
		//FC_PFS2 fmc = new FC_PFS2(c, m, eps, maxStep);

        /*Pixel[] matrix = fcm.readData(FILE_PAR);
         for(int i = 0; i<1000; i++ ){
         System.out.println(matrix[i].toString());
         }*/
        //System.out.println(matrix[i]);
        //fcm.FCM();

    }

}
