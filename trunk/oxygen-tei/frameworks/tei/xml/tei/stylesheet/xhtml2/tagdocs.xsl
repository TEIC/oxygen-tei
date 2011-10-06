<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet  xmlns:s="http://www.ascc.net/xml/schematron" xmlns="http://www.w3.org/1999/xhtml" xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:html="http://www.w3.org/1999/xhtml" xmlns:rng="http://relaxng.org/ns/structure/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" xmlns:teix="http://www.tei-c.org/ns/Examples" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="s html a fo rng tei teix" version="2.0">

  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
    <desc>
      <p> TEI stylesheet dealing with elements from the tagdocs module,
      making HTML output. </p>
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
      <p>Id: $Id: tagdocs.xsl 8976 2011-06-20 00:31:57Z rahtz $</p>
      <p>Copyright: 2011, TEI Consortium</p>
    </desc>
  </doc>

  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
    <desc>Process elements teix:egXML</desc>
  </doc>
  <xsl:template match="teix:egXML">
    <xsl:param name="simple">false</xsl:param>
    <xsl:param name="highlight"/>
    <div>
      <xsl:attribute name="id">
        <xsl:apply-templates mode="ident" select="."/>
      </xsl:attribute>
      <xsl:attribute name="class">
	<xsl:text>pre</xsl:text>
	<xsl:if test="not(*)">
	  <xsl:text> cdata</xsl:text>
	</xsl:if>
	<xsl:choose>
	  <xsl:when test="@valid='feasible'">
	    <xsl:text> egXML_feasible</xsl:text>
	  </xsl:when>
	  <xsl:when test="@valid='false'">
	    <xsl:text> egXML_invalid</xsl:text>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:text> egXML_valid</xsl:text>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:attribute>
      <xsl:choose>
        <xsl:when test="$simple='true'">
          <xsl:apply-templates mode="verbatim">
            <xsl:with-param name="highlight">
              <xsl:value-of select="$highlight"/>
            </xsl:with-param>
          </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="egXMLStartHook"/>
          <xsl:apply-templates mode="verbatim">
            <xsl:with-param name="highlight">
              <xsl:value-of select="$highlight"/>
            </xsl:with-param>
          </xsl:apply-templates>
          <xsl:call-template name="egXMLEndHook"/>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
    <desc>Process element ident</desc>
  </doc>
  <xsl:template match="tei:ident">
    <xsl:choose>
      <xsl:when test="@type">
        <span class="ident-{@type}">
          <xsl:apply-templates/>
        </span>
      </xsl:when>
      <xsl:otherwise>
        <span class="ident">
          <xsl:apply-templates/>
        </span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
    <desc>Process element gi</desc>
  </doc>
  <xsl:template match="tei:gi">
    <span class="gi">
      <xsl:text>&lt;</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>&gt;</xsl:text>
    </span>
  </xsl:template>
</xsl:stylesheet>
