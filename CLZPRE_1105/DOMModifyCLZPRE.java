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

public class DOMModifyCLZPRE {
    
    public static void main(String[] args) {
        final String xmlFilePath = "clzprehallgato.xml";

        // 1. DOM Parser létrehozása
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            // 2. XML fájl beolvasása és Document objektum létrehozása
            Document doc = db.parse(new File(xmlFilePath));
            doc.getDocumentElement().normalize();

            System.out.println("--- Eredeti XML beolvasva ---");
            printDocumentToConsole(doc);
            System.out.println("------------------------------\n");

            // 3. Módosítás végrehajtása (id="01" hallgató)
            modositHallgato(doc, "01", "Tamás", "Varga");

            // 4. Módosított XML kiírása a konzolra
            System.out.println("--- Módosított XML a konzolon ---");
            printDocumentToConsole(doc);
            System.out.println("----------------------------------\n");
            
        } catch (ParserConfigurationException e) {
            System.err.println("Hiba a Parser konfigurációban: " + e.getMessage());
        } catch (SAXException e) {
            System.err.println("Hiba az XML beolvasásakor: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Hiba a fájlkezelésben (nem található a fájl?): " + e.getMessage());
        } catch (TransformerException e) {
            System.err.println("Hiba a kiírás során: " + e.getMessage());
        }
    }

    /**
     * Megkeresi a megadott ID-jű hallgatót, és lecseréli a keresztnevet és vezetéknevet.
     * @param doc A DOM Document objektum.
     * @param id A hallgató azonosítója ("01").
     * @param ujKeresztnev Az új keresztnév.
     * @param ujVezeteknev Az új vezetéknév.
     */
    private static void modositHallgato(Document doc, String id, String ujKeresztnev, String ujVezeteknev) {
        
        // Összes <hallgato> elem lekérése
        NodeList hallgatoList = doc.getElementsByTagName("hallgato");
        
        boolean talalat = false;

        for (int i = 0; i < hallgatoList.getLength(); i++) {
            Node hallgatoNode = hallgatoList.item(i);
            
            if (hallgatoNode.getNodeType() == Node.ELEMENT_NODE) {
                Element hallgatoElement = (Element) hallgatoNode;
                
                // Ellenőrizzük az 'id' attribútumot
                if (hallgatoElement.getAttribute("id").equals(id)) {
                    
                    // A megadott hallgato elem megvan (id="01")
                    talalat = true;
                    System.out.println("Módosítás: Hallgató (id=" + id + ") megtalálva.");

                    // Keresztnev elem tartalmának módosítása
                    Node keresztnevNode = hallgatoElement.getElementsByTagName("keresztnev").item(0);
                    if (keresztnevNode != null) {
                        keresztnevNode.setTextContent(ujKeresztnev);
                        System.out.println("  -> Keresztnév módosítva: " + ujKeresztnev);
                    }

                    // Vezeteknev elem tartalmának módosítása
                    Node vezeteknevNode = hallgatoElement.getElementsByTagName("vezeteknev").item(0);
                    if (vezeteknevNode != null) {
                        vezeteknevNode.setTextContent(ujVezeteknev);
                        System.out.println("  -> Vezetéknév módosítva: " + ujVezeteknev);
                    }
                    
                    // Mivel a fő kulcs megtalálva, kiléphetünk a ciklusból
                    break;
                }
            }
        }
        
        if (!talalat) {
            System.err.println("Hiba: Hallgató (id=" + id + ") nem található a módosításhoz.");
        }
    }

    /**
     * Kiírja a DOM Document tartalmát formázva a konzolra (System.out).
     * @param doc A kiírandó Document.
     * @throws TransformerException Hiba a transzformálás során.
     */
    private static void printDocumentToConsole(Document doc) throws TransformerException {
        // Transzformáló objektum létrehozása
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        
        // A kimenet formázása (szépen tördelt XML)
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        
        // Kiírás a System.out-ra
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(System.out);
        
        transformer.transform(source, result);
    }
}
