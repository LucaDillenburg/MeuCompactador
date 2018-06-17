package compactador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import codigo.*;
import listaDesordenada.*;
import meuRandomAccessFile.MeuRandomAccessFile;
import no.*;

public class Compactador
{
	public class FreqSimb
	{
	   protected int freq;
	   protected int simb;

		public FreqSimb(int f)
	   	{
	      	this(f, -1);
	   	}

	   	public FreqSimb(int f, int s)
	   	{
	    	this.freq = f;
	      	this.simb = s;
	   	}

	   	public int getSimb()
	   	{
	    	return this.simb;
	   	}

	   	public int getFreq()
	   	{
	    	return this.freq;
	   	}
	   	
	   	public String toString()
	   	{
	   		return "Frequencia: " + this.getFreq() + " Simbolo: " + this.getSimb();
	   	}
	}

	public static final String EXTENSAO_MEU_COMPACTADOR = "paodebatata";
	
	protected Codigo[] codigos;
	protected File arq;
	protected int qtdSimb;
	protected int[] vetFreq;
	protected No<FreqSimb> raiz;
	
	//descompactar
	protected String extensaoAntiga;
	protected int qtdLixo;

	//construtuor
	public Compactador()
	{
		this.resetarInformacoes();
	}

	protected void resetarInformacoes()
	{
		this.codigos = null;
		this.arq = null;
		this.qtdSimb = 0;
		this.vetFreq = null;
		this.raiz = null;
		this.extensaoAntiga = null;
		this.qtdLixo = -1;
	}
	
	
	//compactar
	public String compactar(File arquivo) throws Exception
	{
		if (arquivo == null)
			throw new FileNotFoundException("Arquivo nulo");
		
		this.arq = arquivo;
		return this.compactar();
	}
	
	public String compactar(String nomeArquivo) throws Exception
	{
		if(!new File(nomeArquivo).exists())
			throw new FileNotFoundException("Arquivo inexistente!");

		this.arq = new File(nomeArquivo);
		return this.compactar();
	}
	
	protected String compactar() throws Exception
	{		
		//1. montar vetor com a frequencia dos bytes usados
		this.montaVetFreq();
		//2. montar vetor de No ordenado a partir do vetor com a frequencia
		// e
		//3. montar uma arvore a partir do vetor de no
		this.raiz = montarArvore();
		
		//4. colocar novos codigos na arvore
		this.codigos = new Codigo[256];
		for(int i=0; i < this.codigos.length; i++)
			this.codigos[i] = null;
		this.colocarNvsCodigosNaArv(this.raiz, new Codigo());

		//5. escrever novo arquivo com novo
		return this.escreverArqCompactado();
	}
	
	protected String escreverArqCompactado()
	{
		try
		{
			String extensao = this.getFileExtension();
			File novoArquivo = this.criarNovoArquivo(this.getFileDirectory(), EXTENSAO_MEU_COMPACTADOR);
			
			MeuRandomAccessFile escritor = new MeuRandomAccessFile(novoArquivo, "rw");
			MeuRandomAccessFile leitor = new MeuRandomAccessFile(this.arq, "r");
			
			//colocar CABECALHO: length extensao, extensao, posLixo, qtd posicoes usadas vetor No,
			 //vetor de No
			
			//length extensao + extensao
			escritor.write(extensao.length());
			for (int i = 0; i < extensao.length(); i++)
				escritor.write((int)extensao.charAt(i));
			
			//coloca 0 em qtdLixo e coloca um ponteiro para voltar depois
			 //(pois soh depois de escrever o arquivo inteiro vai saber quantos bits sobraram)
			long posLixo = escritor.getFilePointer();
			escritor.write(0);
			
			//escrever numero de posicoes do vetor
			escritor.writeInt(this.qtdSimb);
			//escrever vetor em si
			for (int i = 0; i < this.vetFreq.length; i++) {
				if (this.vetFreq[i] > 0)
				{
					escritor.write(i);
					escritor.writeInt(this.vetFreq[i]);
				}
			}
			
			//escrever novos codigos do arquivo
			for (int i = 0; i < leitor.length(); i++)
				escritor.escreverCodigo(this.codigos[leitor.read()]);
			
			//preencher lixo
			int qtdLixo = escritor.getQtdLixo();
			escritor.preencherLixo();
			
			//volta no lugar onde ficaria o qtdLixo e escreve
			escritor.seek(posLixo);
			escritor.write(qtdLixo);
			
			escritor.close();
			leitor.close();
			
			return novoArquivo.getName();
		}catch(Exception e)
		{
			return "";
		}
	}
	
	
	//descompactar
	public String descompactar(File arquivo) throws Exception
	{
		if (arquivo == null)
			throw new FileNotFoundException("Arquivo nulo");
		
		this.arq = arquivo;
		if(!EXTENSAO_MEU_COMPACTADOR.equals(this.getFileExtension()))
			throw new Exception("Esse arquivo nao foi compactado por esse Compactador!");
			
		return this.descompactar();
	}
	
