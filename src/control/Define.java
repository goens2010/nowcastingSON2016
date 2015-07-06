package control;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Define {
	// chá»‰ sá»‘ má»�.
	public static final int FUZZIFIER = 2;
	
	// chiá»�u dÃ i áº£nh (sá»‘ hÃ ng cá»§a ma tráº­n Ä‘iá»ƒm áº£nh)
	public static final int IMAGE_SIZE_ROW = 100;
	
	// sá»‘ cá»™t cá»§a ma tráº­n áº£nh
	public static final int IMAGE_SIZE_COL = 100;
	
	// sá»‘ áº£nh dÃ¹ng trong mÃ´ hÃ¬nh STAR
	//public static final int NUM_IMAGE = 5;
	// sá»‘ áº£nh training trong mÃ´ hÃ¬nh STAR
	public static final int NUM_TRAINING_IMAGE = 3;
	// sá»‘ pixel hÃ ng xÃ³m sá»­ dá»¥ng trong mÃ´ hÃ¬nh STAR
	public static final int NUM_NEIGHBOR = 1;
	//
	public static final double EPSILON = 0.000007;
	public static final int CELLSIZE = 2;
	public static final int NUM_LAGRANGE = 2;
	
	// Sá»‘ cá»¥m.
	public static final int NUM_CLUSTER = 4;
	
	// thÆ° má»¥c lÆ°u cÃ¡c dá»¯ liá»‡u káº¿t quáº£ thu Ä‘Æ°á»£c.
	public static String BASEDPATH = "/Data";
	
	// epsilon sá»­ dá»¥ng trong phÃ¢n cá»¥m cá»§a phÆ°Æ¡ng phÃ¡p xd luáº­t má»�.
	public static final double EPS = 0.2;
	
	// sá»‘ hÃ ng cá»§a vÃ¹ng dá»¯ liá»‡u con.
	public static final int SIZE_ROW = 10;
	
	// sá»‘ cá»™t cá»§a vÃ¹ng dá»¯ liá»‡u con.
	public static final int SIZE_COL = 10;
	
	// dá»‹ch chuyá»ƒn giá»¯a cÃ¡c vÃ¹ng.
	public static final int MOVE = 5;
	
	//public static final int SAME = 5;
	
	// Sá»‘ luáº­t má»� cáº§n xÃ¢y dá»±ng (= sá»‘ cá»¥m )
	public static final int NUM_RULES = 8;
	
	// Sá»‘ áº£nh Ä‘Æ°a vÃ o tÃ­nh chÃªnh lá»‡ch.
	public static final int NUM_TRAIN_IMG = 3;
	
//	public static String path = "C:/Users/Administrator/Desktop/Forecast/Test3/Data";
	public static String path = BASEDPATH;
			//"C:/Users/Administrator/Desktop/Forecast/Test3/Data";
	
	public static final int maxStep = 1000;
	
	public static final double MIN_EPSILON = 0.000001;
	
	public static final int NUM_FOREST = 2;
	
	 
 	  
     
}