<!-- 
  Copyright 2001-2012 Syncro Soft SRL. All rights reserved.
 -->
<xsl:stylesheet version="2.0" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:e="http://www.oxygenxml.com/xsl/conversion-elements"
    xmlns:f="http://www.oxygenxml.com/xsl/functions"
    exclude-result-prefixes="xs f">
    
     <!-- true if we want to wrap element before heading in a section -->
    <xsl:param name="wrapElementsBeforeHeadingInSection" as="xs:boolean" select="false()"/>
    
    <xsl:template match="/">
        <xsl:apply-templates mode="nestedSections"/>
    </xsl:template>
    
    <!-- Associates to a heading the lower rank headings after it. -->
    <xsl:key 
        name="kHeadings" 
        match="xhtml:body/*[f:isHeading(.)]"
        use="generate-id(preceding-sibling::*
                        [f:isHeading(.)][substring(name(current()),2) > substring(name(),2)][1])"/>
    
    <!-- Associates to a heading the elements after it.-->
    <xsl:key 
        name="kElements" 
        match="xhtml:body/node()[not(f:isHeading(.))]"
        use="generate-id(preceding-sibling::*[f:isHeading(.)][1])"/>
    
    <!-- Copy template for the not heading nodes. -->
    <xsl:template match="node()|@*" mode="nestedSections">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="nestedSections"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:html" mode="nestedSections">
        <xsl:copy>
            <xsl:apply-templates mode="nestedSections"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:body" mode="nestedSections">
        <xsl:copy>
            <!-- Takes all elements from the heading maps that do not have 
               a higher rank heading before them. -->
            <xsl:variable name="masterHeadings" select="key('kHeadings', '')"/>
            <xsl:choose>
                <xsl:when test="empty($masterHeadings)">
                    <xsl:choose>
                        <xsl:when test="$wrapElementsBeforeHeadingInSection">
                           <xsl:call-template name="wrapWithSection">
                               <xsl:with-param name="nodes" select="./*"/>
                           </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates mode="nestedSections"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
			        <xsl:variable name="beforeFirstMasterHeading" select="*[. &lt;&lt; $masterHeadings[1]]"/>
                    <xsl:choose>
                        <xsl:when test="$wrapElementsBeforeHeadingInSection and not(empty($beforeFirstMasterHeading))">
                            <!--Group the elements before heading in a section-->
                            <xsl:call-template name="wrapWithSection">
                                <xsl:with-param name="nodes" select="$beforeFirstMasterHeading"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="$beforeFirstMasterHeading" mode="nestedSections"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:apply-templates select="$masterHeadings" mode="nestedSections"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:body/*[f:isHeading(.)]" mode="nestedSections">
        <e:section level="{substring(name(),2)}" xmlns="http://www.w3.org/1999/xhtml">
            <xsl:choose>
                <xsl:when test="@id">
                    <xsl:copy-of select="@id"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="id">
                        <xsl:variable name="counter">
                            <xsl:value-of select="count(preceding::*[f:isHeading(.)]) 
                                + count(ancestor::*[f:isHeading(.)]) + 1"/>
                        </xsl:variable>
                        <xsl:variable name="idValue">
                            <xsl:choose>
                                <xsl:when test="exists(//*[@id = concat('id_', $counter)])">
                                    <xsl:value-of select="concat('id_', generate-id())"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="concat('id_', $counter)"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:value-of select="$idValue"/>
                    </xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <!-- Copies the header content. -->
            <e:title>
                <xsl:apply-templates mode="nestedSections"/>
            </e:title>
            <!-- Process all elements from its beginning to the next heading (lower or higher rank.)-->
            <xsl:apply-templates select="key('kElements', generate-id())" mode="nestedSections"/>
            <!-- Processes all the headings (lower rank only) recursively-->
            <xsl:apply-templates select="key('kHeadings', generate-id())" mode="nestedSections"/>
        </e:section>
    </xsl:template>
    
    <xsl:template name="wrapWithSection">
        <xsl:param name="nodes"/>
        <e:section level="1" xmlns="http://www.w3.org/1999/xhtml">
            <xsl:attribute name="id">
                <xsl:choose>
                    <xsl:when test="exists(//*[@id = 'id'])">
                        <xsl:value-of select="concat('id_', generate-id())"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:sequence>id</xsl:sequence>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <e:title>
            </e:title>
            <!-- Copies the nodes. -->
            <xsl:apply-templates select="$nodes" mode="nestedSections"/>
        </e:section>
    </xsl:template>
    
    <xsl:function name="f:isHeading" as="xs:boolean">
        <xsl:param name="n" as="node()"/>
        <xsl:sequence select="xs:boolean(
                    local-name($n) = 'h1' or 
                    local-name($n) = 'h2' or 
                    local-name($n) = 'h3' or 
                    local-name($n) = 'h4' or 
                    local-name($n) = 'h5' or 
                    local-name($n) = 'h6')
            "/>
    </xsl:function>
</xsl:stylesheet>
