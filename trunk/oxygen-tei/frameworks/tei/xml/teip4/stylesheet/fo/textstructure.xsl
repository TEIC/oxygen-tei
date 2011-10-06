<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xd="http://www.pnp-software.com/XSLTdoc"
		xmlns="http://www.w3.org/1999/XSL/Format" xmlns:fotex="http://www.tug.org/fotex" xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0" xmlns:edate="http://exslt.org/dates-and-times" xmlns:estr="http://exslt.org/strings" xmlns:exsl="http://exslt.org/common" xmlns:rng="http://relaxng.org/ns/structure/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" xmlns:teix="http://www.tei-c.org/ns/Examples" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" extension-element-prefixes="exsl estr edate" exclude-result-prefixes="xd exsl fotex estr edate a rng tei teix" version="1.0">
  <xd:doc type="stylesheet">
    <xd:short>
    TEI stylesheet
    dealing  with elements from the
      textstructure module, making XSL-FO output.
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
    <xd:cvsId>$Id: textstructure.xsl 8360 2011-01-08 19:12:19Z rahtz $</xd:cvsId>
    <xd:copyright>2008, TEI Consortium</xd:copyright>
  </xd:doc>
  <xd:doc>
    <xd:short>Process elements  * in inner mode</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="*" mode="innertext">
    <xsl:apply-templates select="."/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  *</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="*" mode="heading">
    <xsl:apply-templates/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  TEI.2</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="TEI.2">
    <root>
      <xsl:call-template name="setupPagemasters"/>
      <xsl:call-template name="mainAction"/>
    </root>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  back</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="back">
    <xsl:comment>Back matter</xsl:comment>
    <xsl:choose>
      <xsl:when test="ancestor::group">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <page-sequence 
	    format="{$formatBackpage}"
	    text-align="{$alignment}"
	    hyphenate="{$hyphenate}" 
	    language="{$language}">
          <xsl:call-template name="choosePageMaster">
            <xsl:with-param name="where">
              <xsl:value-of select="$backMulticolumns"/>
            </xsl:with-param>
          </xsl:call-template>
<!-- static areas -->
          <xsl:choose>
            <xsl:when test="$twoSided='true'">
              <xsl:call-template name="headers-footers-twoside-back"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="headers-footers-oneside-back"/>
            </xsl:otherwise>
          </xsl:choose>
<!-- now start the main flow -->
          <flow flow-name="xsl-region-body" font-family="{$bodyFont}" font-size="{$bodySize}">
            <xsl:apply-templates/>
            <xsl:call-template name="afterBodyHook"/>
          </flow>
        </page-sequence>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  body</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="body">
    <xsl:choose>
      <xsl:when test="ancestor::floatingText">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="ancestor::p">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="ancestor::group">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
<!-- start page sequence -->
        <page-sequence format="{$formatBodypage}" text-align="{$alignment}" hyphenate="{$hyphenate}" language="{$language}" initial-page-number="1">
          <xsl:call-template name="choosePageMaster">
            <xsl:with-param name="where">
              <xsl:value-of select="$bodyMulticolumns"/>
            </xsl:with-param>
          </xsl:call-template>
<!-- static areas -->
          <xsl:choose>
            <xsl:when test="$twoSided='true'">
              <xsl:call-template name="headers-footers-twoside"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="headers-footers-oneside"/>
            </xsl:otherwise>
          </xsl:choose>
<!-- now start the main  flow -->
          <flow flow-name="xsl-region-body" font-family="{$bodyFont}" font-size="{$bodySize}">
            <xsl:if test="not($flowMarginLeft='')">
              <xsl:attribute name="margin-left">
                <xsl:value-of select="$flowMarginLeft"/>
              </xsl:attribute>
            </xsl:if>
