package control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

public class Data {

	private static ArrayList<Pixel[][]> image = new ArrayList<Pixel[][]>();
	private static ArrayList<Pixel[][]> image_goc = new ArrayList<Pixel[][]>();

	private static double cutoff = 0.1;
	
	private static double dc_level = 255;
	
	public static void setAfterRemoveImage(Pixel[][] a,int id){
		File f;
		image.remove(id);
		image.add(id, a);
		System.out.println("id = " + id);
		String filename = Define.BASEDPATH + "/image_" + id + ".txt";		
		f = new File(filename);
		//if(f.exists() == false){
			writeImageText(a,filename);
       // }
	}
	public static void setImage(Pixel[][] a) {
		File f;
		int id;
		image.add(a);
		image_goc.add(a);
		id = image.indexOf(a);
		System.out.println("id = " + id);
		String filename = Define.BASEDPATH + "/image_" + id + ".txt";		
		f = new File(filename);
		//if(f.exists() == false){
			writeImageText(a,filename);
       // }
	}
	
	

	public static Pixel[][] getImage(int index) {
		return image.get(index);
	}
	
	public static Pixel[][] getImage_goc(int index) {
		return image_goc.get(index);
	}

	public static Matrix convertToMatrix() {
		Matrix a = new Basic2DMatrix(new double[][] {});
		return a;
	}

	public static Pixel getPixel(int i, int j, int index) {
		return image.get(index)[i][j];
	}
	
	public static Pixel getPixel_goc(int i, int j, int index) {
		return image_goc.get(index)[i][j];
	}

	private static int belongCluster(int id, double [][]u, double [][]e){
		double max, tg;
		int k = 0;
		tg = u[id][0] * (2 - e[id][0]);
		max = tg;
//		System.out.println("max = " + max);
		for (int i = 1; i < Define.NUM_CLUSTER; i++) {
			tg = u[id][i] * (2 - e[id][i]);
			if(max < tg){
				max = tg;
				k = i;
//				System.out.println("max = " + max + " i = " + i);
			}
		}
		return k;
	}
	
	private static int belongCluster(int id, double [][]u){
		double max, tg;
		int k = 0;
		tg = u[id][0];
		max = tg;
		for (int i = 1; i < Define.NUM_CLUSTER; i++) {
			tg = u[id][i];
			if(max < tg){
				max = tg;
				k = i;
			}
		}
		return k;
	}
	
	public static double[][] getNeighborPixelCluster(int i, int j, int index, int cell, ArrayList<double[][]> u, ArrayList<double[][]> e, boolean [] mark) {
		int rstart, rstop, cstart, cstop;
		int ii, jj, idI, idd, k, count=0, size;
				
		if (i <= cell)
			rstart = 0;
		else
			rstart = i - cell;
		
		if (i >= Define.IMAGE_SIZE_ROW - cell)
			rstop = Define.IMAGE_SIZE_ROW;
		else
			rstop = i + cell + 1;
		
		if (j <= cell)
			cstart = 0;
		else
			cstart = j - cell;
		
		if (j >= Define.IMAGE_SIZE_COL - cell)
			cstop = Define.IMAGE_SIZE_COL;
		else
			cstop = j + cell + 1;
				
//		size = (rstop - rstart) * (cstop - cstart);

		// System.out.println("size = " + size);
		size = Define.NUM_CLUSTER * Define.NUM_TRAINING_IMAGE;
		double[][] ma = new double[3][size];
		for (k = 0; k < Define.NUM_CLUSTER * Define.NUM_TRAINING_IMAGE; k++) {			
			for (int k2 = 0; k2 < 3; k2++) {
				ma[k2][k] = 0;
			}
		}
		
		for (idI = index; idI < Define.NUM_TRAINING_IMAGE + index; idI++) {
			for (ii = rstart; ii < rstop; ii++) {
				for (jj = cstart; jj < cstop; jj++) {
					
					if(Transformations.getjCombochooser() == 0){
						idd = (idI - index) * Define.NUM_CLUSTER + belongCluster(idI,u.get(idI),e.get(idI));
//						System.out.print(idd + "  ");
					}else{
						idd = (idI - index) * Define.NUM_CLUSTER + belongCluster(idI,u.get(idI));
					}
/*					
					for(k = 0;k<3;k++){
						ma[k][idd] += imageFilter.get(idI)[ii][jj][k];
					}
*/					
					ma[0][idd] += image.get(idI)[ii][jj].getRed();
					ma[1][idd] += image.get(idI)[ii][jj].getGreen();
					ma[2][idd] += image.get(idI)[ii][jj].getBlue();

				}
			}
		}
		
		for (k = 0; k < size; k++) {
			mark[k] = false;
			for (int k2 = 0; k2 < 3; k2++) {				
				if(ma[k2][k] != 0){
					mark[k] = true;
					break;
				}
			}
			if(mark[k] == false) count++;
		}
		
		size = Define.NUM_CLUSTER * Define.NUM_TRAINING_IMAGE - count;
		
		double[][] mat = new double[3][size];
		idd = 0;
		for (k = 0; k < Define.NUM_CLUSTER * Define.NUM_TRAINING_IMAGE; k++) {			
			if(mark[k]){
				for (int k2 = 0; k2 < 3; k2++) {				
					mat[k2][idd] = ma[k2][k];					
				}
				idd++;
			}
		}				
		return mat;
	}
	
