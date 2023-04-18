package com.ibouce.Elasticsearch.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfUtil {

    public String extractText(MultipartFile file)  {
        Tika tika = new Tika();
        try (InputStream inputStream = file.getInputStream()) {
            byte[] fileBytes = inputStream.readAllBytes();
            byte[] decodedBytes = Base64.decodeBase64(fileBytes);
            return tika.parseToString(new ByteArrayInputStream(decodedBytes));
        } catch (IOException | TikaException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractTextFromPDF(String filePath) {
        try {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            FileInputStream inputstream = new FileInputStream(filePath);
            ParseContext pcontext = new ParseContext();

            //parsing the document using PDF parser
            PDFParser pdfparser = new PDFParser();
            pdfparser.parse(inputstream, handler, metadata, pcontext);

            //getting the content of the document
            System.out.println("Contents of the PDF :" + handler.toString());

            //getting metadata of the document
            /*System.out.println("Metadata of the PDF:");
            String[] metadataNames = metadata.names();

            for(String name : metadataNames) {
                System.out.println(name+ " : " + metadata.get(name));
            }*/

            return handler.toString();
        } catch (TikaException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) throws IOException, TikaException, SAXException {

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream("C:\\Users\\iBouc\\OneDrive\\candidature_doctorat.pdf");
        ParseContext pcontext = new ParseContext();

        //parsing the document using PDF parser
        PDFParser pdfparser = new PDFParser();
        pdfparser.parse(inputstream, handler, metadata, pcontext);

        //getting the content of the document
        System.out.println("Contents of the PDF :" + handler.toString());

        //getting metadata of the document
        System.out.println("Metadata of the PDF:");
        String[] metadataNames = metadata.names();

        for (String name : metadataNames) {
            System.out.println(name + " : " + metadata.get(name));
        }
    }

}