<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
  exclude-result-prefixes="xs xd"
  version="2.0"
  xmlns:xt="http://www.oxygenxml.com/ns/extension"
  xmlns:local="http://teijenkins.hcmc.uvic.ca/ns">
  <xd:doc scope="stylesheet">
    <xd:desc>
      <xd:p><xd:b>Created on:</xd:b> May 15, 2015</xd:p>
      <xd:p><xd:b>Author:</xd:b> mholmes</xd:p>
      <xd:p>This stylesheet processes the updateSite.oxygen XML file 
      from the tei-c.org website in two ways: first, without a version 
      number parameter, it simply reports the last release version 
      number of the plugin, in a text file, so that this can be used 
      as part of the ant build task to prompt the user for the next 
      version number. If a version number is provided, it updates 
      the file by adding a new release link with that version number.</xd:p>
    </xd:desc>
  </xd:doc>
  
  <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
  <xsl:output name="textReport" encoding="UTF-8" method="text" indent="no"/>
  
  <xsl:param name="proposedVersionNumber"/>
  <xsl:param name="newZipFileName"/>
  <xsl:param name="sfReleaseLocation"/>
  <xsl:param name="ghReleaseLocation"/>
  
  <xsl:variable name="newZipFileUrl" select="concat($ghReleaseLocation, 'v', $proposedVersionNumber, '/', $newZipFileName)"/>  
  <xsl:variable name="zipFilenameBits" select="tokenize(replace($newZipFileName, '.zip$', ''), '-')"/>
  <xsl:variable name="teiVersion" select="$zipFilenameBits[3]"/>
  <xsl:variable name="stylesheetsVersion" select="$zipFilenameBits[4]"/>
  
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="string-length($proposedVersionNumber) lt 5">
        <xsl:variable name="lastVersion" select="//xt:extension[not(following-sibling::xt:extension)]/xt:version/text()"/>
        <xsl:result-document href="lastVersion" format="textReport"><xsl:value-of select="normalize-space($lastVersion)"/></xsl:result-document>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
<!-- Handing for the history of released artifacts. -->
<!-- Keep only five: four plus the new one. -->
  <xsl:template match="xt:extension[count(following-sibling::xt:extension) gt 4]"/>
  <xsl:template match="xt:extension[not(following-sibling::xt:extension)]">
    <xsl:copy-of select="."/>
    <xsl:call-template name="createNewExtensionElement">
      <xsl:with-param name="lastExtension" select="."/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template name="createNewExtensionElement">
    <xsl:param name="lastExtension" as="element(xt:extension)"/>
    <xt:extension id="{$lastExtension/@id}">
      <xt:location href="{$newZipFileUrl}"/>
      <xt:version><xsl:value-of select="$proposedVersionNumber"/></xt:version>
      <xsl:copy-of select="$lastExtension/xt:version/(following-sibling::xt:*[not(local-name() = ('location', 'version', 'description'))]|following-sibling::text())"/>
      <xt:description>
        Oxygen TEI plugin based on the latest stable release of TEI P5 (<xsl:sequence select="$teiVersion"/>) and the
        latest stable release of the TEI Stylesheets (<xsl:sequence select="$stylesheetsVersion"/>). To avoid conflict with builtin
        framework, please ensure that you have gone to Preferences-&gt;Document Type Association in
        oXygen and deactivated all the TEI frameworks that have "External" in the storage columns.
      </xt:description>
    </xt:extension>
  </xsl:template>
  
  <!-- Identity transform for everything else. -->
  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy copy-namespaces="no">
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>