#!/bin/csh -f
#
### ==================================================================== ###
#                                                                          #
#  The File Exchange Interface (FEI) Destroy Credentials Client Utility    #
#                                                                          #
#  Function:                                                               #
#  Destroys FEI5 user credentials.  NOTE: The $FEI5 environment            #
#  variable must point to the directory containing the domain.fei and SSL  #
#  keystore files.                                                         #
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
#  Nov. 12, 2003 by Rich Pavlovsky {rich.pavlovsky@jpl.nasa.gov}           #
#                                                                          #
#  Modifications:                                                          #
#  Nov. 25, 2003 by Rich Pavlovsky {rich.pavlovsky@jpl.nasa.gov}           #
#  Passing script name to Java application so this launcher can be         #
#  renamed or aliased at will and still output valid help information.     #
#                                                                          #
### ==================================================================== ###
#
# $Id: fei5kdestroy.csh,v 1.6 2006/07/28 01:33:21 ntt Exp $
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
   jpl.mipl.mdms.FileService.komodo.client.UtilClient $* \
   domain ${FEI5}/domain.fei action logout script_name fei5kdestroy.csh

