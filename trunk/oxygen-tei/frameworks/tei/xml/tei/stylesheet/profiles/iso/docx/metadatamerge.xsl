<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"
                xmlns:cals="http://www.oasis-open.org/specs/tm9901"
                xmlns:iso="http://www.iso.org/ns/1.0"
                xmlns:its="http://www.w3.org/2005/11/its"
                xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
                xmlns:mml="http://www.w3.org/1998/Math/MathML"
                xmlns:o="urn:schemas-microsoft-com:office:office"
                xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture"
                xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
                xmlns:tbx="http://www.lisa.org/TBX-Specification.33.0.html"
                xmlns:tei="http://www.tei-c.org/ns/1.0"
                xmlns:teidocx="http://www.tei-c.org/ns/teidocx/1.0"
                xmlns:v="urn:schemas-microsoft-com:vml"
                xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
                xmlns:ve="http://schemas.openxmlformats.org/markup-compatibility/2006"
                xmlns:w10="urn:schemas-microsoft-com:office:word"
                xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
                xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml"
                xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="2.0"
                exclude-result-prefixes="teidocx cals ve o r m v wp w10 w wne mml tbx iso tei a xs pic fn its">

	  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
      <desc>
         <p>TEI stylesheet to convert TEI XML to Word DOCX XML.</p>
         <p>
			This library is free software; you can redistribute it and/or
			modify it under the terms of the GNU Lesser General Public
			License as
			published by the Free Software Foundation; either
			version 2.1 of the
			License, or (at your option) any later version.

			This library is
			distributed in the hope that it will be useful,
			but WITHOUT ANY
			WARRANTY; without even the implied warranty of
			MERCHANTABILITY or
			FITNESS FOR A PARTICULAR PURPOSE. See the GNU
			Lesser General Public
			License for more details.

			You should have received a copy of the GNU
			Lesser General Public
			License along with this library; if not, write
			to the Free Software
			Foundation, Inc., 59 Temple Place, Suite 330,
			Boston, MA 02111-1307
			USA
  
      </p>
         <p>Author: See AUTHORS</p>
         <p>Id: $Id: metadatamerge.xsl 7952 2010-08-12 21:14:51Z rahtz $</p>
         <p>Copyright: 2008, TEI Consortium</p>
      </desc>
   </doc>

	  <xsl:param name="header-doc" as="item()+" required="yes"/>
	  <xsl:param name="debug">true</xsl:param>

	  <!-- identity transform -->
	<xsl:template match="@*|text()|comment()|processing-instruction()">
		    <xsl:copy-of select="."/>
	  </xsl:template>

	  <xsl:template match="*">
		    <xsl:copy>
			      <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
		    </xsl:copy>
	  </xsl:template>

	  <xsl:template match="tei:titleStmt">
	     <xsl:if test="$debug = 'true'">
		       <xsl:message>replace titleStmt</xsl:message>
		    </xsl:if>
		    <xsl:copy-of select="$header-doc//tei:titleStmt"/>
	  </xsl:template>

	  <xsl:template match="tei:publicationStmt">
	     <xsl:if test="$debug = 'true'">
		       <xsl:message>replace publicationStmt</xsl:message>
		    </xsl:if>
		    <xsl:copy-of select="$header-doc//tei:publicationStmt"/>
	  </xsl:template>

  <xsl:template match="tei:encodingDesc">
      <xsl:copy>
	        <xsl:copy-of select="$header-doc//tei:appInfo"/>
	        <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()"/>
      </xsl:copy>
  </xsl:template>

  <xsl:template match="tei:appInfo">
	    <xsl:variable name="ident" select="tei:application/@ident"/>		
	    <xsl:choose>
		      <xsl:when test="$header-doc//tei:appInfo[tei:application/@ident=$ident]">
		         <xsl:if test="$debug = 'true'">
	             <xsl:message select="concat('replace appInfo with ident: ',$ident[1])"/>
	          </xsl:if>
		      </xsl:when>
		      <xsl:otherwise>
			        <xsl:copy-of select="."/>
		      </xsl:otherwise>
	    </xsl:choose>
  </xsl:template>
	
  <!-- currently we only allow one foreword -->
  <xsl:template match="tei:front/tei:div[@type='foreword']">
	    <xsl:variable name="currForeword" select="."/>
	    <xsl:variable name="templateForewordText">The boilerplate text and project metadata</xsl:variable>
	    <xsl:choose>
		      <xsl:when test="$header-doc//tei:front/tei:div[@type='foreword'] and not(contains($currForeword,$templateForewordText))">
		         <tei:div type="foreword">
		         <xsl:for-each select="$header-doc//tei:front/tei:div[@type='foreword']/*">
		            <xsl:choose>
		               <xsl:when test="name()='q'">
		                  <xsl:variable name="sdtName" select="@iso:meta"/>
		                  <xsl:message select="concat('found sdt element ', $sdtName)"/>
		                  <xsl:if test="$currForeword//*[@iso:meta=$sdtName]">
		                     <xsl:variable name="existingSdt" select="$currForeword//*[@iso:meta=$sdtName]"/>
		                     <xsl:if test="$debug = 'true'">
			                     <xsl:message select="concat('reuse existing sdt section ', $sdtName)"/>
			                     <xsl:message select="$existingSdt"/>
		                     </xsl:if>
		                     <xsl:copy-of select="$existingSdt"/>
		                  </xsl:if>
		               </xsl:when>
		               <xsl:otherwise>
		                 <xsl:if test="$debug = 'true'">
                       <xsl:message select="concat('use from new front ', .)"/>
                      </xsl:if>
		                 <xsl:copy-of select="."/>
		               </xsl:otherwise>
		            </xsl:choose>
		         </xsl:for-each>
		         </tei:div>
		      </xsl:when>
		      <xsl:when test="$header-doc//tei:front/tei:div[@type='foreword'] and contains($currForeword,$templateForewordText)">
		        <xsl:if test="$debug = 'true'">
               <xsl:message>found default foreword will replace all from new header</xsl:message>
             </xsl:if>
		        <xsl:copy-of select="$header-doc//tei:front/tei:div[@type='foreword']"/>
		      </xsl:when>
		      <xsl:otherwise>
			      <xsl:copy-of select="."/>
		      </xsl:otherwise>
	    </xsl:choose>
  </xsl:template>
	

</xsl:stylesheet>
