<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"
		xmlns:rel="http://schemas.openxmlformats.org/package/2006/relationships"
                xmlns:cals="http://www.oasis-open.org/specs/tm9901"
                xmlns:contypes="http://schemas.openxmlformats.org/package/2006/content-types"
                xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:dcmitype="http://purl.org/dc/dcmitype/"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:iso="http://www.iso.org/ns/1.0"
                xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
                xmlns:mml="http://www.w3.org/1998/Math/MathML"
                xmlns:o="urn:schemas-microsoft-com:office:office"
                xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture"
                xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
                xmlns:tbx="http://www.lisa.org/TBX-Specification.33.0.html"
                xmlns:tei="http://www.tei-c.org/ns/1.0"
                xmlns:teidocx="http://www.tei-c.org/ns/teidocx/1.0"
                xmlns:teix="http://www.tei-c.org/ns/Examples"
                xmlns:v="urn:schemas-microsoft-com:vml"
                xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
                xmlns:ve="http://schemas.openxmlformats.org/markup-compatibility/2006"
                xmlns:w10="urn:schemas-microsoft-com:office:word"
                xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
                xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml"
                xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="2.0"
                exclude-result-prefixes="cp ve o r m v wp w10 w wne mml tbx iso   rel  tei a xs pic fn xsi dc dcterms dcmitype     contypes teidocx teix html cals">
    
    
    
    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
      <desc>
         <p> TEI stylesheet for making Word docx files from TEI XML </p>
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
         <p>Id: $Id: maths.xsl 9043 2011-07-03 22:14:48Z rahtz $</p>
         <p>Copyright: 2008, TEI Consortium</p>
      </desc>
   </doc>

    <xsl:template match="m:oMath">
        <xsl:apply-templates select="." mode="iden"/>
    </xsl:template>
    
    
    <xsl:template match="mml:math">
      <oMath xmlns="http://schemas.openxmlformats.org/officeDocument/2006/math">
	        <xsl:apply-templates mode="mml"/>
      </oMath>
    </xsl:template>
    
    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>
        Process Word objects
    </desc>
   </doc>
   <xsl:template match="w:object">
     <xsl:variable name="renderingProperties">
       <xsl:for-each select="..">
	 <xsl:call-template name="applyRend"/>
       </xsl:for-each>
     </xsl:variable>
     <w:r>
       <xsl:if test="$renderingProperties/*">
	 <w:rPr>
	   <xsl:copy-of select="$renderingProperties"/>
	 </w:rPr>
       </xsl:if>
       <xsl:copy>
	 <xsl:apply-templates mode="iden"/>
       </xsl:copy>
     </w:r>
   </xsl:template>

    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>
        Image data 
    </desc>
   </doc>
    <xsl:template match="v:imagedata" mode="iden">
        <xsl:variable name="current" select="@r:id"/>
	<xsl:copy>
	    <!-- override r:id -->
            <xsl:attribute name="r:id">
	      <xsl:choose>
		<xsl:when test="$isofreestanding='true'">
		  <xsl:variable name="me" select="generate-id()"/>
		  <xsl:for-each select="key('IMAGEDATA',1)">
		    <xsl:if test="generate-id()=$me">
		      <xsl:value-of select="concat('rId', string(1000 + position()))"/>
		    </xsl:if>
		  </xsl:for-each>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:value-of select="document(concat($wordDirectory,'/word/_rels/document.xml.rels'))//rel:Relationship[@Target=$current]/@Id"/>
		</xsl:otherwise>
	      </xsl:choose>
            </xsl:attribute>
	</xsl:copy>
    </xsl:template>
    
    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>
        OLE objects
    </desc>
   </doc>
    <xsl:template match="o:OLEObject" mode="iden">
        <xsl:variable name="current" select="@r:id"/>
        <xsl:copy>
            <!-- copy all attributes -->
            <xsl:copy-of select="@*"/>
            <!-- set rId -->
            <xsl:attribute name="r:id">
	      <xsl:choose>
		<xsl:when test="$isofreestanding='true'">
		  <xsl:variable name="me" select="generate-id()"/>
		  <xsl:for-each select="key('OLEOBJECTS',1)">
		    <xsl:if test="generate-id()=$me">
		      <xsl:value-of select="concat('rId', string(2000 + position()))"/>
		    </xsl:if>
		  </xsl:for-each>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:value-of select="document(concat($wordDirectory,'/word/_rels/document.xml.rels'))//rel:Relationship[@Target=$current]/@Id"/>
		</xsl:otherwise>
	      </xsl:choose>
            </xsl:attribute>
	</xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>
