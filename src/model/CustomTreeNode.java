/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Dai
 */
public class CustomTreeNode extends DefaultMutableTreeNode {
    private ImageIcon icon;
    private int id;
    
    public CustomTreeNode(ImageIcon icon, Object userObject){
        super(userObject);
        this.icon = icon;
    }
    
    public CustomTreeNode(Object userObject){
    	super(userObject);
    }
    
    public CustomTreeNode(ImageIcon icon, Object userObject, boolean allowsChildren){
        super(userObject, allowsChildren);
        this.icon = icon;
    }
    
    /**
     * @return the icon
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
    
    public NodeItem getNodeItem()
    {
        return (NodeItem)userObject;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
}
