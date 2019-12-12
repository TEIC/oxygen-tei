<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xt="http://www.oxygenxml.com/ns/extension"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:map="http://www.w3.org/2005/xpath-functions/map"
    xmlns:array="http://www.w3.org/2005/xpath-functions/array"
    exclude-result-prefixes="xs"
    version="3.0">
    
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> April 9, 2019</xd:p>
            <xd:p><xd:b>Author:</xd:b> Peter Stadler</xd:p>
            <xd:p>This stylesheet processes an updateSite.oxygen file
                and uses the first xt:extension element as template,
                It checks the GitHub API for all releases of the 
                oxygen-tei plugin and creates xt:extension elements 
                for every release by processing the above template.
            </xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:param name="releases.endpoint" 
        select="'https://api.github.com/repos/TEIC/oxygen-tei/releases'" 
        as="xs:string"/>
    
    <xsl:variable name="github.releases" 
        select="json-doc($releases.endpoint)" 
        as="array(*)"/>
    <xsl:variable name="extension.template" 
        select=".//xt:extension[1]" 
        as="element(xt:extension)?"/>
    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="xt:extensions">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <!-- iterate over releases array from GitHub API -->
            <xsl:for-each select="$github.releases?*">
                <!-- … and process our $extension.template for each array member -->
                <xsl:apply-templates select="$extension.template">
                    <!-- … with the tunneled JSON object (= a release) -->
                    <xsl:with-param name="model" select="." tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xt:version">
        <xsl:param name="model" tunnel="yes"/>
        <xsl:copy>
            <!-- strip off the leading 'v' from the version number (e.g. 'v8.0.0') -->
            <xsl:value-of select="substring($model?tag_name, 2)"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xt:location">
        <xsl:param name="model" tunnel="yes"/>
        <xsl:copy>
            <xsl:attribute name="href">
                <xsl:value-of select="$model?assets?*?browser_download_url"/>
            </xsl:attribute>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>