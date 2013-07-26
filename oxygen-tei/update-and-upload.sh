#!/bin/sh
#
# Sebastian Rahtz July 2012
# Grab TEIP5 and XSL builds from Sourceforge, rebuild
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
SFP5="http://downloads.sourceforge.net/project/tei/TEI-P5-all"
SFXSL="http://downloads.sourceforge.net/project/tei/Stylesheets"
TEIVERSION=
XSLVERSION=
DEBUG=0
while test $# -gt 0; do
  case $1 in
    --teiversion=*)   TEIVERSION=`echo $1 | sed 's/.*=//'`;;
    --xslversion=*)   XSLVERSION=`echo $1 | sed 's/.*=//'`;;
    --debug) DEBUG=1;;
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
echo Download $SFP5/tei-$TEIVERSION.zip
curl  -L -s -o tei.zip $SFP5/tei-$TEIVERSION.zip
echo Download $SFXSL/tei-xsl-$XSLVERSION.zip
curl  -L -s -o xsl.zip $SFXSL/tei-xsl-$XSLVERSION.zip
cd frameworks/tei
echo zap any old versions
rm -rf xml/tei/Test 
rm -rf xml/tei/custom/odd
rm -rf xml/tei/custom/schema
rm -rf xml/tei/odd
rm -rf xml/tei/schema
rm -rf xml/tei/stylesheet
rm -rf xml/tei/xquery
echo unpack new files
unzip -o -q ../../tei.zip
unzip -o -q ../../xsl.zip
echo remove unwanted material
rm -f xml/tei/Exemplars/*epub
rm -f xml/tei/Exemplars/*html
rm -f xml/tei/Exemplars/*pdf
rm -f xml/tei/Exemplars/*tex
rm -f xml/tei/Exemplars/*compiled
rm tei/xml/tei/odd/p5subset.js
rm tei/xml/tei/odd/p5subset.json
rm tei/xml/tei/odd/p5attlist.txt
rm -rf doc
rm -rf xml/tei/Test 
rm -rf xml/tei/odd/ReleaseNotes
rm -rf xml/tei/odd/Source
rm -rf xml/tei/odd/Exemplars
rm -rf xml/tei/Exemplars
rm -rf xml/tei/odd/*.css
rm -rf xml/tei/odd/p5subset.j*
rm -rf xml/tei/odd/Utilities
rm -rf xml/tei/odd/p5odds-examples.*
rm -rf xml/tei/odd/webnav
rm -rf xml/tei/xquery
rm -rf templates/TEI\ P5
mkdir -p templates/TEI\ P5
mv xml/tei/custom/templates/* templates/TEI\ P5
cd ../..
#echo add Brown specifics
#unzip brown
rm -f tei.zip xsl.zip dist/tei.zip
echo do Ant build
ant
echo move result to tei-$TEIVERSION-$XSLVERSION.zip
mv dist/tei.zip tei-$TEIVERSION-$XSLVERSION.zip
echo upload to Google
python ./googlecode_upload.py -s "TEI release $TEIVERSION and XSL $XSLVERSION" -p oxygen-tei  tei-$TEIVERSION-$XSLVERSION.zip   
rm tei-$TEIVERSION-$XSLVERSION.zip
