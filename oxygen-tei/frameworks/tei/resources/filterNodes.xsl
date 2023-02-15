<!-- 
  Copyright 2001-2012 Syncro Soft SRL. All rights reserved.
 -->
<xsl:stylesheet version="3.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:f="http://www.oxygenxml.com/xsl/functions"
    exclude-result-prefixes="f">
    
    <!-- true if we want to filter all div elements from document
         false if we want to keep div elements with id or class attributes -->
    <xsl:param name="filterDivElements" as="xs:boolean" select="true()"/>
    
    <xsl:variable name="elementsMayHaveIdsOnSibling" select="('img')"/>
    
    <xsl:template match="*" mode="filterNodes">
        <xsl:copy>
            <xsl:if test="local-name() != 'body'">
               <!-- EXM-45627: Check if the first child is an empty anchor and copy the id from it-->
               <xsl:variable name="childAnchor" select="child::xhtml:*[1][local-name() = 'a'][@id or @name][not(node())]"/>
               <xsl:if test="not(@id) and $childAnchor">
                   <xsl:variable name="followingAnchorSibling" select="$childAnchor/following-sibling::*[not(xhtml:a)][1]"/>
                   <xsl:if test="not($followingAnchorSibling) or $followingAnchorSibling[@id or @name] or not(local-name($followingAnchorSibling) = $elementsMayHaveIdsOnSibling)">
                       <xsl:variable name="id" select="f:extractIdFromAnchor($childAnchor)"/>
                       <xsl:if test="$id and string-length($id) > 0">
                           <xsl:attribute name="id" select="$id"/>
                       </xsl:if>
                   </xsl:if>
               </xsl:if>
            </xsl:if>
            <xsl:apply-templates select="node() | @*" mode="filterNodes"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*[local-name() = 'shape' and namespace-uri() = 'urn:schemas-microsoft-com:vml']" mode="filterNodes">
        <!--EXM-51538: Filter the shape element from Word and extract the image source -->
        <xsl:if test="./*[local-name() = 'imagedata'][@src]">
            <xsl:element name="xhtml:img">
                <xsl:copy-of select="./*[local-name() = 'imagedata'][1]/@src"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="*[local-name() = $elementsMayHaveIdsOnSibling][not(@id)][preceding-sibling::*[1][local-name() = 'a'][@id or @name][not(node())]]" mode="filterNodes">
        <xsl:copy>
            <!-- EXM-51091: Copy the id from the preceding empty anchor sibling -->
            <xsl:variable name="id" select="f:extractIdFromAnchor(./preceding-sibling::*[1][local-name() = 'a'][@id or @name][not(node())] )"/>
            <xsl:if test="$id and string-length($id) > 0">
                <xsl:attribute name="id" select="$id"/>
            </xsl:if>
            
            <xsl:apply-templates select="node() | @*" mode="filterNodes"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:a[@id or @name][not(node())][(count(preceding-sibling::*) = 0 and not(parent::*[1][local-name() = 'body']) and parent::*[1][not(@id)])
        or (following-sibling::*[not(xhtml:a)][1][not(@id) and local-name() = $elementsMayHaveIdsOnSibling])]" mode="filterNodes">
        <!-- The id from this anchor is used on the parent or on the following element into the above templates. We should ignore it because it was already used. --> 
    </xsl:template>
    
    <xsl:template match="processing-instruction() |comment() | @*" mode="filterNodes">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="filterNodes"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*/@id" mode="filterNodes">
        <xsl:attribute name="id">
            <xsl:value-of select="f:correctId(.)"/>
        </xsl:attribute>
    </xsl:template>
    
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
    
    <!-- Ignore comments from Word documents -->
    <xsl:template match="xhtml:div[contains(@class, 'msocomtxt')]" mode="filterNodes" priority="2.0">
        <!-- Ignore comment texts-->
    </xsl:template>
    <xsl:template match="xhtml:div[@style = 'mso-element:comment']" mode="filterNodes" priority="2.0">
        <!-- Ignore comment item -->
    </xsl:template>
    <xsl:template match="xhtml:div[@style = 'mso-element:comment-list']" mode="filterNodes" priority="2.0">
        <!-- Ignore comments list -->
    </xsl:template>
    <xsl:template match="xhtml:span[contains(@class, 'MsoCommentReference')]" mode="filterNodes" priority="2.0">
        <!-- Ignore comment reference-->
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
    
    <xsl:template match="xhtml:div[@id or @class]" mode="filterNodes">
        <xsl:choose>
            <xsl:when test="$filterDivElements">
                <!-- Unwrap xhtml:div nodes and keep only the child nodes. -->
                <xsl:apply-templates select="node()" mode="filterNodes"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*" mode="filterNodes"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Unwrap nodes and keep only the child nodes. -->
    <xsl:template match="xhtml:div | xhtml:center | xhtml:font" mode="filterNodes">
        <xsl:apply-templates select="node()" mode="filterNodes"/>
    </xsl:template>
    
    <xsl:template match="xhtml:div[@id = 'oxy_prolog'
        or @id = 'oxy_prolog_author' or @id = 'oxy_prolog_created']" mode="filterNodes">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="filterNodes"/>
        </xsl:copy>
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
            or local-name() = 'audio'
            or local-name() = 'video'
            or local-name() = 'source'
            or local-name() = 'picture'
            or local-name() = 'iframe'
            or local-name() = 'object'
            or local-name() = 'param'
            or local-name() = 'colgroup'
            or (local-name() = 'div' and not($filterDivElements and (@id or @class)))
            or (local-name() = 'p' and ./@id) ) 
        ]" 
        mode="filterNodes"
        priority="2"/>
    
    <xsl:template match="text()[string-length(normalize-space()) = 0]
        [empty(../preceding-sibling::*)]" 
        mode="filterNodes"/>    
</xsl:stylesheet>