	public String descompactar(String nomeArquivo) throws Exception
	{
		if(!new File(nomeArquivo).exists())
			throw new FileNotFoundException("Arquivo inexistente!");

		String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf(".")+1);
		if(!EXTENSAO_MEU_COMPACTADOR.equals(extensao))
			throw new Exception("Esse arquivo nao foi compactado por esse Compactador!");
			
		this.arq = new File(nomeArquivo);
		return this.descompactar();
	}
	
	protected String descompactar() throws Exception
	{		
		MeuRandomAccessFile leitor = new MeuRandomAccessFile(this.arq, "r");
		
		//1. Ler cabecalho (conseguir extensao, conseguir posLixo,
		 //conseguir vetor com a freq dos bytes usados)
		this.extrairInfoCabecalho(leitor);
		
		//2. montar vetor de No ordenado a partir do vetor com a frequencia
		// e
		//3. montar uma arvore a partir do vetor de no
		this.raiz = montarArvore();
		
		//4. colocar novos codigos na arvore
		this.codigos = new Codigo[256];
		for(int i=0; i < this.codigos.length; i++)
			this.codigos[i] = null;
		this.colocarNvsCodigosNaArv(this.raiz, new Codigo());

		//5. escrever novo arquivo com novo
		return this.escreverArqDescompactado(leitor);
	}
	
	protected void extrairInfoCabecalho(MeuRandomAccessFile leitor)
	{
		try
		{
			//CABECALHO: length extensao, extensao, posLixo, qtd posicoes usadas vetor No,
			 //vetor de No
			
			//extensao
			int qtdExtensao = leitor.read();
			this.extensaoAntiga = "";
			for(int i = 0; i < qtdExtensao; i++)
			//cada char ocupa um byte
				this.extensaoAntiga += (char)leitor.read();
			
			//lixo
			this.qtdLixo = leitor.read();
			
			//vetor int (freq)
			this.qtdSimb = leitor.readInt();
			
			this.vetFreq = new int[256];		
			for(int i = 0; i < this.qtdSimb; i++)
			{
				int indice = leitor.read();
				int freq = leitor.readInt();
				
				this.vetFreq[indice] = freq;
			}
		}catch(Exception e)
		{
			System.err.println(e.getMessage() + e.getStackTrace());
		}
	}
	
	protected String escreverArqDescompactado(MeuRandomAccessFile leitor)
	{		
		try
		{
			File novoArquivo = this.criarNovoArquivo(this.getFileDirectory(), this.extensaoAntiga);
			
			MeuRandomAccessFile escritor = new MeuRandomAccessFile(novoArquivo, "rw");
			
			//escrever codigos antigos do arquivo
			No<FreqSimb> atual = this.raiz;
			for (int iBytes = 0; iBytes < leitor.length(); iBytes++)
			{				
				int byteAtual = leitor.read();
				
				int qtdBits = 8;
				if(iBytes == leitor.length()-1)
					qtdBits -= this.qtdLixo;
				
				for(int i = 0; i < qtdBits; i++)
				{
					int bit = getBitFromByte(byteAtual, 7-i);
					
					//se acabou a arvore
					if((bit == 1 && atual.getDir()==null) || (bit == 0 && atual.getEsq()==null))
					{
						escritor.write(((FreqSimb)(atual.getInfo())).getSimb());
						atual = this.raiz;
					}
					
					if(bit == 0)
						atual = atual.getEsq();
					else
						atual = atual.getDir();
				}
			}
			
			escritor.close();
			leitor.close();
			
			return novoArquivo.getName();
		}catch(Exception e)
		{
			return e.getMessage()+"";
		}
	}
	
	protected int getBit(int n, int k) {
		return (n >> k) & 1;
	}
	
	//metodos gerais	
	protected void colocarNvsCodigosNaArv(No<FreqSimb> r, Codigo c)
	{
			if(r != null)
			{
				int simb = r.getInfo().getSimb();
				if(simb >= 0)
					this.codigos[simb] = (Codigo)c.clone();
				else
				{
					c.mais(0);
					this.colocarNvsCodigosNaArv(r.getEsq(), c);
					c.tirarUltimo();
					c.mais(1);
					this.colocarNvsCodigosNaArv(r.getDir(), c);
					c.tirarUltimo();
				}
			}
	}
	
	protected No<FreqSimb> montarArvore()
	{
		//montar vetor de no ordenado
		No<FreqSimb>[] nos = new No[256];
		this.qtdSimb = 0;
		
		for(int i=0; i < this.vetFreq.length; i++)
			if(this.vetFreq[i] != 0)
			{
					try
					{
						nos[this.qtdSimb] = new No(new FreqSimb(this.vetFreq[i], i));
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					
					this.qtdSimb++;
					
					//jah vai ordenando o ultimo que acabou de adicionar, 
					//assim nao precisa ordenar tudo depois
					mudarPosNos(nos, this.qtdSimb);
			}
		
		
		//somar os dois ultimos e ordenando
		for(int qtd = this.qtdSimb; qtd>1; qtd--)
		{
			mudarPosNos(nos, qtd);
			int freq = nos[qtd-2].getInfo().getFreq() + nos[qtd-1].getInfo().getFreq();
			
			No<FreqSimb> aux = null;
			try
			{
				aux = new No<FreqSimb>(new FreqSimb(freq));
			}catch (Exception e){e.printStackTrace();}
			
			aux.setEsq(nos[qtd-2]);
			aux.setDir(nos[qtd-1]);
			nos[qtd-2] = aux;
			nos[qtd-1] = null;
		}
		
		//unico no: raiz
		return nos[0];
	}
	
	protected void mudarPosNos(No<FreqSimb>[] nos, int qtd)
	{
		for(int i=qtd-1; i > 0; i--)
		{
				if(((FreqSimb) nos[i].getInfo()).getFreq() <= ((FreqSimb) nos[i-1].getInfo()).getFreq())
					break;
				else
				{
					No<FreqSimb> aux = nos[i];
					nos[i] = nos[i-1];
					nos[i-1] = aux;
				}
		}
		//"arvs" saira diferente (como passagem por referencia)
	}
	
	protected void montaVetFreq()
	{
		this.vetFreq = new int[256];
		
		try
		{
			MeuRandomAccessFile leitor = new MeuRandomAccessFile(this.arq, "r");
			for(int i = 0; i<leitor.length(); i++)
				this.vetFreq[leitor.read()]++;
		}catch(Exception e)
		{}
	}
	
	protected int getBitFromByte(int byteAtual, int pos)
	{
		return ((byteAtual >> pos) & 0x01);
	}
	
	
	//sobre arquivo
	protected File criarNovoArquivo(String diretorio, String extensao)
	{
		//criar o arquivo compactado soh com a extensao
		File arquivo = new File(diretorio + "." + extensao);
		
		int n = 1;
		while (arquivo.exists()) {
			//se jah existe esse arquivo criar outro com " (1)" na frente
			arquivo = new File(diretorio + " (" + n + ")." + extensao);
			n++;
		}

		return arquivo;
	}
	
	private String getFileExtension()
	{
        try
        {
        	String nomeArq = this.arq.getName();
            return nomeArq.substring(nomeArq.lastIndexOf(".")+1);
        } 
        catch (Exception e) 
        {
        	return "";
        }
    }

	protected String getFileDirectory()
	{
		try
		{
			String dir = this.arq.getCanonicalPath();
			return dir.substring(0, dir.lastIndexOf('.'));
		}
		catch (Exception e) {
			return "";
		}
	}
}