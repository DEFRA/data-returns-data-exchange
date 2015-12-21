<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:output method="xml" encoding="UTF-8" indent="no"/>

	<xsl:param name="lineNos" required="yes"/>
	<xsl:variable name="tokenizedLineNos" select="tokenize($lineNos,',')"/>

	<xsl:template match="/">
		<xsl:element name="ValidationResult">
			<xsl:element name="SchemaErrors">
				<xsl:element name="LineErrors">

					<xsl:for-each select="returns/return">

						<xsl:for-each select="*[@xmlId]">
							<xsl:if test="fn:index-of($tokenizedLineNos, @xmlId)">

								<xsl:variable name="elementName">Line_<xsl:value-of select="@xmlId"/></xsl:variable>

								<xsl:element name="{$elementName}">
									<xsl:element name="OutputLineNo">
										<xsl:value-of select="../@srcId"/>
									</xsl:element>

									<xsl:element name="ErrorValue">
										<xsl:value-of select="."/>
									</xsl:element>

								</xsl:element>
								
							</xsl:if>
						</xsl:for-each>

					</xsl:for-each>

				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>