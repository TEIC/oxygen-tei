<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xd="http://www.pnp-software.com/XSLTdoc" xmlns:fotex="http://www.tug.org/fotex" xmlns:m="http://www.w3.org/1998/Math/MathML" xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0" xmlns:edate="http://exslt.org/dates-and-times" xmlns:estr="http://exslt.org/strings" xmlns:exsl="http://exslt.org/common" xmlns="http://www.w3.org/1999/XSL/Format" xmlns:rng="http://relaxng.org/ns/structure/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" xmlns:teix="http://www.tei-c.org/ns/Examples" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" extension-element-prefixes="exsl estr edate" exclude-result-prefixes="xd exsl estr edate a fotex rng tei teix" version="1.0">
  <xd:doc type="stylesheet">
    <xd:short>
    TEI stylesheet
    dealing  with elements from the
      figures module, making XSL-FO output.
      </xd:short>
    <xd:detail>
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   
   
      </xd:detail>
    <xd:author>See AUTHORS</xd:author>
    <xd:cvsId>$Id: figures.xsl 7900 2010-07-25 11:18:38Z rahtz $</xd:cvsId>
    <xd:copyright>2008, TEI Consortium</xd:copyright>
  </xd:doc>
  <xd:doc>
    <xd:short>Deal with elements in math mode (just copy them)</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="m:*|@*|comment()|processing-instruction()|text()" mode="math">
    <xsl:copy>
      <xsl:apply-templates mode="math" select="*|@*|processing-instruction()|text()"/>
    </xsl:copy>
  </xsl:template>
  <xd:doc>
    <xd:short>Process math elements</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="m:math">
    <m:math>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates mode="math"/>
    </m:math>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:cell</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:cell">
    <table-cell>
      <xsl:if test="@cols &gt; 1">
        <xsl:attribute name="number-columns-spanned">
          <xsl:value-of select="@cols"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@rows &gt; 1">
        <xsl:attribute name="number-rows-spanned">
          <xsl:value-of select="@rows"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:call-template name="cellProperties"/>
      <block>
        <xsl:choose>
          <xsl:when test="@role='label' or parent::tei:row[@role='label' or parent::tei:row[@role='header']]">
            <xsl:attribute name="font-weight">bold</xsl:attribute>
          </xsl:when>
        </xsl:choose>
        <xsl:apply-templates/>
      </block>
    </table-cell>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:figDesc</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:figDesc"/>
  <xd:doc>
    <xd:short>Process elements  tei:figure in display mode</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:figure">
    <xsl:choose>
      <xsl:when test="@rend='display' or tei:head or tei:p">
        <float>
          <xsl:call-template name="addID"/>
          <block text-align="center">
            <xsl:choose>
              <xsl:when test="@url or @entity">
                <xsl:call-template name="makePic"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates/>
              </xsl:otherwise>
            </xsl:choose>
          </block>
          <block>
            <xsl:call-template name="figureCaptionstyle"/>
            <xsl:call-template name="i18n">
              <xsl:with-param name="word">figureWord</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="calculateFigureNumber"/>
            <xsl:text>. </xsl:text>
            <xsl:apply-templates select="tei:head"/>
          </block>
        </float>
      </xsl:when>
      <xsl:otherwise>
	<block>
	  <xsl:choose>
	    <xsl:when test="@url or @entity">
	      <xsl:call-template name="makePic"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:apply-templates/>
	    </xsl:otherwise>
	  </xsl:choose>
	</block>
        <xsl:choose>
          <xsl:when test="$captionInlineFigures='true'">
            <block>
              <xsl:call-template name="figureCaptionstyle"/>
              <xsl:text>Figure </xsl:text>
              <xsl:call-template name="calculateFigureNumber"/>
              <xsl:text>. </xsl:text>
              <xsl:apply-templates select="tei:head"/>
            </block>
          </xsl:when>
          <xsl:otherwise>
            <xsl:if test="tei:head">
              <block text-align="center">
                <xsl:apply-templates select="tei:head"/>
              </block>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:figure</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:figure" mode="xref">
    <xsl:if test="$showFloatLabel">
      <xsl:call-template name="i18n">
        <xsl:with-param name="word">figureWord</xsl:with-param>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:call-template name="calculateFigureNumber"/>
    <xsl:if test="$showFloatHead='true' and tei:head">
      <xsl:text> (</xsl:text>
      <xsl:apply-templates select="tei:head"/>
      <xsl:text>)</xsl:text>
    </xsl:if>
    <xsl:if test="$xrefShowPage='true'">
    on page
    <page-number-citation><xsl:attribute name="ref-id"><xsl:choose><xsl:when test="@xml:id"><xsl:value-of select="@xml:id"/></xsl:when><xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise></xsl:choose></xsl:attribute></page-number-citation> 
    </xsl:if>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:formula</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:formula">
    <wrapper>
      <xsl:if test="@xml:id">
        <xsl:attribute name="id">
          <xsl:value-of select="@xml:id"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </wrapper>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:formula</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:formula" mode="xref">
    <xsl:number/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:formula[@type='display']/m:math</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:formula[@type='display']/m:math">
    <m:math display="block">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates mode="math"/>
    </m:math>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:formula[@type='subeqn']/m:math</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:formula[@type='subeqn']/m:math">
    <xsl:apply-templates mode="math"/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:graphic</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:graphic">
    <xsl:call-template name="makePic"/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:row[@role='header']</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:row[@role='header']">
    <xsl:text>&#10;</xsl:text>
    <table-header>
      <xsl:apply-templates select="tei:cell"/>
    </table-header>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:table</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:table" mode="xref">
    <xsl:if test="$showFloatLabel">
      <xsl:call-template name="i18n">
        <xsl:with-param name="word">tableWord</xsl:with-param>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:if test="$showFloatHead='true' and tei:head">
      <xsl:text> (</xsl:text>
      <xsl:apply-templates select="tei:head"/>
      <xsl:text>)</xsl:text>
    </xsl:if>
    <xsl:call-template name="calculateTableNumber"/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:table</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:table">
    <xsl:choose>
      <xsl:when test="@rend='eqnarray' and $foEngine='passivetex'">
        <fotex:eqnarray>
          <xsl:apply-templates select=".//tei:formula"/>
        </fotex:eqnarray>
      </xsl:when>
      <xsl:when test=".//tei:formula[@type='subeqn'] and $foEngine='passivetex'">
        <fotex:eqnarray>
          <xsl:apply-templates select=".//tei:formula"/>
        </fotex:eqnarray>
      </xsl:when>
      <xsl:when test="$inlineTables or @rend='inline'">
        <xsl:if test="tei:head">
          <block>
            <xsl:call-template name="tableCaptionstyle"/>
            <xsl:call-template name="addID"/>
            <xsl:if test="$makeTableCaption='true'">
              <xsl:call-template name="i18n">
                <xsl:with-param name="word">tableWord</xsl:with-param>
              </xsl:call-template>
              <xsl:text> </xsl:text>
              <xsl:call-template name="calculateTableNumber"/>
              <xsl:text>. </xsl:text>
            </xsl:if>
            <xsl:apply-templates select="tei:head"/>
          </block>
        </xsl:if>
        <xsl:call-template name="blockTable"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="floatTable"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tei:table[@rend='eqnarray']</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:table[@rend='eqnarray']">
    <xsl:choose>
      <xsl:when test="$foEngine='passivetex'">
        <fotex:eqnarray>
          <xsl:for-each select="tei:row">
            <xsl:apply-templates select=".//tei:formula"/>
            <xsl:if test="following-sibling::tei:row">
