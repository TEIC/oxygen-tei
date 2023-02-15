<!-- 
  Copyright 2022 Syncro Soft SRL. All rights reserved.
 -->
<xsl:stylesheet version="3.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:f="http://www.oxygenxml.com/xsl/functions"
    exclude-result-prefixes="f">
    
    <xsl:template match="node() | @*" mode="filterMultipleAnchors">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="filterMultipleAnchors"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*" mode="filterMultipleAnchors" priority="2">
        <xsl:copy>
            <xsl:apply-templates select="./@*" mode="filterMultipleAnchors"/>
             <!-- The html resulted from a Word conversion contains multiple sibling anchors with id. We want to keep only one-->
            <xsl:apply-templates select="node()[not(self::xhtml:a[not(node())][preceding-sibling::*[1][local-name() = 'a'][not(node())][@id or @name]])]" mode="filterMultipleAnchors"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:a/@href[starts-with(.,'#')]" mode="filterMultipleAnchors">
        <xsl:attribute name="href">
            <xsl:variable name="currentId" select="normalize-space(substring(., 2))"/>
            <xsl:variable name="elementWithId" select="(//*[@id = $currentId or @name = $currentId])[1]"/>
            
            <xsl:choose>
                <xsl:when test="$elementWithId and local-name($elementWithId) = 'a' and not($elementWithId/node())">
                    <!-- EXM-45627 The html resulted from a Word conversion contains multiple sibling anchors with id. We want to keep only one.
                    So we need to rewrite the hrefs to point at first anchor -->
                    <xsl:variable name="precedingNonAnchor" select="$elementWithId/preceding-sibling::*[not (local-name() = 'a')][1]"/>
                    <xsl:choose>
                        <xsl:when test="$precedingNonAnchor">
                            <xsl:value-of select="concat('#', f:extractIdFromAnchor($precedingNonAnchor/following::xhtml:a[@id or @name][not(node())][1]) )"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:variable name="firstAnchor" select="$elementWithId/preceding-sibling::xhtml:a[@id or @name][not(node())][last()]"/>
                            <xsl:choose>
                                <xsl:when test="$firstAnchor">
                                    <xsl:value-of select="concat('#', f:extractIdFromAnchor($firstAnchor) )"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="concat('#', f:extractIdFromAnchor($elementWithId) )"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat('#', f:correctId($currentId) )"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>
</xsl:stylesheet>
