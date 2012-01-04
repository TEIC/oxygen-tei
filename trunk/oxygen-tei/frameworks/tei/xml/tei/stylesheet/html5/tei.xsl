<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="tei"
    version="2.0">
    <!-- import base conversion style -->

    <xsl:import href="../xhtml2/tei.xsl"/>
    <xsl:import href="textstructure.xsl"/>
    <xsl:output method="xml" omit-xml-declaration="yes" doctype-system="about:legacy-compat" />
    <xsl:param name="outputTarget">html5</xsl:param>
    <xsl:param name="doctype-system">about:legacy-compat</xsl:param>
    <xsl:param name="doctype-public"/>
    
    <xsl:template match="/">
	<xsl:call-template name="processTEI"/>
    </xsl:template>

<!--
    <xsl:template match="@*|text()|comment()|processing-instruction()"  mode="html5">
      <xsl:copy-of select="."/>
    </xsl:template>
        
    <xsl:template match="*" mode="html5">
      <xsl:element name="{local-name()}">
         <xsl:apply-templates
	     select="*|@*|processing-instruction()|comment()|text()"  mode="html5"/>
      </xsl:element>
   </xsl:template>
-->
</xsl:stylesheet>