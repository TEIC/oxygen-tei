<oXygen/> frameworks 
==================================================

This project contains all the files  required by oXygen XML Editor in order to
provide specific support for editing standards:
- the "frameworks" folder contains the actual frameworks.
- the "lib" folder contains the jars required for building the JAVA extensions from
each framework.
- the "tools" folder is intended for required tools like the Apache ANT.
- the "dist" folder will contain the packed distribution of each framework.

Adding oxygen.jar inside the lib folder
===================================

You have to copy oxygen.jar from the lib folder of an oXygen installation inside the 
project lib folder. As an alternative, if the "framework" folder was extracted
inside an Oxygen installation oxygen.jar will be found directly inside the Oxygen lib
folder. 
The path to an oxygen.jar can also be specified inside file build.properties.

Editing the framework configuration file
========================================================

The configuration files are "*.framework" files. These can be edited using oXygen 
itself by following these steps: 
- open "Preferences"/"Global" page
- check "Use custom frameworks (Document Type Association) framework".
- at "Framework directory" specify the project "frameworks" folder.
- restart oXygen
- open "Preferences"/"Document Type Association" page. The document types presented
are the ones from the project. You can edit them using the "Edit" action.

Alternatively, if the "frameworks" folder is checked out inside an oXygen installation, 
oXygen will automatically load the frameworks. 


Building the distribution
===================================================

The project uses Apache ANT to build the distribution. The "build.xml" file
contains the ant targets. In order to obtain a distribution run "ant" inside 
a command line or use Eclipse to "Run as->Ant build" over the build file. The 
build will put each framework inside the "dist" folder.


Deployment
===================================================

After building the project, you must unzip the resulting "dist/tei.zip" file inside
oXygen frameworks folder. By default this is "{oXygenInstallationDir}/frameworks" folder but 
it can be changed as described at step "Editing the framework configuration file". 
oXygen will automatically load the framework. 