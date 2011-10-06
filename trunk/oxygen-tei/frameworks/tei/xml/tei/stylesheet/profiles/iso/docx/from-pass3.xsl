<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.tei-c.org/ns/1.0"
                xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"
                xmlns:cals="http://www.oasis-open.org/specs/tm9901"
                xmlns:iso="http://www.iso.org/ns/1.0"
                xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
                xmlns:mml="http://www.w3.org/1998/Math/MathML"
                xmlns:o="urn:schemas-microsoft-com:office:office"
                xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture"
                xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
                xmlns:rel="http://schemas.openxmlformats.org/package/2006/relationships"
                xmlns:tbx="http://www.lisa.org/TBX-Specification.33.0.html"
                xmlns:tei="http://www.tei-c.org/ns/1.0"
                xmlns:teidocx="http://www.tei-c.org/ns/teidocx/1.0"
                xmlns:v="urn:schemas-microsoft-com:vml"
                xmlns:ve="http://schemas.openxmlformats.org/markup-compatibility/2006"
                xmlns:w10="urn:schemas-microsoft-com:office:word"
                xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
                xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml"
                xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
                xmlns:custprops="http://schemas.openxmlformats.org/officeDocument/2006/custom-properties"
                xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes"
                xmlns:html="http://www.w3.org/1999/xhtml"
                version="2.0"
                exclude-result-prefixes="a pic rel ve o teidocx r m v wp w10 w wne mml vt cals tbx iso custprops">
  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
    <desc>
      <p> TEI stylesheet for simplifying TEI ODD markup </p>
      <p> This library is free software; you can redistribute it and/or modify it under the
      terms of the GNU Lesser General Public License as published by the Free Software Foundation;
      either version 2.1 of the License, or (at your option) any later version. This library is
      distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
      implied warranty of MAINTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
      General Public License for more details. You should have received a copy of the GNU Lesser
      General Public License along with this library; if not, write to the Free Software Foundation,
      Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA </p>
      <p>Author: See AUTHORS</p>
      <p>Id: $Id: from-pass3.xsl 7952 2010-08-12 21:14:51Z rahtz $</p>
      <p>Copyright: 2008, TEI Consortium</p>
    </desc>
  </doc>

    <!-- ******************************************************************************************* -->
    <!-- third stage processing -->

  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
    <desc>Add ID to <gi>termEntry</gi></desc></doc>

  <xsl:template match="tbx:termEntry[starts-with(@id,'autoTermNum')]" mode="pass3">
    <xsl:copy>
      <xsl:copy-of select="@xml:id"/>
      <xsl:attribute name="id">
	<xsl:value-of select="substring-before(@id,'_')"/>
	<xsl:text>_</xsl:text>
	<xsl:call-template name="numberTerm"/>
      </xsl:attribute>
      <xsl:apply-templates mode="pass3"/>	
    </xsl:copy>
  </xsl:template>

  <xsl:template name="numberTerm">
    <xsl:for-each select="ancestor::tei:div[1]">
      <xsl:choose>
	<xsl:when test="ancestor::tei:body">
	  <xsl:number count="tei:div" from="tei:body" format="1.1.1" level="multiple"/>
	</xsl:when>
	<xsl:when test="ancestor::tei:back">
	  <xsl:number count="tei:div" from="tei:back" format="A.1.1" level="multiple"/>
	</xsl:when>
      </xsl:choose>
    </xsl:for-each>
    <xsl:text>.</xsl:text>
    <xsl:number level="any" from="tei:div" count="tbx:termEntry"/>
  </xsl:template>

    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
    <desc>Add ID to <gi>term</gi></desc></doc>

   <xsl:template match="tbx:term" mode="pass3">
      <xsl:copy>
         <xsl:attribute name="id">
	   <xsl:for-each select="ancestor::tbx:termEntry">
	     <xsl:choose>
	       <xsl:when test="starts-with(@id,'autoTerm')">
		 <xsl:value-of select="substring-before(@id,'_')"/>
		 <xsl:text>_</xsl:text>	   
		 <xsl:call-template name="numberTerm"/>
	       </xsl:when>
	       <xsl:otherwise>
		 <xsl:value-of select="@id"/>
	       </xsl:otherwise>
	     </xsl:choose>
	   </xsl:for-each>
	   <xsl:text>-</xsl:text>
	   <xsl:number level="any" from="tbx:termEntry"/>
	 </xsl:attribute>
	 <xsl:apply-templates mode="pass3"
			      select="text()|comment()|processing-instruction()|*|@*"/>
      </xsl:copy>
   </xsl:template>

    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
    <desc>zap placeholder for noteSymbol </desc></doc>
   <xsl:template match="tbx:descripGrp[tbx:descrip/@type='symbol']"
		 mode="pass3"/>

    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
    <desc>Move noteSymbol into the right place</desc></doc>
   <xsl:template
       match="tbx:termGrp[tbx:termNote='symbol-admn-sts']"
       mode="pass3">
     <xsl:copy>
       <xsl:apply-templates mode="pass3"
			    select="text()|comment()|processing-instruction()|*|@*"/>
     </xsl:copy>
     <xsl:for-each
	 select="ancestor::tbx:termEntry/tbx:descripGrp[tbx:descrip/@type='symbol']/tbx:note">
       <xsl:copy-of select="."/>
     </xsl:for-each>
   </xsl:template>
   
   <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
   <desc>Clean up leftover markup in source field</desc></doc>
   <xsl:template match="tei:hi[@rend='source']/text()" mode="pass3">
     <xsl:choose>
       <xsl:when test=".=']'"/>
       <xsl:when test=".='[SOURCE: '"/>
       <xsl:when test="starts-with(.,'[SOURCE: ')">
	 <xsl:analyze-string select="replace(.,'\[SOURCE: ','')" regex="\]$">
	   <xsl:matching-substring>
	   </xsl:matching-substring>
	   <xsl:non-matching-substring>
	     <xsl:value-of select="."/>
	   </xsl:non-matching-substring>
	 </xsl:analyze-string>
       </xsl:when>
       <xsl:otherwise>
	 <xsl:value-of select="."/>
       </xsl:otherwise>
     </xsl:choose>
   </xsl:template>

   <xsl:template match="tei:hi[@rend='source']" mode="pass3">
     <xsl:apply-templates mode="pass3"/>
   </xsl:template>

    <xsl:template match="@*|comment()|processing-instruction()|text()" mode="pass3">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template match="*" mode="pass3">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()" mode="pass3"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>