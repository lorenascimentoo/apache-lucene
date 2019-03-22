package pdf.teste;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.tika.Tika;

public class Indexador {
  private static Logger logger = Logger.getLogger(Indexador.class);
  //{1}
  private String diretorioDosIndices = System.getProperty("user.dir")
      + "/indice-lucene";
  //{2}
  private String diretorioParaIndexar = System.getProperty("user.dir")
      + "/dados-lucene";
  //{3}
  private IndexWriter writer;
  //{4}
  private Tika tika;

  public static void main(String[] args) {
    Indexador indexador = new Indexador();
    indexador.indexaArquivosDoDiretorio();
  }

  public void indexaArquivosDoDiretorio() {
    try {
      File diretorio = new File(diretorioDosIndices);
      apagaIndices(diretorio);
      //{5}
      Directory d = new SimpleFSDirectory(diretorio);
      logger.info("Diretório do índice: " + diretorioDosIndices);
      //{6}
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
      //{7}
      IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
          analyzer);
      //{8}
      writer = new IndexWriter(d, config);
      long inicio = System.currentTimeMillis();
      indexaArquivosDoDiretorio(new File(diretorioParaIndexar));
      //{12}
      writer.commit();
      writer.close();
      long fim = System.currentTimeMillis();
      logger.info("Tempo para indexar: " + ((fim - inicio) / 1000) + "s");
    } catch (IOException e) {
      logger.error(e);
    }
  }

  private void apagaIndices(File diretorio) {
    if (diretorio.exists()) {
      File arquivos[] = diretorio.listFiles();
      for (File arquivo : arquivos) {
        arquivo.delete();
      }
    }
  }

  @SuppressWarnings("resource")
public void indexaArquivosDoDiretorio(File raiz) {
    FilenameFilter filtro = new FilenameFilter() {
      public boolean accept(File arquivo, String nome) {
        if (nome.toLowerCase().endsWith(".pdf")
            || nome.toLowerCase().endsWith(".odt")
            || nome.toLowerCase().endsWith(".doc")
            || nome.toLowerCase().endsWith(".docx")
            || nome.toLowerCase().endsWith(".ppt")
            || nome.toLowerCase().endsWith(".pptx")
            || nome.toLowerCase().endsWith(".xls")
            || nome.toLowerCase().endsWith(".txt")
            || nome.toLowerCase().endsWith(".rtf") 
        	|| nome.toLowerCase().endsWith(".html")){
          return true;
        }
        return false;
      }
    };
    for (File arquivo : raiz.listFiles(filtro)) {
      if (arquivo.isFile()) {
        StringBuffer msg = new StringBuffer();
        msg.append("Indexando o arquivo ");
        msg.append(arquivo.getAbsoluteFile());
        msg.append(", ");
        msg.append(arquivo.length() / 1000);
        msg.append("kb");
        logger.info(msg);

        try
		{
			PDDocument document = PDDocument.load(arquivo);// here file1.pdf is the name of pdf file which we want to read....
			document.getClass();
			if (!document.isEncrypted())
			{
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);
				PDFTextStripper Tstripper = new PDFTextStripper();
				String str = Tstripper.getText(document);
				
				Scanner scn = null;					
				scn = new Scanner(str);
				List<String> line = new ArrayList<String>();
				
				while (scn.hasNextLine()) 
				{	
					
					line.add(scn.nextLine().trim());
					//System.out.println("\n"+line);
					
				}
				String nomeComercial = line.get(0);
				String fabricante = line.get(1);
				
				System.out.println(nomeComercial);
				System.out.println(fabricante);
				
				System.out.println(line);
				
				indexaArquivo(arquivo, str, nomeComercial, fabricante);
			}
			document.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
      } else {
        indexaArquivosDoDiretorio(arquivo);
        System.out.println("O arquivo "+raiz+ " foi indexado!");
      }
    }
  }

  private void indexaArquivo(File arquivo, String textoExtraido, String nomeComercial, String fabricante) {
    SimpleDateFormat formatador = new SimpleDateFormat("yyyyMMdd");
    String ultimaModificacao = formatador.format(arquivo.lastModified());
    //{10}
    Document documento = new Document();
    documento.add(new Field("UltimaModificacao", ultimaModificacao,
        Field.Store.YES, Field.Index.NOT_ANALYZED));
    documento.add(new Field("Caminho", arquivo.getAbsolutePath(),
        Field.Store.YES, Field.Index.NOT_ANALYZED));
    documento.add(new Field("NomeComercial", nomeComercial, Field.Store.YES,
            Field.Index.ANALYZED));
    documento.add(new Field("Fabricante", fabricante, Field.Store.YES,
            Field.Index.ANALYZED));
    documento.add(new Field("Texto", textoExtraido, Field.Store.YES,
        Field.Index.ANALYZED));
    try {
      //{11}
      getWriter().addDocument(documento);
    } catch (IOException e) {
      logger.error(e);
    }
  }


  public IndexWriter getWriter() {
    return writer;
  }
}
