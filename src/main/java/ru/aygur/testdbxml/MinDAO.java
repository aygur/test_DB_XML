package ru.aygur.testdbxml;

import javax.xml.stream.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitrii on 01.06.17.
 */
public class MinDAO implements Serializable {
    private static final String TRANS_XSL = "trans.xsl";
    private static final String XML_FILE_1= "1.xml";
    private static final String XML_FILE_2= "2.xml";

    private static final String SQL_SELECT_ALL = "SELECT * FROM TEST";
    private static final String SQL_TRUNCATE_TABLE = "TRUNCATE TEST";
    private static final String SQL_INSERT = "INSERT TEST SET FIELD=?";

    private int N;
    private Connector connector;

    public MinDAO() {}

    public MinDAO(String database, String user, String password, int n) {
        this.N = n;
        this.connector = new Connector(database, user, password);
    }

    public MinDAO(String database, String user, String password) {
        this.connector = new Connector(database, user, password);
    }

    public MinDAO(Connector conn) {
        this.connector = conn;
    }

    /**
     * Удаляет все поля в таблице
     */
    private void truncateTable() throws CriticalException {
        try(Connection conn = connector.getConnection();
            Statement statement = conn.createStatement()) {
            statement.executeQuery(SQL_TRUNCATE_TABLE);
            System.out.println("Truncated table data");
        } catch (SQLException e) {
            throw new CriticalException("Exception truncate table", e);
        }
    }

    /**
     * Внесение данных в базу
     * Данные вносятся не сразу, а скопом.
     */
    public void insertNToDB() throws CriticalException {
        this.truncateTable();
        if(getN() == 0) {
            throw new CriticalException("n didn't set or equals zero");
        }
        try(Connection conn = connector.getConnection();
            PreparedStatement ps =
                     conn.prepareStatement(SQL_INSERT)){
            conn.setAutoCommit(false);
            for(long i = 1; i <= N; i++ ){
                ps.setLong(1, i);
                ps.addBatch();
                if(i % 10_000 == 0) {
                    ps.executeBatch();
                    conn.commit();
                    System.out.println("commit to db " + i );
                }
            }
            ps.executeBatch();
            conn.commit();
            System.out.println("Inserted " +  getN() + " to DB");
        } catch (SQLException e) {
            throw new CriticalException("Exception SQL insert", e);
        }
    }

    /**
     * Выборка всех данных из таблицы
     *
     * @return List<Long> все полученные значения
     */
    public List<Long> selectAllFromDB() throws CriticalException {
        List<Long> list = new ArrayList<>();
        try(Connection conn = connector.getConnection();
            Statement statement = conn.createStatement()) {

            ResultSet rs = statement.executeQuery(SQL_SELECT_ALL);

            while (rs.next()){
                list.add(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new CriticalException("Exception in selectAllFromDB : " , e);
        }
        return list;
    }

    /**
     *  Возвращет сумму атрибутов field в файле 2.xml
     *  Парсит файл 2.xml и производит подсчет суммы
     * @return long
     */
    public long sumFieldFromXML() throws CriticalException {
        long sum = 0L;
        try {
            XMLStreamReader xmlr =
                    XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(XML_FILE_2));

            while (xmlr.hasNext()) {
                xmlr.next();
                if (xmlr.isStartElement() && "field".equals(xmlr.getAttributeLocalName(0))) {
                    sum += Long.parseLong(xmlr.getAttributeValue(0));
                }
            }
        } catch (FileNotFoundException e) {
            throw new CriticalException("Exception File Not Found - check file "+ XML_FILE_2, e);
        } catch (XMLStreamException e) {
            throw new CriticalException("Exception XMLStream - validate file "+ XML_FILE_2, e);
        }
        return sum;
    }

    /**
     * <p>Данная функция генерирует xml файл 1.xml</p>
     * Файл заполняется аналогично:
     * <entries>
     *  <entry>
     *      <field>значение поля field</field>
     *   </entry>
     *      ...
     *  </entries>
     */
    public void generateXMLFile() throws CriticalException {
        try (Writer fileWriter = new FileWriter(XML_FILE_1)) {
            XMLOutputFactory output = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = output.createXMLStreamWriter(fileWriter);

            writer.writeStartDocument("UTF-8","1.0");
            writer.writeStartElement("entries");

            for (Long n :
                    selectAllFromDB()) {
                writer.writeStartElement("entry");
                    writer.writeStartElement("field");
                        writer.writeCharacters(n.toString());
                    writer.writeEndElement();
                writer.writeEndElement();
            }

            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            System.out.println("Create 1.XML");

        } catch (XMLStreamException e) {
            throw new CriticalException("Exception XMLStream - validate file "+ XML_FILE_1, e);
        } catch (IOException e) {
            throw new CriticalException("Exception IOException - check file "+ XML_FILE_1, e);
        }
    }

    /**
     * <p>Данная функция генерирует xml файл 2.xml</p>
     * Файл генерируется на основе 1.xml и trans.xsl:
     * <entries>
     *  <entry field="значение поля field">
     *  ...
     *  </entries>
     */
    public void transformXMLFile() throws CriticalException {
        try(InputStream xml = new FileInputStream(XML_FILE_1);
            InputStream xsl = new FileInputStream(TRANS_XSL); //
            OutputStream outputXML = new FileOutputStream(XML_FILE_2)) {

            StreamSource xmlSource = new StreamSource(xml);
            StreamSource styleSource = new StreamSource(xsl);

            StreamResult xmlOutput = new StreamResult(new ByteArrayOutputStream());
            xmlOutput.setOutputStream(outputXML);

            Transformer transformer = TransformerFactory.newInstance().newTransformer(styleSource);
            transformer.transform(xmlSource, xmlOutput);

            System.out.println("Create 2.XML");
        } catch ( TransformerException e){
            throw new CriticalException("Exception in transformXMLFile", e);
        } catch (IOException e) {
            throw new CriticalException("Exception IOException - check file "+ XML_FILE_1, e);
        }
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinDAO minDAO = (MinDAO) o;

        return N == minDAO.N;
    }

    @Override
    public int hashCode() {
        return N;
    }
}
