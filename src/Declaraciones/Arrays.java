package Declaraciones;

import Expresiones.Expresion;
import Expresiones.Identificador;

public class Arrays extends Declaraciones
{
	
	private Identificador id;
	private Expresion E1;
	private Expresion E2;
	
	public Arrays(Identificador id, Expresion E1, Expresion E2)
	{
		this.id = id;
		this.E1 = E1;
		this.E2 = E2;
	}
	
	public String getStatement()
	{
		return id.getID()+"[ "+E1.getExpression()+" ]="+E2.getExpression()+";";
	}
	
}
