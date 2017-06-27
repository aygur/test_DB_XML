<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
    <entries>
        <xsl:apply-templates select="/entries/entry" />
    </entries>

    </xsl:template>
    <xsl:template match="entry">
        <xsl:element name="entry" >
            <xsl:attribute name="field">
                <xsl:apply-templates select="./field"/>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>

    <xsl:template match="field">
        <xsl:value-of select="."/>
    </xsl:template>
</xsl:stylesheet>