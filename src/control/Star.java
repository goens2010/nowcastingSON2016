package control;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.la4j.linear.LeastSquaresSolver;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.sparse.CompressedVector;

import com.jhlabs.image.GaussianFilter;

public class Star {

	private static double rmse;
	
	private static double [][][] p = null;
	
	private static ArrayList<double[][]> u = new ArrayList<double[][]>();
	
	private static ArrayList<double[][]> e = new ArrayList<double[][]>();
	
	private static ArrayList<Pixel[]> center = new ArrayList<Pixel[]>();
	
	
	private static int sizeMax = (int)(Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL / 4);
	
	public static double[][][] getP() {
		return p;
	}

	public static void setP(double[][][] p) {
		Star.p = p;
	}

	private static double diff = 20;

	public static double getRmse() {
		return rmse;
	}

	private static int getAmedian(int i, int j, int [][] pi){
		
		int rstart, rstop, cstart, cstop;
		int ii, jj;
		int size, sx = 1, sy = 1, count = 0, tg, median = 0;

		while(true){
			if (i <= sx)
				rstart = 0;
			else
				rstart = i - sx;
			
			if (i >= Define.IMAGE_SIZE_ROW - sx)
				rstop = Define.IMAGE_SIZE_ROW;
			else
				rstop = i + sx + 1;
			
			if (j <= sy)
				cstart = 0;
			else
				cstart = j - sy;
			
			if (j >= Define.IMAGE_SIZE_COL - sy)
				cstop = Define.IMAGE_SIZE_COL;
			else
				cstop = j + sy + 1;
					
			size = (rstop - rstart) * (cstop - cstart);
			
//			System.out.println("rsa=" + rstart + " rso=" + rstop + " csa=" + cstart + " cso=" + cstop + " size=" + size + " sizemax=" + sizeMax);
			
			if(size >= sizeMax){
//				System.out.println(pi[i][j]);
				return pi[i][j];
			}
			
			count = 0;
			int []medi = new int[size - 1];
			// find the median
						
			for (ii = rstart; ii < rstop; ii++) {
				for (jj = cstart; jj < cstop; jj++) {
					if (ii != i || jj != j) {
						medi[count] = pi[ii][jj];
						count++;
					}
				}
			}
			
			for(ii = 0;ii < size - 2;ii++){
				for(jj = ii;jj < size - 1;jj++){
					if(medi[ii] < medi[jj]){
						tg = medi[ii];
						medi[ii] = medi[jj];
						medi[jj] = tg;
					}
				}
			}
			
			tg = (int)((size - 1) / 2);
			median = medi[tg];
//			System.out.println("count=" + count + "median(" + tg + ") = " + median  + " p[" + i + "," + j + "]" + " = " + pi[i][j]);
			
			if(median - medi[size - 2] > 0 && median - medi[0] < 0 ) {
				if(pi[i][j] - medi[size - 2] > 0 && pi[i][j] - medi[0] < 0){
					return pi[i][j];
				}else{
					pi[i][j] = median;
					return median;
				}
			}else{
				sx++;
				sy++;
			}		
		}		
		// find min - max
	}
	
	
	private static int getAmedian(int i, int j, int color){
		
		int rstart, rstop, cstart, cstop;
		int ii, jj;
		int size, sx = 1, sy = 1, count = 0, tg, median = 0;

		while(true){
			if (i <= sx)
				rstart = 0;
			else
				rstart = i - sx;
			
			if (i >= Define.IMAGE_SIZE_ROW - sx)
				rstop = Define.IMAGE_SIZE_ROW;
			else
				rstop = i + sx + 1;
			
			if (j <= sy)
				cstart = 0;
			else
				cstart = j - sy;
			
			if (j >= Define.IMAGE_SIZE_COL - sy)
				cstop = Define.IMAGE_SIZE_COL;
			else
				cstop = j + sy + 1;
					
			size = (rstop - rstart) * (cstop - cstart);
			
//			System.out.println("rsa=" + rstart + " rso=" + rstop + " csa=" + cstart + " cso=" + cstop + " size=" + size + " sizemax=" + sizeMax);
			
			if(size >= sizeMax){
				System.out.println(p[i][j][color]);
				return (int)p[i][j][color];
			}
			
			count = 0;
			int []medi = new int[size - 1];
			// find the median
						
			for (ii = rstart; ii < rstop; ii++) {
				for (jj = cstart; jj < cstop; jj++) {
					if (ii != i || jj != j) {
						medi[count] = (int)p[ii][jj][color];
						count++;
					}
				}
			}
			
			for(ii = 0;ii < size - 2;ii++){
				for(jj = ii;jj < size - 1;jj++){
					if(medi[ii] < medi[jj]){
						tg = medi[ii];
						medi[ii] = medi[jj];
						medi[jj] = tg;
					}
				}
			}
			tg = (int)((size - 1) / 2);
			median = medi[tg];
//			System.out.println("count=" + count + "median(" + tg + ") = " + median  + " p[" + i + "," + j + "]" + " = " + pi[i][j]);
			
			if(median - medi[size - 2] > 0 && median - medi[0] < 0 ) {
				if((int)p[i][j][color] - medi[size - 2] > 0 && (int)p[i][j][color] - medi[0] < 0){
					return (int)p[i][j][color];
				}else{
					return median;
				}
			}else{
				sx++;
				sy++;
			}		
		}		
	}
	
