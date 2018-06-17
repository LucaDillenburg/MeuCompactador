package codigo;

import java.util.Arrays;

public class Codigo
{
	protected String cod = "";
	
	public Codigo()
	{}
	
	public Codigo(String c)
	{
		this.cod = c;
	}
	
	public Codigo(byte[] vCodigo)
	{
		for(int i = 0; i < vCodigo.length; i++)
		{
			char c = (char) vCodigo[i];
			cod += cod;
		}
	}

	public void mais(int i)
	{
		this.cod += i;
	}
	
	public void mais(byte[] b)
	{
		for (int i = 0; i < b.length; i++)
			this.cod += b[i];
	}

	public byte getByte()
	{
		return (byte)Integer.parseInt(this.cod);
	}
	/*public byte[] getByte()
	{
		if(this.cod.equals(""))
			return null;
		
		String aux = this.cod;
		byte[] ret = new byte[aux.length()/8 + 1];
		
		for (int i = 0;aux.length() > 0; i++)
		{
			if(aux.length() > 7)
			{
				ret[i] = (byte) Integer.parseInt(aux.substring(0, 8));
				aux = aux.substring(8);
			}
			else
			{
				ret[i] = (byte) Integer.parseInt(aux);
				aux = "";
			}
		}
		
		return ret;
	}*/
	
	public int getQtdBits()
	{
		return this.cod.length();
	}

	public void tirarUltimo()
	{
		this.cod = this.cod.substring(0, this.cod.length() - 1);
	}

	public Codigo(Codigo c)
	{
		this.cod = c.cod;
	}

	public Object clone()
	{
		Codigo ret = null;

		try
		{
			ret = new Codigo(this);
		}catch(Exception e)
		{}

		return ret;
	}
	
	public String getCodigo()
	{
		return this.cod;
	}
	
	public String toString()
	{
		return this.cod;
	}

	public int getInt() 
	{
		if(this.cod != "")
			return Integer.parseInt(this.cod);
		
		return 0;
	}
}