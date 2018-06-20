package compactador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import compactadorArquivo.*;

public class Compactador
{
	public static final String STR_FINAL_ARQ = "-[Compacted]";
	
	//compactar
	public static String compactar(File arquivo) throws Exception
	{
		if (arquivo == null)
			throw new FileNotFoundException("Arquivo nulo");
		
		return Compactador.compactarGeral(arquivo, null);
	}
	
	public static String compactar(String nomeArquivo) throws Exception
	{
		if(!new File(nomeArquivo).exists())
			throw new FileNotFoundException("Arquivo inexistente!");

		return Compactador.compactarGeral(new File(nomeArquivo), null);
	}
	
	public static String compactar(File arquivo, String nvDiretorio) throws Exception
	{
		if (arquivo == null)
			throw new FileNotFoundException("Arquivo nulo");
		if(nvDiretorio == null || nvDiretorio == "")
			throw new Exception("Novo diretorio nulo!");
		
		return Compactador.compactarGeral(arquivo, nvDiretorio);
	}
	
	public static String compactar(String nomeArquivo, String nvDiretorio) throws Exception
	{
		if(!new File(nomeArquivo).exists())
			throw new FileNotFoundException("Arquivo inexistente!");
		if(nvDiretorio == null || nvDiretorio == "")
			throw new Exception("Novo diretorio nulo!");

		return Compactador.compactarGeral(new File(nomeArquivo), nvDiretorio);
	}
	
	protected static String compactarGeral(File arquivo, String nvDiretorio) throws Exception
	{
		if(!arquivo.isDirectory())
		{
			CompactadorArquivo compactadorArq = new CompactadorArquivo();
			
			if(nvDiretorio == null || nvDiretorio == "")
				return compactadorArq.compactar(arquivo);
			return compactadorArq.compactar(arquivo, nvDiretorio);
		}else
		{
			String diretorio;
			if(nvDiretorio == null || nvDiretorio == "")
				diretorio = arquivo.getCanonicalPath() + Compactador.STR_FINAL_ARQ;
			else
				diretorio = nvDiretorio;
			
			//para nao sobreescrever nenhuma pasta
			String nomeArq = diretorio;
			int n = 1;
			while (new File(nomeArq).exists())
			{
				//se jah existe esse arquivo criar outro com " (1)" na frente
				nomeArq = diretorio + " (" + n + ")";
				n++;
			}
			
			Compactador.compactarAux(arquivo, nomeArq, true);
			return nomeArq;
		}
	}
	
	protected static void compactarAux(File arquivo, String nomeArq, boolean primeiraVez)
	{
		if(!arquivo.isDirectory())
		{
			CompactadorArquivo compactadorArq = new CompactadorArquivo();

			try
			{
				if(nomeArq == null || nomeArq == "")
					compactadorArq.compactar(arquivo);
				else
					compactadorArq.compactar(arquivo, nomeArq);
			}catch (Exception e) {e.printStackTrace();}
		}else
		{
			if(!primeiraVez)
				nomeArq += Compactador.STR_FINAL_ARQ;
			
			for (final File arq : arquivo.listFiles())
			{
				try
				{
					boolean worked = new File(nomeArq).mkdirs();
					String path = arq.getCanonicalPath();
					int indexUltimoPonto = path.lastIndexOf(".");
					String nomeArquivo;
					if(indexUltimoPonto < 0)
						nomeArquivo = nomeArq + path.substring(path.lastIndexOf("\\"));
					else
						nomeArquivo = nomeArq + path.substring(path.lastIndexOf("\\"), indexUltimoPonto);
					Compactador.compactarAux(arq, nomeArquivo, false);
				} catch (IOException e) {e.printStackTrace();}
		    }
		}
	}
	
	
	//descompactar
	public static String descompactar(File arquivo) throws Exception
	{
		if (arquivo == null)
			throw new FileNotFoundException("Arquivo nulo");
		
		return Compactador.descompactarGeral(arquivo, null);
	}
	
