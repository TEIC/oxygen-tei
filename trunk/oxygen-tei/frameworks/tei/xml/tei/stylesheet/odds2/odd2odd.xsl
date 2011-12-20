<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:sch="http://purl.oclc.org/dsdl/schematron" 
    xmlns:s="http://www.ascc.net/xml/schematron" 
    xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"
    xmlns:rng="http://relaxng.org/ns/structure/1.0"
    xmlns:tei="http://www.tei-c.org/ns/1.0" 
    xmlns:teix="http://www.tei-c.org/ns/Examples" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    version="2.0" 
    exclude-result-prefixes="teix a s tei xs rng sch xsi">
  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
    <desc>
      <p> TEI stylesheet for simplifying TEI ODD markup </p>
      <p>This software is dual-licensed:

1. Distributed under a Creative Commons Attribution-ShareAlike 3.0
Unported License http://creativecommons.org/licenses/by-sa/3.0/ 

2. http://www.opensource.org/licenses/BSD-2-Clause
		
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

* Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not
limited to, the implied warranties of merchantability and fitness for
a particular purpose are disclaimed. In no event shall the copyright
holder or contributors be liable for any direct, indirect, incidental,
special, exemplary, or consequential damages (including, but not
limited to, procurement of substitute goods or services; loss of use,
data, or profits; or business interruption) however caused and on any
theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use
of this software, even if advised of the possibility of such damage.
</p>
      <p>Author: See AUTHORS</p>
      <p>Id: $Id: odd2odd.xsl 9903 2011-12-06 10:02:40Z rahtz $</p>
      <p>Copyright: 2011, TEI Consortium</p>
    </desc>
  </doc>
  <xsl:output encoding="utf-8" indent="no"/>
  <xsl:param name="autoGlobal">false</xsl:param>
  <xsl:param name="selectedSchema"/>
  <xsl:param name="verbose"/>
  <xsl:param name="useVersionFromTEI">true</xsl:param>
  <xsl:param name="stripped">false</xsl:param>
  <xsl:param name="configDirectory"/>
  <xsl:param name="currentDirectory"/>
  <xsl:param name="defaultSource"></xsl:param>
  <xsl:param name="defaultTEIVersion">current</xsl:param>
  <xsl:param name="defaultTEIServer">http://www.tei-c.org/Vault/P5/</xsl:param>
  <xsl:key name="odd2odd-CLASSREFS" match="tei:classRef" use="@key"/>
  <xsl:key name="odd2odd-ATTCLASSES" match="tei:classSpec[(tei:attList or @type='atts') and not(@ident='tei.TEIform')]" use="@ident"/>
  <xsl:key name="odd2odd-CHANGEATT" match="tei:attDef[@mode='change']" use="concat(../../@ident,'_',@ident)"/>
  <xsl:key name="odd2odd-CHANGECONSTRAINT" match="tei:constraintSpec[@mode='change']" use="concat(../@ident,'_',@ident)"/>
  <xsl:key name="odd2odd-CLASS_MEMBERED" use="tei:classes/tei:memberOf/@key" match="tei:classSpec"/>
  <xsl:key name="odd2odd-ATTREFS" match="tei:attRef" use="concat(@name,'_',../../@ident)"/>
  <xsl:key name="odd2odd-DELETEATT" match="tei:attDef[@mode='delete']" use="concat(ancestor::tei:elementSpec/@ident,'_',@ident)"/>
  <xsl:key name="odd2odd-DELETEATT" match="tei:attDef[@mode='delete']" use="concat(ancestor::tei:classSpec/@ident,'_',@ident)"/>
  <xsl:key name="odd2odd-DELETECONSTRAINT" match="tei:constraintSpec[@mode='delete']" use="concat(../@ident,'_',@ident)"/>

  <xsl:key name="odd2odd-ELEMENT_MEMBERED" use="tei:classes/tei:memberOf/@key" match="tei:elementSpec"/>
  <xsl:key name="odd2odd-IDS" match="tei:*[@xml:id]" use="@xml:id"/>
  <xsl:key name="odd2odd-MACROS" use="@ident" match="tei:macroSpec"/>
  <xsl:key name="odd2odd-MEMBEROFADD" match="tei:memberOf[not(@mode='delete')]" use="concat(../../@ident,@key)"/>
  <xsl:key name="odd2odd-MEMBEROFDELETE" match="tei:memberOf[@mode='delete']" use="concat(../../@ident,@key)"/>
  <xsl:key name="odd2odd-MODULES" match="tei:moduleRef" use="@key"/>
  <xsl:key name="odd2odd-NEWATTCLASSES" match="tei:classSpec[@type='atts' and @mode='add']" use="@ident"/>
  <xsl:key name="odd2odd-REFED" use="@name" match="rng:ref[parent::tei:datatype]"/>
  <xsl:key name="odd2odd-REFED" use="@name" match="rng:ref[ancestor::tei:elementSpec]"/>
  <xsl:key name="odd2odd-REFED" use="@name" match="rng:ref[ancestor::tei:macroSpec and not(@name=ancestor::tei:macroSpec/@ident)]"/>
  <xsl:key name="odd2odd-REFED" use="substring-before(@name,'.attribute')" match="tei:attRef"/>
  <xsl:key name="odd2odd-REFED" use="substring-before(@name,'_')" match="rng:ref[contains(@name,'_')]"/>
  <xsl:key name="odd2odd-REPLACEATT" match="tei:attDef[@mode='replace']" use="concat(../../@ident,'_',@ident)"/>
  <xsl:key name="odd2odd-REPLACECONSTRAINT" match="tei:constraintSpec[@mode='replace']" use="concat(../@ident,'_',@ident)"/>

  <xsl:key name="odd2odd-MODULE_MEMBERS" match="tei:macroSpec"  use="@module"/>
  <xsl:key name="odd2odd-MODULE_MEMBERS" match="tei:classSpec"  use="@module"/>
  <xsl:key name="odd2odd-MODULE_MEMBERS" match="tei:elementSpec" use="@module"/>

  <xsl:key name="odd2odd-MODULE_MEMBERS_MODEL" match="tei:classSpec" use="@module"/>

  <xsl:key name="odd2odd-SCHEMASPECS" match="tei:schemaSpec" use="@ident"/>

  <xsl:key name="odd2odd-IDENTS" match="tei:macroSpec" use="@ident"/>
  <xsl:key name="odd2odd-IDENTS" match="tei:classSpec" use="@ident"/>
  <xsl:key name="odd2odd-IDENTS" match="tei:elementSpec" use="@ident"/>

   <!-- all of these use a combination of @ident _and_ @ns (where
   present), in case of duplication of names across schemes -->

  <xsl:key name="odd2odd-CHANGE" match="tei:classSpec[@mode='change']" use="concat(@ns,@ident)"/>
  <xsl:key name="odd2odd-CHANGE" match="tei:elementSpec[@mode='change']" use="concat(@ns,@ident)"/>
  <xsl:key name="odd2odd-CHANGE" match="tei:macroSpec[@mode='change']" use="concat(@ns,@ident)"/>
  <xsl:key name="odd2odd-DELETE" match="tei:classSpec[@mode='delete']" use="concat(@ns,@ident)"/>
  <xsl:key name="odd2odd-DELETE" match="tei:elementSpec[@mode='delete']" use="concat(@ns,@ident)"/>
  <xsl:key name="odd2odd-DELETE" match="tei:macroSpec[@mode='delete']" use="concat(@ns,@ident)"/>
  <xsl:key name="odd2odd-REPLACE" match="tei:classSpec[@mode='replace']" use="concat(@ns,@ident)"/>
  <xsl:key name="odd2odd-REPLACE" match="tei:elementSpec[@mode='replace']" use="concat(@ns,@ident)"/>
  <xsl:key name="odd2odd-REPLACE" match="tei:macroSpec[@mode='replace']" use="concat(@ns,@ident)"/>

  <xsl:variable name="DEFAULTSOURCE">
    <xsl:choose>
      <xsl:when test="$defaultSource != ''">
        <xsl:value-of select="$defaultSource"/>
      </xsl:when>
      <xsl:when test="$configDirectory != ''">
        <xsl:value-of select="$configDirectory"/>
        <xsl:text>odd/p5subset.xml</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$defaultTEIServer"/>
        <xsl:value-of select="$defaultTEIVersion"/>
	<xsl:text>/xml/tei/odd/p5subset.xml</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <xsl:variable name="ODD">
    <xsl:for-each select="/tei:TEI">
      <xsl:copy>
	<xsl:attribute name="xml:base" select="document-uri(/)"/>
        <xsl:copy-of select="@*"/>
        <xsl:if test="$useVersionFromTEI='true'">
          <xsl:processing-instruction name="TEIVERSION">
            <xsl:call-template name="odd2odd-getversion"/>
          </xsl:processing-instruction>
        </xsl:if>
        <xsl:apply-templates mode="odd2odd-pass0"/>
      </xsl:copy>
    </xsl:for-each>
  </xsl:variable>

  <xsl:variable name="top" select="/"/>


  <xsl:template match="/">
    <xsl:if test="$autoGlobal='true'">
      <xsl:message>NOTE: all TEI elements will have global attributes added automatically</xsl:message>
    </xsl:if>
    <!--
    <xsl:result-document href="/tmp/foo.xml">
      <xsl:copy-of select="$ODD"/>
    </xsl:result-document>
    -->
    <xsl:for-each select="$ODD">
      <xsl:apply-templates mode="odd2odd-pass1"/>
    </xsl:for-each>
  </xsl:template>

  <!-- ******************* Pass 0, follow and expand specGrp ********************************* -->
  <xsl:template match="tei:specGrp" mode="odd2odd-pass0">
    <xsl:if test="$verbose='true'">
      <xsl:message>Phase 0: summarize specGrp <xsl:value-of select="@xml:id"/>
      </xsl:message>
    </xsl:if>
    <table xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:for-each select="*">
        <row>
          <xsl:choose>
            <xsl:when test="self::tei:specGrpRef">
              <cell>
                <ref target="#{@target}">reference <xsl:value-of select="@target"/></ref>
              </cell>
              <cell/>
            </xsl:when>
            <xsl:when test="self::tei:elementSpec">
              <cell>
		Element <gi><xsl:value-of select="@ident"/></gi>
	      </cell>
              <cell>
                <xsl:value-of select="@mode"/>
              </cell>
            </xsl:when>
            <xsl:when test="self::tei:classSpec">
              <cell>
		Class <ident type="class"><xsl:value-of select="@ident"/></ident>
	      </cell>
              <cell>
                <xsl:value-of select="@mode"/>
              </cell>
            </xsl:when>
            <xsl:when test="self::tei:macroSpec">
              <cell>
		Macro <ident type="macro"><xsl:value-of select="@ident"/></ident>
	      </cell>
              <cell>
                <xsl:value-of select="@mode"/>
              </cell>
            </xsl:when>
            <xsl:when test="self::tei:moduleRef">
              <cell>
		Module <xsl:value-of select="@key"/>
	      </cell>
              <cell/>
	    </xsl:when>
          </xsl:choose>
        </row>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template match="tei:schemaSpec" mode="odd2odd-pass0">
    <xsl:if test="@ident=$selectedSchema or ($selectedSchema='' and not(preceding-sibling::tei:schemaSpec))">
      <xsl:copy>
	<xsl:copy-of select="@*"/>
	<xsl:choose>
	<xsl:when test="@source">
	<xsl:if test="$verbose='true'">
	  <xsl:message>Source for TEI is <xsl:value-of select="@source"/></xsl:message>
	</xsl:if>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:if test="$verbose='true'">
	    <xsl:message>Source for TEI will be set to <xsl:value-of select="$DEFAULTSOURCE"/> </xsl:message>
	  </xsl:if>
	  <xsl:attribute name="source">
	    <xsl:value-of select="$DEFAULTSOURCE"/>
	  </xsl:attribute>
	</xsl:otherwise>
	</xsl:choose>
	<xsl:apply-templates select="*|text()|processing-instruction()" mode="odd2odd-pass0"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>

  <xsl:template match="tei:specGrpRef" mode="odd2odd-pass0">
    <xsl:if test="$verbose='true'">
      <xsl:message>Phase 0: expand specGrpRef <xsl:value-of
      select="@target"/> </xsl:message>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="starts-with(@target,'#')">
	<xsl:apply-templates  mode="odd2odd-pass0"
			      select="id(substring(@target,2))/*"/>
      </xsl:when>
      <xsl:otherwise>
    <xsl:if test="$verbose='true'">
      <xsl:message> ... read from <xsl:value-of select="resolve-uri(@target,base-uri(/tei:TEI))"/></xsl:message>
    </xsl:if>

	<xsl:for-each 
	    select="doc(resolve-uri(@target,base-uri(/tei:TEI)))">
	  <xsl:choose>
	    <xsl:when test="tei:specGrp">
	      <xsl:apply-templates select="tei:specGrp/*" mode="odd2odd-pass0"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:apply-templates  mode="odd2odd-pass0"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="text()|@*" mode="odd2odd-pass0">
      <xsl:copy-of select="."/>
  </xsl:template>
  
  <xsl:template match="processing-instruction()" mode="odd2odd-pass0">
      <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="*" mode="odd2odd-pass0">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="*|text()|processing-instruction()" mode="odd2odd-pass0"/>
    </xsl:copy>
  </xsl:template>


  <!-- ******************* Pass 1, follow schemaSpec ********************************* -->
  <xsl:template match="@*|processing-instruction()|text()" mode="odd2odd-pass1">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="*" mode="odd2odd-pass1">
    <xsl:copy>
      <xsl:apply-templates mode="odd2odd-pass1" select="*|@*|processing-instruction()|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="tei:schemaSpec" mode="odd2odd-pass1">
    <xsl:variable name="sourceDoc" select="tei:workOutSource(.)"/>

    <xsl:variable name="oddsource">
      <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:if test="$verbose='true'">
          <xsl:message>Schema <xsl:value-of select="@ident"/></xsl:message>
        </xsl:if>
        <!-- 
	     it is important to process "tei" and "core" first 
	     because of the order of declarations
	-->
	<xsl:for-each select="tei:moduleRef[@key='tei']">
	  <xsl:call-template name="odd2odd-expandModule">
	    <xsl:with-param name="Source" select="$sourceDoc" tunnel="yes"/>
	  </xsl:call-template>
	</xsl:for-each>
	
	<xsl:for-each select="tei:moduleRef[@key='core']">
	  <xsl:call-template name="odd2odd-expandModule">
	    <xsl:with-param name="Source" select="$sourceDoc"
			tunnel="yes"/>
	  </xsl:call-template>
	</xsl:for-each>
	
	<xsl:for-each select="tei:moduleRef[@key]">
	  <xsl:if test="not(@key='tei' or @key='core')">
	    <xsl:call-template name="odd2odd-expandModule">
	    <xsl:with-param name="Source" select="$sourceDoc"
			tunnel="yes"/>
	  </xsl:call-template>
	  </xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="tei:macroRef|tei:classRef|tei:elementRef">
	    <xsl:call-template name="odd2odd-followRef">
	  </xsl:call-template>
	</xsl:for-each>

	<xsl:for-each select="tei:classSpec[@mode='add' or not(@mode)]">
	  <xsl:call-template name="odd2odd-createCopy">
	    <xsl:with-param name="Source" select="$sourceDoc"
			tunnel="yes"/>
	  </xsl:call-template>
	</xsl:for-each>

	<xsl:for-each select="tei:macroSpec[@mode='add' or not(@mode)]">
	  <xsl:call-template name="odd2odd-createCopy">
	    <xsl:with-param name="Source" select="$sourceDoc"
			tunnel="yes"/>
	  </xsl:call-template>
	</xsl:for-each>

	<xsl:for-each select="tei:elementSpec[@mode='add' or not(@mode)]">
	  <xsl:call-template name="odd2odd-createCopy">
	    <xsl:with-param name="Source" select="$sourceDoc"
			tunnel="yes"/>
	  </xsl:call-template>
	</xsl:for-each>

	<xsl:apply-templates mode="odd2odd-justcopy" select="tei:moduleRef[@url]"/>

      </xsl:copy>

    </xsl:variable>
    <xsl:for-each select="$oddsource">
      <xsl:apply-templates mode="odd2odd-pass2"/>
    </xsl:for-each>
    <!-- constraints -->
    <xsl:apply-templates mode="odd2odd-copy" select="tei:constraintSpec"/>
  </xsl:template>


  <xsl:template name="odd2odd-expandModule">
    <xsl:variable name="sourceDoc" select="tei:workOutSource(.)"/>
    <xsl:variable name="name" select="@key"/>
    <xsl:variable name="exc"
		  select="concat(' ',normalize-space(@except),' ')"/>
    <xsl:variable name="inc"  
		  select="concat('/',translate(normalize-space(@include),' ','/'),'/')"/>
      <xsl:choose>
	<xsl:when test="not(@except) and not(@include)">
	  <xsl:for-each select="document($sourceDoc,$top)">
	    <xsl:for-each select="key('odd2odd-MODULE_MEMBERS',$name)">
	      <xsl:call-template name="odd2odd-checkObject">
		<xsl:with-param name="Source" select="$sourceDoc" tunnel="yes"/>
		<xsl:with-param name="why"> module <xsl:value-of select="$name"/></xsl:with-param>
	      </xsl:call-template>
	    </xsl:for-each>
	  </xsl:for-each>
	</xsl:when>
	<xsl:when test="@include">
	  <xsl:if test="$verbose='true'">
	    <xsl:message>Process module reference to [<xsl:value-of
	    select="@key"/>] with inclusion of [<xsl:value-of
	    select="@include"/>]</xsl:message>
	  </xsl:if>
	  <!-- get model and attribute classes regardless -->
	  <xsl:for-each select="document($sourceDoc,$top)">
	    <xsl:for-each
		select="key('odd2odd-MODULE_MEMBERS_MODEL',$name)">
	      <xsl:call-template name="odd2odd-checkObject">
		<xsl:with-param name="Source" select="$sourceDoc" tunnel="yes"/>
		<xsl:with-param name="why">module (auto from include) <xsl:value-of select="$name"/></xsl:with-param>
	      </xsl:call-template>
	    </xsl:for-each>
	    <xsl:for-each
		select="key('odd2odd-MODULE_MEMBERS',$name)">
	      <xsl:choose>
		<xsl:when test="self::tei:classSpec"/>
		<xsl:when test="contains($inc,concat('/',@ident,'/'))">
		  <xsl:call-template name="odd2odd-checkObject">
		    <xsl:with-param name="Source" select="$sourceDoc" tunnel="yes"/>
		    <xsl:with-param name="why">(inclusion) module <xsl:value-of select="$name"/></xsl:with-param>
		  </xsl:call-template>
		</xsl:when>
	      </xsl:choose>
	    </xsl:for-each>
	  </xsl:for-each>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:if test="$verbose='true'">
	    <xsl:message>Process module reference to [<xsl:value-of
	    select="@key"/>] with exclusion of [<xsl:value-of select="@except"/>]</xsl:message>
	  </xsl:if>
	  <xsl:for-each select="document($sourceDoc,$top)">
	    <xsl:for-each select="key('odd2odd-MODULE_MEMBERS',$name)">
	      <xsl:if test="not(contains($exc,concat(' ',@ident,' ')))">
		<xsl:call-template name="odd2odd-checkObject">
		  <xsl:with-param name="Source" select="$sourceDoc" tunnel="yes"/>
		  <xsl:with-param name="why">(exclusion) module <xsl:value-of select="$name"/></xsl:with-param>
		</xsl:call-template>
	      </xsl:if>
	    </xsl:for-each>
	    </xsl:for-each>
	</xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <xsl:template name="odd2odd-followRef">
    <xsl:variable name="sourceDoc" select="tei:workOutSource(.)"/>
    <xsl:variable name="name" select="@key"/>
    <xsl:for-each select="document($sourceDoc,$top)">
      <xsl:choose>
	<xsl:when test="key('odd2odd-IDENTS',$name)">
	  <xsl:for-each select="key('odd2odd-IDENTS',$name)">
	    <xsl:call-template name="odd2odd-checkObject">
		<xsl:with-param name="Source" select="$sourceDoc" tunnel="yes"/>
		<xsl:with-param name="why">direct reference</xsl:with-param>
	    </xsl:call-template>
	  </xsl:for-each>
	</xsl:when>
	<xsl:otherwise>
	<xsl:call-template name="die">
	  <xsl:with-param name="message">
	    <xsl:text>Reference to </xsl:text>
	    <xsl:value-of select="$name"/>
	    <xsl:text>: not found in source</xsl:text>
	  </xsl:with-param>
	</xsl:call-template>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

