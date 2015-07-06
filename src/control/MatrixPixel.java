
package control;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;


public class MatrixPixel {

	 BufferedImage myImage1;
	//BufferedImage myImage2;
	//BufferedImage[] myImage;
	
	public MatrixPixel(){};
	
	//public MatrixPixel(BufferedImage myImage1,BufferedImage myImage2) {
	public MatrixPixel(BufferedImage myImage1) {
		super();
		this.myImage1 = myImage1;
		//this.myImage2 = myImage2;
	}

	int width; 
	int height; 
	
	public int getWidth() {
		return width;
	}

	
	public int getHeight() {
		return height;
	}

	public Pixel[][] getData2(int id)
	{
		Raster r1 = this.myImage1.getRaster();
		Pixel[][] data = new Pixel[r1.getHeight()][r1.getWidth()];
		int[] samples = new int[3];
	
		for (int row = 0; row < r1.getHeight(); row++)
		{
			for (int col = 0; col < r1.getWidth(); col++)
			{
				samples = r1.getPixel(col, row, samples);
				Pixel newPixel = new Pixel(samples[0], samples[1], samples[2]);
				data[row][col] = newPixel;
			}
		}
		
		
		//Data.setImage(data);
		Data.setAfterRemoveImage(data, id);
		
		//Ghi ra file text ma tran pixel voi 2 dong dau tien la so hang v� so cot cua ma tran.
		try{
			String name = Define.BASEDPATH +"/out_put2_id" + id + ".txt";
			
			File file = new File(name);
		 
	        if (!file.exists()) {
				file.createNewFile();
			}
	        BufferedWriter o = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
	        PrintWriter pw = new PrintWriter(o,true);
	    
	        pw.println(r1.getHeight());
	        pw.println(r1.getWidth());
	       
	        for (int row = 0; row < r1.getHeight(); row++)
			{
				for (int col = 0; col < r1.getWidth(); col++)
				{
					pw.print(data[row][col]+",");
					
				}
				pw.println("");
			}
			
			pw.close();
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	        

		return data;
	}
	
	public Pixel[][] getData(int id)
	{
		Raster r1 = this.myImage1.getRaster();
		Pixel[][] data = new Pixel[r1.getHeight()][r1.getWidth()];
		int[] samples = new int[3];
	
		for (int row = 0; row < r1.getHeight(); row++)
		{
			for (int col = 0; col < r1.getWidth(); col++)
			{
				samples = r1.getPixel(col, row, samples);
				//System.out.println("samples = "+ samples[0]);
				Pixel newPixel = new Pixel(samples[0], samples[1], samples[2]);
				data[row][col] = newPixel;
				//System.out.println("data = "+ data[row][col]);
			}
		}
		
		
		Data.setImage(data);
		//Data.setImage(data,id);
		//Ghi ra file text ma tran pixel voi 2 dong dau tien la so hang v� so cot cua ma tran.
		try{
			String name = Define.BASEDPATH +"/out_put2_id" + id + ".txt";
			
			File file = new File(name);
		 //File file = new File("out_put2.txt");
	        if (!file.exists()) {
				file.createNewFile();
			}
	        BufferedWriter o = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
	        PrintWriter pw = new PrintWriter(o,true);
	        //pw.println(2* r1.getHeight());
	        pw.println(r1.getHeight());
	        pw.println(r1.getWidth());
	        //for (int row = 0; row < 2 * r1.getHeight(); row++)
	        for (int row = 0; row < r1.getHeight(); row++)
			{
				for (int col = 0; col < r1.getWidth(); col++)
				{
					pw.print(data[row][col]+",");
					
				}
				pw.println("");
			}
			
			pw.close();
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	        

		return data;
	}
	
	//ImageCluster mg = new ImageCluster();
	//Pixel[][] data = mg.phancum();
	public void setData(Pixel[][] data)
	{
		double [] pixelValues = new double[3]; // a temporary array to hold r,g,b values
		WritableRaster wr = this.myImage1.getRaster();
	
		if (data.length != wr.getHeight())
		{
			throw new IllegalArgumentException("Array size does not match");
		}else if (data[0].length != wr.getWidth())
		{
			throw new IllegalArgumentException("Array size does not match");
		}
	
		for (int row = 0; row < wr.getHeight(); row++)
		{
			for (int col = 0; col < wr.getWidth(); col++)
			{
				pixelValues[0] = data[row][col].getRed();
				pixelValues[1] = data[row][col].getGreen();
				pixelValues[2] = data[row][col].getBlue();
				wr.setPixel(col, row, pixelValues);
			}
		}
	}
	
	public void showPixel(Pixel [][]data){
		Raster r1 = myImage1.getRaster(); 
		
		for(int i = 0;i < r1.getHeight(); i++){
			for(int j = 0; j < r1.getWidth();j++){
				System.out.print(data[i][j].toString());
				System.out.print(", ");
			}
			System.out.println("");
		}
	}
	
}
