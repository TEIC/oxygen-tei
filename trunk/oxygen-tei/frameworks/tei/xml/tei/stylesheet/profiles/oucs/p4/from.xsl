<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tei="http://www.tei-c.org/ns/1.0" 
    xmlns="http://www.tei-c.org/ns/1.0"
    version="2.0">
  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
      <desc>

         <p> This library is free software; you can redistribute it and/or
      modify it under the terms of the GNU Lesser General Public License as
      published by the Free Software Foundation; either version 2.1 of the
      License, or (at your option) any later version. This library is
      distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
      without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
      PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
      details. You should have received a copy of the GNU Lesser General Public
      License along with this library; if not, write to the Free Software
      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA </p>
         <p>Author: See AUTHORS</p>
         <p>Id: $Id: from.xsl 8352 2011-01-07 17:41:55Z rahtz $</p>
         <p>Copyright: 2008, TEI Consortium</p>
      </desc>
   </doc>

  <!-- 
       
       P4 to P5 converter 
       
       Sebastian Rahtz <sebastian.rahtz@oucs.ox.ac.uk>
       
       $Date: 2011-01-07 17:41:55 +0000 (Fri, 07 Jan 2011) $  $Id: from.xsl 8352 2011-01-07 17:41:55Z rahtz $
       
  -->
  <xsl:output method="xml" encoding="utf-8"
    cdata-section-elements="tei:eg" omit-xml-declaration="yes"/>
  
  <xsl:variable name="processor">
    <xsl:value-of select="system-property('xsl:vendor')"/>
  </xsl:variable>
  
  <xsl:variable name="today">
      <xsl:value-of select="format-dateTime(current-dateTime(),'[Y]-[M02]-[D02]T[H02]:[m02]:[s02]Z')"/>
  </xsl:variable>
  
  <xsl:variable name="uc">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
  <xsl:variable name="lc">abcdefghijklmnopqrstuvwxyz</xsl:variable>
  
  <xsl:template match="*">
    <xsl:choose>
      <xsl:when test="namespace-uri()=''">
	<xsl:element namespace="http://www.tei-c.org/ns/1.0" name="{local-name(.)}">
	  <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
	</xsl:element>
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy>
	  <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
	</xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="@*|processing-instruction()|comment()">
    <xsl:copy/>
  </xsl:template>
  
  
  <xsl:template match="text()">
    <xsl:value-of select="."/>
  </xsl:template>
  
  
  <!-- change of name, or replaced by another element -->
  <xsl:template match="teiCorpus.2">
    <teiCorpus>
      <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
    </teiCorpus>
  </xsl:template>
  
  <xsl:template match="witness/@sigil">
    <xsl:attribute name="xml:id">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="witList">
    <listWit>
      <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
    </listWit>
  </xsl:template>
  
  
  <xsl:template match="TEI.2">
    <TEI>
      <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
    </TEI>
  </xsl:template>
  
  <xsl:template match="xref">
    <xsl:element namespace="http://www.tei-c.org/ns/1.0" name="ref">
      <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
    </xsl:element>
  </xsl:template>
  
  
  <xsl:template match="xptr">
    <xsl:element namespace="http://www.tei-c.org/ns/1.0" name="ptr">
      <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
    </xsl:element>
  </xsl:template>
  
  
  <xsl:template match="figure[@file]">
    <figure>
      <graphic>
	<xsl:apply-templates select="@*"/>
      </graphic>
      <xsl:apply-templates/>
    </figure>
  </xsl:template>
