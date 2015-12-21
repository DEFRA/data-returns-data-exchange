<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:output method="xml" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>

	<xsl:param name="nodeNames" required="yes"/>

	<xsl:template match="/returns/return[1]">
		<xsl:element name="GeneralResult">
			<xsl:element name="TransformationResults">
				<xsl:element name="Results">
				
					<xsl:variable name="match" select="."/>				
					
					<xsl:for-each select="tokenize($nodeNames,',')">
						<xsl:variable name="nodeName" select="."/>				
						
						<xsl:variable name="elementName">Result_<xsl:value-of select="$nodeName"/></xsl:variable>
	
						<xsl:element name="{$elementName}">
							<xsl:element name="Id">
								<xsl:value-of select="$nodeName"/>
							</xsl:element>
							<xsl:element name="Value">
								<xsl:value-of select="$match/*[name()=$nodeName]"/>
							</xsl:element>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>