<!--        <character character="&#x2028;"/>-->
              <xsl:processing-instruction name="xmltex">\\</xsl:processing-instruction>
            </xsl:if>
          </xsl:for-each>
        </fotex:eqnarray>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="blockTable">
    <table text-align="{$tableAlign}" font-size="{$tableSize}">
      <xsl:call-template name="addID"/>
      <xsl:call-template name="deriveColSpecs"/>
      <xsl:apply-templates select="tei:row[@role='header']"/>
      <table-body text-indent="0pt">
        <xsl:for-each select="tei:row[not(@role='header')]">
          <xsl:text>&#10;</xsl:text>
          <table-row>
            <xsl:apply-templates select="tei:cell"/>
          </table-row>
        </xsl:for-each>
      </table-body>
    </table>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="calculateFigureNumber">
    <xsl:number from="tei:text" level="any"/>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="calculateTableNumber">
    <xsl:number from="tei:text" level="any"/>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="cellProperties">
    <xsl:if test="@role='hi' or @role='label' or   parent::tei:row/@role='label'  or parent::tei:row/@role='header'">
      <xsl:attribute name="background-color">
        <xsl:value-of select="$defaultCellLabelBackground"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="ancestor::tei:table[1][@rend='frame']">
        <xsl:if test="not(parent::tei:row/preceding-sibling::tei:row)">
          <xsl:attribute name="border-before-style">solid</xsl:attribute>
        </xsl:if>
        <xsl:attribute name="border-after-style">solid</xsl:attribute>
        <xsl:if test="not(following-sibling::tei:cell)">
          <xsl:attribute name="border-end-style">solid</xsl:attribute>
        </xsl:if>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
  </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="not(ancestor::tei:table/@rend='tight')">
      <xsl:attribute name="padding">
        <xsl:value-of select="$tableCellPadding"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="@align">
        <xsl:attribute name="text-align">
          <xsl:value-of select="@align"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="thiscol">
          <xsl:value-of select="position()"/>
        </xsl:variable>
        <xsl:variable name="tid">
          <xsl:value-of select="ancestor::tei:table/@xml:id"/>
        </xsl:variable>
        <xsl:variable name="align">
          <xsl:value-of select="exsl:node-set($tableSpecs)/Info/TableSpec[@xml:id=$tid]/table-column[@column-number=$thiscol]/@fotex:column-align"/>
        </xsl:variable>
