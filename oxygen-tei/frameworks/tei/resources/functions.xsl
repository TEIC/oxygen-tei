<!-- 
  Copyright 2001-2012 Syncro Soft SRL. All rights reserved.
 -->
<xsl:stylesheet version="3.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:f="http://www.oxygenxml.com/xsl/functions"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="f xd">
    
    <xd:doc>
        <xd:desc>Corrects id of a topic such as it will NCName.
            Moreover it eliminates "%20".</xd:desc>
        <xd:param name="text">The text of the ID to correct.</xd:param>
        <xd:return>The corrected text which can be used as id</xd:return>
    </xd:doc>
    <xsl:function name="f:correctId" as="xs:string">
        <xsl:param name="text" as="xs:string"/>
        <xsl:variable name="tempId" select="replace(xs:string($text), '%20', '_')"/>
        <xsl:variable name="tempId2" select="translate($tempId, 'ȘșȚț', 'sstt')"/>
        <xsl:variable name="tempId3" select="replace($tempId2, '[^\c_-]|[+:]', '_')"/>
        <xsl:variable name="tempId4" select="replace($tempId3,'[_]+', '_')"/>
        <xsl:variable name="tempId5" select="replace($tempId4,'^[.-/_]+|_$', '')"/>
        <xsl:choose>
            <xsl:when test="matches($tempId5, '^[0-9]+')">
                <!--EXM-51379: Keep the numeric chars in the ID  -->
                <xsl:value-of select="concat('_',$tempId5)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$tempId5"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>Extract id from anchor element.</xd:desc>
        <xd:param name="anchor">The anchor element.</xd:param>
        <xd:return>The extracted id.</xd:return>
    </xd:doc>
    <xsl:function name="f:extractIdFromAnchor" as="xs:string">
        <xsl:param name="anchor" as="node()"/>
        <xsl:choose>
            <xsl:when test="$anchor/@id">
                <xsl:value-of select="f:correctId($anchor/@id)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="f:correctId(normalize-space($anchor/@name))"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>Function for making an ID using the given file path.</xd:desc>
        <xd:param name="filePath">The file path to process.</xd:param>
        <xd:return>The id.</xd:return>
    </xd:doc>
    <xsl:function name="f:makeID">
        <xsl:param name="filePath"/>
        <xsl:value-of select="f:getFilename(translate($filePath,' \()','_/_'))"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>Function for getting the file name from the given path.</xd:desc>
        <xd:param name="path">The file path to process.</xd:param>
        <xd:return>The file name.</xd:return>
    </xd:doc>
    <xsl:function name="f:getFilename">
        <xsl:param name="path"/>
        <xsl:choose>
            <xsl:when test="contains($path,'/')">
                <xsl:value-of select="f:getFilename(substring-after($path,'/'))"/>
            </xsl:when>
            <xsl:when test="contains($path,'\')">
                <xsl:value-of select="f:getFilename(substring-after($path,'\'))"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$path"/>
            </xsl:otherwise>
        </xsl:choose>    
    </xsl:function>
    
    <xd:doc>
        <xd:desc>Function for extracting the format from the given file name parameter.</xd:desc>
        <xd:param name="fileName">The file name.</xd:param>
        <xd:return>The format.</xd:return>
    </xd:doc>
    <xsl:function name="f:extractFormat">
        <xsl:param name="fileName"/>
        <xsl:variable name="withoutQuery">
            <xsl:choose>
                <xsl:when test="contains($fileName, '?') ">
                    <xsl:value-of select="substring-before($fileName, '?')" />
                </xsl:when>
                <xsl:otherwise> 
                    <xsl:value-of select="$fileName"/>            
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="withoutAnchor">
            <xsl:choose>
                <xsl:when test="contains($withoutQuery, '#') ">
                    <xsl:value-of select="substring-before($withoutQuery, '#')" />
                </xsl:when>
                <xsl:otherwise> 
                    <xsl:value-of select="$withoutQuery" />            
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:call-template name="substring-after-last">
            <xsl:with-param name="whereToSearch" select="$withoutAnchor" />
            <xsl:with-param name="whatYouSearch" select="'.'" />
        </xsl:call-template>
    </xsl:function>
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc> Search and returns the value after the last occurrence of a token
        </xd:desc>
        <xd:param name="whereToSearch"/>
        <xd:param name="whatYouSearch"/>
    </xd:doc>
    <xsl:template name="substring-after-last">
        <xsl:param name="whereToSearch" select="''" />
        <xsl:param name="whatYouSearch" select="''" />
        
        <xsl:if test="$whereToSearch != '' and $whatYouSearch != ''">
            <xsl:variable name="head" select="substring-before($whereToSearch, $whatYouSearch)" />
            <xsl:variable name="tail" select="substring-after($whereToSearch, $whatYouSearch)" />
            <xsl:value-of select="$tail" />
            <xsl:if test="contains($tail, $whatYouSearch)">
                <xsl:value-of select="$whatYouSearch" />
                <xsl:call-template name="substring-after-last">
                    <xsl:with-param name="whereToSearch" select="$tail" />
                    <xsl:with-param name="whatYouSearch" select="$whatYouSearch" />
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