<!--include front matter if there is no separate titlepage -->
            <xsl:if test="not($titlePage='true') and not(preceding-sibling::front)">
              <xsl:call-template name="Header"/>
            </xsl:if>
            <xsl:apply-templates/>
            <xsl:if test=".//note[@place='end']">
              <block>
                <xsl:call-template name="setupDiv2"/>
                <xsl:text>Notes</xsl:text>
              </block>
              <xsl:apply-templates select=".//note[@place='end']" mode="endnote"/>
            </xsl:if>
          </flow>
        </page-sequence>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  closer</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="closer">
    <block space-before.optimum="4pt" space-after.optimum="4pt" end-indent="{$exampleMargin}" start-indent="{$exampleMargin}">
      <xsl:apply-templates/>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  dateline</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="dateline">
    <block text-align="end">
      <xsl:apply-templates/>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  div</xd:short>
    <xd:detail>
      <p> Normal headings </p>
    </xd:detail>
  </xd:doc>
  <xsl:template match="div">
    <xsl:text>&#10;</xsl:text>
    <xsl:choose>
      <xsl:when test="@type='bibliog'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="@type='abstract'">
        <block keep-with-next.within-page="always" end-indent="{$exampleMargin}" start-indent="{$exampleMargin}">
          <xsl:attribute name="text-align">center</xsl:attribute>
          <xsl:call-template name="setupDiv2"/>
          <inline font-style="italic">Abstract</inline>
        </block>
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="@type='ack'">
        <block keep-with-next.within-page="always">
          <xsl:attribute name="text-align">start</xsl:attribute>
          <xsl:call-template name="setupDiv3"/>
          <inline font-style="italic">Acknowledgements</inline>
        </block>
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
<!-- behaviour depends on the nesting level of <div> elements -->
        <xsl:variable name="divlevel" select="count(ancestor::div)"/>
        <xsl:call-template name="NumberedHeading">
          <xsl:with-param name="level">
            <xsl:value-of select="$divlevel"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  div (toc mode)</xd:short>
    <xd:detail>
      <p> headings in TOC </p>
    </xd:detail>
  </xd:doc>
  <xsl:template match="div" mode="toc">
    <xsl:variable name="divlevel">
      <xsl:value-of select="count(ancestor::div)"/>
    </xsl:variable>
    <xsl:call-template name="tocheading">
      <xsl:with-param name="level">
        <xsl:value-of select="$divlevel"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  div (xref mode)</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="div" mode="xref">
    <xsl:variable name="divlevel" select="count(ancestor::div)"/>
    <xsl:call-template name="xheading">
      <xsl:with-param name="level">div<xsl:value-of select="$divlevel"/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  div0|div1|div2|div3|div4|div5</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="div0|div1|div2|div3|div4|div5">
    <xsl:choose>
      <xsl:when test="@type='letter'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="NumberedHeading">
          <xsl:with-param name="level">
            <xsl:value-of select="substring-after(local-name(),'div')"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements
      div0|div1|div2|div3|div4|div5 (toc mode)</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="div0|div1|div2|div3|div4|div5" mode="toc">
    <xsl:call-template name="tocheading">
      <xsl:with-param name="level">
        <xsl:value-of select="substring-after(local-name(),'div')"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  div0|div1|div2|div3|div4|div5
    (xref mode)</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template mode="xref" match="div0|div1|div2|div3|div4|div5">
    <xsl:call-template name="xheading">
      <xsl:with-param name="level">
        <xsl:value-of select="local-name()"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  div[@type='canto']</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="div[@type='canto']">
    <xsl:variable name="divlevel" select="count(ancestor::div)"/>
    <xsl:call-template name="NumberedHeading">
      <xsl:with-param name="level">
        <xsl:value-of select="$divlevel"/>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  div[@type='epistle']</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="div[@type='epistle']">
    <xsl:apply-templates/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  div[@type='frontispiece']</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="div[@type='frontispiece']">
    <xsl:apply-templates/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  div[@type='illustration']</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="div[@type='illustration']">
    <xsl:apply-templates/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  divGen</xd:short>
    <xd:detail>
      <p> table of contents </p>
    </xd:detail>
  </xd:doc>
  <xsl:template match="divGen">
    <xsl:choose>
      <xsl:when test="@type='toc' and ancestor::text/group">
        <xsl:call-template name="bookTOC"/>
      </xsl:when>
      <xsl:when test="@type='toc'">
        <xsl:call-template name="mainTOC"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  front</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="front">
    <xsl:comment>Front matter</xsl:comment>
    <xsl:choose>
      <xsl:when test="ancestor::floatingText">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="ancestor::p">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="ancestor::group">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="$titlePage='true'">
          <page-sequence format="{$formatFrontpage}" force-page-count="end-on-even" hyphenate="{$hyphenate}" language="{$language}">
            <xsl:call-template name="choosePageMaster">
              <xsl:with-param name="where">
                <xsl:value-of select="$frontMulticolumns"/>
              </xsl:with-param>
            </xsl:call-template>
            <static-content flow-name="xsl-region-before">
              <block/>
            </static-content>
            <static-content flow-name="xsl-region-after">
              <block/>
            </static-content>
            <flow flow-name="xsl-region-body"  font-family="{$bodyFont}">
              <xsl:call-template name="Header"/>
            </flow>
          </page-sequence>
        </xsl:if>
        <page-sequence format="{$formatFrontpage}" text-align="{$alignment}" hyphenate="{$hyphenate}" language="{$language}" initial-page-number="1">
          <xsl:call-template name="choosePageMaster">
            <xsl:with-param name="where">
              <xsl:value-of select="$frontMulticolumns"/>
            </xsl:with-param>
          </xsl:call-template>
<!-- static areas -->
          <xsl:choose>
            <xsl:when test="$twoSided='true'">
              <xsl:call-template name="headers-footers-twoside"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="headers-footers-oneside"/>
            </xsl:otherwise>
          </xsl:choose>
<!-- now start the main flow -->
          <flow flow-name="xsl-region-body" font-family="{$bodyFont}" font-size="{$bodySize}">
            <xsl:for-each select="*">
              <xsl:comment>Start <xsl:value-of select="name(.)"/></xsl:comment>
              <xsl:apply-templates select="."/>
              <xsl:comment>End <xsl:value-of select="name(.)"/></xsl:comment>
            </xsl:for-each>
          </flow>
        </page-sequence>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  group/text</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="group/text">
    <xsl:variable name="N">
      <xsl:number/>
    </xsl:variable>
    <page-sequence format="1" text-align="{$alignment}" hyphenate="{$hyphenate}" language="{$language}" master-reference="twoside1">
      <xsl:attribute name="initial-page-number">
        <xsl:choose>
          <xsl:when test="$N = 1">1</xsl:when>
          <xsl:otherwise>auto-odd</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:call-template name="grouptextStatic"/>
      <flow  flow-name="xsl-region-body" font-family="{$bodyFont}">
        <xsl:call-template name="textTitle">
          <xsl:with-param name="N" select="$N"/>
        </xsl:call-template>
        <xsl:apply-templates select="body"/>
        <xsl:apply-templates select="back"/>
      </flow>
    </page-sequence>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  group/text/body</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="group/text/body">
    <xsl:apply-templates/>
    <xsl:if test=".//note[@place='end']">
      <block>
        <xsl:call-template name="setupDiv2"/>
        <xsl:text>Notes</xsl:text>
      </block>
      <xsl:apply-templates select=".//note[@place='end']" mode="endnote"/>
    </xsl:if>
  </xsl:template>

  <xd:doc>
    <xd:short>Process element head in heading mode</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="head" mode="makeheading">
    <xsl:apply-templates/>
  </xsl:template>

  <xd:doc>
    <xd:short>Process elements  head (section mode)</xd:short>
    <xd:detail>
      <p> simple head </p>
    </xd:detail>
  </xd:doc>
  <xsl:template match="head" mode="section">
