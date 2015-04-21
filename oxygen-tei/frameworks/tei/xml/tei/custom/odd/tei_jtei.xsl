<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:schold="http://www.ascc.net/xml/schematron"
                xmlns:iso="http://purl.oclc.org/dsdl/schematron"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:tei="http://www.tei-c.org/ns/1.0"
                xmlns:rng="http://relaxng.org/ns/structure/1.0"
                xmlns:sch="http://purl.oclc.org/dsdl/schematron"
                xmlns:eg="http://www.tei-c.org/ns/Examples"
                xmlns:teix="http://www.tei-c.org/ns/Examples"
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
   <!--Strip characters-->
   <xsl:template match="text()" priority="-1"/>

   <!--SCHEMA SETUP-->
   <xsl:template match="/">
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
      <xsl:apply-templates select="/" mode="M39"/>
      <xsl:apply-templates select="/" mode="M40"/>
      <xsl:apply-templates select="/" mode="M41"/>
      <xsl:apply-templates select="/" mode="M42"/>
      <xsl:apply-templates select="/" mode="M43"/>
      <xsl:apply-templates select="/" mode="M44"/>
      <xsl:apply-templates select="/" mode="M45"/>
      <xsl:apply-templates select="/" mode="M46"/>
      <xsl:apply-templates select="/" mode="M47"/>
      <xsl:apply-templates select="/" mode="M48"/>
      <xsl:apply-templates select="/" mode="M49"/>
      <xsl:apply-templates select="/" mode="M50"/>
      <xsl:apply-templates select="/" mode="M51"/>
      <xsl:apply-templates select="/" mode="M52"/>
      <xsl:apply-templates select="/" mode="M53"/>
      <xsl:apply-templates select="/" mode="M54"/>
      <xsl:apply-templates select="/" mode="M55"/>
      <xsl:apply-templates select="/" mode="M56"/>
      <xsl:apply-templates select="/" mode="M57"/>
      <xsl:apply-templates select="/" mode="M58"/>
      <xsl:apply-templates select="/" mode="M59"/>
      <xsl:apply-templates select="/" mode="M60"/>
      <xsl:apply-templates select="/" mode="M61"/>
      <xsl:apply-templates select="/" mode="M62"/>
      <xsl:apply-templates select="/" mode="M63"/>
      <xsl:apply-templates select="/" mode="M64"/>
      <xsl:apply-templates select="/" mode="M65"/>
   </xsl:template>

   <!--SCHEMATRON PATTERNS-->


   <!--PATTERN tei_jtei-att.typed-constraint-subtypeTyped-1-->


	  <!--RULE -->
   <xsl:template match="*[@subtype]" priority="1000" mode="M11">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="@type"/>
         <xsl:otherwise>
            <xsl:message>The <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> element should not be categorized in detail with @subtype
 unless also categorized in general with @type (@type)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M11"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M11"/>
   <xsl:template match="@*|node()" priority="-2" mode="M11">
      <xsl:apply-templates select="*" mode="M11"/>
   </xsl:template>

   <!--PATTERN tei_jtei-quote-constraint-jtei.sch-core-2-->


	  <!--RULE -->
   <xsl:template match="tei:quote" priority="1000" mode="M12">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="id(substring-after(@source, '#'))/(self::tei:ref[@type eq 'bibl']|self::tei:bibl[ancestor::tei:body])"/>
         <xsl:otherwise>
            <xsl:message>
                    <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> must be accompanied by a bibliographic reference (ref[@type="bibl"]) or a bibliographic description in the running text.
                   (id(substring-after(@source, '#'))/(self::tei:ref[@type eq 'bibl']|self::tei:bibl[ancestor::tei:body]))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M12"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M12"/>
   <xsl:template match="@*|node()" priority="-2" mode="M12">
      <xsl:apply-templates select="*" mode="M12"/>
   </xsl:template>

   <!--PATTERN tei_jtei-cit-constraint-jtei.sch-cit-3-->


	  <!--RULE -->
   <xsl:template match="tei:cit" priority="1000" mode="M13">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="tei:ref"/>
         <xsl:otherwise>
            <xsl:message>
                    <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> is normally expected to have a bibliographic reference (ref[@type="bibl"]). Please make sure you intended not to add one here.
                   (tei:ref)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M13"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M13"/>
   <xsl:template match="@*|node()" priority="-2" mode="M13">
      <xsl:apply-templates select="*" mode="M13"/>
   </xsl:template>

   <!--PATTERN tei_jtei-gap-constraint-jtei.sch-gap-4-->


	  <!--RULE -->
   <xsl:template match="tei:gap-period" priority="1000" mode="M14">

		<!--REPORT -->
      <xsl:if test="following-sibling::node()[1][self::text()] and starts-with(following-sibling::node()[1], '.')">
         <xsl:message>
                    A <xsl:text/>
            <xsl:value-of select="name(.)"/>
            <xsl:text/> element should follow a period rather than precede it when an ellipsis follows the end of a sentence.
                   (following-sibling::node()[1][self::text()] and starts-with(following-sibling::node()[1], '.'))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*" mode="M14"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M14"/>
   <xsl:template match="@*|node()" priority="-2" mode="M14">
      <xsl:apply-templates select="*" mode="M14"/>
   </xsl:template>

   <!--PATTERN tei_jtei-gap-constraint-jtei.sch-gap-ws-5-->


	  <!--RULE -->
   <xsl:template match="tei:gap" priority="1000" mode="M15">

		<!--REPORT -->
      <xsl:if test="preceding-sibling::node()[1][self::text()][matches(., '\.\s+$')]">
         <xsl:message>
                    A <xsl:text/>
            <xsl:value-of select="name(.)"/>
            <xsl:text/> should follow a period directly, without preceding whitespace.
                   (preceding-sibling::node()[1][self::text()][matches(., '\.\s+$')])</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*" mode="M15"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M15"/>
   <xsl:template match="@*|node()" priority="-2" mode="M15">
      <xsl:apply-templates select="*" mode="M15"/>
   </xsl:template>

   <!--PATTERN tei_jtei-ptr-constraint-jtei.sch-ptr-multipleTargets-6-->


	  <!--RULE -->
   <xsl:template match="tei:ptr[not(@type='crossref')]" priority="1000" mode="M16">

		<!--REPORT -->
      <xsl:if test="count(tokenize(normalize-space(@target), '\s+')) &gt; 1">
         <xsl:message>
                    Multiple targets are only allowed for <xsl:text/>
            <xsl:value-of select="name(.)"/>
            <xsl:text/>[@type='crossref'].
                   (count(tokenize(normalize-space(@target), '\s+')) &gt; 1)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*" mode="M16"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M16"/>
   <xsl:template match="@*|node()" priority="-2" mode="M16">
      <xsl:apply-templates select="*" mode="M16"/>
   </xsl:template>

   <!--PATTERN tei_jtei-ptr-constraint-ptrAtts-7-->


	  <!--RULE -->
   <xsl:template match="tei:ptr" priority="1000" mode="M17">

		<!--REPORT -->
      <xsl:if test="@target and @cRef">
         <xsl:message>Only one of the
