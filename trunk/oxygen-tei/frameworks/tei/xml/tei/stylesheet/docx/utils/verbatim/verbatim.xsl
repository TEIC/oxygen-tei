<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:sch="http://www.ascc.net/xml/schematron" xmlns:m="http://www.w3.org/1998/Math/MathML" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:dbk="http://docbook.org/ns/docbook" xmlns:rng="http://relaxng.org/ns/structure/1.0" xmlns:tei="http://www.tei-c.org/ns/1.0" xmlns:teix="http://www.tei-c.org/ns/Examples" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" exclude-result-prefixes="xlink xhtml dbk rng sch m tei teix atom">
  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
    <desc>
      <p> TEI stylesheet for creating verbatim XML </p>
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
      <p>Id: $Id: verbatim.xsl 7703 2010-06-16 21:44:41Z rahtz $</p>
      <p>Copyright: 2008, TEI Consortium</p>
    </desc>
  </doc>
  <xsl:strip-space elements="teix:* rng:* xsl:* xhtml:* atom:* m:*"/>
  <xsl:param name="startComment">&lt;span class="comment"&gt;</xsl:param>
  <xsl:param name="endComment">&lt;/span&gt;</xsl:param>
  <xsl:param name="startElement">&lt;span class="element"&gt;</xsl:param>
  <xsl:param name="endElement">&lt;/span&gt;</xsl:param>
  <xsl:param name="startElementName">&lt;span class="elementname"&gt;</xsl:param>
  <xsl:param name="endElementName">&lt;/span&gt;</xsl:param>
  <xsl:param name="startAttribute">&lt;span class="attribute"&gt;</xsl:param>
  <xsl:param name="endAttribute">&lt;/span&gt;</xsl:param>
  <xsl:param name="startAttributeValue">&lt;span class="attributevalue"&gt;</xsl:param>
  <xsl:param name="endAttributeValue">&lt;/span&gt;</xsl:param>
  <xsl:param name="startNamespace">&lt;span class="namespace"&gt;</xsl:param>
  <xsl:param name="endNamespace">&lt;/span&gt;</xsl:param>
  <xsl:param name="spaceCharacter"> </xsl:param>
  <xsl:param name="showNamespaceDecls">true</xsl:param>
  <xsl:param name="forceWrap">false</xsl:param>
  <xsl:param name="wrapLength">65</xsl:param>
  <xsl:param name="attLength">40</xsl:param>
  <xsl:param name="attsOnSameLine">3</xsl:param>
  <xsl:key name="Namespaces" match="*[ancestor::teix:egXML]" use="namespace-uri()"/>
  <xsl:key name="Namespaces" match="*[not(ancestor::*)]" use="namespace-uri()"/>
  <xsl:template name="verbatim-newLine"/>
  <xsl:template name="verbatim-getNamespacePrefix">
    <xsl:variable name="ns" select="namespace-uri()"/>
    <xsl:choose>
      <xsl:when test="$ns='http://www.w3.org/XML/1998/namespace'">xml</xsl:when>
      <xsl:when test="$ns='http://www.tei-c.org/ns/1.0'">tei</xsl:when>
      <xsl:when test="$ns='http://docbook.org/ns/docbook'">dbk</xsl:when>
      <xsl:when test="$ns='http://www.w3.org/2001/XMLSchema'">xsd</xsl:when>
      <xsl:when test="$ns='http://www.ascc.net/xml/schematron'">sch</xsl:when>
      <xsl:when test="$ns='http://www.w3.org/1998/Math/MathML'">m</xsl:when>
      <xsl:when test="$ns='http://purl.oclc.org/dsdl/nvdl/ns/structure/1.0'">nvdl</xsl:when>
      <xsl:when test="$ns='http://relaxng.org/ns/compatibility/annotations/1.0'">a</xsl:when>
      <xsl:when test="$ns='http://www.w3.org/1999/xhtml'">xhtml</xsl:when>
      <xsl:when test="$ns='http://www.w3.org/1999/xlink'">xlink</xsl:when>
      <xsl:when test="$ns='http://relaxng.org/ns/structure/1.0'">rng</xsl:when>
      <xsl:when test="$ns='http://earth.google.com/kml/2.1'">kml</xsl:when>
      <xsl:when test="$ns='http://www.w3.org/2005/11/its'">its</xsl:when>
      <xsl:when test="$ns='http://www.w3.org/1999/XSL/Transform'">xsl</xsl:when>
      <xsl:when test="$ns='http://www.w3.org/2005/Atom'">atom</xsl:when>
      <xsl:when test="$ns='http://purl.org/rss/1.0/modules/event/'">ev</xsl:when>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="verbatim-lineBreak">
    <xsl:param name="id"/>
    <xsl:text>
