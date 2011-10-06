<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
  exclude-result-prefixes="exsl estr edate a fo local rng tei teix xd"
  extension-element-prefixes="exsl estr edate" version="1.0"
  xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"
  xmlns:edate="http://exslt.org/dates-and-times"
  xmlns:estr="http://exslt.org/strings" xmlns:exsl="http://exslt.org/common"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns:local="http://www.pantor.com/ns/local"
  xmlns:rng="http://relaxng.org/ns/structure/1.0"
  xmlns:tei="http://www.tei-c.org/ns/1.0"
  xmlns:teix="http://www.tei-c.org/ns/Examples"
  xmlns:xd="http://www.pnp-software.com/XSLTdoc"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xd:doc type="stylesheet">
    <xd:short> TEI stylesheet dealing with elements from the linking module,
      making HTML output. </xd:short>
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
    <xd:cvsId>$Id: linking.xsl 7205 2010-02-21 13:09:36Z rahtz $</xd:cvsId>
    <xd:copyright>2008, TEI Consortium</xd:copyright>
  </xd:doc>
  <xd:doc>
    <xd:short>Process elements * to work out a unique identififying string</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="*" mode="ident">
    <xsl:variable name="BaseFile">
      <xsl:value-of select="$masterFile"/>
      <xsl:call-template name="addCorpusID"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="@xml:id and $useIDs='true'">
        <xsl:value-of select="@xml:id"/>
      </xsl:when>
      <xsl:when test="starts-with(local-name(.),'div')">
        <xsl:variable name="xpath">
          <xsl:for-each select="ancestor-or-self::tei:*">
	    <xsl:value-of select="local-name()"/>
	    <xsl:text>.</xsl:text>
	    <xsl:number/>
	    <xsl:if test="not(position() = last())">
	      <xsl:text>_</xsl:text>
	    </xsl:if>
	  </xsl:for-each>
	</xsl:variable>
	<xsl:if test="ancestor::tei:group">
	  <xsl:value-of select="$BaseFile"/>
	  <xsl:text>-</xsl:text>
	</xsl:if>
        <xsl:value-of select="substring-after(substring-after($xpath,'_text.'),'_')"/>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$BaseFile"/>-<xsl:value-of
          select="local-name(.)"/>-<xsl:value-of select="generate-id()"
      /></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements *</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="*" mode="generateLink">
    <xsl:variable name="ident">
      <xsl:apply-templates mode="ident" select="."/>
    </xsl:variable>
    <xsl:variable name="depth">
      <xsl:apply-templates mode="depth" select="."/>
    </xsl:variable>
    <xsl:variable name="Hash">
      <xsl:text>#</xsl:text>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$rawXML='true' and $depth &lt;= $splitLevel">
        <xsl:text>JavaScript:void(gotoSection('','</xsl:text>
        <xsl:value-of select="$ident"/>
        <xsl:text>'));</xsl:text>
      </xsl:when>
      <xsl:when test="$STDOUT='true' and $depth &lt;= $splitLevel">
        <xsl:value-of select="$masterFile"/>
        <xsl:value-of select="$standardSuffix"/>
        <xsl:value-of select="$urlChunkPrefix"/>
        <xsl:value-of select="$ident"/>
      </xsl:when>
      <xsl:when test="ancestor::tei:elementSpec and
		      not($STDOUT='true')">
	<xsl:text>ref-</xsl:text>
	<xsl:value-of select="ancestor::tei:elementSpec/@ident"/>
        <xsl:value-of select="$standardSuffix"/>
	<xsl:value-of select="concat($Hash,$ident)"/>
      </xsl:when>
      <xsl:when test="ancestor::tei:classSpec and
		      not($STDOUT='true')">
	<xsl:text>ref-</xsl:text>
	<xsl:value-of select="ancestor::tei:classSpec/@ident"/>
        <xsl:value-of select="$standardSuffix"/>
	<xsl:value-of select="concat($Hash,$ident)"/>
      </xsl:when>
      <xsl:when test="ancestor::tei:back and not($splitBackmatter)">
        <xsl:value-of select="concat($Hash,$ident)"/>
      </xsl:when>
      <xsl:when test="ancestor::tei:front and not($splitFrontmatter)">
        <xsl:value-of select="concat($Hash,$ident)"/>
      </xsl:when>
      <xsl:when test="$splitLevel= -1 and ancestor::tei:teiCorpus">
        <xsl:value-of select="$masterFile"/>
        <xsl:call-template name="addCorpusID"/>
        <xsl:value-of select="$standardSuffix"/>
        <xsl:value-of select="concat($Hash,$ident)"/>
      </xsl:when>
      <xsl:when test="$splitLevel= -1">
        <xsl:value-of select="concat($Hash,$ident)"/>
      </xsl:when>
      <xsl:when test="$depth &lt;= $splitLevel">
        <xsl:value-of select="concat($ident,$standardSuffix)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="parent">
          <xsl:call-template name="locateParentdiv"/>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="$rawXML='true'">
            <xsl:text>JavaScript:void(gotoSection("</xsl:text>
            <xsl:value-of select="$ident"/>
            <xsl:text>","</xsl:text>
            <xsl:value-of select="$parent"/>
            <xsl:text>"));</xsl:text>
          </xsl:when>
          <xsl:when test="$STDOUT='true'">
            <xsl:value-of select="$masterFile"/>
            <xsl:value-of select="$urlChunkPrefix"/>
            <xsl:value-of select="$parent"/>
            <xsl:text>#</xsl:text>
            <xsl:value-of select="$ident"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$parent"/>
            <xsl:value-of select="concat($standardSuffix,'#')"/>
            <xsl:value-of select="$ident"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process element TEI in generateLink mode</xd:short>
    <xd:detail>
      <p> when a &lt;div&gt; is referenced, see whether its plain
        anchor, or needs a parent HTML name prepended </p>
    </xd:detail>
  </xd:doc>
  <xsl:template match="tei:TEI" mode="generateLink">
    <xsl:variable name="BaseFile">
      <xsl:value-of select="$masterFile"/>
      <xsl:call-template name="addCorpusID"/>
    </xsl:variable>
    <xsl:value-of select="concat($BaseFile,$standardSuffix)"/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements tei:anchor</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:anchor">
      <xsl:call-template name="makeAnchor"/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process extra elements in generateLink mode</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template
    match="tei:label|tei:figure|tei:table|tei:item|tei:p|tei:bibl|tei:anchor|tei:cell|tei:lg|tei:list|tei:sp"
    mode="generateLink">
    <xsl:variable name="ident">
      <xsl:apply-templates mode="ident" select="."/>
    </xsl:variable>
    <xsl:variable name="file">
      <xsl:apply-templates mode="generateLink"
        select="ancestor::tei:*[starts-with(local-name(),'div')][1]"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="starts-with($file,'#')">
        <xsl:text>#</xsl:text>
        <xsl:value-of select="$ident"/>
      </xsl:when>
      <xsl:when test="contains($file,'#')">
        <xsl:value-of select="substring-before($file,'#')"/>
        <xsl:text>#</xsl:text>
        <xsl:value-of select="$ident"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$file"/>
        <xsl:text>#</xsl:text>
        <xsl:value-of select="$ident"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements tei:note</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tei:note" mode="generateLink">
    <xsl:text>#</xsl:text>
    <xsl:call-template name="noteN"/>
  </xsl:template>
  <xd:doc>
    <xd:short>[html] </xd:short>
    <xd:param name="where">where</xd:param>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="generateEndLink">
    <xsl:param name="where"/>
    <!--
<xsl:message>find link end for <xsl:value-of select="$where"/>,<xsl:value-of select="name(key('IDS',$where))"/></xsl:message>
-->
    <xsl:choose>
      <xsl:when test="key('IDS',$where)">
	<xsl:apply-templates mode="generateLink"
			     select="key('IDS',$where)"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:text>[[undefined </xsl:text>
	<xsl:value-of select="$where"/>
	<xsl:text>]]</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>[html] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="locateParent">
    <xsl:choose>
      <xsl:when test="self::tei:div">
        <xsl:apply-templates mode="ident"
          select="ancestor::tei:div[last() - $splitLevel + 1]"/>
      </xsl:when>
      <xsl:when test="ancestor::tei:div">
        <xsl:apply-templates mode="ident"
          select="ancestor::tei:div[last() - $splitLevel]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$splitLevel = 0">
            <xsl:apply-templates mode="ident"
              select="ancestor::tei:div1|ancestor::tei:div0"/>
          </xsl:when>
          <xsl:when test="$splitLevel = 1">
            <xsl:apply-templates mode="ident"
              select="ancestor::tei:div2|ancestor::tei:div1|ancestor::tei:div0"
            />
          </xsl:when>
          <xsl:when test="$splitLevel = 2">
            <xsl:apply-templates mode="ident"
              select="ancestor::tei:div3|ancestor::tei:div2"/>
          </xsl:when>
          <xsl:when test="$splitLevel = 3">
            <xsl:apply-templates mode="ident"
              select="ancestor::tei:div4|ancestor::tei:div3"/>
          </xsl:when>
          <xsl:when test="$splitLevel = 4">
            <xsl:apply-templates mode="ident"
              select="ancestor::tei:div5|ancestor::tei:div4"/>
          </xsl:when>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>[html] </xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="locateParentdiv">
    <xsl:choose>
      <xsl:when test="ancestor-or-self::tei:div and $splitLevel &lt; 0">
        <xsl:apply-templates mode="ident" select="ancestor::tei:div[last()]"/>
      </xsl:when>
      <xsl:when test="ancestor-or-self::tei:div">
        <xsl:apply-templates mode="ident"
          select="ancestor::tei:div[last() - $splitLevel]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$splitLevel = 0">
            <xsl:apply-templates mode="ident"
              select="ancestor::tei:div1|ancestor::tei:div0"/>
          </xsl:when>
          <xsl:when test="$splitLevel = 1">
            <xsl:apply-templates mode="ident"
              select="(ancestor::tei:div2|ancestor::tei:div1|ancestor::tei:div0)[last()]"
            />
          </xsl:when>
          <xsl:when test="$splitLevel = 2">
            <xsl:apply-templates mode="ident"
              select="(ancestor::tei:div3|ancestor::tei:div2)[last()]"/>
          </xsl:when>
          <xsl:when test="$splitLevel = 3">
            <xsl:apply-templates mode="ident"
              select="(ancestor::tei:div4|ancestor::tei:div3)[last()]"/>
          </xsl:when>
          <xsl:when test="$splitLevel = 4">
            <xsl:apply-templates mode="ident"
              select="(ancestor::tei:div5|ancestor::tei:div4)[last()]"/>
          </xsl:when>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xd:doc>
    <xd:short>[html] create external link</xd:short>
    <xd:param name="ptr">ptr</xd:param>
    <xd:param name="dest">dest</xd:param>
    <xd:param name="class">class</xd:param>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="makeExternalLink">
    <xsl:param name="ptr"/>
    <xsl:param name="dest"/>
    <xsl:param name="class">
      <xsl:text>link_</xsl:text>
      <xsl:value-of select="local-name(.)"/>
    </xsl:param>
    <a>
        <xsl:choose>
          <xsl:when test="@rend">
	    <xsl:attribute name="class">
	      <xsl:value-of select="@rend"/>
	    </xsl:attribute>
          </xsl:when>
	  <xsl:when test="@rendition">
	    <xsl:call-template name="applyRendition"/>
	  </xsl:when>
          <xsl:when test="parent::tei:item/parent::tei:list[@rend]">
	    <xsl:attribute name="class">
	      <xsl:value-of
		  select="parent::tei:item/parent::tei:list/@rend"/>
	    </xsl:attribute>
          </xsl:when>
	  <xsl:when test="parent::tei:item[@rend]">
	    <xsl:attribute name="class">
	      <xsl:value-of select="parent::tei:item/@rend"/>
	    </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
	    <xsl:attribute name="class">
	      <xsl:value-of select="$class"/>
	    </xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>

      <xsl:if test="@type">
        <xsl:attribute name="type">
          <xsl:value-of select="@type"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="href">
        <xsl:value-of select="$dest"/>
        <xsl:if test="contains(@from,'id (')">
          <xsl:text>#</xsl:text>
          <xsl:value-of
            select="substring(@from,5,string-length(normalize-space(@from))-1)"
          />
        </xsl:if>
      </xsl:attribute>
      <xsl:if test="$xhtml='false'">
        <xsl:choose>
          <xsl:when test="@rend='new'">
            <xsl:attribute name="target">_blank</xsl:attribute>
          </xsl:when>
          <xsl:when
            test="@rend='noframe' or $splitLevel=-1 or substring(@url,string-length(@url),1)='/'">
            <xsl:attribute name="target">_top</xsl:attribute>
          </xsl:when>
          <xsl:when
            test="contains($dest,'://') or starts-with($dest,'.') or starts-with($dest,'/')">
            <xsl:attribute name="target">_top</xsl:attribute>
          </xsl:when>
          <xsl:when test="substring($dest,string-length($dest),1)='/'">
            <xsl:attribute name="target">_top</xsl:attribute>
          </xsl:when>
          <xsl:when test="$splitLevel=-1">
            <xsl:attribute name="target">_top</xsl:attribute>
          </xsl:when>
        </xsl:choose>
      </xsl:if>
      <xsl:attribute name="title">
        <xsl:choose>
          <xsl:when test="@n">
            <xsl:value-of select="@n"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="normalize-space(.)"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:call-template name="xrefHook"/>
      <xsl:choose>
        <xsl:when test="$ptr='true'">
          <xsl:element name="{$urlMarkup}">
            <xsl:choose>
              <xsl:when test="starts-with($dest,'mailto:')">
                <xsl:value-of select="substring-after($dest,'mailto:')"/>
              </xsl:when>
              <xsl:when test="starts-with($dest,'file:')">
                <xsl:value-of select="substring-after($dest,'file:')"/>
              </xsl:when>
	      <xsl:when test="$dest=''">
		<xsl:text>??</xsl:text>
	      </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$dest"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:element>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </a>
  </xsl:template>
  <xd:doc>
    <xd:short>[html] create an internal link</xd:short>
    <xd:param name="target">target</xd:param>
    <xd:param name="ptr">ptr</xd:param>
    <xd:param name="dest">dest</xd:param>
    <xd:param name="body">body</xd:param>
    <xd:param name="class">class</xd:param>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template name="makeInternalLink">
    <xsl:param name="target"/>
    <xsl:param name="ptr"/>
    <xsl:param name="dest"/>
    <xsl:param name="body"/>
    <xsl:param name="class">
      <xsl:text>link_</xsl:text>
      <xsl:value-of select="local-name(.)"/>
    </xsl:param>
    <xsl:variable name="W">
      <xsl:choose>
        <xsl:when test="$target">
          <xsl:value-of select="$target"/>
        </xsl:when>
        <xsl:when test="contains($dest,'#')">
          <xsl:value-of select="substring-after($dest,'#')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$dest"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$dest=''">
        <xsl:choose>
          <xsl:when test="not($body='')">
            <xsl:value-of select="$body"/>
          </xsl:when>
          <xsl:when test="$ptr='true'">
            <xsl:apply-templates mode="xref" select="key('IDS',$W)">
              <xsl:with-param name="minimal" select="$minimalCrossRef"/>
            </xsl:apply-templates>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <a>
	  <xsl:call-template name="htmlAttributes"/>
            <xsl:choose>
	      <xsl:when test="@rend">
		<xsl:attribute name="class">
		  <xsl:value-of select="@rend"/>
		</xsl:attribute>
              </xsl:when>
	      <xsl:when test="@rendition">
		<xsl:call-template name="applyRendition"/>
	      </xsl:when>
              <xsl:otherwise>
		<xsl:attribute name="class">
		  <xsl:value-of select="$class"/>
		</xsl:attribute>
              </xsl:otherwise>
            </xsl:choose>
          <xsl:attribute name="href">
            <xsl:choose>
              <xsl:when
                test="starts-with($dest,'#') or  contains($dest,$outputSuffix) or contains($dest,'ID=')">
                <xsl:value-of select="$dest"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates mode="generateLink" 
				     select="key('IDS',$W)"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <xsl:for-each select="key('IDS',$W)">
            <xsl:attribute name="title">
              <xsl:choose>
                <xsl:when test="starts-with(local-name(.),'div')">
                  <xsl:value-of select="normalize-space(tei:head)"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="normalize-space(text())"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
          </xsl:for-each>
          <xsl:choose>
            <xsl:when test="not($body='')">
              <xsl:value-of select="$body"/>
            </xsl:when>
            <xsl:when test="$ptr='true' and count(key('IDS',$W))&gt;0">
              <xsl:apply-templates mode="xref" select="key('IDS',$W)">
                <xsl:with-param name="minimal" select="$minimalCrossRef"/>
              </xsl:apply-templates>
            </xsl:when>
            <xsl:when test="$ptr='true'">
	      <xsl:text>??</xsl:text>
	    </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates/>
            </xsl:otherwise>
          </xsl:choose>
        </a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
