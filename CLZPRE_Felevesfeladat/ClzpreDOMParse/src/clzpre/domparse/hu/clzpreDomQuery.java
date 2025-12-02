package clzpre.domparse.hu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * XML lekérdező program DOM parserrel az EGYETEMI RENDSZER témában (CLZPRE).
 * Feladata:
 * - beolvassa a hallgatókat, jegyeket, kurzusokat és oktatókat
 * - összekapcsolja az adatokat: Hallgató -> Jegy -> Kurzus -> Oktató szerint
 * - hallgatónként kiírja a kapott jegyeket a kurzus és oktató nevével.
 */
public class clzpreDomQuery {

    /**
     * Adatszerkezet egy hallgató által kapott jegyről.
     */
    static class KapottJegy {
        String kurzusNev;
        String pontszam;
        String oktatoNev;
        String leiras;

        KapottJegy(String kurzusNev, String pontszam, String oktatoNev, String leiras) {
            this.kurzusNev = kurzusNev;
            this.pontszam = pontszam;
            this.oktatoNev = oktatoNev;
            this.leiras = leiras;
        }
    }

    public static void main(String[] args) {
        try {
            // =====================================================================
            // 1) XML BEOLVASÁSA
            // =====================================================================
            File inputFile = new File("CLZPRE_XMLTask/CLZPRE_XML.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // DOM fa felépítése
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();


            // =====================================================================
            // 2) OKTATÓK BEOLVASÁSA (oId + név) - korábbi forgalmazók
            // =====================================================================
            Map<String, String> oktatoNevek = new HashMap<>();
            NodeList oktatoLista = doc.getElementsByTagName("oktato");
            for (int i = 0; i < oktatoLista.getLength(); i++) {
                Element o = (Element) oktatoLista.item(i);
                String oId = o.getAttribute("oId");
                String nev = getTextContentOfChild(o, "nev");
                oktatoNevek.put(oId, nev != null ? nev : "Ismeretlen oktató");
            }


            // =====================================================================
            // 3) KURZUSOK BEOLVASÁSA (kId + név + oktatoId) - korábbi sörök
            // =====================================================================
            Map<String, String> kurzusNevek = new HashMap<>();
            // Kurzus ID -> Oktató ID (FK)
            Map<String, String> kurzusOktatok = new HashMap<>();

            NodeList kurzusLista = doc.getElementsByTagName("kurzus");
            for (int i = 0; i < kurzusLista.getLength(); i++) {
                Element k = (Element) kurzusLista.item(i);
                String kId = k.getAttribute("kId");
                // Az XML-ben a kurzus elem attribútumként tartalmazza az oktató ID-t
                String oktatoId = k.getAttribute("oktatoId"); 
                String nev = getTextContentOfChild(k, "nev");

                kurzusNevek.put(kId, nev != null ? nev : "Ismeretlen kurzus");
                kurzusOktatok.put(kId, oktatoId);
            }


            // =====================================================================
            // 4) HALLGATÓK BEOLVASÁSA (hId + név) - korábbi felhasználók
            // =====================================================================
            Map<String, String> hallgatoNevek = new HashMap<>();
            NodeList hallgatoLista = doc.getElementsByTagName("hallgato");

            for (int i = 0; i < hallgatoLista.getLength(); i++) {
                Element h = (Element) hallgatoLista.item(i);
                String hId = h.getAttribute("hId");
                String nev = getTextContentOfChild(h, "nev");
                hallgatoNevek.put(hId, nev != null ? nev : "Ismeretlen hallgató");
            }


            // =====================================================================
            // 5) JEGYEK ÖSSZEKAPCSOLÁSA - korábbi értékelések/vásárlások
            //     Hallgató (hId) → Lista:KapottJegy
            // =====================================================================
            Map<String, List<KapottJegy>> hallgatoiJegyek = new HashMap<>();
            NodeList jegyLista = doc.getElementsByTagName("jegy");

            for (int i = 0; i < jegyLista.getLength(); i++) {
                Element j = (Element) jegyLista.item(i);

                String hId  = j.getAttribute("hId");
                String kId  = j.getAttribute("kId");

                // pontszám és leírás
                String pontszam = getTextContentOfChild(j, "pontszam");
                String leiras = getTextContentOfChild(j, "ertekeles_szoveg");

                // Kurzus adatok
                String kurzusNev = kurzusNevek.getOrDefault(kId, "Kurzus név hiányzik");
                String oktatoId = kurzusOktatok.get(kId);
                String oktatoNev = oktatoNevek.getOrDefault(oktatoId, "Oktató név hiányzik");

                // Új jegy objektum létrehozása
                KapottJegy jegyObj = new KapottJegy(
                        kurzusNev,
                        pontszam != null ? pontszam : "Nincs jegy",
                        oktatoNev,
                        leiras != null ? leiras : "Nincs leírás"
                );

                // Hallgatóhoz hozzárendelés
                List<KapottJegy> lista = hallgatoiJegyek.getOrDefault(hId, new ArrayList<>());
                lista.add(jegyObj);

                hallgatoiJegyek.put(hId, lista);
            }


            // =====================================================================
            // 6) KONZOL KIÍRATÁS
            // =====================================================================
            for (String hId : hallgatoiJegyek.keySet()) {

                String hallgatoNev = hallgatoNevek.get(hId);
                List<KapottJegy> lista = hallgatoiJegyek.get(hId);

                System.out.println("--------------------------------");
                System.out.println("Hallgató: " + hallgatoNev);
                System.out.println("Hallgató azonosító (hId): " + hId);

                if (lista.isEmpty()) {
                    System.out.println("Nincs rögzített jegy.");
                    continue;
                }

                System.out.println("Kapott jegyek:");

                for (KapottJegy jegy : lista) {
                    System.out.println("\n\tKurzus neve: " + jegy.kurzusNev);
                    System.out.println("\tKapott pontszám: " + jegy.pontszam);
                    System.out.println("\tOktató neve: " + jegy.oktatoNev);
                    System.out.println("\tÉrtékelés: " + jegy.leiras);
                }

                System.out.println("--------------------------------\n");
            }

        } catch (Exception ex) {
            System.out.println("Hiba történt XML beolvasás/elemzés során:");
            ex.printStackTrace();
        }
    }

    /**
     * Segédfüggvény:
     * Visszaadja egy gyermek elem szövegét, ha létezik.
     * Ha nincs → null
     */
    private static String getTextContentOfChild(Element parent, String childTag) {
        NodeList nl = parent.getElementsByTagName(childTag);
        if (nl.getLength() == 0) return null;
        Element c = (Element) nl.item(0);
        if (c == null) return null;

        String txt = c.getTextContent();
        return txt != null ? txt.trim() : null;
    }
}