<xsl:template match="figure/@file">
    <xsl:attribute name="url">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="figure[@url]">
    <figure>
      <graphic>
	<xsl:apply-templates select="@*"/>
      </graphic>
      <xsl:apply-templates/>
    </figure>
  </xsl:template>
  
  
  <xsl:template match="figure/@entity"/>
  
  <xsl:template match="figure[@entity]">
    <figure>
      <graphic>
	<xsl:attribute name="url">
	  <xsl:choose>
	    <xsl:when test="unparsed-entity-uri(@entity)=''">
	      <xsl:text>ENTITY_</xsl:text>
	      <xsl:value-of select="@entity"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:value-of select="unparsed-entity-uri(@entity)"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:attribute>
	<xsl:apply-templates select="@*"/>
      </graphic>
      <xsl:apply-templates/>
    </figure>
  </xsl:template>
  
  <xsl:template match="event">
    <incident>
      <xsl:apply-templates select="@*|*|text()|comment()|processing-instruction()"/>
    </incident>
  </xsl:template>
  
  <xsl:template match="state">
    <refState>
      <xsl:apply-templates select="@*|*|text()|comment()|processing-instruction()"/>
    </refState>
  </xsl:template>
  
  
  <!-- lost elements -->
  <xsl:template match="dateRange">
    <date>
      <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
    </date>
  </xsl:template>
  
  
  <xsl:template match="dateRange/@from">
    <xsl:copy-of select="."/>
  </xsl:template>
  
  <xsl:template match="dateRange/@to">
    <xsl:copy-of select="."/>
  </xsl:template>
  
  <xsl:template match="language">
    <xsl:element namespace="http://www.tei-c.org/ns/1.0" name="language">
	<xsl:if test="@id">
        <xsl:attribute name="ident">
         	<xsl:value-of select="@id"/>
        </xsl:attribute>
        </xsl:if>
      <xsl:apply-templates select="*|processing-instruction()|comment()|text()"/>
    </xsl:element>
  </xsl:template>
  
  <!-- attributes lost -->
  <!-- dropped from TEI. Added as new change records later -->
  <xsl:template match="@date.created"/>
  
  <xsl:template match="@date.updated"/>
  
  <!-- dropped from TEI. No replacement -->
  <xsl:template match="refsDecl/@doctype"/>
  
  <!-- attributes changed name -->
  
  <xsl:template match="date/@value">
    <xsl:attribute name="when">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>
  
  
  <xsl:template match="@url">
    <xsl:attribute name="target">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="figure/@url">
    <xsl:attribute name="url">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>
  
  
  <xsl:template match="@doc">
    <xsl:attribute name="target">
      <xsl:value-of select="unparsed-entity-uri(.)"/>
    </xsl:attribute>
  </xsl:template>
  
  
  <xsl:template match="@id">
    <xsl:choose>
      <xsl:when test="parent::lang">
	<xsl:attribute name="ident">
	  <xsl:value-of select="."/>
	</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
	<xsl:attribute name="xml:id">
	  <xsl:value-of select="."/>
	</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="@lang">
    <xsl:attribute name="xml:lang">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>
  
  
  <xsl:template match="change/@date"/>
  
  <xsl:template match="date/@certainty">
    <xsl:attribute name="cert">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>
  
  <!-- all pointing attributes preceded by # -->
  
  <xsl:template match="variantEncoding/@location">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="@ana|@active|@adj|@adjFrom|@adjTo|@children|@class|@code|@copyOf|@corresp|@decls|@domains|@end|@exclude|@fVal|@feats|@follow|@hand|@inst|@langKey|@location|@mergedin|@new|@next|@old|@origin|@otherLangs|@parent|@passive|@perf|@prev|@render|@resp|@sameAs|@scheme|@script|@select|@since|@start|@synch|@target|@targetEnd|@value|@who|@wit">
    <xsl:attribute name="{name(.)}">
      <xsl:call-template name="splitter">
	<xsl:with-param name="val">
	  <xsl:value-of select="."/>
	</xsl:with-param>
      </xsl:call-template>
    </xsl:attribute>
  </xsl:template>
  
  
  <xsl:template name="splitter">
    <xsl:param name="val"/>
    <xsl:choose>
      <xsl:when test="contains($val,' ')">
	<xsl:text>#</xsl:text>
	<xsl:value-of select="substring-before($val,' ')"/>
	<xsl:text> </xsl:text>
	<xsl:call-template name="splitter">
	  <xsl:with-param name="val">
	    <xsl:value-of select="substring-after($val,' ')"/>
	  </xsl:with-param>
	</xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
	<xsl:text>#</xsl:text>
	<xsl:value-of select="$val"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <!-- fool around with selected elements -->
  

 <!-- imprint is no longer allowed inside bibl -->
 <xsl:template match="bibl/imprint">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="editionStmt/editor">
    <respStmt>    
      <resp><xsl:value-of select="@role"/></resp>
      <name><xsl:apply-templates/></name>
    </respStmt>
  </xsl:template>
  
  <!-- header -->  
  
  <xsl:template match="teiHeader">
    <teiHeader>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()"/>
      
      <xsl:if test="not(revisionDesc) and (@date.created or @date.updated)">
	<revisionDesc>
	  <xsl:if test="@date.updated">
	    <change>>
	    <label>updated</label>
	    <date>
	      <xsl:value-of select="@date.updated"/>
	    </date>
	    <label>Date edited</label>
	    </change>
	  </xsl:if>
	  <xsl:if test="@date.created">
	    <change>
	      <label>created</label>
	      <date>
		<xsl:value-of select="@date.created"/>
	      </date>
	      <label>Date created</label>
	    </change>
	  </xsl:if>
	</revisionDesc>
      </xsl:if>
      <!--
	  <change when="{$today}">Converted to TEI P5 XML by p4top5.xsl
	  written by Sebastian
	  Rahtz at Oxford University Computing Services.</change>
	  </revisionDesc>
	  </xsl:if>
      -->
    </teiHeader>
  </xsl:template>
  
  <xsl:template match="revisionDesc">
    <revisionDesc>
      <xsl:apply-templates
	  select="@*|*|comment()|processing-instruction()"/>
    </revisionDesc>
  </xsl:template>
  
  <xsl:template match="publicationStmt">
    <publicationStmt>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()"/>
      <!--
	  <availability>
	  <p>Licensed under <ptr target="http://creativecommons.org/licenses/by-sa/2.0/uk/"/></p>
	  </availability>
      -->
    </publicationStmt>
  </xsl:template>
  
 <!-- space does not have @extent any more -->
  <xsl:template match="space/@extent">
    <xsl:attribute name="quantity">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

  <!-- tagsDecl has a compulsory namespace child now -->
  <xsl:template match="tagsDecl">
    <xsl:if test="*">
      <tagsDecl>
	<namespace name="http://www.tei-c.org/ns/1.0">
	  <xsl:apply-templates select="*|comment()|processing-instruction"/>
	</namespace>
      </tagsDecl>
    </xsl:if>
  </xsl:template>
  
  <!-- orgTitle inside orgName? redundant -->
  <xsl:template match="orgName/orgTitle">
      <xsl:apply-templates/>
  </xsl:template>

 <!-- no need for empty <p> in sourceDesc -->  
  <xsl:template match="sourceDesc/p[string-length(.)=0]"/>
  
  <!-- start creating the new choice element -->
  <xsl:template match="corr[@sic]">
    <choice>
      <corr>
	<xsl:value-of select="text()" />
      </corr>
      <sic>
	<xsl:value-of select="@sic" />
      </sic>
    </choice>
  </xsl:template>
  
  <xsl:template match="gap/@desc">
    <desc>
      <xsl:value-of select="."/>
    </desc>
  </xsl:template>

  <xsl:template match="sic[@corr]">
    <choice>
      <sic>
	<xsl:apply-templates/>
      </sic>
      <corr>
	<xsl:value-of select="@corr" />
      </corr>
    </choice>
  </xsl:template>
  
  <xsl:template match="abbr[@expan]">
    <choice>
      <abbr>
	<xsl:apply-templates/>
      </abbr>
      <expan>
	<xsl:value-of select="@expan" />
      </expan>
    </choice>
  </xsl:template>
  
  <xsl:template match="expan[@abbr]">
    <choice>
      <expan>
	<xsl:apply-templates/>
      </expan>
      <abbr>
	<xsl:value-of select="@abbr" />
      </abbr>
    </choice>
  </xsl:template>
  
  <!-- special consideration for <change> element -->
  <xsl:template match="change">
    <change>
      <xsl:apply-templates select="item/@*"/>
      
      <xsl:apply-templates select="date"/>
      
      <xsl:if test="respStmt/resp">
	<label>
	  <xsl:value-of select="respStmt/resp/text()"/>
	</label>
      </xsl:if>
	<xsl:for-each select="respStmt/name">
	  <name>
	    <xsl:apply-templates
		select="@*|*|comment()|processing-instruction()|text()"/>
	  </name>
	</xsl:for-each>
	<xsl:for-each select="item">
	  <xsl:apply-templates
	      select="*|comment()|processing-instruction()|text()"/>
	</xsl:for-each>
    </change>
  </xsl:template>


  <xsl:template match="respStmt[resp]">
    <respStmt>
      <xsl:choose>
	<xsl:when test="resp/name">
	  <resp>
	    <xsl:value-of select="resp/text()"/>
	  </resp>
	    <xsl:for-each select="resp/name">
	      <name>
		<xsl:apply-templates/>
	      </name>
	    </xsl:for-each>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:apply-templates/>
	  <name>
	  </name>
	</xsl:otherwise>
      </xsl:choose>
    </respStmt>
  </xsl:template>

  <xsl:template match="q/@direct"/>
  
  <xsl:template match="q">
    <q>
      <xsl:apply-templates
	  select="@*|*|comment()|processing-instruction()|text()"/>
    </q>
  </xsl:template>

  