<!-- pass2 -->      
  <xsl:template match="rng:ref" mode="odd2odd-pass2">
    <xsl:variable name="N">
      <xsl:value-of select="@name"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="starts-with($N,'macro.') and $stripped='true'">
        <xsl:for-each select="key('odd2odd-MACROS',$N)/tei:content/*">
          <xsl:call-template name="odd2odd-simplifyRelax"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
	<ref xmlns="http://relaxng.org/ns/structure/1.0">
	  <xsl:apply-templates select="@*|*|text()|processing-instruction()" mode="odd2odd-pass2"/>
	</ref>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="tei:valDesc|tei:equiv|tei:gloss|tei:desc|tei:remarks|tei:exemplum|tei:listRef" mode="odd2odd-pass2">
    <xsl:choose>
      <xsl:when test="$stripped='true'"> </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
	  <xsl:apply-templates select="@*|*|text()|processing-instruction()" mode="odd2odd-pass2"/>
	</xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="tei:ptr" mode="odd2odd-pass2">
    <xsl:choose>
      <xsl:when test="starts-with(@target,'#') and 
		      (ancestor::tei:remarks or ancestor::tei:listRef or ancestor::tei:valDesc) and
		      not(id(substring(@target,2)))">
	<xsl:variable name="target" select="substring(@target,2)"/>
	<xsl:variable name="sourceDoc" select="tei:workOutSource(.)"/>

	<xsl:choose>
	  <xsl:when test="document($sourceDoc)/id($target)">
	    <ref  xmlns="http://www.tei-c.org/ns/1.0"
		  target="http://www.tei-c.org/release/doc/tei-p5-doc/en/html/{substring($target,1,2)}.html#{$target}">
	      <xsl:for-each select="document($DEFAULTSOURCE)/id($target)">
		<xsl:number count="tei:div" format="1.1.1."
			    level="multiple"/>	  
		<xsl:text> </xsl:text>
		<xsl:value-of select="tei:head"/>
	      </xsl:for-each>
	    </ref>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:copy>
	      <xsl:apply-templates select="@*|*|text()|processing-instruction()" mode="odd2odd-pass2"/>
	    </xsl:copy>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy>
	  <xsl:apply-templates select="@*|*|text()|processing-instruction()" mode="odd2odd-pass2"/>
	</xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@*|text()|comment()" mode="odd2odd-pass2">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="*" mode="odd2odd-pass2">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates mode="odd2odd-pass2"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="tei:content" mode="odd2odd-pass2">
    <xsl:variable name="content">
      <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates mode="odd2odd-pass2"/>
      </xsl:copy>
    </xsl:variable>
    <xsl:apply-templates select="$content" mode="odd2odd-pass3"/>
  </xsl:template>


  <xsl:template match="tei:classSpec" mode="odd2odd-pass2">
    <xsl:variable name="used">
      <xsl:call-template name="odd2odd-amINeeded"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$used=''">
        <xsl:if test="$verbose='true'">
          <xsl:message>Reject unused class <xsl:value-of select="@ident"/>
               </xsl:message>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:copy-of select="@*"/>
          <xsl:apply-templates mode="odd2odd-pass2"/>
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="odd2odd-amINeeded">
    <!--