<!-- if we have multiple <head> elements together, 
  separate by spaces -->
<!--
   <xsl:if test="preceding-sibling::head">
	<xsl:text> </xsl:text>
     </xsl:if>
-->
    <xsl:apply-templates/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  head</xd:short>
    <xd:detail>
      <p> unqualified &lt;head&gt; </p>
    </xd:detail>
  </xd:doc>
  <xsl:template match="head"/>
  <xd:doc>
    <xd:short>Process elements  opener</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="opener">
    <block space-before.optimum="4pt" space-after.optimum="4pt" end-indent="{$exampleMargin}" start-indent="{$exampleMargin}">
      <xsl:apply-templates/>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  text (xref mode)</xd:short>
    <xd:detail>
      <p> headings when cross-referenced </p>
    </xd:detail>
  </xd:doc>
  <xsl:template match="text" mode="xref">
    <xsl:choose>
      <xsl:when test="@n">
        <xsl:value-of select="@n"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:number/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  text</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="text">
    <xsl:choose>
      <xsl:when test="parent::TEI.2">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="parent::group">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <block text-align="start" text-indent="0pt" end-indent="{$exampleMargin}" start-indent="{$exampleMargin}" font-size="{$exampleSize}" border-top-style="solid" border-bottom-style="solid" space-before.optimum="{$exampleBefore}" space-after.optimum="{$exampleAfter}">
          <xsl:apply-templates mode="innertext"/>
        </block>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  text (toc mode)</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="text" mode="toc">
    <block>
      <xsl:attribute name="font-weight">bold</xsl:attribute>
      <xsl:number/>
      <xsl:text> </xsl:text>
      <inline>
        <xsl:choose>
          <xsl:when test="front/docTitle[@n]">
            <xsl:value-of select="front/docTitle/@n"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="front/docTitle"/>
          </xsl:otherwise>
        </xsl:choose>
      </inline>
      <leader rule-thickness="0pt"/>
      <inline>
        <xsl:call-template name="linkStyle"/>
        <xsl:variable name="pagref">
          <xsl:choose>
            <xsl:when test="@id">
              <xsl:value-of select="@id"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="generate-id()"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <basic-link internal-destination="{$pagref}">
          <page-number-citation ref-id="{$pagref}"/>
        </basic-link>
      </inline>
    </block>
    <block font-style="italic" space-after="10pt" space-before="6pt">
      <xsl:apply-templates select="front//docAuthor" mode="heading"/>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  titlePage</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="titlePage">
    <block text-align="center">
      <xsl:apply-templates/>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  titlePage//titlePart[@type='main']</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="titlePage//titlePart[@type='main']">
    <block font-size="{$titleSize}" space-after="8pt" text-align="center">
      <xsl:apply-templates/>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  titlePage//titlePart[@type='sub']</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="titlePage//titlePart[@type='sub']">
    <block>
      <block font-size="{$authorSize}" space-after="8pt" text-align="center">
        <xsl:apply-templates/>
      </block>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  titlePart</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="titlePart">
    <xsl:apply-templates/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  titlePart[@type='running']</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="titlePart[@type='running']"/>
  <xd:doc>
    <xd:short>Process elements  trailer</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="trailer">
    <block>
      <xsl:apply-templates/>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  text()</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="Header">
    <block font-size="12pt" padding-before="6pt">
      <inline>
	<xsl:value-of select="$institution"/>
      </inline>
    </block>
    
    <block font-size="12pt" font-weight="bold">
      <xsl:call-template name="generateTitle"/>
    </block>
    
    <block>
      <xsl:call-template name="generateAuthor"/>
    </block>
    
    <block padding-after="6pt">
      <xsl:call-template name="generateDate"/>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:param name="level">level</xd:param>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="NumberedHeading">
    <xsl:param name="level"/>
    <block keep-with-next.within-page="always">
      <xsl:variable name="divid">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
	    <xsl:text>ID</xsl:text>
            <xsl:number level="any"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:attribute name="id">
        <xsl:value-of select="$divid"/>
      </xsl:attribute>
      <xsl:attribute name="text-align">start</xsl:attribute>
      <xsl:attribute name="font-family">
        <xsl:value-of select="$divFont"/>
      </xsl:attribute>
      <xsl:choose>
        <xsl:when test="$level=0">
          <xsl:call-template name="setupDiv0"/>
        </xsl:when>
        <xsl:when test="$level=1">
          <xsl:call-template name="setupDiv1"/>
        </xsl:when>
        <xsl:when test="$level=2">
          <xsl:call-template name="setupDiv2"/>
        </xsl:when>
        <xsl:when test="$level=3">
          <xsl:call-template name="setupDiv3"/>
        </xsl:when>
        <xsl:when test="$level=4">
          <xsl:call-template name="setupDiv4"/>
        </xsl:when>
        <xsl:when test="$level=5">
          <xsl:call-template name="setupDiv5"/>
        </xsl:when>
      </xsl:choose>
      <xsl:call-template name="blockStartHook"/>
      <xsl:variable name="Number">
        <xsl:if test="$numberHeadings='true' and $numberHeadingsDepth &gt; $level">
          <xsl:call-template name="calculateNumber">
            <xsl:with-param name="numbersuffix">
	      <xsl:call-template name="headingNumberSuffix"/>
	    </xsl:with-param>
          </xsl:call-template>
        </xsl:if>
      </xsl:variable>
