package repository.file;

import domain.Entity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import validation.Validator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

abstract class AbstractXmlFileRepository<ID, E extends Entity<ID>> extends AbstractInFileRepository<ID, E> {
    AbstractXmlFileRepository(Validator<E> validator, String fileName) {
        super(validator, fileName);
    }


    @Override
    void writeEntities() {
        try {
            Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element array = dom.createElement("Array");

            for(E entity : super.findAll()) {
                Element e = writeEntity(entity);
                Node imported = dom.importNode(e, true);
                array.appendChild(imported);
            }
            dom.appendChild(array);
            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(fileName)));
            } catch (TransformerException | IOException te) {
                System.out.println(te.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }

    @Override
    void readEntities() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            File f = new File(fileName);
            Document document = builder.parse(f);
            document.getDocumentElement().normalize();
            NodeList nList = document.getElementsByTagName("Array").item(0).getChildNodes();

            for(int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    E e = readEntity((Element) node);
                    super.save(e);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            //e.printStackTrace();
        }
    }

    abstract Element writeEntity(E entity) throws ParserConfigurationException;

    abstract E readEntity(Element entity);
}