</xsl:text>
  </xsl:template>
  <xsl:template match="comment()" mode="verbatim">
    <xsl:choose>
      <xsl:when test="ancestor::Wrapper"/>
      <xsl:when test="ancestor::xhtml:Wrapper"/>
      <xsl:otherwise>
        <xsl:call-template name="verbatim-lineBreak">
          <xsl:with-param name="id">21</xsl:with-param>
        </xsl:call-template>
        <xsl:value-of disable-output-escaping="yes" select="$startComment"/>
        <xsl:text>&lt;!--</xsl:text>
        <xsl:choose>
          <xsl:when test="$forceWrap='true'">
            <xsl:call-template name="verbatim-reformatText">
              <xsl:with-param name="sofar">0</xsl:with-param>
              <xsl:with-param name="indent">
                <xsl:text> </xsl:text>
              </xsl:with-param>
              <xsl:with-param name="text">
                <xsl:value-of select="normalize-space(.)"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="."/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text>--&gt;</xsl:text>
        <xsl:value-of disable-output-escaping="yes" select="$endComment"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="text()" mode="verbatim">
    <xsl:choose>
      <xsl:when test="$forceWrap='true'">
        <xsl:variable name="indent">
          <xsl:for-each select="parent::*">
            <xsl:call-template name="verbatim-makeIndent"/>
          </xsl:for-each>
        </xsl:variable>
        <xsl:if test="string-length(.)&gt;$wrapLength or parent::sch:assert">
          <xsl:text>
</xsl:text>
          <xsl:value-of select="$indent"/>
        </xsl:if>
        <xsl:call-template name="verbatim-reformatText">
          <xsl:with-param name="sofar">0</xsl:with-param>
          <xsl:with-param name="indent">
            <xsl:value-of select="$indent"/>
          </xsl:with-param>
          <xsl:with-param name="text">
            <xsl:value-of select="normalize-space(.)"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:if test="string-length(.)&gt;$wrapLength or parent::sch:assert">
          <xsl:text>
</xsl:text>
          <xsl:value-of select="$indent"/>
        </xsl:if>
      </xsl:when>
      <xsl:when test="not(preceding-sibling::node() or         contains(.,'&#10;'))">
        <xsl:if test="starts-with(.,' ')">
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:call-template name="verbatim-Text">
          <xsl:with-param name="words">
            <xsl:value-of select="normalize-space(.)"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:if test="substring(.,string-length(.),1)=' '">
          <xsl:text> </xsl:text>
        </xsl:if>
      </xsl:when>
      <xsl:when test="normalize-space(.)=''">
        <xsl:for-each select="following-sibling::*[1]">
          <xsl:call-template name="verbatim-lineBreak">
            <xsl:with-param name="id">7</xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="verbatim-makeIndent"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="verbatim-wraptext">
          <xsl:with-param name="count">0</xsl:with-param>
          <xsl:with-param name="indent">
            <xsl:for-each select="parent::*">
              <xsl:call-template name="verbatim-makeIndent"/>
            </xsl:for-each>
          </xsl:with-param>
          <xsl:with-param name="text">
            <xsl:choose>
              <xsl:when test="starts-with(.,'&#10;') and not          (preceding-sibling::node())">
                <xsl:value-of select="translate(substring(.,2),'&#10;','⌤')"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="translate(.,'&#10;','⌤')"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
        <!--
	<xsl:if test="substring(.,string-length(.))=' '">
	  <xsl:text> </xsl:text>
	</xsl:if>
	-->
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="verbatim-reformatText">
    <xsl:param name="indent"/>
    <xsl:param name="text"/>
    <xsl:param name="sofar"/>
    <xsl:choose>
      <xsl:when test="$sofar&gt;$wrapLength">
        <xsl:text>
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:call-template name="verbatim-reformatText">
          <xsl:with-param name="text">
            <xsl:value-of select="$text"/>
          </xsl:with-param>
          <xsl:with-param name="sofar">
            <xsl:text>0</xsl:text>
          </xsl:with-param>
          <xsl:with-param name="indent">
            <xsl:value-of select="$indent"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="not(contains($text,' '))">
        <xsl:call-template name="verbatim-Text">
          <xsl:with-param name="words">
            <xsl:value-of select="$text"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="chunk">
          <xsl:value-of select="substring-before($text,' ')"/>
        </xsl:variable>
        <xsl:call-template name="verbatim-Text">
          <xsl:with-param name="words">
            <xsl:value-of select="$chunk"/>
            <xsl:text> </xsl:text>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="verbatim-reformatText">
          <xsl:with-param name="text">
            <xsl:value-of select="substring-after($text,' ')"/>
          </xsl:with-param>
          <xsl:with-param name="sofar">
            <xsl:value-of select="$sofar + string-length($chunk) + 1"/>
          </xsl:with-param>
          <xsl:with-param name="indent">
            <xsl:value-of select="$indent"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="verbatim-wraptext">
    <xsl:param name="indent"/>
    <xsl:param name="text"/>
    <xsl:param name="count">0</xsl:param>
    <xsl:variable name="finalSpace">
      <xsl:choose>
        <xsl:when test="substring($text,string-length($text),1)=' '">
          <xsl:text> </xsl:text>
        </xsl:when>
        <xsl:when test="substring($text,string-length($text),1)='⌤'">
          <xsl:text> </xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <!--