<!-- if we are reading the P4 with a DTD,
       we need to avoid copying the default values
       of attributes -->
  
  <xsl:template match="@targOrder">
    <xsl:if test="not(translate(.,$uc,$lc) ='u')">
      <xsl:attribute name="targOrder">
	<xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="@opt">
    <xsl:if test="not(translate(.,$uc,$lc) ='n')">
      <xsl:attribute name="opt">
	<xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="@to">
    <xsl:if test="not(translate(.,$uc,$lc) ='ditto')">
      <xsl:attribute name="to">
	<xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="@default">
    <xsl:choose>
      <xsl:when test="translate(.,$uc,$lc)= 'no'"/>
      <xsl:otherwise>
	<xsl:attribute name="default">
	  <xsl:value-of select="."/>
	</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="@part">
    <xsl:if test="not(translate(.,$uc,$lc) ='n')">
      <xsl:attribute name="part">
	<xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="@full">
    <xsl:if test="not(translate(.,$uc,$lc) ='yes')">
      <xsl:attribute name="full">
	<xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  
  
 
  <xsl:template match="@status">
    <xsl:choose>
      <xsl:when test="parent::teiHeader">
	<xsl:if test="not(translate(.,$uc,$lc) ='new')">
	  <xsl:attribute name="status">
	    <xsl:value-of select="."/>
	  </xsl:attribute>
	</xsl:if>
      </xsl:when>
      <xsl:when test="parent::del">
	<xsl:if test="not(translate(.,$uc,$lc) ='unremarkable')">
	  <xsl:attribute name="status">
	    <xsl:value-of select="."/>
	  </xsl:attribute>
	</xsl:if>
      </xsl:when>
      <xsl:otherwise>
	<xsl:attribute name="status">
	  <xsl:value-of select="."/>
	</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="@place">
    <xsl:if test="not(translate(.,$uc,$lc) ='unspecified')">
      <xsl:attribute name="place">
	<xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="@sample">
    <xsl:if test="not(translate(.,$uc,$lc) ='complete')">
      <xsl:attribute name="sample">
	<xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="@org">
    <xsl:if test="not(translate(.,$uc,$lc) ='uniform')">
      <xsl:attribute name="org">
	<xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="teiHeader/@type">
    <xsl:if test="not(translate(.,$uc,$lc) ='text')">
      <xsl:attribute name="type">
	<xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  
  <!-- yes|no to boolean -->
  
  <xsl:template match="@anchored">
    <xsl:attribute name="anchored">
      <xsl:choose>
	<xsl:when test="translate(.,$uc,$lc)='yes'">true</xsl:when>
	<xsl:when test="translate(.,$uc,$lc)='no'">false</xsl:when>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="sourceDesc/@default"/>
  
  <xsl:template match="@tei">
    <xsl:attribute name="tei">
      <xsl:choose>
	<xsl:when test="translate(.,$uc,$lc)='yes'">true</xsl:when>
	<xsl:when test="translate(.,$uc,$lc)='no'">false</xsl:when>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="@langKey"/>  
  
  <xsl:template match="@TEIform"/>  


  <xsl:template match="gi/@TEI">  	 
    <xsl:if test=".='yes'">
      <xsl:attribute name="scheme">TEI</xsl:attribute>
    </xsl:if>
  </xsl:template>

