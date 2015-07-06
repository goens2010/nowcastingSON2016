package control;

import java.util.Random;

public class AFC_PSO {
	Random randomGenerator = new Random();
	
	//String path = "C:/Users/Administrator/Desktop/Forecast/Test1/Data";
	String path = Define.BASEDPATH;
	int Cmax = 15; int Cmin = 2;
	int popsize = 5;
	int[] Ci = new int[popsize];
	static int N = 2*100*100;
	int maxStep = 100;
	double eps = 0.005;
	
	double[][]u ;
	double[][]e ;
	double[][]Upbest;
	double[][]Epbest;
	double[][]Ugbest;
	double[][]Egbest;
	
	public int AFC_PSO(){
		double Gbestx = 0, GbestC = 0, Gbest = 0;
		int t = 0;
		double min = 0; 
		do{
			t = t + 1;
			
			double Pbest = 0;
			Gbest = Pbest;
			double Pbestx = 0;
			int PbestC = 0;
			
			for(int i = 0; i < popsize; i++){
				double sichma = (randomGenerator.nextDouble() * 0.0001);
				double nuy = (randomGenerator.nextDouble() * 0.0001);
				Ci[i] = Cmin + (randomGenerator.nextInt(Cmax - Cmin));
				System.out.println("Ci = "+ Ci[i]);
				double ASWC;
				FC_PFS2 fc = new FC_PFS2(Ci[i]);
				Pixel[][] array = LuatMo2.training_sple2(Define.NUM_TRAIN_IMG);
				FC_PFS2.FC_PFS(array,Ci[i]);
				
				u = new double[N][Ci[i]];
				e = new double[N][Ci[i]];
				u = LuatMo2.readMatrixU(Define.path + "/matranU_PS.txt",Define.NUM_TRAIN_IMG, Ci[i]);
				e = LuatMo2.readMatrixU(Define.path + "/matranE_PS.txt",Define.NUM_TRAIN_IMG, Ci[i]);
				
				double[] CC = new double[Ci[i]]; double CC_tb = 0;
				for(int j = 0; j < Ci[i]; j++){
					CC[j] = 0;
					for(int k = 0; k < N; k++){
						CC[j] += u[k][j]* (2- e[k][j]);
						//System.out.println("CC[j] = "+ CC[j]);
					}
					CC_tb += CC[j];
				}
				
				for(int j = 0; j < Ci[i]; j++){
					//System.out.println("CCj = "+ CC[j] + " CC_tb =" + CC_tb );
					if(CC[j] < (CC_tb - sichma)){
						for(int jj = j;jj < (Ci[i]-1); jj++){
							CC[jj] = CC[jj +1];
						}
					Ci[i]--;
					//System.out.println("Ci[i] tưng buoc = "+ Ci[i]);
					}
					
				}
			
				if(Ci[i] > 1){
					System.out.println("Ci[i] sau = "+ Ci[i]);
					FC_PFS2.FC_PFS(array,Ci[i]); 
					u = LuatMo2.readMatrixU(Define.path + "/matranU_PS.txt",Define.NUM_TRAIN_IMG,Ci[i]);
					e = LuatMo2.readMatrixU(Define.path + "/matranE_PS.txt",Define.NUM_TRAIN_IMG,Ci[i]);
				}
				
				ASWC = ASWC(u, e, Ci[i], array);
				if(Pbest == 0 || Pbest < ASWC){
					Pbest = ASWC;
				//Cập nhật lại các độ thuộc, tâm cụm
					Upbest = new double[N][Ci[i]];
					Upbest = u;
					Epbest = new double[N][Ci[i]];
					Epbest = e;
					//Pbestx = sichma;
				// Cập nhật lại số cụm PbestC là Ci.
					PbestC = Ci[i];
					System.out.println("Pbest = "+ Pbest + "PbestC = "+ PbestC);
				}
				if(Gbest == 0 || Gbest < Pbest){
					
					min = Pbest - Gbest;
					Gbest = Pbest;
					System.out.println("min = "+ min);
				// Gán tâm cụm, độ thuộc của Pbeest cho Gbest
					Ugbest  = new double[N][Ci[i]];
					Ugbest = u;
					Egbest = new double[N][Ci[i]];
					Egbest= e;
					//Gbestx = Pbestx;
					GbestC = PbestC;
				}
				nuy = nuy + randomGenerator.nextDouble()*( Pbestx - sichma) + randomGenerator.nextDouble()*(Gbestx - sichma);
				sichma = nuy + sichma;
			}
			System.out.println("So cum = "+ GbestC);
		}while(min > eps || t > maxStep );
		
		String filename = Define.path + "\\matranU_Gbest.txt";
        FC_PFS2.writeFile(filename, Ugbest, N, (int)GbestC);

        filename = Define.path + "\\matranE_Gbest.txt";
        FC_PFS2.writeFile(filename, Egbest, N, (int)GbestC);
	return (int)GbestC;
}

	
/*	public int AFC_PSO(){
		double Pbestx = 0;
		int PbestC = 0;
		double Gbestx = 0, GbestC = 0;
		for(int i = 0; i < popsize; i++){
			// Random số cụm.
			Ci[i] = Cmin + (randomGenerator.nextInt(Cmax - Cmin));
			//Ci[i] = 3;
			System.out.println("Ci = "+ Ci[i]);
			double sichma = (randomGenerator.nextDouble() * 0.0001);
			double nuy = (randomGenerator.nextDouble() * 0.0001);
			double Pbest = 0;
			double Gbest = Pbest;
			double ASWC;
			int t = 0;
			double min;
			do{
				t = t + 1;
				min = 0; 
				FC_PFS2 fc = new FC_PFS2(Ci[i]);
				Pixel[][] array = LuatMo2.training_sple2(Define.NUM_TRAIN_IMG);
				FC_PFS2.FC_PFS(array,Ci[i]);
				
				u = new double[N][Ci[i]];
				e = new double[N][Ci[i]];
				u = LuatMo2.readMatrixU(Define.path + "/matranU_PS.txt",Define.NUM_TRAIN_IMG, Ci[i]);
				e = LuatMo2.readMatrixU(Define.path + "/matranE_PS.txt",Define.NUM_TRAIN_IMG, Ci[i]);
				
				double[] CC = new double[Ci[i]]; double CC_tb = 0;
				for(int j = 0; j < Ci[i]; j++){
					CC[j] = 0;
					for(int k = 0; k < N; k++){
						CC[j] += u[k][j]* (2- e[k][j]);
						//System.out.println("CC[j] = "+ CC[j]);
					}
					CC_tb += CC[j];
				}
				
				for(int j = 0; j < Ci[i]; j++){
					//System.out.println("CCj = "+ CC[j] + " CC_tb =" + CC_tb );
					if(CC[j] < (CC_tb - sichma)){
						for(int jj = j;jj < (Ci[i]-1); jj++){
							CC[jj] = CC[jj +1];
						}
					Ci[i]--;
					//System.out.println("Ci[i] tưng buoc = "+ Ci[i]);
					}
					
				}
			
				if(Ci[i] > 1){
				System.out.println("Ci[i] sau = "+ Ci[i]);
				FC_PFS2.FC_PFS(array,Ci[i]); 
				u = LuatMo2.readMatrixU(Define.path + "/matranU_PS.txt",Define.NUM_TRAIN_IMG,Ci[i]);
				e = LuatMo2.readMatrixU(Define.path + "/matranE_PS.txt",Define.NUM_TRAIN_IMG,Ci[i]);
				//Tinh chi so ASWC
				ASWC = ASWC(u, e, Ci[i], array);
				
				if(Pbest == 0 || Pbest < ASWC){
					Pbest = ASWC;
				//Cập nhật lại các độ thuộc, tâm cụm
					Upbest = new double[N][Ci[i]];
					Upbest = u;
					Epbest = new double[N][Ci[i]];
					Epbest = e;
					Pbestx = sichma;
				// Cập nhật lại số cụm PbestC là Ci.
					PbestC = Ci[i];
				}
				
				if(Gbest == 0 || Gbest < Pbest){
					Gbest = Pbest;
					min = Pbest - Gbest;
					System.out.println("min = "+ min);
				// Gán tâm cụm, độ thuộc của Pbeest cho Gbest
					Ugbest  = new double[N][Ci[i]];
					Ugbest = u;
					Egbest = new double[N][Ci[i]];
					Egbest= e;
					Gbestx = Pbestx;
					GbestC = PbestC;
				}
				//min = Gbest;
				nuy = nuy + randomGenerator.nextDouble()*( Pbestx - sichma) + randomGenerator.nextDouble()*(Gbestx - sichma);
				sichma = nuy + sichma;
				}
				System.out.println("min = "+ min + " " + " eps = "+ eps);
			}while( min > eps && t < maxStep );
		}
		String filename = Define.path + "\\matranU_Gbest.txt";
        FC_PFS2.writeFile(filename, u, N,PbestC);

        filename = Define.path + "\\matranE_Gbest.txt";
        FC_PFS2.writeFile(filename, e, N,PbestC);
        System.out.println(" so cum: "+ GbestC);
        return (int) GbestC;
}*/
	