<!--
<xsl:message>**  Calculated   [<xsl:value-of select="$Number"/>] [<xsl:value-of select="$headingNumberSuffix"/>] for <xsl:value-of select="@id"/></xsl:message>
-->
      <xsl:value-of select="$Number"/>
      <xsl:apply-templates mode="section" select="head"/>
      <xsl:if test="$divRunningheads='true'">
<!-- markers for use in running heads -->
        <xsl:choose>
          <xsl:when test="$level=0">
            <marker marker-class-name="section1"/>
            <marker marker-class-name="section2"/>
            <marker marker-class-name="section3"/>
            <marker marker-class-name="section4"/>
            <marker marker-class-name="section5"/>
          </xsl:when>
          <xsl:when test="$level=1">
            <marker marker-class-name="section2"/>
            <marker marker-class-name="section3"/>
            <marker marker-class-name="section4"/>
            <marker marker-class-name="section5"/>
          </xsl:when>
          <xsl:when test="$level=2">
            <marker marker-class-name="section3"/>
            <marker marker-class-name="section4"/>
            <marker marker-class-name="section5"/>
          </xsl:when>
          <xsl:when test="$level=3">
            <marker marker-class-name="section4"/>
            <marker marker-class-name="section5"/>
          </xsl:when>
          <xsl:when test="$level=4">
          <marker marker-class-name="section5"/>
          </xsl:when>
          <xsl:when test="$level=5"/>                     
        </xsl:choose>
        <marker marker-class-name="section{$level}">
          <xsl:if test="$numberHeadings='true'">
            <xsl:value-of select="$Number"/>
            <xsl:call-template name="headingNumberSuffix"/>
          </xsl:if>
          <xsl:value-of select="head"/>
        </marker>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="$foEngine='passivetex'">
