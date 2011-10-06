<?xml version="1.0" encoding="UTF-8"?>
<!-- 
  Copyright 2001-2011 Syncro Soft SRL. All rights reserved.
 -->
 <xsl:stylesheet version="2.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xhtml="http://www.w3.org/1999/xhtml" 
                exclude-result-prefixes="xsl xhtml">

  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
  
  <xsl:param name="folderOfPasteTargetXml"/>
  
  <!-- Main block-level conversions -->
  <xsl:template match="xhtml:html">
    <xsl:apply-templates select="xhtml:body" mode="checkHeadings"/>
  </xsl:template>
  
  <!-- 
      MsoTitle - attr value for MS Word titles. 
      In XHTML that comes from MS Word the last node is a comment with the content 'EndFragment'.
  -->
  <xsl:template match="xhtml:div[xhtml:p[@class = 'MsoTitle']]">
    <xsl:variable name="sentinel" select="
      ( following-sibling::xhtml:div[xhtml:p[@class = 'MsoTitle']]
      | following-sibling::comment()[. = 'EndFragment'])[1]"/>
    <xsl:variable name="firstChild" select="
      (following-sibling::xhtml:h1,
       following-sibling::xhtml:h2,
       following-sibling::xhtml:h3,
       following-sibling::xhtml:h4,
       following-sibling::xhtml:h5,
       following-sibling::xhtml:h6)
       [. &lt;&lt; $sentinel][1]"/>
    <xsl:variable name="title" select="xhtml:p[@class = 'MsoTitle']//text()"/>
    <xsl:call-template name="fillSectionBody">
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="firstChild" select="$firstChild"/>
      <xsl:with-param name="sentinel" select="$sentinel"/>
      <xsl:with-param name="bodySet" select="following-sibling::text() | following-sibling::xhtml:*"/>
      </xsl:call-template>
  </xsl:template>

  <xsl:template name="fillSectionBody">
    <xsl:param name="title"/>
    <xsl:param name="firstChild"/>
    <xsl:param name="sentinel"/>
    <xsl:param name="bodySet"/>
    <xsl:variable name="body">
      <xsl:choose>
        <xsl:when test="$firstChild">
          <xsl:apply-templates select="$bodySet[. &lt;&lt; $firstChild]" mode="preprocess"/>
        </xsl:when>
        <xsl:when test="$sentinel">
          <xsl:apply-templates select="$bodySet[. &lt;&lt; $sentinel]" mode="preprocess"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="some $x in $bodySet satisfies 
                local-name($x) = 'h1' and namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:variable name="limit" select="(for $x in $bodySet return $x 
                [local-name() = 'h1'][namespace-uri() = 'http://www.w3.org/1999/xhtml'])[1]">
              </xsl:variable>
              <xsl:apply-templates select="$bodySet[. &lt;&lt; $limit]" mode="preprocess"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h2' and namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:variable name="limit" select="(for $x in $bodySet return $x 
                [local-name() = 'h2'][namespace-uri() = 'http://www.w3.org/1999/xhtml'])[1]"/>
              <xsl:apply-templates select="$bodySet[. &lt;&lt; $limit]" mode="preprocess"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h3' and namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:variable name="limit" select="(for $x in $bodySet return $x 
                [local-name() = 'h3'][namespace-uri() = 'http://www.w3.org/1999/xhtml'])[1]"/>
              <xsl:apply-templates select="$bodySet[. &lt;&lt; $limit]" mode="preprocess"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h4' and namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:variable name="limit" select="(for $x in $bodySet return $x 
                [local-name() = 'h4'][namespace-uri() = 'http://www.w3.org/1999/xhtml'])[1]"/>
              <xsl:apply-templates select="$bodySet[. &lt;&lt; $limit]" mode="preprocess"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h5' and namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:variable name="limit" select="(for $x in $bodySet return $x 
                [local-name() = 'h5'][namespace-uri() = 'http://www.w3.org/1999/xhtml'])[1]"/>
              <xsl:apply-templates select="$bodySet[. &lt;&lt; $limit]" mode="preprocess"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h6' and namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:variable name="limit" select="(for $x in $bodySet return $x 
                [local-name() = 'h6'][namespace-uri() = 'http://www.w3.org/1999/xhtml'])[1]"/>
              <xsl:apply-templates select="$bodySet[. &lt;&lt; $limit]" mode="preprocess"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="$bodySet"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="afterBody">
      <xsl:choose>
        <xsl:when test="$sentinel">
          <xsl:choose>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h1' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml' and
              $x &lt;&lt; $sentinel">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h1']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']
                [$x &lt;&lt; $sentinel]"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h2' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml' and
              $x &lt;&lt; $sentinel">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h2']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']
                [$x &lt;&lt; $sentinel]"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h3' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml' and
              $x &lt;&lt; $sentinel">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h3']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']
                [$x &lt;&lt; $sentinel]"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h4' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml' and
              $x &lt;&lt; $sentinel">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h4']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']
                [$x &lt;&lt; $sentinel]"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h5' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml' and
              $x &lt;&lt; $sentinel">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h5']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']
                [$x &lt;&lt; $sentinel]"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h6' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml' and
              $x &lt;&lt; $sentinel">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h6']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']
                [$x &lt;&lt; $sentinel]"/>
            </xsl:when>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h1' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h1']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h2' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h2']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h3' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h3']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h4' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h4']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h5' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h5']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']"/>
            </xsl:when>
            <xsl:when test="some $x in $bodySet satisfies 
              local-name($x) = 'h6' and 
              namespace-uri($x) = 'http://www.w3.org/1999/xhtml'">
              <xsl:apply-templates select="for $x in $bodySet return $x 
                [local-name($x) = 'h6']
                [namespace-uri($x) = 'http://www.w3.org/1999/xhtml']"/>
            </xsl:when>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="ancestor::xhtml:td | ancestor::xhtml:th">
        <xsl:copy-of select="$title"/>
        <xsl:copy-of select="$body"/>
        <xsl:copy-of select="$afterBody"/>
      </xsl:when>
      <xsl:otherwise>
        <div xmlns="http://www.tei-c.org/ns/1.0">
          <head>
            <xsl:copy-of select="$title"/>
          </head>
          <xsl:copy-of select="$body"/>
          <xsl:copy-of select="$afterBody"/>
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="xhtml:h1">
    <xsl:variable name="sentinel" select="
      ( following-sibling::xhtml:div[xhtml:p[@class = 'MsoTitle']]
      | following-sibling::xhtml:h1
      | following-sibling::comment()[. = 'EndFragment'])[1]"/>
    <xsl:variable name="firstChild" select="
      (following-sibling::xhtml:h2, 
       following-sibling::xhtml:h3,
       following-sibling::xhtml:h4,
       following-sibling::xhtml:h5,
       following-sibling::xhtml:h6)
      [. &lt;&lt; $sentinel][1]"/>
    <xsl:variable name="title">
      <xsl:apply-templates/>
    </xsl:variable>
    <xsl:call-template name="fillSectionBody">
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="firstChild" select="$firstChild"/>
      <xsl:with-param name="sentinel" select="$sentinel"/>
      <xsl:with-param name="bodySet" select="following-sibling::text() | following-sibling::xhtml:*"/>
      </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="xhtml:h2">
    <xsl:variable name="sentinel" select="
      ( following-sibling::xhtml:div[xhtml:p[@class = 'MsoTitle']]
      | following-sibling::xhtml:h1
      | following-sibling::xhtml:h2
      | following-sibling::comment()[. = 'EndFragment'])[1]"/>
    <xsl:variable name="firstChild" select="
      (following-sibling::xhtml:h3,
       following-sibling::xhtml:h4,
       following-sibling::xhtml:h5,
       following-sibling::xhtml:h6)
       [. &lt;&lt; $sentinel][1]"/>
    <xsl:variable name="title">
      <xsl:apply-templates/>
    </xsl:variable>
    <xsl:call-template name="fillSectionBody">
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="firstChild" select="$firstChild"/>
      <xsl:with-param name="sentinel" select="$sentinel"/>
      <xsl:with-param name="bodySet" select="following-sibling::text() | following-sibling::xhtml:*"/>
      </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="xhtml:h3">
    <xsl:variable name="sentinel" select="
      ( following-sibling::xhtml:div[xhtml:p[@class = 'MsoTitle']]
      | following-sibling::xhtml:h1
      | following-sibling::xhtml:h2
      | following-sibling::xhtml:h3
      | following-sibling::comment()[. = 'EndFragment'])[1]"/>
    <xsl:variable name="firstChild" select="
      (following-sibling::xhtml:h4,
       following-sibling::xhtml:h5,
       following-sibling::xhtml:h6)
       [. &lt;&lt; $sentinel][1]"/>
    <xsl:variable name="title">
      <xsl:apply-templates/>
    </xsl:variable>
    <xsl:call-template name="fillSectionBody">
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="firstChild" select="$firstChild"/>
      <xsl:with-param name="sentinel" select="$sentinel"/>
      <xsl:with-param name="bodySet" select="following-sibling::text() | following-sibling::xhtml:*"/>
      </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="xhtml:h4">
    <xsl:variable name="sentinel" select="
      ( following-sibling::xhtml:div[xhtml:p[@class = 'MsoTitle']]
      | following-sibling::xhtml:h1
      | following-sibling::xhtml:h2
      | following-sibling::xhtml:h3
      | following-sibling::xhtml:h4
      | following-sibling::comment()[. = 'EndFragment'])[1]"/>
    <xsl:variable name="firstChild" select="
      (following-sibling::xhtml:h5,
       following-sibling::xhtml:h6)
       [. &lt;&lt; $sentinel][1]"/>
    <xsl:variable name="title">
      <xsl:apply-templates/>
    </xsl:variable>
    <xsl:call-template name="fillSectionBody">
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="firstChild" select="$firstChild"/>
      <xsl:with-param name="sentinel" select="$sentinel"/>
      <xsl:with-param name="bodySet" select="following-sibling::text() | following-sibling::xhtml:*"/>
      </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="xhtml:h5">
    <xsl:variable name="sentinel" select="
      ( following-sibling::xhtml:div[xhtml:p[@class = 'MsoTitle']]
      | following-sibling::xhtml:h1
      | following-sibling::xhtml:h2
      | following-sibling::xhtml:h3
      | following-sibling::xhtml:h4
      | following-sibling::xhtml:h5
      | following-sibling::comment()[. = 'EndFragment'])[1]"/>
    <xsl:variable name="firstChild" select="following-sibling::xhtml:h6[. &lt;&lt; $sentinel][1]"/>
    <xsl:variable name="title">
      <xsl:apply-templates/>
    </xsl:variable>
    <xsl:call-template name="fillSectionBody">
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="firstChild" select="$firstChild"/>
      <xsl:with-param name="sentinel" select="$sentinel"/>
      <xsl:with-param name="bodySet" select="following-sibling::text() | following-sibling::xhtml:*"/>
      </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="xhtml:h6">
    <xsl:variable name="sentinel" select="
      ( following-sibling::xhtml:div[xhtml:p[@class = 'MsoTitle']]
      | following-sibling::xhtml:h1
      | following-sibling::xhtml:h2
      | following-sibling::xhtml:h3
      | following-sibling::xhtml:h4
      | following-sibling::xhtml:h5
      | following-sibling::xhtml:h6
      | following-sibling::comment()[. = 'EndFragment'])[1]"/>
    <xsl:variable name="title">
      <xsl:apply-templates/>
    </xsl:variable>
    <xsl:call-template name="fillSectionBody">
      <xsl:with-param name="title" select="$title"/>
      <xsl:with-param name="sentinel" select="$sentinel"/>
      <xsl:with-param name="bodySet" select="following-sibling::text() | following-sibling::xhtml:*"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="xhtml:h1[ancestor::xhtml:dl] 
    | xhtml:h2[ancestor::xhtml:dl] 
    | xhtml:h3[ancestor::xhtml:dl] 
    | xhtml:h4[ancestor::xhtml:dl] 
    | xhtml:h5[ancestor::xhtml:dl]
    | xhtml:h6[ancestor::xhtml:dl]">
    <hi rend="bold" xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:apply-templates/>
    </hi>
  </xsl:template>
  
  <xsl:template match="xhtml:p">
   <xsl:choose>
     <xsl:when test="not(parent::xhtml:th | parent::xhtml:td) and not(normalize-space(.) = '')">
       <p xmlns="http://www.tei-c.org/ns/1.0">
         <xsl:apply-templates select="@*"/>
         <xsl:apply-templates select="." mode="checkHeadings"/>
       </p>
     </xsl:when>
     <xsl:otherwise>
          <xsl:apply-templates select="@*"/>
       <xsl:apply-templates select="." mode="checkHeadings"/>
      </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <xsl:template match="xhtml:div[xhtml:br]">
    <p xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:apply-templates select="." mode="checkHeadings"/>
    </p>
  </xsl:template>
  
  <xsl:template match="xhtml:br">
    <lb xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:apply-templates select="." mode="checkHeadings"/>
    </lb>
  </xsl:template>

  <xsl:template match="xhtml:pre">
    <quote xmlns="http://www.tei-c.org/ns/1.0">
        <xsl:apply-templates select="@*"/>
       <xsl:apply-templates select="." mode="checkHeadings"/>
   </quote>
  </xsl:template>
  
  <!-- Hyperlinks -->
  <xsl:template match="xhtml:a[contains(@href,'http://') or contains(@href,'ftp://')]" priority="1.5">
    <ptr xmlns="http://www.tei-c.org/ns/1.0">
       <xsl:attribute name="target">
          <xsl:value-of select="normalize-space(@href)"/>
       </xsl:attribute>
    </ptr>
    <xsl:apply-templates select="." mode="checkHeadings"/>
  </xsl:template>
  
  <xsl:template match="xhtml:a[contains(@href,'#')]" priority="0.6">
    <ptr xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:attribute name="target">
       <xsl:call-template name="make_id">
        <xsl:with-param name="string" select="normalize-space(@href)"/>
       </xsl:call-template>
      </xsl:attribute>
     </ptr>
    <xsl:apply-templates select="." mode="checkHeadings"/>
  </xsl:template>
  
  <xsl:template match="xhtml:a[@name != '']" priority="0.6">
    <hi xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:attribute name="id">
      <xsl:call-template name="make_id">
         <xsl:with-param name="string" select="normalize-space(@name)"/>
      </xsl:call-template>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="." mode="checkHeadings"/>
    </hi>
  </xsl:template>
  
  <xsl:template match="xhtml:a[@href != '']">
    <ptr xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:attribute name="target">
       <xsl:call-template name="make_id">
         <xsl:with-param name="string" select="normalize-space(@href)"/>
       </xsl:call-template>
      </xsl:attribute>
     </ptr>
    <xsl:apply-templates select="node()"/>
  </xsl:template>
  
  <xsl:template name="make_id">
   <xsl:param name="string" select="''"/>
   <xsl:variable name="fixedname">
    <xsl:call-template name="getFilename">
     <xsl:with-param name="path" select="translate($string,' \()','_/_')"/>
    </xsl:call-template>
   </xsl:variable>
   <xsl:choose>
     <xsl:when test="contains($fixedname,'.html')">
       <xsl:value-of select="substring-before($fixedname,'.html')"/>
       <xsl:text>.xml</xsl:text>
       <xsl:value-of select="substring-after($fixedname,'.html')"/>
     </xsl:when>
     <xsl:when test="contains($fixedname,'.htm')">
     <xsl:value-of select="substring-before($fixedname,'.htm')"/>
     <xsl:text>.xml</xsl:text>
     <xsl:value-of select="substring-after($fixedname,'.htm')"/>
    </xsl:when>
    <xsl:otherwise>
     <xsl:value-of select="$fixedname"/>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>
  
  <xsl:template name="string.subst">
   <xsl:param name="string" select="''"/>
   <xsl:param name="substitute" select="''"/>
   <xsl:param name="with" select="''"/>
   <xsl:choose>
    <xsl:when test="contains($string,$substitute)">
     <xsl:variable name="pre" select="substring-before($string,$substitute)"/>
     <xsl:variable name="post" select="substring-after($string,$substitute)"/>
     <xsl:call-template name="string.subst">
      <xsl:with-param name="string" select="concat($pre,$with,$post)"/>
      <xsl:with-param name="substitute" select="$substitute"/>
      <xsl:with-param name="with" select="$with"/>
     </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
     <xsl:value-of select="$string"/>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>
  
  <!-- Images -->
  <xsl:template match="xhtml:img">
    <xsl:variable name="pastedImageURL" 
      xmlns:URL="java:java.net.URL"
      xmlns:URLUtil="java:ro.sync.util.URLUtil"
      xmlns:UUID="java:java.util.UUID">
      <xsl:choose>
        <xsl:when test="namespace-uri-for-prefix('o', .) = 'urn:schemas-microsoft-com:office:office'">
          <!-- Copy from MS Office. Copy the image from user temp folder to folder of XML document
            that is the paste target. -->
          <xsl:variable name="imageFilename">
            <xsl:variable name="fullPath" select="URL:getPath(URL:new(translate(@src, '\', '/')))"/>
            <xsl:variable name="srcFile">
              <xsl:choose>
                <xsl:when test="contains($fullPath, ':')">
                  <xsl:value-of select="substring($fullPath, 2)"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$fullPath"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <xsl:call-template name="getFilename">
              <xsl:with-param name="path" select="string($srcFile)"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:variable name="stringImageFilename" select="string($imageFilename)"/>
          <xsl:variable name="uid" select="UUID:hashCode(UUID:randomUUID())"/>
          <xsl:variable name="uniqueTargetFilename" select="concat(substring-before($stringImageFilename, '.'), '_', $uid, '.', substring-after($stringImageFilename, '.'))"/>
          <xsl:variable name="sourceURL" select="URL:new(translate(@src, '\', '/'))"/>
          <xsl:variable name="correctedSourceFile">
            <xsl:choose>
              <xsl:when test="contains(URL:getPath($sourceURL), ':')">
                <xsl:value-of select="substring-after(URL:getPath($sourceURL), '/')"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="URL:getPath($sourceURL)"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <xsl:variable name="sourceFile" select="URLUtil:uncorrect($correctedSourceFile)"/>
          <xsl:variable name="targetURL" select="URL:new(concat($folderOfPasteTargetXml, '/', $uniqueTargetFilename))"/>
          <xsl:value-of select="URLUtil:copyURL($sourceURL, $targetURL)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@src"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <graphic xmlns="http://www.tei-c.org/ns/1.0">
        <xsl:if test="@src != ''">
          <xsl:attribute name="url">
            <xsl:value-of select="$pastedImageURL"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="@height != ''">
          <xsl:attribute name="height">
            <xsl:value-of select="@height"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="@width != ''">
          <xsl:attribute name="width">
            <xsl:value-of select="@width"/>
          </xsl:attribute>
        </xsl:if>
      </graphic>
  </xsl:template>
  
  <xsl:template name="getFilename">
   <xsl:param name="path"/>
   <xsl:choose>
    <xsl:when test="contains($path,'/')">
     <xsl:call-template name="getFilename">
      <xsl:with-param name="path" select="substring-after($path,'/')"/>
     </xsl:call-template>
    </xsl:when>
     <xsl:when test="contains($path,'\')">
       <xsl:call-template name="getFilename">
         <xsl:with-param name="path" select="substring-after($path,'\')"/>
       </xsl:call-template>
     </xsl:when>
     <xsl:otherwise>
     <xsl:value-of select="$path"/>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>
  
  <!-- List elements -->
  <xsl:template match="xhtml:ul">
    <list type="simple" xmlns="http://www.tei-c.org/ns/1.0">
          <xsl:apply-templates select="@*"/>
     <xsl:apply-templates select="." mode="checkHeadings"/>
   </list>
  </xsl:template>
  
  <xsl:template match="xhtml:ol">
    <list type="ordered" xmlns="http://www.tei-c.org/ns/1.0">
          <xsl:apply-templates select="@*"/>
     <xsl:apply-templates select="." mode="checkHeadings"/>
   </list>
  </xsl:template>
  
  <!-- 
         MS Office START 
  -->
  
  <!-- Unordered lists from MS Word can be translated as TEI unordered lists only when they can
    be identified, that is the @style attrib contains ''. This is true for single level lists
    but never for multi-level list. If they cannot be identified as unordered lists will be
    translated as ordered lists. -->
  
  <!-- Unordered lists. -->
  <!-- <p> and <span> can be separated by <i> and <b>. -->
  <xsl:template match="xhtml:p[contains(@class, 'MsoListParagraphCxSpFirst')]
          [descendant::xhtml:span[contains(@style, 'mso-fareast-font-family')]]" priority="1">    
    <list type="simple" xmlns="http://www.tei-c.org/ns/1.0">
      <item>
        <p>
          <xsl:value-of select=".//text()"/>
        </p>
      </item>
      <xsl:variable name="sentinel" select="following-sibling::xhtml:p[contains(@class, 'MsoListParagraphCxSpLast')]
          [descendant::xhtml:span[contains(@style, 'mso-fareast-font-family')]][1]/following-sibling::*[1]"/> 
      <xsl:for-each select="following-sibling::xhtml:p[contains(@class, 'MsoListParagraphCxSp')][. &lt;&lt; $sentinel]">
        <item>
          <p>
            <xsl:value-of select=".//text()"/>
          </p>
        </item>
      </xsl:for-each>
    </list>
  </xsl:template>
  
  <!-- Ordered lists from MS Word -->
  <xsl:template match="xhtml:p[contains(@class, 'MsoListParagraphCxSpFirst')]">    
    <list type="ordered" xmlns="http://www.tei-c.org/ns/1.0">
      <item>
        <p>
          <xsl:value-of select=".//text()"/>
        </p>
      </item>
      <xsl:variable name="sentinel" select="following-sibling::xhtml:p[contains(@class, 'MsoListParagraphCxSpLast')][1]/following-sibling::*[1]"/>
      <xsl:for-each select="following-sibling::xhtml:p[contains(@class, 'MsoListParagraphCxSp')][. &lt;&lt; $sentinel]">
        <item>
          <p>
            <xsl:value-of select=".//text()"/>
          </p>
        </item>
      </xsl:for-each>
    </list>
  </xsl:template>
  
  <xsl:template match="xhtml:p[contains(@class, 'MsoListParagraphCxSpMiddle') or contains(@class, 'MsoListParagraphCxSpLast')]"/>
  
  <!-- 
         MS Office END 
  -->  
     
  <xsl:template match="xhtml:blockquote">
    <quote xmlns="http://www.tei-c.org/ns/1.0">
        <xsl:apply-templates select="." mode="checkHeadings"/>
    </quote>
  </xsl:template>
  
  <xsl:template match="xhtml:q">
    <hi rend="quoted" xmlns="http://www.tei-c.org/ns/1.0">
        <xsl:apply-templates select="." mode="checkHeadings"/>
    </hi>
  </xsl:template>
  
	<!-- This template makes a TEI gloss list from an HTML definition list. -->
	<xsl:template match="xhtml:dl">
		<xsl:variable name="dataBeforeTitle" select="xhtml:dd[empty(preceding-sibling::xhtml:dt)]"/>
		<xsl:if test="not(empty($dataBeforeTitle))">
			<list type="gloss" xmlns="http://www.tei-c.org/ns/1.0">
				<xsl:apply-templates select="@*"/>
				<xsl:for-each select="$dataBeforeTitle">
					<item xmlns="http://www.tei-c.org/ns/1.0">
						<xsl:apply-templates select="."/>
					</item>
				</xsl:for-each>
			</list>
		</xsl:if>
		<xsl:for-each select="xhtml:dt">
			<list type="gloss" xmlns="http://www.tei-c.org/ns/1.0">
				<xsl:apply-templates select="parent::xhtml:dl/@*"/>
				<label xmlns="http://www.tei-c.org/ns/1.0">
					<xsl:apply-templates select="@* | node()" mode="preprocess"/>
				</label>
				<item xmlns="http://www.tei-c.org/ns/1.0">
					<xsl:apply-templates select="following-sibling::xhtml:dd[current() is preceding-sibling::xhtml:dt[1]]"/>
				</item>
			</list>
		</xsl:for-each>
	</xsl:template>
	
  <xsl:template match="xhtml:dd">
   <xsl:choose>
    <xsl:when test="xhtml:p">
          <xsl:apply-templates select="node()" mode="preprocess"/>
    </xsl:when>
    <xsl:otherwise>
      <p xmlns="http://www.tei-c.org/ns/1.0">
          <xsl:apply-templates select="node()" mode="preprocess"/>
     </p>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>
  
  <xsl:template match="xhtml:li">
    <item xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:choose>
        <xsl:when test="count(xhtml:p) = 0">
          <p>
            <xsl:apply-templates select="." mode="checkHeadings"/>
          </p>
       </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="." mode="checkHeadings"/>
        </xsl:otherwise>
    </xsl:choose>
   </item>
  </xsl:template>
  
  <xsl:template match="*" mode="checkHeadings">
    <xsl:choose>
      <xsl:when test="xhtml:div[xhtml:p[@class = 'MsoTitle']]">
        <xsl:apply-templates select="(xhtml:* | text())[. &lt;&lt; current()/xhtml:div[xhtml:p[@class = 'MsoTitle']][1]]" mode="preprocess"/>
        <xsl:apply-templates select="xhtml:div[xhtml:p[@class = 'MsoTitle']]"/>
      </xsl:when>
      <xsl:when test="xhtml:h1">
        <xsl:apply-templates select="(xhtml:* | text())[. &lt;&lt; current()/xhtml:h1[1]]" mode="preprocess"/>
        <xsl:apply-templates select="xhtml:h1"/>
      </xsl:when>
      <xsl:when test="xhtml:h2">
        <xsl:apply-templates select="(xhtml:* | text())[. &lt;&lt; current()/xhtml:h2[1]]" mode="preprocess"/>
        <xsl:apply-templates select="xhtml:h2"/>
      </xsl:when>
      <xsl:when test="xhtml:h3">
        <xsl:apply-templates select="(xhtml:* | text())[. &lt;&lt; current()/xhtml:h3[1]]" mode="preprocess"/>
        <xsl:apply-templates select="xhtml:h3"/>
      </xsl:when>
      <xsl:when test="xhtml:h4">
        <xsl:apply-templates select="(xhtml:* | text())[. &lt;&lt; current()/xhtml:h4[1]]" mode="preprocess"/>
        <xsl:apply-templates select="xhtml:h4"/>
      </xsl:when>
      <xsl:when test="xhtml:h5">
        <xsl:apply-templates select="(xhtml:* | text())[. &lt;&lt; current()/xhtml:h5[1]]" mode="preprocess"/>
        <xsl:apply-templates select="xhtml:h5"/>
      </xsl:when>
      <xsl:when test="xhtml:h6">
        <xsl:apply-templates select="(xhtml:* | text())[. &lt;&lt; current()/xhtml:h6[1]]" mode="preprocess"/>
        <xsl:apply-templates select="xhtml:h6"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="node()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="@id" mode="#all"> 
    <xsl:attribute name="id">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="@*" mode="#all">
   <!--<xsl:message>No template for attribute <xsl:value-of select="name()"/></xsl:message>-->
  </xsl:template>
  
  <!-- Inline formatting -->
  <xsl:template match="xhtml:b | xhtml:strong">
    <hi rend="bold" xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:apply-templates select="@*"/>
    <xsl:apply-templates select="." mode="checkHeadings"/>
   </hi>
  </xsl:template>
  
  <xsl:template match="xhtml:i | xhtml:em">
    <hi rend="italic" xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:apply-templates select="@*"/>
    <xsl:apply-templates select="." mode="checkHeadings"/>
   </hi>
  </xsl:template>
  
  <xsl:template match="xhtml:u">
    <hi rend="underline" xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:apply-templates select="@*"/>
    <xsl:apply-templates select="." mode="checkHeadings"/>
    </hi>
  </xsl:template>

  <!-- Ignored elements -->
  <xsl:template match="xhtml:hr"/>
  <xsl:template match="xhtml:meta"/>
  <xsl:template match="xhtml:style"/>
  <xsl:template match="xhtml:script"/>
  <xsl:template match="xhtml:p[normalize-space(.) = '' and count(*) = 0]"/>
  <xsl:template match="text()" mode="#all">
   <xsl:choose>
    <xsl:when test="normalize-space(.) = ''"><xsl:text> </xsl:text></xsl:when>
    <xsl:otherwise><xsl:copy/></xsl:otherwise>
   </xsl:choose>
  </xsl:template>
  
  <xsl:template match="xhtml:a[@href != '' 
                        and not(boolean(ancestor::xhtml:p|ancestor::xhtml:li))]" 
                priority="1">
    <p xmlns="http://www.tei-c.org/ns/1.0">
       <ptr>
        <xsl:attribute name="target">
         <xsl:call-template name="make_id">
           <xsl:with-param name="string" select="normalize-space(@href)"/>
         </xsl:call-template>
        </xsl:attribute>
       </ptr>
      <xsl:apply-templates select="." mode="checkHeadings"/>
    </p>
  </xsl:template>
  
  <xsl:template match="xhtml:a[contains(@href,'#') 
                      and not(boolean(ancestor::xhtml:p|ancestor::xhtml:li))]" 
                priority="1.1">
    <p xmlns="http://www.tei-c.org/ns/1.0">
       <ptr>
        <xsl:attribute name="target">
         <xsl:call-template name="make_id">
           <xsl:with-param name="string" select="normalize-space(@href)"/>
         </xsl:call-template>
        </xsl:attribute>
       </ptr>
      <xsl:apply-templates select="." mode="checkHeadings"/>
    </p>
  </xsl:template>
  
  <!-- Table conversion -->
  
  <!-- In TEI P4 the XHTML table elements are transformed to the elements of TEI table. -->
  <xsl:template match="xhtml:table">
    <table xmlns="http://www.tei-c.org/ns/1.0">
    	<xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="xhtml:caption, xhtml:thead, xhtml:tr | xhtml:tbody/xhtml:tr | text() | xhtml:b | xhtml:strong | xhtml:i | xhtml:em | xhtml:u, xhtml:tfoot/xhtml:tr"/>
    </table>
  </xsl:template>
  
  <xsl:template match="xhtml:caption">
    <head xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="." mode="checkHeadings"/>
    </head>
  </xsl:template>
  
  <xsl:template match="xhtml:thead/xhtml:tr">
    <row role="label" xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:apply-templates select="@*"/>
          <xsl:apply-templates select="." mode="checkHeadings"/>
    </row>
  </xsl:template>
  
  <xsl:template match="xhtml:tr">
    <row xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:apply-templates select="." mode="checkHeadings"/>
    </row>
  </xsl:template>

  <xsl:template match="xhtml:td | xhtml:th">
    <cell xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:if test="number(@rowspan) > 1">
        <xsl:attribute name="rows">
          <xsl:value-of select="@rowspan"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="number(@colspan) > 1">
        <xsl:attribute name="cols">
          <xsl:value-of select="@colspan"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="." mode="checkHeadings"/>
    </cell>
  </xsl:template>

  <xsl:template match="*" mode="preprocess">
    <xsl:apply-templates select="."/>
  </xsl:template>
  
  <xsl:template match="*">
    <xsl:apply-templates select="." mode="checkHeadings"/>
  </xsl:template>

  <xsl:template match="xhtml:h1 | xhtml:h2 | xhtml:h3 | xhtml:h4 | xhtml:h5 |xhtml:h6" mode="preprocess">
    <p xmlns="http://www.tei-c.org/ns/1.0">
      <hi rend="bold" xmlns="http://www.tei-c.org/ns/1.0">
        <xsl:value-of select="."/>
      </hi>
    </p>
  </xsl:template>
</xsl:stylesheet>