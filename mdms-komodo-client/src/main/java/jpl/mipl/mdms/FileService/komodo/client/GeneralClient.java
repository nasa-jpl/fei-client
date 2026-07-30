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

package jpl.mipl.mdms.FileService.komodo.client;

import jpl.mipl.mdms.utils.logging.Logger;

/**
 * Make simple connection to serverHost and serverPort specified starting point
 * for a network client.
 * 
 * @author G. Turek

 */

public class GeneralClient extends BaseClient {

   public static final Logger logger = Logger.getLogger(GeneralClient.class
         .getName());

   /**
    * Constructor, accepts string array object which contains the command line
    * arguments.
    * 
    * @param args The command line arguments
    * @throws Exception when general failure
    */
   public GeneralClient(String[] args) throws Exception {
      super(args);
      CCLProcessor cli = new CCLProcessor(this._ssl, this._batchFile,
            this._silent, this._exitAfterBatch);
   }

   /**
    * Main method, accepts string array object which contains the command line
    * arguments.
    * 
    * @param args The command line arguments
    */
   public static void main(String[] args) {
      try {
         GeneralClient client = new GeneralClient(args);
      } catch (Exception e) {
         GeneralClient.logger.error(e.getMessage());
         GeneralClient.logger.debug(null, e);
         System.exit(1);
      }
   }
}