<!-- Passive TeX extension, to get PDF bookmarks -->
          <fotex:bookmark fotex-bookmark-level="{$level}" fotex-bookmark-label="{$divid}">
            <xsl:if test="$numberHeadings='true'">
              <xsl:value-of select="$Number"/>
            </xsl:if>
            <xsl:value-of select="head"/>
          </fotex:bookmark>
        </xsl:when>
      </xsl:choose>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="bookTOC">
    <page-sequence format="1" initial-page-number="{$tocStartPage}" master-reference="twoside1">
      <xsl:call-template name="headers-footers-twoside">
        <xsl:with-param name="runeven">even page running head</xsl:with-param>
        <xsl:with-param name="runodd"> odd page running head</xsl:with-param>
      </xsl:call-template>
      <flow  flow-name="xsl-region-body" font-family="{$bodyFont}">
        <block text-align="center">
          <xsl:attribute name="font-size">
            <xsl:value-of select="$tocSize"/>
          </xsl:attribute>
          <xsl:attribute name="text-indent">
            <xsl:value-of select="$headingOutdent"/>
          </xsl:attribute>
          <xsl:attribute name="font-weight">bold</xsl:attribute>
          <xsl:attribute name="space-after">24pt</xsl:attribute>
          <xsl:attribute name="space-before.optimum">24pt</xsl:attribute>
          <xsl:text>Contents</xsl:text>
        </block>
        <xsl:for-each select="ancestor::text/group/text">
          <xsl:apply-templates select="." mode="toc"/>
        </xsl:for-each>
      </flow>
    </page-sequence>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:param name="where">where</xd:param>
    <xd:param name="force">force</xd:param>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="choosePageMaster">
    <xsl:param name="where"/>
    <xsl:param name="force"/>
    <xsl:variable name="mn">
      <xsl:choose>
        <xsl:when test="$forcePageMaster">
          <xsl:value-of select="$forcePageMaster"/>
        </xsl:when>
        <xsl:when test="not($where='')">
          <xsl:choose>
            <xsl:when test="$twoSided='true'">twoside2</xsl:when>
            <xsl:otherwise>oneside2</xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="$twoSided='true'">twoside1</xsl:when>
            <xsl:otherwise>oneside1</xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:attribute name="master-reference">
      <xsl:value-of select="$mn"/>
    </xsl:attribute>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="grouptextStatic">
    <static-content flow-name="xsl-region-after-right">
      <block text-align="end" font-size="{$bodySize}">
        <page-number/>
      </block>
    </static-content>
    <static-content flow-name="xsl-region-after-left">
      <block text-align="start" font-size="{$bodySize}">
        <page-number/>
      </block>
    </static-content>
    <static-content flow-name="xsl-region-before-left">
      <block text-indent="0em" background-color="blue" font-weight="bold" padding="3pt" color="white" font-family="{$runFont}" text-align="justify" font-size="{$runSize}">
        <inline>
          <xsl:number/>
        </inline>
        <leader rule-thickness="0pt"/>
        <inline>
          <xsl:choose>
            <xsl:when test="front/docTitle[@n]">
              <xsl:value-of select="front/docTitle[@n]"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="front/docTitle"/>
            </xsl:otherwise>
          </xsl:choose>
        </inline>
      </block>
    </static-content>
    <static-content flow-name="xsl-region-before-right">
      <block background-color="blue" text-indent="0em" font-weight="bold" padding="3pt" color="white" text-align="left" font-family="{$runFont}" font-size="{$runSize}">
        <xsl:value-of select="front//docAuthor"/>
      </block>
    </static-content>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:param name="runhead">runhead</xd:param>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="headers-footers-oneside">
    <xsl:param name="runhead"/>
    <static-content flow-name="xsl-region-before">
      <block font-size="{$bodySize}">
        <xsl:choose>
          <xsl:when test="$runhead='true'">
            <xsl:value-of select="$runhead"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="runninghead-author"/>
          </xsl:otherwise>
        </xsl:choose>
      </block>
    </static-content>
    <static-content flow-name="xsl-region-after">
      <block text-align="center" font-size="{$bodySize}">
        <page-number/>
      </block>
    </static-content>
    <static-content flow-name="xsl-region-before-first">
        </static-content>
    <static-content flow-name="xsl-region-after-first">
      <block text-align="center" font-size="{$bodySize}">
        <page-number/>
      </block>
    </static-content>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:param name="runhead">runhead</xd:param>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="headers-footers-oneside-back">
    <xsl:param name="runhead"/>
    <xsl:call-template name="headers-footers-oneside">
      <xsl:with-param name="runhead">
        <xsl:value-of select="$runhead"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:param name="runodd">odd page running head</xd:param>
    <xd:param name="runeven">even page running head</xd:param>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="headers-footers-twoside">
    <xsl:param name="runodd"/>
    <xsl:param name="runeven"/>
    <static-content flow-name="xsl-region-before-right">
      <block text-align="justify" text-align-last="justify" font-size="{$bodySize}">
        <xsl:choose>
          <xsl:when test="$runodd">
            <xsl:value-of select="$runodd"/>
          </xsl:when>
          <xsl:when test="$sectionHeaders='true'">
            <block>
              <xsl:if test="$divRunningheads='true'">
                <inline>
                  <retrieve-marker retrieve-class-name="section2"/>
                </inline>
              </xsl:if>
              <leader rule-thickness="0pt"/>
              <inline>
                <page-number/>
              </inline>
            </block>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="runninghead-title"/>
          </xsl:otherwise>
        </xsl:choose>
      </block>
    </static-content>
    <static-content flow-name="xsl-region-before-left">
      <block text-align="justify" font-size="{$bodySize}">
        <xsl:choose>
          <xsl:when test="$runeven">
            <xsl:value-of select="$runeven"/>
          </xsl:when>
          <xsl:when test="$sectionHeaders='true'">
            <block>
              <inline>
                <page-number/>
              </inline>
              <leader rule-thickness="0pt"/>
              <xsl:if test="$divRunningheads='true'">
                <inline>
                  <retrieve-marker retrieve-class-name="section1"/>
                </inline>
              </xsl:if>
            </block>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="runninghead-author"/>
          </xsl:otherwise>
        </xsl:choose>
      </block>
    </static-content>
    <static-content flow-name="xsl-region-before-first">
      <block/>
    </static-content>
    <static-content flow-name="xsl-region-after-right">
      <block text-align="end" font-size="{$bodySize}">
        <page-number/>
      </block>
    </static-content>
    <static-content flow-name="xsl-region-after-left">
      <block text-align="start" font-size="{$bodySize}">
        <page-number/>
      </block>
    </static-content>
    <static-content flow-name="xsl-region-after-first">
      <block font-size="{$bodySize}" text-align="end">
        <page-number/>
      </block>
    </static-content>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:param name="runodd">odd page running head</xd:param>
    <xd:param name="runeven">even page running head</xd:param>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="headers-footers-twoside-back">
    <xsl:param name="runodd"/>
    <xsl:param name="runeven"/>
    <xsl:call-template name="headers-footers-twoside">
      <xsl:with-param name="runeven">
        <xsl:value-of select="$runeven"/>
      </xsl:with-param>
      <xsl:with-param name="runodd">
        <xsl:value-of select="$runodd"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="mainAction">
    <xsl:choose>
      <xsl:when test="text/group">
        <xsl:apply-templates select="text/front"/>
        <xsl:apply-templates select="text/group"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="text/front"/>
        <xsl:apply-templates select="text/body"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="text/back"/>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="mainTOC">
    <block>
      <xsl:call-template name="setupDiv1"/>
      <xsl:call-template name="i18n">
        <xsl:with-param name="word">contentsWord</xsl:with-param>
      </xsl:call-template>
    </block>
    <xsl:choose>
      <xsl:when test="ancestor::text/group">
        <xsl:for-each select="ancestor::text/group">
          <xsl:apply-templates select="text" mode="toc"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:when test="ancestor::text/body/div1">
        <xsl:if test="$tocFront='true'">
          <xsl:for-each select="ancestor::text/front/div1|ancestor::text/front//div2|ancestor::text/front//div3">
            <xsl:apply-templates mode="toc" select="(.)"/>
          </xsl:for-each>
        </xsl:if>
        <xsl:for-each select="ancestor::text/body/div1|ancestor::text/body//div2|ancestor::text/body//div3">
          <xsl:apply-templates mode="toc" select="(.)"/>
        </xsl:for-each>
        <xsl:if test="$tocBack='true'">
          <xsl:for-each select="ancestor::text/back/div1|ancestor::text/back//div2|ancestor::text/back//div3">
            <xsl:apply-templates mode="toc" select="(.)"/>
          </xsl:for-each>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="tocBits"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="runninghead-author">
    <xsl:choose>
      <xsl:when test="ancestor::text[1]/front//docAuthor[@type='running']">
        <xsl:apply-templates select="ancestor-or-self::text[1]/front//docAuthor[@type='running']" mode="heading"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="ancestor-or-self::text[1]/front//docAuthor" mode="heading"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="runninghead-title">
    <xsl:comment>Running Head: title</xsl:comment>
    <xsl:choose>
      <xsl:when test="ancestor-or-self::text[1]/front//docTitle[1]/titlePart[@type='running']">
        <xsl:apply-templates select="ancestor-or-self::text[1]/front//docTitle[1]/titlePart[@type='running']" mode="heading"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="ancestor-or-self::text[1]/front//docTitle[1]/titlePart" mode="heading"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="setupPagemasters">
    <layout-master-set>
