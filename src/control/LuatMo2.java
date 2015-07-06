package control;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class LuatMo2 {

	private static int cluster = Define.NUM_RULES;
	private int number_image = Define.NUM_TRAIN_IMG;
	private static double eps = 0.05;
	private static int maxstep = 1000;
	private static double rmse;
	public static double getRmse() {
		return rmse;
	}
        
	public static double distance(Pixel a, Pixel b) {
		double m = (a.getRed() - b.getRed()) * (a.getRed() - b.getRed());
		double n = (a.getGreen() - b.getGreen())
				* (a.getGreen() - b.getGreen());
		double p = (a.getBlue() - b.getBlue()) * (a.getBlue() - b.getBlue());

		double tong = m + n + p;
		if (tong == 0.0f) {
			System.out.println("pixel a = " + a.toString());
			System.out.println("pixel B = " + b.toString());
			System.out.println("tong" + m + "+" + n + " +" + p);
		}
		return tong;// Math.sqrt(tong);
	}

	public static double distance(Pixel p, double[] b) {
		double m = (p.getRed() - b[0]) * (p.getRed() - b[0]);
		double n = (p.getGreen() - b[1]) * (p.getGreen() - b[1]);
		double t = (p.getBlue() - b[2]) * (p.getBlue() - b[2]);
		double kc = m + n + t;
		if (kc == 0.0f) {
			System.out.println("pixel p = " + p.toString());
			System.out.println("kc = " + m + "+" + n + " +" + t);
		}
		return m + n + t;
	}
	public static double distance(double [] p, double[] b) {
		double m = (p[0] - b[0]) * (p[0] - b[0]);
		double n = (p[1] - b[1]) * (p[1] - b[1]);
		double t = (p[2] - b[2]) * (p[2] - b[2]);
		double kc = m + n + t;
		if (kc == 0.0f) {
			System.out.println("pixel p = " + p.toString());
			System.out.println("kc = " + m + "+" + n + " +" + t);
		}
		return m + n + t;
	}

	public static double[] nhanPixel(double a, double[] b) {
		double[] t = new double[b.length];
		for (int i = 0; i < b.length; i++) {
			t[i] = b[i] * a;
		}
		return t;
	}

	public static double[] chiaP(double a, double[] b) {
		double[] t = new double[b.length];
		for (int i = 0; i < b.length; i++) {
			t[i] = b[i] / a;
		}
		return t;
	}
	public static double[] NhanP(double a, double[] b) {
		double[] t = new double[b.length];
		for (int i = 0; i < b.length; i++) {
			t[i] = b[i] * a;
		}
		return t;
	}

	public static double[] congPixel(double[] a, double[] b) {
		double[] t = new double[b.length];
		for (int i = 0; i < b.length; i++) {
			t[i] = b[i] + a[i];
		}
		return t;
	}

	public static double[] chia(double[] tg1, double[] tg) {
		double[] t = new double[3];
		for (int i = 0; i < tg.length; i++) {
			t[i] = tg1[i] / tg[i];
		}
		return t;
	}

	public static Pixel[] rate(Pixel[] p1, Pixel[] p2) {
		Pixel[] result = new Pixel[p1.length];
		double m = 0, n = 0, p = 0;
		for (int i = 0; i < p1.length; i++) {
			// if(p1[i].getRed() != 0 && p1[i].getGreen() != 0 &&
			// p1[i].getBlue() !=0 ){
			m = (p2[i].getRed() - p1[i].getRed()) / (p1[i].getRed() + 1);
			//m = (p2[i].getRed() - p1[i].getRed());
			//System.out.println("mm = "+m);
			n = (p2[i].getGreen() - p1[i].getGreen()) / (p1[i].getGreen() + 1);
			//n = (p2[i].getGreen() - p1[i].getGreen());
			p = (p2[i].getBlue() - p1[i].getBlue()) / (p1[i].getBlue() + 1);
			//p = (p2[i].getBlue() - p1[i].getBlue());
			result[i] = new Pixel(m, n, p);
		}
		return result;
	}
	public static double[][] rates(Pixel[]p1, Pixel[] p2){
		double [][] result = new double[p1.length][3];
		double m = 0, n = 0, p = 0;
		for (int i = 0; i < p1.length; i++) {
			//m = (p2[i].getRed() - p1[i].getRed()) / (p1[i].getRed() + 1);
			m = (p2[i].getRed() - p1[i].getRed());
			//System.out.println("mm = "+m);
			//n = (p2[i].getGreen() - p1[i].getGreen()) / (p1[i].getGreen() + 1);
			n = (p2[i].getGreen() - p1[i].getGreen());
			//p = (p2[i].getBlue() - p1[i].getBlue()) / (p1[i].getBlue() + 1);
			p = (p2[i].getBlue() - p1[i].getBlue());
			result[i][0] = m;
			result[i][1] = n;
			result[i][2] = p;
		}
		return result;
	}
	public static Pixel[][] training_sple2(int number_image) {

		Pixel[][] training_sample = new Pixel[number_image - 1][Define.IMAGE_SIZE_ROW* Define.IMAGE_SIZE_COL];

		String[] filename = new String[number_image];
		for (int i = 0; i < number_image; i++) {
			filename[i] = Define.path + "/out_put2_id" + i + ".txt";
			//System.out.println("filename : " + filename[i]);
		}

		int i = 0;
		int j = 0;
		while (i < number_image - 1) {
			j = i + 1;
			Pixel[] p1 = FC_PFS2.readData(filename[i]);
			// System.out.println(p1[10].toString());
			Pixel[] p2 = FC_PFS2.readData(filename[j]);
			training_sample[i] = rate(p1, p2);
			/*
			 * for(int t = 0; t < 10000; t ++ ){
			 * System.out.println(training_sample[i][t].toString()); }
			 */
			i++;
		}
		return training_sample;
	}

	// phancum[i][t] : diem anh i thuoc cum t.
	public double[] tinh_a1(double[][][] region){
		double [] a1 = new double[3];
		double min_r = 255, min_g = 255, min_b = 255;
		for (int i = 0; i < region.length; i++) {
			for (int j = 0; j < region[0].length; j++) {
			if (region[i][j][0] < min_r) {
				min_r = region[i][j][0];

			}
			if (region[i][j][1] < min_g) {
				min_g = region[i][j][1];

			}
			if (region[i][j][2] < min_b) {
				min_b = region[i][j][2];

			}
			}
		}
		a1[0] = min_r;
		a1[1] = min_g;
		a1[2] = min_b;
		return a1;
	}
	/*public static double[] tinh_a1(ArrayList<double [][][]>list){
		double [] a1 = new double[3];
		double min_r = 255, min_g = 255, min_b = 255;
		for (int i = 0; i <list.size(); i++) { // i < 3
			for (int j = 0; j < list.get(i).length; j++) {  // j < 25 or 20
				for(int k = 0;k < list.get(i)[0].length; k++){ // k < 25 or 20
					if (list.get(i)[j][k][0] < min_r) {
						min_r = list.get(i)[j][k][0];
		
					}
					if (list.get(i)[j][k][1] < min_g) {
						min_g = list.get(i)[j][k][1];
		
					}
					if (list.get(i)[j][k][2] < min_b) {
						min_b =list.get(i)[j][k][2];
		
					}
				}
			}
		}
		a1[0] = min_r;
		a1[1] = min_g;
		a1[2] = min_b;
		return a1;
	}
	public static double[] tinh_c1(ArrayList<double [][][]>list){
		double [] c1 = new double[3];
		double max_r = 0, max_g = 0, max_b = 0;
		for (int i = 0; i <list.size(); i++) { // i < 3
			for (int j = 0; j < list.get(i).length; j++) {  // j < 25 or 20
				for(int k = 0;k < list.get(i)[0].length; k++){ // k < 25 or 20
					if (list.get(i)[j][k][0] > max_r) {
						max_r = list.get(i)[j][k][0];
		
					}
					if (list.get(i)[j][k][1] > max_g) {
						max_g = list.get(i)[j][k][1];
		
					}
					if (list.get(i)[j][k][2] > max_b) {
						max_b =list.get(i)[j][k][2];
		
					}
				}
			}
		}
		c1[0] = max_r;
		c1[1] = max_g;
		c1[2] = max_b;
		return c1;
	}
	*/
	
	// tinh b
	public static int [][] position(ArrayList<double [][][]> list_u){
		int [][] pos = new int[Define.NUM_RULES][3];
		//System.out.println("len = "+ list_u.get(0)[0][0].length);
		for(int h = 0; h < list_u.get(0)[0][0].length; h++){ // h < 10
			double b = 0;
			for(int i = 0; i < list_u.size(); i++){  // i < 3
			for(int j = 0; j < list_u.get(i).length; j++){ // j < 25;
				for(int t = 0; t < list_u.get(i)[j].length; t++){  // t < 25
					 if(list_u.get(i)[j][t][h] > b){
							b = list_u.get(i)[j][t][h];
							pos[h][0] = i;
							pos[h][1] = j;
							pos[h][2] = t;
						}
					}
				}
			}
		}
		return pos;
	}
	public static double[][] tinh_b(ArrayList<double[][][]>list,int [][]position){
		double[][]b = new double[Define.NUM_RULES][3];
		for(int i = 0; i < position.length; i++){
			for(int j = 0; j < position[i].length; j++){
				b[i][j] = list.get(position[i][0])[position[i][1]][ position[i][2]][j];
			}
		}
		return b;
	}
	/*public static double[][] tinh_Uij(int number_image){
		double [][] u = LuatMo2.readMatrixU(Define.path + "/matranU_rule.txt",number_image);
		double [][] e = LuatMo2.readMatrixU(Define.path + "/matranE_rule.txt",number_image);
		double [][] n = LuatMo2.readMatrixU(Define.path + "/matranN_rule.txt",number_image);
		double[][] Uij = new double[u.length][u[0].length];
		for(int i = 0; i < u.length; i++){
			for(int j = 0; j < u[0].length; j++){
				Uij[i][j] = (u[i][j] + n[i][j])/ (1 + e[i][j]);
			}
		}
		return Uij;
	}*/
	public static double[][] tinh_Ujt(int number_image){
		double [][] u = LuatMo2.readMatrixU(Define.path + "/matranU_rule.txt",number_image);
		double [][] e = LuatMo2.readMatrixU(Define.path + "/matranE_rule.txt",number_image);
		//double [][] n = LuatMo2.readMatrixU(Define.path + "/matranN_rule.txt",number_image);
		double[][] Ujt = new double[u.length][u[0].length];
		for(int i = 0; i < u.length; i++){
			for(int j = 0; j < u[0].length; j++){
				Ujt[i][j] = u[i][j] * (2 - e[i][j]);
				//Ujt[i][j] = (u[i][j]+ n[i][j])* (2 - e[i][j]);
			}
		}
		return Ujt;
	}
	public static double[][] tinh_U1jt(int number_image){  // sử dụng tính a.
		double [][] u = LuatMo2.readMatrixU(Define.path + "/matranU_rule.txt",number_image);
		double [][] e = LuatMo2.readMatrixU(Define.path + "/matranE_rule.txt",number_image);
		double [][] n = LuatMo2.readMatrixU(Define.path + "/matranN_rule.txt",number_image);
		double[][] Ujt = new double[u.length][u[0].length];
		for(int i = 0; i < u.length; i++){
			for(int j = 0; j < u[0].length; j++){
				//Ujt[i][j] = u[i][j] * (2 - e[i][j]);
				Ujt[i][j] = (u[i][j]+ n[i][j])* (2 - e[i][j]); 
			}
		}
		return Ujt;
	}
	
	public static double[][] tinh_a1(ArrayList<double[][][]>list,ArrayList<double[][][]>list_u1,double[][]a){
		double[][] arr = new double[Define.NUM_RULES][3];
		//double m = 0, n = 0, p = 0, m1 = 0, n1 = 0, p1 = 0;
		for(int i = 0; i < Define.NUM_RULES; i++){
			double u0 = -1,u1 = -1, u2 = -1;
			double tg0 = 0,tg1 = 0,tg2 = 0;
			for(int j = 0;j < list.size(); j++){  // j < 3
				for(int k = 0; k < list.get(j).length; k++){  // k < 25
					for(int h = 0; h < list.get(j)[0].length; h++){
						if(list.get(j)[k][h][0] <= a[i][0]){
							if(list_u1.get(j)[k][h][i] > u0){
								u0 = list_u1.get(j)[k][h][i];
								tg0 = list.get(j)[k][h][0];
							}
							//m = m + list.get(j)[k][h][0] * list_u.get(j)[k][h][i];
							//m1 = m1 + list_u.get(j)[k][h][i];
						}
						if(list.get(j)[k][h][1] <= a[i][1]){
							if(list_u1.get(j)[k][h][i] > u1){
								u1 = list_u1.get(j)[k][h][i];
								tg1 = list.get(j)[k][h][1];
							}
						}
						if(list.get(j)[k][h][2] <= a[i][2]){
							if(list_u1.get(j)[k][h][i] > u2){
								u2 = list_u1.get(j)[k][h][i];
								tg2 = list.get(j)[k][h][2];
							}
						}
					}
				}
			}
//			if(m1 == 0 || n1 ==0 || p1 ==0 ) System.out.println("division by zero");
			arr[i][0] = tg0;
			arr[i][1] = tg1;
			arr[i][2] = tg2;
		}
		
		return arr;
		
		/*double[][] a1 = new double[Define.NUM_RULES][3];
		double m = 0, n = 0, p = 0, m1 = 0, n1 = 0, p1 = 0;
		for(int i = 0; i < Define.NUM_RULES;i++){
			for(int j = 0;j < list.size(); j++){
				for(int k = 0; k < list.get(j).length; k++){
					for(int h = 0; h < list.get(j)[0].length; h++){
						if(list.get(j)[k][h][0] <= a[i][0]){
							m += list.get(j)[k][h][0] * list_u1.get(j)[k][h][i];
							m1 += list_u1.get(j)[k][h][i];
						}
						if(list.get(j)[k][h][1] <= a[i][1]){
							n += list.get(j)[k][h][1] * list_u1.get(j)[k][h][i];
							n1 += list_u1.get(j)[k][h][i];
						}
						if(list.get(j)[k][h][2] <= a[i][2]){
							p += list.get(j)[k][h][2] * list_u1.get(j)[k][h][i];
							p1 += list_u1.get(j)[k][h][i];
						}
					}
				}
			}
			a1[i][0] = m / m1;
			a1[i][1] = n / n1;
			a1[i][2] = p / p1;
		}
		
		return a1;*/
	}
	
	public static double[][] tinh_c1(ArrayList<double[][][]>list,ArrayList<double[][][]>list_u1,double[][]c){
		double[][] a = new double[Define.NUM_RULES][3];
		double u0,u1,u2,tg0,tg1,tg2;
		//double m = 0, n = 0, p = 0, m1 = 0, n1 = 0, p1 = 0;
		for(int i = 0; i < Define.NUM_RULES; i++){
			u0 = -1;u1 = -1;u2 = -1;
			tg0 = 0;tg1 = 0;tg2 = 0;
			for(int j = 0;j < list.size(); j++){  // j < 3
				for(int k = 0; k < list.get(j).length; k++){  // k < 25
					for(int h = 0; h < list.get(j)[0].length; h++){
						if(list.get(j)[k][h][0] >= c[i][0]){
							if(list_u1.get(j)[k][h][i] > u0){
								u0 = list_u1.get(j)[k][h][i];
								tg0 = list.get(j)[k][h][0];
								if(tg0 == 0)
									System.out.println(" c : "+ c[i][0]+ "  u = "+ u0+ "  tg0 = "+ tg0);
							}
						}
						if(list.get(j)[k][h][1] >= c[i][1]){
							if(list_u1.get(j)[k][h][i] > u1){
								u1 = list_u1.get(j)[k][h][i];
								tg1 = list.get(j)[k][h][1];
							}
						}
						if(list.get(j)[k][h][2] >= c[i][2]){
							if(list_u1.get(j)[k][h][i] > u2){
								u2 = list_u1.get(j)[k][h][i];
								tg2 = list.get(j)[k][h][2];
							}
						}
					}
				}
			}
//			if(m1 == 0 || n1 ==0 || p1 ==0 ) System.out.println("division by zero");
			if(tg0 == 0)
				System.out.println("tg0 = "+ tg0);
			a[i][0] = tg0;
			if(a[i][0] ==0)
				System.out.println("a[i][0] = 0 vs i = "+ i);
			a[i][1] = tg1;
			a[i][2] = tg2;
		}
		
		return a;
		/*double[][] a = new double[Define.NUM_RULES][3];
		double m = 0, n = 0, p = 0, m1 = 0, n1 = 0, p1 = 0;
		for(int i = 0; i < Define.NUM_RULES;i++){
			for(int j = 0;j < list.size(); j++){
				for(int k = 0; k < list.get(j).length; k++){
					for(int h = 0; h < list.get(j)[0].length; h++){
						if(list.get(j)[k][h][0] >= c[i][0]){
							m += list.get(j)[k][h][0] * list_u1.get(j)[k][h][i];
							m1 += list_u1.get(j)[k][h][i];
						}
						if(list.get(j)[k][h][1] >= c[i][1]){
							n += list.get(j)[k][h][1] * list_u1.get(j)[k][h][i];
							n1 += list_u1.get(j)[k][h][i];
						}
						if(list.get(j)[k][h][2] >= c[i][2]){
							p += list.get(j)[k][h][2] * list_u1.get(j)[k][h][i];
							p1 += list_u1.get(j)[k][h][i];
						}
					}
				}
			}
			a[i][0] = m / m1;
			a[i][1] = n / n1;
			a[i][2] = p / p1;
		}
		
		return a;*/
	}
	
	public static double[][] tinh_c(ArrayList<double[][][]>list,ArrayList<double[][][]>list_u1,double[][]b){
		double[][] a = new double[Define.NUM_RULES][3];
		//double m = 0, n = 0, p = 0, m1 = 0, n1 = 0, p1 = 0;
		for(int i = 0; i < Define.NUM_RULES;i++){
			double m = 0, n = 0, p = 0, m1 = 0, n1 = 0, p1 = 0;
			for(int j = 0;j < list.size(); j++){
				for(int k = 0; k < list.get(j).length; k++){
					for(int h = 0; h < list.get(j)[0].length; h++){
						if(list.get(j)[k][h][0] >= b[i][0]){
							m += list.get(j)[k][h][0] * (list_u1.get(j)[k][h][i]+ Define.EPS);
							m1 += (list_u1.get(j)[k][h][i]+ Define.EPS);
						}
						if(list.get(j)[k][h][1] >= b[i][1]){
							n += list.get(j)[k][h][1] * (list_u1.get(j)[k][h][i]+ Define.EPS);
							n1 += (list_u1.get(j)[k][h][i]+ Define.EPS);
						}
						if(list.get(j)[k][h][2] >= b[i][2]){
							p += list.get(j)[k][h][2] * (list_u1.get(j)[k][h][i]+ Define.EPS);
							p1 += (list_u1.get(j)[k][h][i]+ Define.EPS);
						}
					}
				}
			}
			a[i][0] = m / m1;
			a[i][1] = n / n1;
			a[i][2] = p / p1;
		}
		
		return a;
	}
	
	public static double[][] tinh_a(ArrayList<double[][][]>list,ArrayList<double[][][]>list_u1,double[][]b){
		double[][] a1 = new double[Define.NUM_RULES][3];
		//double m = 0, n = 0, p = 0, m1 = 0, n1 = 0, p1 = 0;
		for(int i = 0; i < Define.NUM_RULES;i++){
			double m = 0, n = 0, p = 0, m1 = 0, n1 = 0, p1 = 0;
			for(int j = 0;j < list.size(); j++){
				for(int k = 0; k < list.get(j).length; k++){
					for(int h = 0; h < list.get(j)[0].length; h++){
						if(list.get(j)[k][h][0] <= b[i][0]){
							m += list.get(j)[k][h][0] * (list_u1.get(j)[k][h][i]+ Define.EPS);
							m1 += (list_u1.get(j)[k][h][i]+ Define.EPS);
						}
						if(list.get(j)[k][h][1] <= b[i][1]){
							n += list.get(j)[k][h][1] * (list_u1.get(j)[k][h][i]+ Define.EPS);
							n1 += (list_u1.get(j)[k][h][i]+ Define.EPS);
						}
						if(list.get(j)[k][h][2] <= b[i][2]){
							p += list.get(j)[k][h][2] * (list_u1.get(j)[k][h][i]+ Define.EPS);
							p1 += (list_u1.get(j)[k][h][i]+ Define.EPS);
						}
					}
				}
			}
			a1[i][0] = m / m1;
			a1[i][1] = n / n1;
			a1[i][2] = p / p1;
		}
		
		return a1;
	}
	
	/*public static double[][] tinh_c(ArrayList<double[][][]>list,ArrayList<double[][][]>list_u,double[][]b){
		double[][] a = new double[Define.NUM_RULES][3];
		double m = 0, n = 0, p = 0, m1 = 0, n1 = 0, p1 = 0;
		for(int i = 0; i < Define.NUM_RULES;i++){
			for(int j = 0;j < list.size(); j++){
				for(int k = 0; k < list.get(j).length; k++){
					for(int h = 0; h < list.get(j)[0].length; h++){
						if(list.get(j)[k][h][0] >= b[i][0]){
							m += list.get(j)[k][h][0] * list_u.get(j)[k][h][i];
							m1 += list_u.get(j)[k][h][i];
						}
						if(list.get(j)[k][h][1] >= b[i][1]){
							n += list.get(j)[k][h][1] * list_u.get(j)[k][h][i];
							n1 += list_u.get(j)[k][h][i];
						}
						if(list.get(j)[k][h][2] >= b[i][2]){
							p += list.get(j)[k][h][2] * list_u.get(j)[k][h][i];
							p1 += list_u.get(j)[k][h][i];
						}
					}
				}
			}
			a[i][0] = m / m1;
			a[i][1] = n / n1;
			a[i][2] = p / p1;
		}
		
		return a;
	}*/
	

	public static double[][] DEF(double[][][] a) {
		double[][] def = new double[Define.NUM_RULES][3];
		for (int i = 0; i < a.length; i++) {
			for(int j = 0; j < a[i].length; j++){
				def[i][j] = (a[i][j][0] + 10* a[i][j][1] +100* a[i][j][2] + 10 * a[i][j][3] + a[i][j][4]) / 122;
//				def[i][j] = (20 * a[i][j][1] + 40* a[i][j][2] + 20 * a[i][j][3] ) / 80;
				//System.out.println("def ="+ def[i][j] );
				/*if(Double.isNaN(def[i][j])){
					System.out.println(" "+a[i][j][0]+" "+a[i][j][1]+" "+a[i][j][2]+" "+a[i][j][3]+" "+a[i][j][4]);
				}*/
			}
		}
		return def;
	}

	// Tinh U_Akj.
	public static double u(double input, double a, double b, double c) {
		double u = 0;
		if (a <= input && input <= b) {
			u = (input - a) / (b - a);
		} else if (b <= input && input <= c) {
			u = (c - input) / (c - b);
		} else
			u = 0;
		return u;
	}

	public static double tong(double input, double a1, double b, double c1) {
		double t = 0;
		if (a1 <= input && input <= b) {
			t = (b - input) / (b - a1);
		} else if (b < input && input < c1) {
			t = (input - b) / (c1 - b);
		} else
			t = 0;
		return t;
	}

	public static double nuy(double u, double tong, double alpha) {
		double tg = Math.pow((1 - u - tong), alpha);
		double tg1 = Math.pow(1 - tg, 1 / alpha);
		return tg1 - u;
	}

	public static double si(double u, double nuy, double alpha) {
		double tg = Math.pow(u + nuy, alpha);
		double tg1 = Math.pow(1 - tg, 1 / alpha);
		return 1 - u - nuy - tg1;
	}

	
	public static double[][] U_Akj22(double[] p, double[][][] rule) { // U_Akj của 1 vùng.
		double[][] U = new double[cluster][3];
		double tg, tg1, n, e;
		// for(int j = 0; j < rule.length; j++){
		for(int i = 0; i < cluster; i++){
			tg = u(p[0], rule[i][0][1], rule[i][0][2], rule[i][0][3]);
			//System.out.println("tg = "+ tg);
			tg1 = tong(p[0], rule[i][0][0], rule[i][0][2], rule[i][0][4]);
			n = nuy(tg, tg1, 2);  
			e = si(tg, n, 2);
			//U[i][0] = (tg + n)/ ((e + 1)); //old
			
			U[i][0] = tg * (2 - e);	
			//U[i][0] = (tg+ n)*(2 - e);
			//System.out.println("U = "+ U[i][0]);
	
			tg = u(p[1], rule[i][1][1], rule[i][1][2], rule[i][1][3]);
			tg1 = tong(p[1], rule[i][1][0], rule[i][1][2], rule[i][1][4]);
			n = nuy(tg, tg1, 2); // alpha = 2..the fuzzifier
			e = si(tg, n, 2);
			//U[i][1] = (tg +n) / ( (e + 1)); //old
			
			U[i][1] = tg * (2 - e);
			//U[i][1] = (tg+ n)*(2 - e);
	
			tg = u(p[2], rule[i][2][1], rule[i][2][2], rule[i][2][3]);
			tg1 = tong(p[2], rule[i][2][0], rule[i][2][2], rule[i][2][4]);
			n = nuy(tg, tg1, 2); // alpha = 2..the fuzzifier
			e = si(tg, n, 2);
			//U[i][2] = (tg + n) / ((e + 1));  //old
			
			U[i][2] = tg* (2 - e);
			//U[i][2] = (tg+ n)*(2 - e);
		 }
		
		return U;
	}

	public double min(double[] u) {
		double min = 1000;
		for (int i = 0; i < u.length; i++) {
			if (u[i] < min) {
				min = u[i];
			}
		}
		return min;
	}
	public static double[] min2(double[][] u) {
		//double min = 1000;
		double[] min = new double[u.length];
		for(int t = 0; t < u.length; t++){
			min[t] = 1000;
			for (int i = 0; i < u[0].length; i++) {
				if (u[t][i] < min[t]) {
					min[t] = u[t][i];
				}
			}
			
		}
		return min;
	}

	
  // đối với 1 phân vùng.
	public static double[][][] tinh_O(ArrayList<double[][][]> list, double[][][] rule,double[][][] rate) {//tinh_O(Pixel[][] arr, double[][][] result,Pixel [] rate)arr: mảng lớn gồm 3 mảng chênh lệch

		
		//double[] a1 = tinh_a1(list);
		//double[] c1 = tinh_c1(list);
		double[][][] output = new double[rate.length][rate[0].length][3];
		//double tg[] = new double[3];
		
	
		//double[][] U = new double[10][3];
		//double[][]def = new double[10][3];
		try {
			//for (int i = 0; i < list.size(); i++) {
				//System.out.println("size ="+ list.size());
			int count = 0;
				for(int j = 0; j < rate.length; j++){
					//System.out.println("length = "+ list.get(i).length);
					for(int j1 = 0; j1 < rate[0].length; j1++){
						double tg1[] = new double[3];
						double tg;//, tg1 = 0;
						double tg3[] = new double[3];
						int flag = 0;
						
						double [][] U = U_Akj22(rate[j][j1], rule);
						//System.out.println("UAkj = "+ U[0][1]);
						double [] Umin = new double[10];
						Umin = min2(U);
						//System.out.println("Umin = "+ Umin[0]);
						double [][] def = DEF(rule);
						
						for(int j2 = 0; j2 < Umin.length; j2 ++){
							if(Umin[j2] > 0){
								//System.out.println("Umin = "+ Umin[j2]);
								flag = 1;
							}
							break;
						}
						if(flag == 1){
							count ++;
							tg = 0;
							for(int j3 = 0; j3 < Umin.length; j3++){
								tg = tg + Umin[j3];
								for (int t = 0; t < 3; t++) {
									//System.out.println("Umin = "+ Umin[j3]+ "def = "+ def[j3][t]);
									tg1[t] = tg1[t] + Umin[j3] * def[j3][t];
									//System.out.println(" tg1 = " + tg1[t]);
								}
							}
							if(tg == 0){
								System.out.println("tg = "+ tg);
							}
							//if(tg != 0){
							output[j][j1] = chiaP(tg, tg1);
							if(Double.isInfinite(output[j][j1][0]) || Double.isNaN(output[j][j1][0])){
								//System.out.println("Th1: "+ output[j][j1][0]);
							}
							//}else{
							//	output[j][j1] = tg1;
							//}
						}else{
							double tg2 ;
							for(int j3 = 0; j3 < Umin.length; j3++){
								tg2 = 0;
								double a = distance(rate[j][j1], def[j3]); // a != 0
								for(int h = 0; h < list.size(); h++){
									for(int t = 0; t < list.get(h).length; t++){
										for(int t2 = 0; t2 < list.get(h)[0].length; t2++){
											if(rate[j][j1][0] != list.get(h)[t][t2][0]){
												double b = distance(rate[j][j1], list.get(h)[t][t2]); 
												if( b ==0){
													System.out.println("kc b =" + b);
												}// b != 0
												tg2 = tg2 + a / b;
												if(tg2 == 0)
												System.out.println("tg2 = "+ tg2);
										}
										}
									}
								}
								
								if(tg2 == 0){
									System.out.println("tg2 = "+ tg2+ "a = "+ a);
								}
								tg3 = congPixel(tg3, chiaP(tg2, def[j3]));
								
							}
							output[j][j1] = tg3;
					}
				}
				/*for(int i = 0; i < output.length; i++){
					for(int k = 0; k < output[0].length; k++){
						System.out.println(" output = ");
						for(int t = 0; t < 3; t++){
						System.out.print(" "+ output[i][k][t]);
						}
					}
					System.out.println();
				}*/
					
			//}
		}
		//System.out.println("active = "+ count);		
	} catch (ArithmeticException e) {
			System.out.println(" da chia cho 0");
	}
		return output;
	}
/*public static Pixel [][] forecast( double[][][] output,int number_image) {//Pixel[][] array,
		
		Pixel [] M = FC_PFS2.readData(Define.path + "/out_put2_id"+ number_image +".txt");
		//System.out.println("m = "+ M[0].toString());
		//System.out.println("number = "+ number_image);
		double [][][] a = new double [Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		double [][][] b = new double [Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		  Pixel[] M2 = FC_PFS2.readData(Define.path + "/out_put2_id"+ (number_image +1) +".txt");
		//System.out.println("m2 = "+ M2[0].toString());
		double [][][] result = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		Pixel[][] forecast = new Pixel[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
		
		String pattern = "#.###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
		int count = 0;
		rmse = 0;
		//System.out.println("b = "+ b[0][0][1]+ "a = "+ a[0][0][1]+ " "+ a[0][0][2]);
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
				a[i][j][0] = M[count].getRed();
				//a[i][j][0] = M[count].getRed() + 1 ;
				a[i][j][1] = M[count].getGreen();
				//a[i][j][1] = M[count].getGreen()+1;
				a[i][j][2] = M[count].getBlue();
				//a[i][j][2] = M[count].getBlue() +1;
				b[i][j][0] = M2[count].getRed() ;
				b[i][j][1] = M2[count].getGreen() ;
				b[i][j][2] = M2[count].getBlue();
				//System.out.println("b = "+ b[i][j][2]+ "a = "+ a[i][j][2]);
				count++;
			}
		}
		//System.out.println("b = "+ b[0][0][0]+ " "+b[0][0][1]+ " "+ b[0][0][2]);
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
				double  tg = 0,tg1 = 0;
				for(int t = 0; t < 3; t++){
					tg += Math.pow(result[i][j][t]- b[i][j][t], 2);
					//tg = tg + b[i][j][t];
					//result[i][j][t] = a[i][j][t] * (1 + output[i][j][t]) -1;
					//result[i][j][t] = a[i][j][t] + output[i][j][t];
					if(output[i][j][t] < 0){
						result[i][j][t] = Math.floor(output[i][j][t])+a[i][j][t]; 
					}else{
						result[i][j][t] = Math.ceil(output[i][j][t])+a[i][j][t]; 
					}
					tg1 = tg1 + b[i][j][t];
					
					//if(output[i][j][t] != 0)
						//System.out.print("anh goc: "+ a[i][j][t]+" " +" chenh lech: "+ output[i][j][t] + " ");
				}
				//rmse += Math.pow(tg / tg1, 2);
				rmse += tg / tg1;
				//System.out.println();
			}
		}
		rmse = Math.sqrt(rmse / (Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));
		System.out.println("rmse = "+ rmse);
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
				forecast[i][j] = new Pixel();
				forecast[i][j].setRed(result[i][j][0]);
				forecast[i][j].setGreen(result[i][j][1]);
				forecast[i][j].setBlue(result[i][j][2]);
			}
		}
		
		return forecast;
	}*/

	public static double[][][] forecast2( double[][][] output,int number_image) {//Pixel[][] array,
	
	Pixel [] M = FC_PFS2.readData(Define.path + "/out_put2_id"+ number_image +".txt");

	double [][][] a = new double [Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
	double [][][] b = new double [Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
	  Pixel[] M2 = FC_PFS2.readData(Define.path + "/out_put2_id"+ (number_image +1) +".txt");
	double [][][] result = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
	//Pixel[][] forecast = new Pixel[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
	
	String pattern = "#.###";
    DecimalFormat decimalFormat = new DecimalFormat(pattern);
	int count = 0;
	rmse = 0;
	//System.out.println("b = "+ b[0][0][1]+ "a = "+ a[0][0][1]+ " "+ a[0][0][2]);
	for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
		for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
			a[i][j][0] = M[count].getRed();
			//a[i][j][0] = M[count].getRed() + 1 ;
			a[i][j][1] = M[count].getGreen();
			//a[i][j][1] = M[count].getGreen()+1;
			a[i][j][2] = M[count].getBlue();
			//a[i][j][2] = M[count].getBlue() +1;
			b[i][j][0] = M2[count].getRed() ;
			b[i][j][1] = M2[count].getGreen() ;
			b[i][j][2] = M2[count].getBlue();
			//System.out.println("b = "+ b[i][j][2]+ "a = "+ a[i][j][2]);
			count++;
		}
	}
	//System.out.println("b = "+ b[0][0][0]+ " "+b[0][0][1]+ " "+ b[0][0][2]);
	for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
		for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
			double  tg = 0,tg1 = 0;
			for(int t = 0; t < 3; t++){
				//tg += Math.pow(result[i][j][t]- b[i][j][t], 2);
				//tg = tg + b[i][j][t];
				//result[i][j][t] = a[i][j][t] * (1 + output[i][j][t]) -1;
				//result[i][j][t] = a[i][j][t] + output[i][j][t];
				if(output[i][j][t] < 0){
					result[i][j][t] = Math.floor(output[i][j][t])+a[i][j][t]; 
				}else{
					result[i][j][t] = Math.ceil(output[i][j][t])+a[i][j][t]; 
				}
                                
                tg += Math.pow(result[i][j][t]- b[i][j][t], 2);
                                
				tg1 = tg1 + b[i][j][t];
				
				//if(output[i][j][t] != 0)
					//System.out.print("anh goc: "+ a[i][j][t]+" " +" chenh lech: "+ output[i][j][t] + " ");
			}
			//rmse += Math.pow(tg / tg1, 2);
			rmse += tg / tg1;
			//System.out.println();
		}
	}
	rmse = Math.sqrt(rmse / (Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));
	System.out.println("rmse = "+ rmse);
	/*for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
		for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
			forecast[i][j] = new Pixel();
			forecast[i][j].setRed(result[i][j][0]);
			forecast[i][j].setGreen(result[i][j][1]);
			forecast[i][j].setBlue(result[i][j][2]);
		}
	}*/
	
	return result;
}
	
	public double[] readU(String filename) {
		double[] a = null;
		try {
			File file = new File(filename);
			Scanner scan = new Scanner(file);
			int count = 0;
			int num = Integer.parseInt(scan.nextLine());
			a = new double[num];
			while (scan.hasNextLine()) {
				a[count] = Double.parseDouble(scan.nextLine());
				count++;
			}
		} catch (FileNotFoundException | NumberFormatException err) {
			err.getStackTrace();
		}
		return a;
	}
	public static double[][] readMatrixU(String filename,int number_image,int Ci){
		double[][] I = null;
		String st;
		String tg;
		StringTokenizer line;
		
		try{
			//File input = new File(filename);
			System.out.println(filename);
			BufferedReader inp = new BufferedReader(new FileReader(filename));
			//row = Integer.parseInt(inp.readLine());
			//column = Integer.parseInt(inp.readLine());
			I = new double[Define.NUM_TRAINING_IMAGE*Define.IMAGE_SIZE_ROW*Define.IMAGE_SIZE_COL][Ci]; // cluster
			int count = 0;
			while ((st = inp.readLine()) != null) {
				String []sts = st.split(" ");
				line = new StringTokenizer(st);				
//				System.out.println(st);
				int t = 0;
/*
				for (int j = 0; j < sts.length; j++) {					
					if(sts[j].contains(",")){						
						tg = sts[j].replace(",",".");
//						System.out.println(sts[j] + " " + tg);
						I[count][j] = Double.parseDouble(tg);
					}else{
						I[count][j] = Double.parseDouble(sts[j]);
					}
				}
*/

				while(line.hasMoreTokens()){
					String s = line.nextToken();
					I[count][t] = Double.parseDouble(s.replace(",", "."));
					t++;
					if(t > Ci){
						System.out.println("out of bound t!");
					}
				}
			
				count++;
/*
				if(count > 9998){
					System.out.println("count out of bound!");
				}
*/				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println(I[1][1]);
		
		return I;
	}
	
	public static double[][] readMatrixU(String filename,int number_image){
		double[][] I = null;
		String st;
		StringTokenizer line;
		
		try{
			//File input = new File(filename);
			BufferedReader inp = new BufferedReader(new FileReader(filename));
			//row = Integer.parseInt(inp.readLine());
			//column = Integer.parseInt(inp.readLine());
			I = new double[(number_image-1)*Define.IMAGE_SIZE_ROW*Define.IMAGE_SIZE_COL][Define.NUM_RULES]; // cluster
			int count = 0;
			while ((st = inp.readLine()) != null) {
				line = new StringTokenizer(st);
				int t = 0;
				while(line.hasMoreTokens()){
					String s = line.nextToken();
					if(s.contains(",")){
						s = s.replace(",",".");
					}
					I[count][t] = Double.parseDouble(s);
					t++;
				}
				count++;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println(I[1][1]);
		
		return I;
	}
	
	public static void writeFile(String filename, double[][] arr) {
		OutputStream fstream;
		Writer pw;
		String pattern = "#.###";
		DecimalFormat decimalFormat = new DecimalFormat(pattern);
		String str = "";
		try {
			fstream = new FileOutputStream(filename);
			pw = new OutputStreamWriter(fstream, "UTF-8");

			for (int row = 0; row < arr.length; row++) {
				str = decimalFormat.format(arr[row][0]);
				for (int j = 0; j < 4; j++) {
					str += "  " + decimalFormat.format(arr[row][j + 1]);
				}
				pw.write(str + "\n");
			}
			pw.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public static void writeFile(String filename, double[][][] arr) {
		OutputStream fstream;
		Writer pw;
		String pattern = "#.###";
		DecimalFormat decimalFormat = new DecimalFormat(pattern);
		String str = "";
		try {
			fstream = new FileOutputStream(filename);
			pw = new OutputStreamWriter(fstream, "UTF-8");

			for (int row = 0; row < arr.length; row++) {
				for(int i = 0; i < arr[0].length;i++){
					str = decimalFormat.format(arr[row][i][0]);
					for (int j = 0; j < arr[0][0].length-1; j++) {
						str += "  " + decimalFormat.format(arr[row][i][j + 1]);
					}
					pw.write(str + "\t");
				}
				pw.write("\n");
			}
			pw.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void writeFile(String filename, ArrayList<double[][][]> list_rule) {
		OutputStream fstream;
		Writer pw;
		String pattern = "#.###";
		DecimalFormat decimalFormat = new DecimalFormat(pattern);
		String str = "";
		try {
			fstream = new FileOutputStream(filename);
			pw = new OutputStreamWriter(fstream, "UTF-8");
			for(int t = 0; t < list_rule.size(); t++){
				pw.write(" Vùng thứ "+ t+ ":");
				pw.write("\n");
				for (int row = 0; row < list_rule.get(t).length; row++) {
					pw.write("Luat "+ row+ ":\n");
					for(int i = 0; i < list_rule.get(t)[0].length;i++){
						str = decimalFormat.format(list_rule.get(t)[row][i][0]);
						for (int j = 0; j < list_rule.get(t)[0][0].length-1; j++) {
							if(j == 1){
								str += " [" + decimalFormat.format(list_rule.get(t)[row][i][j + 1])+ "]";
							}else
							str += "  " + decimalFormat.format(list_rule.get(t)[row][i][j + 1]);
						}
						pw.write(str + "\n");
					}
					pw.write("\n");
				}
				pw.write("\n");
			}
			pw.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void writeFile(String filename, double[] a1, double[] a,
			double[] b, double[] c, double[] c1) {
		OutputStream fstream;
		Writer pw;
		String pattern = "#.###";
		DecimalFormat decimalFormat = new DecimalFormat(pattern);
		String str = "";
		try {
			fstream = new FileOutputStream(filename);
			pw = new OutputStreamWriter(fstream, "UTF-8");//
			for (int i = 0; i < 3; i++) {
				str = decimalFormat.format(a1[i]);
				str += "  " + decimalFormat.format(a[i]) + "  "
						+ decimalFormat.format(b[i]) + "  "
						+ decimalFormat.format(c[i]) + "  "
						+ decimalFormat.format(c1[i]);
				pw.write(str + "\n");
			}
			pw.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static Pixel[] readData(String filename) {
		Pixel[] matrix = null;
		try {
			File input = new File(filename);
			Scanner scan = new Scanner(input);
			int count = 0;
			int row = Integer.parseInt(scan.nextLine());
		
			matrix = new Pixel[row];
			// matrix = new Pixel[100*100];
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] array = line.split(",");
				String m = array[0];
				String n = array[1];
				String p = array[2];
				Pixel pi = new Pixel(Double.parseDouble(array[0]),
						Double.parseDouble(array[1]),
						Double.parseDouble(array[2]));
				matrix[count] = pi;
				count++;
			}
			
		} catch (FileNotFoundException e) {
		}
		return matrix;
	}
	
	public static double [][][] getRegion(int i,int j,int move,double [][][] arr){
		int rstart,rstop,cstart,cstop;
		if(i < move) 
			rstart = 0;
		else{
			int t = i/ move;
			rstart = move * t;
		}
		if(i >= Define.IMAGE_SIZE_ROW - move){
			rstop = Define.IMAGE_SIZE_ROW;
		}else{
			rstop = rstart+ Define.SIZE_ROW;
		}
		
		if(j < move)
			cstart = 0;
		else{
			int t = j / move;
			cstart = move * t;
		}
		if(j >= Define.IMAGE_SIZE_COL - move){
			cstop = 100;
		}else{
			cstop = cstart + Define.SIZE_COL;
		}
		
		
		double [][][] region = new double [rstop-rstart][cstop-cstart][arr[0][0].length];
		for(int t = 0; t < rstop-rstart; t++){
			for(int t1 = 0; t1 < cstop-cstart; t1++){
				for(int t2 = 0; t2 < arr[0][0].length; t2++){
				region[t][t1][t2] = 0;
				//region[t][t1][1] = 0;
				//region[t][t1][2] = 0;
				}
			}
		}
		//System.out.println("rstart = "+ rstart + " rstop = "+ rstop);
		//System.out.println("cstart = "+ cstart + " cstop = "+ cstop);
		int d = 0;
		for(int t = rstart; t < rstop; t++){
			int d1 = 0;
			for(int t1 = cstart; t1 < cstop; t1++){
				
				for(int t2 = 0; t2 < arr[0][0].length; t2++ ){
				//System.out.println("region = " + region[d][d1][0]);
					region[d][d1][t2] = arr[t][t1][t2]; 
				//System.out.println("region after  = " + region[d][d1][0]);
				//region[d][d1][1] = arr[b1][b][1];
				//region[d][d1][2] = arr[b1][b][2];
				}
					d1++;
				//System.out.println("region = "+ region[d][d1][0] +"arr[0] = "+ arr[b1][b][0] );
				//System.out.println("arr i = "+ arr[b1][b]);
			}
			//System.out.println("d = "+d);
			d++;
			
		}
		//System.out.println("r = "+ region.length +" c = "+ region[0].length);
		return region;
	}
	
	// Du lieu input cua tung vung.
	public static ArrayList<double [][][]> getRate(int number_image){ // list gom 3 mang chenh lech
		ArrayList<double [][][]> list = new ArrayList<double [][][]>();
		for(int i = 0; i < number_image -1; i++){
			String filename = Define.path + "/out_put2_id"+ i+ ".txt";
			String filename2 = Define.path + "/out_put2_id" +(i+1) + ".txt";
			double [][] rate = rates(FC_PFS2.readData(filename), FC_PFS2.readData(filename2));
			double [][][] p = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
			int count = 0;
			for(int t = 0; t < Define.IMAGE_SIZE_ROW; t++){
				for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
					p[t][j][0] = rate[count][0];
					p[t][j][1] = rate[count][1];
					p[t][j][2] = rate[count][2];
					count ++;
				}
			}
			list.add(p);
		}
		return list;
	}
	
	public static ArrayList<double [][][]> getRegionImg(double [][][] arr){
		ArrayList<double [][][]> list = new ArrayList<double[][][]>();
		int t = Define.SIZE_COL +Define.MOVE;
		if((Define.SIZE_ROW +Define.MOVE) % 2 == 0 ){
			for(int i = 0; i + Define.MOVE < Define.IMAGE_SIZE_ROW;i += Define.MOVE){
				if(t % 2 == 0){
					for(int j = 0; j+ Define.MOVE < Define.IMAGE_SIZE_COL;j += Define.MOVE){
							double [][][] pi = getRegion(i, j, Define.MOVE, arr);
							list.add(pi);
						}
				}else{
					for(int j = 0; j < Define.IMAGE_SIZE_COL;j += Define.MOVE){
							double [][][] pi = getRegion(i, j, Define.MOVE, arr);
							list.add(pi);
						} 
						
					}
			}
		}else{
			for(int i = 0; i < Define.IMAGE_SIZE_ROW;i += Define.MOVE){
				if(t % 2 == 0){
					for(int j = 0; j+ Define.MOVE < Define.IMAGE_SIZE_COL;j += Define.MOVE){
							double [][][] pi = getRegion(i, j, Define.MOVE, arr);
							list.add(pi);
						}
				}else{
					for(int j = 0; j < Define.IMAGE_SIZE_COL;j += Define.MOVE){
							double [][][] pi = getRegion(i, j, Define.MOVE, arr);
							list.add(pi);
						} 
						
				}
		}
		}
		return list;
	}
	public static ArrayList<ArrayList<double [][][]>> getRegionOfImages2(ArrayList<double [][][]> list,int number_image){
		ArrayList<ArrayList<double [][][]>>  list1 = new ArrayList<ArrayList<double[][][]>>();
		int t = Define.SIZE_COL +Define.MOVE;
		if((Define.SIZE_ROW +Define.MOVE) % 2 == 0 ){
			for(int i = 0; i + Define.MOVE < Define.IMAGE_SIZE_ROW;i += Define.MOVE){
				if(t % 2 == 0){
					for(int j = 0; j+ Define.MOVE < Define.IMAGE_SIZE_COL;j += Define.MOVE){
						ArrayList<double [][][]> li = new ArrayList<double [][][]>();
						for(int k = 0; k < number_image -1; k++){
							double [][][] pi = getRegion(i, j, Define.MOVE, list.get(k));
							li.add(pi);
						} 
						list1.add(li); //// list nay chua 25 list con,moi list con la 1 list chua 3 phan vung o cung vi tri cua 3 mang chenh lech
					//if(j+Define.SIZE_COL < Define.IMAGE_SIZE_COL)
						
					}
				}else{
					for(int j = 0; j < Define.IMAGE_SIZE_COL;j += Define.MOVE){
						ArrayList<double [][][]> li = new ArrayList<double [][][]>();
						for(int k = 0; k < number_image -1; k++){
							double [][][] pi = getRegion(i, j, Define.MOVE, list.get(k));
							li.add(pi);
						} 
						list1.add(li); //// list nay chua 25 list con,moi list con la 1 list chua 3 phan vung o cung vi tri cua 3 mang chenh lech
					//if(j+Define.SIZE_COL < Define.IMAGE_SIZE_COL)
						
					}
				}
			//if(i + Define.SIZE_ROW < Define.IMAGE_SIZE_ROW)
				//i += Define.MOVE;
			}
		}else{
			for(int i = 0; i < Define.IMAGE_SIZE_ROW;i += Define.MOVE){
				if(t % 2 == 0){
					for(int j = 0; j+ Define.MOVE < Define.IMAGE_SIZE_COL;j += Define.MOVE){
						ArrayList<double [][][]> li = new ArrayList<double [][][]>();
						for(int k = 0; k < number_image -1; k++){
							double [][][] pi = getRegion(i, j, Define.MOVE, list.get(k));
							li.add(pi);
						} 
						list1.add(li); //// list nay chua 25 list con,moi list con la 1 list chua 3 phan vung o cung vi tri cua 3 mang chenh lech
					//if(j+Define.SIZE_COL < Define.IMAGE_SIZE_COL)
						
					}
				}else{
					for(int j = 0; j < Define.IMAGE_SIZE_COL;j += Define.MOVE){
						ArrayList<double [][][]> li = new ArrayList<double [][][]>();
						for(int k = 0; k < number_image -1; k++){
							double [][][] pi = getRegion(i, j, Define.MOVE, list.get(k));
							li.add(pi);
						} 
						list1.add(li); //// list nay chua 25 list con,moi list con la 1 list chua 3 phan vung o cung vi tri cua 3 mang chenh lech
					//if(j+Define.SIZE_COL < Define.IMAGE_SIZE_COL)
						
					}
				}
			//if(i + Define.SIZE_ROW < Define.IMAGE_SIZE_ROW)
				//i += Define.MOVE;
			}
		}
		System.out.println("list1_size = "+ list1.size());
		return list1; 
	} 
	// Du lieu ve do thuoc cua tung vung 
	public static ArrayList<double [][][]> getU (int number_image){
		ArrayList<double [][][]> list = new ArrayList<double [][][]>();
		/*double [][] u = new double[(number_image-1)*10000][10];
		u =	readMatrixU("matranU_rule.txt",4); */ // test mang u nay
		double [][] u = LuatMo2.tinh_Ujt(number_image);
		double [][][] sub = new double [Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][];
		int num = 0;
		int count = 0;
		while(num < number_image - 1 ){
			for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
				for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
					sub[i][j] = u[count++];
				}
			}
			list.add(sub);
			num++;
		}
		return list;
	}
	public static ArrayList<double [][][]> getU1 (int number_image){
		ArrayList<double [][][]> list = new ArrayList<double [][][]>();
		/*double [][] u = new double[(number_image-1)*10000][10];
		u =	readMatrixU("matranU_rule.txt",4); */ // test mang u nay
		double [][] u = LuatMo2.tinh_U1jt(number_image);
		double [][][] sub = new double [Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][];
		int num = 0;
		int count = 0;
		while(num < number_image - 1 ){
			for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
				for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
					sub[i][j] = u[count++];
				}
			}
			list.add(sub);
			num++;
		}
		return list;
	}
	
	public static ArrayList<double [][][]> getData (int number_image,String filename){
		ArrayList<double [][][]> list = new ArrayList<double [][][]>();
		double [][] u = new double[(number_image-1)*Define.IMAGE_SIZE_COL*Define.IMAGE_SIZE_ROW][10];
		u =	readMatrixU(filename,number_image);  // test mang u 
		double [][][] sub = new double [100][100][];
		int num = 0;
		int count = 0;
		while(num < number_image - 1 ){
			for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
				for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
					sub[i][j] = u[count++];
				}
			}
			list.add(sub);
			num++;
		}
		return list;
	}
	
	public static ArrayList<ArrayList<double [][][]>> getRegionOfDependence(ArrayList<double [][][]> list,int number_image){
		ArrayList<ArrayList<double [][][]>>  list1 = new ArrayList<ArrayList<double[][][]>>();
		for(int i = 0; i+ Define.MOVE < Define.IMAGE_SIZE_ROW; ){
			for(int j = 0; j+ Define.MOVE < Define.IMAGE_SIZE_COL; ){
				ArrayList<double [][][]> li = new ArrayList<double [][][]>();
				for(int k = 0; k < number_image -1; k++){
					double [][][] pi = getRegion(i, j, Define.MOVE, list.get(k));
					li.add(pi);
				} 
			list1.add(li); //// list nay chua 25 list con,moi list con la 1 list chua 3 phan vung o cung vi tri cua 3 mang do thuoc
			//if(j+Define.SIZE_COL < Define.IMAGE_SIZE_COL)
				j += Define.MOVE;
			}
		//if(i + Define.SIZE_ROW < Define.IMAGE_SIZE_ROW)
			i += Define.MOVE;
		}
		return list1; 
	}
	
	public static ArrayList<double[][][]>  luat(ArrayList<ArrayList<double [][][]>> list,ArrayList<ArrayList<double [][][]>> list_u){
		ArrayList<double[][][]> list_rule = new ArrayList<double[][][]>();
		for(int i = 0; i < list.size(); i++ ){ //i < 25
			//System.out.println("vung thu: "+ i);
			double[][][] result = new double[cluster][3][5];
			double [][] b = tinh_b(list.get(i), LuatMo2.position(list_u.get(i)));
			double [][] a = tinh_a(list.get(i), list_u.get(i), b);
			double [][] c = tinh_c(list.get(i), list_u.get(i), b);
			double [][] a1 = tinh_a1(list.get(i), list_u.get(i), a);
			double [][] c1 = tinh_c1(list.get(i),list_u.get(i),c);
			//System.out.println("length = "+ list.get(i).get(0).length);
			for(int j = 0; j < cluster; j++){
				for(int t = 0; t < 3; t++){
					double[] tg1 = {a1[j][t],a[j][t],b[j][t],c[j][t],c1[j][t]};
					result[j][t] = tg1;
				}
				
			}
			list_rule.add(result);
		}
		//writeFile(Define.path + "/luat.txt", list_rule.get(0));
		writeFile(Define.path+ "/BoLuat.txt", list_rule);
		return list_rule;
	}
	public static double [][][] mergeCol(int index,ArrayList<double [][][]> list_out){
		double [][][] tg1 ;
		int t;
		if(Define.SIZE_COL % 2 == 0){
			t = (Define.IMAGE_SIZE_COL - Define.SIZE_COL)/ Define.MOVE ;
		}else{
			t = (Define.IMAGE_SIZE_COL - Define.SIZE_COL)/ Define.MOVE + 1 ;
		}
		ArrayList<double [][][]> list1 = new ArrayList<double[][][]>();
		for(int i = index; i < index + t ; i++){
			//System.out.println("leng = "+ list_out.get(i).length+ "l2 = "+list_out.get(i)[0].length);
			tg1 = new double[list_out.get(i).length][list_out.get(i)[0].length][3];
			int d = 0;
			for(int r = 0; r < list_out.get(i).length; r++){
				int d1 = 0;
				for(int c = 0; c < list_out.get(i)[r].length; c++){
					if(c < Define.MOVE){
						tg1[d][d1] = list_out.get(i)[r][c];
						//System.out.println("tg1 = "+ tg1[d][d1][0]);
						d1++;
					}
					else{
						for(int j = 0; j < 3; j++){
							tg1[d][d1][j] = (list_out.get(i)[r][c][j] + list_out.get(i+1)[r][Define.SIZE_COL-1-c][j])/2;
						}
						d1++;
					}
				}
				d++;
			}
			
			list1.add(tg1);
		}
		list1.add(list_out.get(index+ t));
		//aftere meger
		//System.out.println("list col = "+ list1.size()+ "list 9 = "+ list_out.get(index+ t)[0].length);
		double[][][] tg2 = afterMegerCol(list1);
		//System.out.println("tg2 = "+ tg2[0][0][0]);
		return tg2;
	}
	public static double[][][] afterMegerCol(ArrayList<double[][][]> list){
		double[][][] row1 = new double[list.get(0).length][Define.IMAGE_SIZE_COL][3];
		//System.out.println("len = "+ list.get(0).length);
		for(int i = 0; i < list.get(0).length; i++){
			for(int j = 0 ; j < Define.SIZE_COL; j++){
				row1[i][j] = list.get(0)[i][j];
				//System.out.println("row = "+ row1[i][j][0]);
			}
		}
		int d1 = Define.SIZE_COL;
		for(int i = 1; i < list.size(); i++){
			//System.out.println("leng "+ i+ "= "+ (list.get(i)[0].length - Define.SAME));
			for(int k = 0; k < list.get(i)[0].length - (Define.SIZE_COL- Define.MOVE); k ++){
				int d = 0;
				for(int j = 0; j < list.get(i).length; j++){
					row1[d][d1] = list.get(i)[j][ k + (Define.SIZE_COL - Define.MOVE)];
					d++;
				}
				//System.out.println("d = "+ d);
				d1++;
			}
			//System.out.println("size = "+ list.get(9).length);
			//System.out.println("d1 = "+ d1);
		}
		/*for(int k = 0; k < list.size(); k++){
			int d = 0;
			for(int i = 0; i < list.get(k).length; i++){
				int d1 = 0;
				for(int j = 0; j < list.get(k)[0].length; j++){
					if(k == 0){
						row1[d][d1] = list.get(0)[i][j];
						//System.out.println("row = " + row1[d][d1][0]);
						d1++;
					}
					else{
						if(j < list.get(k)[0].length - 5){
							row1[d][d1] = list.get(k)[i][j+5];
							d1++;
						}
					}
				}
				d++;
			}
		}*/
		//System.out.println("row = "+ row1[0][0][0]);
		return row1;
	}
	public static ArrayList<double[][][]> mergerList(ArrayList<double [][][]> list_out){
		ArrayList<double[][][]> mergerList = new ArrayList<double[][][]>();
		int t;
		if(Define.SIZE_COL % 2 == 0){
			t = (Define.IMAGE_SIZE_COL - Define.SIZE_COL) / Define.MOVE + 1;
		}else{
			t = (Define.IMAGE_SIZE_COL - Define.SIZE_COL)/ Define.MOVE + 1 + 1;
		}
		for(int i = 0; i < list_out.size(); i += t){
			double [][][] a = mergeCol(i, list_out);
			//System.out.println("a = "+ a[0][0][0]);
			mergerList.add(a);
		}
		//System.out.println("length = "+ mergerList.size());
		return mergerList;
	}
	public static double[][][] megerRow(ArrayList<double[][][]> mergerList){
		double[][][] tg;
		ArrayList<double [][][]> listRow = new ArrayList<double[][][]>();
		for(int i = 0; i < mergerList.size()-1; i++){
			tg = new double[mergerList.get(i).length][mergerList.get(i)[0].length][3];
			int d1 = 0;
			for(int k = 0; k < mergerList.get(i)[0].length; k++){
				int d = 0;
				for(int j = 0; j < mergerList.get(i).length; j++){
					if(j < Define.MOVE){
						tg[d][d1] = mergerList.get(i)[j][k];
						d++;
					}
					else{
						for(int j1 = 0; j1 < 3; j1++){
							tg[d][d1][j1] = (mergerList.get(i)[j][k][j1] + mergerList.get(i+1)[Define.SIZE_ROW - 1 -j][k][j1])/2;
						}
						d++;
					}
				}
				d1++;
			}
			listRow.add(tg);
			//System.out.println("listrow = "+ listRow.get(0)[0][5][0]);
		}
		listRow.add(mergerList.get(mergerList.size()-1));
		//System.out.println("row finish = "+mergerList.get(mergerList.size()-1)[0][0][0] );
		//after merger row
		//System.out.println("size = "+ listRow.size());
		double [][][] tg2 = new double [Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		tg2 = afterMergerRow(listRow);
		/*for(int i = 0; i < 100; i++){
			for(int j = 0; j < 100; j++){
				for(int k = 0; k < 3; k++){
					if(tg2[i][j][k] != 0)
					System.out.print(" "+ tg2[i][j][k]);
				}
				System.out.print(" ");
			}
			System.out.println();
		}*/
		return tg2;
	}
	public static double[][][] afterMergerRow(ArrayList<double [][][]> listRow){
		//System.out.println("size = "+ listRow.size());
		double [][][] output = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		for(int i = 0; i < Define.SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
				output[i][j] = listRow.get(0)[i][j];
				//System.out.println("out = "+ output[i][j][0]);
			}
		}
		int d = Define.SIZE_ROW;
		for(int i = 1; i < listRow.size(); i++){
			
			for(int j = 0; j < listRow.get(i).length - (Define.SIZE_ROW -  Define.MOVE); j++){
				//System.out.println("j = "+j + " length = "+listRow.get(i)[j + Define.SAME].length);
				int d1 = 0;
				for(int k = 0; k < listRow.get(i)[j].length; k++){
					output[d][d1] = listRow.get(i)[ j + (Define.SIZE_ROW - Define.MOVE)][k];
					d1++;
				}
				d++;
			}
			//System.out.println("d = "+d);
		}
		/*for(int i = 0; i < listRow.size(); i++ ){
			int d = 0;
			for(int j = 0; j < listRow.get(i).length; j++){
				int d1 = 0;
				//System.out.println("list = "+ listRow.get(0)[0][0][0]);
				for(int k = 0; k < listRow.get(i)[0].length; k++){
					if(i == 0){
						output[d][d1] = listRow.get(0)[j][k];
						//System.out.println("output = "+ output[d][d1][0]+ "list = "+listRow.get(i)[j][k][0] );  // Infinity 
						d1++;
					}else if(j < listRow.get(i).length - 5){
							output[d][d1] = listRow.get(i)[j+5][k];
							d1++;
					}
					//System.out.println("ou = "+ output[0][d][0]);
					
				}
				d++;
			}
		}*/
		//System.out.println("output = "+ output[0][5][1]);
		/*for(int i = 0; i < 100; i++){
			for(int j = 0; j < 100; j++){
				for(int k = 0; k < 3; k++){
					if(output[i][j][k] != 0)
					System.out.println(" "+ output[i][j][k]);
				}
			}
			//System.out.println();
		}*/
		return output;
	}
	
	// pvu cho transform
	public static double[][][] beforeTransform(double[][][]befor, double [][][] forecast1,ArrayList<ArrayList<double[][][]>> list,ArrayList<double[][][]> result){
		double[][][] nextForecast = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		
		/*int dd = 0;
		double[][][] befor = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL;j++){
				befor[i][j][0] = M[dd].getRed();
				befor[i][j][1] = M[dd].getGreen();
				befor[i][j][2] = M[dd].getBlue();
				dd++;
			}
		}*/
		// tinh chenh lech giua anh buoc truoc va anh du bao duoc.
		double[][][] chenhlech = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL;j++){
				for(int t = 0; t< 3; t++){
					chenhlech[i][j][t] = forecast1[i][j][t] - befor[i][j][t];
				}
			}
		}
		// phan vung mang chenh lech
		ArrayList<double[][][]> data = new ArrayList<double[][][]>();
		//data = LuatMo2.getRegionImg(pi);
		for (int i = 0; (i+Define.MOVE) < Define.IMAGE_SIZE_ROW; i += Define.MOVE) {
			for (int j = 0; (j+Define.MOVE) < Define.IMAGE_SIZE_COL; j += Define.MOVE) {
				double[][][] pix = LuatMo2.getRegion(i, j, Define.MOVE, chenhlech);
				data.add(pix);
			}
		}
		// list cac vung output
				ArrayList<double[][][]> list_out = new ArrayList<double[][][]>();
				for (int k = 0; k < data.size(); k++) {
					 double [][][] output = LuatMo2.tinh_O(list.get(k), result.get(k), data.get(k));
					LuatMo2.writeFile(Define.path + "/sub_output"+k +".txt", output);
					list_out.add(output);
				}
				// don cac vung output lai
				ArrayList<double[][][]> mergerList = LuatMo2.mergerList(list_out);
				double [][][] output = LuatMo2.megerRow(mergerList);
				LuatMo2.writeFile(Define.path + "\\output.txt", output);
				// du bao anh tiep theo
				for(int i = 0; i < Define.IMAGE_SIZE_ROW;i++){
					for(int j = 0; j < Define.IMAGE_SIZE_COL;j++){
						for(int t = 0; t < 3; t++){
							if(output[i][j][t] < 0){
								nextForecast[i][j][t] = Math.floor(output[i][j][t]) + forecast1[i][j][t]; 
							}else{
								nextForecast[i][j][t] = Math.ceil(output[i][j][t]) + forecast1[i][j][t]; 
							}
						}
					}
				}
				
		return nextForecast;
	}
	public static int[][] median(double[][][]nextForecast, double[][][] forecast1){
		int[][] median = new int[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
		int count1 = 0;
			int r = 0,g = 0,b = 0;
			for (int x = 0; x < Define.IMAGE_SIZE_ROW; x++) {
				for (int y = 0; y < Define.IMAGE_SIZE_COL; y++) {
					int red = (int) nextForecast[x][y][0];
					int green = (int) nextForecast[x][y][1];
					int blue = (int) nextForecast[x][y][2];

					if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
						
						
						if(red < -5 || red > 260){
							r++;
							red = (int) forecast1[x][y][0];
						}
						if(green < -5 || green > 260){
							g++;
							green = (int) forecast1[x][y][1];
						}
						if(blue < -5 || blue > 260){
							b++;
							blue = (int) forecast1[x][y][2];
						}
						
						if(red < 0){
						red = 0;
					}
					if(green < 0){
						green = 0;
					}
					if(blue < 0){
						blue = 0;
					}
					if(red > 255){
						red = 255;
					}
					if(green > 255){
						green = 255;
					}
					if(blue > 255){
						blue = 255;
					}
						count1++;
					}

					Color c = new Color(red, green, blue);
					median[x][y] = c.getRGB();

				}
			}
			System.out.println("count = " + count1);
			System.out.println("r = "+ r+ "  g = "+ g+ "  b = "+ b);
			return median;
	}
	
	
	public double tinh_rmse (double [][][] forecast,int t ){
		double rmse = 0;
		Pixel [] M = FC_PFS2.readData(Define.path + "/out_put2_id"+ (number_image + t) +".txt");

		double [][][] anhthuc = new double [Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3]; 
		int count = 0;
		double tg,tg1;
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
				anhthuc[i][j][0] = M[0].getRed();
				anhthuc[i][j][1] = M[0].getGreen();
				anhthuc[i][j][2] = M[0].getBlue();
			}
		}
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
				tg = 0;tg1 = 0;
				for(int k = 0; k < 3; k++){
					tg += (forecast[i][j][k] - anhthuc[i][j][k]) * (forecast[i][j][k] - anhthuc[i][j][k]);
					tg1 += anhthuc[i][j][k];
				}
				rmse += tg/tg1;
			}
		}
		rmse = Math.sqrt(rmse /(Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));
		return rmse;
	}
		public static void main(String[] args) {
		LuatMo2 luatmo = new LuatMo2();
		int number_image = Define.NUM_TRAIN_IMG;
		int cluster = Define.NUM_RULES;
		
		FC_PFS2 fc = new FC_PFS2(cluster);
		Pixel[][] array = LuatMo2.training_sple2(number_image);
		 FC_PFS2.FC_PFS(array);
		
		ArrayList<double [][][]> li = LuatMo2.getRate(number_image);
		for(int i = 0; i < li.size(); i++){
			writeFile(Define.path + "/MangChenhlech"+i+".txt", li.get(i));
		}
		//System.out.println("size = " + li.size());
		 ArrayList<ArrayList<double [][][]>> list = LuatMo2.getRegionOfImages2(li,number_image);
		//System.out.println("size = "+ list.size());
		 //System.out.println("length = "+ list.get(0).get(0).length);
		//System.out.println("sub_size = "+ list.get(0).size() + "value = "+list.get(0).get(0)[0][0][2]);
		 li = LuatMo2.getU(number_image);
		 for(int j = 0; j < 3; j++){
		  for(int t = 0; t < li.get(j).length; t++){
				 for(int k = 0; k < li.get(j)[0].length; k++){
					 for(int i = 0; i < cluster; i ++){
						// if(li.get(j)[t][k][i] != 0)
						 //System.out.print(" "+li.get(j)[t][k][i]);
					 }
			 		//System.out.println("");
				 }
			 }
		 }
		 ArrayList<ArrayList<double [][][]>> list_u = LuatMo2.getRegionOfDependence(li, number_image);
		 
		 li = LuatMo2.getU1(number_image);
		 ArrayList<ArrayList<double [][][]>> list_u1 = LuatMo2.getRegionOfDependence(li, number_image);
		 
		/* ArrayList<double[][][]> e = new ArrayList<double[][][]>();
		 e = LuatMo2.getData(number_image, Define.path + "/matranE_rule.txt");
		 ArrayList<ArrayList<double[][][]>> list_e = LuatMo2.getRegionOfDependence(e, number_image);
		 
		 ArrayList<double[][][]> n = new ArrayList<double[][][]>();
		 n = LuatMo2.getData(number_image,Define.path + "/matranN_rule.txt");
		 ArrayList<ArrayList<double[][][]>> list_n = LuatMo2.getRegionOfDependence(n, number_image);*/
		
		 
		 //ArrayList<double [][][]> result = LuatMo2.luat(list, list_u,list_u1);
		 ArrayList<double [][][]> result = LuatMo2.luat(list, list_u);
		//System.out.println("size = "+ result.size()+ "phân vùng 1: ");
		/*for(int i = 0;i < 5; i++){
			//System.out.print( " "+result.get(0)[0][0][i]);
			System.out.print(" "+ result.get(0)[1][1][i]);
		}*/
		//for(int i = 0; i < result.size(); i++){
			/*for(int j = 0; j < 10; j ++){
				System.out.println("bo luat cum : "+ j);
				for(int k = 0; k < 3; k++){
					for(int h = 0; h < 5; h++){
						System.out.print(" "+ result.get(0)[j][k][h]);
					}
					System.out.println();
				}
				System.out.println();
			}*/
		//}
			// lấy ảnh 5 trừ ảnh 4 cho vào luật -> output.
			String filename = Define.path + "/out_put2_id"+ (number_image-1) + ".txt";
			String filename2 = Define.path + "/out_put2_id" + number_image + ".txt";
			double [][] rate = rates(FC_PFS2.readData(filename), FC_PFS2.readData(filename2));
			double [][][] p = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
			int count = 0;
			for(int t = 0; t < Define.IMAGE_SIZE_ROW; t++){
				for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
					p[t][j][0] = rate[count][0];
					p[t][j][1] = rate[count][1];
					p[t][j][2] = rate[count][2];
					count ++;
				}
			}
			writeFile(Define.path + "/ChenhlechCuoi.txt", p);
			ArrayList<double [][][]> data = new ArrayList<double[][][]>();
			for(int i = 0; i < Define.IMAGE_SIZE_ROW; i+=Define.MOVE){
				for(int j = 0; j < Define.IMAGE_SIZE_COL; j += Define.MOVE){
					double [][][] pi = getRegion(i, j, Define.MOVE, p);
					data.add(pi);
				}
			}
			// mang list_out chứa kết quả output của 25 vùng.
			ArrayList<double [][][]> list_out = new ArrayList<double[][][]>();
			//System.out.println("..="+ list.get(0).get(0).length);
		for(int k = 0; k < data.size(); k++){
			double[][][] output = luatmo.tinh_O(list.get(k),  result.get(k),data.get(k) );
			writeFile(Define.path + "/sub_output"+k +".txt", output);
			list_out.add(output);
		}
		//writeFile("Chenhlech1.txt", list_out.get(0));
		for(int k = 0; k < list_out.size(); k++){
			//System.out.println("k:" +k);
			for(int i = 0; i < list_out.get(k).length; i++){
				for(int j = 0; j < list_out.get(k)[0].length; j++){
					for(int t = 0; t < 3; t++){
						if(Double.isInfinite(list_out.get(k)[i][j][t]) || Double.isNaN(list_out.get(k)[i][j][t]))
						 System.out.println("output in list: " + list_out.get(k)[i][j][t]+ "k = "+ k);
					}
				}
				
			}
		}
		
		ArrayList<double [][][]> mergerList = mergerList(list_out);
		//System.out.println(".. = "+mergerList.get(0)[0][0][0]);
		double[][][]output = megerRow(mergerList);
		/*for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
				for(int k = 0; k < 3; k++){
					if(Math.abs(output[i][j][k] - Define.MIN_EPSILON) < 0)
						System.out.println(" output "+i +":"+ j + ":"+ k +" "+ output[i][j][k]);
				}
			}
		}*/
		
		writeFile(Define.path + "/output.txt", output);
		//Pixel[][] PixelFor = forecast(output,number_image);
		System.out.println("do");
		/*for(int i = 0; i < 100; i++){
			for(int j = 0; j < 100; j++){
				for(int k = 0; k < 3; k++){
					if(output[i][j][k] != 0)
						System.out.println(" output "+i +":"+ j + ":"+ k +" "+ output[i][j][k]);
				}
			}
		}*/
		
