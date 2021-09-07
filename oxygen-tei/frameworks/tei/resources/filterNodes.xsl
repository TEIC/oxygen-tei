<!-- 
  Copyright 2001-2012 Syncro Soft SRL. All rights reserved.
 -->
<xsl:stylesheet version="3.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:f="http://www.oxygenxml.com/xsl/functions"
    exclude-result-prefixes="f">
    
    <xsl:template match="*" mode="filterNodes">
        <xsl:copy>
            <!-- EXM-45627: Copy the id from the empty child anchor-->
            <xsl:if test="not(@id) and child::xhtml:a[1][@id][not(node())]">
                <xsl:attribute name="id">
                    <xsl:value-of select="f:correctId(child::xhtml:a[1]/@id)"/>
                </xsl:attribute>
            </xsl:if>
            
            <xsl:apply-templates select="node() | @*" mode="filterNodes"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="processing-instruction() |comment() | @*" mode="filterNodes">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="filterNodes"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:a/@href[starts-with(.,'#')]" mode="filterNodes">
        <xsl:attribute name="href">
            <xsl:variable name="currentId" select="normalize-space(substring(., 2))"/>
            <xsl:variable name="elementWithId" select="//*[@id = $currentId]"/>
            
            <xsl:choose>
                <xsl:when test="$elementWithId[local-name() = 'a'][not(node())]/preceding-sibling::xhtml:a[@id][not(node())][last()]">
                    <!-- EXM-45627 The html resulted from a Word conversion contains multiple sibling anchors with id. We want to keep only one.
                        So we need to rewrite the hrefs to point at first anchor -->
                    <xsl:variable name="firstAnchorId">
                        <xsl:value-of select="$elementWithId/preceding-sibling::xhtml:a[@id][not(node())][last()]/@id"/>
                    </xsl:variable>
                    <xsl:value-of select="concat('#', f:correctId($firstAnchorId))"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat('#', f:correctId($currentId))"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>
    
    <xsl:template match="*/@id" mode="filterNodes">
        <xsl:attribute name="id">
            <xsl:value-of select="f:correctId(.)"/>
        </xsl:attribute>
    </xsl:template>
    
    <!--
        Corrects id of a topic such as it will NCName.
            Moreover it eliminates "%20".</xd:desc>
        Para "text": Text to be corrected</xd:param>
        Return: The corrected text which can be used as id</xd:return>
    -->
    <xsl:function name="f:correctId" as="xs:string">
        <xsl:param name="text" as="xs:string"/>
        <xsl:variable name="tempId" select="replace(xs:string($text), '%20', '_')"/>
        <xsl:variable name="tempId2" select="replace($tempId, '[^\c_-]|[+:]', '_')"/>
        <xsl:variable name="tempId3" select="replace($tempId2,'[_]+', '_')"/>
        <xsl:variable name="tempId4" select="replace($tempId3,'_$', '')"/>
        <xsl:value-of select="replace($tempId4, '^[0-9.-/_]+', '')"/>
    </xsl:function>
    
    <!-- CSS properties of fonts in MSOffice -->
    <xsl:variable name="stylesPropMap" as="map(xs:string, xs:string)" 
        select="map{
        'bold' : 'font-weight',
        'italic' : 'font-style',
        'underlined' : 'text-decoration',
        'underlined2' : 'text-decoration-line',
        'monospaced' : 'font-family'
        }"/>
    
    <!-- CSS properties values in MSOffice -->
    <xsl:variable name="stylesValMap" as="map(xs:string, xs:string)"
        select="map{
        'bold' : 'bold',
        'bold700' : '700',
        'italic' : 'italic',
        'underlined' : 'underline',
        'monospaced' : 'Courier New'
        }"/>
    
    <!--  
        Possibly we could at some point we could set the anchor name to the parent element ID
        <xsl:template match="xhtml:*[xhtml:a[@name != '']][not(@id)]" mode="filterNodes">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="filterNodes"/>
            <xsl:attribute name="id" select="xhtml:a[@name != ''][1]/@name"/>
            <xsl:apply-templates select="node()" mode="filterNodes"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:a[@name != ''][parent::xhtml:*[not(@id)]]" mode="filterNodes">
        <!-\- Ignore, we pass the ID on the parent element. -\->
        <xsl:apply-templates select="node()" mode="filterNodes"/>
    </xsl:template>-->
    
    <!-- EXM-36613 Convert word-style links to XHTML style links. -->
    <xsl:template match="text()" mode="filterNodes">
        <xsl:variable name="linkComment" select="preceding-sibling::node()[1][self::comment()][contains(., 'mso- element:field- begin') and contains(., 'REF ')]"/>
        <xsl:variable name="refTarget" select="substring-before(substring-after($linkComment, 'REF '), ' \h')"/>
        <xsl:choose>
            <xsl:when test="$linkComment and $refTarget">
                <a href="#{$refTarget}" xmlns="http://www.w3.org/1999/xhtml">
                    <xsl:copy-of select="."/>
                </a>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Transform MS Word titles to XHTML titles. -->
    <xsl:template match="xhtml:div[xhtml:p[@class = 'MsoTitle']]" mode="filterNodes">
        <h1 xmlns="http://www.w3.org/1999/xhtml">
            <xsl:value-of select="xhtml:p[@class = 'MsoTitle']"/>
        </h1>
    </xsl:template>
    
    <!-- 
        ===============================
        Manage  styling
        ===============================
    -->
    
    <xsl:template match="xhtml:span[
        f:hasFontStyle(@style, $stylesPropMap('bold'), $stylesValMap('bold')) or
        f:hasFontStyle(@style, $stylesPropMap('bold'), $stylesValMap('bold700')) or
        f:hasFontStyle(@style, $stylesPropMap('italic'), $stylesValMap('italic')) or
        f:hasFontStyle(@style, $stylesPropMap('underlined'), $stylesValMap('underlined')) or
        f:hasFontStyle(@style, $stylesPropMap('underlined2'), $stylesValMap('underlined'))
        ]" mode="filterNodes">
        
        <xsl:call-template name="styling">
            <!-- The three props: bold, italic and underline are passed automatically. 
                They are used to create an order when parsing the fragment styles. 
            -->
            <xsl:with-param name="toConsume" select="('bold', 'italic', 'underline')" tunnel="yes"/>
            <!-- position in the so-called array = "toConsume" ('bold', 'italic', 'underline');  -->
            <xsl:with-param name="index" select="xs:integer(1)"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="xhtml:td[
        f:hasFontStyle(@style, $stylesPropMap('bold'), $stylesValMap('bold')) or
        f:hasFontStyle(@style, $stylesPropMap('bold'), $stylesValMap('bold700')) or
        f:hasFontStyle(@style, $stylesPropMap('italic'), $stylesValMap('italic')) or
        f:hasFontStyle(@style, $stylesPropMap('underlined'), $stylesValMap('underlined')) or
        f:hasFontStyle(@style, $stylesPropMap('underlined2'), $stylesValMap('underlined'))
        ]" mode="filterNodes">
        
        <xsl:copy>
            <xsl:for-each select="@*">
                <xsl:copy select="."/>
            </xsl:for-each>
            <xsl:call-template name="styling">
                <!-- The three props: bold, italic and underline are passed automatically. 
                    They are used to create an order when parsing the fragment styles. 
                -->
                <xsl:with-param name="toConsume" select="('bold', 'italic', 'underline')" tunnel="yes"/>
                <!-- position in the so-called array = "toConsume" ('bold', 'italic', 'underline');  -->
                <xsl:with-param name="index" select="xs:integer(1)"/>
                
                <xsl:with-param name="copyChildren" select="true()" tunnel="yes"/>
            </xsl:call-template>
        </xsl:copy>
    </xsl:template>
    
    <!-- 
        Preserve font style at paste from google doc.
    -->
    <xsl:template name="styling">
        <xsl:param name="toConsume" as="xs:string*" tunnel="yes"/>
        <xsl:param name="index" as="xs:integer"/>
        <xsl:param name="copyChildren" as="xs:boolean" select="false()" tunnel="yes"/>
        
        <xsl:if test="$index &lt;= count($toConsume)">
            <xsl:choose>
                <!-- 1. check if the current prop is bold.
                    If the bold prop is not detected, increment the index and advance to next prop.
                    See the <xsl:otherwise> condition.
                -->
                <xsl:when test="$toConsume[$index]='bold' and
                    (xs:boolean(f:hasFontStyle(@style, $stylesPropMap('bold'), $stylesValMap('bold'))) or 
                    xs:boolean(f:hasFontStyle(@style, $stylesPropMap('bold'), $stylesValMap('bold700'))))">
                    <!-- 2. emit the first(bold) tag -->
                    <b xmlns="http://www.w3.org/1999/xhtml">
                        <!-- 3. now apply the styling template, with the next porp: index -->
                        <xsl:call-template name="styling">
                            <xsl:with-param name="index" select="$index + 1"/>
                        </xsl:call-template>
                        <!-- if the next prop is not found, close the current element.-->
                    </b>        
                </xsl:when>
                <!-- the bold prop was consumed; look for italic now -->
                <xsl:when test="$toConsume[$index]='italic' and 
                    xs:boolean(f:hasFontStyle(@style, $stylesPropMap('italic'), $stylesValMap('italic')))">
                    <!-- 4. emit the italic tag -->
                    <i xmlns="http://www.w3.org/1999/xhtml">
                        <xsl:call-template name="styling">
                            <!-- advance to next prop to consume-->
                            <xsl:with-param name="index" select="$index + 1"/>
                        </xsl:call-template>
                        <!-- close it -->
                    </i>        
                </xsl:when>
                <!-- underline style -->
                <xsl:when test="$toConsume[$index]='underline' and
                    xs:boolean(f:hasFontStyle(@style, $stylesPropMap('underlined'), $stylesValMap('underlined'))
                      or f:hasFontStyle(@style, $stylesPropMap('underlined2'), $stylesValMap('underlined')))">
                    <u xmlns="http://www.w3.org/1999/xhtml">
                        <xsl:call-template name="styling">
                            <xsl:with-param name="index" select="$index + 1"/>
                        </xsl:call-template>
                    </u>        
                </xsl:when>
                <xsl:otherwise>
                    <!-- 
                        1. if the bold property is not found, advance to next element
                    to consume by incrementing the index.
                    -->
                    <xsl:call-template name="styling">
                        <xsl:with-param name="index" select="$index + 1"/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        
        <!-- copy the text -->
        <xsl:if test="$index > count($toConsume)">
            <xsl:copy-of select="if ($copyChildren) then (node()) else (.)"/>
        </xsl:if>
    </xsl:template>
    
    <!-- 
        Check if the font style has a property
    -->
    <xsl:function name="f:hasFontStyle" as="xs:boolean">
        <xsl:param name="styleValue"/>
        <xsl:param name="propParam"/>
        <xsl:param name="valParam"/>
        
        <xsl:variable name="toReturn" as="xs:boolean*">
            <xsl:for-each select="tokenize($styleValue,';')">
                <xsl:variable name="propAndValue" select="tokenize(., ':')"/>
                <xsl:variable name="property" select="normalize-space($propAndValue[1])"/>
                <xsl:variable name="value" select="normalize-space($propAndValue[2])"/>
                <xsl:choose>
                    <xsl:when test="$property = $stylesPropMap('monospaced')">
                        <xsl:if test="contains($value, $valParam)">
                            <xsl:value-of select="true()"/>
                        </xsl:if>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:if test="$property = $propParam">
                            <xsl:if test="$value = $valParam">
                                <xsl:value-of select="true()"/>
                            </xsl:if>
                        </xsl:if>    
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:variable>
        
        <xsl:value-of select="$toReturn = true()"/>
    </xsl:function>    
    
    <!-- Unwrap xhtml:div nodes and keep only the child nodes. -->
    <xsl:template match="xhtml:div | xhtml:center | xhtml:font" mode="filterNodes">
        <xsl:apply-templates select="node()" mode="filterNodes"/>
    </xsl:template>
    
    <!-- Filter xhtml:head and empty nodes. -->
    <xsl:template match="xhtml:head" mode="filterNodes" priority="3"/>
    
    <xsl:template match="*[not(node())]
        [not(local-name() = 'img' 
        	or local-name() = 'ph' 
        	or local-name() = 'br'
        	or local-name() = 'a'  
        	or local-name() = 'col' 
        	or local-name() = 'td'
        	or local-name() = 'colgroup') 
        or (local-name() = 'a' and not(./@href))]" 
        mode="filterNodes"
        priority="2"/>
    
    <xsl:template match="text()[string-length(normalize-space()) = 0]
        [empty(../preceding-sibling::*)]" 
        mode="filterNodes"/>    
</xsl:stylesheet>