	private static boolean check(double a[], double [][] b){
		int l;
		double tg = 0, tg1 = 0, tg2 = 0;
		for (l = 0; l < 3; l++) {
			tg += a[l];
			tg1 += b[l][0];
			tg2 += b[l][1];									
		}		
		if(tg1 - diff > tg || tg2 + diff < tg) return false;
		else return true;
	}
	
	private static void checkSolve(int i, int j) {
		int rstart, rstop, cstart, cstop;
		int ii, jj, l = 0, k = 0;
		int size;
//		double [] res = new double[3]; 
		double[] tg = new double[3];
		double[][] m = new double[3][2];
		if (i == 0)
			rstart = 0;
		else
			rstart = i - 1;
		if (i == Define.IMAGE_SIZE_ROW - 1)
			rstop = i + 1;
		else
			rstop = i + 2;

		if (j == 0)
			cstart = 0;
		else
			cstart = j - 1;
		if (j == Define.IMAGE_SIZE_COL - 1)
			cstop = j + 1;
		else
			cstop = j + 2;

		size = (rstop - rstart) * (cstop - cstart);

		if (rstart == i && cstart == j) {
			l = i;
			k = j + 1;
		}else{
			l = rstart;
			k = cstart;
		}

		for (ii = 0; ii < 3; ii++) {
			m[ii][0] = p[l][k][ii];
			m[ii][1] = p[l][k][ii];
//			res[ii] = p[i][j][ii];
		}

		// find min - max

		for (ii = rstart; ii < rstop; ii++) {
			for (jj = cstart; jj < cstop; jj++) {
				if (ii != i || jj != j) {
					for (l = 0; l < 3; l++) {
						if (m[l][0] > p[ii][jj][l])
							m[l][0] = p[ii][jj][l];
						if (m[l][1] < p[ii][jj][l])
							m[l][1] = p[ii][jj][l];
					}
				}
			}
		}
		
		for(l = 0;l<3;l++){			
//			res[l] = p[i][j][l];			
			if(p[i][j][l] < m[l][0] || p[i][j][l] > m[l][1]){
				tg[l] = 0;
				for (ii = rstart; ii < rstop; ii++) {
					for (jj = cstart; jj < cstop; jj++) {
						if(i!=ii || j != jj)
							tg[l] += p[i][j][l];
					}
				}
//				res[l] = (double)tg[l] / (size - 1);
				p[i][j][l] = tg[l] / (size - 1);				
//				System.out.println("res[" + l + "]=" + res[l] + " size=" + size);
			}	
			if(Double.isNaN(p[i][j][l])){
				System.out.println("size = " + size + " i=" + i + " j=" + j + " l=" + l + " tg=" + tg[l]);
			}
		}		
//		return res;
	}

