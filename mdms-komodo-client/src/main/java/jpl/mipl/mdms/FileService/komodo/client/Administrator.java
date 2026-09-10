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

/**
 * Make simple connection to serverHost and serverPort specified starting point
 * for a network client.
 * 
 * @author G. Turek

 */
public class Administrator extends BaseClient {

   /**
    * Constructor, accepts string array object with command line arguments
    * 
    * @param args The command line arguments
    * @throws Exception when general failure
    */
   public Administrator(String[] args) throws Exception {
      super(args);
      ACLProcessor cli = new ACLProcessor(this._ssl, this._batchFile,
            this._silent, this._exitAfterBatch);
   }

   /**
    * Main method of FEI Administrator client.
    * 
    * @param args The command line arguments
    * @throws Exception when general failure
    */
   public static void main(String[] args) throws Exception {
      try {
         Administrator client = new Administrator(args);
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(1);
      }
   }
}