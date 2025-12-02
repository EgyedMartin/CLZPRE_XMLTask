package clzpre.domparse.hu;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ========================================================================
 * DOM alapú XML olvasó program az EGYETEMI RENDSZER témában (CLZPRE)
 * * Feladata:
 * - A teljes XML dokumentum bejárása (CLZPRE_XML.xml)
 * - Elemenként kiírás magyar megnevezésekkel
 * - Attribútumok és gyermekelemek megjelenítése blokkszerűen
 * - Konzolra + TXT fájlba mentés
 * ========================================================================
 */
public class clzpreDomRead {

    // ===== Magyarított elemnevek tárolása (pl. "kurzus" -> "Kurzus") =====
    private static final Map<String, String> adatNevek = new HashMap<>();

    // ===== Attribútumnevek magyarítása (oId, kId, stb.) =====
    private static final Map<String, String> kulcsNevek = new HashMap<>();


    // =====================================================================
    // MAPPÁK feltöltése — XML tagek magyar megfelelői
    // =====================================================================
    static {
        // ========= Oktatók (Felhasználó) =========
        adatNevek.put("oktato", "Oktató");
        adatNevek.put("nev", "Név");
        adatNevek.put("szekhely", "Székhely/Iroda");
        adatNevek.put("telefonszam", "Telefonszám");

        // ========= Tanszékek (Főzde) =========
        adatNevek.put("tanszek", "Tanszék");
        adatNevek.put("alapitva", "Alapítva");
        adatNevek.put("cim", "Cím");
        adatNevek.put("varos", "Város");
        adatNevek.put("utca", "Utca");
        adatNevek.put("hazszam", "Házszám");

        // ========= Kurzusok (Sör) =========
        adatNevek.put("kurzus", "Kurzus");
        adatNevek.put("kredit", "Kredit érték");
        adatNevek.put("leiras", "Leírás");

        // ========= Követelmények (Címke) =========
        adatNevek.put("kovetelmeny", "Követelmény");
        adatNevek.put("tipus", "Típus");
        adatNevek.put("vizsga_ido", "Vizsga időpont");
        adatNevek.put("min_pont", "Minimális pontszám");
        adatNevek.put("max_pont", "Maximális pontszám");

        // ========= Hallgatók (Forgalmazó) =========
        adatNevek.put("hallgato", "Hallgató");
        adatNevek.put("email", "E-mail cím");
        adatNevek.put("neptun", "Neptun kód");
        adatNevek.put("profilkep", "Profilkép");

        // ========= Jelentkezések (Értékelés) =========
        adatNevek.put("jelentkezes", "Jelentkezés (Oktató)");
        adatNevek.put("datum", "Dátum");
        adatNevek.put("megjegyzes", "Megjegyzés");

        // ========= Vizsgák (Főzés) =========
        adatNevek.put("vizsga", "Vizsga");
        adatNevek.put("vizsgazo", "Vizsgázó");
        
        // ========= Jegyek (Vásárlás) =========
        adatNevek.put("jegy", "Jegy (Hallgató)");
        adatNevek.put("pontszam", "Pontszám");
        adatNevek.put("ertekeles_szoveg", "Értékelés szövege");

        // ========= XML ATTRIBÚTUMOK MAGYARÍTÁSA =========
        kulcsNevek.put("oId", "Oktató azonosító");
        kulcsNevek.put("kId", "Kurzus azonosító");
        kulcsNevek.put("hId", "Hallgató azonosító");
        kulcsNevek.put("tId", "Tanszék azonosító");
        kulcsNevek.put("kvId", "Követelmény azonosító");
        kulcsNevek.put("tanszekId", "Tanszék azonosító (FK)");
        kulcsNevek.put("oktatoId", "Oktató azonosító (FK)");
    }

    public static void main(String[] args) {

        try {
            // =================================================================
            // 1) XML dokumentum beolvasása
            // =================================================================
            // Az XML fájl helye a feladat struktúrában
            File xmlFile = new File("CLZPRE_XMLTask/CLZPRE_XML.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();


            // =================================================================
            // 2) Kiíró TXT fájl megnyitása
            // =================================================================
            // Kimeneti fájl helye
            FileWriter writer = new FileWriter("CLZPRE_XMLTask/CLZPRE_XML_output.txt");


            // =================================================================
            // 3) Gyökérelem neve kiírás
            // =================================================================
            String rootName = doc.getDocumentElement().getNodeName();
            printLine("Gyökér elem: " + at(rootName), writer);
            printLine("--------------------------------", writer);


            // =================================================================
            // 4) Gyökér gyermekelemeinek bejárása
            // =================================================================
            NodeList rootChildren = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < rootChildren.getLength(); i++) {
                Node node = rootChildren.item(i);

                // Csak ELEMEK (szöveg, whitespace kizárva)
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element elem = (Element) node;

                    // Nyitó blokk
                    printLine("\nElem: " + at(elem.getNodeName()), writer);
                    printLine("--------------------------------", writer);


                    // =================================================================
                    // 4/A) ATTRIBÚTUMOK (pl. oId, kId) magyar névvel
                    // =================================================================
                    NamedNodeMap attributes = elem.getAttributes();

                    if (attributes != null && attributes.getLength() > 0) {

                        for (int a = 0; a < attributes.getLength(); a++) {
                            Node attr = attributes.item(a);

                            // kulcsnév magyarítás
                            String magyarKulcs =
                                kulcsNevek.getOrDefault(attr.getNodeName(), attr.getNodeName());

                            printLine(magyarKulcs + ": " + attr.getNodeValue(), writer);
                        }
                    }


                    // =================================================================
                    // 4/B) Gyermekelemek feldolgozása
                    // =================================================================
                    NodeList subNodes = elem.getChildNodes();

                    for (int j = 0; j < subNodes.getLength(); j++) {
                        Node sub = subNodes.item(j);

                        if (sub.getNodeType() == Node.ELEMENT_NODE) {
                            Element subElem = (Element) sub;

                            // Ha van mélyebb struktúra (pl. <cim> alatt <varos>, <utca>…)
                            if (subElem.hasChildNodes() && hasElementChild(subElem)) {

                                printLine(at(subElem.getNodeName()) + ":", writer);

                                NodeList deepList = subElem.getChildNodes();

                                for (int k = 0; k < deepList.getLength(); k++) {
                                    Node deep = deepList.item(k);

                                    if (deep.getNodeType() == Node.ELEMENT_NODE) {
                                        printLine(
                                            "    " + at(deep.getNodeName()) + ": "
                                            + deep.getTextContent(),
                                            writer
                                        );
                                    }
                                }

                            } else {
                                // sima egy szintű elem: <nev>XML Adatbázisok</nev>
                                printLine(
                                    at(subElem.getNodeName()) + ": "
                                    + subElem.getTextContent(),
                                    writer
                                );
                            }
                        }
                    }

                    printLine("--------------------------------", writer);
                }
            }

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // =========================================================================
    // Segédfüggvény: megállapítja, hogy egy elemnek van-e másik elem gyermeke
    // (tehát összetett-e)
    // =========================================================================
    private static boolean hasElementChild(Element e) {
        NodeList list = e.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
                return true;
        }
        return false;
    }


    // =========================================================================
    // Elemnév magyarítása (adatNevek → fallback az eredetire)
    // =========================================================================
    private static String at(String name) {
        return adatNevek.getOrDefault(name, name);
    }


    // =========================================================================
    // Kiírás konzolra + fájlba egyszerre
    // =========================================================================
    private static void printLine(String text, FileWriter writer) throws Exception {
        System.out.println(text);
        writer.write(text + System.lineSeparator());
    }
}