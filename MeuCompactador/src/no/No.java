package no;

import cloneDeX.*;

public class No<X>
{
	protected No ant = null;
	protected X  info;
	protected No esq = null;
	protected No dir = null;

	// construtores
	public No(X x) throws Exception
	{
		this(null, null, x, null);
	}

	public No(X x, No d) throws Exception
	{
		this(null, null, x, d);
	}

	public No(No e, X x, No d) throws Exception
	{
		this(null, e, x, d);

		if(e != null)
			e.ant = this;
		if(d != null)
			d.ant = this;
	}

	public No(No a, No e, X x, No d) throws Exception
	{
		this.setInfo(x);
		this.ant = a;
		this.esq = e;
		this.dir = d;
	}


	// getters and setters
	public void setAnt(No no)
	{
		this.ant = no;
	}

	public void setInfo(X x) throws Exception
	{
		if(x == null)
			throw new Exception("Informacao nula!");
		this.info = (X)CloneDeX.clone(x);
	}

	public void setEsq(No<X> no)
	{
		this.esq = no;
	}

	public void setDir(No<X> no)
	{
		this.dir = no;
	}

	public No getAnt()
	{
		return this.ant;
	}

	public X getInfo()
	{
		return this.info;
	}

	public No<X> getEsq()
	{
		return this.esq;
	}

	public No<X> getDir()
	{
		return this.dir;
	}

    public String toString()
    {
        return "{" + this.esq + 
        	"(" + this.info + ") " + this.dir;
    }

    public int hashCode()
    {
        int ret = 3;

        if (this.esq != null)
            ret = ret*7 + this.esq.hashCode();

        ret = ret*7 + this.info.hashCode();

        if (this.dir != null)
            ret = ret*7 + this.dir.hashCode();

        return ret;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (obj == this)
            return true;

        if (obj.getClass() != this.getClass())
            return false;

        No<X> no = (No<X>)obj;

        if (!this.info.equals(no.info))
        	return false;
        
        if (this.esq != null)
        {
            if(!this.esq.equals(no.esq))
                return false;
        }else
        	if (no.esq != null)
        		return false;

        if (this.dir != null) {
            if (!this.dir.equals(no.dir))
                return false;
        } else
             if (no.dir != null)
                return false;

        return true;
    }

    public No(No<X> modelo) throws Exception
    {
    	if (modelo == null)
    		throw new Exception("Modelo nulo!");

    	this.info = modelo.info;
    	
    	if (modelo.dir != null)
    		this.dir = (No<X>)modelo.dir.clone();
    	else
    		this.dir = null;

    	if (modelo.esq != null)
    		this.esq = (No<X>)modelo.esq.clone();
    	else
    		this.esq = null;
    }

    public Object clone()
    {
    	No<X> ret = null;

        try {
            ret = new No<X>(this);
        } 
        catch (Exception e) {}

        return ret;
    }
}
