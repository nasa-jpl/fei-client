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

package jpl.mipl.mdms.FileService.komodo.client.handlers;

/**
 * Builds a new instance of a FileEventHandler from the information
 * contained within the metadata.
 *
 *   <PRE>
 *   Copyright 2008, California Institute of Technology.
 *   ALL RIGHTS RESERVED.
 *   U.S. Government Sponsorship acknowledge. 2008.
 *   </PRE>
 *
 * <PRE>
 * ============================================================================
 * <B>Modification History :</B>
 * ----------------------
 *
 * <B>Date              Who              What</B>
 * ----------------------------------------------------------------------------
 * 08/15/2008        Nick             Initial Release
 * ============================================================================
 * </PRE>
 *
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)

 */

public class FileEventHandlerBuilder
{

    /**
     * Create new handler instance, and initialize it with passed
     * in metadata parameter.
     * @param metadata Handler metadata information
     * @return New instance of FileEventHandler, or null if one could
     *         not be created
     * @throws Exception if error occurs during construction or initialization
     */
    
    public FileEventHandler build(FileEventsContext context,
                                  FileEventHandlerInfo metadata) 
                                                throws Exception
    {
        FileEventHandler handler = null;
        
        try {
            Class clazz = null;
            String clazzname = metadata.getImplementation();
            
            ClassLoader cLoader = FileEventHandlerBuilder.class.getClassLoader();
            clazz = cLoader.loadClass(clazzname);

            Object o = clazz.newInstance();
            if (o instanceof FileEventHandler)
            {
                handler = (FileEventHandler) o;
                handler.initialize(context, metadata);
                
            }
        } catch (Exception ex) {
            throw ex;
        }
        
        return handler;
    }
}
