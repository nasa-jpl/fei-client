#!/bin/csh -f
#
### ==================================================================== ###
#                                                                          #
#  The File Exchange Interface (FEI) GUI Client Bootstrap Script           #
#                                                                          #
#  Function:                                                               #
#  Launches the FEI GUI client application.  NOTE: The $FEI5 environment   #
#  variable must point to the directory containing the domain.fei and SSL  #
#  keystore files.                                                         #
#                                                                          #
#  Assumptions:                                                            #
#  - The target platform uses ":" as a classpath separator or perl         #
#    indicates it is dos/win32.                                            #
#  - The target platform uses "/" as a directory separator.                #
#                                                                          #
#  Copyright (c) 2006 by the California Institute of Technology.           #
#  ALL RIGHTS RESERVED.  United States Government Sponsorship              #
#  acknowledged. Any commercial use must be negotiated with the            #
#  Office of Technology at the California Institute of Technology.         #
#                                                                          #
#  Installation under terms of the software license.  The Department       #
#  of Commerce has classified the FEI Client 5 Software as EAR 99,         #
#  which means that the software may be distributed to any country         #
#  except the Terrorist 6.  The Terrorist 6 Countries include North        #
#  Korea, Cuba, Iran, Syria, Sudan and Libya.                              #
#                                                                          #
#                                                                          #
#  Created:                                                                #
#  Jul. 20, 2004 by Rich Pavlovsky {rich.pavlovsky@jpl.nasa.gov}           #
#                                                                          #
#  Modifications:                                                          #
#  Aug. 17, 2004 by Rich Pavlovsky {rich.pavlovsky@jpl.nasa.gov}           #
#  Added System Parameter for SAX XML Parsing                              #
### ==================================================================== ###
#
# $Id: fei5gui.csh,v 1.3 2006/07/28 01:33:21 ntt Exp $
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


# configure restart directory
if ( ! ${?FEI5CCDIR} ) then
   setenv FEI5CCDIR $HOME
endif

${JAVA_HOME}/bin/java -Xms32m -Xmx75m -classpath ${CLASSPATH} \
   -Djavax.net.ssl.trustStore=${FEI5}/mdms-fei.keystore \
   -Dkomodo.restartdir=${FEI5CCDIR} \
   -Ddomain.file=${FEI5}/domain.fei \
   -Dmdms.logging.config=$FEI5/log4j.xml \
   -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
   -Dorg.xml.sax.driver=org.apache.xerces.parsers.SAXParser \
   jpl.mipl.mdms.FileService.komodo.ui.savannah.Savannah $*

