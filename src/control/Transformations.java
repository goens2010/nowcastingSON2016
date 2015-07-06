package control;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import org.omg.CORBA.DefinitionKind;

public class Transformations {
	private static int c = 4;

	private static int jCombochooser = 0;

	/**
	 * @return the c
	 */
	public static int getC() {
		return c;
	}

	
	public static double rmse2;
	public static double getRmse2() {
		return rmse2;
	}
	public static double rmse3;
	public static double getRmse3() {
		return rmse3;
	}
	/**
	 * @param c
	 *            the c to set
	 */
	public static void setC(int c) {
		Transformations.c = c;
	}

	public static void setJcombo(int value) {
		jCombochooser = value;

	}

	public static int getjCombochooser() {
		return jCombochooser;
	}

public static BufferedImage flipVertical2(BufferedImage img, int id) {
		BufferedImage dest = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		MatrixPixel mp = new MatrixPixel(img);
		Pixel[][] px = mp.getData2(id); // ghi ra file out_put2.txt
		
		int clus = 4;
		
		FC_PFS2 fc = new FC_PFS2(clus);
		fc.FC_PFS(id); // ghi ra center.txt.
		Pixel[][] p;

		String filename = Define.BASEDPATH + "/out_put2_id" + id + ".txt";
		String filename1 = Define.BASEDPATH + "/matranU_id" + id + ".txt";
		String filename2 = Define.BASEDPATH + "/matranE_id" + id + ".txt";
		String filename3 = Define.BASEDPATH + "/center_id" + id + ".txt";

		//if (jCombochooser == 0) {
			ImageCluster2 mg = new ImageCluster2();

			double[][] u = new double[Define.IMAGE_SIZE_COL
					* Define.IMAGE_SIZE_ROW][clus];
			u = Data.readMatrix2(filename1);
			double[][] e = new double[Define.IMAGE_SIZE_COL
					* Define.IMAGE_SIZE_ROW][clus];
			e = Data.readMatrix2(filename2);
			Pixel[] v = new Pixel[clus];
			v = Data.readCenter2(filename3);
			p = mg.phancum(mg.readData(filename), u, e, v, id);

		//} 
		//if (jCombochooser == 1){
		/*else {
			ImageClusterFCM mg = new ImageClusterFCM();

			p = mg.phancum(mg.readData(filename), mg.readMatrix(filename1),
					mg.readCenter(filename3), id);

		}*/

		// Flip vertical and horizontal
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int red = (int) p[x][y].getRed();
				int green = (int) p[x][y].getGreen();
				int blue = (int) p[x][y].getBlue();

				Color c = new Color(red, green, blue);

				dest.setRGB(x, y, c.getRGB());
			}
		}
		return dest;
	}

