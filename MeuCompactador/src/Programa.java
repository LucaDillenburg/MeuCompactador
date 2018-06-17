import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import compactador.*;

public class Programa
{	
	public static void main(String[] args)
	{
		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
		Compactador compactador = new Compactador();
		
		for(;;)
		{
			try
			{
				System.out.println("Digite 'c' para compactar, 'd' para descompactar e 's' para sair...");	
				
				String resp = null;
				try {
					resp = teclado.readLine();
				} catch (IOException e)
				{}
				
				if(resp.equals("s") || resp.equals("S"))
					break;
				else
				if(resp.equals("c") || resp.equals("C"))
				{
					System.out.print("Digite o caminho do arquivo: ");
					String nomeArquivo = null;
					try
					{
						nomeArquivo = teclado.readLine();
					}catch(Exception e) {}
					
					String nvNome = compactador.compactar(nomeArquivo);
					
					System.out.println();
					System.out.println("Arquivo compactado com sucesso! Nome do arquivo compactado: " + nvNome);
					System.out.println();
				}else
				if(resp.equals("d") || resp.equals("D"))
				{
					System.out.print("Digite o caminho do arquivo: ");
					String nomeArquivo = null;
					try
					{
						nomeArquivo = teclado.readLine();
					}catch(Exception e) {}
					
					String nvNome = compactador.descompactar(nomeArquivo);
					
					System.out.println();
					System.out.println("Arquivo descompactado com sucesso! Nome do arquivo descompactado: " + nvNome);
					System.out.println();
				}else
				{
					System.err.println("Digite uma das opcoes acima...");
				}
			}catch(Exception e)
			{
				System.err.println(e.getMessage());
			}		
		}
	}
}