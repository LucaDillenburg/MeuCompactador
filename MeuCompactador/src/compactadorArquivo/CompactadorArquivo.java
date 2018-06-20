package compactadorArquivo;

import java.io.File;
import java.io.FileNotFoundException;
import codigo.*;
import meuRandomAccessFile.MeuRandomAccessFile;
import no.*;

public class CompactadorArquivo
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

	public static final String EXTENSAO_MEU_COMPACTADOR = "sup";
	
	protected Codigo[] codigos;
	protected File arq;
	protected int qtdSimb;
	protected int[] vetFreq;
	protected No<FreqSimb> raiz;
	
	protected String nomeFinal;
	
	//descompactar
	protected String extensaoAntiga;
	protected int qtdLixo;

	//construtuor
	public CompactadorArquivo()
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
		this.nomeFinal = null;
	}
	
	
	//compactar
	public String compactar(File arquivo, String nome) throws Exception
	{
		if (arquivo == null)
			throw new FileNotFoundException("Arquivo nulo");
		
		this.resetarInformacoes();
		this.arq = arquivo;
		this.nomeFinal = nome;
		
		return this.compactar();
	}
	
	public String compactar(File arquivo) throws Exception
	{
		if (arquivo == null)
			throw new FileNotFoundException("Arquivo nulo");
		
		this.resetarInformacoes();
		this.arq = arquivo;
		
		return this.compactar();
	}
	
	public String compactar(String nomeArquivo) throws Exception
	{
		if(!new File(nomeArquivo).exists())
			throw new FileNotFoundException("Arquivo inexistente!");
		
		this.resetarInformacoes();
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
		this.colocarNvsCodigosNaArv();

		//5. escrever novo arquivo com novo
		return this.escreverArqCompactado();
	}
	
	protected String escreverArqCompactado()
	{
		try
		{
			String extensao = CompactadorArquivo.getFileExtension(this.arq);
			
			File novoArquivo = null;
			if(this.nomeFinal == null)
				novoArquivo = this.criarNovoArquivo(this.getFileDirectory(), EXTENSAO_MEU_COMPACTADOR);
			else
				novoArquivo = new File(this.nomeFinal + "." + EXTENSAO_MEU_COMPACTADOR);
			
			MeuRandomAccessFile escritor = new MeuRandomAccessFile(novoArquivo, "rw");
			
			//colocar CABECALHO: length extensao, extensao, posLixo, qtd posicoes usadas vetor No,
			 //vetor de No
			
			//length extensao + extensao
			escritor.write(extensao.length());
			for (int i = 0; i < extensao.length(); i++)
				escritor.write((int)extensao.charAt(i));
			
			if(this.raiz != null)
			{
				MeuRandomAccessFile leitor = new MeuRandomAccessFile(this.arq, "r");
				
				//coloca 0 em qtdLixo e coloca um ponteiro para voltar depois
				 //(pois soh depois de escrever o arquivo inteiro vai saber quantos bits sobraram)
				long posLixo = escritor.getFilePointer();
				escritor.write(0);
				
				//escrever numero de posicoes do vetor
				escritor.writeInt(this.qtdSimb);
				//escrever vetor em si
				for (int i = 0; i < this.vetFreq.length; i++)
					if (this.vetFreq[i] > 0)
					{
						escritor.write(i);
						escritor.writeInt(this.vetFreq[i]);
					}
				
				//escrever novos codigos do arquivo
				for (int i = 0; i < leitor.length(); i++)
				{
					int ind = leitor.read();
					escritor.escreverCodigo(this.codigos[ind]);
				}
				
				//preencher lixo
				int qtdLixo = escritor.getQtdLixo();
				escritor.preencherLixo();
				
				//volta no lugar onde ficaria o qtdLixo e escreve
				escritor.seek(posLixo);
				escritor.write(qtdLixo);
				
				leitor.close();
			}
			escritor.close();
			
			return novoArquivo.getAbsolutePath();
		}catch(Exception e)
		{
			return "";
		}
	}
	
	
	//descompactar
	public String descompactar(File arquivo, String nome) throws Exception
	{
		if (arquivo == null)
			throw new FileNotFoundException("Arquivo nulo");
		
		this.resetarInformacoes();
		this.arq = arquivo;
		if(!EXTENSAO_MEU_COMPACTADOR.equals(CompactadorArquivo.getFileExtension(this.arq)))
			throw new Exception("Esse arquivo nao foi compactado por esse Compactador!");
		this.nomeFinal = nome;
		
		return this.descompactar();
	}
	
	public String descompactar(File arquivo) throws Exception
	{
		if (arquivo == null)
			throw new FileNotFoundException("Arquivo nulo");
		
		this.resetarInformacoes();
		this.arq = arquivo;
		if(!EXTENSAO_MEU_COMPACTADOR.equals(CompactadorArquivo.getFileExtension(this.arq)))
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
		
		this.resetarInformacoes();
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
		this.colocarNvsCodigosNaArv();

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
			
			if(this.qtdLixo >= 0)
			{
				//vetor int (freq)
				this.qtdSimb = leitor.readInt();
				
				this.vetFreq = new int[256];		
				for(int i = 0; i < this.qtdSimb; i++)
				{
					int indice = leitor.read();
					int freq = leitor.readInt();
					
					this.vetFreq[indice] = freq;
				}
			}else
				this.vetFreq = new int[256];
		}catch(Exception e)
		{
			System.err.println(e.getMessage() + e.getStackTrace());
		}
	}
	
	protected String escreverArqDescompactado(MeuRandomAccessFile leitor)
	{		
		try
		{
			File novoArquivo = null;
			if(this.nomeFinal == null)
				novoArquivo = this.criarNovoArquivo(this.getFileDirectory(), this.extensaoAntiga);
			else
				novoArquivo = new File(this.nomeFinal + "." + this.extensaoAntiga);
			
			MeuRandomAccessFile escritor = new MeuRandomAccessFile(novoArquivo, "rw");
			if(raiz != null)
			{
				if(this.raiz.getEsq() == null && this.raiz.getDir() == null)
				{
					for (long iBytes = leitor.getFilePointer(); iBytes < leitor.length(); iBytes++)
					{
						int qtdBits = 8;
						if(iBytes == leitor.length()-1)
							qtdBits -= this.qtdLixo;
						
						for(int i = 0; i < qtdBits; i++)
						{
							escritor.write(this.raiz.getInfo().getSimb());
						}
					}
				}else
				{
					//escrever codigos antigos do arquivo
					No<FreqSimb> atual = this.raiz;
					for (long iBytes = leitor.getFilePointer(); iBytes < leitor.length(); iBytes++)
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
						
							if(iBytes == leitor.length() && i == qtdBits-1)
								//se acabou a arvore
								if((bit == 1 && atual.getDir()==null) || (bit == 0 && atual.getEsq()==null))
								{
									escritor.write(((FreqSimb)(atual.getInfo())).getSimb());
									atual = this.raiz;
								}
						}
					}
					
					escritor.write(((FreqSimb)(atual.getInfo())).getSimb());
				}
			}
			escritor.close();
			leitor.close();
			
			return novoArquivo.getAbsolutePath();
		}catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	
	//metodos gerais
	protected void colocarNvsCodigosNaArv()
	{
		this.codigos = new Codigo[256];
		
		for(int i=0; i < this.codigos.length; i++)
			this.codigos[i] = null;
		
		if(this.raiz != null)
		{
			if(this.raiz.getEsq()==null && this.raiz.getDir()==null && this.raiz.getInfo().getSimb() >= 0)
				this.codigos[this.raiz.getInfo().getSimb()] = new Codigo("0");
			else
				this.colocarNvsCodigosNaArv(this.raiz, new Codigo());
		}
	}
	
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
						nos[this.qtdSimb] = new No<FreqSimb>(new FreqSimb(this.vetFreq[i], i));
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
		
		//para nao sobreescrever nenhum arquivo
		int n = 1;
		while (arquivo.exists())
		{
			//se jah existe esse arquivo criar outro com " (1)" na frente
			arquivo = new File(diretorio + " (" + n + ")." + extensao);
			n++;
		}

		return arquivo;
	}
	
	protected static String getFileExtension(File file) throws Exception
	{		
        try
        {
        	String nomeArq = file.getName();
            return nomeArq.substring(nomeArq.lastIndexOf(".")+1);
        } 
        catch (Exception e) 
        {
        	throw new Exception("Error: coudn't find file extension!");
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

	public static boolean estahCompactado(File arquivo)
	{
		try
		{
			return (CompactadorArquivo.EXTENSAO_MEU_COMPACTADOR.equals(CompactadorArquivo.getFileExtension(arquivo)));
		}catch(Exception e)
		{
			return false;
		}
	}
}