package model;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dai
 */
public class ImageImplement extends JPanel{
    private final Image Img;
    
    public ImageImplement(Image img){
        Img = img;
        setPreferredSize(new Dimension(img.getWidth(null),img.getHeight(null)));
        setSize(new Dimension(img.getWidth(null),img.getHeight(null)));
        setLayout(null);
    }
    
    @Override
    public void paintComponent(Graphics g){
        g.drawImage(Img,0,0,null);
    }
}