	public static double distance(Pixel a, Pixel b){
        double m = (a.getRed() - b.getRed()) * (a.getRed() - b.getRed());
        double n = (a.getGreen() - b.getGreen()) * (a.getGreen() - b.getGreen());
        double p = (a.getBlue() - b.getBlue()) * (a.getBlue() - b.getBlue());
        double tong = m + n + p;
        return tong;
    }
	
	public double ASWC(double[][]u, double[][]e,int Ci, Pixel[][] arr){
		double sum = 0,tg;
		double ASWC_EPS = 0.0000001;
		int[] mark = new int[N];
		int[] markcount = new int[N];
		double[] average = new double[N];
		double[] minClus = new double[N];
		Pixel[] array = new Pixel[N];
		int count = 0;
		for(int i = 0; i < arr.length; i++){
			for(int j = 0; j < arr[0].length; j++){
				array[count] = arr[i][j];
				count ++;
			}
		}
		for(int i = 0; i < N; i ++){
			mark[i] = 0; 
			markcount[i] = 0;
			double max = u[i][0] * (2 - e[i][0]);
			for(int j = 1; j < Ci; j++){
				tg = u[i][j] * (2 - e[i][j]);
				if(max < tg){
					max = tg;
					mark[i] = j;
				}
			}
		}
		for(int i = 0; i < N; i++){
			sum = 0; minClus[i] = -1;
			for(int j = 0; j < N; j++){
				if(i != j){
					tg = distance(array[i], array[j]);
					//System.out.println("array_i = "+array[i].getGreen()+ "array_j = "+array[j].getGreen()+" tg = "+ tg);
					if(mark[i] == mark[j]){
						average[i] += Math.sqrt(tg);
						markcount[i]++;
					}else{
						if(minClus[i] == -1 || minClus[i] > tg){
							minClus[i] = tg;
						}
					}
				}
			}
			minClus[i] = Math.sqrt(minClus[i]);	
			if(markcount[i] != 0) {
				average[i] /= markcount[i];
			}
			else markcount[i] = 1;
			tg = minClus[i] / (average[i] + ASWC_EPS);
			//System.out.println("tg = "+ tg);
			sum += tg;
		}
		System.out.println("ASWC = "+ sum/N);
		return sum/N;
	}
	public static void main(String[] args) {
		AFC_PSO AP = new AFC_PSO();
		AP.AFC_PSO();
	}
}
