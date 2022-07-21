package top.iseason.bukkit.bukkittemplate.dependency;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlDependency {
    Document doc;

    public XmlDependency(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(file);
    }

    public List<String> getDependency() {
        List<String> list = new ArrayList<>();
        NodeList dependencies = doc.getElementsByTagName("dependencies");
        for (int n = 0; n < dependencies.getLength(); n++) {
            Node item = dependencies.item(n);
            if (item == null) return list;
            NodeList childNodes = item.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;
                Element dependency = (Element) node;
                String groupId = dependency.getElementsByTagName("groupId").item(0).getTextContent();
                String artifactId = dependency.getElementsByTagName("artifactId").item(0).getTextContent();
                String version = dependency.getElementsByTagName("version").item(0).getTextContent();
                list.add(groupId + ":" + artifactId + ":" + version);
            }
        }
        return list;
    }
}
