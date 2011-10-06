<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:dbk="http://docbook.org/ns/docbook"
  xmlns:rng="http://relaxng.org/ns/structure/1.0"
  xmlns:tei="http://www.tei-c.org/ns/1.0"
  xmlns:teix="http://www.tei-c.org/ns/Examples"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"
  xmlns:edate="http://exslt.org/dates-and-times"
  xmlns:estr="http://exslt.org/strings" xmlns:exsl="http://exslt.org/common"
  xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns:pantor="http://www.pantor.com/ns/local"
  xmlns:xd="http://www.pnp-software.com/XSLTdoc"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  exclude-result-prefixes="xlink dbk rng tei teix xhtml a edate estr html pantor xd xs xsl"
  extension-element-prefixes="exsl estr edate" 
>
  <xsl:import href="../odds/teiodds.xsl"/>
  <xsl:import href="tei.xsl"/>
  <xsl:import href="tagdocs.xsl"/>
  <xsl:import href="../odds/RngToRnc.xsl"/>
  <xsl:param name="xhtml">true</xsl:param>
  <xd:doc type="stylesheet">
    <xd:short> TEI stylesheet for making LaTeX from ODD </xd:short>
    <xd:detail> This library is free software; you can redistribute it and/or
      modify it under the terms of the GNU Lesser General Public License as
      published by the Free Software Foundation; either version 2.1 of the
      License, or (at your option) any later version. This library is
      distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
      without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
      PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
      details. You should have received a copy of the GNU Lesser General Public
      License along with this library; if not, write to the Free Software
      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA </xd:detail>
    <xd:author>See AUTHORS</xd:author>
    <xd:cvsId>$Id: odd2latex.xsl 6657 2009-07-08 13:33:54Z rahtz $</xd:cvsId>
    <xd:copyright>2008, TEI Consortium</xd:copyright>
  </xd:doc>
  <xsl:key match="*" name="NameToID" use="@ident"/>
  <xsl:param name="oddmode">html</xsl:param>
  <xsl:param name="BITS">Bits</xsl:param>
  <xsl:param name="STDOUT">false</xsl:param>
  <xsl:param name="TAG"/>

  <xsl:output method="text" encoding="utf-8"/>
  <xsl:variable name="top" select="/"/>
  <xsl:template match="divGen[@type='index']"/>

  <xsl:template name="header_for_odd2html">
    <xsl:param name="minimal">false</xsl:param>
    <xsl:param name="toc"/>
    <xsl:variable name="depth">
      <xsl:apply-templates mode="depth" select="."/>
    </xsl:variable>
    <xsl:if test="$numberHeadingsDepth &gt;= $depth">
      <xsl:call-template name="calculateNumber">
        <xsl:with-param name="numbersuffix">
	  <xsl:call-template name="headingNumberSuffix"/>
	</xsl:with-param>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="$minimal='false'">
      <xsl:call-template name="headingNumberSuffix"/>
      <xsl:choose>
        <xsl:when test="contains(name(.),'Spec')">
          <xsl:call-template name="makeLink">
            <xsl:with-param name="class">toc</xsl:with-param>
            <xsl:with-param name="name">
              <xsl:value-of select="@ident"/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="not($toc='')">
          <xsl:call-template name="makeInternalLink">
            <xsl:with-param name="class">toc</xsl:with-param>
            <xsl:with-param name="dest">
              <xsl:value-of select="$toc"/>
            </xsl:with-param>
            <xsl:with-param name="body">
              <xsl:apply-templates mode="plain" select="head"/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates mode="plain" select="head"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

  <xsl:template name="headingNumberSuffix">
    <xsl:text> </xsl:text>
  </xsl:template>

  <xsl:template name="processSchemaFragment">
    <xsl:param name="filename"/>
    <div class="schemaFragment">
      <xsl:if test="classSpec">
        <h2>
          <xsl:call-template name="i18n">
            <xsl:with-param name="word">Classes defined</xsl:with-param>
          </xsl:call-template>
        </h2>
        <xsl:apply-templates mode="weave" select="classSpec">
          <xsl:sort select="altIdent|@ident"/>
        </xsl:apply-templates>
      </xsl:if>
      <xsl:if test="elementSpec">
        <h2>
          <xsl:call-template name="i18n">
            <xsl:with-param name="word">Elements defined</xsl:with-param>
          </xsl:call-template>
        </h2>
        <xsl:apply-templates mode="weave" select="elementSpec">
          <xsl:sort select="altIdent|@ident"/>
        </xsl:apply-templates>
      </xsl:if>
      <xsl:if test="macroSpec">
        <h2>
          <xsl:call-template name="i18n">
            <xsl:with-param name="word">Macros defined</xsl:with-param>
          </xsl:call-template>
        </h2>
        <xsl:apply-templates mode="weave" select="macroSpec">
          <xsl:sort select="altIdent|@ident"/>
        </xsl:apply-templates>
      </xsl:if>
      <xsl:apply-templates select="specGrpRef"/>
    </div>
  </xsl:template>

  <xsl:template name="listSpecs">
    <xsl:for-each select="..//schemaSpec">
      <hr/>
      <xsl:for-each select="classSpec">
        <xsl:sort select="altIdent"/>
        <xsl:sort select="@ident"/>
	<xsl:element name="{$tocElement}">
	  <xsl:attribute name="class">toclist0</xsl:attribute>
          <a xmlns="http://www.w3.org/1999/xhtml" 
	     class="toclist" href="#{@ident}">
            <xsl:choose>
              <xsl:when test="altIdent">
                <xsl:value-of select="altIdent"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="@ident"/>
              </xsl:otherwise>
            </xsl:choose>
          </a>
	</xsl:element>
      </xsl:for-each>
      <hr/>
      <xsl:for-each select="elementSpec">
        <xsl:sort select="altIdent"/>
        <xsl:sort select="@ident"/>
	<xsl:element name="{$tocElement}">
	  <xsl:attribute name="class">toclist0</xsl:attribute>
          <a xmlns="http://www.w3.org/1999/xhtml"
	     class="toclist" href="#{@ident}">
            <xsl:choose>
              <xsl:when test="altIdent">
                <xsl:value-of select="altIdent"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="@ident"/>
              </xsl:otherwise>
            </xsl:choose>
          </a>
        </xsl:element>
      </xsl:for-each>
      <hr/>
      <xsl:for-each select="macroSpec">
        <xsl:sort select="altIdent"/>
        <xsl:sort select="@ident"/>
	<xsl:element name="{$tocElement}">
	  <xsl:attribute name="class">toclist0</xsl:attribute>
          <a xmlns="http://www.w3.org/1999/xhtml"
	     class="toclist" href="#{@ident}">
            <xsl:choose>
              <xsl:when test="altIdent">
                <xsl:value-of select="altIdent"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="@ident"/>
              </xsl:otherwise>
            </xsl:choose>
          </a>
        </xsl:element>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

  <xd:doc>
    <xd:short>Process elements listRef</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="listRef" mode="weave"/>

  <xd:doc>
    <xd:short>Process elements ptr</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="ptr" mode="weave">
    <xsl:choose>
      <xsl:when test="parent::listRef">
	<xsl:choose>
	<xsl:when test="starts-with(@target,'#') and key('IDS',substring-after(@target,'#'))">
	  <xsl:call-template name="makeInternalLink">
	    <xsl:with-param name="target"
			    select="substring-after(@target,'#')"/>
	    <xsl:with-param name="ptr">true</xsl:with-param>
	    <xsl:with-param name="dest">
	      <xsl:call-template name="generateEndLink">
		<xsl:with-param name="where">
		  <xsl:value-of select="substring-after(@target,'#')"/>
		</xsl:with-param>
	      </xsl:call-template>
	    </xsl:with-param>
	  </xsl:call-template>
	</xsl:when>
	<xsl:when test="starts-with(@target,'#')">
	  <xsl:text>«</xsl:text>
	  <xsl:value-of select="@target"/>
	  <xsl:text>»</xsl:text>
	</xsl:when>
	<xsl:otherwise>
	    <xsl:apply-imports/>
	</xsl:otherwise>
	</xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-imports/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="rng:*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates select="rng:*|*|text()|comment()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="rng:zeroOrMore">
    <xsl:choose>
      <xsl:when test="count(rng:*)=1 and rng:zeroOrMore">
        <xsl:apply-templates select="rng:*|*|text()|comment()"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:copy-of select="@*"/>
          <xsl:apply-templates select="rng:*|*|text()|comment()"/>
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<xsl:template match="elementSpec[@mode='delete']" mode="weave"/>

