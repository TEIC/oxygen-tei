<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:schold="http://www.ascc.net/xml/schematron"
                xmlns:iso="http://purl.oclc.org/dsdl/schematron"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:tei="http://www.tei-c.org/ns/1.0"
                xmlns:rng="http://relaxng.org/ns/structure/1.0"
                xmlns:s="http://www.ascc.net/xml/schematron"
                xmlns:sch="http://purl.oclc.org/dsdl/schematron"
                version="2.0"><!--Implementers: please note that overriding process-prolog or process-root is 
    the preferred method for meta-stylesheets to use where possible. -->
<xsl:param name="archiveDirParameter"/>
   <xsl:param name="archiveNameParameter"/>
   <xsl:param name="fileNameParameter"/>
   <xsl:param name="fileDirParameter"/>
   <xsl:variable name="document-uri">
      <xsl:value-of select="document-uri(/)"/>
   </xsl:variable>

   <!--PHASES-->


<!--PROLOG-->
<xsl:output method="text"/>

   <!--XSD TYPES FOR XSLT2-->


<!--KEYS AND FUNCTIONS-->


<!--DEFAULT RULES-->


<!--MODE: SCHEMATRON-SELECT-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<xsl:template match="*" mode="schematron-select-full-path">
      <xsl:apply-templates select="." mode="schematron-get-full-path"/>
   </xsl:template>

   <!--MODE: SCHEMATRON-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<xsl:template match="*" mode="schematron-get-full-path">
      <xsl:apply-templates select="parent::*" mode="schematron-get-full-path"/>
      <xsl:text>/</xsl:text>
      <xsl:choose>
         <xsl:when test="namespace-uri()=''">
            <xsl:value-of select="name()"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:text>*:</xsl:text>
            <xsl:value-of select="local-name()"/>
            <xsl:text>[namespace-uri()='</xsl:text>
            <xsl:value-of select="namespace-uri()"/>
            <xsl:text>']</xsl:text>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:variable name="preceding"
                    select="count(preceding-sibling::*[local-name()=local-name(current())                                   and namespace-uri() = namespace-uri(current())])"/>
      <xsl:text>[</xsl:text>
      <xsl:value-of select="1+ $preceding"/>
      <xsl:text>]</xsl:text>
   </xsl:template>
   <xsl:template match="@*" mode="schematron-get-full-path">
      <xsl:apply-templates select="parent::*" mode="schematron-get-full-path"/>
      <xsl:text>/</xsl:text>
      <xsl:choose>
         <xsl:when test="namespace-uri()=''">@<xsl:value-of select="name()"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:text>@*[local-name()='</xsl:text>
            <xsl:value-of select="local-name()"/>
            <xsl:text>' and namespace-uri()='</xsl:text>
            <xsl:value-of select="namespace-uri()"/>
            <xsl:text>']</xsl:text>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <!--MODE: SCHEMATRON-FULL-PATH-2-->
<!--This mode can be used to generate prefixed XPath for humans-->
<xsl:template match="node() | @*" mode="schematron-get-full-path-2">
      <xsl:for-each select="ancestor-or-self::*">
         <xsl:text>/</xsl:text>
         <xsl:value-of select="name(.)"/>
         <xsl:if test="preceding-sibling::*[name(.)=name(current())]">
            <xsl:text>[</xsl:text>
            <xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1"/>
            <xsl:text>]</xsl:text>
         </xsl:if>
      </xsl:for-each>
      <xsl:if test="not(self::*)">
         <xsl:text/>/@<xsl:value-of select="name(.)"/>
      </xsl:if>
   </xsl:template>
   <!--MODE: SCHEMATRON-FULL-PATH-3-->
<!--This mode can be used to generate prefixed XPath for humans 
	(Top-level element has index)-->
<xsl:template match="node() | @*" mode="schematron-get-full-path-3">
      <xsl:for-each select="ancestor-or-self::*">
         <xsl:text>/</xsl:text>
         <xsl:value-of select="name(.)"/>
         <xsl:if test="parent::*">
            <xsl:text>[</xsl:text>
            <xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1"/>
            <xsl:text>]</xsl:text>
         </xsl:if>
      </xsl:for-each>
      <xsl:if test="not(self::*)">
         <xsl:text/>/@<xsl:value-of select="name(.)"/>
      </xsl:if>
   </xsl:template>

   <!--MODE: GENERATE-ID-FROM-PATH -->
<xsl:template match="/" mode="generate-id-from-path"/>
   <xsl:template match="text()" mode="generate-id-from-path">
      <xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
      <xsl:value-of select="concat('.text-', 1+count(preceding-sibling::text()), '-')"/>
   </xsl:template>
   <xsl:template match="comment()" mode="generate-id-from-path">
      <xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
      <xsl:value-of select="concat('.comment-', 1+count(preceding-sibling::comment()), '-')"/>
   </xsl:template>
   <xsl:template match="processing-instruction()" mode="generate-id-from-path">
      <xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
      <xsl:value-of select="concat('.processing-instruction-', 1+count(preceding-sibling::processing-instruction()), '-')"/>
   </xsl:template>
   <xsl:template match="@*" mode="generate-id-from-path">
      <xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
      <xsl:value-of select="concat('.@', name())"/>
   </xsl:template>
   <xsl:template match="*" mode="generate-id-from-path" priority="-0.5">
      <xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
      <xsl:text>.</xsl:text>
      <xsl:value-of select="concat('.',name(),'-',1+count(preceding-sibling::*[name()=name(current())]),'-')"/>
   </xsl:template>

   <!--MODE: GENERATE-ID-2 -->
<xsl:template match="/" mode="generate-id-2">U</xsl:template>
   <xsl:template match="*" mode="generate-id-2" priority="2">
      <xsl:text>U</xsl:text>
      <xsl:number level="multiple" count="*"/>
   </xsl:template>
   <xsl:template match="node()" mode="generate-id-2">
      <xsl:text>U.</xsl:text>
      <xsl:number level="multiple" count="*"/>
      <xsl:text>n</xsl:text>
      <xsl:number count="node()"/>
   </xsl:template>
   <xsl:template match="@*" mode="generate-id-2">
      <xsl:text>U.</xsl:text>
      <xsl:number level="multiple" count="*"/>
      <xsl:text>_</xsl:text>
      <xsl:value-of select="string-length(local-name(.))"/>
      <xsl:text>_</xsl:text>
      <xsl:value-of select="translate(name(),':','.')"/>
   </xsl:template>
   <!--Strip characters--><xsl:template match="text()" priority="-1"/>

   <!--SCHEMA SETUP-->
<xsl:template match="/">
      <xsl:apply-templates select="/" mode="M5"/>
      <xsl:apply-templates select="/" mode="M6"/>
      <xsl:apply-templates select="/" mode="M7"/>
      <xsl:apply-templates select="/" mode="M8"/>
      <xsl:apply-templates select="/" mode="M9"/>
      <xsl:apply-templates select="/" mode="M10"/>
      <xsl:apply-templates select="/" mode="M11"/>
      <xsl:apply-templates select="/" mode="M12"/>
      <xsl:apply-templates select="/" mode="M13"/>
      <xsl:apply-templates select="/" mode="M14"/>
      <xsl:apply-templates select="/" mode="M15"/>
      <xsl:apply-templates select="/" mode="M16"/>
      <xsl:apply-templates select="/" mode="M17"/>
      <xsl:apply-templates select="/" mode="M18"/>
      <xsl:apply-templates select="/" mode="M19"/>
      <xsl:apply-templates select="/" mode="M20"/>
      <xsl:apply-templates select="/" mode="M21"/>
      <xsl:apply-templates select="/" mode="M22"/>
      <xsl:apply-templates select="/" mode="M23"/>
      <xsl:apply-templates select="/" mode="M24"/>
      <xsl:apply-templates select="/" mode="M25"/>
      <xsl:apply-templates select="/" mode="M26"/>
      <xsl:apply-templates select="/" mode="M27"/>
      <xsl:apply-templates select="/" mode="M28"/>
      <xsl:apply-templates select="/" mode="M29"/>
      <xsl:apply-templates select="/" mode="M30"/>
      <xsl:apply-templates select="/" mode="M31"/>
      <xsl:apply-templates select="/" mode="M32"/>
      <xsl:apply-templates select="/" mode="M33"/>
      <xsl:apply-templates select="/" mode="M34"/>
      <xsl:apply-templates select="/" mode="M35"/>
      <xsl:apply-templates select="/" mode="M36"/>
      <xsl:apply-templates select="/" mode="M37"/>
      <xsl:apply-templates select="/" mode="M38"/>
   </xsl:template>

   <!--SCHEMATRON PATTERNS-->


<!--PATTERN att.pointing-constraint-targetLang-->


	<!--RULE -->
<xsl:template match="tei:*[@targetLang]" priority="1000" mode="M5">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="count(@target)"/>
         <xsl:otherwise>
            <xsl:message>@targetLang can only be used if @target is specified. (count(@target))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M5"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M5"/>
   <xsl:template match="@*|node()" priority="-2" mode="M5">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M5"/>
   </xsl:template>

   <!--PATTERN ptr-constraint-ptrAtts-->


	<!--RULE -->
<xsl:template match="tei:ptr" priority="1000" mode="M6">

		<!--REPORT -->
<xsl:if test="@target and @cRef">
         <xsl:message>Only one of the
	attributes 'target' and 'cRef' may be supplied. (@target and @cRef)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M6"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M6"/>
   <xsl:template match="@*|node()" priority="-2" mode="M6">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M6"/>
   </xsl:template>

   <!--PATTERN ref-constraint-refAtts-->


	<!--RULE -->
<xsl:template match="tei:ref" priority="1000" mode="M7">

		<!--REPORT -->
<xsl:if test="@target and @cRef">
         <xsl:message>Only one of the
	attributes 'target' and 'cRef' may be supplied. (@target and @cRef)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M7"/>
   <xsl:template match="@*|node()" priority="-2" mode="M7">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

   <!--PATTERN relatedItem-constraint-targetorcontent1-->


	<!--RULE -->
<xsl:template match="tei:relatedItem" priority="1000" mode="M8">

		<!--REPORT -->
<xsl:if test="@target and count( child::* ) &gt; 0">
         <xsl:message>If the 'target' attribute is used, the
        relatedItem element must be empty (@target and count( child::* ) &gt; 0)</xsl:message>
      </xsl:if>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="@target or child::*"/>
         <xsl:otherwise>
            <xsl:message>A relatedItem element should have either a 'target' attribute
        or a child element to indicate the related bibliographic item (@target or child::*)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M8"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M8"/>
   <xsl:template match="@*|node()" priority="-2" mode="M8">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M8"/>
   </xsl:template>

   <!--PATTERN lg-constraint-atleast1oflggapl-->


	<!--RULE -->
<xsl:template match="tei:lg" priority="1000" mode="M9">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="count(descendant::tei:lg|descendant::tei:l|descendant::tei:gap) &gt; 0"/>
         <xsl:otherwise>
            <xsl:message>An &lt;lg&gt; 
        must contain at least one child &lt;l&gt;, &lt;lg&gt; or &lt;gap&gt;. (count(descendant::tei:lg|descendant::tei:l|descendant::tei:gap) &gt; 0)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M9"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M9"/>
   <xsl:template match="@*|node()" priority="-2" mode="M9">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M9"/>
   </xsl:template>

   <!--PATTERN s-constraint-noNestedS-->


	<!--RULE -->
<xsl:template match="tei:s" priority="1000" mode="M10">

		<!--REPORT -->
<xsl:if test="tei:s">
         <xsl:message>You may not nest one s element within
      another: use seg instead (tei:s)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M10"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M10"/>
   <xsl:template match="@*|node()" priority="-2" mode="M10">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M10"/>
   </xsl:template>

   <!--PATTERN f-constraint-fValConstraints-->


	<!--RULE -->
<xsl:template match="tei:fVal" priority="1001" mode="M11">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="not(tei:* and text)"/>
         <xsl:otherwise>
            <xsl:message> A feature value cannot
    contain both text and element content (not(tei:* and text))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M11"/>
   </xsl:template>

	  <!--RULE -->
<xsl:template match="tei:fVal" priority="1000" mode="M11">

		<!--REPORT -->
<xsl:if test="count(tei:*)&gt;1">
         <xsl:message> A feature value can contain
    only one child element (count(tei:*)&gt;1)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M11"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M11"/>
   <xsl:template match="@*|node()" priority="-2" mode="M11">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M11"/>
   </xsl:template>

   <!--PATTERN link-constraint-linkTargets1-->


	<!--RULE -->
<xsl:template match="tei:link" priority="1000" mode="M12">

		<!--REPORT -->
<xsl:if test="@target and @targets">
         <xsl:message>You may not supply both 
	@target and @targets (@target and @targets)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M12"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M12"/>
   <xsl:template match="@*|node()" priority="-2" mode="M12">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M12"/>
   </xsl:template>

   <!--PATTERN link-constraint-linkTargets2-->


	<!--RULE -->
<xsl:template match="tei:link" priority="1000" mode="M13">

		<!--REPORT -->
<xsl:if test="not(@target) and not(@targets)">
         <xsl:message>You must
	supply either @target or @targets (not(@target) and not(@targets))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M13"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M13"/>
   <xsl:template match="@*|node()" priority="-2" mode="M13">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M13"/>
   </xsl:template>

   <!--PATTERN link-constraint-linkTargets3-->


	<!--RULE -->
<xsl:template match="tei:link" priority="1000" mode="M14">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="contains(@target,' ')"/>
         <xsl:otherwise>
            <xsl:message>You must supply at least two
values for @target (contains(@target,' '))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M14"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M14"/>
   <xsl:template match="@*|node()" priority="-2" mode="M14">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M14"/>
   </xsl:template>

   <!--PATTERN join-constraint-joinTargets1-->


	<!--RULE -->
<xsl:template match="tei:join" priority="1000" mode="M15">

		<!--REPORT -->
<xsl:if test="@target and @targets">
         <xsl:message>You may not supply both 
	@target and @targets (@target and @targets)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M15"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M15"/>
   <xsl:template match="@*|node()" priority="-2" mode="M15">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M15"/>
   </xsl:template>

   <!--PATTERN join-constraint-joinTargets2-->


	<!--RULE -->
<xsl:template match="tei:join" priority="1000" mode="M16">

		<!--REPORT -->
<xsl:if test="not(@target) and not(@targets)">
         <xsl:message>You must
	supply either @target or @targets (not(@target) and not(@targets))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M16"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M16"/>
   <xsl:template match="@*|node()" priority="-2" mode="M16">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M16"/>
   </xsl:template>

   <!--PATTERN join-constraint-joinTargets3-->


	<!--RULE -->
<xsl:template match="tei:join" priority="1000" mode="M17">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="contains(@target,' ')"/>
         <xsl:otherwise>
            <xsl:message>You must supply at least two
values for @target (contains(@target,' '))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M17"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M17"/>
   <xsl:template match="@*|node()" priority="-2" mode="M17">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M17"/>
   </xsl:template>

   <!--PATTERN alt-constraint-altTargets1-->


	<!--RULE -->
<xsl:template match="tei:alt" priority="1000" mode="M18">

		<!--REPORT -->
<xsl:if test="@target and @targets">
         <xsl:message>You may not supply both 
	@target and @targets (@target and @targets)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M18"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M18"/>
   <xsl:template match="@*|node()" priority="-2" mode="M18">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M18"/>
   </xsl:template>

   <!--PATTERN alt-constraint-altTargets2-->


	<!--RULE -->
<xsl:template match="tei:alt" priority="1000" mode="M19">

		<!--REPORT -->
<xsl:if test="not(@target) and not(@targets)">
         <xsl:message>You must
	supply either @target or @targets (not(@target) and not(@targets))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M19"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M19"/>
   <xsl:template match="@*|node()" priority="-2" mode="M19">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M19"/>
   </xsl:template>

   <!--PATTERN alt-constraint-altTargets3-->


	<!--RULE -->
<xsl:template match="tei:alt" priority="1000" mode="M20">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="contains(@target,' ')"/>
         <xsl:otherwise>
            <xsl:message>You must supply at least two
values for @target (contains(@target,' '))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M20"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M20"/>
   <xsl:template match="@*|node()" priority="-2" mode="M20">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M20"/>
   </xsl:template>

   <!--PATTERN dimensions-constraint-duplicateDim-->


	<!--RULE -->
<xsl:template match="tei:dimensions" priority="1000" mode="M21">

		<!--REPORT -->
<xsl:if test="count(tei:width)&gt; 1">
         <xsl:message>
	Width element may appear once only
       (count(tei:width)&gt; 1)</xsl:message>
      </xsl:if>

		    <!--REPORT -->
<xsl:if test="count(tei:height)&gt; 1">
         <xsl:message>
	Height element may appear once only
       (count(tei:height)&gt; 1)</xsl:message>
      </xsl:if>

		    <!--REPORT -->
<xsl:if test="count(tei:depth)&gt; 1">
         <xsl:message>
	Depth element may appear once only
       (count(tei:depth)&gt; 1)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M21"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M21"/>
   <xsl:template match="@*|node()" priority="-2" mode="M21">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M21"/>
   </xsl:template>

   <!--PATTERN msIdentifier-constraint-msId_minimal-->


	<!--RULE -->
<xsl:template match="tei:msIdentifier" priority="1000" mode="M22">

		<!--REPORT -->
<xsl:if test="local-name(*[1])='idno' or                              local-name(*[1])='altIdentifier' or        .='' ">
         <xsl:message>
	    You must supply either a locator of some type or a
	    name (local-name(*[1])='idno' or local-name(*[1])='altIdentifier' or .='')</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M22"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M22"/>
   <xsl:template match="@*|node()" priority="-2" mode="M22">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M22"/>
   </xsl:template>

   <!--PATTERN relation-constraint-activemutual-->


	<!--RULE -->
<xsl:template match="tei:relation" priority="1000" mode="M23">

		<!--REPORT -->
<xsl:if test="@active and @mutual">
         <xsl:message>Only one of the attributes
	'active' and 'mutual' may be supplied (@active and @mutual)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M23"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M23"/>
   <xsl:template match="@*|node()" priority="-2" mode="M23">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M23"/>
   </xsl:template>

   <!--PATTERN relation-constraint-activepassive-->


	<!--RULE -->
<xsl:template match="tei:relation" priority="1000" mode="M24">

		<!--REPORT -->
<xsl:if test="@passive and not(@active)">
         <xsl:message>the attribute 'passive'
	may be supplied only if the attribute 'active' is
	supplied (@passive and not(@active))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M24"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M24"/>
   <xsl:template match="@*|node()" priority="-2" mode="M24">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M24"/>
   </xsl:template>

   <!--PATTERN app-constraint-only1lem-->


	<!--RULE -->
<xsl:template match="tei:app" priority="1000" mode="M25">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="count( descendant::tei:lem[ generate-id(       current() ) = generate-id( ancestor::tei:app[1] ) ]) &lt;       2"/>
         <xsl:otherwise>
            <xsl:message>Only one &lt;lem&gt; element may appear within a single
      apparatus entry, whether it appears outside a &lt;rdgGrp&gt;
      element or within it. (count( descendant::tei:lem[ generate-id( current() ) = generate-id( ancestor::tei:app[1] ) ]) &lt; 2)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M25"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M25"/>
   <xsl:template match="@*|node()" priority="-2" mode="M25">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M25"/>
   </xsl:template>

   <!--PATTERN app-constraint-atleast1rdg-->


	<!--RULE -->
<xsl:template match="tei:app" priority="1000" mode="M26">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="count(descendant::tei:rdg[generate-id(current() ) = generate-id(ancestor::tei:app[1])]) &gt; 0"/>
         <xsl:otherwise>
            <xsl:message>An &lt;app&gt; 
        must contain at least one &lt;rdg&gt; element. (count(descendant::tei:rdg[generate-id(current() ) = generate-id(ancestor::tei:app[1])]) &gt; 0)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M26"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M26"/>
   <xsl:template match="@*|node()" priority="-2" mode="M26">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M26"/>
   </xsl:template>

   <!--PATTERN addSpan-constraint-spanTo-->


	<!--RULE -->
<xsl:template match="tei:addSpan" priority="1000" mode="M27">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="@spanTo"/>
         <xsl:otherwise>
            <xsl:message>The spanTo= attribute of <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> is required. (@spanTo)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M27"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M27"/>
   <xsl:template match="@*|node()" priority="-2" mode="M27">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M27"/>
   </xsl:template>

   <!--PATTERN damageSpan-constraint-spanTo-->


	<!--RULE -->
<xsl:template match="tei:damageSpan" priority="1000" mode="M28">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="@spanTo"/>
         <xsl:otherwise>
            <xsl:message>The spanTo= attribute of
	<xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> is required. (@spanTo)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M28"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M28"/>
   <xsl:template match="@*|node()" priority="-2" mode="M28">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M28"/>
   </xsl:template>

   <!--PATTERN delSpan-constraint-spanTo-->


	<!--RULE -->
<xsl:template match="tei:delSpan" priority="1000" mode="M29">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="@spanTo"/>
         <xsl:otherwise>
            <xsl:message>The spanTo= attribute of <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> is required. (@spanTo)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M29"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M29"/>
   <xsl:template match="@*|node()" priority="-2" mode="M29">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M29"/>
   </xsl:template>

   <!--PATTERN subst-constraint-substContents1-->


	<!--RULE -->
<xsl:template match="tei:subst" priority="1000" mode="M30">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="child::add and child::del"/>
         <xsl:otherwise>
            <xsl:message>
        Subst must have at least one child add and at least one child del (child::add and child::del)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M30"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M30"/>
   <xsl:template match="@*|node()" priority="-2" mode="M30">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M30"/>
   </xsl:template>

   <!--PATTERN moduleRef-constraint-modref-->


	<!--RULE -->
<xsl:template match="tei:moduleRef" priority="1000" mode="M31">

		<!--REPORT -->
<xsl:if test="* and @key">
         <xsl:message>
          child elements of moduleRef are only allowed when an external module
          is being loaded
         (* and @key)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M31"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M31"/>
   <xsl:template match="@*|node()" priority="-2" mode="M31">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M31"/>
   </xsl:template>

   <!--PATTERN moduleRef-constraint-not-same-prefix-->


	<!--RULE -->
<xsl:template match="tei:moduleRef" priority="1000" mode="M32">

		<!--REPORT -->
<xsl:if test="//*[ not( generate-id(.) eq generate-id(      current() ) ) ]/@prefix = @prefix">
         <xsl:message>The prefix attribute
	    of a moduleRef element should not match that of any other
	    element (it would defeat the purpose) (//*[ not( generate-id(.) eq generate-id( current() ) ) ]/@prefix = @prefix)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M32"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M32"/>
   <xsl:template match="@*|node()" priority="-2" mode="M32">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M32"/>
   </xsl:template>

   <!--PATTERN elementSpec-constraint-elementspec-in-module-->


	<!--RULE -->
<xsl:template match="tei:elementSpec" priority="1000" mode="M33">

		<!--REPORT -->
<xsl:if test="not(//tei:moduleSpec[@ident=current()/@module])">
         <xsl:message>
Macro <xsl:text/>
            <xsl:value-of select="@ident"/>
            <xsl:text/>: the value of the module [<xsl:text/>
            <xsl:value-of select="@module"/>
            <xsl:text/>] attribute must correspond to an existing moduleSpec (not(//tei:moduleSpec[@ident=current()/@module]))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M33"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M33"/>
   <xsl:template match="@*|node()" priority="-2" mode="M33">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M33"/>
   </xsl:template>

   <!--PATTERN classSpec-constraint-classspec-in-module-->


	<!--RULE -->
<xsl:template match="tei:classSpec" priority="1000" mode="M34">

		<!--REPORT -->
<xsl:if test="not(//tei:moduleSpec[@ident=current()/@module])">
         <xsl:message>
Class <xsl:text/>
            <xsl:value-of select="@ident"/>
            <xsl:text/>: the value of the module attribute [<xsl:text/>
            <xsl:value-of select="@module"/>
            <xsl:text/>]  must correspond to an existing moduleSpec (not(//tei:moduleSpec[@ident=current()/@module]))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M34"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M34"/>
   <xsl:template match="@*|node()" priority="-2" mode="M34">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M34"/>
   </xsl:template>

   <!--PATTERN macroSpec-constraint-macrospec-in-module-->


	<!--RULE -->
<xsl:template match="tei:macroSpec" priority="1000" mode="M35">

		<!--REPORT -->
<xsl:if test="not(//tei:moduleSpec[@ident=current()/@module])">
         <xsl:message>
Macro <xsl:text/>
            <xsl:value-of select="@ident"/>
            <xsl:text/>: the value of the module attribute [<xsl:text/>
            <xsl:value-of select="@module"/>
            <xsl:text/>]  must correspond to an existing moduleSpec (not(//tei:moduleSpec[@ident=current()/@module]))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M35"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M35"/>
   <xsl:template match="@*|node()" priority="-2" mode="M35">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M35"/>
   </xsl:template>

   <!--PATTERN constraintSpec-constraint-sch-->


	<!--RULE -->
<xsl:template match="tei:constraintSpec" priority="1000" mode="M36">

		<!--REPORT -->
<xsl:if test="tei:constraint/s:* and    not(@scheme='schematron')">
         <xsl:message>
	Rules in the Schematron 1.* language must be inside
	a constraint with a value of 'schematron' on the scheme attribute
       (tei:constraint/s:* and not(@scheme='schematron'))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M36"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M36"/>
   <xsl:template match="@*|node()" priority="-2" mode="M36">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M36"/>
   </xsl:template>

   <!--PATTERN constraintSpec-constraint-isosch-->


	<!--RULE -->
<xsl:template match="tei:constraintSpec" priority="1000" mode="M37">

		<!--REPORT -->
<xsl:if test="tei:constraint/sch:* and    not(@scheme='isoschematron')">
         <xsl:message>
	Rules in the ISO Schematron language must be inside
	a constraint with a value of 'isoschematron' on the scheme attribute
       (tei:constraint/sch:* and not(@scheme='isoschematron'))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M37"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M37"/>
   <xsl:template match="@*|node()" priority="-2" mode="M37">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M37"/>
   </xsl:template>

   <!--PATTERN attDef-constraint-attDefContents-->


	<!--RULE -->
<xsl:template match="tei:attDef" priority="1000" mode="M38">

		<!--ASSERT -->
<xsl:choose>
         <xsl:when test="tei:datatype or tei:valList[@type='closed']"/>
         <xsl:otherwise>
            <xsl:message>
        Attribute [@<xsl:text/>
               <xsl:value-of select="@ident"/>
               <xsl:text/>] from [<xsl:text/>
               <xsl:value-of select="ancestor::*[@ident]/@ident"/>
               <xsl:text/>] must have a closed valList or a datatype (tei:datatype or tei:valList[@type='closed'])</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M38"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M38"/>
   <xsl:template match="@*|node()" priority="-2" mode="M38">
      <xsl:apply-templates select="*|comment()|processing-instruction()" mode="M38"/>
   </xsl:template>
</xsl:stylesheet>