/*
		Pixel [] M = FC_PFS2.readData("out_put2_id4.txt");
		double [][][] a = new double [100][100][3];
		
		int countt = 0;
		for(int i = 0; i < 100; i++){
			for(int j = 0; j < 100; j++){
				a[i][j][0] = M[countt].getRed();
				a[i][j][1] = M[countt].getGreen();
				a[i][j][2] = M[countt].getBlue();
				count++;
			}
		}
		ArrayList<double [][][]> d = new ArrayList<double[][][]>();
		for(int i = 0; i < 100; i+=20){
			for(int j = 0; j < 100; j += 20){
				double [][][] pi = getRegion(i, j, 20, p);
				d.add(pi);
			}
		}
		ArrayList<double [][][]> forecast = new ArrayList<double[][][]>();
		double [][][] fore;
		for(int i = 0; i < d.size(); i++){
			fore = new double[d.get(i).length][d.get(i)[0].length][3];
			for(int j = 0; j < d.get(i).length;j++){
				for(int k = 0; k < d.get(i)[0].length; k++){
					fore[j][k][0] = d.get(i)[j][k][0] * (1 + list_out.get(i)[j][k][0]) -1;
					fore[j][k][1] = d.get(i)[j][k][1] * (1 + list_out.get(i)[j][k][1]) -1;
					fore[j][k][2] = d.get(i)[j][k][2] * (1 + list_out.get(i)[j][k][2]) -1;
				}
			}
			forecast.add(fore);
		}*/
		
		//System.out.println("length = "+ output.length);
		
		/*ArrayList<double [][][]> tg = new ArrayList<double[][][]>();
		double [][][] tg1 = new double[25][25][3];
		for(int i = 0; i < 5; i++){
			for(int j = i; j < 25; j+=5){
				int d2 = 0;
				for(int c = 0; c < list_out.get(j)[0].length; c++){
					int d3 = 0;
					for(int r = 0; r < list_out.get(j).length; r++){
						if(r < 20){
							tg1[d2][d3] = list_out.get(i)[r][c];
							d3++;
						}
						if(r >= 20){
							for(int jj = 0; jj < 3; jj++){
								tg1[d2][d3][j] = (list_out.get(j)[r][c][j] + list_out.get(j+5)[25-r][c][j])/2;
							}
							d3++;
						}
					}
					d2++;
				}
				if(j ==0){
					tg.add(tg1);
				}else{
					double[][][] ttg = new double[20][25][3];
					for(int r = 0; r < list_out.get(i).length -5; r++){
						for(int c = 0; c < list_out.get(i)[r].length ; c++){
							ttg[r][c] = tg1[r+5][c];
						}
					}
					tg.add(ttg);
				}
			}
			int d = 0;
			for(int r = 0; r < list_out.get(i).length; r++){
				int d1 = 0;
				for(int c = 0; c < list_out.get(i)[r].length; c++){
					if(c < 20){
						tg1[d][d1] = list_out.get(i)[r][c];
						d1++;
					}
					if(c >= 20){
						for(int j = 0; j < 3; j++){
							tg1[d][d1][j] = (list_out.get(i)[r][c][j] + list_out.get(i+1)[r][25-c][j])/2;
						}
						d1++;
					}
				}
				d++;
			}
			if(i ==0){
				tg.add(tg1);
			}else{
				double[][][] ttg = new double[25][20][3];
				for(int r = 0; r < list_out.get(i).length; r++){
					for(int c = 0; c < list_out.get(i)[r].length - 5; c++){
						ttg[r][c] = tg1[r][c+5];
					}
				}
				tg.add(ttg);
			}
			
		}*/
		
	}
}