public static BufferedImage flipVertical21(BufferedImage img, int id) {
	BufferedImage dest = new BufferedImage(img.getWidth(), img.getHeight(),
			BufferedImage.TYPE_INT_ARGB);

	MatrixPixel mp = new MatrixPixel(img);
	Pixel[][] px = mp.getData2(id); // ghi ra file out_put2.txt
	
	int clus = 4;
	
	FC_PFS2 fc = new FC_PFS2(clus);
	fc.FC_PFS(id); // ghi ra center.txt.
	Pixel[][] p;

	String filename = Define.BASEDPATH + "/out_put2_id" + id + ".txt";
	String filename1 = Define.BASEDPATH + "/matranU_id" + id + ".txt";
	String filename2 = Define.BASEDPATH + "/matranE_id" + id + ".txt";
	String filename3 = Define.BASEDPATH + "/center_id" + id + ".txt";

	/*if (jCombochooser == 0) {
		ImageCluster2 mg = new ImageCluster2();

		double[][] u = new double[Define.IMAGE_SIZE_COL
				* Define.IMAGE_SIZE_ROW][clus];
		u = Data.readMatrix2(filename1);
		double[][] e = new double[Define.IMAGE_SIZE_COL
				* Define.IMAGE_SIZE_ROW][clus];
		e = Data.readMatrix2(filename2);
		Pixel[] v = new Pixel[clus];
		v = Data.readCenter2(filename3);
		p = mg.phancum(mg.readData(filename), u, e, v, id);

	} */
	
	//else {
		ImageClusterFCM mg = new ImageClusterFCM();

		p = mg.phancum(mg.readData(filename), mg.readMatrix(filename1),
				mg.readCenter(filename3), id);

	//}

	// Flip vertical and horizontal
	for (int x = 0; x < img.getWidth(); x++) {
		for (int y = 0; y < img.getHeight(); y++) {
			int red = (int) p[x][y].getRed();
			int green = (int) p[x][y].getGreen();
			int blue = (int) p[x][y].getBlue();

			Color c = new Color(red, green, blue);

			dest.setRGB(x, y, c.getRGB());
		}
	}
	return dest;
}


	public static BufferedImage flipVertical(BufferedImage img, int id) {
		BufferedImage dest = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		MatrixPixel mp = new MatrixPixel(img);
		Pixel[][] px = mp.getData(id); // ghi ra file out_put2.txt
		//System.out.println("px = "+ px[0].toString());
		
		//AFC_PSO afc = new AFC_PSO();
		//int clus = afc.AFC_PSO(px,id);
		
		int clus = 4;
		FC_PFS2 fc = new FC_PFS2(clus);
		fc.FC_PFS(id); // ghi ra center.txt.
		//FCM fc = new FCM();
		//fc.FCM(id);
		Pixel[][] p;

		String filename = Define.BASEDPATH + "/out_put2_id" + id + ".txt";
		String filename1 = Define.BASEDPATH + "/matranU_id" + id + ".txt";
		String filename2 = Define.BASEDPATH + "/matranE_id" + id + ".txt";
		String filename3 = Define.BASEDPATH + "/center_id" + id + ".txt";

		if (jCombochooser == 0) {
			ImageCluster2 mg = new ImageCluster2();
			// Data.printArray(Data.readMatrix(filename1));
			// Data.printCenter(Data.readCenter(filename3));
			// p = mg.phancum(mg.readData(filename), mg.readMatrix(filename1),
			// mg.readMatrix(filename2),mg.readCenter(filename3),id);

			double[][] u = new double[Define.IMAGE_SIZE_COL
					* Define.IMAGE_SIZE_ROW][clus];
			u = Data.readMatrix2(filename1);
			double[][] e = new double[Define.IMAGE_SIZE_COL
					* Define.IMAGE_SIZE_ROW][clus];
			e = Data.readMatrix2(filename2);
			Pixel[] v = new Pixel[clus];
			v = Data.readCenter2(filename3);
			p = mg.phancum(mg.readData(filename), u, e, v, id);

			// p = mg.phancum(mg.readData(filename),
			// Data.readMatrix(filename1),Data.readMatrix(filename2),Data.readCenter(filename3),id);
			// p = mg.phancum(mg.readData("out_put2.txt"),
			// mg.readMatrix("matranU.txt"),
			// mg.readMatrix("matranE.txt"),mg.readCenter("center.txt"));
		} else {
			ImageClusterFCM mg = new ImageClusterFCM();

			p = mg.phancum(mg.readData(filename), mg.readMatrix(filename1),
					mg.readCenter(filename3), id);
			// p = mg.phancum(mg.readData("out_put2.txt"),
			// mg.readMatrix("matranU.txt"),mg.readCenter("center.txt"));
		}

		// Flip vertical and horizontal
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int red = (int) p[x][y].getRed();
				int green = (int) p[x][y].getGreen();
				int blue = (int) p[x][y].getBlue();

				Color c = new Color(red, green, blue);

				dest.setRGB(x, y, c.getRGB());
			}
		}
		return dest;
	}
	
	/*public static BufferedImage flipVertical2(BufferedImage img, int id) {
		BufferedImage dest = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		MatrixPixel mp = new MatrixPixel(img);
		Pixel[][] px = mp.getData2(id); // ghi ra file out_put2.txt
		//System.out.println("px = "+ px[0].toString());
		FC_PFS2 fc = new FC_PFS2(c);
		fc.FC_PFS(id); // ghi ra center.txt.
		//FCM fc = new FCM();
		//fc.FCM(id);
		Pixel[][] p;

		String filename = Define.BASEDPATH + "/outputnew_id" + id + ".txt";
		String filename1 = Define.BASEDPATH + "/matranU_id" + id + ".txt";
		String filename2 = Define.BASEDPATH + "/matranE_id" + id + ".txt";
		String filename3 = Define.BASEDPATH + "/center_id" + id + ".txt";

		if (jCombochooser == 0) {
			ImageCluster2 mg = new ImageCluster2();
			// Data.printArray(Data.readMatrix(filename1));
			// Data.printCenter(Data.readCenter(filename3));
			// p = mg.phancum(mg.readData(filename), mg.readMatrix(filename1),
			// mg.readMatrix(filename2),mg.readCenter(filename3),id);

			double[][] u = new double[Define.IMAGE_SIZE_COL
					* Define.IMAGE_SIZE_ROW][Define.NUM_CLUSTER];
			u = Data.readMatrix2(filename1);
			double[][] e = new double[Define.IMAGE_SIZE_COL
					* Define.IMAGE_SIZE_ROW][Define.NUM_CLUSTER];
			e = Data.readMatrix2(filename2);
			Pixel[] v = new Pixel[Define.NUM_CLUSTER];
			v = Data.readCenter2(filename3);
			p = mg.phancum(mg.readData(filename), u, e, v, id);

			// p = mg.phancum(mg.readData(filename),
			// Data.readMatrix(filename1),Data.readMatrix(filename2),Data.readCenter(filename3),id);
			// p = mg.phancum(mg.readData("out_put2.txt"),
			// mg.readMatrix("matranU.txt"),
			// mg.readMatrix("matranE.txt"),mg.readCenter("center.txt"));
		} else {
			ImageClusterFCM mg = new ImageClusterFCM();

			p = mg.phancum(mg.readData(filename), mg.readMatrix(filename1),
					mg.readCenter(filename3), id);
			// p = mg.phancum(mg.readData("out_put2.txt"),
			// mg.readMatrix("matranU.txt"),mg.readCenter("center.txt"));
		}

		// Flip vertical and horizontal
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int red = (int) p[x][y].getRed();
				int green = (int) p[x][y].getGreen();
				int blue = (int) p[x][y].getBlue();

				Color c = new Color(red, green, blue);

				dest.setRGB(x, y, c.getRGB());
			}
		}
		return dest;
	}*/

	/*public static BufferedImage ForecastImage() {
		
		 * BufferedImage dest = new BufferedImage(Define.IMAGE_SIZE_ROW,
		 * Define.IMAGE_SIZE_ROW, BufferedImage.TYPE_INT_ARGB); Pixel [][]p =
		 * Star.predictedImage(0); for (int x = 0; x < Define.IMAGE_SIZE_ROW;
		 * x++) { for (int y = 0; y < Define.IMAGE_SIZE_ROW; y++) { int red =
		 * (int) p[x][y].getRed(); int green = (int) p[x][y].getGreen(); int
		 * blue = (int) p[x][y].getBlue();
		 * 
		 * Color c = new Color(red, green, blue);
		 * 
		 * dest.setRGB(x, y, c.getRGB()); } }
		 
		return Star.predictedImage(0);
	}*/

	public static BufferedImage[] forcast() {
		BufferedImage[] fore = new BufferedImage[3];
		
		LuatMo2 luatmo = new LuatMo2();
		
		int cluster = Define.NUM_RULES;
		int number_image = Define.NUM_TRAIN_IMG;
		//Pixel[][] p = new Pixel[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
		
		FC_PFS2 fc = new FC_PFS2(cluster);
		Pixel[][] array = LuatMo2.training_sple2(number_image);
		FC_PFS2.FC_PFS(array);
		
		ArrayList<double[][][]> li = LuatMo2.getRate(number_image);
		for(int i = 0; i < li.size(); i++){
			LuatMo2.writeFile(Define.path + "\\MangChenhlech"+i+".txt", li.get(i));
		}
		ArrayList<ArrayList<double[][][]>> list = LuatMo2.getRegionOfDependence(li, number_image);
		
		li = LuatMo2.getU(number_image);
		ArrayList<ArrayList<double[][][]>> list_u = LuatMo2.getRegionOfDependence(li, number_image);
		
		 ArrayList<double[][][]> result = LuatMo2.luat(list, list_u);
		String filename = Define.path+"\\out_put2_id" + (number_image - 1) + ".txt";
		String filename2 = Define.path+"\\out_put2_id" + number_image + ".txt";
		double[][] rate = LuatMo2.rates(FC_PFS2.readData(filename),
				FC_PFS2.readData(filename2));
		double[][][] pi = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		int count = 0;
		for (int t = 0; t < Define.IMAGE_SIZE_ROW; t++) {
			for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {
				pi[t][j][0] = rate[count][0];
				pi[t][j][1] = rate[count][1];
				pi[t][j][2] = rate[count][2];
				count++;
			}
		}
		LuatMo2.writeFile(Define.path + "\\ChenhlechCuoi.txt", pi);
		ArrayList<double[][][]> data = new ArrayList<double[][][]>();
		//data = LuatMo2.getRegionImg(pi);
		for (int i = 0; (i+Define.MOVE) < Define.IMAGE_SIZE_ROW; i += Define.MOVE) {
			for (int j = 0; (j+Define.MOVE) < Define.IMAGE_SIZE_COL; j += Define.MOVE) {
				double[][][] pix = LuatMo2.getRegion(i, j, Define.MOVE, pi);
				data.add(pix);
			}
		}
		// mang list_out chứa kết quả output của 25 vùng.
		ArrayList<double[][][]> list_out = new ArrayList<double[][][]>();
		// System.out.println("..="+ list.get(0).get(0).length);
		for (int k = 0; k < data.size(); k++) {
			double[][][] output = luatmo.tinh_O(list.get(k), result.get(k), data.get(k));
			LuatMo2.writeFile(Define.path + "/sub_output"+k +".txt", output);
			list_out.add(output);
		}

		ArrayList<double[][][]> mergerList = LuatMo2.mergerList(list_out);
		// System.out.println(".. = "+mergerList.get(0)[0][0][0]);
		double[][][] output = LuatMo2.megerRow(mergerList);
		LuatMo2.writeFile(Define.path + "\\output.txt", output);
		
		int[][] median = new int[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
		Pixel[] M = FC_PFS2.readData(Define.path+"\\out_put2_id"+number_image+ ".txt");
		Pixel[][] before = new Pixel[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
		int d = 0;
		for (int i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
			for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {
				before[i][j] = M[d++];
			}
		}
		//p = LuatMo2.forecast(output,number_image);
		double [][][] forecast1 = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		forecast1 = LuatMo2.forecast2(output, number_image);
		
		int dd = 0;
		double[][][] befor = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL;j++){
				befor[i][j][0] = M[dd].getRed();
				befor[i][j][1] = M[dd].getGreen();
				befor[i][j][2] = M[dd].getBlue();
				dd++;
			}
		}
		
		
		
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int flag;
		int flag2;
		int r = 0,g = 0,b = 0;
		for (int x = 0; x < Define.IMAGE_SIZE_ROW; x++) {
			for (int y = 0; y < Define.IMAGE_SIZE_COL; y++) {
				flag = 0;
				flag2 = 0;
				int red = (int) forecast1[x][y][0];
				int green = (int) forecast1[x][y][1];
				int blue = (int) forecast1[x][y][2];
				
				if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
					
					
					if(red < -5 || red > 260){
						r++;
						red = (int) before[x][y].getRed();
						//red = (int) forecast1[x][y][0];
						//red = (int) forecast2[x][y][0];
					}
					if(green < -5 || green > 260){
						g++;
						green = (int) before[x][y].getGreen();
						//green = (int) forecast1[x][y][1];
						//green = (int) forecast2[x][y][1];
					}
					if(blue < -5 || blue > 260){
						b++;
						blue = (int) before[x][y].getBlue();
						//blue = (int) forecast1[x][y][2];
						//blue = (int) forecast2[x][y][2];
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
				// Color c = new Color((int)tg[0],(int)tg[1],(int)tg[2]);
				// pre.setRGB(y, x, c.getRGB());
				median[x][y] = c.getRGB();

			}
		}
		fore[0] = new BufferedImage(Define.IMAGE_SIZE_ROW, Define.IMAGE_SIZE_COL,BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < Define.IMAGE_SIZE_ROW; x++) {
			for (int y = 0; y < Define.IMAGE_SIZE_COL; y++) {
				fore[0].setRGB(y, x, getAmedian(x, y, median));
			}
		}
		System.out.println("count = " + count1);
		System.out.println("r = "+ r+ "  g = "+ g+ "  b = "+ b);
		
		
		double[][][] forecast2 = LuatMo2.beforeTransform(befor, forecast1, list_u, result);  // anh du bao thu 2
		double [][][] anhthuc = new double [Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		  Pixel[] M2 = FC_PFS2.readData(Define.path + "/out_put2_id"+ (number_image +2) +".txt");
		  int dem = 0;double tg,tg1;
		  rmse2 = 0;
		
		int[][] median2 = LuatMo2.median(forecast2, forecast1);
		fore[1] = new BufferedImage(Define.IMAGE_SIZE_ROW, Define.IMAGE_SIZE_COL,BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < Define.IMAGE_SIZE_ROW; x++) {
			for (int y = 0; y < Define.IMAGE_SIZE_COL; y++) {
				fore[1].setRGB(y, x, getAmedian(x, y, median2));
			}
		}
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
				tg = 0;tg1 = 0;
				/*for(int k = 0; k < 3; k++){
					tg += (forecast3[i][j][k] - anhthuc[i][j][k]) * (forecast3[i][j][k] - anhthuc[i][j][k]);
					tg1 += anhthuc[i][j][k];
				}*/
				Color df = new Color(fore[1].getRGB(i, j));
				tg = ( df.getRed() - Data.getPixel_goc(i, j, 5).getRed()) * (df.getRed() - Data.getPixel_goc(i, j, 5).getRed())+
						(df.getGreen() - Data.getPixel_goc(i, j, 5).getGreen())*(df.getGreen() - Data.getPixel_goc(i, j, 5).getGreen())+
						(df.getBlue() - Data.getPixel_goc(i, j, 5).getBlue())*(df.getBlue() - Data.getPixel_goc(i, j, 5).getBlue());
				tg1 = df.getRed() + df.getGreen() +df.getBlue();
				if(tg1 == 0){
					System.out.println("tg1_3 = " + tg1);
					//rmse3 += (tg+1)/(tg1 + 1);
				}else
				rmse2 += tg/tg1;
				
			}
		}
		rmse2 = Math.sqrt(rmse2 /(Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));
		
		double [][][] forecast3 = LuatMo2.beforeTransform(forecast1, forecast2, list_u, result);  //anh du bao thu 3
		 Pixel[] M3 = FC_PFS2.readData(Define.path + "/out_put2_id"+ (number_image +3) +".txt");
		   dem = 0;rmse3 = 0;
		
		int[][] median3 = LuatMo2.median(forecast3, forecast2);
		fore[2] = new BufferedImage(Define.IMAGE_SIZE_ROW, Define.IMAGE_SIZE_COL,BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < Define.IMAGE_SIZE_ROW; x++) {
			for (int y = 0; y < Define.IMAGE_SIZE_COL; y++) {
				fore[2].setRGB(y, x, getAmedian(x, y, median3));
			}
		}
		// System.out.println("So diem chenh lech nhieu = "+ count2);
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
				tg = 0;tg1 = 0;
				Color df = new Color(fore[2].getRGB(i, j));
				tg = ( df.getRed() - Data.getPixel_goc(i, j, 6).getRed()) * (df.getRed() - Data.getPixel_goc(i, j, 6).getRed())+
						(df.getGreen() - Data.getPixel_goc(i, j, 6).getGreen())*(df.getGreen() - Data.getPixel_goc(i, j, 6).getGreen())+
						(df.getBlue() - Data.getPixel_goc(i, j, 6).getBlue())*(df.getBlue() - Data.getPixel_goc(i, j, 6).getBlue());
				tg1 = df.getRed() + df.getGreen() +df.getBlue();
				if(tg1 == 0){
					System.out.println("tg1_3 = " + tg1);
					//rmse3 += (tg+1)/(tg1 + 1);
				}else
				rmse3 += tg/tg1;
				
			}
		}
		rmse3 = Math.sqrt(rmse3 /(Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));
		return fore;
	}

	public static BufferedImage[] forcastAFC() {
		
		//BufferedImage pre = new BufferedImage(Define.IMAGE_SIZE_ROW,
				//Define.IMAGE_SIZE_COL, BufferedImage.TYPE_INT_ARGB);
		BufferedImage[] fore = new BufferedImage[3];
		// LuatMo rule = new LuatMo();

		// int index = 5;
		/*
		 * Pixel [][] data = new Pixel [index+1][]; Pixel[][][]pi = new
		 * Pixel[index+1][Define.IMAGE_SIZE_COL][Define.IMAGE_SIZE_ROW]; for(int
		 * i = 0; i <= index; i++){ data[i] = FC_PFS2.readData("out_put2_id"+
		 * i+".txt"); int count = 0; for(int m = 0; m < Define.IMAGE_SIZE_ROW;
		 * m++){ for(int n = 0; n < Define.IMAGE_SIZE_COL; n++){ pi[i][m][n] =
		 * data[i][count++]; } } }
		 */
		/*
		 * p.a 1 Pixel [][] p; Pixel[][] array = rule.training_sple2(4); FC_PFS2
		 * fc = new FC_PFS2(10); FC_PFS2.FC_PFS(array); double[][][] a =
		 * rule.cluster_rule(array, 10);
		 * 
		 * 
		 * int ii = array.length; String filename = "out_put2_id"+ ii+ ".txt";
		 * String filename2 = "out_put2_id" +(ii+1) + ".txt"; Pixel [] rate =
		 * rule.rate(FC_PFS2.readData(filename), FC_PFS2.readData(filename2));
		 * //Pixel [] rate = rule.rate(data[ii], data[ii+1]); double[][] output
		 * = rule.tinh_O(array, a,rate);
		 * 
		 * //double[][] output = rule.tinh_O(array, a); p = rule.forecast(
		 * output);
		 */

		// p.a 2
		
		LuatMoAFC luatmo = new LuatMoAFC();
		
		int clus = luatmo.cluster;
		int number_image = Define.NUM_TRAIN_IMG;
		//Pixel[][] p = new Pixel[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
		
		FC_PFS2 fc = new FC_PFS2(clus);
		Pixel[][] array = LuatMo2.training_sple2(number_image);
		FC_PFS2.FC_PFS(array);
		
		ArrayList<double[][][]> li = LuatMo2.getRate(number_image);
		for(int i = 0; i < li.size(); i++){
			LuatMo2.writeFile(Define.path + "\\MangChenhlech"+i+".txt", li.get(i));
		}
		//ArrayList<ArrayList<double[][][]>> list = LuatMo2.getRegionOfImages2(li, number_image);
		ArrayList<ArrayList<double[][][]>> list = LuatMo2.getRegionOfDependence(li, number_image);
		
		li = LuatMo2.getU(number_image);
		ArrayList<ArrayList<double[][][]>> list_u = LuatMo2.getRegionOfDependence(li, number_image);
		//ArrayList<ArrayList<double[][][]>> list_u = LuatMo2.getRegionOfImages2(li, number_image);
		
		
		//ArrayList<ArrayList<double[][][]>> list_u = LuatMo2.getRegionOfDependence(li, number_image);
		
		 //li = LuatMo2.getU1(number_image);
		 //ArrayList<ArrayList<double [][][]>> list_u1 = LuatMo2.getRegionOfDependence(li, number_image);
		 

		//ArrayList<double[][][]> result = LuatMo2.luat(list, list_u,list_u1);
		 ArrayList<double[][][]> result = LuatMo2.luat(list, list_u);
		String filename = Define.path+"\\out_put2_id" + (number_image - 1) + ".txt";
		String filename2 = Define.path+"\\out_put2_id" + number_image + ".txt";
		double[][] rate = LuatMo2.rates(FC_PFS2.readData(filename),
				FC_PFS2.readData(filename2));
		double[][][] pi = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		int count = 0;
		for (int t = 0; t < Define.IMAGE_SIZE_ROW; t++) {
			for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {
				pi[t][j][0] = rate[count][0];
				pi[t][j][1] = rate[count][1];
				pi[t][j][2] = rate[count][2];
				count++;
			}
		}
		LuatMo2.writeFile(Define.path + "\\ChenhlechCuoi.txt", pi);
		ArrayList<double[][][]> data = new ArrayList<double[][][]>();
		//data = LuatMo2.getRegionImg(pi);
		for (int i = 0; (i+Define.MOVE) < Define.IMAGE_SIZE_ROW; i += Define.MOVE) {
			for (int j = 0; (j+Define.MOVE) < Define.IMAGE_SIZE_COL; j += Define.MOVE) {
				double[][][] pix = LuatMo2.getRegion(i, j, Define.MOVE, pi);
				data.add(pix);
			}
		}
		// mang list_out chứa kết quả output của 25 vùng.
		ArrayList<double[][][]> list_out = new ArrayList<double[][][]>();
		// System.out.println("..="+ list.get(0).get(0).length);
		for (int k = 0; k < data.size(); k++) {
			double[][][] output = luatmo.tinh_O(list.get(k), result.get(k), data.get(k));
			LuatMo2.writeFile(Define.path + "/sub_output"+k +".txt", output);
			list_out.add(output);
		}

		ArrayList<double[][][]> mergerList = LuatMo2.mergerList(list_out);
		// System.out.println(".. = "+mergerList.get(0)[0][0][0]);
		double[][][] output = LuatMo2.megerRow(mergerList);
		LuatMo2.writeFile(Define.path + "\\output.txt", output);
		
		int[][] median = new int[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
		Pixel[] M = FC_PFS2.readData(Define.path+"\\out_put2_id"+number_image+ ".txt");
		Pixel[][] before = new Pixel[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL];
		int d = 0;
		for (int i = 0; i < Define.IMAGE_SIZE_ROW; i++) {
			for (int j = 0; j < Define.IMAGE_SIZE_COL; j++) {
				before[i][j] = M[d++];
			}
		}
		//p = LuatMo2.forecast(output,number_image);
		double [][][] forecast1 = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		forecast1 = LuatMo2.forecast2(output, number_image);
		
		int dd = 0;
		double[][][] befor = new double[Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL;j++){
				befor[i][j][0] = M[dd].getRed();
				befor[i][j][1] = M[dd].getGreen();
				befor[i][j][2] = M[dd].getBlue();
				dd++;
			}
		}
		
		
		
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int flag;
		int flag2;
		int r = 0,g = 0,b = 0;
		for (int x = 0; x < Define.IMAGE_SIZE_ROW; x++) {
			for (int y = 0; y < Define.IMAGE_SIZE_COL; y++) {
				flag = 0;
				flag2 = 0;
				int red = (int) forecast1[x][y][0];
				int green = (int) forecast1[x][y][1];
				int blue = (int) forecast1[x][y][2];
				/*int red = (int) forecast2[x][y][0];
				int green = (int) forecast2[x][y][1];
				int blue = (int) forecast2[x][y][2];*/
				/*int red = (int) forecast3[x][y][0];
				int green = (int) forecast3[x][y][1];
				int blue = (int) forecast3[x][y][2];*/

				if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
					
					
					if(red < -5 || red > 260){
						r++;
						red = (int) before[x][y].getRed();
						//red = (int) forecast1[x][y][0];
						//red = (int) forecast2[x][y][0];
					}
					if(green < -5 || green > 260){
						g++;
						green = (int) before[x][y].getGreen();
						//green = (int) forecast1[x][y][1];
						//green = (int) forecast2[x][y][1];
					}
					if(blue < -5 || blue > 260){
						b++;
						blue = (int) before[x][y].getBlue();
						//blue = (int) forecast1[x][y][2];
						//blue = (int) forecast2[x][y][2];
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
					/*
					System.out.println("vi pham:  " + red + " " + green + " " + blue);
					red = (int) before[x][y].getRed();
					green = (int) before[x][y].getGreen();
					blue = (int) before[x][y].getBlue();
					System.out.println("sua:  " + red + " " + green + " "
							+ blue);*/
					count1++;
				}

				Color c = new Color(red, green, blue);
				// Color c = new Color((int)tg[0],(int)tg[1],(int)tg[2]);
				// pre.setRGB(y, x, c.getRGB());
				median[x][y] = c.getRGB();

			}
		}
		fore[0] = new BufferedImage(Define.IMAGE_SIZE_ROW, Define.IMAGE_SIZE_COL,BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < Define.IMAGE_SIZE_ROW; x++) {
			for (int y = 0; y < Define.IMAGE_SIZE_COL; y++) {
				fore[0].setRGB(y, x, getAmedian(x, y, median));
			}
		}
		System.out.println("count = " + count1);
		System.out.println("r = "+ r+ "  g = "+ g+ "  b = "+ b);
		
		
		double[][][] forecast2 = LuatMo2.beforeTransform(befor, forecast1, list_u, result);  // anh du bao thu 2
		double [][][] anhthuc = new double [Define.IMAGE_SIZE_ROW][Define.IMAGE_SIZE_COL][3];
		  Pixel[] M2 = FC_PFS2.readData(Define.path + "/out_put2_id"+ (number_image +2) +".txt");
		  int dem = 0;double tg,tg1;
		  rmse2 = 0;
		  for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
				for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
					anhthuc[i][j][0] = M2[dem].getRed();
					//a[i][j][0] = M[count].getRed() + 1 ;
					anhthuc[i][j][1] = M2[dem].getGreen();
					//a[i][j][1] = M[count].getGreen()+1;
					anhthuc[i][j][2] = M2[dem].getBlue();
				}
		  }
		 /* for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
				for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
					tg = 0;tg1 = 0;
					for(int k = 0; k < 3; k++){
						tg += (forecast2[i][j][k] - anhthuc[i][j][k]) * (forecast2[i][j][k] - anhthuc[i][j][k]);
						tg1 += anhthuc[i][j][k];
					}
					tg = (forecast2[i][j][0] - Data.getPixel_goc(i, j, 5).getRed()) * (forecast2[i][j][0] - Data.getPixel_goc(i, j, 5).getRed())+
							(forecast2[i][j][1] - Data.getPixel_goc(i, j, 5).getGreen())*(forecast2[i][j][1] - Data.getPixel_goc(i, j, 5).getGreen())+
							(forecast2[i][j][2] - Data.getPixel_goc(i, j, 5).getBlue())*(forecast2[i][j][2] - Data.getPixel_goc(i, j, 5).getBlue());
					tg1 = forecast2[i][j][0] + forecast2[i][j][1] +forecast2[i][j][2];
					if(tg1 == 0){
						System.out.println("tg1_2 = " + tg1);
						//rmse2 += (tg+1)/(tg1 + 1);
					}else
					rmse2 += tg/tg1;
				}
			}
			rmse2 = Math.sqrt(rmse2 /(Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));*/
		int[][] median2 = LuatMo2.median(forecast2, forecast1);
		fore[1] = new BufferedImage(Define.IMAGE_SIZE_ROW, Define.IMAGE_SIZE_COL,BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < Define.IMAGE_SIZE_ROW; x++) {
			for (int y = 0; y < Define.IMAGE_SIZE_COL; y++) {
				fore[1].setRGB(y, x, getAmedian(x, y, median2));
			}
		}
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
				tg = 0;tg1 = 0;
				/*for(int k = 0; k < 3; k++){
					tg += (forecast3[i][j][k] - anhthuc[i][j][k]) * (forecast3[i][j][k] - anhthuc[i][j][k]);
					tg1 += anhthuc[i][j][k];
				}*/
				Color df = new Color(fore[1].getRGB(i, j));
				/*tg = ( df.getRed() - Data.getPixel_goc(i, j, 5).getRed()) * (df.getRed() - Data.getPixel_goc(i, j, 5).getRed())+
						(df.getGreen() - Data.getPixel_goc(i, j, 5).getGreen())*(df.getGreen() - Data.getPixel_goc(i, j, 5).getGreen())+
						(df.getBlue() - Data.getPixel_goc(i, j, 5).getBlue())*(df.getBlue() - Data.getPixel_goc(i, j, 5).getBlue());*/
				tg = ( df.getRed() - anhthuc[i][j][0]) * (df.getRed() - anhthuc[i][j][0])+
						(df.getGreen() - anhthuc[i][j][1])*(df.getGreen() - anhthuc[i][j][1])+
						(df.getBlue() - anhthuc[i][j][2])*(df.getBlue() - anhthuc[i][j][2]);
				tg1 = df.getRed() + df.getGreen() +df.getBlue();
				if(tg1 == 0){
					System.out.println("tg1_3 = " + tg1);
					//rmse3 += (tg+1)/(tg1 + 1);
				}else
				rmse2 += tg/tg1;
				
			}
		}
		rmse2 = Math.sqrt(rmse2 /(Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));
		
		double [][][] forecast3 = LuatMo2.beforeTransform(forecast1, forecast2, list_u, result);  //anh du bao thu 3
		 Pixel[] M3 = FC_PFS2.readData(Define.path + "/out_put2_id"+ (number_image +3) +".txt");
		   dem = 0;rmse3 = 0;
		 for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
				for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
					anhthuc[i][j][0] = M3[dem].getRed();
					//a[i][j][0] = M[count].getRed() + 1 ;
					anhthuc[i][j][1] = M3[dem].getGreen();
					//a[i][j][1] = M[count].getGreen()+1;
					anhthuc[i][j][2] = M3[dem].getBlue();
				}
		  }
		  /*for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
				for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
					tg = 0;tg1 = 0;
					for(int k = 0; k < 3; k++){
						tg += (forecast3[i][j][k] - anhthuc[i][j][k]) * (forecast3[i][j][k] - anhthuc[i][j][k]);
						tg1 += anhthuc[i][j][k];
					}
					
					tg = (forecast3[i][j][0] - Data.getPixel_goc(i, j, 6).getRed()) * (forecast3[i][j][0] - Data.getPixel_goc(i, j, 6).getRed())+
							(forecast3[i][j][1] - Data.getPixel_goc(i, j, 6).getGreen())*(forecast3[i][j][1] - Data.getPixel_goc(i, j, 6).getGreen())+
							(forecast3[i][j][2] - Data.getPixel_goc(i, j, 6).getBlue())*(forecast3[i][j][2] - Data.getPixel_goc(i, j, 6).getBlue());
					tg1 = forecast3[i][j][0] + forecast3[i][j][1] +forecast3[i][j][2];
					if(tg1 == 0){
						System.out.println("tg1_3 = " + tg1);
						//rmse3 += (tg+1)/(tg1 + 1);
					}else
					rmse3 += tg/tg1;
					
				}
			}
			rmse3 = Math.sqrt(rmse3 /(Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));*/
		int[][] median3 = LuatMo2.median(forecast3, forecast2);
		fore[2] = new BufferedImage(Define.IMAGE_SIZE_ROW, Define.IMAGE_SIZE_COL,BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < Define.IMAGE_SIZE_ROW; x++) {
			for (int y = 0; y < Define.IMAGE_SIZE_COL; y++) {
				fore[2].setRGB(y, x, getAmedian(x, y, median3));
			}
		}
		// System.out.println("So diem chenh lech nhieu = "+ count2);
		for(int i = 0; i < Define.IMAGE_SIZE_ROW; i++){
			for(int j = 0; j < Define.IMAGE_SIZE_COL; j++){
				tg = 0;tg1 = 0;
				/*for(int k = 0; k < 3; k++){
					tg += (forecast3[i][j][k] - anhthuc[i][j][k]) * (forecast3[i][j][k] - anhthuc[i][j][k]);
					tg1 += anhthuc[i][j][k];
				}*/
				Color df = new Color(fore[2].getRGB(i, j));
				/*tg = ( df.getRed() - Data.getPixel_goc(i, j, 6).getRed()) * (df.getRed() - Data.getPixel_goc(i, j, 6).getRed())+
						(df.getGreen() - Data.getPixel_goc(i, j, 6).getGreen())*(df.getGreen() - Data.getPixel_goc(i, j, 6).getGreen())+
						(df.getBlue() - Data.getPixel_goc(i, j, 6).getBlue())*(df.getBlue() - Data.getPixel_goc(i, j, 6).getBlue());*/
				tg = ( df.getRed() - anhthuc[i][j][0]) * (df.getRed() - anhthuc[i][j][0])+
						(df.getGreen() - anhthuc[i][j][1])*(df.getGreen() - anhthuc[i][j][1])+
						(df.getBlue() - anhthuc[i][j][2])*(df.getBlue() - anhthuc[i][j][2]);
				tg1 = df.getRed() + df.getGreen() +df.getBlue();
				if(tg1 == 0){
					System.out.println("tg1_3 = " + tg1);
					//rmse3 += (tg+1)/(tg1 + 1);
				}else
				rmse3 += tg/tg1;
				
			}
		}
		rmse3 = Math.sqrt(rmse3 /(Define.IMAGE_SIZE_ROW * Define.IMAGE_SIZE_COL));
		return fore;
	}

	/*
	 * public static BufferedImage ClusterFCM(BufferedImage img,int id) {
	 * BufferedImage dest = new BufferedImage(img.getWidth(), img.getHeight(),
	 * BufferedImage.TYPE_INT_ARGB);
	 * 
	 * MatrixPixel mp = new MatrixPixel(img); Pixel[][] px = mp.getData(id); FCM
	 * fc = new FCM(c); fc.FCM();
	 * 
	 * ImageClusterFCM mg = new ImageClusterFCM();
	 * 
	 * Pixel[][] p = mg.phancum(mg.readData("out_put2.txt"),
	 * mg.readMatrix("matranU.txt"),mg.readCenter("center.txt"));
	 * 
	 * //Flip vertical and horizontal for (int x = 0; x < img.getWidth(); x++) {
	 * for (int y = 0; y < img.getHeight(); y++) { int red = (int)
	 * p[x][y].getRed(); int green = (int) p[x][y].getGreen(); int blue = (int)
	 * p[x][y].getBlue();
	 * 
	 * Color c = new Color(red, green, blue);
	 * 
	 * dest.setRGB(x, y, c.getRGB()); } } return dest; }
	 */
	private static int sizeMax = (int) (Define.IMAGE_SIZE_ROW
			* Define.IMAGE_SIZE_COL / 4);

	private static int getAmedian(int i, int j, int[][] pi) {

		int rstart, rstop, cstart, cstop;
		int ii, jj;
		int size, sx = 1, sy = 1, count = 0, tg, median = 0;

		while (true) {
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

			// System.out.println("rsa=" + rstart + " rso=" + rstop + " csa=" +
			// cstart + " cso=" + cstop + " size=" + size + " sizemax=" +
			// sizeMax);

			if (size >= sizeMax) {
				// System.out.println(pi[i][j]);
				return pi[i][j];
			}

			count = 0;
			int[] medi = new int[size - 1];
			// find the median

			for (ii = rstart; ii < rstop; ii++) {
				for (jj = cstart; jj < cstop; jj++) {
					if (ii != i || jj != j) {
						medi[count] = pi[ii][jj];
						count++;
					}
				}
			}

			for (ii = 0; ii < size - 2; ii++) {
				for (jj = ii; jj < size - 1; jj++) {
					if (medi[ii] < medi[jj]) {
						tg = medi[ii];
						medi[ii] = medi[jj];
						medi[jj] = tg;
					}
				}
			}

			tg = (int) ((size - 1) / 2);
			median = medi[tg];
			// System.out.println("count=" + count + "median(" + tg + ") = " +
			// median + " p[" + i + "," + j + "]" + " = " + pi[i][j]);

			if (median - medi[size - 2] > 0 && median - medi[0] < 0) {
				if (pi[i][j] - medi[size - 2] > 0 && pi[i][j] - medi[0] < 0) {
					return pi[i][j];
				} else {
					pi[i][j] = median;
					return median;
				}
			} else {
				sx++;
				sy++;
			}
		}
		// find min - max
	}

}
