<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
		xmlns:mathml="http://www.w3.org/1998/Math/MathML"
		xmlns:svg="http://www.w3.org/2000/svg"
		xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:tbx="http://www.lisa.org/TBX-Specification.33.0.html"
		xmlns:iso="http://www.iso.org/ns/1.0"
		xmlns:cals="http://www.oasis-open.org/specs/tm9901"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:teix="http://www.tei-c.org/ns/Examples"
                xmlns:s="http://www.ascc.net/xml/schematron"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:tei="http://www.tei-c.org/ns/1.0"
                xmlns:t="http://www.thaiopensource.com/ns/annotations"
                xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"
                xmlns:rng="http://relaxng.org/ns/structure/1.0"
                exclude-result-prefixes="tei html t a rng s iso tbx
					 cals svg xlink mathml teix"
                version="2.0">
    <xsl:import href="../../../epub/tei-to-epub.xsl"/>

  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
      <desc>
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
         <p>Id: $Id: to.xsl 8920 2011-05-24 21:02:39Z rahtz $</p>
         <p>Copyright: 2008, TEI Consortium</p>
      </desc>
   </doc>

   <xsl:param name="cssFile">../profiles/oucs/epub/oucs.css</xsl:param>
    
    <xsl:template match="tei:w[@type and @lemma]">
      <span class="wordtype{@type}">
	<xsl:apply-templates/>
      </span>
    </xsl:template>

    <xsl:template match="tei:figure[svg:svg]">
      <xsl:copy-of select="svg:svg"/>
    </xsl:template>

    <xsl:template match="tei:formula[mathml:math]">
      <xsl:copy-of select="mathml:math"/>
    </xsl:template>
</xsl:stylesheet>
