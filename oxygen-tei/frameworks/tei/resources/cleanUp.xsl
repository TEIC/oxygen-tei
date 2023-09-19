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
    
    <!-- EXM-52685: Remove dir attribute that has "auto" value-->
    <xsl:template match="@dir[. = 'auto']"  mode="cleanUp"/>

    <!-- EXM-52717: Move inner lists without a parent list item into a list item -->
    <xsl:template match="xhtml:ul | xhtml:ol" mode="cleanUp">
        <xsl:variable name="firstParentLocalName" select="local-name(parent::*[1])"></xsl:variable>
        <xsl:choose>
          <xsl:when test="$firstParentLocalName = ('ol', 'ul')">
                <xsl:choose>
                    <xsl:when test="preceding-sibling::xhtml:li[1]">
                        <!-- We have a preceding sibling. Ignore this ul because it will migrate to this sibling -->
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:element name="li" namespace="http://www.w3.org/1999/xhtml">
                            <xsl:copy>
                                <xsl:apply-templates select="node() | @*" mode="cleanUp" />
                            </xsl:copy>
                        </xsl:element>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*" mode="cleanUp" />
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="xhtml:li" mode="cleanUp">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="cleanUp" />
            <xsl:variable name="followingSibling" select="following-sibling::*[1]" />
            <xsl:if test="$followingSibling and local-name($followingSibling) = ('ul' ,'ol')">
                <xsl:copy-of select="$followingSibling" />
            </xsl:if>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>