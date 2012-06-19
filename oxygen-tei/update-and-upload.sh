#!/bin/sh
# Sebastian Rahtz July 2012
# Grab the latest TEIP5 and XSL builds from Jenkins, rebuild
# the distribution, and upload to Google for release
#
die()
{
    echo; echo
    echo "ERROR: $@."
    D=`date "+%Y-%m-%d %H:%M:%S"`
    echo "$D. That was a fatal error"
    exit 1
}

JENKINS=http://tei.oucs.ox.ac.uk/jenkins
TEIVERSION=
XSLVERSION=
while test $# -gt 0; do
  case $1 in
    --teiversion=*)   TEIVERSION=`echo $1 | sed 's/.*=//'`;;
    --xslversion=*)   XSLVERSION=`echo $1 | sed 's/.*=//'`;;
   *) if test "$1" = "${1#--}" ; then 
	   break
	else
	   echo "WARNING: Unrecognized option '$1' ignored"
	   echo "For usage syntax issue $0 --help"
	fi ;;
  esac
  shift
done
if [ -z $TEIVERSION ] 
then
 echo You must use the --teiversion option to specify which version of TEI P5  you are installing
 exit 1
fi
if [ -z $XSLVERSION ] 
then
 echo You must use the --xslversion option to specify which version of TEI Stylesheets  you are installing
 exit 1
fi
curl -o tei.zip $JENKINS/job/TEIP5/lastSuccessfulBuild/artifact/tei-$TEIVERSION.zip
curl -o xsl.zip $JENKINS/job/Stylesheets/lastSuccessfulBuild/artifact/tei-xsl-$XSLVERSION.zip
cd frameworks/tei
rm -rf xml/tei/stylesheet
rm -rf xml/tei/odd
rm -rf xml/tei/schema
rm -rf xml/tei/custom/schema
rm -rf xml/tei/custom/odd
rm -rf xml/tei/Test 
rm -rf xml/tei/xquery
unzip -o -q ../../tei.zip
unzip -o -q ../../xsl.zip
rm -rf doc
rm -rf xml/tei/Test 
rm -rf xml/tei/xquery
cd ../..
unzip brown
rm tei.zip xsl.zip
ant
mv dist/tei.zip tei-$TEIVERSION.zip
python googlecode_upload.py -s "TEI release $TEIVERSION and XSL $XSLVERSION" -p oxygen-tei  tei-$TEIVERSION.zip   

