<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
    version="2.0" 
    xmlns:dc="http://purl.org/dc/elements/1.1/" 
    xmlns:iso="http://www.iso.org/ns/1.0" 
    xmlns="http://www.w3.org/1999/xhtml" 
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns:tei="http://www.tei-c.org/ns/1.0" 
    xmlns:teix="http://www.tei-c.org/ns/Examples" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:ncx="http://www.daisy.org/z3986/2005/ncx/" 
    exclude-result-prefixes="iso tei teix dc html ncx"
    xpath-default-namespace="http://www.tei-c.org/ns/1.0">

  

  <xsl:import href="../html5/tei.xsl"/>
  <xsl:import href="../epub/epub-common.xsl"/>
  <xsl:output method="xml" encoding="utf-8" indent="no"/>
  <xsl:key match="tei:graphic[not(ancestor::teix:egXML)]" use="1" name="G"/>
  <xsl:key name="GRAPHICS" use="1" match="tei:graphic"/>
  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
    <desc>
      <p>
	TEI stylesheet for making ePub output. A lot learnt from
	http://www.hxa.name/articles/content/epub-guide_hxa7241_2007.html and
	the stylesheets of the NZETC.
      </p>
      <p>This software is dual-licensed:

1. Distributed under a Creative Commons Attribution-ShareAlike 3.0
Unported License http://creativecommons.org/licenses/by-sa/3.0/ 

2. http://www.opensource.org/licenses/BSD-2-Clause
		
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

* Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not
limited to, the implied warranties of merchantability and fitness for
a particular purpose are disclaimed. In no event shall the copyright
holder or contributors be liable for any direct, indirect, incidental,
special, exemplary, or consequential damages (including, but not
limited to, procurement of substitute goods or services; loss of use,
data, or profits; or business interruption) however caused and on any
theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use
of this software, even if advised of the possibility of such damage.
</p>
      <p>Author: See AUTHORS</p>
      <p>Id: $Id: tei-to-epub3.xsl 9646 2011-11-05 23:39:08Z rahtz $</p>
      <p>Copyright: 2008, TEI Consortium</p>
    </desc>
  </doc>
  <xsl:param name="useHeaderFrontMatter">false</xsl:param>
  <xsl:param name="STDOUT">false</xsl:param>
  <xsl:param name="autoHead">true</xsl:param>
  <xsl:param name="autoToc">true</xsl:param>
  <xsl:param name="bottomNavigationPanel">false</xsl:param>
  <xsl:param name="coverimage"/>
  <xsl:param name="cssFile">../tei.css</xsl:param>
  <xsl:param name="cssODDFile">../odd.css</xsl:param>
  <xsl:param name="cssPrintFile">../epub-print.css</xsl:param>
  <xsl:param name="debug">false</xsl:param>
  <xsl:param name="directory">.</xsl:param>
  <xsl:param name="doctypePublic">-//W3C//DTD XHTML 1.1//EN</xsl:param>
  <xsl:param name="doctypeSystem">http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd</xsl:param>
  <xsl:param name="fixgraphicsurl">false</xsl:param>
  <xsl:param name="createanttask">false</xsl:param>
  <xsl:param name="institution"/>
  <xsl:param name="linkPanel">false</xsl:param>
  <xsl:param name="odd">false</xsl:param>
  <xsl:param name="inputDir">.</xsl:param>
  <xsl:param name="outputDir"><xsl:value-of select="$directory"/>/Content</xsl:param>
  <xsl:param name="publisher"/>
  <xsl:param name="splitLevel">0</xsl:param>
  <xsl:param name="subject"/>
  <xsl:param name="tocDepth">5</xsl:param>
  <xsl:param name="tocFront">true</xsl:param>
  <xsl:param name="topNavigationPanel">false</xsl:param>
  <xsl:param name="uid"/>
  <xsl:param name="outputTarget">epub</xsl:param>
  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
    <desc>(extensible) wrapper for root element</desc>
  </doc>
  <xsl:template match="/">
    <xsl:call-template name="processTEI"/>
  </xsl:template>
  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl">
    <desc>[epub] Override of top-level template. This does most of
  the work: performing the normal transformation, fixing the links to graphics files so that they are
  all relative, creating the extra output files, etc</desc>
  </doc>
  <xsl:template name="processTEI">
    <xsl:variable name="stage1">
      <xsl:apply-templates mode="fixgraphics"/>
    </xsl:variable>
    <xsl:for-each select="$stage1">
      <xsl:call-template name="processTEIHook"/>
      <xsl:variable name="coverImageOutside">
        <xsl:choose>
          <xsl:when test="/tei:TEI/tei:text/tei:front/tei:titlePage[@facs]">
            <xsl:for-each select="/tei:TEI/tei:text/tei:front/tei:titlePage[@facs][1]">
              <xsl:for-each select="key('IDS',substring(@facs,2))">
                <xsl:choose>
                  <xsl:when test="count(tei:graphic)=1">
                    <xsl:value-of select="tei:graphic/@url"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="tei:graphic[2]/@url"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:for-each>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="not($coverimage='')">
            <xsl:value-of select="$coverimage"/>
          </xsl:when>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="coverImageInside">
        <xsl:choose>
          <xsl:when test="/tei:TEI/tei:text/tei:front/tei:titlePage[@facs]">
            <xsl:for-each select="/tei:TEI/tei:text/tei:front/tei:titlePage[@facs][1]">
              <xsl:for-each select="key('IDS',substring(@facs,2))">
                <xsl:value-of select="tei:graphic[1]/@url"/>
              </xsl:for-each>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="not($coverimage='')">
            <xsl:value-of select="$coverimage"/>
          </xsl:when>
        </xsl:choose>
      </xsl:variable>
      <xsl:apply-templates mode="split"/>
      <xsl:for-each select="*">
        <xsl:variable name="TOC">
          <TOC xmlns="http://www.w3.org/1999/xhtml">
            <xsl:call-template name="mainTOC"/>
          </TOC>
        </xsl:variable>
        <!--
	    <xsl:result-document href="/tmp/TOC">
	    <xsl:copy-of select="$TOC"/>
	    </xsl:result-document>
	-->
        <xsl:if test="$debug='true'">
          <xsl:message>write file Content/stylesheet.css</xsl:message>
        </xsl:if>
        <xsl:result-document method="text" href="{concat($directory,'/Content/stylesheet.css')}">
          <xsl:if test="$debug='true'">
            <xsl:message>reading file <xsl:value-of select="$cssFile"/></xsl:message>
          </xsl:if>
          <xsl:for-each select="tokenize(unparsed-text($cssFile),     '\r?\n')">
            <xsl:call-template name="purgeCSS"/>
          </xsl:for-each>
          <xsl:if test="not($cssSecondaryFile='')">
            <xsl:if test="$debug='true'">
              <xsl:message>reading secondary file <xsl:value-of select="$cssSecondaryFile"/></xsl:message>
            </xsl:if>
            <xsl:for-each select="tokenize(unparsed-text($cssSecondaryFile),       '\r?\n')">
              <xsl:call-template name="purgeCSS"/>
            </xsl:for-each>
          </xsl:if>
          <xsl:if test="$odd='true'">
            <xsl:if test="$debug='true'">
              <xsl:message>reading file <xsl:value-of select="$cssODDFile"/></xsl:message>
            </xsl:if>
            <xsl:for-each select="tokenize(unparsed-text($cssODDFile),         '\r?\n')">
              <xsl:call-template name="purgeCSS"/>
            </xsl:for-each>
          </xsl:if>
          <xsl:if test="$odd='true'">
            <xsl:if test="$debug='true'">
              <xsl:message>reading file <xsl:value-of select="$cssODDFile"/></xsl:message>
            </xsl:if>
            <xsl:for-each select="tokenize(unparsed-text($cssODDFile),         '\r?\n')">
              <xsl:call-template name="purgeCSS"/>
            </xsl:for-each>
          </xsl:if>
        </xsl:result-document>
        <xsl:if test="$debug='true'">
          <xsl:message>write file Content/print.css</xsl:message>
        </xsl:if>
        <xsl:result-document method="text" href="{concat($directory,'/Content/print.css')}">
          <xsl:if test="$debug='true'">
            <xsl:message>reading file <xsl:value-of select="$cssPrintFile"/></xsl:message>
          </xsl:if>
          <xsl:for-each select="tokenize(unparsed-text($cssPrintFile),     '\r?\n')">
            <xsl:call-template name="purgeCSS"/>
          </xsl:for-each>
        </xsl:result-document>
        <xsl:if test="$debug='true'">
          <xsl:message>write file mimetype</xsl:message>
        </xsl:if>
        <xsl:result-document method="text" href="{concat($directory,'/mimetype')}">
          <xsl:text>application/epub+zip</xsl:text>
        </xsl:result-document>
        <xsl:if test="$debug='true'">
          <xsl:message>write file META-INF/container.xml</xsl:message>
        </xsl:if>
        <xsl:result-document method="xml" href="{concat($directory,'/META-INF/container.xml')}">
          <container xmlns="urn:oasis:names:tc:opendocument:xmlns:container" version="1.0">
            <rootfiles>
              <rootfile full-path="Content/content.opf" media-type="application/oebps-package+xml"/>
            </rootfiles>
          </container>
        </xsl:result-document>
        <xsl:if test="$debug='true'">
          <xsl:message>write file Content/content.opf</xsl:message>
        </xsl:if>
        <xsl:result-document href="{concat($directory,'/Content/content.opf')}" method="xml">
          <package xmlns="http://www.idpf.org/2007/opf" unique-identifier="dcidid" version="2.0">
            <metadata xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:opf="http://www.idpf.org/2007/opf">
              <dc:title prefer="dcterms-title">
                <xsl:call-template name="generateSimpleTitle"/>
              </dc:title>
	      <meta property="dcterms:title" id="dcterms-title">
                <xsl:call-template name="generateSimpleTitle"/>
	      </meta>
	      <meta about="#dcterms-title" property="title-type">primary</meta>	      
              <xsl:variable name="A">
                <xsl:call-template name="generateAuthor"/>
              </xsl:variable>
	      <xsl:variable name="printA">
		<xsl:analyze-string select="$A" regex="([^,]+), ([^,]+), (.+)">
		  <xsl:matching-substring>
		    <xsl:value-of select="regex-group(1)"/>
		    <xsl:text>, </xsl:text>
		    <xsl:value-of select="regex-group(2)"/>
		  </xsl:matching-substring>
		  <xsl:non-matching-substring>
		    <xsl:value-of select="."/>
		  </xsl:non-matching-substring>
		</xsl:analyze-string>
	      </xsl:variable>
              <dc:creator prefer="auth">
		<xsl:value-of select="$printA"/>
              </dc:creator>
	      <meta property="dcterms:creator" id="auth">
		<xsl:value-of select="$A"/>
	      </meta>
	      <meta about="#auth" property="file-as">
		<xsl:value-of select="$printA"/>
	      </meta>
	      <meta about="#auth" property="role" id="auth-role">aut</meta>
	      <meta about="#auth-role" property="scheme" datatype="xsd:anyURI">http://id.loc.gov/vocabulary/relators</meta>
              <dc:language xsi:type="dcterms:RFC3066">
                <xsl:call-template name="generateLanguage"/>
              </dc:language>
              <xsl:call-template name="generateSubject"/>
              <dc:identifier id="dcidid" opf:scheme="URI">
                <xsl:call-template name="generateID"/>
              </dc:identifier>
              <dc:description>
                <xsl:call-template name="generateSimpleTitle"/>
                <xsl:text> / </xsl:text>
                <xsl:value-of select="$A"/>
              </dc:description>
	      <meta property="dcterms:publisher">
                <xsl:call-template name="generatePublisher"/>
	      </meta>
              <xsl:for-each select="tei:teiHeader/tei:profileDesc/tei:creation/tei:date[@notAfter]">
                <dc:date opf:event="creation">
                  <xsl:value-of select="@notAfter"/>
                </dc:date>
              </xsl:for-each>
              <xsl:for-each select="tei:teiHeader/tei:fileDesc/tei:sourceDesc//tei:date[@when][1]">
                <dc:date opf:event="original-publication">
                  <xsl:value-of select="@when"/>
                </dc:date>
              </xsl:for-each>
              <dc:date opf:event="epub-publication" xsi:type="dcterms:W3CDTF">
                <xsl:call-template name="generateDate"/>
              </dc:date>
              <dc:rights>
                <xsl:call-template name="generateLicence"/>
              </dc:rights>
              <xsl:if test="not($coverImageOutside='')">
                <meta name="cover" content="cover-image"/>
              </xsl:if>
	      <meta property="dcterms:modified">
		<xsl:call-template name="lastRevisedDate"/>
	      </meta>
            </metadata>
            <manifest>
              <xsl:if test="not($coverImageOutside='')">
                <item href="{$coverImageOutside}" id="cover-image" media-type="image/jpeg"/>
              </xsl:if>
              <xsl:if test="not($coverimage='') and not($coverimage=$coverImageOutside)">
                <item href="{$coverimage}" id="cover-image-extra" media-type="image/jpeg"/>
              </xsl:if>
              <item href="stylesheet.css" id="css" media-type="text/css"/>
              <item href="titlepage.html" id="titlepage" media-type="application/xhtml+xml"/>
              <xsl:for-each select="tei:text/tei:front/tei:titlePage">
                <xsl:variable name="N" select="position()"/>
                <item href="titlepage{$N}.html" id="titlepage{$N}" media-type="application/xhtml+xml"/>
              </xsl:for-each>
              <item href="titlepageback.html" id="titlepageback" media-type="application/xhtml+xml"/>
              <item id="print.css" href="print.css" media-type="text/css"/>
              <item id="apt" href="page-template.xpgt"
		    media-type="application/adobe-page-template+xml"/>
	      <item id="toc" properties="nav" href="toc.html" media-type="application/xhtml+xml"/>
              <item id="start" href="index.html" media-type="application/xhtml+xml"/>
              <xsl:for-each select="$TOC/html:TOC/html:ul/html:li">
                <xsl:choose>
                  <xsl:when test="not(html:a)"/>
                  <xsl:when test="starts-with(html:a/@href,'#')"/>
                  <xsl:otherwise>
                    <item href="{html:a[1]/@href}" media-type="application/xhtml+xml">
                      <xsl:attribute name="id">
                        <xsl:text>section</xsl:text>
                        <xsl:number count="html:li" level="any"/>
                      </xsl:attribute>
                    </item>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="html:ul">
                  <xsl:for-each select="html:ul//html:li[html:a and not(contains(html:a/@href,'#'))]">
                    <item href="{html:a[1]/@href}" media-type="application/xhtml+xml">
                      <xsl:attribute name="id">
                        <xsl:text>section</xsl:text>
                        <xsl:number count="html:li" level="any"/>
                      </xsl:attribute>
                    </item>
                  </xsl:for-each>
                </xsl:if>
              </xsl:for-each>
              <!-- images -->
              <xsl:for-each select="key('GRAPHICS',1)">
                <xsl:if test="not(@url=$coverImageOutside)">
                  <xsl:variable name="ID">
                    <xsl:number level="any"/>
                  </xsl:variable>
                  <xsl:variable name="mimetype">
                    <xsl:choose>
                      <xsl:when test="contains(@url,'.gif')">image/gif</xsl:when>
                      <xsl:when test="contains(@url,'.png')">image/png</xsl:when>
                      <xsl:when test="contains(@url,'.mpeg')">video/mpeg4</xsl:when>
                      <xsl:when test="contains(@url,'.mp4')">video/mpeg4</xsl:when>
                      <xsl:when test="contains(@url,'.m4v')">video/mpeg4</xsl:when>
                      <xsl:otherwise>image/jpeg</xsl:otherwise>
                    </xsl:choose>
                  </xsl:variable>
                  <item href="{@url}" id="image-{$ID}" media-type="{$mimetype}"/>
                </xsl:if>
              </xsl:for-each>
              <item id="ncx" href="toc.ncx" media-type="application/x-dtbncx+xml"/>
              <xsl:call-template name="epubManifestHook"/>
            </manifest>
            <spine toc="ncx">
              <itemref idref="titlepage" linear="yes"/>
              <xsl:for-each select="tei:text/tei:front/tei:titlePage">
                <xsl:variable name="N" select="position()"/>
                <itemref idref="titlepage{$N}" linear="yes"/>
              </xsl:for-each>
              <itemref idref="start" linear="yes"/>
              <xsl:for-each select="$TOC/html:TOC/html:ul/html:li">
                <xsl:choose>
                  <xsl:when test="not(html:a)"/>
                  <xsl:when test="starts-with(html:a/@href,'#')"/>
                  <xsl:otherwise>
                    <itemref linear="yes">
                      <xsl:attribute name="idref">
                        <xsl:text>section</xsl:text>
                        <xsl:number count="html:li" level="any"/>
                      </xsl:attribute>
                    </itemref>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="html:ul">
                  <xsl:for-each select="html:ul//html:li[html:a and not(contains(html:a/@href,'#'))]">
                    <itemref linear="yes">
                      <xsl:attribute name="idref">
                        <xsl:text>section</xsl:text>
                        <xsl:number count="html:li" level="any"/>
                      </xsl:attribute>
                    </itemref>
                  </xsl:for-each>
                </xsl:if>
              </xsl:for-each>
              <itemref idref="titlepageback" linear="no"/>
              <xsl:call-template name="epubSpineHook"/>
            </spine>
            <guide>
              <reference type="text" href="titlepage.html" title="Cover"/>
              <reference type="text" title="Start" href="index.html"/>
              <xsl:for-each select="$TOC/html:TOC/html:ul/html:li">
                <xsl:if test="html:a">
                  <reference type="text" href="{html:a[1]/@href}">
                    <xsl:attribute name="title">
                      <xsl:value-of select="normalize-space(html:a[1])"/>
                    </xsl:attribute>
                  </reference>
                </xsl:if>
                <!--
		      <xsl:if test="html:ul">
		      <xsl:for-each select="html:ul//html:li[not(contains(html:a/@href,'#'))]">
                      <reference type="text" href="{html:a/@href}">
		      <xsl:attribute name="title">
		      <xsl:value-of select="normalize-space(html:a[1])"/>
		      </xsl:attribute>
                      </reference>
		      </xsl:for-each>
		      </xsl:if>
		  -->
              </xsl:for-each>
              <reference href="titlepageback.html" type="text" title="About this book"/>
            </guide>
          </package>
        </xsl:result-document>
        <xsl:if test="$debug='true'">
          <xsl:message>write file Content/titlepage.html</xsl:message>
        </xsl:if>
        <xsl:result-document href="{concat($directory,'/Content/titlepage.html')}" method="xml">
          <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
            <head>
              <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
              <meta name="calibre:cover" content="true"/>
              <title>Title page</title>
              <style type="text/css" title="override_css">
		@page {padding: 0pt; margin:0pt}
		body { text-align: center; padding:0pt; margin: 0pt; }
	      </style>
            </head>
            <body>
              <xsl:choose>
                <xsl:when test="$coverImageInside=''">
                  <div style="font-family: serif; height:860;          font-size:36pt; border: bold red 1pt; text-align:center">
                    <xsl:call-template name="generateTitle"/>
                  </div>
                </xsl:when>
                <xsl:otherwise>
                  <div>
                    <img width="600" height="860" alt="cover picture" src="{$coverImageInside}"/>
                  </div>
                </xsl:otherwise>
              </xsl:choose>
            </body>
          </html>
        </xsl:result-document>
        <xsl:for-each select="tei:text/tei:front/tei:titlePage">
          <xsl:variable name="N" select="position()"/>
          <xsl:if test="$debug='true'">
            <xsl:message>write file Content/titlepage<xsl:value-of select="$N"/>.html</xsl:message>
          </xsl:if>
          <xsl:result-document href="{concat($directory,'/Content/titlepage',$N,'.html')}" method="xml">
            <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
              <head>
                <meta http-equiv="Content-Type" content="text/html;         charset=UTF-8"/>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <link href="stylesheet.css" rel="stylesheet" type="text/css"/>
                <title>Title page</title>
              </head>
              <body>
                <div class="titlePage">
                  <xsl:apply-templates/>
                </div>
              </body>
            </html>
          </xsl:result-document>
        </xsl:for-each>
        <xsl:if test="$debug='true'">
          <xsl:message>write file Content/titlepageback.html</xsl:message>
        </xsl:if>
        <xsl:result-document href="{concat($directory,'/Content/titlepageback.html')}" method="xml">
          <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
            <head>
              <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
              <title>About this book</title>
            </head>
            <body>
              <div style="text-align: left; font-size: smaller">
                <h2>Information about this book</h2>
                <xsl:for-each select="/*/tei:teiHeader/tei:fileDesc">
                  <xsl:apply-templates mode="metadata"/>
                </xsl:for-each>
              </div>
            </body>
          </html>
        </xsl:result-document>
        <xsl:if test="$debug='true'">
          <xsl:message>write file Content/toc.ncx</xsl:message>
        </xsl:if>
        <xsl:result-document href="{concat($directory,'/Content/toc.ncx')}" method="xml">
          <ncx xmlns="http://www.daisy.org/z3986/2005/ncx/" version="2005-1">
            <head>
              <meta name="dtb:uid">
                <xsl:attribute name="content">
                  <xsl:call-template name="generateID"/>
                </xsl:attribute>
              </meta>
              <meta name="dtb:totalPageCount" content="0"/>
              <meta name="dtb:maxPageNumber" content="0"/>
            </head>
            <docTitle>
              <text>
                <xsl:call-template name="generateSimpleTitle"/>
              </text>
            </docTitle>
            <navMap>
              <xsl:variable name="navPoints">
                <navPoint>
                  <navLabel>
                    <text>[Cover]</text>
                  </navLabel>
                  <content src="titlepage.html"/>
                </navPoint>
                <xsl:for-each select="tei:text/tei:front/tei:titlePage[1]">
                  <xsl:variable name="N" select="position()"/>
                  <navPoint>
                    <navLabel>
                      <text>[Title page]</text>
                    </navLabel>
                    <content src="titlepage{$N}.html"/>
                  </navPoint>
                </xsl:for-each>
                <navPoint>
                  <navLabel>
                    <text>[The book]</text>
                  </navLabel>
                  <content src="index.html"/>
                </navPoint>
                <xsl:for-each select="$TOC/html:TOC/html:ul/html:li">
                  <xsl:choose>
                    <xsl:when test="not(html:a)"/>
                    <xsl:when test="starts-with(html:a/@href,'#')"/>
                    <xsl:when test="contains(@class,'headless')"/>
                    <xsl:otherwise>
                      <navPoint>
                        <navLabel>
                          <text>
                            <xsl:value-of select="html:span[@class='headingNumber']"/>
                            <xsl:value-of select="normalize-space(html:a[1])"/>
                          </text>
                        </navLabel>
                        <content src="{html:a/@href}"/>
                      </navPoint>
                    </xsl:otherwise>
                  </xsl:choose>
                  <!--		<xsl:if test="html:ul">
                    <xsl:for-each select="html:ul/html:li">
		    <xsl:variable name="pos">
		    <xsl:number level="any"/>
		    </xsl:variable>
		    <navPoint id="navPoint-{$pos+1}" playOrder="{$pos+1}">
		    <navLabel>
		    <text>
		    <xsl:value-of select="normalize-space(html:a[1])"/>
		    </text>
		    </navLabel>
		    <content src="{html:a/@href}"/>
		    </navPoint>
                    </xsl:for-each>
		    </xsl:if>
		-->
                </xsl:for-each>
                <navPoint>
                  <navLabel>
                    <text>[About this book]</text>
                  </navLabel>
                  <content src="titlepageback.html"/>
                </navPoint>
              </xsl:variable>
              <xsl:for-each select="$navPoints/ncx:navPoint">
                <xsl:variable name="pos" select="position()"/>
                <navPoint id="navPoint-{$pos}" playOrder="{$pos}">
                  <xsl:copy-of select="*"/>
                </navPoint>
              </xsl:for-each>
            </navMap>
          </ncx>
        </xsl:result-document>
        <xsl:if test="$debug='true'">
          <xsl:message>write file Content/toc.html</xsl:message>
        </xsl:if>
        <xsl:result-document href="{concat($directory,'/Content/toc.html')}" method="xml">
	  <html xmlns="http://www.w3.org/1999/xhtml" profile="http://www.idpf.org/epub/30/profile/content/">
	    <head>
	      <title>
		<xsl:call-template name="generateSimpleTitle"/>
	      </title>
	      <link rel="stylesheet" href="stylesheet.css" type="text/css"/>
	      <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
	    </head>
	    <body>
	      <section class="TableOfContents">
		<header>
		  <h1>Contents</h1>
		</header>
		<nav xmlns:epub="http://www.idpf.org/2007/ops" epub:type="toc" id="toc">
		  <ol>
		    <xsl:for-each select="$TOC/html:TOC/html:ul/html:li">
		      <xsl:choose>
			<xsl:when test="not(html:a)"/>
			<xsl:when test="starts-with(html:a/@href,'#')"/>
			<xsl:when test="contains(@class,'headless')"/>
			<xsl:otherwise>
			  <li>
			    <a href="{html:a/@href}">
			      <xsl:value-of select="html:span[@class='headingNumber']"/>
			      <xsl:value-of select="normalize-space(html:a[1])"/>
			    </a>
			  </li>
			</xsl:otherwise>
		      </xsl:choose>
		    </xsl:for-each>
		  </ol>
		</nav>
		<nav xmlns:epub="http://www.idpf.org/2007/ops" epub:type="landmarks" id="guide">
		  <h2>Guide</h2>
		  <ol>
		    <li>
		      <a epub:type="toc" href="#toc">Table of Contents</a>
		    </li>
		    <li>
		      <a epub:type="titlepage" href="titlepage.html">[Title page]</a>
		    </li>
		    <li>
		      <a epub:type="bodymatter" href="index.html">[The book]</a>
		    </li>
		    <li>
		      <a href="titlepageback.html">[About this book]</a>
		    </li>
		  </ol>
		</nav>
	      </section>
	    </body>
	  </html>
        </xsl:result-document>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="epubSpineHook"/>
  <xsl:template name="epubManifestHook"/>
  <xsl:template name="processTEIHook"/>
</xsl:stylesheet>