<xsl:template match="elementSpec[@mode='delete']">
<xsl:text>\label[</xsl:text>
Element <xsl:value-of select="@ident"/>
<xsl:text>] </xsl:text>
<xsl:text>\textbf{DELETED}</xsl:text>
</xsl:template>

<xsl:template match="divGen[@type='toc']">

<xsl:call-template name="mainTOC"/>

<div><b>Classes:</b>
  <div class="oddToc">
    <xsl:for-each
	select="ancestor-or-self::text//classSpec">
    <xsl:sort select="@ident"/>
    <xsl:call-template name="oddTocEntry"/>
    </xsl:for-each>
  </div>
</div>

<div><b>Elements:</b>
  <div class="oddToc">
    <xsl:for-each
	select="ancestor-or-self::text//elementSpec">
    <xsl:sort select="@ident"/>
    <xsl:call-template name="oddTocEntry"/>
    </xsl:for-each>
  </div>
</div>

<div><b>Macros:</b>
  <div class="oddToc">
    <xsl:for-each
	select="ancestor-or-self::text//macroSpec">
    <xsl:sort select="@ident"/>
    <xsl:call-template name="oddTocEntry"/>
    </xsl:for-each>
  </div>
</div>

</xsl:template>

<xsl:template name="oddTocEntry">
    <xsl:variable name="loc">
      <xsl:choose>
      <xsl:when test="$splitLevel=-1 or $STDOUT='true'">
	<xsl:text>#</xsl:text>
	<xsl:value-of select="@ident"/>
      </xsl:when>
      <xsl:otherwise> 
	<xsl:text>ref-</xsl:text>
	<xsl:value-of select="@ident"/>
	<xsl:value-of select="$outputSuffix"/>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <div class="oddTocEntry">
      <a href="{$loc}">
	<xsl:value-of select="@ident"/>
      </a>
    </div>
