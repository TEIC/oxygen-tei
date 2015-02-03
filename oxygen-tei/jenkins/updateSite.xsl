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
      <xd:p><xd:b>Created on:</xd:b> Jan 30, 2015</xd:p>
      <xd:p><xd:b>Author:</xd:b> mholmes</xd:p>
      <xd:p>This stylesheet processes the updateSite.oxygen XML file 
      at the end of a Jenkins build to add the latest build of the plugin 
      to the list of available builds, remove older builds (we keep ten in 
      the list) and update the version number.</xd:p>
    </xd:desc>
  </xd:doc>
  
  <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
  
  <xsl:param name="teiVersionNumber"/>
  <xsl:param name="jenkinsJobLocation" select="'http://teijenkins.hcmc.uvic.ca/job/oxygen-tei/'"/>
  <xsl:param name="jenkinsBuildNumber"/>
  <xsl:param name="newZipFileName"/>
  <xsl:variable name="newZipFileUrl" select="concat($jenkinsJobLocation, $jenkinsBuildNumber, '/artifact/oxygen-tei/', $newZipFileName)"/>
  
<!-- Handing for the history of released artifacts. -->
<!-- Keep only ten: nine plus the new one. -->
  <xsl:template match="xt:extension[count(following-sibling::xt:extension) gt 8]"/>
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
      <xsl:sequence select="local:getNextVersionNumber($lastExtension/xt:version[1])"/>
      <xsl:copy-of select="$lastExtension/xt:version/(following-sibling::xt:*[not(local-name() = ('location', 'version'))]|following-sibling::text())"/>
    </xt:extension>
  </xsl:template>
  
<!--  Handling for the version number: should be incremented, and 
     we need to check whether the TEI version number has changed. -->
  <xsl:function name="local:getNextVersionNumber" as="element(xt:version)">
    <xsl:param name="lastVersion" as="element(xt:version)"/>
    <xsl:variable name="teiVMajor" select="tokenize($teiVersionNumber, '\.')[1]"/>
    <xsl:variable name="teiVMinor" select="tokenize($teiVersionNumber, '\.')[2]"/>
    <xsl:variable name="lastVMajor" select="tokenize(normalize-space($lastVersion), '\.')[1]"/>
    <xsl:variable name="lastVMinor" select="tokenize(normalize-space($lastVersion), '\.')[2]"/>
    <xsl:variable name="lastVBuild" select="tokenize(normalize-space($lastVersion), '\.')[3]"/>
    <xsl:choose>
      <xsl:when test="$teiVMajor = $lastVMajor and $teiVMinor = $lastVMinor">
        <xt:version><xsl:value-of select="concat($lastVMajor, '.', $lastVMinor, '.', local:getIncrementedVersion($lastVBuild))"/></xt:version>
      </xsl:when>
      <xsl:otherwise>
        <xt:version><xsl:value-of select="concat($teiVMajor, '.', $teiVMinor, '.1')"/></xt:version>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  
  <xsl:function name="local:getIncrementedVersion" as="xs:string">
    <xsl:param name="version" as="xs:string"/>
    <xsl:variable name="digitsOnly" select="replace($version, '[^0-9]', '')"/>
    <xsl:value-of select="concat(xs:string(xs:integer($digitsOnly) + 1), substring-after($version, $digitsOnly))"/>
  </xsl:function>
  
  <!-- Identity transform for everything else. -->
  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy copy-namespaces="no">
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>