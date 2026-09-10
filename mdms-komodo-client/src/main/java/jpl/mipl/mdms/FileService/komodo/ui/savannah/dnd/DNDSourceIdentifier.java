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

package jpl.mipl.mdms.FileService.komodo.ui.savannah.dnd;

/**
 * <b>Purpose:</b>
 *  DNDSourceIdentifier is merely a wrapper around a string
 *  to be used to identify the source of a DND transfer.
 *
 *   <PRE>
 *   Copyright 2004, California Institute of Technology.
 *   ALL RIGHTS RESERVED.
 *   U.S. Government Sponsorship acknowledge. 2004.
 *   </PRE>
 *
 * <PRE>
 * ============================================================================
 * <B>Modification History :</B>
 * ----------------------
 *
 * <B>Date              Who              What</B>
 * ----------------------------------------------------------------------------
 * 06/02/2004        Nick             Initial Release
 * ============================================================================
 * </PRE>
 *
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)

 *
 */

public class DNDSourceIdentifier 
{
    public static final String HUMAN_READABLE = "DNDSourceIdentifier";
    
    protected String _name;
    
    public DNDSourceIdentifier(String name)
    {
        _name = name;    
    }
    
    public String getName()
    {       
        return _name;
    }
    
    public boolean equals(Object obj)
    {
        DNDSourceIdentifier other;
        
        if (obj instanceof DNDSourceIdentifier)
        {
            other = (DNDSourceIdentifier) obj;
            return _name.equals(other.getName());
        }
        return false;
    }
}