How can a class be ok?
  a) if an element is a member of it
  b) if its referred to in a content model
  c) if some other class is a member of it, and that class is OK
-->
    <xsl:variable name="k" select="@ident"/>
    <xsl:choose>
      <xsl:when test="$autoGlobal='true' and starts-with(@ident,'att.global')">y</xsl:when>
      <xsl:when test="self::tei:classSpec and $stripped='true'">y</xsl:when>
      <xsl:when test="key('odd2odd-ELEMENT_MEMBERED',$k)">y</xsl:when>
      <xsl:when test="key('odd2odd-REFED',$k)">y</xsl:when>
      <xsl:when test="key('odd2odd-CLASS_MEMBERED',$k)">
        <xsl:for-each select="key('odd2odd-CLASS_MEMBERED',$k)">
          <xsl:call-template name="odd2odd-amINeeded"/>
        </xsl:for-each>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="tei:macroSpec" mode="odd2odd-pass2">
    <xsl:variable name="k">
      <xsl:value-of select="@prefix"/>
      <xsl:value-of select="@ident"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$stripped='true' and starts-with($k,'macro.')"/>
      <xsl:when test="key('odd2odd-REFED',$k)">
	<macroSpec xmlns="http://www.tei-c.org/ns/1.0" >
          <xsl:copy-of select="@*"/>
          <xsl:apply-templates mode="odd2odd-pass2"/>
	</macroSpec>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="$verbose='true'">
          <xsl:message>Reject unused macro <xsl:value-of select="$k"/></xsl:message>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="odd2odd-checkObject">
    <xsl:param name="why"/>
    <xsl:param name="Source" tunnel="yes"/>
    <!--
        for every object
         - if its in DELETE list, ignore
         - if its in REPLACE list, use that
         - if its in CHANGE list
           (do the hard merge bit)
         - otherwise copy 
        done
  -->
      <xsl:variable name="Current" select="."/>
      <xsl:variable name="specName">
	<xsl:choose>
	  <xsl:when test="@ns">
	    <xsl:value-of select="@ns"/>
	  </xsl:when>
	  <xsl:when test="ancestor::tei:schemaSpec/@ns">
	    <xsl:value-of select="ancestor::tei:schemaSpec/@ns"/>
	  </xsl:when>
	</xsl:choose>
	<xsl:value-of select="@ident"/>
      </xsl:variable>
      <xsl:variable name="N" select="local-name(.)"/>
      <xsl:for-each select="$ODD">
        <xsl:choose>
          <xsl:when test="key('odd2odd-DELETE',$specName)">
            <xsl:if test="$verbose='true'">
              <xsl:message>Phase 1: remove <xsl:value-of select="$specName"/></xsl:message>
            </xsl:if>
          </xsl:when>
	  <xsl:when test="key('odd2odd-REPLACE',$specName)">
            <xsl:if test="$verbose='true'">
              <xsl:message>Phase 1: replace <xsl:value-of select="$specName"/></xsl:message>
            </xsl:if>
            <xsl:apply-templates mode="odd2odd-copy" select="key('odd2odd-REPLACE',$specName)"/>
          </xsl:when>
          <xsl:when test="key('odd2odd-CHANGE',$specName)">
            <xsl:if test="$verbose='true'">
              <xsl:message>Phase 1: change <xsl:value-of select="$specName"/></xsl:message>
            </xsl:if>
            <xsl:apply-templates mode="odd2odd-change" select="$Current"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:if test="$verbose='true'">
	      <xsl:message>Phase 1: include <xsl:value-of
	      select="$specName"/> from <xsl:value-of
	      select="$Source"/> (<xsl:value-of select="$why"/>)</xsl:message>
            </xsl:if>
            <xsl:apply-templates mode="odd2odd-copy" select="$Current"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
  </xsl:template>

  <xsl:template match="@*|processing-instruction()|text()" mode="odd2odd-change">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="*" mode="odd2odd-change">
    <xsl:copy>
      <xsl:apply-templates mode="odd2odd-change" select="*|@*|processing-instruction()|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="@*|processing-instruction()|text()" mode="odd2odd-copy">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="tei:memberOf" mode="odd2odd-copy">
    <xsl:variable name="k" select="@key"/>
    <xsl:choose>
      <xsl:when test="$ODD/key('odd2odd-DELETE',$k)"/>
      <xsl:otherwise>
	<memberOf xmlns="http://www.tei-c.org/ns/1.0" key="{$k}">
	  <xsl:copy-of select="@min|@max"/>
	</memberOf>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="*" mode="odd2odd-copy">
    <xsl:copy>
      <xsl:apply-templates mode="odd2odd-copy" select="*|@*|processing-instruction()|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="tei:elementSpec/@mode" mode="odd2odd-change">
    <xsl:copy/>
  </xsl:template>

  <xsl:template match="tei:listRef" mode="odd2odd-copy"/>

  <xsl:template match="tei:elementSpec" mode="odd2odd-copy">
    <xsl:copy>
      <xsl:choose>
	<xsl:when test="@module"/>
	<xsl:when test="ancestor::tei:schemaSpec/@module">
	  <xsl:copy-of select="ancestor::tei:schemaSpec/@module"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:attribute name="module">
	    <xsl:text>derived-module-</xsl:text>
	    <xsl:value-of select="ancestor::tei:schemaSpec/@ident"/>
	  </xsl:attribute>
	</xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="odd2odd-copyElementSpec">
	<xsl:with-param name="n" select="'1'"/>
      </xsl:call-template>
    </xsl:copy>
  </xsl:template>

  <xsl:template name="odd2odd-copyElementSpec">
    <xsl:param name="n"/>
    <xsl:param name="Source" tunnel="yes"/>
      <xsl:variable name="orig" select="."/>
      <xsl:apply-templates mode="odd2odd-copy" select="@*"/>
      <xsl:apply-templates mode="odd2odd-justcopy" select="tei:altIdent"/>
      <xsl:if test="$stripped='false'">
        <xsl:apply-templates mode="odd2odd-copy" select="tei:equiv"/>
        <xsl:apply-templates mode="odd2odd-justcopy" select="tei:gloss"/>
        <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
      </xsl:if>
      <xsl:apply-templates mode="odd2odd-justcopy" select="tei:classes"/>
      <xsl:apply-templates mode="odd2odd-copy" select="tei:content"/>
      <xsl:apply-templates mode="odd2odd-copy" select="tei:constraintSpec"/>
      <attList xmlns="http://www.tei-c.org/ns/1.0">
        <xsl:call-template name="odd2odd-addClassAttsToCopy"/>
        <xsl:choose>
          <xsl:when test="tei:attList[@org='choice']">
            <xsl:for-each select="tei:attList">
              <xsl:copy>
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:attDef[@mode='add' or not(@mode)]"/>
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:attRef"/>
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:attList"/>
              </xsl:copy>
            </xsl:for-each>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:attList/tei:attDef[@mode='add' or not(@mode)]"/>
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:attList/tei:attRef"/>
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:attList/tei:attList"/>
          </xsl:otherwise>
        </xsl:choose>
      </attList>
      <xsl:if test="$stripped='false'">
        <xsl:apply-templates mode="odd2odd-justcopy" select="tei:exemplum"/>
        <xsl:apply-templates mode="odd2odd-justcopy" select="tei:remarks"/>
        <xsl:apply-templates mode="odd2odd-justcopy" select="tei:listRef"/>
      </xsl:if>
  </xsl:template>

  <xsl:template name="odd2odd-addClassAttsToCopy">
    <xsl:if test="$autoGlobal='true' and (not(@ns) or @ns='http://www.tei-c.org/ns/1.0' or @ns='http://www.tei-c.org/ns/Examples')">
      <xsl:call-template name="odd2odd-classAttributes">
        <xsl:with-param name="whence">1</xsl:with-param>
        <xsl:with-param name="elementName" select="@ident"/>
        <xsl:with-param name="className" select="'att.global'"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:for-each select="tei:classes/tei:memberOf">
      <xsl:call-template name="odd2odd-classAttributes">
        <xsl:with-param name="whence">2</xsl:with-param>
        <xsl:with-param name="elementName" select="../../@ident"/>
        <xsl:with-param name="className" select="@key"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="tei:elementSpec" mode="odd2odd-change">
    <xsl:variable name="elementName">
      <xsl:value-of select="concat(@ns,@ident)"/>
    </xsl:variable>

    <xsl:variable name="ORIGINAL" select="."/>
    <xsl:copy>
      <xsl:attribute name="rend">change</xsl:attribute>
      <xsl:apply-templates mode="odd2odd-change" select="@*"/>
      <!-- 