</xsl:template>

<xsl:template name="lineBreak">
  <xsl:param name="id"/>
  <xsl:text>\\&#10;</xsl:text>
</xsl:template>

<xsl:template match="rng:ref/@name" mode="attributetext">
    <xsl:variable name="me">
      <xsl:choose>
        <xsl:when test="contains(.,'.attributes')">
          <xsl:value-of select="substring-before(.,'.attributes')"/>
        </xsl:when>
        <xsl:when test="contains(.,'.content')">
          <xsl:value-of select="substring-before(.,'.content')"/>
        </xsl:when>
        <xsl:when test="contains(.,'.attribute.')">
          <xsl:value-of select="substring-before(.,'.attribute.')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="n" select="."/>
    <xsl:choose>
      <xsl:when test="contains(.,'.localattributes')">
	<xsl:value-of select="$n"/>
      </xsl:when>
      <xsl:when test="contains(.,'.content')">
	<xsl:value-of select="$n"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:for-each select="$top">
	  <xsl:call-template name="linkTogether">
	    <xsl:with-param name="name">
	      <xsl:value-of select="$me"/>
	    </xsl:with-param>
	    <xsl:with-param name="reftext">
	      <xsl:value-of select="$n"/>
	    </xsl:with-param>
	  </xsl:call-template>
	</xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
