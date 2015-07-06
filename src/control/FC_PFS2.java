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
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class FC_PFS2 {

    //private static final String FILE_PAR = "out_put2.txt";

    public static int N ; // number of elements
    public int d; // dimension.
    public static int C = Define.NUM_CLUSTER; // number of clusters
    public static int M = Define.FUZZIFIER; // the fuzzifier.
    public static double eps = Define.EPS; // epsilon.
    public static double maxStep = Define.maxStep; // maximun iteration.
    static Pixel[] matrix;
    //double a1 = 0, a2 = 0, a3;

    public FC_PFS2() {
        this.N = 10000;
        this.C = 4;
        this.M = 2;
        this.eps = 0.01;
        this.maxStep = 1000;
    }

    public FC_PFS2(int c) {
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

            // for(int i = 0; i< matrix.length; i++){	System.out.println(matrix[i].toString());}
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        }
        return matrix;
    }

    // Khoang cach giua 2 vecto.
    public static double distance(Pixel a, Pixel b) {
        double m = (a.getRed() - b.getRed()) * (a.getRed() - b.getRed());
        double n = (a.getGreen() - b.getGreen()) * (a.getGreen() - b.getGreen());
        double p = (a.getBlue() - b.getBlue()) * (a.getBlue() - b.getBlue());
        double tong = m + n + p;
        return tong;
    }

    public static Pixel nhanPixel(double a, Pixel v) {
        double m = a * v.getRed();
        double n = a * v.getGreen();
        double p = a * v.getBlue();
        Pixel t = new Pixel(m, n, p);
        return t;
    }

    public static Pixel chiaPixel(Pixel v, double a){
        double m = v.getRed() / a;
        double n = v.getGreen() / a;
        double p = v.getBlue() / a;
        Pixel t = new Pixel(m, n, p);
        return t;
    }

    public static Pixel congPixel(Pixel a, Pixel b) {
        double m = a.getRed() + b.getRed();
        double n = a.getGreen() + b.getGreen();
        double p = a.getBlue() + b.getBlue();
        Pixel t = new Pixel(m, n, p);
        return t;

    }

    public static void FC_PFS(Pixel [][] array) {
    	Pixel[] matrix = new Pixel[array.length * array[0].length];
    	//System.out.println(matrix.length);
    	int count = 0;
    	for(int i = 0; i < array.length; i++ ){
    		for(int j = 0; j < array[0].length; j++){
    			matrix[count] = array[i][j];
    			//System.out.println(matrix[count].toString());
    			count ++;
    		}
    	}
        //Pixel[] matrix = readData(FILE_PAR);
        int N = matrix.length;

        double[][] u = new double[N][C];
        double[][] y = new double[N][C];
        double[][] e = new double[N][C];
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
                y[k][j] = (randomGenerator.nextDouble() * (1 - u[k][j]));
                e[k][j] = (randomGenerator.nextDouble() * (1 - u[k][j] - y[k][j]));
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
                    b = Math.pow(Math.abs(u[k][j] * (2 - e[k][j])), M);
                    sum = sum + b;
                    Pixel p = nhanPixel(b, matrix[k]);
                    v = congPixel(v, p);
                }
                if (sum == 0) {
                    center[j] = new Pixel();
                } else {
                    center[j] = chiaPixel(v, sum);
                    //System.out.println(center[j].toString());
                }
            }
            
             //for(Pixel pi : center){ System.out.println(pi.toString()); }
             

            // Tih u_kj
            max1 = 0;
            max2 = 0;
            max3 = 0;

            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    a = distance(matrix[k], center[j]);
                    tong = 0;
                    for (int i = 0; i < C; i++) {
                        b = distance(matrix[k], center[i]);
                        if (b == 0) {
                            System.out.println("Matrix" + k + " - " + matrix[k].toString() + "   k" + k);
                            //System.out.println("Center" + i + " - " +center[i].toString());
                            System.out.println("");

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
//                        System.out.println("tong " + tong);
                    }
                    a1 = 1 / ((2 - e[k][j]) * tong);
                    a = Math.abs(a1 - u[k][j]);
                    if (max1 < a) {
                        max1 = a;
                    }
                    u[k][j] = a1;
                }
            }
            // Tinh n_kj;
            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    tong = 0;
                    double ep = 0;
                    for (int i = 0; i < C; i++) {
                        tong = tong + e[k][i];
                        ep = ep + Math.pow(Math.E, -e[k][i]);
                    }
                    if (ep == 0) {
                        System.out.println("ep = 0, division by zero");
                        ep = eps;
                    }
                    if (e[k][j] != 0) {
                        a1 = (Math.pow(Math.E, -e[k][j]))
                                * (1 - tong / 4) / ep;
                        a = Math.abs(a1 - y[k][j]);
                    }
                    if (max2 < a) {
                        max2 = a;
                    }
                    y[k][j] = a1;

                }
            }

            // Tinh e_kj;
            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    double kq = u[k][j] + y[k][j];
                    if(kq< 0)
                    {
                        //System.out.println(u[k][j]);
                       // System.out.println(y[k][j]);
                    }
                    double kq1 = Math.pow(Math.abs(kq), 0.5);
                    double kq2 = Math.pow(Math.abs(1 - kq1), 1 / 0.5);

                    a1 = 1 - kq - kq2;
                    a = Math.abs(a1 - e[k][j]);
                    if (max3 < a) {
                        max3 = a;
                    }
                    e[k][j] = a1;
                }

            }
            if (t % 10 == 0) {
                System.out.println("lan " + t + "  max = " + (max1 + max2 + max3) + "  eps = " + eps);
            }

        } while (max1 + max2 + max3 > eps && t < maxStep);

        
        ArrayList<ArrayList<Pixel>> list_cum = new ArrayList<ArrayList<Pixel>>();
        for(int k = 0; k < C; k++){
        	ArrayList<Pixel> pixel = new ArrayList<Pixel>();
        	list_cum.add(pixel);
        }
        
       ArrayList<ArrayList<Double>> list = new ArrayList<ArrayList<Double>>();
       for(int k = 0; k < C; k++){
    	   ArrayList<Double> dothuoc = new ArrayList<Double>();
    	   list.add(dothuoc);
       }
		 for (int i = 0; i < N; i++) {
	            int p = 0;
	            double m = 0,max = 0;
	            for (int j = 0; j < C; j++) {

	                m = u[i][j] * (2 - e[i][j]);
	                if (m > max) {
	                    max = m;
	                    p = j; // luu lai chi so cum.
	                }
	            }
	            //list_phu.add(matrix[i]);
	            list_cum.get(p).add(matrix[i]);
	            //list.get(p).add(matrix[i].getRed());
	           // list.get(p).add(matrix[i].getGreen());
	           // list.get(p).add(matrix[i].getBlue());
	            list.get(p).add(m);
	            // luu lai do thuoc cua diem anh i vào cum p. u[i][p]
		 }
      /* for(int i = 0; i < 10; i++){
    	   for(int j = 0; j < list_cum.get(i).size(); j ++){
    		   System.out.println(list_cum.get(i).get(j).toString());
    	   }
       }*/
        String filename = Define.path + "\\center_rule.txt";
        writeFile(filename, center);

        filename = Define.path + "\\matranU_rule.txt";
        writeFile(filename, u, N);

        filename = Define.path + "\\matranE_rule.txt";
        writeFile(filename, e, N);
        
        filename = Define.path + "\\matranN_rule.txt";
        writeFile(filename, y, N);
        
        for(int i = 0; i < C; i++){
        	 filename = Define.path + "\\cum_" + i+ ".txt";
        	String filename2 = Define.path + "\\U_" + i+ ".txt";
        	writeFile(filename, list_cum.get(i));
        	writeFile2(filename2, list.get(i));
        }
    }
    
    public static void FC_PFS(Pixel [][] array, int C) {
    	Pixel[] matrix = new Pixel[array.length * array[0].length];
    	//System.out.println(matrix.length);
    	int count = 0;
    	for(int i = 0; i < array.length; i++ ){
    		for(int j = 0; j < array[0].length; j++){
    			matrix[count] = array[i][j];
    			//System.out.println(matrix[count].toString());
    			count ++;
    		}
    	}
        //Pixel[] matrix = readData(FILE_PAR);
        int N = matrix.length;

        double[][] u = new double[N][C];
        double[][] y = new double[N][C];
        double[][] e = new double[N][C];
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
                y[k][j] = (randomGenerator.nextDouble() * (1 - u[k][j]));
                e[k][j] = (randomGenerator.nextDouble() * (1 - u[k][j] - y[k][j]));
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
                    b = Math.pow(Math.abs(u[k][j] * (2 - e[k][j])), M);
                    sum = sum + b;
                    Pixel p = nhanPixel(b, matrix[k]);
                    v = congPixel(v, p);
                }
                if (sum == 0) {
                    center[j] = new Pixel();
                } else {
                    center[j] = chiaPixel(v, sum);
                    //System.out.println(center[j].toString());
                }
            }
            
             //for(Pixel pi : center){ System.out.println(pi.toString()); }
             

            // Tih u_kj
            max1 = 0;
            max2 = 0;
            max3 = 0;

            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    a = distance(matrix[k], center[j]);
                    tong = 0;
                    for (int i = 0; i < C; i++) {
                        b = distance(matrix[k], center[i]);
                        if (b == 0) {
                            System.out.println("Matrix" + k + " - " + matrix[k].toString() + "   k" + k);
                            //System.out.println("Center" + i + " - " +center[i].toString());
                            System.out.println("");

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
//                        System.out.println("tong " + tong);
                    }
                    a1 = 1 / ((2 - e[k][j]) * tong);
                    a = Math.abs(a1 - u[k][j]);
                    if (max1 < a) {
                        max1 = a;
                    }
                    u[k][j] = a1;
                }
            }
            // Tinh n_kj;
            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    tong = 0;
                    double ep = 0;
                    for (int i = 0; i < C; i++) {
                        tong = tong + e[k][i];
                        ep = ep + Math.pow(Math.E, -e[k][i]);
                    }
                    if (ep == 0) {
                        System.out.println("ep = 0, division by zero");
                        ep = eps;
                    }
                    if (e[k][j] != 0) {
                        a1 = (Math.pow(Math.E, -e[k][j]))
                                * (1 - tong / 4) / ep;
                        a = Math.abs(a1 - y[k][j]);
                    }
                    if (max2 < a) {
                        max2 = a;
                    }
                    y[k][j] = a1;

                }
            }

            // Tinh e_kj;
            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    double kq = u[k][j] + y[k][j];
                    if(kq< 0)
                    {
                        //System.out.println(u[k][j]);
                       // System.out.println(y[k][j]);
                    }
                    double kq1 = Math.pow(Math.abs(kq), 0.5);
                    double kq2 = Math.pow(Math.abs(1 - kq1), 1 / 0.5);

                    a1 = 1 - kq - kq2;
                    a = Math.abs(a1 - e[k][j]);
                    if (max3 < a) {
                        max3 = a;
                    }
                    e[k][j] = a1;
                }

            }
            if (t % 10 == 0) {
                System.out.println("lan " + t + "  max = " + (max1 + max2 + max3) + "  eps = " + eps);
            }

        } while (max1 + max2 + max3 > eps && t < maxStep);

        
        ArrayList<ArrayList<Pixel>> list_cum = new ArrayList<ArrayList<Pixel>>();
        for(int k = 0; k < C; k++){
        	ArrayList<Pixel> pixel = new ArrayList<Pixel>();
        	list_cum.add(pixel);
        }
        
       ArrayList<ArrayList<Double>> list = new ArrayList<ArrayList<Double>>();
       for(int k = 0; k < C; k++){
    	   ArrayList<Double> dothuoc = new ArrayList<Double>();
    	   list.add(dothuoc);
       }
		 for (int i = 0; i < N; i++) {
	            int p = 0;
	            double m = 0,max = 0;
	            for (int j = 0; j < C; j++) {

	                m = u[i][j] * (2 - e[i][j]);
	                if (m > max) {
	                    max = m;
	                    p = j; // luu lai chi so cum.
	                }
	            }
	            //list_phu.add(matrix[i]);
	            list_cum.get(p).add(matrix[i]);
	            //list.get(p).add(matrix[i].getRed());
	           // list.get(p).add(matrix[i].getGreen());
	           // list.get(p).add(matrix[i].getBlue());
	            list.get(p).add(m);
	            // luu lai do thuoc cua diem anh i vào cum p. u[i][p]
		 }
      /* for(int i = 0; i < 10; i++){
    	   for(int j = 0; j < list_cum.get(i).size(); j ++){
    		   System.out.println(list_cum.get(i).get(j).toString());
    	   }
       }*/
        String filename = Define.path + "\\center_PS.txt";
        writeFile(filename, center);

        filename = Define.path + "\\matranU_PS.txt";
        writeFile(filename, u, N,C);

        filename = Define.path + "\\matranE_PS.txt";
        writeFile(filename, e, N,C);
        
        filename = Define.path + "\\matranN_PS.txt";
        writeFile(filename, y, N,C);
        
        for(int i = 0; i < C; i++){
        	 filename = Define.path + "\\cum_" + i+ ".txt";
        	String filename2 = Define.path + "\\U_" + i+ ".txt";
        	writeFile(filename, list_cum.get(i));
        	writeFile2(filename2, list.get(i));
        }
    }
    
    public static void FC_PFS(Pixel [][] array, int C,int id) {
    	Pixel[] matrix = new Pixel[array.length * array[0].length];
    	//System.out.println(matrix.length);
    	int count = 0;
    	for(int i = 0; i < array.length; i++ ){
    		for(int j = 0; j < array[0].length; j++){
    			matrix[count] = array[i][j];
    			//System.out.println(matrix[count].toString());
    			count ++;
    		}
    	}
        //Pixel[] matrix = readData(FILE_PAR);
        int N = matrix.length;

        double[][] u = new double[N][C];
        double[][] y = new double[N][C];
        double[][] e = new double[N][C];
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
                y[k][j] = (randomGenerator.nextDouble() * (1 - u[k][j]));
                e[k][j] = (randomGenerator.nextDouble() * (1 - u[k][j] - y[k][j]));
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
                    b = Math.pow(Math.abs(u[k][j] * (2 - e[k][j])), M);
                    sum = sum + b;
                    Pixel p = nhanPixel(b, matrix[k]);
                    v = congPixel(v, p);
                }
                if (sum == 0) {
                    center[j] = new Pixel();
                } else {
                    center[j] = chiaPixel(v, sum);
                    //System.out.println(center[j].toString());
                }
            }
            
             //for(Pixel pi : center){ System.out.println(pi.toString()); }
             

            // Tih u_kj
            max1 = 0;
            max2 = 0;
            max3 = 0;

            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    a = distance(matrix[k], center[j]);
                    tong = 0;
                    for (int i = 0; i < C; i++) {
                        b = distance(matrix[k], center[i]);
                        if (b == 0) {
                            System.out.println("Matrix" + k + " - " + matrix[k].toString() + "   k" + k);
                            //System.out.println("Center" + i + " - " +center[i].toString());
                            System.out.println("");

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
//                        System.out.println("tong " + tong);
                    }
                    a1 = 1 / ((2 - e[k][j]) * tong);
                    a = Math.abs(a1 - u[k][j]);
                    if (max1 < a) {
                        max1 = a;
                    }
                    u[k][j] = a1;
                }
            }
            // Tinh n_kj;
            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    tong = 0;
                    double ep = 0;
                    for (int i = 0; i < C; i++) {
                        tong = tong + e[k][i];
                        ep = ep + Math.pow(Math.E, -e[k][i]);
                    }
                    if (ep == 0) {
                        System.out.println("ep = 0, division by zero");
                        ep = eps;
                    }
                    if (e[k][j] != 0) {
                        a1 = (Math.pow(Math.E, -e[k][j]))
                                * (1 - tong / 4) / ep;
                        a = Math.abs(a1 - y[k][j]);
                    }
                    if (max2 < a) {
                        max2 = a;
                    }
                    y[k][j] = a1;

                }
            }

            // Tinh e_kj;
            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    double kq = u[k][j] + y[k][j];
                    if(kq< 0)
                    {
                        //System.out.println(u[k][j]);
                       // System.out.println(y[k][j]);
                    }
                    double kq1 = Math.pow(Math.abs(kq), 0.5);
                    double kq2 = Math.pow(Math.abs(1 - kq1), 1 / 0.5);

                    a1 = 1 - kq - kq2;
                    a = Math.abs(a1 - e[k][j]);
                    if (max3 < a) {
                        max3 = a;
                    }
                    e[k][j] = a1;
                }

            }
            if (t % 10 == 0) {
                System.out.println("lan " + t + "  max = " + (max1 + max2 + max3) + "  eps = " + eps);
            }

        } while (max1 + max2 + max3 > eps && t < maxStep);

        
        ArrayList<ArrayList<Pixel>> list_cum = new ArrayList<ArrayList<Pixel>>();
        for(int k = 0; k < C; k++){
        	ArrayList<Pixel> pixel = new ArrayList<Pixel>();
        	list_cum.add(pixel);
        }
        
       ArrayList<ArrayList<Double>> list = new ArrayList<ArrayList<Double>>();
       for(int k = 0; k < C; k++){
    	   ArrayList<Double> dothuoc = new ArrayList<Double>();
    	   list.add(dothuoc);
       }
		 for (int i = 0; i < N; i++) {
	            int p = 0;
	            double m = 0,max = 0;
	            for (int j = 0; j < C; j++) {

	                m = u[i][j] * (2 - e[i][j]);
	                if (m > max) {
	                    max = m;
	                    p = j; // luu lai chi so cum.
	                }
	            }
	            //list_phu.add(matrix[i]);
	            list_cum.get(p).add(matrix[i]);
	            //list.get(p).add(matrix[i].getRed());
	           // list.get(p).add(matrix[i].getGreen());
	           // list.get(p).add(matrix[i].getBlue());
	            list.get(p).add(m);
	            // luu lai do thuoc cua diem anh i vào cum p. u[i][p]
		 }
      /* for(int i = 0; i < 10; i++){
    	   for(int j = 0; j < list_cum.get(i).size(); j ++){
    		   System.out.println(list_cum.get(i).get(j).toString());
    	   }
       }*/
        String filename = Define.path + "\\center_PS_id"+id+".txt";
        writeFile(filename, center);

        filename = Define.path + "\\U_PS_id"+id+".txt";
        writeFile(filename, u, N,C);

        filename = Define.path + "\\E_PS_id"+id+".txt";
        writeFile(filename, e, N,C);
        
        filename = Define.path + "\\matranN_PS_id"+id+" .txt";
        writeFile(filename, y, N,C);
        
        for(int i = 0; i < C; i++){
        	 filename = Define.path + "\\cum_" + i+ ".txt";
        	String filename2 = Define.path + "\\U_" + i+ ".txt";
        	writeFile(filename, list_cum.get(i));
        	writeFile2(filename2, list.get(i));
        }
    }
    public static void FC_PFS(int id) {
        Pixel[] matrix = readData( Define.BASEDPATH +"\\out_put2_id"+ id + ".txt");
        int N = matrix.length;

        double[][] u = new double[N][C];
        double[][] y = new double[N][C];
        double[][] e = new double[N][C];
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
                y[k][j] = (randomGenerator.nextDouble() * (1 - u[k][j]));
                e[k][j] = (randomGenerator.nextDouble() * (1 - u[k][j] - y[k][j]));
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
                    b = Math.pow(Math.abs(u[k][j] * (2 - e[k][j])), M);
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
            max2 = 0;
            max3 = 0;

            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    a = distance(matrix[k], center[j]);
                    tong = 0;
                    for (int i = 0; i < C; i++) {
                        b = distance(matrix[k], center[i]);
                        if (b == 0) {
                            //System.out.println("Matrix" + k + " - " + matrix[k].toString() + "   k" + k);
                            //System.out.println("Center" + i + " - " +center[i].toString());
                            //System.out.println("");

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
//                        System.out.println("tong " + tong);
                    }
                    a1 = 1 / ((2 - e[k][j]) * tong);
                    a = Math.abs(a1 - u[k][j]);
                    if (max1 < a) {
                        max1 = a;
                    }
                    u[k][j] = a1;
                }
            }
            // Tinh n_kj;
            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    tong = 0;
                    double ep = 0;
                    for (int i = 0; i < C; i++) {
                        tong = tong + e[k][i];
                        ep = ep + Math.pow(Math.E, -e[k][i]);
                    }
                    if (ep == 0) {
                        System.out.println("ep = 0, division by zero");
                        ep = eps;
                    }
                    if (e[k][j] != 0) {
                        a1 = (Math.pow(Math.E, -e[k][j]))
                                * (1 - tong / 4) / ep;
                        a = Math.abs(a1 - y[k][j]);
                    }
                    if (max2 < a) {
                        max2 = a;
                    }
                    y[k][j] = a1;

                }
            }

            // Tinh e_kj;
            for (int j = 0; j < C; j++) {
                for (int k = 0; k < N; k++) {
                    double kq = u[k][j] + y[k][j];
                    if(kq< 0)
                    {
                        //System.out.println(u[k][j]);
                       // System.out.println(y[k][j]);
                    }
                    double kq1 = Math.pow(Math.abs(kq), 0.5);
                    double kq2 = Math.pow(Math.abs(1 - kq1), 1 / 0.5);

                    a1 = 1 - kq - kq2;
                    a = Math.abs(a1 - e[k][j]);
                    if (max3 < a) {
                        max3 = a;
                    }
                    e[k][j] = a1;
                }

            }
            /*if (t % 10 == 0) {
                System.out.println("lan " + t + "  max = " + (max1 + max2 + max3) + "  eps = " + eps);
            }*/

        } while (max1 + max2 + max3 > eps && t < maxStep);

        /*String filename = "result.txt";
         writeFile(filename, u,y,e,n);
		
         filename = "center.txt";
         writeFile(filename, center);
         */
       
        String filename =  Define.BASEDPATH +"\\center_id" + id + ".txt";
        writeFile(filename, center);

        filename = Define.BASEDPATH +"\\matranU_id"+ id + ".txt";
        writeFile(filename, u, N);

        filename = Define.BASEDPATH +"\\matranE_id" + id + ".txt";
        writeFile(filename, e, N);
    }

    public static void writeFile(String filename, Pixel[] v) {
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
    public static void writeFile(String filename, ArrayList<Pixel> v) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter o = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            PrintWriter pw = new PrintWriter(o, true);
            pw.println(v.size());
            	for(int col = 0; col < v.size(); col ++){
	                pw.print(v.get(col).getRed() + ",");
	                pw.print(v.get(col).getGreen() + ",");
	                pw.print(v.get(col).getBlue() + ",");
	                pw.println();
            	}
            	
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeFile2(String filename, ArrayList<Double> a) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
           /* String pattern = "#.###";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);*/
            BufferedWriter o = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            PrintWriter pw = new PrintWriter(o, true);
            pw.println(a.size());
            	for(int col = 0; col < a.size(); col ++){
	                pw.println(a.get(col)+" ");
	               
            	}
            	
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String filename, double[][] arr, int n) {
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
    public static void writeFile(String filename, double[][] arr, int n,int C) {
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

    public void writeFile(String filename, double[][] arr, double[][] y, double[][] e, int n) {
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
    }

    public static void main(String[] args) {
        FC_PFS2 fcm = new FC_PFS2();
        
        
		//FC_PFS2 fmc = new FC_PFS2(c, m, eps, maxStep);

        /*Pixel[] matrix = fcm.readData(FILE_PAR);
         for(int i = 0; i<1000; i++ ){
         System.out.println(matrix[i].toString());
         }*/
        //System.out.println(matrix[i]);
        //fcm.FC_PFS();

    }

}