For each element, go through most of the sections one by one
and see if they are present in the change mode version.
If so, use them as is. The constraints and attributes are identifiable
for change individually.
 -->
      <xsl:for-each select="$ODD">
        <xsl:for-each select="key('odd2odd-CHANGE',$elementName)">
	  <xsl:if test="$verbose='true'">
	    <xsl:message>Change <xsl:value-of select="$elementName"/></xsl:message>
	  </xsl:if>
          <xsl:copy-of select="@ns"/>
          <!-- if there is an altIdent, use it -->
          <xsl:apply-templates mode="odd2odd-justcopy" select="tei:altIdent"/>
          <!-- equiv, gloss, desc trio -->
          <xsl:choose>
            <xsl:when test="tei:equiv">
              <xsl:apply-templates mode="odd2odd-copy" select="tei:equiv"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-copy" select="tei:equiv"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="tei:gloss">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:gloss"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:gloss"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="$stripped='true'"/>
            <xsl:when test="tei:desc">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <!-- classes -->
          <classes xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:choose>
              <xsl:when test="tei:classes[@mode='change']">
                <xsl:for-each select="tei:classes/tei:memberOf">
                  <xsl:choose>
                    <xsl:when test="@mode='delete'"/>
                    <xsl:when test="@mode='add' or not (@mode)">
                      <memberOf key="{@key}">
			<xsl:copy-of select="@min|@max"/>
		      </memberOf>
                    </xsl:when>
                  </xsl:choose>
                </xsl:for-each>
                <xsl:for-each select="$ORIGINAL">
                  <xsl:for-each select="tei:classes/tei:memberOf">
                    <xsl:variable name="me">
                      <xsl:value-of select="@key"/>
                    </xsl:variable>
                    <xsl:variable name="metoo">
                      <xsl:value-of select="concat(../../@ident,@key)"/>
                    </xsl:variable>
                    <xsl:for-each select="$ODD">
                      <xsl:choose>
                        <xsl:when test="key('odd2odd-DELETE',$me)"> </xsl:when>
                        <xsl:when test="key('odd2odd-MEMBEROFDELETE',$metoo)"> </xsl:when>
                        <xsl:when test="key('odd2odd-MEMBEROFADD',$metoo)"> </xsl:when>
                        <xsl:otherwise>
                          <memberOf key="{$me}"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:for-each>
                  </xsl:for-each>
                </xsl:for-each>
              </xsl:when>
              <xsl:when test="tei:classes">
                <xsl:for-each select="tei:classes/tei:memberOf">
                  <xsl:copy-of select="."/>
                </xsl:for-each>
              </xsl:when>
              <xsl:otherwise>
                <xsl:for-each select="$ORIGINAL">
                  <xsl:for-each select="tei:classes/tei:memberOf">
                    <xsl:variable name="me">
                      <xsl:value-of select="@key"/>
                    </xsl:variable>
                    <xsl:for-each select="$ODD">
                      <xsl:if test="not(key('odd2odd-DELETE',$me))">
                        <memberOf key="{$me}"/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:for-each>
                </xsl:for-each>
              </xsl:otherwise>
            </xsl:choose>
          </classes>
          <!-- valList -->
          <xsl:choose>
            <xsl:when test="tei:valList[@mode='delete']"/>
            <xsl:when test="tei:valList">
              <xsl:apply-templates mode="odd2odd-copy" select="tei:valList"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-copy" select="tei:valList"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <!-- element content -->
          <content xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:choose>
              <xsl:when test="tei:content/rng:*">
                <xsl:apply-templates mode="odd2odd-copy" select="tei:content/*"/>
              </xsl:when>
              <xsl:when test="tei:content/tei:*">
                <xsl:apply-templates mode="odd2odd-copy" select="tei:content/*"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:for-each select="$ORIGINAL">
                  <xsl:apply-templates mode="odd2odd-copy" select="tei:content/*"/>
                </xsl:for-each>
              </xsl:otherwise>
            </xsl:choose>
          </content>
          <!-- element constraints -->
          <xsl:call-template name="odd2odd-processConstraints">
            <xsl:with-param name="ORIGINAL" select="$ORIGINAL"/>
            <xsl:with-param name="elementName" select="$elementName"/>
          </xsl:call-template>
          <!-- attList -->
          <attList xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:attList/@org"/>
            <xsl:call-template name="odd2odd-processAttributes">
              <xsl:with-param name="ORIGINAL" select="$ORIGINAL"/>
              <xsl:with-param name="elementName" select="$elementName"/>
            </xsl:call-template>
          </attList>
          <!-- exemplum, remarks and listRef are either replacements or not -->
          <xsl:choose>
            <xsl:when test="$stripped='true'"/>
            <xsl:when test="tei:exemplum">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:exemplum"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:exemplum"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="$stripped='true'"/>
            <xsl:when test="tei:remarks">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:remarks"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:remarks"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="tei:listRef">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:listRef"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:listRef"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="tei:macroSpec" mode="odd2odd-change">
    <xsl:variable name="elementName">
      <xsl:value-of select="concat(@ns,@ident)"/>
    </xsl:variable>
    <xsl:variable name="ORIGINAL" select="."/>
    <xsl:copy>
      <xsl:attribute name="rend">change</xsl:attribute>
      <xsl:apply-templates mode="odd2odd-change" select="@*"/>
      <!-- 
	   For each macro, go through most of the sections one by one
	   and see if they are present in the change mode version.
	   If so, use them as is. 
      -->
      <xsl:for-each select="$ODD">
        <xsl:for-each select="key('odd2odd-CHANGE',$elementName)">
          <!-- if there is an altIdent, use it -->
          <xsl:apply-templates mode="odd2odd-justcopy" select="tei:altIdent"/>
          <!-- equiv, gloss, desc trio -->
          <xsl:choose>
            <xsl:when test="$stripped='true'"/>
            <xsl:when test="tei:equiv">
              <xsl:apply-templates mode="odd2odd-copy" select="tei:equiv"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-copy" select="tei:equiv"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="tei:gloss">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:gloss"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:gloss"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="$stripped='true'"/>
            <xsl:when test="tei:desc">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <!-- content -->
          <xsl:choose>
            <xsl:when test="tei:content">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:content"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-copy" select="tei:content"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="tei:valList">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:valList"/>
            </xsl:when>
            <xsl:when test="tei:stringVal">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:stringVal"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-copy" select="tei:stringVal"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <!-- exemplum, remarks and listRef are either replacements or not -->
          <xsl:choose>
            <xsl:when test="$stripped='true'"/>
            <xsl:when test="tei:exemplum">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:exemplum"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:exemplum"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="tei:remarks">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:remarks"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:remarks"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="$stripped='true'"/>
            <xsl:when test="tei:listRef">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:listRef"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:listRef"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="tei:classSpec" mode="odd2odd-change">
    <xsl:variable name="className">
	<xsl:value-of select="concat(@ns,@ident)"/>
    </xsl:variable>
    <xsl:variable name="ORIGINAL" select="."/>
    <xsl:copy>
      <xsl:attribute name="rend">change</xsl:attribute>
      <xsl:apply-templates mode="odd2odd-change" select="@*"/>
      <!-- for each section of the class spec, 
     go through the sections one by one
     and see if they are present in the change mode version -->
      <xsl:for-each select="$ODD">
        <xsl:for-each select="key('odd2odd-CHANGE',$className)">
          <!-- context is now a classSpec in change mode in the ODD spec -->
          <!-- description -->
          <xsl:choose>
            <xsl:when test="$stripped='true'"/>
            <xsl:when test="tei:desc">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="$ORIGINAL">
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
          <!-- classes -->
          <classes xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:choose>
              <xsl:when test="tei:classes[@mode='change']">
                <xsl:for-each select="tei:classes/tei:memberOf">
                  <xsl:choose>
                    <xsl:when test="@mode='delete'"/>
                    <xsl:when test="@mode='add' or not (@mode)">
                      <memberOf key="{@key}"/>
                    </xsl:when>
                  </xsl:choose>
                </xsl:for-each>
                <xsl:for-each select="$ORIGINAL">
                  <xsl:for-each select="tei:classes/tei:memberOf">
                    <xsl:variable name="me">
                      <xsl:value-of select="@key"/>
                    </xsl:variable>
                    <xsl:variable name="metoo">
                      <xsl:value-of select="concat(../../@ident,@key)"/>
                    </xsl:variable>
                    <xsl:for-each select="$ODD">
                      <xsl:choose>
                        <xsl:when test="key('odd2odd-DELETE',$me)"> </xsl:when>
                        <xsl:when test="key('odd2odd-MEMBEROFDELETE',$metoo)"> </xsl:when>
                        <xsl:when test="key('odd2odd-MEMBEROFADD',$metoo)"> </xsl:when>
                        <xsl:otherwise>
                          <memberOf key="{$me}"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:for-each>
                  </xsl:for-each>
                </xsl:for-each>
              </xsl:when>
              <xsl:when test="tei:classes">
                <xsl:for-each select="tei:classes/tei:memberOf">
                  <xsl:copy-of select="."/>
                </xsl:for-each>
              </xsl:when>
              <xsl:otherwise>
                <xsl:for-each select="$ORIGINAL">
                  <xsl:for-each select="tei:classes/tei:memberOf">
                    <xsl:variable name="me">
                      <xsl:value-of select="@key"/>
                    </xsl:variable>
                    <xsl:for-each select="$ODD">
                      <xsl:if test="not(key('odd2odd-DELETE',$me))">
                        <memberOf key="{$me}"/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:for-each>
                </xsl:for-each>
              </xsl:otherwise>
            </xsl:choose>
          </classes>
          <!-- attList -->
          <attList xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:call-template name="odd2odd-processAttributes">
              <xsl:with-param name="ORIGINAL" select="$ORIGINAL"/>
              <xsl:with-param name="elementName" select="''"/>
            </xsl:call-template>
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:attList/tei:attRef"/>
          </attList>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="rng:choice|rng:list|rng:group|rng:optional|rng:oneOrMore|rng:zeroOrMore" mode="odd2odd-copy">
    <xsl:call-template name="odd2odd-simplifyRelax"/>
  </xsl:template>
  <xsl:template name="odd2odd-simplifyRelax">
    <xsl:variable name="element">
      <xsl:value-of select="local-name(.)"/>
    </xsl:variable>
    <!-- 
