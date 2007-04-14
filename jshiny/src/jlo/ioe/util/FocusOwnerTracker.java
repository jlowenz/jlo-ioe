package jlo.ioe.util;

import javax.swing.SwingUtilities;
import java.applet.Applet;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 13, 2007<br>
 * Time: 5:13:15 PM<br>
 */
// @author Santhosh Kumar T - santhosh@in.fiorano.com
public abstract class FocusOwnerTracker implements PropertyChangeListener {
    private static final String PERMANENT_FOCUS_OWNER = "focusOwner";

    private KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    private Component comp;
    private boolean inside;

    public FocusOwnerTracker(Component comp){
        this.comp = comp; 
    }

    public boolean isFocusInside(){
        return isFocusInside(false);
    }

    private boolean isFocusInside(boolean find){
        if(!find)
            return inside;

        Component c = focusManager.getPermanentFocusOwner();
        while(c!=null){
            if(c==comp){
                return true;
            } else if((c instanceof Window) ||
                    (c instanceof Applet && c.getParent()==null)){
                if(c== SwingUtilities.getRoot(comp)){
                    return false;
                }
                break;
            }
            c = c.getParent();
        }
        return false;
    }

    public void start(){
        focusManager.addPropertyChangeListener(PERMANENT_FOCUS_OWNER, this);
        inside = isFocusInside(true);
    }

    public void stop(){
        focusManager.removePropertyChangeListener(PERMANENT_FOCUS_OWNER, this);
    }

    public void propertyChange(PropertyChangeEvent evt){
        boolean inside = isFocusInside(true);
	    System.out.println("focus: " + evt.getSource());
        if(this.inside!=inside){
            if(inside)
                focusGained();
            else
                focusLost();
            this.inside = inside;
        }
    }

    public abstract void focusLost();
    public abstract void focusGained();
}