	public static String descompactar(String nomeArquivo) throws Exception
	{
		if(!new File(nomeArquivo).exists())
			throw new FileNotFoundException("Arquivo inexistente!");

		return Compactador.descompactarGeral(new File(nomeArquivo), null);
	}
	
	public static String descompactar(File arquivo, String nvDiretorio) throws Exception
	{
		if (arquivo == null)
			throw new FileNotFoundException("Arquivo nulo");
		if(nvDiretorio == null || nvDiretorio == "")
			throw new Exception("Novo diretorio nulo!");
		
		return Compactador.descompactarGeral(arquivo, nvDiretorio);
	}
	
	public static String descompactar(String nomeArquivo, String nvDiretorio) throws Exception
	{
		if(!new File(nomeArquivo).exists())
			throw new FileNotFoundException("Arquivo inexistente!");
		if(nvDiretorio == null || nvDiretorio == "")
			throw new Exception("Novo diretorio nulo!");

		return Compactador.descompactarGeral(new File(nomeArquivo), nvDiretorio);
	}
	
	protected static String descompactarGeral(File arquivo, String nvDiretorio) throws Exception
	{
		if(!arquivo.isDirectory())
		{
			CompactadorArquivo compactadorArq = new CompactadorArquivo();
			
			if(nvDiretorio == null || nvDiretorio == "")
				return compactadorArq.descompactar(arquivo);
			return compactadorArq.descompactar(arquivo, nvDiretorio);
		}else
		{
			String diretorio;
			if(nvDiretorio == null || nvDiretorio == "")
				diretorio = Compactador.diretorioNaoCompactado(arquivo.getCanonicalPath());
			else
				diretorio = nvDiretorio;
			
			//para nao sobreescrever nenhuma pasta
			String nomeArq = diretorio;
			int n = 1;
			while (new File(nomeArq).exists())
			{
				//se jah existe esse arquivo criar outro com " (1)" na frente
				nomeArq = diretorio + " (" + n + ")";
				n++;
			}
			
			Compactador.descompactarAux(arquivo, nomeArq, true);
			return nomeArq;
		}
	}
	
	protected static String diretorioNaoCompactado(String nvDiretorio) throws Exception
	{
		int indexOf = nvDiretorio.lastIndexOf(Compactador.STR_FINAL_ARQ);
		if(indexOf<0)
			throw new Exception("Esse arquivo n�o foi compactado!");
		return nvDiretorio.substring(0, indexOf);
	}
	
	protected static void descompactarAux(File arquivo, String nomeArq, boolean primeiraVez) throws Exception
	{
		if(!arquivo.isDirectory())
		{
			CompactadorArquivo compactadorArq = new CompactadorArquivo();

			try
			{
				compactadorArq.descompactar(arquivo, nomeArq);
			}catch (Exception e) {e.printStackTrace();}
		}else
		{
			if(!primeiraVez)
				nomeArq = Compactador.diretorioNaoCompactado(nomeArq);
			for (final File arq : arquivo.listFiles())
			{
				try
				{
					boolean worked = new File(nomeArq).mkdirs();
					String path = arq.getCanonicalPath();
					int indexUltimoPonto = path.lastIndexOf(".");
					String nomeArquivo;
					if(indexUltimoPonto < 0)
						nomeArquivo = nomeArq + path.substring(path.lastIndexOf("\\"));
					else
						nomeArquivo = nomeArq + path.substring(path.lastIndexOf("\\"), indexUltimoPonto);
					Compactador.descompactarAux(arq, nomeArquivo, false);
				} catch (IOException e) {e.printStackTrace();}
		    }
		}
	}
		
	//geral
	public static boolean estahCompactado(File arquivo)
	{
		if(arquivo == null)
			return false;
		
		if(!arquivo.isDirectory())
			return CompactadorArquivo.estahCompactado(arquivo);
		
		String diretorio = null;
		try
		{
			diretorio = arquivo.getCanonicalPath();
		} catch (IOException e) {}

		return diretorio.indexOf(STR_FINAL_ARQ) >= 0;
	}
}