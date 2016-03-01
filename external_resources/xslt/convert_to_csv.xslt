<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xs="http://www.w3.org/2001/XMLSchema" 
				xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:output method="text" encoding="UTF-8" indent="no" omit-xml-declaration="yes" />
	
	<!-- TODO A bit long winded due to random CSV upload column ordering, could pass config as params ? -->
	
	<xsl:param name="separator" required="yes"/>
	
	<xsl:variable name="column-names">EA_ID,Site_Name,Rtn_Type,Mon_Date,Mon_Date_End,Mon_Period,Smpl_Ref,Smpl_By,Mon_Point,Parameter,Value,Txt_Value,Unit,Ref_Period,Meth_Stand,Comments,CiC,CAS,RD_Code</xsl:variable>
	<xsl:variable name="quote">"</xsl:variable>

	<xsl:template match="/returns">
	
		<!-- Output column names -->
		<xsl:value-of select="$column-names" />
		<xsl:call-template name="add-new-line"/>
		
		<!-- Output return values -->
		<xsl:for-each select="return">
			
			<xsl:apply-templates select="EA_ID" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Site_Name" />
			<xsl:call-template name="add-separator"/>
			
			<xsl:apply-templates select="Rtn_Type" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Mon_Date" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Mon_Date_End" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Mon_Period" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Smpl_Ref" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Smpl_By" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Mon_Point" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Parameter" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Value" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Txt_Value" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Unit" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Ref_Period" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Meth_Stand" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="Comments" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="CiC" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="CAS" />
			<xsl:call-template name="add-separator"/>

			<xsl:apply-templates select="RD_Code" />

			<xsl:call-template name="add-new-line"/>
			
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="EA_ID">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="Site_Name">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="Rtn_Type">
		<xsl:call-template name="add-quote-delimited-value"/>
	</xsl:template>

	<xsl:template match="Mon_Date">
		<xsl:call-template name="add-date-time"/>
	</xsl:template>

	<xsl:template match="Mon_Date_End">
		<xsl:call-template name="add-date-time"/>
	</xsl:template>

	<xsl:template match="Mon_Period">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="Smpl_Ref">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="Smpl_By">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="Mon_Point">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="Parameter">
		<xsl:call-template name="add-quote-delimited-value"/>
	</xsl:template>

	<xsl:template match="Value">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="Txt_Value">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="Unit">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="Ref_Period">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="Meth_Stand">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="Comments">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="CiC">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="CAS">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<xsl:template match="RD_Code">
		<xsl:call-template name="add-non-delimited-value"/>
	</xsl:template>

	<!--  Helpers -->

	<xsl:template name="add-separator">
		<xsl:value-of select="$separator"/>
	</xsl:template>	

	<xsl:template name="add-new-line">
		<xsl:value-of select="'&#13;&#10;'"	disable-output-escaping="yes" />
	</xsl:template>	
	
	<xsl:template name="add-non-delimited-value">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<xsl:template name="add-date-time">
		<xsl:variable name="dtVal" select="normalize-space(.)" />
		
		<xsl:analyze-string select="$dtVal"
							regex="(\d+)-(\d{{2}})-(\d+)(T\d{{2}}:\d{{2}}:\d{{2}})?">
			<xsl:matching-substring>
				<xsl:choose>
					<xsl:when test="string-length(regex-group(1)) = 4">
						<!-- Date is already in international format (yyyy-mm-dd) -->
						<xsl:value-of select="concat(regex-group(1), '-', regex-group(2), '-', regex-group(3))"/>
					</xsl:when>
					<xsl:otherwise>
						<!-- Date is in UK format dd-mm-yyyy, convert to international format -->
						<xsl:value-of select="concat(regex-group(3), '-', regex-group(2), '-', regex-group(1))"/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="regex-group(4)"/>
			</xsl:matching-substring>
			<xsl:non-matching-substring>
				<xsl:message>convert_to_csv.xslt: Invalid date (<xsl:value-of select="."/>) encountered, ignoring.</xsl:message>
			</xsl:non-matching-substring>
		</xsl:analyze-string>
	</xsl:template>


	<xsl:template name="add-quote-delimited-value">
		<xsl:value-of select="$quote"/><xsl:call-template name="add-non-delimited-value"/><xsl:value-of select="$quote"/>
	</xsl:template>
</xsl:stylesheet>