/*
 * Copyright (c) 2026 by the California Institute of Technology.
 * ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology
 * at the California Institute of Technology.
 *
 * The technical data in this document (or file) is controlled for export
 * under the Export Administration Regulations (EAR), 15 CFR, Parts 730-774.
 * Violations of these laws are subject to fines and penalties under the
 * Export Administration Act.
 */

package jpl.mipl.mdms.FileService.komodo.ui.savannah.tools;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jpl.mipl.mdms.FileService.komodo.ui.savannah.SavannahDateFilterModel;
import jpl.mipl.mdms.FileService.komodo.ui.savannah.SavannahDateFilterPanel;
import jpl.mipl.mdms.FileService.komodo.ui.savannah.SavannahModel;

/**
 * <b>Purpose:</b>
 *  Action implementation for setting the date/time filter.
 *
 *   <PRE>
 *   Copyright 2013, California Institute of Technology.
 *   ALL RIGHTS RESERVED.
 *   U.S. Government Sponsorship acknowledge. 2009.
 *   </PRE>
 *
 * <PRE>
 * ============================================================================
 * <B>Modification History :</B>
 * ----------------------
 *
 * <B>Date              Who              What</B>
 * ----------------------------------------------------------------------------
 * 04/02/2013        Nick             Initial Release
 * ============================================================================
 * </PRE>
 *
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)

 *
 */

public class SetDateTimeFilterAction extends AbstractAction
{
    SavannahModel model;
    Component parent;
    
    
    SavannahDateFilterModel filterModel;
    SavannahDateFilterPanel filterPanel;
    JPanel mainPanel;
    
    //---------------------------------------------------------------------
    
    public SetDateTimeFilterAction(Component parent, SavannahModel model)
    {
        super("Edit Date Filters");
        this.parent = parent;
        this.model = model;
        
        init();
    }
    
    protected void init()
    {
        
        this.filterModel = this.model.getDateFilterModel();
        this.filterPanel = new SavannahDateFilterPanel(filterModel);
        
        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(filterPanel);        
        mainPanel.add(Box.createVerticalStrut(15));
        
    }
    
    //---------------------------------------------------------------------
    
    
    public void run()
    {        
               
        
        Object[] options = new Object[] {"OK"};
        int opt = JOptionPane.showOptionDialog(this.parent, mainPanel, 
                                               "Edit Date Filter",
                                               JOptionPane.OK_OPTION, 
                                               JOptionPane.PLAIN_MESSAGE,
                                               null, options, options[0]);
        if (opt == JOptionPane.CLOSED_OPTION || options[opt].equals("Cancel"))
        {
            return;
        }
        
      
    }
    
    public void runOld()
    {        
               
        
        Object[] options = new Object[] {"Apply", "Cancel"};
        int opt = JOptionPane.showOptionDialog(this.parent, filterPanel, 
                                               "Edit Date Filter",
                                               JOptionPane.YES_NO_OPTION, 
                                               JOptionPane.PLAIN_MESSAGE,
                                               null, options, options[0]);
        if (opt == JOptionPane.CLOSED_OPTION || options[opt].equals("Cancel"))
        {
            return;
        }
        
      
    }
    
    //---------------------------------------------------------------------
    
   

    //---------------------------------------------------------------------

    public void actionPerformed(ActionEvent arg0)
    {
        run();        
    }
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    
}
