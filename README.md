# oXygen XML Editor frameworks for TEI 

This project contains the oXygen XML Editor specific support for TEI. 
It is organized as follows:
* the "frameworks" folder contains the actual TEI frameworks.
* the "lib" folder contains the jars required for building the JAVA extensions from each framework.
* the "tools" folder is intended for required tools like the Apache ANT.
* the "dist" folder will contain the packed distribution of all frameworks.

# If you just want to update your oXygen XML Editor framework for TEI
If all you want to do is update your oXygen XML Editor framework for TEI to the latest release then
the (now out of date) suggestions at https://faqingperplxd.wordpress.com/2014/04/02/auto-update-your-tei-framework-in-oxygen/ 
with some differences because of the changes in oXygen versions since then.  If you really wanted to get the 
development version and have that auto-update you could point to the oxygen-tei-stable or oxygen-tei-bleeding 
jobs on http://jenkins.tei-c.org e.g. http://jenkins.tei-c.org/job/oxygen-tei-stable/lastSuccessfulBuild/artifact/oxygen-tei/updateSite.oxygen but be warned that here be dragons.

# Before you begin - adding oxygen.jar to the project

The extensions defined in the project depend on the oxygen.jar library that is
not distributed with this project. There are a few possibilities make that 
library available:

* you can copy oxygen.jar from the lib folder of an oXygen installation inside the project lib folder
* edit the build.properties file and add an entry like oxygen.jar = path/to/your/oxygen.jar

As an alternative, if the project frameworks/tei folder is checked out as the 
oXygen frameworks/tei folder then the oxygen.jar will be found automatically by
looking inside the Oxygen lib folder. 

# Editing the frameworks configuration files

The framework configurations are stored in a "*.framework" files. These can be 
edited using oXygen through the Options -> Preferences -- Document Type 
Associations option page.

The frameworks should be the current oXygen frameworks, so you need to place 
your framework folder inside oXygen frameworks folder or if you have this in a 
different folder then you need to configure oXygen to use that folder as the 
folder that contains the frameworks. You can select a different frameworks 
folder following the steps below:
* go to Options -> Preferences -- Global options page
* check "Use custom frameworks (Document Type Association) framework"
* in the "Framework directory" field specify your frameworks folder
* restart oXygen
Now if you go to Options -> Preferences -- Document Type Association page you 
should see there the frameworks defined in your frameworks folder. 

# Building the distribution

The project uses Apache ANT to build the distribution. The "build.xml" file
contains the ant targets. In order to obtain a distribution run "ant" inside 
a command line or use Eclipse to "Run as->Ant build" over the build file. The 
build will put the frameworks inside the "dist" folder packed in a zip archive.

# Deployment

After building the project, you must unzip the resulting "dist/tei.zip" file 
inside oXygen frameworks folder. By default this is the frameworks" folder from 
your oXygen installation but it can be changed as described at "Editing the 
framework configuration file" section above. 