for each RELAX NG content model,
remove reference to any elements which have been
deleted, or to classes which are empty.
This may make the container empty,
so that is only put back in if there is some content
-->
    <xsl:variable name="contents">
      <WHAT>
        <xsl:for-each select="a:*|rng:*|processing-instruction()">
          <xsl:choose>
            <xsl:when test="self::a:*">
              <xsl:copy-of select="."/>
            </xsl:when>
            <xsl:when test="self::processing-instruction()">
              <xsl:copy-of select="."/>
            </xsl:when>
            <xsl:when test="self::rng:element">
              <element xmlns="http://relaxng.org/ns/structure/1.0">
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates mode="odd2odd-copy"/>
              </element>
            </xsl:when>
            <xsl:when test="self::rng:name">
              <name xmlns="http://relaxng.org/ns/structure/1.0">
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates mode="odd2odd-copy"/>
              </name>
            </xsl:when>
            <xsl:when test="self::rng:attribute">
              <attribute xmlns="http://relaxng.org/ns/structure/1.0">
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates mode="odd2odd-copy"/>
              </attribute>
            </xsl:when>
            <xsl:when test="self::rng:data">
              <data xmlns="http://relaxng.org/ns/structure/1.0">
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates mode="odd2odd-copy"/>
              </data>
            </xsl:when>
            <xsl:when test="self::rng:text">
              <text xmlns="http://relaxng.org/ns/structure/1.0"/>
            </xsl:when>
            <xsl:when test="self::rng:value">
              <value xmlns="http://relaxng.org/ns/structure/1.0">
                <xsl:apply-templates/>
              </value>
            </xsl:when>
            <xsl:when test="self::rng:ref">
              <xsl:variable name="N" select="@name"/>
              <xsl:for-each select="$ODD">
                <xsl:choose>
                  <xsl:when test="$stripped='true'">
                    <ref xmlns="http://relaxng.org/ns/structure/1.0" name="{$N}"/>
                  </xsl:when>
                  <xsl:when test="key('odd2odd-DELETE',$N)"/>
                  <xsl:otherwise>
                    <ref xmlns="http://relaxng.org/ns/structure/1.0" name="{$N}"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="odd2odd-simplifyRelax"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </WHAT>
    </xsl:variable>
    <xsl:variable name="entCount">
      <xsl:for-each select="$contents/WHAT">
        <xsl:value-of select="count(*)"/>
      </xsl:for-each>
    </xsl:variable>
    <xsl:for-each select="$contents/WHAT">
      <xsl:choose>
        <xsl:when test="$entCount=1     and local-name(*)=$element">
          <xsl:copy-of select="*|@*|text()|processing-instruction()"/>
        </xsl:when>
        <xsl:when test="$element='optional'     and $entCount=1     and rng:zeroOrMore">
          <xsl:copy-of select="*|@*|text()|processing-instruction()"/>
        </xsl:when>
        <xsl:when test="$element='optional'     and $entCount=1     and rng:oneOrMore">
          <xsl:copy-of select="*|@*|text()|processing-instruction()"/>
        </xsl:when>
        <xsl:when test="$element='oneOrMore'     and $entCount=1     and rng:zeroOrMore">
          <oneOrMore xmlns="http://relaxng.org/ns/structure/1.0">
            <xsl:copy-of select="rng:zeroOrMore/*"/>
          </oneOrMore>
        </xsl:when>
        <xsl:when test="self::rng:zeroOrMore/rng:ref/@name='model.global'        and preceding-sibling::rng:*[1][self::rng:zeroOrMore/rng:ref/@name='model.global']"/>
        <xsl:when test="$entCount&gt;0 or $stripped='true'">
          <xsl:element xmlns="http://relaxng.org/ns/structure/1.0" name="{$element}">
            <xsl:copy-of select="*|@*|text()|processing-instruction()"/>
          </xsl:element>
        </xsl:when>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="odd2odd-classAttributes">
    <xsl:param name="elementName"/>
    <xsl:param name="className"/>
    <xsl:param name="whence"/>
    <xsl:param name="Source" tunnel="yes"/>

    <!-- 
    On entry, we are sitting on an <elementSpec> or <classSpec> 
    and seeing if we can pick up some attributes for 
    $elementName. We travel to the ODD first
    to see if it has some overrides
    -->
    <xsl:for-each select="$ODD">
      <xsl:choose>
        <xsl:when test="key('odd2odd-MEMBEROFDELETE',concat($elementName,$className))"> </xsl:when>
        <!-- the class is referenced in the ODD and has redefined <classes>-->
        <xsl:when test="key('odd2odd-ATTCLASSES',$className)/tei:classes">
          <xsl:for-each select="key('odd2odd-ATTCLASSES',$className)">
            <xsl:call-template name="odd2odd-processClassAttributes">
              <xsl:with-param name="elementName" select="$elementName"/>
              <xsl:with-param name="className" select="$className"/>
              <xsl:with-param name="whence" select="$whence"/>
              <xsl:with-param name="fromODD">true</xsl:with-param>
            </xsl:call-template>
          </xsl:for-each>
        </xsl:when>
        <!-- the class is referenced in the ODD and has redefined <attList>-->
        <xsl:when test="key('odd2odd-ATTCLASSES',$className)/tei:attList">
          <xsl:for-each select="key('odd2odd-ATTCLASSES',$className)">
            <xsl:call-template name="odd2odd-processClassAttributes">
              <xsl:with-param name="elementName" select="$elementName"/>
              <xsl:with-param name="className" select="$className"/>
              <xsl:with-param name="whence" select="$whence"/>
              <xsl:with-param name="fromODD">true</xsl:with-param>
            </xsl:call-template>
          </xsl:for-each>
        </xsl:when>
        <!-- otherwise, we'll revert to source
	     (assuming the class is of type 'atts')
	-->
        <xsl:otherwise>
          <xsl:for-each select="document($Source)/tei:TEI">
            <xsl:for-each select="key('odd2odd-ATTCLASSES',$className)">
              <xsl:call-template name="odd2odd-processClassAttributes">
                <xsl:with-param name="elementName" select="$elementName"/>
                <xsl:with-param name="className" select="$className"/>
                <xsl:with-param name="whence" select="$whence"/>
                <xsl:with-param name="fromODD">false</xsl:with-param>
              </xsl:call-template>
            </xsl:for-each>
          </xsl:for-each>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="odd2odd-processClassAttributes">
    <xsl:param name="elementName"/>
    <xsl:param name="className"/>
    <xsl:param name="fromODD">false</xsl:param>
    <xsl:param name="whence"/>
    <xsl:param name="Source" tunnel="yes"/>

    <!-- we are sitting on a attribute classSpec, could be in the ODD
	 or could be in the source -->
    <xsl:variable name="M" select="@module"/>
    <!-- decide whether to proceed with this class at all. if we don't
    include this class specifically, don't have this module, or we
    have deleted the class, 
    we can bypass it now.-->
    <xsl:variable name="use">
      <xsl:choose>
        <xsl:when test="$fromODD='true' and (not(@mode) or @mode='add')">
          <xsl:text>true</xsl:text>
        </xsl:when>
        <xsl:when test="not(@module)">
          <xsl:text>true</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="$ODD">
            <xsl:choose>
              <xsl:when test="key('odd2odd-DELETE',$className)">false</xsl:when>
              <xsl:when test="key('odd2odd-CLASSREFS',$className)">true</xsl:when>
              <xsl:when test="key('odd2odd-MODULES',$M)">
                <xsl:text>true</xsl:text>
              </xsl:when>
	      <xsl:otherwise>false</xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <!-- DEBUG-->
    <!--
    <xsl:message>START <xsl:value-of select="$whence"/>: <xsl:value-of select="$elementName"/> + <xsl:value-of
    select="$className"/> [<xsl:value-of select="$M"/>] + <xsl:value-of
    select="$fromODD"/>, so use=<xsl:value-of select="$use"/></xsl:message>
    -->
    <xsl:if test="$use='true'">
      <!-- 
	   We need to put in the class attributes. We'll 
	   use the value of $fromODD to see whether this is in the
	   current ODD or the source.
	   
	   a) the class is new in this customization, add all attributes regardless
	   b) the class is marked for deletion. do nothing
	   c) the class is marked for replacement. reference attributes from the replacement
	   d) the class is marked for change. compare attributes (tedious)
	   e) the class has no replacement; reference its attributes
	   
	   In each case, once we have a potential attribute, we have to check
	   back to see if it is changed in the element (mergeClassAttribute)
	   
	   First, establish whether any attributes in the class,
	   inherited or otherwise, are changed in the ODD. We will
	   use this information later
      -->
      <xsl:variable name="anyChanged">
        <xsl:choose>
          <xsl:when test="$fromODD='false'">
            <xsl:call-template name="odd2odd-checkClassAttribute">
              <xsl:with-param name="element" select="$elementName"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="document($Source)/tei:TEI">
              <xsl:for-each select="key('odd2odd-ATTCLASSES',$className)">
                <xsl:call-template name="odd2odd-checkClassAttribute">
                  <xsl:with-param name="element" select="$elementName"/>
                </xsl:call-template>
              </xsl:for-each>
            </xsl:for-each>
	  </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <!--
	  <xsl:message>Class <xsl:value-of select="$className"/>, element
	  <xsl:value-of select="$elementName"/>: has changes: <xsl:value-of
	  select="$anyChanged"/></xsl:message>
      -->
      <xsl:choose>
        <!-- a) new class in ODD -->
        <xsl:when test="$fromODD='true' and (not(@mode) or @mode='add')">
          <attRef xmlns="http://www.tei-c.org/ns/1.0" n="1" name="{$className}.attributes"/>
        </xsl:when>
        <!-- b) its deleted -->
        <xsl:when test="@mode='delete'"/>
        <!-- c) its a replacement -->
        <xsl:when test="@mode='replace'">
          <xsl:for-each select="tei:attList/tei:attDef">
            <xsl:call-template name="odd2odd-mergeClassAttribute">
              <xsl:with-param name="source">1</xsl:with-param>
              <xsl:with-param name="element" select="$elementName"/>
              <xsl:with-param name="class" select="$className"/>
              <xsl:with-param name="fromODD">
                <xsl:value-of select="$fromODD"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:for-each>
          <xsl:for-each select="tei:attList/tei:attList">
            <attList xmlns="http://www.tei-c.org/ns/1.0">
              <xsl:copy-of select="@org"/>
              <xsl:for-each select="tei:attDef">
                <xsl:call-template name="odd2odd-mergeClassAttribute">
                  <xsl:with-param name="source">2</xsl:with-param>
                  <xsl:with-param name="element" select="$elementName"/>
                  <xsl:with-param name="class" select="$className"/>
                  <xsl:with-param name="fromODD">
                    <xsl:value-of select="$fromODD"/>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:for-each>
            </attList>
          </xsl:for-each>
        </xsl:when>
        <!-- d) there are changes to attributes in the class spec itself,
	but the element makes no override -->
        <xsl:when test="@mode='change' and tei:attList and not (contains($anyChanged,':element-'))">
          <xsl:if test="$verbose='true'">
            <xsl:message>Phase 1 check d) Class <xsl:value-of select="$className"/> for <xsl:value-of select="$elementName"/> has no changes in element, refer by name</xsl:message>
          </xsl:if>
          <attRef xmlns="http://www.tei-c.org/ns/1.0" n="5" name="{$className}.attributes"/>
        </xsl:when>
        <!-- e) there are changes to attributes in the class spec itself -->
        <xsl:when test="@mode='change' and tei:attList">
          <!-- always references attributes in add mode -->
          <xsl:for-each select="tei:attList/tei:attDef[@mode='add']">
            <attRef xmlns="http://www.tei-c.org/ns/1.0" n="2" name="{$className}.attribute.{translate(@ident,':','')}"/>
          </xsl:for-each>
          <!-- go back to original and proceed from there -->
	  <xsl:for-each select="document($Source)/tei:TEI">
	    <xsl:for-each select="key('odd2odd-ATTCLASSES',$className)">
	      <xsl:call-template name="odd2odd-tryAttributes">
		<xsl:with-param name="elementName" select="$elementName"/>
		<xsl:with-param name="className" select="$className"/>
		<xsl:with-param name="fromODD">
		  <xsl:value-of select="$fromODD"/>
		</xsl:with-param>
	      </xsl:call-template>
	    </xsl:for-each>
	  </xsl:for-each>
          <xsl:for-each select="tei:attList/tei:attRef">
            <xsl:copy-of select="."/>
          </xsl:for-each>
        </xsl:when>
        <!-- there are no changes to the attributes in the odd-->
        <xsl:when test="$anyChanged=''">
          <xsl:if test="$verbose='true'">
            <xsl:message>Phase 1 check f) Class <xsl:value-of select="$className"/> for <xsl:value-of select="$elementName"/> has no changes, refer by name</xsl:message>
          </xsl:if>
          <attRef xmlns="http://www.tei-c.org/ns/1.0" n="4" name="{$className}.attributes"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$verbose='true'">
            <xsl:message>Phase 1 check g) Class <xsl:value-of select="$className"/> for <xsl:value-of select="$elementName"/> has changes in odd, refer by values</xsl:message>
          </xsl:if>
          <!-- attributes here -->
          <xsl:for-each select="tei:attList/tei:attDef">
            <xsl:call-template name="odd2odd-mergeClassAttribute">
              <xsl:with-param name="source">7</xsl:with-param>
              <xsl:with-param name="element" select="$elementName"/>
              <xsl:with-param name="class" select="$className"/>
              <xsl:with-param name="fromODD">
                <xsl:value-of select="$fromODD"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:for-each>
          <!-- embedded attribute lists  -->
          <xsl:for-each select="tei:attList/tei:attList">
            <attList xmlns="http://www.tei-c.org/ns/1.0">
              <xsl:copy-of select="@org"/>
              <xsl:for-each select="tei:attDef">
                <xsl:call-template name="odd2odd-mergeClassAttribute">
                  <xsl:with-param name="source">8</xsl:with-param>
                  <xsl:with-param name="element" select="$elementName"/>
                  <xsl:with-param name="class" select="$className"/>
                  <xsl:with-param name="fromODD">
                    <xsl:value-of select="$fromODD"/>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:for-each>
            </attList>
          </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>
      <!-- Now attributes referenced from classes we are a member
     of. Again, check whether we are in ODD or not. If
     we have not tinkered with the superclass, this is not
     needed, obviously.
      -->
      <!-- DEBUG
      <xsl:message>Now time to look at subclasses of <xsl:value-of
      select="@ident"/> whose changes status was <xsl:value-of
      select="$anyChanged"/>; we are in fromOdd <xsl:value-of
      select="$fromODD"/></xsl:message>
