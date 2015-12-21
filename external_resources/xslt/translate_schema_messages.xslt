<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>

	<xsl:key name="translationLookup" match="ElementName" use="@elementId"/>

	<xsl:template match="text()"/>

	<xsl:template match="/">
		<xsl:element name="ValidationResult">
			<xsl:element name="SchemaErrors">
				<xsl:element name="LineErrors">
					<xsl:apply-templates/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="*[starts-with(name(), 'Line_')]">

		<xsl:variable name="elementName"><xsl:value-of select="name()"/></xsl:variable>

		<xsl:element name="{$elementName}">

			<xsl:variable name="columnName"><xsl:value-of select="./ColumnName"/></xsl:variable>
			<xsl:variable name='outputMessage'><xsl:value-of select="key('translationLookup',$columnName)"/></xsl:variable>
			
			<xsl:element name="ErrorDetail">
				<xsl:element name="OutputMessage">
					<xsl:value-of select="$outputMessage"/>
				</xsl:element>
			</xsl:element>

		</xsl:element>

	</xsl:template>
</xsl:stylesheet>
