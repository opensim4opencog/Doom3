package com.touchgraph.graphlayout;

import  com.touchgraph.graphlayout.interaction.*;
import  com.touchgraph.graphlayout.graphelements.*;

import  java.awt.*;
import  java.awt.event.*;
//import  javax.swing.*;
import  java.util.*;
import java.lang.*;

import org.opencyc.api.*;
import org.opencyc.cycobject.*;
import org.opencyc.cyclobject.*;

public class ExpandThread extends Thread {
	public String expandKey;
	public TGPanel targetPanel;
        public Node focusNode;
        public int radius;
	public void run () {
		targetPanel.demoDB2(expandKey);
                try {
                    targetPanel.queryKB1(expandKey);
                    focusNode=(Node) targetPanel.findNodeLabelContaining(expandKey);
		    targetPanel.setLocale(focusNode,radius);

                    targetPanel.queryKB2(expandKey);
                    Thread.currentThread().sleep(600); 
                    targetPanel.updateVisList ();
                    Thread.currentThread().sleep(60); 
		    targetPanel.setLocale(focusNode,radius);

                } catch (Exception ex) {
                    //break;
                }
	}
}
