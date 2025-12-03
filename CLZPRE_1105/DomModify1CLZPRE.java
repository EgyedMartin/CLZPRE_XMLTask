package CLZPRE_1105;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomModify1CLZPRE {
    private static final String INPUT_XML_NAME = "orarendCLZPRE.xml"; // Használjunk konvenciót
    private static final String OUTPUT_XML_NAME = "orarendModify1CLZPRE.xml"; // Mentés fájlnév

    public static void main(String[] args) {
        Document doc;
        
        try {
            // 1. Dokumentum beolvasása
            doc = readXmlDocument(INPUT_XML_NAME);

            // ==========================================================
            // 1. Módosítás: Hozzáad egy 'óraadó' elemet
            // ==========================================================
            System.out.println("--- 1. MÓDOSÍTÁS: 'óraadó' hozzáadása (id=\"1\") ---");
            addOraadoToFirstOra(doc, "id", "1", "Dr. Óraadó Tanár");

            // Kiírás a konzolra
            System.out.println("\n--- Módosított XML (1. rész) a konzolon ---");
            printDocumentToConsole(doc);

            // Kiírás fájlba
            writeDocumentToFile(doc, OUTPUT_XML_NAME);
            System.out.println("\n--- Módosított XML (1. rész) sikeresen elmentve a: " + OUTPUT_XML_NAME + " fájlba ---");
            
            // ==========================================================
            // 2. Módosítás: Minden óra típusának módosítása gyakorlatról előadásra
            // ==========================================================
            System.out.println("\n--- 2. MÓDOSÍTÁS: Minden 'tipus' elem 'gyakorlat'-ról 'előadás'-ra cserélése ---");
            modifyAllOraType(doc, "gyakorlat", "előadás");

            // Kiírás a konzolra
            System.out.println("\n--- Módosított XML (2. rész) a konzolon ---");
            printDocumentToConsole(doc);

        } catch (Exception e) {
            System.err.println("Hiba történt a DOM műveletek során: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Beolvassa a megadott nevű XML fájlt és visszaadja a DOM Document objektumot.
     */
    private static Document readXmlDocument(String fileName) 
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(fileName));
        doc.getDocumentElement().normalize();
        System.out.println("XML fájl beolvasva: " + fileName);
        return doc;
    }

    // --- 1. Módosítás: Hozzáad egy óraadó elemet ---
    
    /**
     * Megkeresi a megadott attribútumú elemet, és hozzáad egy 'óraadó' child elemet.
     */
    private static void addOraadoToFirstOra(Document doc, String attrName, String attrValue, String oraadoName) {
        // Összes <ora> elem lekérése
        NodeList oraList = doc.getElementsByTagName("ora");
        
        for (int i = 0; i < oraList.getLength(); i++) {
            Node oraNode = oraList.item(i);
            
            if (oraNode.getNodeType() == Node.ELEMENT_NODE) {
                Element oraElement = (Element) oraNode;
                
                // Ellenőrizzük az attribútumot (pl. id="1")
                if (oraElement.getAttribute(attrName).equals(attrValue)) {
                    
                    // Új 'óraadó' elem létrehozása
                    Element oraadoElement = doc.createElement("óraadó");
                    oraadoElement.setTextContent(oraadoName);
                    
                    // Hozzáadás az <ora> elemhez
                    oraElement.appendChild(oraadoElement);
                    System.out.println("Sikeresen hozzáadva az <óraadó> elem a(z) id=\"" + attrValue + "\" órához: " + oraadoName);
                    
                    // Mivel csak egy példányt kér a feladat, kiléphetünk
                    break;
                }
            }
        }
    }

    // --- 2. Módosítás: Óra típusának módosítása ---
    
    /**
     * Módosítja az összes <tipus> elem tartalmát a megadott értékre.
     */
    private static void modifyAllOraType(Document doc, String oldType, String newType) {
        NodeList tipusList = doc.getElementsByTagName("tipus");
        int count = 0;

        for (int i = 0; i < tipusList.getLength(); i++) {
            Node tipusNode = tipusList.item(i);
            
            if (tipusNode.getNodeType() == Node.ELEMENT_NODE) {
                if (tipusNode.getTextContent().equals(oldType)) {
                     tipusNode.setTextContent(newType);
                     count++;
                }
            }
        }
        System.out.println(count + " db <tipus> elem módosítva innen: " + oldType + ", ide: " + newType);
    }
    
    // --- Segédfüggvények: Kiírás ---

    /**
     * Kiírja a DOM Document tartalmát formázva a konzolra (System.out).
     */
    private static void printDocumentToConsole(Document doc) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        
        // Formázás beállítása
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(System.out);
        
        transformer.transform(source, result);
    }

    /**
     * Kiírja a DOM Document tartalmát a megadott fájlba.
     */
    private static void writeDocumentToFile(Document doc, String fileName) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        
        // Formázás beállítása
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(fileName));
        
        transformer.transform(source, result);
    }
}
