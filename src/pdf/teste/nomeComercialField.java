package pdf.teste;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class nomeComercialField {

    private final PDDocument doc;
    
    //Classe responsável por carregar o arquivo
    public nomeComercialField(InputStream arquivo) throws IOException{
        doc = PDDocument.load (arquivo);
    }
    
    
	public String extrairNomeComercial() throws IOException {
	    PDFTextStripper stripper = new PDFTextStripper();
	    stripper.setStartPage(2);
	    stripper.setEndPage(2);
	    stripper.setAddMoreFormatting(true);
	    String docText = stripper.getText(doc);
	    String lines[] = docText.split("\\r? \\n");
	    for(String line: lines) {
	    	System.out.println(line);
	    }
	   
	    return docText;
	}
	
	public static void main(String arg[]) throws  IOException {
        InputStream archivoI = new FileInputStream("C:\\Users\\loren\\eclipse-workspace\\apache_lucene\\dados-lucene\\Metronidazol.pdf");
        nomeComercialField texto = new nomeComercialField(archivoI);
           texto.extrairNomeComercial();
    }
	
}
