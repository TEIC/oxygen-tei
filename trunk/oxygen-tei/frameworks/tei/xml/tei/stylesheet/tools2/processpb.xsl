<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns="http://www.tei-c.org/ns/1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xpath-default-namespace="http://www.tei-c.org/ns/1.0" version="2.0">
  <!-- This library is free software; you can redistribute it and/or
      modify it under the terms of the GNU Lesser General Public License as
      published by the Free Software Foundation; either version 2.1 of the
      License, or (at your option) any later version. This library is
      distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
      without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
      PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
      details. You should have received a copy of the GNU Lesser General Public
      License along with this library; if not, write to the Free Software
      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
      02111-1307 USA 

     $Id: processpb.xsl 9187 2011-08-04 10:14:20Z rahtz $

    Take an arbitrary TEI file and move page breaks (<pb>) up in the
    hierarchy, splitting containers as needed, until <pb>s are at the
    same level as <div>. Wrap the resulting pages on <page> element.
-->
  <xsl:output indent="yes"/>

  <xsl:template match="teiHeader">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="TEI|teiCorpus|group">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="*|processing-instruction()|comment()|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="text|body|back|front">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:variable name="pages">
        <xsl:apply-templates select="*|processing-instruction()|comment()|text()"/>
      </xsl:variable>
      <xsl:for-each select="$pages">
        <xsl:apply-templates select="*|processing-instruction()|comment()|text()" mode="pass2"/>
      </xsl:for-each>
    </xsl:copy>
  </xsl:template>


 <!-- first (recursive) pass. look for <pb> elements and group on them -->
  <xsl:template match="comment()|@*|processing-instruction()|text()">
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="*">
    <xsl:call-template name="checkpb">
      <xsl:with-param name="eName" select="local-name()"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="pb">
    <pb>
      <xsl:copy-of select="@*"/>
    </pb>
  </xsl:template>

  <xsl:template name="checkpb">
    <xsl:param name="eName"/>
    <xsl:choose>
      <xsl:when test="not(.//pb)">
        <xsl:copy>
          <xsl:apply-templates select="@*"/>
          <xsl:apply-templates select="*|processing-instruction()|comment()|text()"/>
        </xsl:copy>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="pass">
          <xsl:call-template name="groupbypb">
            <xsl:with-param name="Name" select="$eName"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:for-each select="$pass">
          <xsl:apply-templates/>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="groupbypb">
    <xsl:param name="Name"/>
    <xsl:for-each-group select="node()" group-starting-with="pb">
      <xsl:choose>
        <xsl:when test="self::pb">
          <xsl:copy-of select="."/>
          <xsl:element name="{$Name}">
	    <xsl:attribute name="rend">CONTINUED</xsl:attribute>
            <xsl:apply-templates select="current-group() except ."/>
          </xsl:element>
        </xsl:when>
        <xsl:otherwise>
          <xsl:element name="{$Name}">
            <xsl:for-each select="..">
              <xsl:copy-of select="@*"/>
              <xsl:apply-templates select="current-group()"/>
            </xsl:for-each>
          </xsl:element>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each-group>
  </xsl:template>

  <!-- second pass. group by <pb> (now all at top level) and wrap groups
       in <page> -->
  <xsl:template match="*" mode="pass2">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|processing-instruction()|comment()|text()" mode="pass2"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="*[pb]" mode="pass2">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:for-each-group select="*" group-starting-with="pb">
        <xsl:choose>
          <xsl:when test="self::pb">
            <page xmlns="http://www.tei-c.org/ns/notTEI"> 
              <xsl:copy-of select="@*"/>
              <xsl:copy-of select="current-group() except ."/>
            </page>
          </xsl:when>
          <xsl:otherwise>
            <xsl:copy-of select="current-group()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each-group>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="comment()|@*|processing-instruction()|text()" mode="pass2">
    <xsl:copy-of select="."/>
  </xsl:template>

</xsl:stylesheet>
