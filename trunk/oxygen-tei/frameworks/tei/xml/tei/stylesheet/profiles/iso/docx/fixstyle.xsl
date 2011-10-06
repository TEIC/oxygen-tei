<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
    exclude-result-prefixes="w">
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
      <p>Id: $Id: fixstyle.xsl 7952 2010-08-12 21:14:51Z rahtz $</p>
      <p>Copyright: 2008, TEI Consortium</p>
    </desc>
  </doc>

  <xsl:output  encoding="UTF-8" standalone="yes" method="xml"/>

  <xsl:template match="/w:styles">
    <w:styles xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
      xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
      
      <!-- copy all existing elements -->
      <xsl:apply-templates/>
      
      <!-- create some new styles -->
      <xsl:call-template name="create-egXML"/>
      
    </w:styles>
  </xsl:template>

  <!-- identity transform -->
  
  <xsl:template match="@*|text()|comment()|processing-instruction()" >
    <xsl:copy-of select="."/>
  </xsl:template>
  
  <xsl:template match="*" >
    <xsl:copy>
      <xsl:apply-templates select="*|@*|processing-instruction()|comment()|text()" 
			   />
    </xsl:copy>
  </xsl:template>

  <xsl:template match="w:style[@w:styleId='Tabletitle']//w:numId">
    <w:numId w:val="5"/>
  </xsl:template>
  
  <xsl:template match="w:style[@w:styleId='Bibliography']//w:numId">
    <w:numId w:val="6"/>
  </xsl:template>

  <xsl:template match="w:style[@w:styleId='Figuretitle']//w:numId">
    <w:numId w:val="7"/>
  </xsl:template>
  
  <xsl:template match="w:spacing">
    <xsl:choose>
      <xsl:when test="ancestor::w:style[@w:styleId='Heading1']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='Heading2']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='Heading3']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='Heading4']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='Heading5']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='Heading6']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='Heading7']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='Heading8']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='Heading9']"/>
      
      <xsl:when test="ancestor::w:style[@w:styleId='ANNEX']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='a2']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='a3']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='a4']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='a5']"/>
      <xsl:when test="ancestor::w:style[@w:styleId='a6']"/>
      
      <xsl:otherwise>
        <w:spacing>
          <xsl:apply-templates
              select="*|@*|processing-instruction()|comment()|text()"
          />
        </w:spacing>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="w:numPr">
    <xsl:choose>
      <xsl:when test="ancestor::w:style[@w:styleId='Heading1']">
        <w:numPr>
          <w:numId w:val="1"/>
        </w:numPr>
      </xsl:when>
      
      <xsl:when test="ancestor::w:style[@w:styleId='ANNEX']">
        <w:numPr>
          <w:numId w:val="3"/>
        </w:numPr>
      </xsl:when>
      <xsl:when test="ancestor::w:style[@w:styleId='a2']">
        <w:numPr>
          <w:ilvl w:val="1"/>
          <w:numId w:val="3"/>
        </w:numPr>
      </xsl:when>
      <xsl:when test="ancestor::w:style[@w:styleId='a3']">
        <w:numPr>
          <w:ilvl w:val="2"/>
          <w:numId w:val="3"/>
        </w:numPr>
      </xsl:when>
      <xsl:when test="ancestor::w:style[@w:styleId='a4']">
        <w:numPr>
          <w:ilvl w:val="3"/>
          <w:numId w:val="3"/>
        </w:numPr>
      </xsl:when>
      <xsl:when test="ancestor::w:style[@w:styleId='a5']">
        <w:numPr>
          <w:ilvl w:val="4"/>
          <w:numId w:val="3"/>
        </w:numPr>
      </xsl:when>
      <xsl:when test="ancestor::w:style[@w:styleId='a6']">
        <w:numPr>
          <w:ilvl w:val="5"/>
          <w:numId w:val="3"/>
        </w:numPr>
      </xsl:when>
      
      <xsl:when test="ancestor::w:style[@w:styleId='ListNumber']">
        <w:numPr>
          <w:numId w:val="8"/>
        </w:numPr>
      </xsl:when>

      <xsl:when test="ancestor::w:style[@w:styleId='ListNumber1']">
        <w:numPr>
          <w:ilvl w:val="1"/>
          <w:numId w:val="8"/>
        </w:numPr>
      </xsl:when>
      
      <xsl:when test="ancestor::w:style[@w:styleId='ListNumber2']">
        <w:numPr>
          <w:ilvl w:val="2"/>
          <w:numId w:val="8"/>
        </w:numPr>
      </xsl:when>
      
      <xsl:when test="ancestor::w:style[@w:styleId='ListNumber3']">
        <w:numPr>
          <w:ilvl w:val="3"/>
          <w:numId w:val="8"/>
        </w:numPr>
      </xsl:when>
      
      <xsl:when test="ancestor::w:style[@w:styleId='ListNumber4']">
        <w:numPr>
          <w:ilvl w:val="4"/>
          <w:numId w:val="8"/>
        </w:numPr>
      </xsl:when>
      
      <xsl:when test="ancestor::w:style[@w:styleId='ListNumber5']">
        <w:numPr>
          <w:ilvl w:val="5"/>
          <w:numId w:val="8"/>
        </w:numPr>
      </xsl:when>
      
      <xsl:when test="ancestor::w:style[@w:styleId='ListBullet']">
        <w:numPr>
          <w:numId w:val="9"/>
        </w:numPr>
      </xsl:when>
      
      

      <xsl:otherwise>
      	<w:numPr>
      	  <xsl:apply-templates
      	      select="*|@*|processing-instruction()|comment()|text()"
      	      />
      	</w:numPr>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- new styles -->
  <xsl:template name="create-egXML">
    <w:style w:type="paragraph" w:customStyle="1" w:styleId="egXML">
      <w:name w:val="egXML"/>
      <w:basedOn w:val="Normal"/>
      <w:qFormat/>
      <w:rPr>
        <w:rFonts w:ascii="Courier" w:hAnsi="Courier"/>
        <w:sz w:val="20"/>
      </w:rPr>
    </w:style>
  </xsl:template>
</xsl:stylesheet>