<!--
    <xsl:message>    Cell: whats my position: <xsl:value-of select="$thiscol"/>, <xsl:value-of select="$align"/>, <xsl:value-of select="$tid"/>
</xsl:message>
-->
        <xsl:choose>
          <xsl:when test="$align='R'">
            <xsl:attribute name="text-align">right</xsl:attribute>
          </xsl:when>
          <xsl:when test="$align='L'">
            <xsl:attribute name="text-align">left</xsl:attribute>
          </xsl:when>
          <xsl:when test="$align='C'">
            <xsl:attribute name="text-align">center</xsl:attribute>
          </xsl:when>
          <xsl:when test="not($align='')">
            <xsl:attribute name="text-align">
              <xsl:value-of select="$align"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="text-align">
              <xsl:value-of select="$cellAlign"/>
            </xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="floatTable">
    <table-and-caption>
      <xsl:if test="rend='landscape'">
        <xsl:attribute name="reference-direction">-90</xsl:attribute>
      </xsl:if>
      <xsl:call-template name="addID"/>
      <table-caption>
        <block text-align="{$tableCaptionAlign}" space-after="{$spaceBelowCaption}">
          <xsl:call-template name="i18n">
            <xsl:with-param name="word">tableWord</xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="calculateTableNumber"/>
          <xsl:text>. </xsl:text>
          <xsl:apply-templates select="tei:head"/>
        </block>
      </table-caption>
      <xsl:call-template name="blockTable"/>
    </table-and-caption>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] Insert reference to graphics file </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="makePic">
    <xsl:variable name="File">
      <xsl:choose>
        <xsl:when test="@url">
          <xsl:value-of select="@url"/>
        </xsl:when>
        <xsl:when test="@entity">
          <xsl:value-of select="unparsed-entity-uri(@entity)"/>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>
    <external-graphic>
      <xsl:call-template name="addID"/>
      <xsl:attribute name="src">
        <xsl:text>url(</xsl:text>
        <xsl:if test="not(starts-with($File,'./'))">
          <xsl:value-of select="$graphicsPrefix"/>
        </xsl:if>
        <xsl:value-of select="$File"/>
        <xsl:if test="not(contains($File,'.'))">
          <xsl:value-of select="$graphicsSuffix"/>
        </xsl:if>
        <xsl:text>)</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="graphicsAttributes">
        <xsl:with-param name="mode">fo</xsl:with-param>
      </xsl:call-template>
    </external-graphic>
  </xsl:template>

  <xsl:template match="tei:binaryObject">
    <external-graphic>
      <xsl:attribute name="src">
	<xsl:text>url('data:image/auto;base64,</xsl:text>
	<xsl:value-of select="."/>
	<xsl:text>')</xsl:text>
      </xsl:attribute>
      <xsl:call-template name="graphicsAttributes">
	<xsl:with-param name="mode">fo</xsl:with-param>
      </xsl:call-template>
    </external-graphic>  
  </xsl:template>

</xsl:stylesheet>
