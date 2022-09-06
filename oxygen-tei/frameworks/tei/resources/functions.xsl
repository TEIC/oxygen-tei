<!-- 
  Copyright 2001-2012 Syncro Soft SRL. All rights reserved.
 -->
<xsl:stylesheet version="3.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:f="http://www.oxygenxml.com/xsl/functions"
    exclude-result-prefixes="f">
    
    <!--
        Corrects id of a topic such as it will NCName.
            Moreover it eliminates "%20".</xd:desc>
        Para "text": Text to be corrected</xd:param>
        Return: The corrected text which can be used as id</xd:return>
    -->
    <xsl:function name="f:correctId" as="xs:string">
        <xsl:param name="text" as="xs:string"/>
        <xsl:variable name="tempId" select="replace(xs:string($text), '%20', '_')"/>
        <xsl:variable name="tempId2" select="replace($tempId, '[^\c_-]|[+:]', '_')"/>
        <xsl:variable name="tempId3" select="replace($tempId2,'[_]+', '_')"/>
        <xsl:variable name="tempId4" select="replace($tempId3,'_$', '')"/>
        <xsl:value-of select="replace($tempId4, '^[0-9.-/_]+', '')"/>
    </xsl:function>
    
</xsl:stylesheet>
