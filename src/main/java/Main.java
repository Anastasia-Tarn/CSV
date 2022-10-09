
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.sun.jdi.Value;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileName2 = "data.xml";
        writeString(listToJson(parseCSV(columnMapping, fileName)), "data.json");
        parseXML(fileName2);

    }

    public static List<Employee> parseCSV (String[] columnMapping,String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy <Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee>csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee>staff = csv.parse();
            return staff;

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
    }

    public static String listToJson(List<Employee>staff) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type type = new TypeToken<List<Employee>>(){}.getType();
        String json = gson.toJson(staff, type);
        System.out.println(json);
        return json;
    };

    public static File writeString(String json, String path) {
        try (FileWriter file = new FileWriter(path)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
        return new File(path);
    }

    public static List<Employee> parseXML (String path) {
        List<Employee> staff = new ArrayList<>();
        try {
            File file = new File(path);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            Node root = doc.getFirstChild();
            NodeList nodelist = root.getChildNodes();
            for (int i = 0; i < nodelist.getLength(); i++) {
                Node n = nodelist.item(i);
                NodeList elementChild = n.getChildNodes();
                long id = 0;
                String firstName = null;
                String lastName = null;
                String country = null;
                int age = 0;
                for (int j = 0; j < elementChild.getLength(); j++) {
                    Node child = nodelist.item(j);
                    switch (child.getNodeName()) {
                        case "id": {
                            id = Long.valueOf(child.getTextContent());
                        }
                        case "firstName": {
                            firstName = child.getTextContent();
                        }
                        case "lastName": {
                            lastName = child.getTextContent();
                        }
                        case "country": {
                            country = child.getTextContent();
                        }
                        case "age": {
                            age = Integer.valueOf(child.getTextContent());
                        }
                    }
                }
                Employee employee = new Employee(id, firstName, lastName, country, age);
                staff.add(employee);
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            System.out.println(e);
        }
        return staff;
    }


}
