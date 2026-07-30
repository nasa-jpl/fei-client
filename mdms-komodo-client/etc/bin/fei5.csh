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
#  The File Exchange Interface (FEI) Bootstrap Script		         	   #
#                                                                          #
#  Function:                                                               #
#  Launches the FEI client application.  NOTE: The $FEI5 environment       #
#  variable must point to the directory containing the domain.fei and SSL  #
#  keystore files.                                                         #
#                                                                          #
#                                                                          #
#  Korea, Cuba, Iran, Syria, Sudan and Libya.                              #
#                                                                          #
#  Created:                                                                #
#  Nov. 26, 2002 by Rich Pavlovsky {rich.pavlovsky@jpl.nasa.gov}           #
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
#  Sep. 19, 2003 by Rich Pavlovsky {rich.pavlovsky@jpl.nasa.gov}           #
#  Changed the restartdir parameter on FEI5 client.  Introduced the        #
#  $FEI5RDIR env. variable which should point to the directory to save     #
#  save the restart dir to.  If $FEI5RDIR is not set, default to home dir. #
#                                                                          #
#  Oct. 07, 2003 by Thomas Huang {Thomas.Huang@jpl.nasa.gov}               #
#  Changed to look for restart directory in environment variable FEI5CCDIR,#
#  corrected bug in classpath construction by parsing V2HTML.  Added XML   #
#  parser setting to user xerces.                                          #
#                                                                          #
#  Oct. 08, 2003 by Rich Pavlovsky {rich.pavlovsky@jpl.nasa.gov}           #
#  Set CLASSPATH to an empty string if not defined, changed etc directory  #
#  to config so it'll match the users guide.                               #
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

# Check to see if FEI is defined. Set to ops domain file if not defined.
if ( ! ${?FEI5} ) then
	echo "FEI5 environment variable is not set!  setting to ../config\n\n"
	setenv FEI5 ../config
endif

# Check to see if CLASSPATH is defined, set to empty string if it isn't
if ( ! ${?CLASSPATH} ) then
	setenv CLASSPATH ""
endif

# Update: No more V2HTML
foreach jar ($FEI5/../lib/*.jar)
  setenv CLASSPATH ${CLASSPATH}:$jar
end

# Set the PWDSERVER env variable to use MDMS PWD Client
#if ( ! ${?PWDSERVER} ) then
#	echo "\nPWDSERVER is not set! setting to ../etc\n\n"
#	setenv PWDSERVER ${FEI5}
#endif

# Set the KRB5_CONFIG env variable to use MDMS PWD Client
#if ( ! ${?KRB5_CONFIG} ) then
#	echo "\nKRB5_CONFIG is not set! setting to ../etc/krb5.conf\n\n"
#	setenv KRB5_CONFIG ${FEI5}/krb5.conf
#endif

# configure restart directory
if ( ! ${?FEI5CCDIR} ) then
   setenv FEI5CCDIR $HOME
endif

${JAVA_HOME}/bin/java -Xms32m -Xmx75m -classpath ${CLASSPATH} \
   -Djavax.net.ssl.trustStore=${FEI5}/mdms-fei.keystore \
   -Dkomodo.restartdir=${FEI5CCDIR} \
   -Ddomain.file=${FEI5}/domain.fei \
   -Dmdms.logging.config=${FEI5}/log4j.xml \
   -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
   jpl.mipl.mdms.FileService.komodo.client.GeneralClient $*

