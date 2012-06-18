JENKINS=http://tei.oucs.ox.ac.uk/jenkins
TEIVERSION=2.1.0
XSLVERSION=6.12
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
cd ../..
rm tei.zip xsl.zip
ant
mv dist/tei.zip tei-$TEIVERSION.zip
python googlecode_upload.py -s "TEI release $TEIVERSION and XSL $XSLVERSION" -p oxygen-tei -w wH7AD7BY7mS2 tei-$TEIVERSION.zip   