-->
      <xsl:choose>
        <xsl:when test="@mode='change' and tei:attList and not (contains($anyChanged,':element-'))"/>
        <xsl:when test="$fromODD='false' and $anyChanged =''"/>
        <xsl:when test="$fromODD='true' and not(.//tei:attDef)"/>
        <xsl:when test="$fromODD='true' and tei:classes[@mode='replace']">
          <xsl:for-each select="tei:classes/tei:memberOf">
            <xsl:call-template name="odd2odd-classAttributes">
              <xsl:with-param name="whence">3</xsl:with-param>
              <xsl:with-param name="elementName" select="$elementName"/>
              <xsl:with-param name="className" select="@key"/>
            </xsl:call-template>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$fromODD='true' and tei:classes[@mode='change']">
            <xsl:for-each select="tei:classes/tei:memberOf[@mode='add']">
              <xsl:call-template name="odd2odd-classAttributes">
                <xsl:with-param name="whence">4</xsl:with-param>
                <xsl:with-param name="elementName" select="$elementName"/>
                <xsl:with-param name="className" select="@key"/>
              </xsl:call-template>
            </xsl:for-each>
          </xsl:if>
	  <xsl:for-each select="document($Source)/tei:TEI">
	    <xsl:for-each select="key('odd2odd-ATTCLASSES',$className)">
	      <xsl:for-each select="tei:classes/tei:memberOf">
		<xsl:call-template name="odd2odd-classAttributes">
		  <xsl:with-param name="whence">5</xsl:with-param>
		  <xsl:with-param name="elementName" select="$elementName"/>
		  <xsl:with-param name="className" select="@key"/>
		</xsl:call-template>
	      </xsl:for-each>
	    </xsl:for-each>
	  </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>
  <xsl:template name="odd2odd-tryAttributes">
    <xsl:param name="elementName"/>
    <xsl:param name="className"/>
    <xsl:param name="fromODD"/>
    <xsl:for-each select="tei:attList/tei:attDef">
      <xsl:call-template name="odd2odd-mergeClassAttribute">
        <xsl:with-param name="source">3</xsl:with-param>
        <xsl:with-param name="element" select="$elementName"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="fromODD">
          <xsl:value-of select="$fromODD"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:for-each select="tei:attList/tei:attList">
      <attList xmlns="http://www.tei-c.org/ns/1.0">
        <xsl:copy-of select="@org"/>
        <xsl:for-each select="tei:attDef">
          <xsl:call-template name="odd2odd-mergeClassAttribute">
            <xsl:with-param name="source">4</xsl:with-param>
            <xsl:with-param name="element" select="$elementName"/>
            <xsl:with-param name="class" select="$className"/>
            <xsl:with-param name="fromODD">
              <xsl:value-of select="$fromODD"/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:for-each>
      </attList>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="odd2odd-mergeClassAttribute">
    <!-- 
	 sitting on a source class. look at the 
	 attribute and see whether it has changed
	 in the customization
    -->
    <xsl:param name="source"/>
    <xsl:param name="element"/>
    <xsl:param name="class"/>
    <xsl:param name="fromODD"/>
    <xsl:variable name="att" select="@ident"/>
    <xsl:variable name="wherefrom" select="."/>
    <xsl:variable name="attRef">
      <xsl:value-of select="concat($class,'.attribute.' ,translate($att,':',''),'_',$element)"/>
    </xsl:variable>
    <xsl:variable name="lookingAt">
      <xsl:value-of select="concat($element,'_',@ident)"/>
    </xsl:variable>
    <xsl:for-each select="$ODD">
      <xsl:choose>
        <!-- deleted in the customization at the class level -->
        <xsl:when test="key('odd2odd-DELETEATT',concat($class,'_',$att))"/>
        <!-- deleted in the customization at the element level -->
        <xsl:when test="key('odd2odd-DELETEATT',$lookingAt)"/>
        <!-- replaced in the customization at the element level -->
        <xsl:when test="key('odd2odd-REPLACEATT',$lookingAt)"/>
        <!-- changed in the customization by the element -->
        <xsl:when test="key('odd2odd-CHANGEATT',$lookingAt)">
          <xsl:call-template name="odd2odd-mergeAttribute">
            <xsl:with-param name="New" select="key('odd2odd-CHANGEATT',$lookingAt)"/>
            <xsl:with-param name="Old" select="$wherefrom"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="$fromODD='false'">
              <xsl:for-each select="$wherefrom">
                <xsl:call-template name="odd2odd-unChangedAtt">
                  <xsl:with-param name="debug">1</xsl:with-param>
                  <xsl:with-param name="lookingAt">
                    <xsl:value-of select="$lookingAt"/>
                  </xsl:with-param>
                  <xsl:with-param name="att">
                    <xsl:value-of select="$att"/>
                  </xsl:with-param>
                  <xsl:with-param name="attRef">
                    <xsl:value-of select="$attRef"/>
                  </xsl:with-param>
                  <xsl:with-param name="class">
                    <xsl:value-of select="$class"/>
                  </xsl:with-param>
                  <xsl:with-param name="orig">
                    <xsl:value-of select="$wherefrom"/>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="odd2odd-unChangedAtt">
                <xsl:with-param name="debug">2</xsl:with-param>
                <xsl:with-param name="lookingAt">
                  <xsl:value-of select="$lookingAt"/>
                </xsl:with-param>
                <xsl:with-param name="att">
                  <xsl:value-of select="$att"/>
                </xsl:with-param>
                <xsl:with-param name="attRef">
                  <xsl:value-of select="$attRef"/>
                </xsl:with-param>
                <xsl:with-param name="class">
                  <xsl:value-of select="$class"/>
                </xsl:with-param>
                <xsl:with-param name="orig">
                  <xsl:value-of select="$wherefrom"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="odd2odd-unChangedAtt">
    <xsl:param name="lookingAt"/>
    <xsl:param name="att"/>
    <xsl:param name="class"/>
    <xsl:param name="orig"/>
    <xsl:param name="attRef"/>
    <xsl:param name="debug"/>
    <xsl:choose>
      <!-- don't make another reference to a class attribute 
	 if we already have an attRef -->
      <xsl:when test="key('odd2odd-ATTREFS',$attRef)"/>
      <xsl:otherwise>
        <attRef xmlns="http://www.tei-c.org/ns/1.0" n="3-{$debug}" name="{$class}.attribute.{translate($att,':','')}"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="odd2odd-checkClassAttribute">
    <xsl:param name="element"/>
    <!-- look at each attribute in turn, and then repeat for any
	 inherited classes -->
    <xsl:variable name="class" select="@ident"/>
    <xsl:variable name="mode" select="@mode"/>
    <xsl:for-each select="tei:attList//tei:attDef">
      <xsl:variable name="att" select="@ident"/>
      <xsl:for-each select="$ODD">
        <xsl:choose>
          <!-- deleted in the customization at the class level -->
          <xsl:when test="key('odd2odd-DELETEATT',concat($class,'_',$att))">
            <xsl:text>:class-del-</xsl:text>
            <xsl:value-of select="$att"/>
          </xsl:when>
          <!-- deleted in the customization at the element level -->
          <xsl:when test="key('odd2odd-DELETEATT',concat($element,'_',$att))">
            <xsl:text>:element-del-</xsl:text>
            <xsl:value-of select="$att"/>
          </xsl:when>
          <!-- replaced in the customization at the element level -->
          <xsl:when test="key('odd2odd-REPLACEATT',concat($element,'_',$att))">
            <xsl:text>:element-replace-</xsl:text>
            <xsl:value-of select="$att"/>
          </xsl:when>
          <!-- changed in the customization by the element -->
          <xsl:when test="key('odd2odd-CHANGEATT',concat($element,'_',$att))">
            <xsl:text>:element-change-</xsl:text>
            <xsl:value-of select="$att"/>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
    </xsl:for-each>
    <xsl:for-each select="tei:classes/tei:memberOf">
      <xsl:for-each select="key('odd2odd-ATTCLASSES',@key)">
        <xsl:call-template name="odd2odd-checkClassAttribute">
          <xsl:with-param name="element" select="$element"/>
        </xsl:call-template>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="odd2odd-processAttributes">
    <xsl:param name="ORIGINAL"/>
    <xsl:param name="elementName"/>
    <!-- first put in the ones we know take precedence -->
    <xsl:apply-templates mode="odd2odd-justcopy" select="tei:attList/tei:attDef[@mode='add' or not(@mode)]"/>
    <xsl:apply-templates mode="odd2odd-justcopy" select="tei:attList/tei:attDef[@mode='replace']"/>
    <xsl:for-each select="$ORIGINAL/tei:attList">
      <!-- original source  context -->
      <xsl:for-each select="tei:attList">
        <attList xmlns="http://www.tei-c.org/ns/1.0">
          <xsl:copy-of select="@org"/>
          <xsl:for-each select="tei:attDef">
            <xsl:variable name="ATT" select="."/>
            <xsl:variable name="lookingAt">
              <xsl:value-of select="concat(../../../@ident,'_',@ident)"/>
            </xsl:variable>
            <xsl:for-each select="$ODD">
              <xsl:choose>
                <xsl:when test="key('odd2odd-DELETEATT',$lookingAt)"/>
                <xsl:when test="key('odd2odd-REPLACEATT',$lookingAt)"/>
                <xsl:when test="key('odd2odd-CHANGEATT',$lookingAt)">
                  <xsl:call-template name="odd2odd-mergeAttribute">
                    <xsl:with-param name="New" select="key('odd2odd-CHANGEATT',$lookingAt)"/>
                    <xsl:with-param name="Old" select="$ATT"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:copy-of select="$ATT"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
          </xsl:for-each>
        </attList>
      </xsl:for-each>
      <xsl:variable name="atts">
        <xsl:for-each select="tei:attDef">
          <xsl:variable name="ATT" select="."/>
          <xsl:variable name="lookingAt">
            <xsl:value-of select="concat(../../@ident,'_',@ident)"/>
          </xsl:variable>
          <xsl:for-each select="$ODD">
            <xsl:choose>
              <xsl:when test="key('odd2odd-DELETEATT',$lookingAt)"/>
              <xsl:when test="key('odd2odd-REPLACEATT',$lookingAt)"/>
              <xsl:when test="key('odd2odd-CHANGEATT',$lookingAt)">
                <xsl:call-template name="odd2odd-mergeAttribute">
                  <xsl:with-param name="New" select="key('odd2odd-CHANGEATT',$lookingAt)"/>
                  <xsl:with-param name="Old" select="$ATT"/>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:copy-of select="$ATT"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </xsl:for-each>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="@org">
          <attList xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:copy-of select="@org"/>
            <xsl:copy-of select="$atts"/>
          </attList>
        </xsl:when>
        <xsl:otherwise>
          <xsl:copy-of select="$atts"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <!-- now we need to go back to the classes of which this 
       element is a member and reference their untouched attributes -->
    <xsl:choose>
      <xsl:when test="$elementName=''"/>
      <xsl:otherwise>
	<xsl:if test="$autoGlobal='true'">
	  <xsl:call-template name="odd2odd-classAttributes">
	    <xsl:with-param name="whence">7</xsl:with-param>
	    <xsl:with-param name="elementName" select="$elementName"/>
	    <xsl:with-param name="className" select="'att.global'"/>
	  </xsl:call-template>
	</xsl:if>
        <xsl:variable name="classMembership">
          <x>
            <xsl:choose>
              <xsl:when test="tei:classes[@mode='change']">
                <xsl:for-each select="tei:classes/tei:memberOf[not(@mode='delete')]">
                  <xsl:copy-of select="."/>
                </xsl:for-each>
                <xsl:for-each select="$ORIGINAL">
                  <xsl:for-each select="tei:classes/tei:memberOf">
                    <xsl:copy-of select="."/>
                  </xsl:for-each>
                </xsl:for-each>
              </xsl:when>
              <xsl:when test="tei:classes">
                <xsl:for-each select="tei:classes/tei:memberOf">
                  <xsl:copy-of select="."/>
                </xsl:for-each>
              </xsl:when>
              <xsl:otherwise>
                <xsl:for-each select="$ORIGINAL">
                  <xsl:for-each select="tei:classes/tei:memberOf">
                    <xsl:copy-of select="."/>
                  </xsl:for-each>
                </xsl:for-each>
              </xsl:otherwise>
            </xsl:choose>
          </x>
        </xsl:variable>
        <xsl:for-each select="$classMembership/x/tei:memberOf">
          <xsl:if test="not(preceding-sibling::tei:memberOf[@key=current()/@key])">
            <xsl:call-template name="odd2odd-classAttributes">
              <xsl:with-param name="whence">8</xsl:with-param>
              <xsl:with-param name="elementName" select="$elementName"/>
              <xsl:with-param name="className" select="@key"/>
            </xsl:call-template>
          </xsl:if>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="odd2odd-mergeAttribute">
    <xsl:param name="New"/>
    <xsl:param name="Old"/>
    <attDef xmlns="http://www.tei-c.org/ns/1.0" ident="{$Old/@ident}">
      <xsl:for-each select="$New">
        <xsl:attribute name="usage">
          <xsl:choose>
            <xsl:when test="@usage">
              <xsl:value-of select="@usage"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$Old/@usage"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <xsl:if test="tei:altIdent">
          <xsl:apply-templates mode="odd2odd-justcopy" select="tei:altIdent"/>
        </xsl:if>
        <!-- equiv, gloss, desc trio -->
        <xsl:choose>
          <xsl:when test="$stripped='true'"/>
          <xsl:when test="tei:equiv">
            <xsl:apply-templates mode="odd2odd-copy" select="tei:equiv"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="$Old">
              <xsl:apply-templates mode="odd2odd-copy" select="tei:equiv"/>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="tei:gloss">
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:gloss"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="$Old">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:gloss"/>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="$stripped='true'"/>
          <xsl:when test="tei:desc">
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="$Old">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="tei:constraintSpec">
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:constraintSpec"/>
          </xsl:when>
          <xsl:when test="$Old/tei:constraintSpec">
            <xsl:copy-of select="$Old/tei:constraintSpec"/>
          </xsl:when>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="tei:datatype">
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:datatype"/>
          </xsl:when>
          <xsl:when test="$Old/tei:datatype">
            <xsl:copy-of select="$Old/tei:datatype"/>
          </xsl:when>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="tei:defaultVal">
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:defaultVal"/>
          </xsl:when>
          <xsl:when test="$Old/tei:defaultVal">
            <xsl:copy-of select="$Old/tei:defaultVal"/>
          </xsl:when>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="tei:valDesc">
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:valDesc"/>
          </xsl:when>
          <xsl:when test="$Old/tei:valDesc">
            <xsl:copy-of select="$Old/tei:valDesc"/>
          </xsl:when>
        </xsl:choose>
        <xsl:choose>
	  <xsl:when test="tei:valList[@mode='delete']"/>
          <xsl:when test="tei:valList[@mode='add' or @mode='replace']">
            <xsl:for-each select="tei:valList">
              <xsl:copy>
                <xsl:copy-of select="@type"/>
                <xsl:copy-of select="@repeatable"/>
                <xsl:copy-of select="*"/>
              </xsl:copy>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="tei:valList[@mode='change']">
            <xsl:for-each select="tei:valList">
              <xsl:copy>
                <xsl:copy-of select="@*"/>
                <xsl:for-each select="$Old/tei:valList/tei:valItem">
                  <xsl:variable name="thisme" select="@ident"/>
                  <xsl:if test="not($New/tei:valList/tei:valItem[@ident=$thisme and (@mode='delete' or @mode='replace')])">
                    <xsl:copy>
                      <xsl:copy-of select="@*"/>
                      <xsl:for-each select="$New/tei:valList/tei:valItem[@ident=$thisme]">
                        <xsl:choose>
                          <xsl:when test="tei:equiv">
                            <xsl:apply-templates mode="odd2odd-copy" select="tei:equiv"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:for-each select="$Old/tei:valList/tei:valItem[@ident=$thisme]">
                              <xsl:apply-templates mode="odd2odd-copy" select="tei:equiv"/>
                            </xsl:for-each>
                          </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                          <xsl:when test="tei:gloss">
                            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:gloss"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:for-each select="$Old/tei:valList/tei:valItem[@ident=$thisme]">
                              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:gloss"/>
                            </xsl:for-each>
                          </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                          <xsl:when test="$stripped='true'"/>
                          <xsl:when test="tei:desc">
                            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:for-each select="$Old/tei:valList/tei:valItem[@ident=$thisme]">
                              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
                            </xsl:for-each>
                          </xsl:otherwise>
                        </xsl:choose>
                      </xsl:for-each>
                    </xsl:copy>
                  </xsl:if>
                </xsl:for-each>
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:valItem[@mode='add']"/>
                <xsl:apply-templates mode="odd2odd-justcopy" select="tei:valItem[@mode='replace']"/>
              </xsl:copy>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="$Old/tei:valList">
            <xsl:copy-of select="$Old/tei:valList"/>
          </xsl:when>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="$stripped='true'"/>
          <xsl:when test="tei:exemplum">
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:exemplum"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="$Old">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:exemplum"/>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="$stripped='true'"/>
          <xsl:when test="tei:remarks">
            <xsl:apply-templates mode="odd2odd-justcopy" select="tei:remarks"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="$Old">
              <xsl:apply-templates mode="odd2odd-justcopy" select="tei:remarks"/>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </attDef>
  </xsl:template>
  <xsl:template match="tei:specGrp">
    <xsl:choose>
      <xsl:when test="ancestor::tei:schemaSpec"> </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:apply-templates/>
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="tei:specGrpRef"/>
  <xsl:template match="tei:macroSpec|tei:classSpec">
    <xsl:if test="not(ancestor::tei:schemaSpec)">
      <xsl:copy-of select="."/>
    </xsl:if>
  </xsl:template>
  <xsl:template match="tei:attDef[@mode]"/>
  <xsl:template match="tei:elementSpec">
    <xsl:if test="not(//tei:schemaSpec)">
      <xsl:variable name="elementName">
        <xsl:value-of select="@ident"/>
      </xsl:variable>
      <xsl:copy>
        <xsl:apply-templates mode="odd2odd-copy" select="@*"/>
        <xsl:apply-templates mode="odd2odd-justcopy" select="tei:altIdent"/>
        <xsl:if test="$stripped='false'">
          <xsl:apply-templates mode="odd2odd-copy" select="tei:equiv"/>
          <xsl:apply-templates mode="odd2odd-justcopy" select="tei:gloss"/>
          <xsl:apply-templates mode="odd2odd-justcopy" select="tei:desc"/>
        </xsl:if>
        <xsl:apply-templates mode="odd2odd-justcopy" select="tei:classes"/>
        <xsl:apply-templates mode="odd2odd-copy" select="tei:content"/>
        <xsl:apply-templates mode="odd2odd-copy" select="tei:constraintSpec"/>
        <attList xmlns="http://www.tei-c.org/ns/1.0">
	  <xsl:if test="$autoGlobal='true' and not(tei:classes/tei:memberOf[@key='att.global'])">
	    <xsl:comment>1.</xsl:comment>
	    <xsl:call-template name="odd2odd-classAttributesSimple">
	      <xsl:with-param name="whence">9</xsl:with-param>
	      <xsl:with-param name="elementName" select="$elementName"/>
	      <xsl:with-param name="className" select="'att.global'"/>
	    </xsl:call-template>
	  </xsl:if>
          <xsl:comment>2.</xsl:comment>
          <xsl:for-each select="tei:classes/tei:memberOf">
            <xsl:comment>3: <xsl:value-of select="@key"/>
                  </xsl:comment>
            <xsl:call-template name="odd2odd-classAttributesSimple">
              <xsl:with-param name="whence">10</xsl:with-param>
              <xsl:with-param name="elementName" select="$elementName"/>
              <xsl:with-param name="className" select="@key"/>
            </xsl:call-template>
          </xsl:for-each>
          <xsl:comment>4.</xsl:comment>
          <xsl:apply-templates select="tei:attList"/>
          <xsl:comment>5.</xsl:comment>
        </attList>
        <xsl:if test="$stripped='false'">
          <xsl:apply-templates mode="odd2odd-justcopy" select="tei:exemplum"/>
          <xsl:apply-templates mode="odd2odd-justcopy" select="tei:remarks"/>
          <xsl:apply-templates mode="odd2odd-justcopy" select="tei:listRef"/>
        </xsl:if>
      </xsl:copy>
    </xsl:if>
  </xsl:template>
  <xsl:template match="tei:moduleRef[@url]">
    <p>Include external module <xsl:value-of select="@url"/>.</p>
  </xsl:template>
  <xsl:template match="tei:moduleRef[@key]">
    <p>Internal module <xsl:value-of select="@key"/> was located and expanded.</p>
  </xsl:template>
  <xsl:template match="@*|processing-instruction()|text()">
    <xsl:copy-of select="."/>
  </xsl:template>
  <xsl:template match="*">
    <xsl:copy>
      <xsl:apply-templates select="*|@*|processing-instruction()|text()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template name="odd2odd-classAttributesSimple">
    <xsl:param name="elementName"/>
    <xsl:param name="className"/>
    <xsl:param name="whence"/>
    <xsl:comment>START on <xsl:value-of select="$className"/>
      </xsl:comment>
    <xsl:for-each select="key('odd2odd-ATTCLASSES',$className)">
      <xsl:variable name="CURRENTCLASS" select="."/>
      <xsl:for-each select="tei:attList/tei:attDef">
        <xsl:call-template name="odd2odd-mergeClassAttribute">
          <xsl:with-param name="source">9</xsl:with-param>
          <xsl:with-param name="element" select="$elementName"/>
          <xsl:with-param name="class" select="$className"/>
        </xsl:call-template>
      </xsl:for-each>
      <xsl:if test="tei:classes/tei:memberOf">
        <xsl:for-each select="tei:classes/tei:memberOf">
          <xsl:variable name="cName" select="@key"/>
          <xsl:call-template name="odd2odd-classAttributesSimple">
            <xsl:with-param name="whence">11</xsl:with-param>
            <xsl:with-param name="elementName" select="$elementName"/>
            <xsl:with-param name="className" select="$cName"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:if>
    </xsl:for-each>
    <xsl:comment>FINISH <xsl:value-of select="$className"/>
      </xsl:comment>
  </xsl:template>

  <xsl:template name="odd2odd-createCopy">
    <xsl:param name="Source"  tunnel="yes"/>
    <xsl:if test="$verbose='true'">
      <xsl:message>Create <xsl:value-of select="local-name()"/> named <xsl:value-of select="@ident"/>            </xsl:message>
    </xsl:if>
    <xsl:element xmlns="http://www.tei-c.org/ns/1.0" name="{local-name()}">
      <xsl:choose>
	<xsl:when test="@module"/>
	<xsl:when test="ancestor::tei:schemaSpec/@module">
	  <xsl:copy-of select="ancestor::tei:schemaSpec/@module"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:attribute name="module">
	    <xsl:text>derived-module-</xsl:text>
	    <xsl:value-of select="ancestor::tei:schemaSpec/@ident"/>
	  </xsl:attribute>
	</xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
	<xsl:when test="local-name()='classSpec'">
	  <xsl:if test="@type='model' and not(@predeclare)">
	    <xsl:attribute name="predeclare">true</xsl:attribute>
	  </xsl:if>
	  <xsl:apply-templates mode="odd2odd-copy" select="@*|*|processing-instruction()|text()"/>
	</xsl:when>
	<xsl:when test="local-name()='macroSpec'">
	  <xsl:apply-templates mode="odd2odd-copy" select="@*|*|processing-instruction()|text()"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:call-template name="odd2odd-copyElementSpec">
	    <xsl:with-param name="n" select="'2'"/>
	  </xsl:call-template>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:element>
  </xsl:template>

  <xsl:template name="odd2odd-getversion">
    <xsl:choose>
      <xsl:when test="key('odd2odd-SCHEMASPECS',$selectedSchema)">
	<xsl:for-each
	    select="key('odd2odd-SCHEMASPECS',$selectedSchema)">
	  <xsl:variable name="source" select="tei:workOutSource(.)"/>
	  <xsl:for-each select="document($source)/tei:TEI/tei:teiHeader/tei:fileDesc/tei:editionStmt/tei:edition">
	    <xsl:value-of select="."/>
	  </xsl:for-each>
	</xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
	<xsl:for-each select="document($DEFAULTSOURCE)/tei:TEI/tei:teiHeader/tei:fileDesc/tei:editionStmt/tei:edition">
	  <xsl:value-of select="."/>
	</xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="odd2odd-processConstraints">
    <xsl:param name="ORIGINAL"/>
    <xsl:param name="elementName"/>
    <!-- first put in the ones we know take precedence -->
    <xsl:apply-templates mode="odd2odd-justcopy" select="tei:constraintSpec[@mode='add' or not(@mode)]"/>
    <xsl:apply-templates mode="odd2odd-justcopy" select="tei:constraintSpec[@mode='replace']"/>
    <xsl:apply-templates mode="odd2odd-justcopy" select="tei:constraintSpec[@mode='change']"/>
    <xsl:for-each select="$ORIGINAL">
      <!-- original source  context -->
      <xsl:for-each select="tei:constraintSpec">
        <xsl:variable name="CONSTRAINT" select="."/>
        <xsl:variable name="lookingAt">
          <xsl:value-of select="concat(../@ident,'_',@ident)"/>
        </xsl:variable>
        <xsl:for-each select="$ODD">
          <xsl:choose>
            <xsl:when test="key('odd2odd-DELETECONSTRAINT',$lookingAt)"/>
            <xsl:when test="key('odd2odd-REPLACECONSTRAINT',$lookingAt)"/>
            <xsl:when test="key('odd2odd-CHANGECONSTRAINT',$lookingAt)"/>
            <xsl:otherwise>
              <xsl:copy-of select="$CONSTRAINT"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>


   <xsl:template match="@*|text()|processing-instruction()" mode="odd2odd-justcopy">
      <xsl:copy-of select="."/>
   </xsl:template>

   <xsl:template match="*" mode="odd2odd-justcopy">
      <xsl:copy>
         <xsl:apply-templates
	     select="*|@*|processing-instruction()|text()" mode="odd2odd-justcopy"/>
      </xsl:copy>
   </xsl:template>


  <xsl:template match="processing-instruction()" mode="odd2odd-pass2">
    <xsl:copy-of select="."/>
  </xsl:template>


  <xsl:template match="processing-instruction()" mode="odd2odd-pass3">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="@*|text()" mode="odd2odd-pass3">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="*" mode="odd2odd-pass3">
    <xsl:choose>
      <xsl:when test="self::rng:optional     and count(rng:zeroOrMore)=2    and count(*)=2">
        <xsl:apply-templates select="*|@*|text()|processing-instruction()" mode="odd2odd-pass3"/>
      </xsl:when>
      <xsl:when test="count(*)=1">
        <xsl:variable name="element" select="local-name()"/>
        <xsl:choose>
          <xsl:when test="*[local-name()=$element]">
            <xsl:apply-templates select="*|@*|text()|processing-instruction()" mode="odd2odd-pass3"/>
          </xsl:when>
          <xsl:when test="$element='optional'         and rng:zeroOrMore">
            <xsl:apply-templates select="*|@*|text()|processing-instruction()" mode="odd2odd-pass3"/>
          </xsl:when>
          <xsl:when test="$element='optional'         and rng:oneOrMore">
            <xsl:apply-templates select="*|@*|text()|processing-instruction()" mode="odd2odd-pass3"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:copy>
              <xsl:apply-templates select="*|@*|text()|processing-instruction()" mode="odd2odd-pass3"/>
            </xsl:copy>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:apply-templates select="*|@*|text()|processing-instruction()" mode="odd2odd-pass3"/>
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:function name="tei:workOutSource" as="xs:string">
    <xsl:param name="e"/>
    <xsl:variable name="loc">
      <xsl:choose>
	<xsl:when test="$e/@source">
	  <xsl:value-of select="$e/@source"/>
	</xsl:when>
	<xsl:when test="$e/ancestor::tei:schemaSpec/@source">
	  <xsl:value-of select="$e/ancestor::tei:schemaSpec/@source"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="$DEFAULTSOURCE"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="source">
      <xsl:choose>
	<xsl:when test="starts-with($loc,'/')">
	  <xsl:value-of select="$loc"/>
	</xsl:when>
	<xsl:when test="starts-with($loc,'file:')">
	  <xsl:value-of select="$loc"/>
	</xsl:when>
	<xsl:when test="starts-with($loc,'http:')">
	  <xsl:value-of select="$loc"/>
	</xsl:when>
	<xsl:when test="starts-with($loc,'https:')">
	  <xsl:value-of select="$loc"/>
	</xsl:when>
	<xsl:when test="starts-with($loc,'tei:')">
	  <xsl:value-of
	      select="replace($loc,'tei:',$defaultTEIServer)"/>
	  <xsl:text>/xml/tei/odd/p5subset.xml</xsl:text>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="$currentDirectory"/>
	  <xsl:value-of select="$loc"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="not(doc-available($source))">
	<xsl:call-template name="die">
	  <xsl:with-param name="message">
	    <xsl:text>Source </xsl:text>
	   <xsl:value-of select='$source'/>
	   <xsl:text> not readable</xsl:text>
	  </xsl:with-param>
	</xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
	<xsl:if test="$verbose='true'">
	  <xsl:message>
	    <xsl:text>Setting source document to </xsl:text>
	    <xsl:value-of  select="$source"/>
	  </xsl:message>
	</xsl:if>
	<xsl:value-of select="$source"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:template name="die">
    <xsl:param name="message"/>
    <xsl:message terminate="yes">
      <xsl:text>Error: odd2odd.xsl: </xsl:text> 
      <xsl:value-of select="$message"/>
    </xsl:message>
  </xsl:template>

</xsl:stylesheet>