<!-- assorted atts -->
  <xsl:template match="@old"/>  

  <xsl:template match="xref/@from"/>  

  <xsl:template match="@mergedin">  
    <xsl:attribute name="mergedIn">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

<!-- deal with the loss of div0 -->  

  <xsl:template match="div1|div2|div3|div4|div5|div6">
    <xsl:variable name="divName">
    <xsl:choose>
      <xsl:when test="ancestor::div0">
	<xsl:text>div</xsl:text>
	<xsl:value-of select="number(substring-after(local-name(.),'div')) + 1"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="local-name()"/>
      </xsl:otherwise>
    </xsl:choose>
    </xsl:variable>
    <xsl:element name="{$divName}" namespace="http://www.tei-c.org/ns/1.0">
      <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="div0">
    <div1>
    <xsl:apply-templates 
	select="*|@*|processing-instruction()|comment()|text()"/>
    </div1>
  </xsl:template>

<!-- from Conal Tuohy -->
<xsl:template match="orig[@reg]">
  <choice>
    <orig>
      <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
    </orig>
    <reg>
      <xsl:value-of select="@reg"/>
    </reg>
  </choice>
</xsl:template>

<xsl:template match="reg[@orig]">
  <choice>
    <reg><xsl:apply-templates select="*|@*|processing-instruction()|
    comment()|text()"/></reg>
    <orig><xsl:value-of select="@orig"/></orig>
  </choice>
