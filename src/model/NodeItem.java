/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;

/**
 *
 * @author Dai
 */
public class NodeItem {
    private int id;
    private String name;
    private String path;
    private boolean isRoot;
    private String time;
    public ArrayList<NodeItem> items;
    
    public NodeItem(String name, String path,String time, boolean isRoot){
        setName(name);
        setPath(path);
        setIsRoot(isRoot);
        setTime(time);
    }
    
    @Override
    public String toString() {
        return this.getName();
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
     * @return the isRoot
     */
    public boolean isIsRoot() {
        return isRoot;
    }

    /**
     * @param isRoot the isRoot to set
     */
    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
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
