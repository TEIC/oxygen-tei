<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cals="http://www.oasis-open.org/specs/tm9901"
                xmlns:tei="http://www.tei-c.org/ns/1.0"
                xmlns:iso="http://www.iso.org/ns/1.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:ve="http://schemas.openxmlformats.org/markup-compatibility/2006"
                xmlns:o="urn:schemas-microsoft-com:office:office"
                xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
                xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
                xmlns:v="urn:schemas-microsoft-com:vml"
                xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
                xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
                xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"
                xmlns:w10="urn:schemas-microsoft-com:office:word"
                xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
                xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml"
                xmlns:mml="http://www.w3.org/1998/Math/MathML"
                xmlns:tbx="http://www.lisa.org/TBX-Specification.33.0.html"
                xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture"
		xmlns:teidocx="http://www.tei-c.org/ns/teidocx/1.0"
                version="2.0"
                exclude-result-prefixes="cals ve o r m v wp w10 w wne mml tbx iso tei a xs pic fn">
    
    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
      <desc>
         <p> TEI Utility stylesheet for making Word docx files
        from TEI XML (see docx-tei.xsl)</p>
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
         <p>Id: $Id: functions.xsl 11232 2012-12-18 18:06:19Z rahtz $</p>
         <p>Copyright: 2013, TEI Consortium</p>
      </desc></doc>

    
        <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>Defines whether or not a word paragraph is a first level heading.</desc></doc>
    <xsl:function name="tei:is-firstlevel-heading" as="xs:boolean">
        <xsl:param name="p"/>
        
        <xsl:choose>
            <xsl:when test="$p[w:pPr/w:pStyle/@w:val='heading 1']">true</xsl:when>
            <xsl:when test="$p[w:pPr/w:pStyle/@w:val='Heading 1']">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
        <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>Defines whether or not a word paragraph is a  heading.</desc></doc>
    <xsl:function name="tei:is-heading" as="xs:boolean">
        <xsl:param name="p"/>
	<xsl:variable name="s" select="$p/w:pPr/w:pStyle/@w:val"/>
        <xsl:choose>
            <xsl:when test="matches($s,'[Hh]eading.+')">true</xsl:when>
            <xsl:when test="matches($s,'[Cc]aption')">true</xsl:when>
            <xsl:when test="matches($s,'Figure[ ]?title')">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:function>

        <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>Defines whether or not a word paragraph is a list element.</desc></doc>
    <xsl:function name="tei:is-list" as="xs:boolean">
        <xsl:param name="p"/>        
        <xsl:choose>
            <xsl:when test="$p[contains(w:pPr/w:pStyle/@w:val,'List')]">true</xsl:when>
            <xsl:when
		test="$p[w:pPr/w:pStyle/@w:val='dl']">true</xsl:when>
	    <xsl:when test="$p/w:pPr/w:numPr[not(w:ins)]">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:function>

        <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>Defines whether or not a word paragraph is a table of contents.</desc></doc>
    <xsl:function name="tei:is-toc" as="xs:boolean">
        <xsl:param name="p"/>        
        <xsl:choose>
            <xsl:when test="$p[contains(w:pPr/w:pStyle/@w:val,'toc')]">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:function>

        <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>Defines whether or not a word paragraph is a figure element.</desc></doc>
    <xsl:function name="tei:is-figure" as="xs:boolean">
        <xsl:param name="p"/>        
        <xsl:choose>
            <xsl:when test="$p[matches(w:pPr/w:pStyle/@w:val,'[Ff]igure')]">true</xsl:when>
            <xsl:when test="$p[w:r/w:drawing and not(w:r/w:t)]">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:function>

        <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>Defines whether or not a word paragraph is a caption.</desc></doc>
    <xsl:function name="tei:is-caption" as="xs:boolean">
        <xsl:param name="p"/>        
        <xsl:choose>
            <xsl:when test="$p[matches(w:pPr/w:pStyle/@w:val,'[Cc]aption')]">true</xsl:when>
            <xsl:when test="$p[matches(w:pPr/w:pStyle/@w:val,'Figuretitle')]">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    

        <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>Defines whether or not a word paragraph is a line of poetry.</desc></doc>
    <xsl:function name="tei:is-line" as="xs:boolean">
        <xsl:param name="p"/>        
        <xsl:choose>
            <xsl:when test="$p[w:pPr/w:pStyle/@w:val='tei_l']">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:function>

        <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>Defines whether or not a word paragraph is gloss list.</desc></doc>
    <xsl:function name="tei:is-glosslist" as="xs:boolean">
        <xsl:param name="p"/>        
        <xsl:choose>
            <xsl:when test="$p[w:pPr/w:pStyle/@w:val='dl']">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
        <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>Is given a header style and returns the style for the next level header.</desc></doc>
    <xsl:function name="tei:get-nextlevel-header" as="xs:string">
        <xsl:param name="current-header"/>
        <xsl:value-of select="translate($current-header,'12345678','23456789')"/>
    </xsl:function>
    

        <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>Returns a listtype for a given stylename (return empty string to figure it out dynamically).</desc></doc>

    <xsl:function name="tei:get-listtype" as="xs:string">
        <xsl:param name="style"/>
        <xsl:choose>
            <xsl:when test="starts-with($style,'dl')">
                <xsl:text>gloss</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($style,$ListBullet)">
                <xsl:text>unordered</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($style,$ListContinue)">
                <xsl:text>unordered</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($style,$ListNumber)">
                <xsl:text>ordered</xsl:text>
            </xsl:when>
            <xsl:when test="$style=$List">
                <xsl:text>ordered</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>


</xsl:stylesheet>