	public static double[][] getNeighborPixelCluster(int i, int j, int index, int cell, ArrayList<double[][]> u, ArrayList<double[][]> e) {
		int rstart, rstop, cstart, cstop;
		int ii, jj, idI, idd, k, size;
				
		if (i <= cell)
			rstart = 0;
		else
			rstart = i - cell;
		
		if (i >= Define.IMAGE_SIZE_ROW - cell)
			rstop = Define.IMAGE_SIZE_ROW;
		else
			rstop = i + cell + 1;
		
		if (j <= cell)
			cstart = 0;
		else
			cstart = j - cell;
		
		if (j >= Define.IMAGE_SIZE_COL - cell)
			cstop = Define.IMAGE_SIZE_COL;
		else
			cstop = j + cell + 1;
				
//		size = (rstop - rstart) * (cstop - cstart);

		// System.out.println("size = " + size);
		size = Define.NUM_CLUSTER * Define.NUM_TRAINING_IMAGE;
		double[][] ma = new double[3][size];
		for (k = 0; k < Define.NUM_CLUSTER * Define.NUM_TRAINING_IMAGE; k++) {			
			for (int k2 = 0; k2 < 3; k2++) {
				ma[k2][k] = 0;
			}
		}
		
		for (idI = index; idI < Define.NUM_TRAINING_IMAGE + index; idI++) {
			for (ii = rstart; ii < rstop; ii++) {
				for (jj = cstart; jj < cstop; jj++) {
					
					if(Transformations.getjCombochooser() == 0){
						idd = (idI - index) * Define.NUM_CLUSTER + belongCluster(idI,u.get(idI),e.get(idI));
//						System.out.print(idd + "  ");
					}else{
						idd = (idI - index) * Define.NUM_CLUSTER + belongCluster(idI,u.get(idI));
					}
					
					/*for(k = 0;k<3;k++){
						ma[k][idd] += imageFilter.get(idI)[ii][jj][k];
					}*/
					
					ma[0][idd] += image.get(idI)[ii][jj].getRed();
					ma[1][idd] += image.get(idI)[ii][jj].getGreen();
					ma[2][idd] += image.get(idI)[ii][jj].getBlue();

				}
			}
		}						
		return ma;
	}
	
