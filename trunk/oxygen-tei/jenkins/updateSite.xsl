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
  
  <xsl:param name="teiVersionNumber"/>
  <xsl:param name="jenkinsJobLocation" select="'http://teijenkins.hcmc.uvic.ca/job/oxygen-tei/'"/>
  <xsl:param name="jenkinsBuildNumber"/>
  <xsl:param name="newZipFileName"/>
  <xsl:variable name="newZipFileUrl" select="concat($jenkinsJobLocation, $jenkinsBuildNumber, '/artifact/oxygen-tei/', $newZipFileName)"/>
  
<!-- Handing for the history of released artifacts. -->
<!-- Keep only ten: nine plus the new one. -->
  <xsl:template match="xt:location[count(following-sibling::xt:location) gt 8]"/>
  <xsl:template match="xt:location[not(following-sibling::xt:location)]">
    <xsl:copy-of select="."/>
    <xt:location href="{$newZipFileUrl}"/>
  </xsl:template>
  
<!--  Handling for the version number: should be incremented, and 
     we need to check whether the TEI version number has changed. -->
  <xsl:template match="xt:version">
    <xsl:variable name="teiVMajor" select="tokenize($teiVersionNumber, '\.')[1]"/>
    <xsl:variable name="teiVMinor" select="tokenize($teiVersionNumber, '\.')[2]"/>
    <xsl:variable name="localVMajor" select="tokenize(normalize-space(.), '\.')[1]"/>
    <xsl:variable name="localVMinor" select="tokenize(normalize-space(.), '\.')[2]"/>
    <xsl:choose>
      <xsl:when test="$teiVMajor = $localVMajor and $teiVMinor = $localVMinor">
        <xt:version><xsl:value-of select="concat($localVMajor, '.', $localVMinor, '.', local:getIncrementedVersion(tokenize(normalize-space(.), '\.')[3]))"/></xt:version>
      </xsl:when>
      <xsl:otherwise>
        <xt:version><xsl:value-of select="concat($teiVMajor, '.', $teiVMinor, '.1')"/></xt:version>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
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