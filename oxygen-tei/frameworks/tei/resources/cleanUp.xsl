<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:f="http://www.oxygenxml.com/xsl/functions"
    exclude-result-prefixes="xs xhtml f"
    version="2.0">
    
    <xsl:template match="node() | @*" mode="cleanUp">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="cleanUp"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Remove the unwanted bold elements added by Browsers when text is copied from google docs. -->
    <xsl:template match="xhtml:b[f:hasFontStyle(@style, 'font-weight', 'normal')]" mode="cleanUp">
	    <xsl:apply-templates mode="cleanUp"/>	
    </xsl:template>
    
</xsl:stylesheet>