<!-- one sided, single column -->
      <simple-page-master master-name="simple1" page-width="{$pageWidth}" page-height="{$pageHeight}" margin-top="{$pageMarginTop}" margin-bottom="{$pageMarginBottom}" margin-left="{$pageMarginLeft}" margin-right="{$pageMarginRight}">
        <region-body margin-bottom="{$bodyMarginBottom}" margin-top="{$bodyMarginTop}"/>
        <region-before extent="{$regionBeforeExtent}"/>
        <region-after extent="{$regionAfterExtent}"/>
      </simple-page-master>
<!-- for left-hand/even pages in twosided mode, single column -->
      <simple-page-master master-name="left1" page-width="{$pageWidth}" page-height="{$pageHeight}" margin-top="{$pageMarginTop}" margin-bottom="{$pageMarginBottom}" margin-left="{$pageMarginLeft}" margin-right="{$pageMarginRight}">
        <region-body margin-bottom="{$bodyMarginBottom}" margin-top="{$bodyMarginTop}"/>
        <region-before region-name="xsl-region-before-left" extent="{$regionBeforeExtent}"/>
        <region-after region-name="xsl-region-after-left" extent="{$regionAfterExtent}"/>
      </simple-page-master>
<!-- for right-hand/odd pages in twosided mode, single column -->
      <simple-page-master master-name="right1" page-width="{$pageWidth}" page-height="{$pageHeight}" margin-top="{$pageMarginTop}" margin-bottom="{$pageMarginBottom}" margin-left="{$pageMarginLeft}" margin-right="{$pageMarginRight}">
        <region-body margin-bottom="{$bodyMarginBottom}" margin-top="{$bodyMarginTop}"/>
        <region-before region-name="xsl-region-before-right" extent="{$regionBeforeExtent}"/>
        <region-after region-name="xsl-region-after-right" extent="{$regionAfterExtent}"/>
      </simple-page-master>
<!-- special case of first page in either mode, single column -->
      <simple-page-master master-name="first1" page-width="{$pageWidth}" page-height="{$pageHeight}" margin-top="{$pageMarginTop}" margin-bottom="{$pageMarginBottom}" margin-left="{$pageMarginLeft}" margin-right="{$pageMarginRight}">
        <region-body margin-bottom="{$bodyMarginBottom}" margin-top="{$bodyMarginTop}"/>
        <region-before region-name="xsl-region-before-first" extent="{$regionBeforeExtent}"/>
        <region-after region-name="xsl-region-after-first" extent="{$regionAfterExtent}"/>
      </simple-page-master>
<!-- for pages in one-side mode, 2 column -->
      <simple-page-master master-name="simple2" page-width="{$pageWidth}" page-height="{$pageHeight}" margin-top="{$pageMarginTop}" margin-bottom="{$pageMarginBottom}" margin-left="{$pageMarginLeft}" margin-right="{$pageMarginRight}">
        <region-body column-count="{$columnCount}" margin-bottom="{$bodyMarginBottom}" margin-top="{$bodyMarginTop}"/>
        <region-before extent="{$regionBeforeExtent}"/>
        <region-after extent="{$regionAfterExtent}"/>
      </simple-page-master>
<!-- for left-hand/even pages in twosided mode, 2 column -->
      <simple-page-master master-name="left2" page-width="{$pageWidth}" page-height="{$pageHeight}" margin-top="{$pageMarginTop}" margin-bottom="{$pageMarginBottom}" margin-left="{$pageMarginLeft}" margin-right="{$pageMarginRight}">
        <region-body column-count="{$columnCount}" margin-bottom="{$bodyMarginBottom}" margin-top="{$bodyMarginTop}"/>
        <region-before region-name="xsl-region-before-left" extent="{$regionBeforeExtent}"/>
        <region-after region-name="xsl-region-after-left" extent="{$regionAfterExtent}"/>
      </simple-page-master>