	public static double[][] getNeighborPixelClusterNew(int i, int j, int index, int cell, ArrayList<double[][]> u, ArrayList<double[][]> e) {
		int rstart, rstop, cstart, cstop;
		int ii, jj, idI, idd, idy, k;
		int size;
		
		if (i <= cell)
			rstart = 0;
		else
			rstart = i - cell;
		
		if (i >= Define.IMAGE_SIZE_ROW - cell)
			rstop = Define.IMAGE_SIZE_ROW;
		else
			rstop = i + cell + 1;
		
		if (j <= cell)
			cstart = 0;
		else
			cstart = j - cell;
		
		if (j >= Define.IMAGE_SIZE_COL - cell)
			cstop = Define.IMAGE_SIZE_COL;
		else
			cstop = j + cell + 1;
				
		size = Define.NUM_TRAINING_IMAGE * Define.NUM_CLUSTER;
		// System.out.println("size = " + size);

		double[][] ma = new double[3][size];
		for (k = 0; k < size; k++) {
			for (int k2 = 0; k2 < 3; k2++) {
				ma[k2][k] = 0;
			}
		}
		
		for (idI = index; idI < Define.NUM_TRAINING_IMAGE + index; idI++) {
			for (ii = rstart; ii < rstop; ii++) {
				for (jj = cstart; jj < cstop; jj++) {
					
					if(Transformations.getjCombochooser() == 0){
						idd = (idI - index) * Define.NUM_CLUSTER + belongCluster(idI,u.get(idI),e.get(idI));
					}else{
						idd = (idI - index) * Define.NUM_CLUSTER + belongCluster(idI,u.get(idI));
					}
					
					/*for(k = 0;k<3;k++){
						ma[k][idd] += imageFilter.get(idI)[ii][jj][k];
					}*/
					
					ma[0][idd] += image.get(idI)[ii][jj].getRed();
					ma[1][idd] += image.get(idI)[ii][jj].getGreen();
					ma[2][idd] += image.get(idI)[ii][jj].getBlue();

				}
			}
		}
		
		return ma;
	}
	
