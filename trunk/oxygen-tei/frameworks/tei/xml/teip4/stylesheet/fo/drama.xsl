<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xd="http://www.pnp-software.com/XSLTdoc" xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0" xmlns:edate="http://exslt.org/dates-and-times" xmlns:estr="http://exslt.org/strings" xmlns:exsl="http://exslt.org/common" xmlns="http://www.w3.org/1999/XSL/Format" xmlns:rng="http://relaxng.org/ns/structure/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" xmlns:teix="http://www.tei-c.org/ns/Examples" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" extension-element-prefixes="exsl estr edate" exclude-result-prefixes="xd exsl estr edate a rng tei teix" version="1.0">
  <xd:doc type="stylesheet">
    <xd:short>
    TEI stylesheet
    dealing  with elements from the
      drama module, making XSL-FO output.
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
    <xd:cvsId>$Id: drama.xsl 6983 2009-11-12 22:20:52Z rahtz $</xd:cvsId>
    <xd:copyright>2008, TEI Consortium</xd:copyright>
  </xd:doc>
  <xd:doc>
    <xd:short>Process elements  actor</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="actor">
    <inline>
      <xsl:call-template name="rend">
        <xsl:with-param name="defaultvalue" select="string('normal')"/>
        <xsl:with-param name="defaultstyle" select="string('font-style')"/>
      </xsl:call-template>
      <xsl:apply-templates/>
    </inline>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  camera</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="camera">
    <inline>
      <xsl:call-template name="rend">
        <xsl:with-param name="defaultvalue" select="string('normal')"/>
        <xsl:with-param name="defaultstyle" select="string('font-style')"/>
      </xsl:call-template>
      <xsl:apply-templates/>
    </inline>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  caption</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="caption">
    <inline>
      <xsl:call-template name="rend">
        <xsl:with-param name="defaultvalue" select="string('normal')"/>
        <xsl:with-param name="defaultstyle" select="string('font-style')"/>
      </xsl:call-template>
      <xsl:apply-templates/>
    </inline>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  castGroup</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="castGroup">
    <xsl:apply-templates/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  castItem</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="castItem">
    <xsl:call-template name="makeItem"/>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  castItem (when @type is 'list')</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="castItem[@type='list']">
    <list-item>
      <xsl:attribute name="space-before.optimum">
        <xsl:value-of select="$listItemsep"/>
      </xsl:attribute>
      <list-item-label end-indent="label-end()">
        <xsl:if test="@id">
          <xsl:attribute name="id">
            <xsl:value-of select="@id"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:text>&#10;</xsl:text>
        <block/>
      </list-item-label>
      <list-item-body start-indent="body-start()">
        <block>
          <xsl:call-template name="rend">
            <xsl:with-param name="defaultvalue" select="string('italic')"/>
            <xsl:with-param name="defaultstyle" select="string('font-style')"/>
          </xsl:call-template>
          <xsl:text>(</xsl:text>
          <xsl:apply-templates/>
          <xsl:text>)</xsl:text>
        </block>
      </list-item-body>
    </list-item>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  castList</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="castList">
    <xsl:if test="child::head">
      <block font-style="italic" text-align="start" space-before.optimum="4pt">
        <xsl:for-each select="head">
          <xsl:apply-templates/>
        </xsl:for-each>
      </block>
    </xsl:if>
    <list-block>
      <xsl:call-template name="setListIndents"/>
      <xsl:apply-templates/>
    </list-block>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  sp</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="sp">
    <block text-align="justify" start-indent="1em" text-indent="-1em" space-before="3pt">
      <xsl:apply-templates/>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  sp/p</xd:short>
    <xd:detail>
      <p> paragraphs inside speeches do very little</p>
    </xd:detail>
  </xd:doc>
  <xsl:template match="sp/p">
    <inline>
      <xsl:apply-templates/>
    </inline>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  speaker</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="speaker">
    <inline>
      <xsl:call-template name="rend">
        <xsl:with-param name="defaultvalue" select="string('italic')"/>
        <xsl:with-param name="defaultstyle" select="string('font-style')"/>
      </xsl:call-template>
      <xsl:apply-templates/>
      <xsl:text> </xsl:text>
    </inline>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  stage</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="stage">
    <block>
      <xsl:attribute name="text-indent">1em</xsl:attribute>
      <xsl:call-template name="rend">
        <xsl:with-param name="defaultvalue" select="string('italic')"/>
        <xsl:with-param name="defaultstyle" select="string('font-style')"/>
      </xsl:call-template>
      <xsl:apply-templates/>
    </block>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  p/stage</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="p/stage">
    <inline>
      <xsl:call-template name="rend">
        <xsl:with-param name="defaultvalue" select="string('italic')"/>
        <xsl:with-param name="defaultstyle" select="string('font-style')"/>
      </xsl:call-template>
      <xsl:apply-templates/>
    </inline>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  sp/stage</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="sp/stage">
    <inline>
      <xsl:call-template name="rend">
        <xsl:with-param name="defaultvalue" select="string('italic')"/>
        <xsl:with-param name="defaultstyle" select="string('font-style')"/>
      </xsl:call-template>
      <xsl:apply-templates/>
    </inline>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  tech</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="tech">
    <inline>
      <xsl:call-template name="rend">
        <xsl:with-param name="defaultvalue" select="string('italic')"/>
        <xsl:with-param name="defaultstyle" select="string('font-style')"/>
      </xsl:call-template>
      <xsl:apply-templates/>
    </inline>
  </xsl:template>
  <xd:doc>
    <xd:short>Process elements  view</xd:short>
    <xd:detail> </xd:detail>
  </xd:doc>
  <xsl:template match="view">
    <inline>
      <xsl:call-template name="rend">
        <xsl:with-param name="defaultvalue" select="string('italic')"/>
        <xsl:with-param name="defaultstyle" select="string('font-style')"/>
      </xsl:call-template>
      <xsl:apply-templates/>
    </inline>
  </xsl:template>
</xsl:stylesheet>
