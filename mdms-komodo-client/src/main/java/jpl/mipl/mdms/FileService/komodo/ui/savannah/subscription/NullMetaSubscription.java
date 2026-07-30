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

package jpl.mipl.mdms.FileService.komodo.ui.savannah.subscription;

/**
 * <b>Purpose:</b>
 * NullSubscription extends DefaultMetaSubscription but offers
 * no client support.  Implementation of the initClient() method
 * trivially sets the client field to null.
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
 * 09/24/2004        Nick             Initial Release
 * ============================================================================
 * </PRE>
 *
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)

 *
 */

public class NullMetaSubscription extends DefaultMetaSubscription
{
    //---------------------------------------------------------------------
    
    /**
     * Constructs a NullSubscription.  Uses an empty parameter object.
     * so that source, target, and client parameters are all null.
     */
    
    public NullMetaSubscription()
    {
        this(new DefaultMetaParameters());        
    }
    
    //---------------------------------------------------------------------
    
    /**
     * Constructs a NullSubscription.  
     * @param Instance of MetaParameters.
     */
    
    public NullMetaSubscription(MetaParameters parameters)
    {
        super(parameters);        
    }
    
    //---------------------------------------------------------------------
    
    /**
     * Simply sets client reference to null.
     */
    
    protected void initClient()
    {
        this._client = null;
    }
    
    //---------------------------------------------------------------------
    
}
