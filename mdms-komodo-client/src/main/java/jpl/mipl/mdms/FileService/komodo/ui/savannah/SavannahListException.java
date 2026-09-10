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

package jpl.mipl.mdms.FileService.komodo.ui.savannah;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *  
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)

 */
public class SavannahListException extends Throwable 
{
    protected SavannahList _list;
    
    public SavannahListException(SavannahList list,
                                 String message)
    {
        super(message);
        _list = list;
    }
    
    public SavannahList getList()
    {
        return _list;
    }
    
}