<!-- for right-hand/odd pages in twosided mode, 2 column -->
      <simple-page-master master-name="right2" page-width="{$pageWidth}" page-height="{$pageHeight}" margin-top="{$pageMarginTop}" margin-bottom="{$pageMarginBottom}" margin-left="{$pageMarginLeft}" margin-right="{$pageMarginRight}">
        <region-body column-count="{$columnCount}" margin-bottom="{$bodyMarginBottom}" margin-top="{$bodyMarginTop}"/>
        <region-before region-name="xsl-region-before-right" extent="{$regionBeforeExtent}"/>
        <region-after region-name="xsl-region-after-right" extent="{$regionAfterExtent}"/>
      </simple-page-master>
<!-- special case of first page in either mode -->
      <simple-page-master master-name="first2" page-width="{$pageWidth}" page-height="{$pageHeight}" margin-top="{$pageMarginTop}" margin-bottom="{$pageMarginBottom}" margin-left="{$pageMarginLeft}" margin-right="{$pageMarginRight}">
        <region-body column-count="{$columnCount}" margin-bottom="{$bodyMarginBottom}" margin-top="{$bodyMarginTop}"/>
        <region-before region-name="xsl-region-before-first" extent="{$regionBeforeExtent}"/>
        <region-after region-name="xsl-region-after-first" extent="{$regionAfterExtent}"/>
      </simple-page-master>
<!-- setup for double-sided, 1 column, no first page -->
      <page-sequence-master master-name="twoside1nofirst">
        <repeatable-page-master-alternatives>
          <conditional-page-master-reference master-reference="right1" odd-or-even="odd"/>
          <conditional-page-master-reference master-reference="left1" odd-or-even="even"/>
        </repeatable-page-master-alternatives>
      </page-sequence-master>
<!-- setup for double-sided, 1 column -->
      <page-sequence-master master-name="twoside1">
        <repeatable-page-master-alternatives>
          <conditional-page-master-reference master-reference="first1" page-position="first"/>
          <conditional-page-master-reference master-reference="right1" odd-or-even="odd"/>
          <conditional-page-master-reference master-reference="left1" odd-or-even="even"/>
        </repeatable-page-master-alternatives>
      </page-sequence-master>
<!-- setup for single-sided, 1 column -->
      <page-sequence-master master-name="oneside1">
        <repeatable-page-master-alternatives>
          <conditional-page-master-reference master-reference="first1" page-position="first"/>
          <conditional-page-master-reference master-reference="simple1"/>
        </repeatable-page-master-alternatives>
      </page-sequence-master>
<!-- setup for double-sided, 2 column -->
      <page-sequence-master master-name="twoside2">
        <repeatable-page-master-alternatives>
          <conditional-page-master-reference master-reference="first2" page-position="first"/>
          <conditional-page-master-reference master-reference="right2" odd-or-even="odd"/>
          <conditional-page-master-reference master-reference="left2" odd-or-even="even"/>
        </repeatable-page-master-alternatives>
      </page-sequence-master>
<!-- setup for single-sided, 2 column -->
      <page-sequence-master master-name="oneside2">
        <repeatable-page-master-alternatives>
          <conditional-page-master-reference master-reference="first2" page-position="first"/>
          <conditional-page-master-reference master-reference="simple2"/>
        </repeatable-page-master-alternatives>
      </page-sequence-master>
      <xsl:call-template name="pageMasterHook"/>
    </layout-master-set>
    <xsl:if test="$foEngine='xep'">
<!-- PDF bookmarks using XEP -->
      <outline xmlns="http://www.renderx.com/XSL/Extensions">
        <xsl:for-each select="/TEI.2/text/front/div">
          <xsl:call-template name="makeBookMark"/>
        </xsl:for-each>
        <xsl:for-each select="/TEI.2/text/body/div">
          <xsl:call-template name="makeBookMark"/>
        </xsl:for-each>
        <xsl:for-each select="/TEI.2/text/back/div">
          <xsl:call-template name="makeBookMark"/>
        </xsl:for-each>
<!-- now try numbered divs -->
        <xsl:for-each select="/TEI.2/text/front/div0">
          <xsl:call-template name="makeBookMarkN"/>
        </xsl:for-each>
        <xsl:for-each select="/TEI.2/text/body/div0">
          <xsl:call-template name="makeBookMarkN"/>
        </xsl:for-each>
        <xsl:for-each select="/TEI.2/text/back/div0">
          <xsl:call-template name="makeBookMarkN"/>
        </xsl:for-each>