attributes @target and @cRef may be supplied on <xsl:text/>
            <xsl:value-of select="name(.)"/>
            <xsl:text/>. (@target and @cRef)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*" mode="M17"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M17"/>
   <xsl:template match="@*|node()" priority="-2" mode="M17">
      <xsl:apply-templates select="*" mode="M17"/>
   </xsl:template>

   <!--PATTERN tei_jtei-ref-constraint-jtei.sch-ref-multipleTargets-8-->


	  <!--RULE -->
   <xsl:template match="tei:ref" priority="1000" mode="M18">

		<!--REPORT -->
      <xsl:if test="count(tokenize(normalize-space(@target), '\s+')) &gt; 1">
         <xsl:message>
                    <xsl:text/>
            <xsl:value-of select="name(.)"/>
            <xsl:text/> with multiple targets is not supported.
                   (count(tokenize(normalize-space(@target), '\s+')) &gt; 1)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*" mode="M18"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M18"/>
   <xsl:template match="@*|node()" priority="-2" mode="M18">
      <xsl:apply-templates select="*" mode="M18"/>
   </xsl:template>

   <!--PATTERN tei_jtei-ref-constraint-jtei.sch-biblref-parentheses-9-->


	  <!--RULE -->
   <xsl:template match="tei:ref[@type eq 'bibl']" priority="1000" mode="M19">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(matches(., '^\(.*\)$'))"/>
         <xsl:otherwise>
            <xsl:message>
                    Parentheses are not part of bibliographic references. Please move them out of <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>.
                   (not(matches(., '^\(.*\)$')))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M19"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M19"/>
   <xsl:template match="@*|node()" priority="-2" mode="M19">
      <xsl:apply-templates select="*" mode="M19"/>
   </xsl:template>

   <!--PATTERN tei_jtei-ref-constraint-jtei.sch-biblref-target-10-->


	  <!--RULE -->
   <xsl:template match="tei:ref[@type eq 'bibl']" priority="1000" mode="M20">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="id(substring-after(@target, '#'))/(self::tei:bibl|self::tei:person[ancestor::tei:particDesc/parent::tei:profileDesc])"/>
         <xsl:otherwise>
            <xsl:message>
                    A bibliographic reference must point to an entry in the bibliography.
                   (id(substring-after(@target, '#'))/(self::tei:bibl|self::tei:person[ancestor::tei:particDesc/parent::tei:profileDesc]))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M20"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M20"/>
   <xsl:template match="@*|node()" priority="-2" mode="M20">
      <xsl:apply-templates select="*" mode="M20"/>
   </xsl:template>

   <!--PATTERN tei_jtei-ref-constraint-jtei.sch-biblref-type-11-->


	  <!--RULE -->
   <xsl:template match="tei:ref[id(substring-after(@target, '#'))/self::tei:bibl]"
                 priority="1000"
                 mode="M21">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="@type eq 'bibl'"/>
         <xsl:otherwise>
            <xsl:message>
                    A bibliographic reference must be typed as @type="bibl".
                   (@type eq 'bibl')</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M21"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M21"/>
   <xsl:template match="@*|node()" priority="-2" mode="M21">
      <xsl:apply-templates select="*" mode="M21"/>
   </xsl:template>

   <!--PATTERN tei_jtei-ref-constraint-refAtts-12-->


	  <!--RULE -->
   <xsl:template match="tei:ref" priority="1000" mode="M22">

		<!--REPORT -->
      <xsl:if test="@target and @cRef">
         <xsl:message>Only one of the
	attributes @target' and @cRef' may be supplied on <xsl:text/>
            <xsl:value-of select="name(.)"/>
            <xsl:text/>
          (@target and @cRef)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*" mode="M22"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M22"/>
   <xsl:template match="@*|node()" priority="-2" mode="M22">
      <xsl:apply-templates select="*" mode="M22"/>
   </xsl:template>

   <!--PATTERN tei_jtei-list-constraint-gloss-list-must-have-labels-13-->


	  <!--RULE -->
   <xsl:template match="tei:list[@type='gloss']" priority="1000" mode="M23">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="tei:label"/>
         <xsl:otherwise>
            <xsl:message>The content of a "gloss" list should include a sequence of one or more pairs of a label element followed by an item element (tei:label)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M23"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M23"/>
   <xsl:template match="@*|node()" priority="-2" mode="M23">
      <xsl:apply-templates select="*" mode="M23"/>
   </xsl:template>

   <!--PATTERN tei_jtei-head-constraint-jtei.sch-head-number-14-->


	  <!--RULE -->
   <xsl:template match="tei:head" priority="1000" mode="M24">

		<!--REPORT -->
      <xsl:if test="matches(., '^\s*((figure|table|example|section) )?\d', 'i')">
         <xsl:message>
                    Headings are numbered and labeled automatically, please remove the hard-coded label from the text.
                   (matches(., '^\s*((figure|table|example|section) )?\d', 'i'))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*" mode="M24"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M24"/>
   <xsl:template match="@*|node()" priority="-2" mode="M24">
      <xsl:apply-templates select="*" mode="M24"/>
   </xsl:template>

   <!--PATTERN tei_jtei-head-constraint-jtei.sch-figure-head-15-->


	  <!--RULE -->
   <xsl:template match="tei:figure/tei:head" priority="1000" mode="M25">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="@type = ('legend', 'license')"/>
         <xsl:otherwise>
            <xsl:message>
                    Figure titles must have a type 'legend' or 'license'.
                   (@type = ('legend', 'license'))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M25"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M25"/>
   <xsl:template match="@*|node()" priority="-2" mode="M25">
      <xsl:apply-templates select="*" mode="M25"/>
   </xsl:template>

   <!--PATTERN tei_jtei-note-constraint-jtei.sch-note-16-->


	  <!--RULE -->
   <xsl:template match="tei:note" priority="1000" mode="M26">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(following-sibling::text()[1][matches(., '^[,\.:;!?)\]]')])"/>
         <xsl:otherwise>
            <xsl:message>
                    Footnotes should follow punctuation marks, not precede them. Place 
                    your &lt;note&gt; element after the punctuation mark.
                   (not(following-sibling::text()[1][matches(., '^[,\.:;!?)\]]')]))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M26"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M26"/>
   <xsl:template match="@*|node()" priority="-2" mode="M26">
      <xsl:apply-templates select="*" mode="M26"/>
   </xsl:template>

   <!--PATTERN tei_jtei-note-constraint-jtei.sch-note-blocks-17-->


	  <!--RULE -->
   <xsl:template match="tei:note" priority="1000" mode="M27">

		<!--REPORT -->
      <xsl:if test=".//(tei:cit|tei:table|tei:list[not(tokenize(@rend, '\s+')[. eq 'inline'])]|tei:figure|eg:egXML|tei:eg)">
         <xsl:message>
                    No block-level elements are allowed inside note.
                   (.//(tei:cit|tei:table|tei:list[not(tokenize(@rend, '\s+')[. eq 'inline'])]|tei:figure|eg:egXML|tei:eg))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*" mode="M27"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M27"/>
   <xsl:template match="@*|node()" priority="-2" mode="M27">
      <xsl:apply-templates select="*" mode="M27"/>
   </xsl:template>

   <!--PATTERN tei_jtei-graphic-constraint-jtei.sch-graphic-dimensions-18-->


	  <!--RULE -->
   <xsl:template match="tei:graphic" priority="1000" mode="M28">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="matches(@width, '\d+px') and matches(@height, '\d+px')"/>
         <xsl:otherwise>
            <xsl:message>
                    Width and height in pixels must be specified for any <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>.
                   (matches(@width, '\d+px') and matches(@height, '\d+px'))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M28"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M28"/>
   <xsl:template match="@*|node()" priority="-2" mode="M28">
      <xsl:apply-templates select="*" mode="M28"/>
   </xsl:template>

   <!--PATTERN tei_jtei-graphic-constraint-jtei.sch-graphic-context-19-->


	  <!--RULE -->
   <xsl:template match="tei:graphic" priority="1000" mode="M29">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="parent::tei:figure"/>
         <xsl:otherwise>
            <xsl:message>
                    <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> may only occur inside figure.
                   (parent::tei:figure)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M29"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M29"/>
   <xsl:template match="@*|node()" priority="-2" mode="M29">
      <xsl:apply-templates select="*" mode="M29"/>
   </xsl:template>

   <!--PATTERN tei_jtei-author-constraint-jtei.sch-author-20-->


	  <!--RULE -->
   <xsl:template match="tei:titleStmt/tei:author" priority="1000" mode="M30">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="tei:name and tei:affiliation and tei:email"/>
         <xsl:otherwise>
            <xsl:message>
                    Author information in the &lt;titleStmt&gt; must include &lt;name&gt;, &lt;affiliation&gt; and &lt;email&gt;.
                   (tei:name and tei:affiliation and tei:email)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M30"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M30"/>
   <xsl:template match="@*|node()" priority="-2" mode="M30">
      <xsl:apply-templates select="*" mode="M30"/>
   </xsl:template>

   <!--PATTERN tei_jtei-bibl-constraint-jtei.sch-bibl-id-21-->


	  <!--RULE -->
   <xsl:template match="tei:back/tei:div[@type eq 'bibliography']//tei:bibl"
                 priority="1000"
                 mode="M31">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="@xml:id"/>
         <xsl:otherwise>
            <xsl:message>
                    A bibliographic entry should have a unique value for @xml:id.
                   (@xml:id)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M31"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M31"/>
   <xsl:template match="@*|node()" priority="-2" mode="M31">
      <xsl:apply-templates select="*" mode="M31"/>
   </xsl:template>

   <!--PATTERN tei_jtei-bibl-constraint-jtei.sch-bibl-orphan-22-->


	  <!--RULE -->
   <xsl:template match="tei:back/tei:div[@type eq 'bibliography']//tei:bibl"
                 priority="1000"
                 mode="M32">
      <xsl:variable name="currId" select="@xml:id"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="some $i in //tei:ref[@type='bibl'] satisfies tokenize($i/@target, '\s+')[replace(., '#', '') = $currId]"/>
         <xsl:otherwise>
            <xsl:message>
                    This bibliographic entry is an orphan: no ref[@type="bibl"] references to it occur in the text.
                   (some $i in //tei:ref[@type='bibl'] satisfies tokenize($i/@target, '\s+')[replace(., '#', '') = $currId])</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M32"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M32"/>
   <xsl:template match="@*|node()" priority="-2" mode="M32">
      <xsl:apply-templates select="*" mode="M32"/>
   </xsl:template>

   <!--PATTERN tei_jtei-table-constraint-jtei.sch-table-23-->


	  <!--RULE -->
   <xsl:template match="tei:table" priority="1000" mode="M33">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(ancestor::tei:list)"/>
         <xsl:otherwise>
            <xsl:message>
                    No tables are are allowed inside lists.
                   (not(ancestor::tei:list))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M33"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M33"/>
   <xsl:template match="@*|node()" priority="-2" mode="M33">
      <xsl:apply-templates select="*" mode="M33"/>
   </xsl:template>

   <!--PATTERN tei_jtei-idno-constraint-jtei.sch-doi-order-24-->


	  <!--RULE -->
   <xsl:template match="tei:back/tei:div[@type eq 'bibliography']//tei:idno[@type eq 'doi']"
                 priority="1000"
                 mode="M34">

		<!--REPORT -->
      <xsl:if test="following-sibling::tei:ref">
         <xsl:message>
                    If a bibliographic entry has a formal DOI code, it should be placed at the very end of the bibliographic description.
                   (following-sibling::tei:ref)</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*" mode="M34"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M34"/>
   <xsl:template match="@*|node()" priority="-2" mode="M34">
      <xsl:apply-templates select="*" mode="M34"/>
   </xsl:template>

   <!--PATTERN tei_jtei-rendition-constraint-jtei.sch-rendition-25-->


	  <!--RULE -->
   <xsl:template match="tei:rendition" priority="1000" mode="M35">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="some $i in //@rendition satisfies tokenize($i, '\s+')[replace(., '#', '') = current()/@xml:id]"/>
         <xsl:otherwise>
            <xsl:message>
                    Please remove all <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> definitions that aren't actually being used in the article.
                   (some $i in //@rendition satisfies tokenize($i, '\s+')[replace(., '#', '') = current()/@xml:id])</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M35"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M35"/>
   <xsl:template match="@*|node()" priority="-2" mode="M35">
      <xsl:apply-templates select="*" mode="M35"/>
   </xsl:template>

   <!--PATTERN tei_jtei-att-constraint-jtei.sch-att-26-->


	  <!--RULE -->
   <xsl:template match="tei:att" priority="1000" mode="M36">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(matches(., '^@'))"/>
         <xsl:otherwise>
            <xsl:message>
                    Attribute delimiters are not allowed for <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>: they are completed at processing time via XSLT.
                   (not(matches(., '^@')))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M36"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M36"/>
   <xsl:template match="@*|node()" priority="-2" mode="M36">
      <xsl:apply-templates select="*" mode="M36"/>
   </xsl:template>

   <!--PATTERN tei_jtei-tag-constraint-jtei.sch-tag-27-->


	  <!--RULE -->
   <xsl:template match="tei:tag" priority="1000" mode="M37">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(matches(., '^[&lt;!?-]|[&gt;/?\-]$'))"/>
         <xsl:otherwise>
            <xsl:message>
                    Tag delimiters are not allowed for <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>: they are completed at processing time via XSLT.
                   (not(matches(., '^[&lt;!?-]|[&gt;/?\-]$')))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M37"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M37"/>
   <xsl:template match="@*|node()" priority="-2" mode="M37">
      <xsl:apply-templates select="*" mode="M37"/>
   </xsl:template>

   <!--PATTERN tei_jtei-val-constraint-jtei.sch-att-28-->


	  <!--RULE -->
   <xsl:template match="tei:val" priority="1000" mode="M38">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(matches(., concat('^', $quotes, '|', $quotes, '$')))"/>
         <xsl:otherwise>
            <xsl:message>
                    Attribute value delimiters are not allowed for <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>: they are completed at processing time via XSLT.
                   (not(matches(., concat('^', $quotes, '|', $quotes, '$'))))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M38"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M38"/>
   <xsl:template match="@*|node()" priority="-2" mode="M38">
      <xsl:apply-templates select="*" mode="M38"/>
   </xsl:template>

   <!--PATTERN tei_jtei-text-constraint-jtei.sch-article-keywords-31-->


	  <!--RULE -->
   <xsl:template match="tei:text[not(tei:body/tei:div[@type = ('editorialIntroduction')])]"
                 priority="1000"
                 mode="M39">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="parent::tei:TEI/tei:teiHeader/tei:profileDesc/tei:textClass/tei:keywords"/>
         <xsl:otherwise>
            <xsl:message>
                    An article must have a keyword list in the header.
                   (parent::tei:TEI/tei:teiHeader/tei:profileDesc/tei:textClass/tei:keywords)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M39"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M39"/>
   <xsl:template match="@*|node()" priority="-2" mode="M39">
      <xsl:apply-templates select="*" mode="M39"/>
   </xsl:template>

   <!--PATTERN tei_jtei-text-constraint-jtei.sch-article-abstract-32-->


	  <!--RULE -->
   <xsl:template match="tei:text[not(tei:body/tei:div[@type = ('editorialIntroduction')])]"
                 priority="1000"
                 mode="M40">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="tei:front/tei:div[@type='abstract']"/>
         <xsl:otherwise>
            <xsl:message>
                    An article must have a front section with an abstract.
                   (tei:front/tei:div[@type='abstract'])</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M40"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M40"/>
   <xsl:template match="@*|node()" priority="-2" mode="M40">
      <xsl:apply-templates select="*" mode="M40"/>
   </xsl:template>

   <!--PATTERN tei_jtei-text-constraint-jtei.sch-article-back-33-->


	  <!--RULE -->
   <xsl:template match="tei:text[not(tei:body/tei:div[@type = ('editorialIntroduction')])]"
                 priority="1000"
                 mode="M41">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="tei:back/tei:div[@type='bibliography']/tei:listBibl"/>
         <xsl:otherwise>
            <xsl:message>
                    An article must have a back section with a bibliography.
                   (tei:back/tei:div[@type='bibliography']/tei:listBibl)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M41"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M41"/>
   <xsl:template match="@*|node()" priority="-2" mode="M41">
      <xsl:apply-templates select="*" mode="M41"/>
   </xsl:template>

   <!--PATTERN tei_jtei-body-constraint-jtei.sch-body-34-->


	  <!--RULE -->
   <xsl:template match="tei:body[child::tei:div[not(@type=('editorialIntroduction'))]]"
                 priority="1000"
                 mode="M42">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="count(child::tei:div) gt 1"/>
         <xsl:otherwise>
            <xsl:message>
                    If <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> contains a div, and that div is not an editorial introduction, then there should be 
                    more than one div. Rather than using only a single div, you may place the content directly
                    in the <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> element.
                   (count(child::tei:div) gt 1)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M42"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M42"/>
   <xsl:template match="@*|node()" priority="-2" mode="M42">
      <xsl:apply-templates select="*" mode="M42"/>
   </xsl:template>

   <!--PATTERN tei_jtei-div-constraint-jtei.sch-divtypes-front-35-->


	  <!--RULE -->
   <xsl:template match="tei:div[@type = ('abstract', 'acknowledgements')]"
                 priority="1000"
                 mode="M43">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="parent::tei:front"/>
         <xsl:otherwise>
            <xsl:message>
                    Abstracts (<xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>[@type="abstract"]) and acknowledgements (<xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>[@type="acknowledgements"]) may only occur inside front.
                   (parent::tei:front)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M43"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M43"/>
   <xsl:template match="@*|node()" priority="-2" mode="M43">
      <xsl:apply-templates select="*" mode="M43"/>
   </xsl:template>

   <!--PATTERN tei_jtei-div-constraint-jtei.sch-divtypes-front2-36-->


	  <!--RULE -->
   <xsl:template match="tei:front/tei:div" priority="1000" mode="M44">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="@type=('abstract', 'acknowledgements')"/>
         <xsl:otherwise>
            <xsl:message>
                    Only abstracts (div[@type="abstract"]) and acknowledgements (div[@type="acknowledgements"]) may appear in the &lt;front&gt;.
                   (@type=('abstract', 'acknowledgements'))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M44"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M44"/>
   <xsl:template match="@*|node()" priority="-2" mode="M44">
      <xsl:apply-templates select="*" mode="M44"/>
   </xsl:template>

   <!--PATTERN tei_jtei-div-constraint-jtei.sch-divtypes-back-37-->


	  <!--RULE -->
   <xsl:template match="tei:div[@type = ('bibliography', 'appendix')]"
                 priority="1000"
                 mode="M45">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="parent::tei:back"/>
         <xsl:otherwise>
            <xsl:message>
                    Bibliography (<xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>[@type="bibliography"]) and appendices (<xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>[@type="appendix"]) may only occur inside back.
                   (parent::tei:back)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M45"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M45"/>
   <xsl:template match="@*|node()" priority="-2" mode="M45">
      <xsl:apply-templates select="*" mode="M45"/>
   </xsl:template>

   <!--PATTERN tei_jtei-div-constraint-jtei.sch-divtypes-body-38-->


	  <!--RULE -->
   <xsl:template match="tei:div[@type = ('editorialIntroduction')]"
                 priority="1000"
                 mode="M46">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="parent::tei:body"/>
         <xsl:otherwise>
            <xsl:message>
                    An editorial introduction (<xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>[@type="editorialIntroduction"]) may only occur inside body.
                   (parent::tei:body)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M46"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M46"/>
   <xsl:template match="@*|node()" priority="-2" mode="M46">
      <xsl:apply-templates select="*" mode="M46"/>
   </xsl:template>

   <!--PATTERN tei_jtei-div-constraint-jtei.sch-div-head-39-->


	  <!--RULE -->
   <xsl:template match="tei:div[not(@type = ('editorialIntroduction', 'bibliography', 'abstract', 'acknowledgements'))]"
                 priority="1000"
                 mode="M47">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="tei:head"/>
         <xsl:otherwise>
            <xsl:message>
                    A <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> must contain a head.
                   (tei:head)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M47"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M47"/>
   <xsl:template match="@*|node()" priority="-2" mode="M47">
      <xsl:apply-templates select="*" mode="M47"/>
   </xsl:template>

   <!--PATTERN tei_jtei-front-constraint-jtei.sch-front-abstract-40-->


	  <!--RULE -->
   <xsl:template match="tei:front" priority="1000" mode="M48">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="tei:div[@type='abstract']"/>
         <xsl:otherwise>
            <xsl:message>
                    <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> must have an abstract (div[@type='abstract']).
                   (tei:div[@type='abstract'])</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M48"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M48"/>
   <xsl:template match="@*|node()" priority="-2" mode="M48">
      <xsl:apply-templates select="*" mode="M48"/>
   </xsl:template>

   <!--PATTERN tei_jtei-back-constraint-jtei.sch-back-41-->


	  <!--RULE -->
   <xsl:template match="tei:back" priority="1000" mode="M49">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="tei:div[@type='bibliography']/tei:listBibl"/>
         <xsl:otherwise>
            <xsl:message>
                    <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> must have a bibliography (div[@type="bibliography"]), which must be organized inside a listBibl element.
                   (tei:div[@type='bibliography']/tei:listBibl)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M49"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M49"/>
   <xsl:template match="@*|node()" priority="-2" mode="M49">
      <xsl:apply-templates select="*" mode="M49"/>
   </xsl:template>

   <!--PATTERN tei_jtei-supplied-constraint-jtei.sch-supplied-42-->


	  <!--RULE -->
   <xsl:template match="tei:supplied" priority="1000" mode="M50">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(matches(., '^\[|\]$'))"/>
         <xsl:otherwise>
            <xsl:message>
                    Please remove square brackets from <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>: they are completed at processing time via XSLT.
                   (not(matches(., '^\[|\]$')))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M50"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M50"/>
   <xsl:template match="@*|node()" priority="-2" mode="M50">
      <xsl:apply-templates select="*" mode="M50"/>
   </xsl:template>

   <!--PATTERN -->
   <xsl:variable name="double.quotes" select="'[&#34;“”]'"/>
   <xsl:variable name="apos.typographic" select="'[‘’]'"/>
   <xsl:variable name="apos.straight" select="''''"/>
   <xsl:variable name="quotes" select="concat('[', $apos.straight, '&#34;]')"/>
   <xsl:template match="text()" priority="-1" mode="M51"/>
   <xsl:template match="@*|node()" priority="-2" mode="M51">
      <xsl:apply-templates select="*" mode="M51"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-straightApos-45-->


	  <!--RULE -->
   <xsl:template match="text()[not(ancestor::tei:eg|ancestor::eg:egXML|ancestor::tei:code|ancestor::tei:tag)]"
                 priority="1000"
                 mode="M52">

		<!--REPORT -->
      <xsl:if test="matches(., $apos.straight)">
         <xsl:message>
                  "Straight apostrophe" characters are not permitted. Please use the
                  Right Single Quotation Mark (U+2019 or ’) character instead. On the other hand, if the straight 
                  apostrophe characters function as quotation marks, please replace them with appropriate mark-up 
                  that will ensure the appropriate quotation marks will be generated consistently.
                 (matches(., $apos.straight))</xsl:message>
      </xsl:if>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M52"/>
   <xsl:template match="@*|node()" priority="-2" mode="M52">
      <xsl:apply-templates select="*" mode="M52"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-LRquotes-46-->


	  <!--RULE -->
   <xsl:template match="text()[not(ancestor::tei:eg|ancestor::eg:egXML|ancestor::tei:code|ancestor::tei:tag)][matches(., $apos.typographic)]"
                 priority="1000"
                 mode="M53">

		<!--REPORT -->
      <xsl:if test="matches(., '\W[’]\D') or matches(., '[‘](\W|$)') or matches(., '\w[‘]\w')">
         <xsl:message>
                  Left and Right Single Quotation Marks should be used in the right place.
                 (matches(., '\W[’]\D') or matches(., '[‘](\W|$)') or matches(., '\w[‘]\w'))</xsl:message>
      </xsl:if>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M53"/>
   <xsl:template match="@*|node()" priority="-2" mode="M53">
      <xsl:apply-templates select="*" mode="M53"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-quotationMarks-47-->


	  <!--RULE -->
   <xsl:template match="text()[not(ancestor::tei:eg|ancestor::eg:egXML|ancestor::tei:code|ancestor::tei:tag)]"
                 priority="1000"
                 mode="M54">

		<!--REPORT -->
      <xsl:if test="matches(., $double.quotes) or matches(., '(^|\W)[‘][^‘’]+[’](\W|$)')">
         <xsl:message>
                  Quotation marks are not permitted in plain text. Please use appropriate mark-up that will ensure the appropriate quotation marks will be generated consistently.
                 (matches(., $double.quotes) or matches(., '(^|\W)[‘][^‘’]+[’](\W|$)'))</xsl:message>
      </xsl:if>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M54"/>
   <xsl:template match="@*|node()" priority="-2" mode="M54">
      <xsl:apply-templates select="*" mode="M54"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-doubleHyphens-48-->


	  <!--RULE -->
   <xsl:template match="text()[not(ancestor::tei:eg|ancestor::eg:egXML|ancestor::tei:code|ancestor::tei:tag|ancestor::tei:ref)]"
                 priority="1000"
                 mode="M55">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(contains(., '--'))"/>
         <xsl:otherwise>
            <xsl:message>
                  Double hyphens should not be used for dashes. Please use 
                  the EM Dash (U+2014 or —) instead.
                 (not(contains(., '--')))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M55"/>
   <xsl:template match="@*|node()" priority="-2" mode="M55">
      <xsl:apply-templates select="*" mode="M55"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-rangeHyphen-49-->


	  <!--RULE -->
   <xsl:template match="text()[not(ancestor::tei:eg|ancestor::eg:egXML|ancestor::tei:code|ancestor::tei:tag|ancestor::tei:idno)][not(. = parent::*/@*)]"
                 priority="1000"
                 mode="M56">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(matches(., '\d-\d'))"/>
         <xsl:otherwise>
            <xsl:message>
                  Numeric ranges should not be indicated with a hyphen. Please use 
                  the EN Dash (U+2013 or –) character instead.
                 (not(matches(., '\d-\d')))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M56"/>
   <xsl:template match="@*|node()" priority="-2" mode="M56">
      <xsl:apply-templates select="*" mode="M56"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-ieEg-50-->


	  <!--RULE -->
   <xsl:template match="text()[not(ancestor::tei:eg|ancestor::eg:egXML|ancestor::tei:code|ancestor::tei:tag)]"
                 priority="1000"
                 mode="M57">

		<!--REPORT -->
      <xsl:if test="matches(., '(i\.e\.|e\.g\.)[^,]', 'i')">
         <xsl:message>
                  You should put a comma after "i.e." and "e.g.". 
                 (matches(., '(i\.e\.|e\.g\.)[^,]', 'i'))</xsl:message>
      </xsl:if>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M57"/>
   <xsl:template match="@*|node()" priority="-2" mode="M57">
      <xsl:apply-templates select="*" mode="M57"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-localLinkTarget-51-->


	  <!--RULE -->
   <xsl:template match="@*[not(ancestor::eg:egXML)][name() = ('corresp', 'target', 'from', 'to', 'ref', 'rendition', 'resp', 'source')][some $i in tokenize(., '\s+') satisfies starts-with($i, '#')]"
                 priority="1000"
                 mode="M58">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="every $i in tokenize(., '\s+')[starts-with(., '#')] satisfies id( substring-after($i, '#'))"/>
         <xsl:otherwise>
            <xsl:message>
                  There's no local target for this link. Please make sure you use an existing @xml:id value.
                 (every $i in tokenize(., '\s+')[starts-with(., '#')] satisfies id( substring-after($i, '#')))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M58"/>
   <xsl:template match="@*|node()" priority="-2" mode="M58">
      <xsl:apply-templates select="*" mode="M58"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-quoteDelim-52-->


	  <!--RULE -->
   <xsl:template match="tei:title[@level eq 'a']|tei:mentioned|tei:soCalled|tei:quote|tei:q"
                 priority="1000"
                 mode="M59">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(matches(., concat('^', $double.quotes, '|', $double.quotes, '$')))"/>
         <xsl:otherwise>
            <xsl:message>
                  Quotation mark delimiters are not allowed for <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>: they are completed at processing time via XSLT.
                 (not(matches(., concat('^', $double.quotes, '|', $double.quotes, '$'))))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M59"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M59"/>
   <xsl:template match="@*|node()" priority="-2" mode="M59">
      <xsl:apply-templates select="*" mode="M59"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-crossref-id-53-->


	  <!--RULE -->
   <xsl:template match="tei:body//tei:div[not(@type='editorialIntroduction')]|tei:figure|tei:table"
                 priority="1000"
                 mode="M60">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="@xml:id"/>
         <xsl:otherwise>
            <xsl:message>
                  You're strongly advised to add an @xml:id attribute to <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> to ease formal cross-referencing 
                  with (ptr|ref)[@type='crossref']
                 (@xml:id)</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M60"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M60"/>
   <xsl:template match="@*|node()" priority="-2" mode="M60">
      <xsl:apply-templates select="*" mode="M60"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-formalCrossref-54-->


	  <!--RULE -->
   <xsl:template match="text()[not(ancestor::tei:eg|ancestor::eg:egXML|ancestor::tei:code|ancestor::tei:tag|ancestor::tei:ref[not(@type='crossref')])]"
                 priority="1000"
                 mode="M61">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(matches(., '(table|figure|example|section) \d+([.,]\d+)* ((above)|(below))', 'i'))"/>
         <xsl:otherwise>
            <xsl:message>
                  Please replace literal references to tables, figures, examples, and sections with a formal crosslink:
                  (ptr|ref)[@type="crossref"]
                 (not(matches(., '(table|figure|example|section) \d+([.,]\d+)* ((above)|(below))', 'i')))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M61"/>
   <xsl:template match="@*|node()" priority="-2" mode="M61">
      <xsl:apply-templates select="*" mode="M61"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-crossrefTargetType-55-->


	  <!--RULE -->
   <xsl:template match="tei:ptr[@type='crossref']|tei:ref[@type='crossref']"
                 priority="1000"
                 mode="M62">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="id(substring-after(@target, '#'))/(self::tei:div|self::tei:figure|self::tei:table)"/>
         <xsl:otherwise>
            <xsl:message>
                  Cross-links (<xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/>[@type="crossref"]) should be targeted at div, figure, or table elements.
                 (id(substring-after(@target, '#'))/(self::tei:div|self::tei:figure|self::tei:table))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*" mode="M62"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M62"/>
   <xsl:template match="@*|node()" priority="-2" mode="M62">
      <xsl:apply-templates select="*" mode="M62"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-crossrefType-56-->


	  <!--RULE -->
   <xsl:template match="tei:ptr[not(@type='crossref')]|tei:ref[not(@type='crossref')]"
                 priority="1000"
                 mode="M63">

		<!--REPORT -->
      <xsl:if test="id(substring-after(@target, '#'))/(self::tei:div|self::tei:figure|self::tei:table)">
         <xsl:message>
                  Please type internal cross-references as 'crossref' (<xsl:text/>
            <xsl:value-of select="name(.)"/>
            <xsl:text/>[@type="crossref"]).
                 (id(substring-after(@target, '#'))/(self::tei:div|self::tei:figure|self::tei:table))</xsl:message>
      </xsl:if>
      <xsl:apply-templates select="*" mode="M63"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M63"/>
   <xsl:template match="@*|node()" priority="-2" mode="M63">
      <xsl:apply-templates select="*" mode="M63"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-centuries-57-->


	  <!--RULE -->
   <xsl:template match="text()[not(ancestor::tei:quote or ancestor::tei:title)]"
                 priority="1000"
                 mode="M64">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(matches(., '\d\d?((th)|(st)|(rd)|(nd))[- ]centur((y)|(ies))', 'i'))"/>
         <xsl:otherwise>
            <xsl:message>
                  Centuries such as "the nineteenth century" should be spelled out, not written with digits.
                 (not(matches(., '\d\d?((th)|(st)|(rd)|(nd))[- ]centur((y)|(ies))', 'i')))</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M64"/>
   <xsl:template match="@*|node()" priority="-2" mode="M64">
      <xsl:apply-templates select="*" mode="M64"/>
   </xsl:template>

   <!--PATTERN tei_jtei-constraint-jtei.sch-teiVersion-58-->


	  <!--RULE -->
   <xsl:template match="@target[contains(., 'http://www.tei-c.org/release/doc/tei-p5-doc')]"
                 priority="1000"
                 mode="M65">

		<!--ASSERT -->
      <xsl:choose>
         <xsl:when test="false()"/>
         <xsl:otherwise>
            <xsl:message>
                  Please refer to the exact version of the TEI Guidelines, and link to the version that can be found in the Vault section. For an overview of all archived versions, see http://www.tei-c.org/Vault/P5/.
                  
                  If you're referring to the English version, the correct URL will likely take the form of http://www.tei-c.org/Vault/P5/{$version-number}/doc/tei-p5-doc/en/html/.
                 (false())</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M65"/>
   <xsl:template match="@*|node()" priority="-2" mode="M65">
      <xsl:apply-templates select="*" mode="M65"/>
   </xsl:template>
</xsl:stylesheet>
