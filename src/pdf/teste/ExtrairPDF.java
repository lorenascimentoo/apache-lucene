package pdf.teste;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class ExtrairPDF {
	private String nomeComercial;
	private String principioAtivo;
	private String fabricante;
	private String indicacoes;
	private String contraIndicacoes;
	private String reacoesAdversas;
	private String textoExtraido;
	
	public ExtrairPDF(File arquivo) throws IOException{
		
		try (PDDocument document = PDDocument.load(arquivo)){
			if(!document.isEncrypted()) {
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);
				
				//Cria o leitor e faz a leitura do TEXTO para uma String;
				PDFTextStripper tStripper = new PDFTextStripper();
				String pdfFileInText = tStripper.getText(document);
				this.textoExtraido = pdfFileInText;
				
				
				List<String> dados = getBulasInfo(pdfFileInText);
			   	
				this.nomeComercial = dados.get(0);
				this.principioAtivo = dados.get(1);
				this.fabricante = dados.get(2);
								
				List<String> texto = getIndicacoes(pdfFileInText);
				this.indicacoes = texto.get(0);
				this.contraIndicacoes = texto.get(1);
				this.reacoesAdversas = texto.get(2);
				
				System.out.println("NOME COMERCIAL: " +nomeComercial);
				System.out.println("PRINCIPIO ATIVO: " +principioAtivo);
				System.out.println("FABRICANTE: " +fabricante);
				System.out.println(""+indicacoes);
				System.out.println("" +contraIndicacoes);
				System.out.println(""+reacoesAdversas);
				System.out.println("-------------------------------------");
				
			}
		}
	}
	
	public static List<String> getBulasInfo(String pdfFileInText){
		//Faz a varredura linha a linha do texto extraído
		String fabricante=null;
		Scanner scn = null;					
		scn = new Scanner(pdfFileInText);
		List<String> line = new ArrayList<String>();
			while (scn.hasNextLine()){
				String analisar = scn.nextLine().trim();
				if(analisar.length()>0 ) {
					if (analisar.contains("Ltda") || analisar.contains("LTDA")) {
						fabricante = analisar;
					} else {
						line.add(analisar);
					}					
				}
			}
		
		
		List<String> dados = new ArrayList<>();
		dados.add(line.get(0));
		dados.add(line.get(1));
		dados.add(fabricante);
		
		scn.close();
		return dados;	
	}
	
public static List<String> getIndicacoes(String pdfFileInText) {
		
		String p = pdfFileInText.replaceAll("\r\n", "");
		p = p.replaceAll("  ", "\n").replace("?", ":");
		//Leitura linha a linha que gera o arquivo sem os espaços em branco
		Scanner scn = null;					
		scn = new Scanner(p);
		
		List<String> textoExtraido = new ArrayList<String>();
		while (scn.hasNextLine()){
			//retira as linhas em branco
			String analisar = scn.nextLine().trim();
			
			//não permite linhas em branco no documento
			if(analisar.length()>0) {
				textoExtraido.add(analisar);
			}
		}
		scn.close();
		
		List<String> inf = new ArrayList<String>();
		List<String> indicacao = new ArrayList<String>();
		List<String> contraIndicacao = new ArrayList<String>();
		List<String> reacaoAdversa = new ArrayList<String>();
		for(int i=0;i<textoExtraido.size();i++) {
			String line = textoExtraido.get(i);
			if(line.contains("PARA")) {
				int j = i;
				while(!textoExtraido.get(j).contains("NÃO")) {
					indicacao.add(textoExtraido.get(j));
					j++;
				}
			} else if(line.contains("NÃO")){
				int j = i;
				while(!textoExtraido.get(j).contains("SABER")) {
					contraIndicacao.add(textoExtraido.get(j));
					j++;
					}
				
			}else if(line.contains("MALES")){
				int j = i;
				while(!textoExtraido.get(j).contains("QUANTIDADE")) {
					reacaoAdversa.add(textoExtraido.get(j));
					j++;
					}
			}

		}
		
		String dadoInd = getString(indicacao); 
		String dadoContraInd = getString(contraIndicacao);
		String dadoReacAdv = getString(reacaoAdversa);
		
		
		inf.add(dadoInd);
		inf.add(dadoContraInd);
		inf.add(dadoReacAdv);
		
		
		return inf;
	}
	
	public static String getString(List<String> lista) {
		String dado = lista.toString();
		dado = dado.replaceAll("\\d.", "");
		return dado;
	}
	
	public String getNomeComercial() {
		return nomeComercial;
	}

	public String getPrincipioAtivo() {
		return principioAtivo;
	}

	public String getFabricante() {
		return fabricante;
	}

	public String getIndicacoes() {
		return indicacoes;
	}

	public String getContraIndicacoes() {
		return contraIndicacoes;
	}

	public String getReacoesAdversas() {
		return reacoesAdversas;
	}

	public String getTextoExtraido() {
		return textoExtraido;
	}

}