<!-- now try numbered divs -->
        <xsl:for-each select="/TEI.2/text/front/div1">
          <xsl:call-template name="makeBookMarkN"/>
        </xsl:for-each>
        <xsl:for-each select="/TEI.2/text/body/div1">
          <xsl:call-template name="makeBookMarkN"/>
        </xsl:for-each>
        <xsl:for-each select="/TEI.2/text/back/div1">
          <xsl:call-template name="makeBookMarkN"/>
        </xsl:for-each>
      </outline>
    </xsl:if>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] Make an XEP bookmark</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="makeBookMark">
    <xsl:if test="head">
      <bookmark xmlns="http://www.renderx.com/XSL/Extensions">
        <xsl:variable name="depth" select="count(ancestor::div)"/>
        <xsl:attribute name="internal-destination">
          <xsl:choose>
            <xsl:when test="@id">
              <xsl:value-of select="@id"/>
            </xsl:when>
            <xsl:otherwise>
	      <xsl:text>ID</xsl:text>
	      <xsl:number level="any"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <bookmark-label xmlns="http://www.renderx.com/XSL/Extensions">
          <xsl:if test="$numberHeadings='true' and $numberHeadingsDepth &gt; $depth">
            <xsl:call-template name="calculateNumber">
              <xsl:with-param name="numbersuffix">
		<xsl:call-template name="headingNumberSuffix"/>
	      </xsl:with-param>
            </xsl:call-template>
            <xsl:text> </xsl:text>
          </xsl:if>
          <xsl:value-of select="head"/>
        </bookmark-label>
        <xsl:for-each select="div">
          <xsl:call-template name="makeBookMark"/>
        </xsl:for-each>
      </bookmark>
    </xsl:if>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] Make an XEP bookmark for numbered divs</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="makeBookMarkN">
    <xsl:if test="head">
      <xsl:variable name="depth" select="substring-after(local-name(.),'div')"/>
      <bookmark xmlns="http://www.renderx.com/XSL/Extensions">
        <xsl:attribute name="internal-destination">
          <xsl:choose>
            <xsl:when test="@id">
              <xsl:value-of select="@id"/>
            </xsl:when>
            <xsl:otherwise>
	      <xsl:text>ID</xsl:text>
	      <xsl:number level="any"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <bookmark-label xmlns="http://www.renderx.com/XSL/Extensions">
          <xsl:if test="$numberHeadings='true' and $numberHeadingsDepth &gt; $depth">
            <xsl:call-template name="calculateNumber">
              <xsl:with-param name="numbersuffix">
		<xsl:call-template name="headingNumberSuffix"/>
	      </xsl:with-param>
            </xsl:call-template>
            <xsl:text> </xsl:text>
          </xsl:if>
          <xsl:value-of select="head"/>
        </bookmark-label>
        <xsl:choose>
          <xsl:when test="$depth='0'">
            <xsl:for-each select="div1">
              <xsl:call-template name="makeBookMarkN"/>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="$depth='1'">
            <xsl:for-each select="div2">
              <xsl:call-template name="makeBookMarkN"/>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="$depth='2'">
            <xsl:for-each select="div3">
              <xsl:call-template name="makeBookMarkN"/>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="$depth='3'">
            <xsl:for-each select="div4">
              <xsl:call-template name="makeBookMarkN"/>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="$depth='4'">
            <xsl:for-each select="div5">
              <xsl:call-template name="makeBookMarkN"/>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="$depth='5'">
            <xsl:for-each select="div6">
              <xsl:call-template name="makeBookMarkN"/>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </bookmark>
    </xsl:if>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="tocBits">
    <xsl:if test="$tocFront='true'">
      <xsl:for-each select="ancestor::text/front//div">
        <xsl:apply-templates mode="toc" select="(.)"/>
      </xsl:for-each>
    </xsl:if>
    <xsl:for-each select="ancestor::text/body//div">
      <xsl:apply-templates mode="toc" select="(.)"/>
    </xsl:for-each>
    <xsl:if test="$tocBack='true'">
      <xsl:for-each select="ancestor::text/back//div">
        <xsl:apply-templates mode="toc" select="(.)"/>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:param name="level">level</xd:param>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="tocheading">
    <xsl:param name="level"/>
    <xsl:variable name="tocindent">
      <xsl:choose>
        <xsl:when test="$level='0'">
          <xsl:value-of select="$div0Tocindent"/>
        </xsl:when>
        <xsl:when test="$level='1'">
          <xsl:value-of select="$div1Tocindent"/>
        </xsl:when>
        <xsl:when test="$level='2'">
          <xsl:value-of select="$div2Tocindent"/>
        </xsl:when>
        <xsl:when test="$level='3'">
          <xsl:value-of select="$div3Tocindent"/>
        </xsl:when>
        <xsl:when test="$level='4'">
          <xsl:value-of select="$div4Tocindent"/>
        </xsl:when>
        <xsl:when test="$level='5'">
          <xsl:value-of select="$div5Tocindent"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$div1Tocindent"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <block>
      <xsl:if test="$level='0'">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
      </xsl:if>
      <xsl:attribute name="text-indent">
        <xsl:value-of select="$tocindent"/>
      </xsl:attribute>
      <xsl:variable name="Number">
        <xsl:if test="$numberHeadings='true' and $numberHeadingsDepth &gt; $level">
          <xsl:call-template name="calculateNumber">
            <xsl:with-param name="numbersuffix" select="$tocNumberSuffix"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:variable>
      <xsl:value-of select="$Number"/>
      <xsl:text> </xsl:text>
      <inline>
        <xsl:apply-templates mode="section" select="head"/>
      </inline>
      <leader rule-thickness="0pt"/>
      <inline>
        <xsl:call-template name="linkStyle"/>
        <xsl:variable name="pagref">
          <xsl:choose>
            <xsl:when test="@id">
              <xsl:value-of select="@id"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="generate-id()"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <basic-link internal-destination="{$pagref}">
          <page-number-citation ref-id="{$pagref}"/>
        </basic-link>
      </inline>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>[fo] </xd:short>
    <xd:param name="level">level</xd:param>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="xheading">
    <xsl:param name="level"/>
    <xsl:if test="$numberHeadings='true'">
      <xsl:call-template name="calculateNumber"/>
    </xsl:if>
    <xsl:call-template name="divXRefHeading"/>
  </xsl:template>
</xsl:stylesheet>