</xsl:template>

<xsl:template match="@orig|@reg"/>

<!-- remove default values for attributes -->

<xsl:template match="cell/@role[.='data']"/>
<xsl:template match="cell/@rows[.='1']"/>
<xsl:template match="cell/@cols[.='1']"/>
<xsl:template match="q/@broken[.='no']"/>

<!-- from CES -->
  <xsl:template match="cesdoc">
    <cesDoc>
    <xsl:apply-templates 
        select="*|@*|processing-instruction()|comment()|text()"/>
    </cesDoc>
</xsl:template>

<!-- from OTA DTD -->
  <xsl:template match="spkr">
    <speaker>
    <xsl:apply-templates 
        select="*|@*|processing-instruction()|comment()|text()"/>
    </speaker>
  </xsl:template>

  <xsl:template match="letter">
    <floatingText type="letter">
    <xsl:apply-templates 
        select="*|@*|processing-instruction()|comment()|text()"/>
    </floatingText>
  </xsl:template>

<!-- from OUCS -->
<xsl:template match="Code">
  <code>
    <xsl:apply-templates 
        select="*|@*|processing-instruction()|comment()|text()"/>
  </code>
</xsl:template>
<xsl:template match="Program">
  <eg>
    <xsl:apply-templates 
        select="*|@*|processing-instruction()|comment()|text()"/>
  </eg>
</xsl:template>

<xsl:template match="Menu">
  <hi rend="Menu">
    <xsl:apply-templates 
        select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="B" >
  <hi>
    <xsl:apply-templates
	select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="Button" >
  <hi rend="Button">
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>


<xsl:template match="EM" >
  <emph>
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </emph>
</xsl:template>

<xsl:template match="Field" >
  <hi rend="Field">
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="Filespec" >
  <hi rend="Filespec">
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="Icon" >
  <hi rend="Icon">
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="Info" >
  <hi rend="Info">
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="Input" >
  <hi rend="Input">
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="Key" >
  <hi rend="Key">
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="Label" >
  <hi rend="Label">
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="Link" >
  <hi rend="Link">
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="Software" >
  <hi rend="Software">
    <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="Value" >
  <hi rend="Value">
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </hi>
</xsl:template>

<xsl:template match="dialect" >
  <trait type="dialect">
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </trait>
</xsl:template>

<xsl:template match="born" >
  <birth>
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </birth>
</xsl:template>

<xsl:template match="domicile" >
  <residence>
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
  </residence>
</xsl:template>

<xsl:template match="para" >
<p>
  <xsl:apply-templates
      select="*|@*|processing-instruction()|comment()|text()"/>
</p>
</xsl:template>

<xsl:template match="particLinks" >
  <relationGrp>
    <xsl:apply-templates
	select="*|@*|processing-instruction()|comment()|text()"/>
  </relationGrp>
</xsl:template>


</xsl:stylesheet>
