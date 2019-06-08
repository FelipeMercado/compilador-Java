package Declaraciones;

import Declaraciones.Declaraciones;

public class Corchetes extends Declaraciones
{
	
	private Declaraciones S1;
	
	public Corchetes(Declaraciones S1)
	{
		this.S1 = S1;
	}

	public String getStatement() 
	{
		return "{"+S1.getStatement()+"}";
	}
	
}