<xsl:message>my text is [<xsl:value-of select="$text"/>]</xsl:message>
<xsl:message>my space is [<xsl:value-of select="$finalSpace"/>]</xsl:message>
-->
    <xsl:choose>
      <xsl:when test="normalize-space($text)=''"/>
      <xsl:when test="contains($text,'⌤')">
        <xsl:if test="$count &gt; 0">
          <xsl:value-of select="$indent"/>
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:if test="starts-with($text,' ')">
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:call-template name="verbatim-Text">
          <xsl:with-param name="words">
            <xsl:value-of select="normalize-space(substring-before($text,'⌤'))"/>
          </xsl:with-param>
        </xsl:call-template>
        <!--	<xsl:if test="not(substring-after($text,'&#10;')='')">-->
        <xsl:call-template name="verbatim-lineBreak">
          <xsl:with-param name="id">6</xsl:with-param>
        </xsl:call-template>
        <xsl:value-of select="$indent"/>
        <xsl:call-template name="verbatim-wraptext">
          <xsl:with-param name="indent">
            <xsl:value-of select="$indent"/>
          </xsl:with-param>
          <xsl:with-param name="text">
            <xsl:value-of select="normalize-space(substring-after($text,'⌤'))"/>
            <xsl:value-of select="$finalSpace"/>
          </xsl:with-param>
          <xsl:with-param name="count">
            <xsl:value-of select="$count + 1"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="starts-with($text,' ')">
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:if test="$count &gt; 0 and parent::*">
          <xsl:value-of select="$indent"/>
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:call-template name="verbatim-Text">
          <xsl:with-param name="words">
            <xsl:value-of select="normalize-space($text)"/>
            <xsl:value-of select="$finalSpace"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="verbatim-Text">
    <xsl:param name="words"/>
    <xsl:analyze-string select="." regex="(&amp;)">
      <xsl:matching-substring>
        <xsl:text>&amp;amp;</xsl:text>
      </xsl:matching-substring>
      <xsl:non-matching-substring>
        <xsl:value-of select="."/>
      </xsl:non-matching-substring>
    </xsl:analyze-string>
  </xsl:template>
  <xsl:template match="*" mode="verbatim">
    <xsl:choose>
      <xsl:when test="parent::xhtml:Wrapper"/>
      <!--      <xsl:when test="child::node()[last()]/self::text()[not(.='')] and child::node()[1]/self::text()[not(.='')]"/>-->
      <xsl:when test="not(parent::*)  or parent::teix:egXML">
        <xsl:choose>
          <xsl:when test="preceding-sibling::node()[1][self::text()]      and following-sibling::node()[1][self::text()]"/>
          <xsl:when test="preceding-sibling::*">
            <xsl:call-template name="verbatim-lineBreak">
              <xsl:with-param name="id">-1</xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="verbatim-newLine"/>
            <!-- <xsl:call-template name="makeIndent"/>-->
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="not(preceding-sibling::node())">
        <xsl:call-template name="verbatim-lineBreak">
          <xsl:with-param name="id">-2</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="verbatim-makeIndent"/>
      </xsl:when>
      <xsl:when test="preceding-sibling::node()[1]/self::*">
        <xsl:call-template name="verbatim-lineBreak">
          <xsl:with-param name="id">1</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="verbatim-makeIndent"/>
      </xsl:when>
      <xsl:when test="preceding-sibling::node()[1]/self::text()"> </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="verbatim-lineBreak">
          <xsl:with-param name="id">9</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="verbatim-makeIndent"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:value-of disable-output-escaping="yes" select="$startElement"/>
    <xsl:text>&lt;</xsl:text>
    <xsl:call-template name="verbatim-makeElementName">
      <xsl:with-param name="start">true</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates select="@*" mode="verbatim"/>
    <xsl:if test="$showNamespaceDecls='true' or parent::teix:egXML[@rend='full']">
      <!--
	  <xsl:variable name="me" select="."/>
	  <xsl:message><xsl:value-of select="name()"/>: </xsl:message>
	  <xsl:for-each select="in-scope-prefixes(.)">
	  <xsl:message>  .. <xsl:value-of select="."/>: <xsl:value-of select="namespace-uri-for-prefix(.,$me)"/> </xsl:message>
	  </xsl:for-each>
      -->
      <!-- 2009-02-01 stop emitting xmlns at all -->
      <!--
      <xsl:choose>
        <xsl:when test="not(parent::*)">
          <xsl:apply-templates select="." mode="ns"/>
        </xsl:when>
        <xsl:when test="parent::teix:egXML and not(preceding-sibling::*)">
          <xsl:apply-templates select="." mode="ns"/>
        </xsl:when>
      </xsl:choose>
      -->
    </xsl:if>
    <xsl:choose>
      <xsl:when test="child::node()">
        <xsl:text>&gt;</xsl:text>
        <xsl:value-of disable-output-escaping="yes" select="$endElement"/>
        <xsl:apply-templates mode="verbatim"/>
        <xsl:choose>
          <xsl:when test="child::node()[last()]/self::text() and child::node()[1]/self::text()"/>
          <xsl:when test="not(parent::*)  or parent::teix:egXML">
            <xsl:call-template name="verbatim-lineBreak">
              <xsl:with-param name="id">23</xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="child::node()[last()]/self::text()[normalize-space(.)='']">
            <xsl:call-template name="verbatim-lineBreak">
              <xsl:with-param name="id">3</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="verbatim-makeIndent"/>
          </xsl:when>
          <xsl:when test="child::node()[last()]/self::comment()">
            <xsl:call-template name="verbatim-lineBreak">
              <xsl:with-param name="id">4</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="verbatim-makeIndent"/>
          </xsl:when>
          <xsl:when test="child::node()[last()]/self::*">
            <xsl:call-template name="verbatim-lineBreak">
              <xsl:with-param name="id">5</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="verbatim-makeIndent"/>
          </xsl:when>
        </xsl:choose>
        <xsl:value-of disable-output-escaping="yes" select="$startElement"/>
        <xsl:text>&lt;/</xsl:text>
        <xsl:call-template name="verbatim-makeElementName">
          <xsl:with-param name="start">false</xsl:with-param>
        </xsl:call-template>
        <xsl:text>&gt;</xsl:text>
        <xsl:value-of disable-output-escaping="yes" select="$endElement"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>/&gt;</xsl:text>
        <xsl:value-of disable-output-escaping="yes" select="$endElement"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="verbatim-createElement">
    <xsl:param name="special"/>
    <xsl:param name="name"/>
    <xsl:value-of select="$name"/>
  </xsl:template>
  <xsl:template name="verbatim-createAttribute">
    <xsl:param name="name"/>
    <xsl:value-of select="$name"/>
  </xsl:template>
  <xsl:template name="verbatim-makeElementName">
    <xsl:param name="start"/>
    <!-- get namespace prefix -->
    <xsl:variable name="ns-prefix">
      <xsl:call-template name="verbatim-getNamespacePrefix"/>
    </xsl:variable>
    <xsl:variable name="ns-parent">
      <xsl:for-each select="parent::*">
        <xsl:value-of select="namespace-uri()"/>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="ns">
      <xsl:value-of select="namespace-uri()"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="namespace-uri()='http://www.tei-c.org/ns/Examples'">
        <xsl:call-template name="verbatim-createElement">
          <xsl:with-param name="name" select="local-name(.)"/>
          <xsl:with-param name="special"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="string-length($ns-prefix) &gt; 0">
        <xsl:call-template name="verbatim-createElement">
          <xsl:with-param name="name" select="concat($ns-prefix,concat(':',local-name(.)))"/>
          <xsl:with-param name="special"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$ns-parent=$ns">
        <xsl:call-template name="verbatim-createElement">
          <xsl:with-param name="name" select="local-name(.)"/>
          <xsl:with-param name="special"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="not($ns='')">
        <xsl:call-template name="verbatim-createElement">
          <xsl:with-param name="name" select="local-name(.)"/>
          <xsl:with-param name="special"/>
        </xsl:call-template>
        <xsl:if test="$start='true'">
          <xsl:text> xmlns="</xsl:text>
          <xsl:value-of select="namespace-uri()"/>
          <xsl:text>"</xsl:text>
          <xsl:call-template name="verbatim-lineBreak">
            <xsl:with-param name="id">5</xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="verbatim-makeIndent"/>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of disable-output-escaping="yes" select="$startElementName"/>
        <xsl:value-of select="local-name(.)"/>
        <xsl:value-of disable-output-escaping="yes" select="$endElementName"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="verbatim-makeIndent">
    <xsl:variable name="depth" select="count(ancestor::*[not(namespace-uri()='http://www.tei-c.org/ns/1.0')])"/>
    <xsl:call-template name="verbatim-makeSpace">
      <xsl:with-param name="d">
        <xsl:value-of select="$depth"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="verbatim-makeSpace">
    <xsl:param name="d"/>
    <xsl:if test="number($d)&gt;1">
      <xsl:value-of select="$spaceCharacter"/>
      <xsl:call-template name="verbatim-makeSpace">
        <xsl:with-param name="d">
          <xsl:value-of select="$d -1"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
  <xsl:template match="@*" mode="verbatim">
    <xsl:variable name="L">
      <xsl:for-each select="../@*">
        <xsl:value-of select="."/>
      </xsl:for-each>
    </xsl:variable>
    <xsl:if test="count(../@*)&gt;$attsOnSameLine or      string-length($L)&gt;$attLength or     ancestor::tei:cell[not(@rend='wovenodd-col2')] or     namespace-uri()='http://www.w3.org/2005/11/its' or     string-length(.)+string-length(name(.)) &gt;     $attLength">
      <xsl:call-template name="verbatim-lineBreak">
        <xsl:with-param name="id">5</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="verbatim-makeIndent"/>
    </xsl:if>
    <xsl:value-of select="$spaceCharacter"/>
    <xsl:variable name="ns-prefix">
      <xsl:call-template name="verbatim-getNamespacePrefix"/>
    </xsl:variable>
    <xsl:variable name="name">
      <xsl:choose>
        <xsl:when test="string-length($ns-prefix) &gt; 0">
          <xsl:value-of select="$ns-prefix"/>
          <xsl:text>:</xsl:text>
          <xsl:value-of select="local-name(.)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="local-name(.)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:call-template name="verbatim-createAttribute">
      <xsl:with-param name="name" select="$name"/>
    </xsl:call-template>
    <xsl:text>="</xsl:text>
    <xsl:value-of disable-output-escaping="yes" select="$startAttributeValue"/>
    <xsl:apply-templates select="." mode="attributetext"/>
    <xsl:value-of disable-output-escaping="yes" select="$endAttributeValue"/>
    <xsl:text>"</xsl:text>
  </xsl:template>
  <xsl:template match="@*" mode="attributetext">
    <xsl:choose>
      <xsl:when test="string-length(.)&gt;$attLength and contains(.,' ')">
        <xsl:call-template name="verbatim-reformatText">
          <xsl:with-param name="sofar">0</xsl:with-param>
          <xsl:with-param name="indent">
            <xsl:text> </xsl:text>
          </xsl:with-param>
          <xsl:with-param name="text">
            <xsl:value-of select="normalize-space(.)"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="text()|comment()|processing-instruction()" mode="ns"/>
  <xsl:template match="@*|*" mode="ns">
    <xsl:variable name="ns" select="namespace-uri()"/>
    <xsl:choose>
      <xsl:when test="$ns=''"/>
      <xsl:when test="$ns='http://relaxng.org/ns/structure/1.0'"/>
      <xsl:when test="$ns='http://www.w3.org/2001/XInclude'"/>
      <xsl:when test="$ns='http://www.tei-c.org/ns/Examples'"/>
      <xsl:when test="$ns='http://www.ascc.net/xml/schematron'"/>
      <xsl:when test="$ns='http://relaxng.org/ns/compatibility/annotations/1.0'"/>
      <xsl:when test="$ns='http://www.w3.org/XML/1998/namespace'"/>
      <xsl:otherwise>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="ns" select="@*|*"/>
  </xsl:template>
  <xsl:template name="ns">
    <xsl:param name="nsname"/>
    <xsl:call-template name="verbatim-lineBreak">
      <xsl:with-param name="id">22</xsl:with-param>
    </xsl:call-template>
    <xsl:text>   </xsl:text>
    <xsl:text>xmlns:</xsl:text>
    <xsl:value-of select="name(.)"/>
    <xsl:text>="</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>"</xsl:text>
  </xsl:template>
</xsl:stylesheet>