	public static BufferedImage predictedRGBMedianImage(int index) {
		BufferedImage pre = new BufferedImage(Define.IMAGE_SIZE_COL,
				Define.IMAGE_SIZE_ROW, BufferedImage.TYPE_INT_ARGB);
		int predictID = index + Define.NUM_TRAINING_IMAGE;
		double tg1, tg3;
		int tg2;
		int [][] pi = new int[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
		if(p == null){ 
//			setP(predict(index));
			setP(predictCluster(index));
		}
		for(int i = 0;i < Define.IMAGE_SIZE_ROW;i++){
			for(int j = 0;j < Define.IMAGE_SIZE_COL;j++){
				Color co = new Color((int)p[i][j][0],(int)p[i][j][1],(int)p[i][j][2]);
				pi[i][j] = co.getRGB();				
//				System.out.print(pi[i][j] + " ");
			}
//			System.out.println("");
		}
		
		rmse = 0;	
		for (int i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
			for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {
				
				tg1 = Data.getPixel(i, j, predictID + 2).getRed()
						+ Data.getPixel(i, j, predictID + 2).getGreen()
						+ Data.getPixel(i, j, predictID + 2).getBlue();
				
				tg2 = getAmedian(i,j,0) + getAmedian(i,j,1) + getAmedian(i,j,2);
				Color co = new Color(tg2);
				tg3 = co.getRed() + co.getBlue() + co.getGreen(); 
				tg1 = Math.pow(100 * (tg3 - tg1) / tg1, 2);
				rmse += tg1;
				System.out.print(tg2 + " ");
		        pre.setRGB(j, i, tg2);
			}
			System.out.println("");
		}
		rmse = Math
				.sqrt(rmse / (Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));
		System.out.println("rmse median RGB = " + rmse);
		
		return pre;
	}
	
	public static BufferedImage predictedImage1(int index) {
		BufferedImage pre = new BufferedImage(Define.IMAGE_SIZE_COL,
				Define.IMAGE_SIZE_ROW, BufferedImage.TYPE_INT_ARGB);
//		int predictID = index + Define.NUM_TRAINING_IMAGE + Define.NUM_LAGRANGE;
		int predictID = index + Define.NUM_TRAINING_IMAGE + 1;
		double tg1, tg3, tg[];
		int count = 0,tg2,k, tt = 0;
		int [][] pi = new int[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
		if(p == null){ 
			setP(predictCluster(index));
		}
		for(int i = 0;i < Define.IMAGE_SIZE_ROW;i++){
			for(int j = 0;j < Define.IMAGE_SIZE_COL;j++){				
				
				if (p[i][j][0] < 0 || p[i][j][0] > 255 || p[i][j][1] < 0 || p[i][j][1] > 255 || p[i][j][2] < 0
						|| p[i][j][2] > 255) {
//					System.out.println(p[i][j][0] + " " + p[i][j][1] + " " + p[i][j][2]);
					
//					p[i][j][0] = Data.getImageFilter().get(predictID - 1)[i][j][0];
//					p[i][j][1] = Data.getImageFilter().get(predictID - 1)[i][j][1];
//					p[i][j][2] = Data.getImageFilter().get(predictID - 1)[i][j][2];
					
//					tg = Data.normalizePixel(i, j, index, u, e, center);
//					tg = Data.normalizePixel(i, j, index, u, e, center, predictID);
					
//					tg = normalizePixel(i,j,index,p);
					tg = Data.normalizePixel(i, j, index, u, e, center);
					p[i][j][0] = tg[0];
					p[i][j][1] = tg[1];
					p[i][j][2] = tg[2];
					
					count++;
				}
/*
				tg = Data.normalizePixel(i, j, index, u, e, center);
				p[i][j][0] = tg[0];
				p[i][j][1] = tg[1];
				p[i][j][2] = tg[2];
*/
				Color co = new Color((int)p[i][j][0],(int)p[i][j][1],(int)p[i][j][2]);
				pi[i][j] = co.getRGB();
				
			}
		}
		//them vao
		/*Pixel [] anhcu = FC_PFS2.readData(Define.BASEDPATH + "/image" + predictID + ".txt");
		Pixel[][] anhcu2 = new Pixel[100][100];
		int demanh1 = 0;
		for (int i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
			for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {
				anhcu2[i][j] = anhcu[demanh1 ++];
			}
		}*/
		//
		System.out.println("num corrected pixels median = " + count);
		System.out.println("predictedid = " + predictID);
		rmse = 0;	
		for (int i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
			for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {
				
				/*tg1 = Data.getPixel(i, j, predictID + 1).getRed()
						+ Data.getPixel(i, j, predictID + 1).getGreen()
						+ Data.getPixel(i, j, predictID + 1).getBlue();*/
				
				//tg1 = Data.getImageFilter().get(predictID)[i][j][0] + Data.getImageFilter().get(predictID)[i][j][1] + Data.getImageFilter().get(predictID)[i][j][2];
				tg2 = getAmedian(i,j,pi);
				Color co = new Color(tg2);
				tg3 = co.getRed() + co.getBlue() + co.getGreen(); 
				tg1 = (co.getRed() - Data.getPixel_goc(i, j, predictID).getRed()) * (co.getRed() - Data.getPixel_goc(i, j, predictID).getRed())
						+(co.getGreen() - Data.getPixel_goc(i, j, predictID).getGreen()) * (co.getGreen() - Data.getPixel_goc(i, j, predictID).getGreen())
						+ (co.getBlue() - Data.getPixel_goc(i, j, predictID).getBlue()) * (co.getBlue() - Data.getPixel_goc(i, j, predictID).getBlue());
				/*tg1 = (co.getRed() - anhcu2[i][j].getRed())* (co.getRed() - anhcu2[i][j].getRed()) + 
						(co.getGreen() - anhcu2[i][j].getGreen()) * (co.getGreen() - anhcu2[i][j].getGreen()) +
						(co.getBlue() - anhcu2[i][j].getBlue()) * (co.getBlue() - anhcu2[i][j].getBlue());*/
				//tg1 = Math.pow(100 * (tg3 - tg1) / tg1, 2);
				rmse += tg1 /tg3;
				pre.setRGB(j, i, tg2);				
			}
		}
		rmse = Math.sqrt(rmse / (Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));
		System.out.println("rmse median = " + rmse);
		return pre;	
	}
	
	
	
	public static BufferedImage rotate180(BufferedImage a){
		BufferedImage pre = new BufferedImage(Define.IMAGE_SIZE_COL,
				Define.IMAGE_SIZE_ROW, BufferedImage.TYPE_INT_ARGB);
		
		for (int i = 0; i < Define.IMAGE_SIZE_COL; i++) {
			for (int j = 0; j < Define.IMAGE_SIZE_ROW; j++) {
				pre.setRGB(i, j, a.getRGB(Define.IMAGE_SIZE_COL - i - 1, Define.IMAGE_SIZE_ROW - j - 1));
			}
		}
		
		return pre;
	}
	
		
	public static BufferedImage predictedImage(int index) {
		BufferedImage pre = new BufferedImage(Define.IMAGE_SIZE_COL,
				Define.IMAGE_SIZE_ROW, BufferedImage.TYPE_INT_ARGB);
		int predictID = index + Define.NUM_TRAINING_IMAGE;
		double tg1, tg3, tg[];
		int count = 0,tg2;
		int [][] pi = new int[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
		if(p == null){ 
//			setP(predict(index));
			setP(predictClusterNew(index));
		}
		
		double max[] = new double[3]; 
		double min[] = new double[3];
		
		for (int i = 0; i < 3; i++) {
			max[i] = p[0][0][i];
			min[i] = p[0][0][i];
		}
			
		for(int i = 0;i < Define.IMAGE_SIZE_ROW;i++){
			for(int j = 0;j < Define.IMAGE_SIZE_COL;j++){
											
				if (p[i][j][0] < 0 || p[i][j][0] > 255 || p[i][j][1] < 0 || p[i][j][1] > 255 || p[i][j][2] < 0
						|| p[i][j][2] > 255) {
					System.out.println(p[i][j][0] + " " + p[i][j][1] + " " + p[i][j][2]);
					tg = Data.normalizePixel(i, j, index, u, e, center);
					p[i][j][0] = tg[0];
					p[i][j][1] = tg[1];
					p[i][j][2] = tg[2];
					count++;
				}
				Color co = new Color((int)p[i][j][0],(int)p[i][j][1],(int)p[i][j][2]);
				pi[i][j] = co.getRGB();
//				System.out.println(co.toString());
			}
		}
		System.out.println("num corrected pixels = " + count);
		
		rmse = 0;	
		for (int i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
			for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {
				
				tg1 = Data.getPixel(i, j, predictID + 1).getRed()
						+ Data.getPixel(i, j, predictID + 1).getGreen()
						+ Data.getPixel(i, j, predictID + 1).getBlue();
				
				tg2 = getAmedian(i,j,pi);
				Color co = new Color(tg2);
				tg3 = co.getRed() + co.getBlue() + co.getGreen(); 
//				tg3 = p[i][j][0] + p[i][j][1] + p[i][j][2]; 	
				tg1 = Math.pow(100 * (tg3 - tg1) / tg1, 2);
				rmse += tg1;
				pre.setRGB(j, i, tg2);
			}
//			System.out.println("");
		}
		rmse = Math
				.sqrt(rmse / (Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));
		System.out.println("rmse = " + rmse);
		
		return pre;
	}

	public static double[][][] predictCluster(int index) {

		int i,ii, j, l, predictID = index + Define.NUM_TRAINING_IMAGE;
		double vecB[];
		vecB = new double[3];
		int idI, cell = Define.CELLSIZE;		
		int idd;
		int size = Define.NUM_TRAINING_IMAGE * Define.NUM_CLUSTER;
		boolean[] mark = new boolean[size];
		String filename = "";
		for (idI = index; idI < Define.NUM_TRAINING_IMAGE + 1; idI++) {
			filename = Define.BASEDPATH + "/matranU_id" + idI + ".txt";
			u.add(Data.readMatrix2(filename));
			if(Transformations.getjCombochooser() == 0){
				filename =  Define.BASEDPATH + "/matranE_id" + idI + ".txt";
				e.add(Data.readMatrix2(filename));
			}			
			filename = Define.BASEDPATH + "/center_id" + idI + ".txt";
			center.add(Data.readCenter2(filename));
		}
		double[][][] pre = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3]; 
	
		//System.out.println(predictID);
		
		for (i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
			for (j = 0; j < Define.IMAGE_SIZE_COL; j++){
				for(ii = 0;ii<3;ii++){
//					vecB[ii] = Math.round(Data.getImageFilter().get(predictID)[i][j][ii]);
					
					
					pre[i][j][ii] = 0;
//					System.out.print(vecB[ii]+ " ");					
				}
				
				vecB[0] = Math.round(Data.getImage(predictID)[i][j].getRed());
				vecB[1] = Math.round(Data.getImage(predictID)[i][j].getGreen());
				vecB[2] = Math.round(Data.getImage(predictID)[i][j].getRed()); 
				
				Vector b = new CompressedVector(vecB);				
				double [][]mat = Data.getNeighborPixelCluster(i, j, index, cell, u, e, mark);				
				Matrix a = new Basic2DMatrix(mat);
				Matrix aT = a.transpose();
				Matrix aTa = aT.multiply(a);
				Vector f = aT.multiply(b);
				LeastSquaresSolver ls = new LeastSquaresSolver(aTa);
				Vector c = ls.solve(f);
				double [][]ma = Data.getNeighborPixelCluster(i, j, index + 1, cell, u, e);		
				idd = 0;
				for (l = 0; l < size; l++) {
					if(mark[l]){						
						for(ii = 0;ii<3;ii++){
							pre[i][j][ii] += c.get(idd) * ma[ii][l];
						}
						idd++;
					}					
				}
				for(ii = 0;ii<3;ii++){
					if(Math.abs(pre[i][j][ii] - 255) < Define.EPSILON) pre[i][j][ii] = 255;
					if(Math.abs(pre[i][j][ii]) < Define.EPSILON) pre[i][j][ii] = 0;
				}
			}
		}
		return pre;
	}
	
	public static double[][][] predictClusterNew(int index) {

		int i,ii, j, l, predictID = index + Define.NUM_TRAINING_IMAGE;
		double vecB[];
		vecB = new double[3];
		int idI, cell = Define.CELLSIZE;
		
		String filename = "";
		for (idI = index; idI < Define.NUM_TRAINING_IMAGE + 1; idI++) {
			filename = Define.BASEDPATH + "/matranU_id" + idI + ".txt";
			u.add(Data.readMatrix2(filename));
			if(Transformations.getjCombochooser() == 0){
				filename = Define.BASEDPATH + "/matranE_id" + idI + ".txt";
				e.add(Data.readMatrix2(filename));
			}			
			filename = Define.BASEDPATH + "/center_id" + idI + ".txt";
			center.add(Data.readCenter2(filename));
		}
		double[][][] pre = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
//		mat = new double[3][Define.NUM_CLUSTER * Define.NUM_TRAINING_IMAGE];
				
		for (i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
			for (j = 0; j < Define.IMAGE_SIZE_COL; j++) {
				vecB[0] = Data.getImage(predictID)[i][j].getRed();
				vecB[1] = Data.getImage(predictID)[i][j].getGreen();
				vecB[2] = Data.getImage(predictID)[i][j].getBlue();
				
				Vector b = new CompressedVector(vecB);				
				double [][]mat = Data.getNeighborPixelClusterNew(i, j, index, cell, u, e);				
//				mat2 = Data.getNeighborPixelClusterNew(i, j, index, cell, u, e, true);
								
				Matrix a = new Basic2DMatrix(mat);
				Matrix aT = a.transpose();
				Matrix aTa = aT.multiply(a);
				Vector f = aT.multiply(b);
				// Data.printArray(mat);
				LeastSquaresSolver ls = new LeastSquaresSolver(aTa);
				Vector c = ls.solve(f);
				
				for(ii = 0;ii<3;ii++){					 
					pre[i][j][ii] = 0;
				}
				
//				System.out.println(c.toString());
				mat = Data.getNeighborPixelClusterNew(i, j, index + 1, cell, u, e);				
				for (l = 0; l < c.length(); l++) {
					for(ii = 0;ii<3;ii++){
						pre[i][j][ii] += c.get(l) * mat[ii][l];
					}					
				}				
			}
		}
//		System.out.println("so diem anh phai chinh sua lai la : " + count);
		return pre;
	}
	
	public static double []lagrange(double[][]y, int num, int training, int input){
		double value[] = new double[num];
		double sum = 0, multi;		
		int i,j,k;
		for(k = 0;k<num;k++){
			sum = 0;
			for(i = 0;i<training;i++){
				multi = 1;
				for(j = 0;j<training;j++){
					if(i != j){
						multi *= (double)(input - j) / (i - j);					
					}
				}
				sum += y[i][k] * multi;
			}
			value[k] = sum;
		}
		return value;
	}
	
	public static double []recursion (double[][]y, int num, int training){
		double value[] = new double[num];
		double y1 [][] = new double[num][training - 1];
		
		for (int i = 0; i < num; i++) {
			value[i] = 0;
			for (int j = 0; j < training - 1; j++) {
				y1[i][j] = y[j][i];
			}
		}
		
		double vecB[] = new double[num];
		for (int i = 0; i < num; i++) {
			vecB[i] = y[training - 1][i];
		}
		Vector b = new CompressedVector(vecB);				
		Matrix a = new Basic2DMatrix(y1);
		Matrix aT = a.transpose();
		Matrix aTa = aT.multiply(a);
		Vector f = aT.multiply(b);
		LeastSquaresSolver ls = new LeastSquaresSolver(aTa);
		Vector c = ls.solve(f);
		for(int j = 0;j < num;j++){
			for (int i = 1; i < training; i++) {
				value[j] += c.get(i - 1) * y[i][j];
			}			
		}
		return value;
	}
		
	
}
