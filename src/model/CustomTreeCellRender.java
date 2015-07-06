/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author 
 */
public class CustomTreeCellRender extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        CustomTreeNode node = (CustomTreeNode) value;

        if (node.getIcon() != null) {
            //System.out.println(node + " - " + node.getIcon());
            setClosedIcon(node.getIcon());
            setOpenIcon(node.getIcon());
            setLeafIcon(node.getIcon());
        } else {
            //System.out.println(node + " - default");
            setClosedIcon(getDefaultClosedIcon());
            setLeafIcon(getDefaultLeafIcon());
            setOpenIcon(getDefaultOpenIcon());
        }

        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        return this;
    }
}
