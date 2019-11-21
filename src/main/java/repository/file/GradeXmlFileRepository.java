package repository.file;

import domain.Grade;
import domain.GradeId;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import validation.Validator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.time.LocalDate;

public class GradeXmlFileRepository extends AbstractXmlFileRepository<GradeId, Grade> {
    public GradeXmlFileRepository(Validator<Grade> validator, String fileName) {
        super(validator, fileName);
    }

    private void put(Document dom, Element grade, String name, String value) {
        Element eName = dom.createElement(name);
        eName.appendChild(dom.createTextNode(value));
        grade.appendChild(eName);
    }

    private void put(Document dom, Element grade, String name, Integer value) {
        Element eName = dom.createElement(name);
        eName.appendChild(dom.createTextNode(value.toString()));
        grade.appendChild(eName);
    }

    @Override
    Element writeEntity(Grade entity) throws ParserConfigurationException {
        Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element g = dom.createElement("Grade");
        put(dom, g, "studentId", entity.getId().getStudentId());
        put(dom, g, "homeworkId", entity.getHomeworkId());
        put(dom, g, "professorId", entity.getProfessorId());
        put(dom, g, "handOverDate", entity.getHandOverDate().toString());
        put(dom, g, "givenGrade", entity.getGivenGrade().toString());
        put(dom, g, "feedback", entity.getFeedback());

        return g;
    }

    @Override
    Grade readEntity(Element entity) {
        Integer studentId = Integer.parseInt(entity.getElementsByTagName("studentId").item(0).getTextContent());
        Integer homeworkId = Integer.parseInt(entity.getElementsByTagName("homeworkId").item(0).getTextContent());
        Integer professorId = Integer.parseInt(entity.getElementsByTagName("professorId").item(0).getTextContent());
        GradeId gradeId = new GradeId(homeworkId, studentId);
        LocalDate handOverDate = LocalDate.parse(entity.getElementsByTagName("handOverDate").item(0).getTextContent());
        Double givenGrade = Double.parseDouble(entity.getElementsByTagName("givenGrade").item(0).getTextContent());
        String feedback = entity.getElementsByTagName("feedback").item(0).getTextContent();
        return new Grade(gradeId, handOverDate, professorId, givenGrade, homeworkId, feedback);

    }
}
