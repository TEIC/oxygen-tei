<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
    xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:html="http://www.w3.org/1999/xhtml" 
    xmlns:i="http://www.iso.org/ns/1.0"
    xmlns:rng="http://relaxng.org/ns/structure/1.0"
    xmlns:s="http://www.ascc.net/xml/schematron" 
    xmlns:sch="http://purl.oclc.org/dsdl/schematron" 
    xmlns="http://www.tei-c.org/ns/1.0"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:teix="http://www.tei-c.org/ns/Examples" 
    xmlns:xi="http://www.w3.org/2001/XInclude"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="a fo html i rng s sch tei teix xi xs xsl" 
    version="2.0">
  <xsl:import href="teiodds.xsl"/>
  <xsl:import href="../common2/i18n.xsl"/>
  <xsl:import href="../common2/tei-param.xsl"/>
  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
      <desc>
         <p> TEI stylesheet for making JSON from ODD </p>
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
         <p>Id: $Id: odd2json.xsl 9669 2011-11-07 19:17:54Z rahtz $</p>
         <p>Copyright: 2011, TEI Consortium</p>
      </desc>
   </doc>
  <xsl:output encoding="utf-8" indent="yes" method="text"/>
  <xsl:strip-space elements="*"/>
  <xsl:param name="TEIC">false</xsl:param>
  <xsl:param name="verbose"/>
  <xsl:param name="outputDir"/>
  <xsl:param name="appendixWords"/>
  <xsl:template name="makeAnchor">
      <xsl:param name="name"/>
  </xsl:template>
  <xsl:param name="splitLevel">-1</xsl:param>
  <xsl:variable name="oddmode">dtd</xsl:variable>
  <xsl:variable name="filesuffix"/>
   <!-- get list of output files -->
  <xsl:variable name="linkColor"/>
  <xsl:template match="tei:moduleSpec[@type='decls']"/>

  <xsl:variable name="dq">"</xsl:variable>
  <xsl:variable name="escdq">\\"</xsl:variable>
  <xsl:template match="/">
    <xsl:text>{"title": "</xsl:text>
    <xsl:call-template name="generateTitle"/>
    <xsl:text>","edition": "</xsl:text>
    <xsl:call-template name="generateEdition"/>
    <xsl:text>","generator": "oddj2son",
    "date":"</xsl:text>
    <xsl:call-template name="showDate"/>
    <xsl:text>","modules": [</xsl:text>
    <xsl:for-each select="key('Modules',1)">
      <xsl:sort select="@ident"/>
      <xsl:text>{"ident":"</xsl:text>
      <xsl:value-of select="@ident"/>
      <xsl:text>",</xsl:text>
      <xsl:call-template name="desc"/>
      <xsl:text>}</xsl:text>
      <xsl:if test="not(position() = last())">,</xsl:if>
      <xsl:text>&#10;</xsl:text>
    </xsl:for-each>
    <xsl:text>],</xsl:text>

    <xsl:text>"elements": [</xsl:text>
    <xsl:for-each select="key('ELEMENTDOCS',1)">
      <xsl:sort select="@ident"/>
      <xsl:text>{"ident":"</xsl:text>
      <xsl:value-of select="@ident"/>
      <xsl:text>",</xsl:text>
      <xsl:text>"module":"</xsl:text>
      <xsl:value-of select="@module"/>
      <xsl:text>",</xsl:text>
      <xsl:call-template name="desc"/>
      <xsl:if test="tei:classes">
	<xsl:text>,"classes":[</xsl:text>
	<xsl:for-each select="tei:classes/tei:memberOf">
	  <xsl:text>{"</xsl:text>
	  <xsl:value-of select="@key"/>
	  <xsl:text>":"</xsl:text>
	  <xsl:for-each select="key('IDENTS',@key)">
	    <xsl:value-of select="@type"/>
	  </xsl:for-each>
	  <xsl:text>"}</xsl:text>
	  <xsl:if test="following-sibling::tei:memberOf">,</xsl:if>
	</xsl:for-each>
	<xsl:text>]</xsl:text>
      </xsl:if>
      <xsl:text>,"attributes":[</xsl:text>
      <xsl:variable name="a">
	<xsl:call-template name="atts"/>
      </xsl:variable>
      <xsl:for-each select="$a/tei:attDef">
	<xsl:text>{"ident":"</xsl:text>
	<xsl:value-of select="@ident"/>
	<xsl:text>",</xsl:text>
	<xsl:value-of select="tei:desc"/>
	<xsl:text>}</xsl:text>
	<xsl:if test="following-sibling::tei:attDef">,</xsl:if>
      </xsl:for-each>
      <xsl:text>]</xsl:text>
      <xsl:text>}</xsl:text>
      <xsl:if test="not(position() = last())">,</xsl:if>
      <xsl:text>&#10;</xsl:text>
    </xsl:for-each>
    <xsl:text>],</xsl:text>

    <xsl:text>"attclasses": [</xsl:text>
    <xsl:for-each select="key('ATTCLASSDOCS',1)">
      <xsl:sort select="@ident"/>
      <xsl:text>{"ident":"</xsl:text>
      <xsl:value-of  select="@ident"/>
      <xsl:text>",</xsl:text>
      <xsl:text>"type":"</xsl:text>
      <xsl:value-of  select="@type"/>
      <xsl:text>",</xsl:text>
      <xsl:call-template name="desc"/>
      <xsl:text>}</xsl:text>
      <xsl:if test="not(position() = last())">,</xsl:if>
      <xsl:text>&#10;</xsl:text>
    </xsl:for-each>
    <xsl:text>],</xsl:text>

    <xsl:text>"modelclasses": [</xsl:text>
    <xsl:for-each select="key('MODELCLASSDOCS',1)">
      <xsl:sort select="@ident"/>
      <xsl:text>{"ident":"</xsl:text>
      <xsl:value-of  select="@ident"/>
      <xsl:text>",</xsl:text>
      <xsl:text>"type":"</xsl:text>
      <xsl:value-of  select="@type"/>
      <xsl:text>",</xsl:text>
      <xsl:call-template name="desc"/>
      <xsl:text>}</xsl:text>
      <xsl:if test="not(position() = last())">,</xsl:if>
      <xsl:text>&#10;</xsl:text>
    </xsl:for-each>
    <xsl:text>],</xsl:text>

    <xsl:text>"macros": [</xsl:text>
    <xsl:for-each select="key('MACRODOCS',1)">
      <xsl:sort select="@ident"/>
      <xsl:text>{"ident":"</xsl:text>
      <xsl:value-of select="@ident"/>
      <xsl:text>",</xsl:text>
      <xsl:text>"type":"</xsl:text>
      <xsl:value-of select="@type"/>
      <xsl:text>",</xsl:text>
      <xsl:call-template name="desc"/>
      <xsl:text>}</xsl:text>
      <xsl:if test="not(position() = last())">,</xsl:if>
      <xsl:text>&#10;</xsl:text>
    </xsl:for-each>
    <xsl:text>]</xsl:text>
    <xsl:text>}</xsl:text>
  </xsl:template>

  <xsl:template name="bitOut">
      <xsl:param name="grammar"/>
      <xsl:param name="element"/>
      <xsl:param name="content"/>
      <xsl:value-of select="normalize-space($content)"/>
  </xsl:template>

  <xsl:template name="typewriter">
    <xsl:param name="text"/>
    <xsl:value-of select="$text"/>
  </xsl:template>

  <xsl:template name="desc">
    <xsl:variable name="d">      
      <xsl:call-template name="makeDescription"/>
    </xsl:variable>
    <xsl:text>"desc":"</xsl:text>
    <xsl:value-of select="replace(normalize-space($d),$dq,$escdq)"/>
    <xsl:text>"</xsl:text>
  </xsl:template>

  <xsl:template name="atts">
    <xsl:call-template name="listAtts"/>
    <xsl:if test="$TEIC='true'">
      <xsl:call-template name="classA">
	<xsl:with-param name="i">att.global</xsl:with-param>
      </xsl:call-template>
    </xsl:if>
    <xsl:for-each select="tei:classes/tei:memberOf">
      <xsl:call-template name="classA">
	<xsl:with-param name="i" select="@key"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="listAtts">
    <xsl:for-each select=".//tei:attDef">
      <tei:attDef ident="{@ident}">
	<desc>
	  <xsl:call-template name="desc"/>
	</desc>
      </tei:attDef>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="classA">
    <xsl:param name="i"/>
    <xsl:for-each select="key('CLASSES',$i)">
      <xsl:call-template name="listAtts"/>
      <xsl:for-each select="tei:classes/tei:memberOf">
	<xsl:call-template name="classA">
	  <xsl:with-param name="i" select="@key"/>
	</xsl:call-template>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>


    <xsl:template name="generateEdition">
      <xsl:value-of
	  select="tei:TEI/tei:teiHeader/tei:fileDesc/tei:editionStmt/tei:edition"/>
    </xsl:template>

    <xsl:template name="generateTitle">
      <xsl:for-each
	  select="tei:TEI/tei:teiHeader/tei:fileDesc/tei:titleStmt">
	<xsl:choose>
	  <xsl:when test="tei:title[@type='main']">
	    <xsl:apply-templates select="tei:title"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:apply-templates select="tei:title"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
