<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:prop="http://schemas.openxmlformats.org/officeDocument/2006/custom-properties"
                xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"
                xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:dcmitype="http://purl.org/dc/dcmitype/"
                xmlns:iso="http://www.iso.org/ns/1.0"
                xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
                xmlns:mml="http://www.w3.org/1998/Math/MathML"
                xmlns:mo="http://schemas.microsoft.com/office/mac/office/2008/main"
                xmlns:mv="urn:schemas-microsoft-com:mac:vml"
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
                
                xmlns="http://www.tei-c.org/ns/1.0"
                version="2.0"
                exclude-result-prefixes="a cp dc dcterms dcmitype prop     iso m mml mo mv o pic r rel     tbx tei teidocx v xs ve w10 w wne wp">
    
    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
      <desc>
         <p> TEI stylesheet for converting Word docx files to TEI </p>
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
         <p>Id: $Id: tei-templates.xsl 8953 2011-06-16 18:26:00Z rahtz $</p>
         <p>Copyright: 2008, TEI Consortium</p>
      </desc>
   </doc>
    
    
    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>
    	simple teiHeader. For a more sophisticated header, think about overriding
        this template
    </desc>
   </doc>
    <xsl:template name="create-tei-header">
        <teiHeader>
            <fileDesc>
                <titleStmt>
                    <title>
                        <xsl:call-template name="getDocTitle"/>
                    </title>
                    <author>
                        <xsl:call-template name="getDocAuthor"/>
                    </author>
                </titleStmt>
                <editionStmt>
                    <edition>
                        <date>
                            <xsl:call-template name="getDocDate"/>
                        </date>
                    </edition>
                </editionStmt>
                <publicationStmt>
                    <p>unknown</p>
                </publicationStmt>
                <sourceDesc>
                    <p>Converted from a Word document </p>
                </sourceDesc>
            </fileDesc>
	        <encodingDesc>
	           <xsl:call-template name="generateAppInfo"/>
	        </encodingDesc>
            <revisionDesc>
	           <change>
		             <date>
		                <xsl:text>$LastChangedDate: </xsl:text>
		                <xsl:value-of select="teidocx:whatsTheDate()"/>
		                <xsl:text>$</xsl:text>
		             </date>
			     <name>
			       <xsl:call-template name="getDocAuthor"/>
			     </name>
	           </change>
            </revisionDesc>
        </teiHeader>
    </xsl:template>
    
    <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
      <desc>
     generates a section heading. If you need something specific, feel free
        to overwrite this template
    </desc>
   </doc>
    <xsl:template name="generate-section-heading">
        <xsl:param name="Style"/>
        <head>
            <xsl:apply-templates/>
        </head>
    </xsl:template>
    
    
</xsl:stylesheet>