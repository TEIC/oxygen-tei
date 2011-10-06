<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.tei-c.org/ns/1.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:iso="http://www.iso.org/ns/1.0"
                xmlns:teidocx="http://www.tei-c.org/ns/teidocx/1.0"
                xmlns:tei="http://www.tei-c.org/ns/1.0"
                xmlns:ve="http://schemas.openxmlformats.org/markup-compatibility/2006"
                xmlns:o="urn:schemas-microsoft-com:office:office"
                xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
                xmlns:rel="http://schemas.openxmlformats.org/package/2006/relationships"
                xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
                xmlns:v="urn:schemas-microsoft-com:vml"
                xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
                xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"
                xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture"
                xmlns:w10="urn:schemas-microsoft-com:office:word"
                xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
                xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml"
                xmlns:mml="http://www.w3.org/1998/Math/MathML"
                xmlns:tbx="http://www.lisa.org/TBX-Specification.33.0.html"
                version="2.0"
                exclude-result-prefixes="ve o r m v wp w10 w wne mml tbx pic rel a         tei teidocx xs iso">
    <!-- import base conversion style -->


    <xsl:import href="../../default/docx/from.xsl"/>
  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
    <desc>
      <p> This library is free software; you can redistribute it and/or modify it under the
      terms of the GNU Lesser General Public License as published by the Free Software Foundation;
      either version 2.1 of the License, or (at your option) any later version. This library is
      distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
      implied warranty of MAINTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
      General Public License for more details. You should have received a copy of the GNU Lesser
      General Public License along with this library; if not, write to the Free Software Foundation,
      Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA </p>
      <p>Author: See AUTHORS</p>
      <p>Id: $Id: from.xsl 9108 2011-07-15 14:13:47Z rahtz $</p>
      <p>Copyright: 2008, TEI Consortium</p>
    </desc>
  </doc>
    
    <xsl:template match="@rend[.='ITLP_Body_Text']" mode="pass2"/>

    <xsl:template match="@rend[.='ITLP Body Text']" mode="pass2"/>

    <xsl:template match="tei:cell/@rend[.='ITLP_Body_Text background-color(D9D9D9)']" mode="pass2">
      <xsl:attribute name="role">label</xsl:attribute>
    </xsl:template>

    <xsl:template match="w:p[w:pPr/w:pStyle/@w:val='ITLP Caption']" mode="paragraph">
        <head>
	  <xsl:apply-templates/>
        </head>
    </xsl:template>

    <xsl:template match="w:p[w:pPr/w:pStyle/@w:val='ITLP Table Heading']" mode="paragraph">
        <head>
	  <xsl:apply-templates/>
        </head>
    </xsl:template>
    <xsl:template match="w:p[w:pPr/w:pStyle/@w:val='ITLP Ex Tasks Bulleted']" mode="paragraph">
        <item>
	  <xsl:apply-templates/>
        </item>
    </xsl:template>
    <xsl:template match="w:p[w:pPr/w:pStyle/@w:val='ITLP BodyText Bulletted']" mode="paragraph">
        <item>
	  <xsl:apply-templates/>
        </item>
    </xsl:template>

    <doc type="function" xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
      <desc>
	Returns a listtype for a given stylename (return empty string to
	figure it out dynamically)
      </desc>
    </doc>
    
    <xsl:function name="teidocx:get-listtype" as="xs:string">
      <xsl:param name="style"/>
      <xsl:choose>
	<xsl:when test="$style='ITLP BodyText Bulletted'">
	  <xsl:text>unordered</xsl:text>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:text/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:function>

    <doc type="function" xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
      <desc>
	Defines whether or not a word paragraph is a first level heading 
      </desc>
    </doc>
    
    <xsl:function name="teidocx:is-firstlevel-heading" as="xs:boolean">
      <xsl:param name="p"/>      
      <xsl:variable name="s" select="$p/w:pPr/w:pStyle/@w:val"/>
      <xsl:choose>
	<xsl:when test="$s='heading 1'">true</xsl:when>
	<xsl:when test="$s='Heading 1'">true</xsl:when>
	<xsl:when test="$s='Heading1'">true</xsl:when>
	<xsl:when test="$s='ITLP H1'">true</xsl:when>
	<xsl:when test="$s='ITLP Anonymous Heading 1'">true</xsl:when>
	<xsl:otherwise>false</xsl:otherwise>
      </xsl:choose>
    </xsl:function>
    
    <doc type="function" xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
      <desc>
	Defines whether or not a word paragraph is a  heading 
      </desc>
    </doc>
    
    <xsl:function name="teidocx:is-heading" as="xs:boolean">
      <xsl:param name="p"/>
      <xsl:variable name="s" select="$p/w:pPr/w:pStyle/@w:val"/>
      <xsl:choose>
	<xsl:when test="$s=''">false</xsl:when>
	<xsl:when test="starts-with($s,'heading')">true</xsl:when>
	<xsl:when test="starts-with($s,'Heading')">true</xsl:when>
	<xsl:when test="$s='ITLP Anonymous Heading 1'">true</xsl:when>
	<xsl:when test="$s='ITLP Anonymous Heading 2'">true</xsl:when>
	<xsl:when test="$s='ITLP H1'">true</xsl:when>
	<xsl:when test="$s='ITLP H2'">true</xsl:when>
	<xsl:when test="$s='ITLP H3'">true</xsl:when>
	<xsl:when test="$s='Heading1'">true</xsl:when>
	<xsl:when test="$s='Heading2'">true</xsl:when>
	<xsl:when test="$s='Heading3'">true</xsl:when>
	<xsl:when test="$s='Heading4'">true</xsl:when>
	<xsl:when test="$s='Heading5'">true</xsl:when>
	<xsl:when test="$s='Heading6'">true</xsl:when>
	<xsl:when test="$s='Heading7'">true</xsl:when>
	<xsl:when test="$s='Heading8'">true</xsl:when>
	<xsl:when test="$s='Heading9'">true</xsl:when>
	<xsl:when test="$s='heading 1'">true</xsl:when>
	<xsl:when test="$s='heading 2'">true</xsl:when>
	<xsl:when test="$s='heading 3'">true</xsl:when>
	<xsl:when test="$s='heading 4'">true</xsl:when>
	<xsl:when test="$s='heading 5'">true</xsl:when>
	<xsl:when test="$s='heading 6'">true</xsl:when>
	<xsl:when test="$s='heading 7'">true</xsl:when>
	<xsl:when test="$s='heading 8'">true</xsl:when>
	<xsl:when test="$s='heading 9'">true</xsl:when>
	<xsl:otherwise>false</xsl:otherwise>
      </xsl:choose>
    </xsl:function>

    <xsl:function name="teidocx:heading-level" as="xs:string">
      <xsl:param name="p"/>
      <xsl:analyze-string select="$p/w:pPr/w:pStyle/@w:val" regex="[^0-9]*([0-9])">
	<xsl:matching-substring>
	  <xsl:value-of select="number(regex-group(1))"/>
	</xsl:matching-substring>
	<xsl:non-matching-substring>
	  <xsl:text>1</xsl:text>
	</xsl:non-matching-substring>
      </xsl:analyze-string>
    </xsl:function>
    
    <doc type="function" xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
      <desc>
	Is given a header style and returns the style for the next level
	header 
      </desc>
    </doc>
    
    <xsl:function name="teidocx:get-nextlevel-header" as="xs:string">
      <xsl:param name="current-header"/>
      <xsl:value-of select="translate($current-header,'12345678','23456789')"/>
    </xsl:function>
   
    <doc type="function" xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
      <desc>Defines whether or not a word paragraph is a list element.</desc>
    </doc>
   
    <xsl:function name="teidocx:is-list" as="xs:boolean">
        <xsl:param name="p"/>        
        <xsl:choose>
            <xsl:when
		test="$p[contains(w:pPr/w:pStyle/@w:val,'List')]">true</xsl:when>
            <xsl:when test="$p[contains(w:pPr/w:pStyle/@w:val,'Bulletted')]">true</xsl:when>
            <xsl:when test="$p[contains(w:pPr/w:pStyle/@w:val,'Bulleted')]">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <doc type="function" xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
      <desc>Override default behaviour for a styled paragraph</desc>
    </doc>

    <xsl:template match="w:p[w:pPr/w:pStyle/@w:val='ITLP Ex Explanation']"
		  mode="paragraph">
      <p rend="ExampleExplanation">
	<xsl:apply-templates/>
      </p>
    </xsl:template>


    <xsl:template match="w:p[w:pPr/w:pStyle/@w:val='ITLP Task Text']"
		  mode="paragraph">
      <p rend="ExampleTask">
	<xsl:apply-templates/>
      </p>
    </xsl:template>

    <xsl:template match="w:p[w:pPr/w:pStyle/@w:val='ITLP Step Text']"
		  mode="paragraph">
      <p rend="ExampleStep">
	<xsl:apply-templates/>
      </p>
    </xsl:template>


    <xsl:template match="w:p[w:pPr/w:pStyle/@w:val='ITLP Ex Heading']"
		  mode="paragraph">
      <p rend="ExampleHeading">
	<xsl:apply-templates/>
      </p>
    </xsl:template>

    <doc type="function" xmlns="http://www.oxygenxml.com/ns/doc/xsl"  >
      <desc>Override default behaviour for a styled text run</desc>
    </doc>
    <xsl:template match="w:r[w:rPr/w:rStyle/@w:val='ITLP FileSpec']">
      <code rend="FileSpec">
	<xsl:apply-templates/>
      </code>
    </xsl:template>
    <xsl:template match="w:r[w:rPr/w:rStyle/@w:val='ITLP Button']">
      <code rend="Button">
	<xsl:apply-templates/>
      </code>
    </xsl:template>

    <xsl:template match="w:r[w:rPr/w:rStyle/@w:val='ITLP Input']">
      <code rend="Input">
	<xsl:apply-templates/>
      </code>
    </xsl:template>

    <xsl:template match="w:r[w:rPr/w:rStyle/@w:val='ITLP Key']">
      <code rend="Key">
	<xsl:apply-templates/>
      </code>
    </xsl:template>

    <xsl:template match="w:r[w:rPr/w:rStyle/@w:val='ITLP Label']">
      <code rend="Label">
	<xsl:apply-templates/>
      </code>
    </xsl:template>

    <xsl:template match="w:r[w:rPr/w:rStyle/@w:val='ITLP Menu']">
      <code rend="Menu">
	<xsl:apply-templates/>
      </code>
    </xsl:template>

    <xsl:template match="w:r[w:rPr/w:rStyle/@w:val='ITLP Software']">
      <code rend="Software">
	<xsl:apply-templates/>
      </code>
    </xsl:template>

  </xsl:stylesheet>
  
