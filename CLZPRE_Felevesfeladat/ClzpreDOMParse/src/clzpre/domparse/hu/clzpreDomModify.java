package clzpre.domparse.hu;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class clzpreDomModify {

    // ========== Attribútumnevek magyarítása a megjelenítéshez ==========
    private static final Map<String, String> kulcsnevek = new HashMap<>();
    static {
        kulcsnevek.put("oId", "Oktató azonosító");
        kulcsnevek.put("kId", "Kurzus azonosító");
        kulcsnevek.put("hId", "Hallgató azonosító");
        kulcsnevek.put("tId", "Tanszék azonosító");
        kulcsnevek.put("kvId", "Követelmény azonosító");
        kulcsnevek.put("tanszekId", "Tanszék azonosító (FK)");
        kulcsnevek.put("oktatoId", "Oktató azonosító (FK)");
    }

    // ========= Mely XML elemek jelenjenek meg a konzolon =========
    private static final Set<String> megjelenitendo = new HashSet<>(Arrays.asList(
            "oktato",
            "jegy",
            "kurzus",
            "vizsga"
    ));

    public static void main(String[] args) {
        try {

            // ======================== XML BEOLVASÁS ============================
            File xmlFile = new File("CLZPRE_XMLTask/CLZPRE_XML.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();


            // ===================================================================
            // 1) MÓDOSÍTÁS: Hallgató (hId=1) jegye (kId=1) pontszám: 85 -> 95
            // ===================================================================
            NodeList jegyLista = doc.getElementsByTagName("jegy");

            for (int i = 0; i < jegyLista.getLength(); i++) {
                Element j = (Element) jegyLista.item(i);

                // Hallgató 1 jegye az 1. kurzusból
                if ("1".equals(j.getAttribute("hId")) && "1".equals(j.getAttribute("kId"))) {
                    Node pontszam = j.getElementsByTagName("pontszam").item(0);
                    if (pontszam != null) {
                        System.out.println("Eredeti pontszám: " + pontszam.getTextContent());
                        pontszam.setTextContent("95");
                        System.out.println("Új pontszám -> 95\n");
                    }
                    break;
                }
            }


            // ===================================================================
            // 2) MÓDOSÍTÁS: Kurzus (kId=1) kredit értéke: 4 -> 6
            // ===================================================================
            NodeList kurzusLista = doc.getElementsByTagName("kurzus");
            for (int i = 0; i < kurzusLista.getLength(); i++) {
                Element k = (Element) kurzusLista.item(i);

                if ("1".equals(k.getAttribute("kId"))) {
                    Node kredit = k.getElementsByTagName("kredit").item(0);

                    if (kredit != null) {
                        System.out.println("Eredeti kredit: " + kredit.getTextContent());
                        kredit.setTextContent("6");
                        System.out.println("Új kredit -> 6\n");
                    }
                    break;
                }
            }


            // ===================================================================
            // 3) MÓDOSÍTÁS: Vizsga (kId=1) vizsgázó neve: "Horváth Bálint" -> "Alaktos István"
            // ===================================================================
            NodeList vizsgaLista = doc.getElementsByTagName("vizsga");
            for (int i = 0; i < vizsgaLista.getLength(); i++) {
                Element v = (Element) vizsgaLista.item(i);

                if ("1".equals(v.getAttribute("kId"))) {
                    Node vizsgazo = v.getElementsByTagName("vizsgazo").item(0);

                    if (vizsgazo != null) {
                        System.out.println("Eredeti vizsgázó: " + vizsgazo.getTextContent());
                        vizsgazo.setTextContent("Alaktos István");
                        System.out.println("Új vizsgázó -> Alaktos István\n");
                    }
                    break;
                }
            }


            // ===================================================================
            // 4) MÓDOSÍTÁS: Oktató (oId=1) telefonszáma: 06-46-111-222 -> 06-70-987-6543
            // ===================================================================
            NodeList oktatoLista = doc.getElementsByTagName("oktato");
            for (int i = 0; i < oktatoLista.getLength(); i++) {
                Element o = (Element) oktatoLista.item(i);

                if ("1".equals(o.getAttribute("oId"))) {
                    Node tel = o.getElementsByTagName("telefonszam").item(0);

                    if (tel != null) {
                        System.out.println("Eredeti telefonszám: " + tel.getTextContent());
                        tel.setTextContent("06-70-987-6543");
                        System.out.println("Új telefonszám -> 06-70-987-6543\n");
                    }
                    break;
                }
            }


            // ===================================================================
            // 5) KIVÁLASZTOTT ENTITÁSOK KIÍRÁSA (MÓDOSÍTOTT)
            // ===================================================================
            System.out.println("\n====== KIVÁLASZTOTT ENTITÁSOK (MÓDOSÍTOTT) ======\n");

            Node root = doc.getDocumentElement();
            NodeList children = root.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) n;

                    // Csak a kiválasztott elemek
                    if (!megjelenitendo.contains(elem.getNodeName()))
                        continue;

                    System.out.println("Elem: " + elem.getNodeName());
                    System.out.println("--------------------------------");

                    // ===== ATTRIBÚTUMOK MAGYARÍTÁSA =====
                    NamedNodeMap attrs = elem.getAttributes();
                    for (int a = 0; a < attrs.getLength(); a++) {
                        Node attr = attrs.item(a);
                        String kulcs = kulcsnevek.getOrDefault(attr.getNodeName(), attr.getNodeName());
                        System.out.println(kulcs + ": " + attr.getNodeValue());
                    }

                    // ===== CHILD NODES KIÍRÁSA =====
                    NodeList subs = elem.getChildNodes();
                    for (int j = 0; j < subs.getLength(); j++) {
                        Node sub = subs.item(j);

                        if (sub.getNodeType() == Node.ELEMENT_NODE) {
                            Element se = (Element) sub;

                            // Ha az elemnek vannak további elemei (pl. cím blokk)
                            if (hasElementChild(se)) {
                                System.out.println(se.getNodeName() + ":");
                                NodeList deep = se.getChildNodes();
                                for (int k = 0; k < deep.getLength(); k++) {
                                    Node d = deep.item(k);
                                    if (d.getNodeType() == Node.ELEMENT_NODE) {
                                        System.out.println("    " + d.getNodeName() + ": " + d.getTextContent());
                                    }
                                }
                            } else {
                                System.out.println(se.getNodeName() + ": " + se.getTextContent());
                            }
                        }
                    }

                    System.out.println("--------------------------------\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===============================================================
    // Segédfüggvény: megállapítja, hogy van-e további elem gyerek
    // ===============================================================
    private static boolean hasElementChild(Element e) {
        NodeList list = e.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
                return true;
        }
        return false;
    }
}