	public static double[] normalizePixel(int i, int j, int index,ArrayList<double[][]> u, ArrayList<double[][]> e, ArrayList<Pixel[]> center, int stop) {

		int idI, l;
		double[] tg = new double[3];
		
		tg[0] = 0;
		tg[1] = 0;
		tg[2] = 0;
		for (idI = index; idI < stop; idI++) {
			for (l = 0; l < Define.NUM_CLUSTER; l++) {
				if(Transformations.getjCombochooser() == 0){	
				
					tg[0] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]
							* (2 - e.get(idI)[i * Define.IMAGE_SIZE_COL + j][l])
							* center.get(idI)[l].getRed();
					tg[1] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]
							* (2 - e.get(idI)[i * Define.IMAGE_SIZE_COL + j][l])
							* center.get(idI)[l].getGreen();
					tg[2] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]
							* (2 - e.get(idI)[i * Define.IMAGE_SIZE_COL + j][l])
							* center.get(idI)[l].getBlue();
				}else{
					tg[0] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]							
							* center.get(idI)[l].getRed();
					tg[1] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]							
							* center.get(idI)[l].getGreen();
					tg[2] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]
							* center.get(idI)[l].getBlue();
				}
			}
		}

		tg[0] = tg[0] / (stop - index);
		tg[1] = tg[1] / (stop - index);
		tg[2] = tg[2] / (stop - index);
		return tg;
	}

	
	
	public static double[] normalizePixel(int i, int j, int index,ArrayList<double[][]> u, ArrayList<double[][]> e,ArrayList<Pixel[]> center) {

		int idI, l;
		double[] tg = new double[3];
		
		tg[0] = 0;
		tg[1] = 0;
		tg[2] = 0;
		for (idI = index; idI < Define.NUM_TRAINING_IMAGE; idI++) {
			for (l = 0; l < Define.NUM_CLUSTER; l++) {
				if(Transformations.getjCombochooser() == 0){	
				
					tg[0] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]
							* (2 - e.get(idI)[i * Define.IMAGE_SIZE_COL + j][l])
							* center.get(idI)[l].getRed();
					tg[1] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]
							* (2 - e.get(idI)[i * Define.IMAGE_SIZE_COL + j][l])
							* center.get(idI)[l].getGreen();
					tg[2] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]
							* (2 - e.get(idI)[i * Define.IMAGE_SIZE_COL + j][l])
							* center.get(idI)[l].getBlue();
				}else{
					tg[0] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]							
							* center.get(idI)[l].getRed();
					tg[1] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]							
							* center.get(idI)[l].getGreen();
					tg[2] += u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]
							* center.get(idI)[l].getBlue();
				}
			}
		}

		tg[0] = tg[0] / Define.NUM_TRAINING_IMAGE;
		tg[1] = tg[1] / Define.NUM_TRAINING_IMAGE;
		tg[2] = tg[2] / Define.NUM_TRAINING_IMAGE;
		return tg;
	}

	public static double normalizePixel(int i, int j, int index, int color,ArrayList<double[][]> u, ArrayList<double[][]> e,ArrayList<Pixel[]> center) {

		int idI, l;
		double tg = 0, tg1;

		// System.out.println("size = " + size);

		// double [][] ma = new double[3][size * Define.NUM_TRAINING_IMAGE];

		// for(idI = index;idI < Define.NUM_TRAINING_IMAGE * 2;idI++){

		for (idI = index; idI < Define.NUM_TRAINING_IMAGE; idI++) {

			for (l = 0; l < Define.NUM_CLUSTER; l++) {
				tg1 = u.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]
						* (2 - e.get(idI)[i * Define.IMAGE_SIZE_COL + j][l]);
				switch (color) {
				case 0:
					tg += tg1 * center.get(idI)[l].getRed();
					break;
				case 1:
					tg += tg1 * center.get(idI)[l].getGreen();
					break;
				case 2:
					tg += tg1 * center.get(idI)[l].getBlue();
					break;
				}
			}
		}
		tg1 = tg / Define.NUM_TRAINING_IMAGE;
		return tg1;
	}

		
	
	public static double[][] readMatrix2(String filename) {
		// a = new double[10000][4];
		// System.out.println(row);
		// System.out.println(FC_PFS2.getC());
	
		BufferedReader br = null;
		int end = Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL;
		double[][] a = new double[end][Define.NUM_CLUSTER];
		String s;
		try {
			br = new BufferedReader(new FileReader(filename));
			for (int i = 0; i < end; i++) {
				s = br.readLine();
				String[] array = s.split(" ");

				for (int j = 0; j < Define.NUM_CLUSTER; j++) {
					array[j] = array[j].replace(',', '.');
					// System.out.print(array[j] + " ");
					a[i][j] = Double.parseDouble(array[j]);
				}
				// System.out.println("");

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return a;
	}

	public static Pixel[] readCenter2(String filename) {
		Pixel [] center = new Pixel[Define.NUM_CLUSTER];
		try {
			String s;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			for (int i = 0; i < Define.NUM_CLUSTER; i++) {
				s = br.readLine();
				String arr[] = s.split(",");
				center[i] = new Pixel(Double.parseDouble(arr[0]),Double.parseDouble(arr[1]),Double.parseDouble(arr[2]));
			}

		} catch (FileNotFoundException | NumberFormatException e) {
			e.getStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return center;
	}
	
	public static void writeImageText(double [][][]a, String filename){
		try {
			
			BufferedWriter br = new BufferedWriter(new FileWriter(filename));
			for (int i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
				for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {
					for (int k = 0; k < a[i][j].length; k++) {
						br.write(a[i][j][k] + " ");
					}
					br.write("\n");
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void writeImageText(Pixel [][]a, String filename){
		try {
			
			BufferedWriter br = new BufferedWriter(new FileWriter(filename));
			for (int i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
				for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {					
					br.write(a[i][j].toString() + "\n");				
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static double [][][] rotate180(double [][][]a){
		double[][][] pre = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
/*
		for (int i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
			for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {
				for (int j2 = 0; j2 < 3; j2++) {
					pre[i][j][j2] = a[Define.IMAGE_SIZE_ROW - i - 1][Define.IMAGE_SIZE_COL - j - 1][j2];
				}
			}
		}
*/
		int k,h;
		for (int i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
			if(i < Define.IMAGE_SIZE_ROW - 1) k = i + 1;
			else k = 0;
			for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {
				if(j < Define.IMAGE_SIZE_COL - 1) h = j + 1;
				else h = 0;
				for (int j2 = 0; j2 < 3; j2++) {
					pre[k][h][j2] = a[Define.IMAGE_SIZE_ROW - i - 1][Define.IMAGE_SIZE_COL - j - 1][j2];
				}
			}
		}
		
		return pre;
	}
	
}
