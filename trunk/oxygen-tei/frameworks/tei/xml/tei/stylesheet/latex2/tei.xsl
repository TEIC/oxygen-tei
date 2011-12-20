<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:s="http://www.ascc.net/xml/schematron"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"                
                xmlns:tei="http://www.tei-c.org/ns/1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="s xs tei"
                version="2.0">
  <xsl:import href="../common2/tei.xsl"/>
  <xsl:import href="tei-param.xsl"/>
  <xsl:import href="../common2/verbatim.xsl"/>
  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
      <desc>
         <p>
    TEI stylesheet for making LaTeX output.
      </p>
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
         <p>Id: $Id: tei.xsl 9669 2011-11-07 19:17:54Z rahtz $</p>
         <p>Copyright: 2011, TEI Consortium</p>
      </desc>
   </doc>
  <xsl:output method="text" encoding="utf8"/>
  <xsl:strip-space elements="*"/>
  <xsl:preserve-space elements="tei:hi tei:emph tei:foreign tei:p"/>
  <xsl:include href="core.xsl"/>
  <xsl:include href="corpus.xsl"/>
  <xsl:include href="drama.xsl"/>
  <xsl:include href="figures.xsl"/>
  <xsl:include href="header.xsl"/>
  <xsl:include href="linking.xsl"/>
  <xsl:include href="namesdates.xsl"/>
  <xsl:include href="tagdocs.xsl"/>
  <xsl:include href="textstructure.xsl"/>
  <xsl:include href="transcr.xsl"/>
  <xsl:include href="verse.xsl"/>
  <xsl:include href="textcrit.xsl"/>

  <xsl:param name="startNamespace">\color{red}</xsl:param>
  <xsl:param name="startElement">{</xsl:param>
  <xsl:param name="highlightStartElementName">\textcolor{red}{</xsl:param>
  <xsl:param name="highlightEndElementName">}</xsl:param>
  <xsl:param name="startElementName">\textbf{</xsl:param>
  <xsl:param name="startAttribute">{</xsl:param>
  <xsl:param name="startAttributeValue">{</xsl:param>
  <xsl:param name="startComment">\begin{it}</xsl:param>
  <xsl:param name="endElement">}</xsl:param>
  <xsl:param name="endElementName">}</xsl:param>
  <xsl:param name="endComment">\end{it}</xsl:param>
  <xsl:param name="endAttribute">}</xsl:param>
  <xsl:param name="endAttributeValue">}</xsl:param>
  <xsl:param name="endNamespace"/>
  <xsl:param name="spaceCharacter">\hspace*{6pt}</xsl:param>
  <xsl:variable name="docClass">
      <xsl:choose>
         <xsl:when test="/tei:TEI[@rend='letter']">
            <xsl:text>letter</xsl:text>
         </xsl:when>
         <xsl:when test="/tei:TEI[@rend='book']">
            <xsl:text>book</xsl:text>
         </xsl:when>
         <xsl:otherwise>
            <xsl:text>article</xsl:text>
         </xsl:otherwise>
      </xsl:choose>
  </xsl:variable>
  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>Process elements  processing-instruction()[name(.)='tex']</desc>
   </doc>
  <xsl:template match="processing-instruction()[name(.)='tex']">
      <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template name="verbatim-lineBreak">
      <xsl:param name="id"/>
      <xsl:text>\mbox{}\newline 
</xsl:text>
  </xsl:template>

  <xsl:template name="verbatim-createElement">
      <xsl:param name="name"/>
      <xsl:param name="special"/>
      <xsl:text>\textbf{</xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text>}</xsl:text>
  </xsl:template>

  <xsl:template name="verbatim-newLine"/>

  <xsl:template name="verbatim-createAttribute">
      <xsl:param name="name"/>
      <xsl:value-of select="$name"/>
  </xsl:template>

  <xsl:template name="verbatim-Text">
      <xsl:param name="words"/>
      <xsl:choose>
         <xsl:when test="parent::*/@xml:lang='zh-tw'">
	           <xsl:text>{\textChinese </xsl:text>
		   <xsl:value-of select="tei:escapeCharsVerbatim($words)"/>
	           <xsl:text>}</xsl:text>
         </xsl:when>
         <xsl:when test="parent::*/@xml:lang='ja'">
	           <xsl:text>{\textJapanese </xsl:text>
		   <xsl:value-of select="tei:escapeCharsVerbatim($words)"/>
	           <xsl:text>}</xsl:text>
         </xsl:when>
         <xsl:when test="parent::*/@xml:lang='kr'">
	           <xsl:text>{\textKorean </xsl:text>
		   <xsl:value-of select="tei:escapeCharsVerbatim($words)"/>
	           <xsl:text>}</xsl:text>
         </xsl:when>
         <xsl:otherwise>
	   <xsl:value-of select="tei:escapeCharsVerbatim($words)"/>
         </xsl:otherwise>
      </xsl:choose>

  </xsl:template>


  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc><p>We need the backslash and two curly braces to insert LaTeX
      commands into the output, so these characters need to replaced when they
      are found in running text. </p></desc>
  </doc>
  <xsl:function name="tei:escapeCharsVerbatim" as="xs:string">
    <xsl:param name="letters"/>
    <xsl:value-of select="translate($letters, '\{}','⃥❴❵')"/>
  </xsl:function>

  <xsl:function name="tei:escapeChars" as="xs:string">
    <xsl:param name="letters"/>
      <xsl:value-of
	  select="replace(replace(replace(replace(translate($letters,'&#10;',' '), 
		  '\\','\\textbackslash '),
		  '\{','\\{'),
		  '\}','\\}'),
		  '~','\\textasciitilde ')"/>
  </xsl:function>


</xsl:stylesheet>
