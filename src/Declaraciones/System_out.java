package Declaraciones;

import Expresiones.Expresion;

public class System_out extends Declaraciones
{
	
	private Expresion E1;
	
	public System_out(Expresion E1)
	{
		this.E1 = E1;
	}
	
	public String getStatement()
	{
		return "System.out.prinln("+E1.getExpression()+");";
	}
}
