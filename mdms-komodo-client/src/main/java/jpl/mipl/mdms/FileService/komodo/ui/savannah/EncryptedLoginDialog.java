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

import java.io.IOException;

import jpl.mipl.mdms.FileService.komodo.util.PublicKeyEncrypter;
import jpl.mipl.mdms.utils.logging.Logger;

/**
 * <B>Purpose:<B>
 * Extension of the login dialog with password encryption applied.
 *
 * @author Nicholas Toole (Nicholas.T.Toole@jpl.nasa.gov)

 *
 */
public class EncryptedLoginDialog extends LoginDialog
{
    PublicKeyEncrypter _encrypter;
    private Logger logger = Logger.getLogger(EncryptedLoginDialog.class.getName());
    
    
    /**
     * Constructor.
     * @param title Optional title to be displayed with dialog.
     */
    
    public EncryptedLoginDialog(String title)
    {       
        this(title, null);
    }
    
    //---------------------------------------------------------------------
    
    /**
     *  Constructs a login dialog with username and password
     *  fields.
     *  @param title Optional title to be displayed with dialog.
     *  @param username Initial username to enter in field
     */
    public EncryptedLoginDialog(String title, String username)                            
    {            
        this(title, username, null);
    }
    
    //---------------------------------------------------------------------
    
    
    /**
     * Constructs a login dialog with username and password
     * fields.
     * @param title Optional title to be displayed with dialog.
     * @param username Initial username to enter in field
     * @param labelValue Optional message value for panel 
     */
    public EncryptedLoginDialog(String title, String username, String message)
    {        
        super(title, username, message);       
        
        try {
            _encrypter = new PublicKeyEncrypter();
        } catch (IOException ioEx) {   
            this.logger.error("Could not build instance of encrypter: "+ioEx.getMessage());
            this.logger.debug(null, ioEx);
            _encrypter = null;
        }       
    }
    
    //---------------------------------------------------------------------
    
    /**
     * Override of password field read, where text is encrypted,
     * then assigned to the password member.
     */
    
    protected void readPasswordField()
    {
        String passText = new String(this._passField.getPassword());
        String encrText = null;
        
        if (passText != null && !passText.equals("") && _encrypter != null)
        {
            try {
                encrText = this._encrypter.encrypt(passText);
            } catch (Exception ex) {
                ex.printStackTrace();
                encrText = null;
            }
            
            if (encrText != null)
            {
                this._password = encrText;
            }
        }
    }
    
    //---------------------------------------------------------------------

    
    //---------------------------------------------------------------------
    
}
