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
 * Interface for handlers that respond to Komodo file events and errors.
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

 *
 */

public interface FileEventHandler extends Comparable
{

    //---------------------------------------------------------------------

    /**
     * Returns the name of this handler
     * @return Handler name
     */

    public String getName();

    //---------------------------------------------------------------------

    /**
     * Returns the description of this handler
     * @return Handler description
     */

    public String getDescription();

    //---------------------------------------------------------------------

    /**
     * Returns the version of this handler
     * @return Handler version
     */

    public String getVersion();

    //---------------------------------------------------------------------

    /**
     * Returns the id of this handler
     * @return Handler id
     */

    public String getId();

    //---------------------------------------------------------------------

    /**
     * Returns the value associated with a property name, null if not defined
     * @return propertyName Name of the property
     * @return Handler property value
     */

    public String getProperty(String propertyName);

    //---------------------------------------------------------------------

    /**
     * Handle method performs operation of the handler.  Implementors
     * can use the event type as a filter so that handler is only
     * invoked for specific event types.
     * @param taskType Type of task
     * @param results List of Result objects
     */

    public void eventOccurred(FileResultEvent fileEvent) throws HandlerException;

    //---------------------------------------------------------------------

    public void errorOccurred(FileResultError fileError) throws HandlerException;

    //---------------------------------------------------------------------

    /**
     * Initializes handler from contents of a DOM element.
     * @param into Handler configuration info object
     * @throws Exception if element is null is does not contain required
     *         attributes.
     */

    public void initialize(FileEventsContext context,
                           FileEventHandlerInfo info) throws HandlerException;

    //---------------------------------------------------------------------

    public void close();

    //---------------------------------------------------------------------
}
