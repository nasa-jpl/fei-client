#!/bin/csh -f

# Copyright (c) 2026 by the California Institute of Technology.
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
# Any commercial use must be negotiated with the Office of Technology
# at the California Institute of Technology.
#
# The technical data in this document (or file) is controlled for export
# under the Export Administration Regulations (EAR), 15 CFR, Parts 730-774.
# Violations of these laws are subject to fines and penalties under the
# Export Administration Act.

#
### ==================================================================== ###
#                                                                          #
#  The File System (FEI5) Encryption Utility   	                           #
#  					                                                       #
#  Function: Outputs a one-way encryption of input message.                #
#                                                                          #
#                                                                          #
#  Korea, Cuba, Iran, Syria, Sudan and Libya.                              #
#                                                                          #
#                                                                          #
#  Created: Nov. 26, 2002 by Rich Pavlovsky {rich.pavlovsky@jpl.nasa.gov}  #
#                                                                          #
#  Modifications:                                                          #
#  Dec. 6, 2002 by Thomas Huang {thomas.huang@jpl.nasa.gov}                #
#  Set the JAVA_HOME env var to the value of $V2JDK which is set by        #
#  by MIPL select or selcat.                                               #
#                                                                          #
#  Apr. 25, 2003 by Rich Pavlovsky {rich.pavlovsky@jpl.nasa.gov}           #
#  Added xercesImpl.jar and xmlParserAPIs.jar to the classpath             #
#  these jars are needed by the FEI client to parse the help XML file.     #
#                                                                          #
#  Oct. 07, 2003 by Thomas Huang {Thomas.Huang@jpl.nasa.gov}               #
#  Updated to parse V2HTML to set CLASSPATH.                               #
#                                                                          #
#  Oct. 08, 2003 by Rich Pavlovsky {rich.pavlovsky@jpl.nasa.gov}           #
#  Set CLASSPATH to an empty string if not defined.                        #
#                                                                          #
#  Jun. 18, 2018 by William Phyo {wai.phyo@jpl.nasa.gov}                   #
#  Removing V2HTML code as part of maven + artifactory + jenkins process   #
### ==================================================================== ###
#
#

umask 077

# VICAR vicset1.csh sets this env.
if ( ! ${?V2JDK} ) then
	setenv V2JDK /usr/java
endif

# Check to see if JAVA_HOME is defined
if ( ! ${?JAVA_HOME} ) then
	setenv JAVA_HOME ${V2JDK}
endif

# Check to see if CLASSPATH is defined, set to empty string if it isn't
if ( ! ${?CLASSPATH} ) then
        setenv CLASSPATH ""
endif

# Update: No more V2HTML
foreach jar ($FEI5/../lib/*.jar)
  setenv CLASSPATH ${CLASSPATH}:$jar
end

${JAVA_HOME}/bin/java -Xms32m -Xmx75m -classpath ${CLASSPATH} \
	jpl.mipl.mdms.FileService.util.EncryptMessage
