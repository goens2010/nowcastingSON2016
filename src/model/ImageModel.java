/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import control.Pixel;
import java.awt.image.BufferedImage;

/**
 *
 * @author Dai
 */
public class ImageModel {
    private BufferedImage image;
    private String time;
    private String name;
    private String path;
    private Pixel[] listCenter;
    
    public ImageModel(){
    }
    
    public ImageModel(BufferedImage image,String name,String time, Pixel[] listCenter){
        setName(name);
        setTime(time);
        setImage(image);
        setListCenter(listCenter);
    }
    // them
    public ImageModel(BufferedImage image,String name){
    	setName(name);
    	setImage(image);
    }
    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the listCenter
     */
    public Pixel[] getListCenter() {
        return listCenter;
    }

    /**
     * @param listCenter the listCenter to set
     */
    public void setListCenter(Pixel[] listCenter) {
        this.listCenter = listCenter;
    }
}
