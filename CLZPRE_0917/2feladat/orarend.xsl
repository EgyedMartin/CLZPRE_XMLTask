<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="UTF-8" indent="yes"/>

<xsl:template match="/">
  <html>
  <head>
    <title>CLZPRE Órarend - HTML Konverzió</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #e8eaf6; margin: 20px; }
        h1 { text-align: center; color: #3f51b5; border-bottom: 3px solid #3f51b5; padding-bottom: 10px; }
        table { 
            width: 90%; 
            border-collapse: collapse; 
            margin: 20px auto; 
            background-color: white; 
            box-shadow: 0 6px 12px rgba(0,0,0,0.2); 
            border-radius: 8px;
            overflow: hidden;
        }
        th, td { 
            border: 1px solid #c5cae9; 
            padding: 10px 15px; 
            text-align: left; 
        }
        th { 
            background-color: #5c6bc0; 
            color: white; 
            font-weight: bold; 
            text-transform: uppercase;
        }
        tr:nth-child(even) { background-color: #eef1f9; } 
        tr:hover { background-color: #c5cae9; }
        .ora-type { font-style: italic; color: #757575; font-size: 0.9em; }
    </style>
  </head>
  <body>
    <h1>CLZPRE 2025 Őszi Órarend (XML Konverzió)</h1>
    <table>
      <tr>
        <th>Nap</th>
        <th>Kezdés</th>
        <th>Befejezés</th>
        <th>Tárgy</th>
        <th>Helyszín</th>
        <th>Oktató</th>
        <th>Szak</th>
      </tr>
      
      <xsl:for-each select="CLZPRE_orarend/ora">
        <xsl:sort select="idopont/nap" order="ascending"/>
        <xsl:sort select="idopont/tol" order="ascending"/>
        
        <tr>
          <td><xsl:value-of select="idopont/nap"/></td>
          <td><xsl:value-of select="idopont/tol"/></td>
          <td><xsl:value-of select="idopont/ig"/></td>
          <td>
            <xsl:value-of select="targy"/>
            <br/><span class="ora-type"> (<xsl:value-of select="@tipus"/>)</span>
          </td>
          <td><xsl:value-of select="helyszin"/></td>
          <td><xsl:value-of select="oktato"/></td>
          <td><xsl:value-of select="szak"/></td>
        </tr>
      </xsl:for-each>
      
    </table>
  </body>
  </html>
</xsl:template>

</xsl:stylesheet>