
import java.awt.Color;
import java.util.*;
 
import Errores.Excepcion_de_Analizador;
import Errores.Excepciones_Semanticas;
import Expresiones.*;
import Goal.*;
import Metodos.*;
import Declaraciones.*;
import Declaraciones.Arrays;


public class Parser 
{

	final int EOF = 0,LLAVE_IZQ = 5,LLAVE_DER = 6,PAREN_IZQ = 7,PAREN_DER = 8,CORCHETE_IZQ = 9,CORCHETE_DER = 10,
	PUNTOYCOMA = 11,IGUAL = 12,AND   = 13,MENOSQUE	= 14,MAS   = 15,MENOS = 16,MULT = 17,PUNTO = 18,COMA = 19,
	NEG = 20,UNDER = 21,DIV = 22,CLASS = 23,PUBLIC = 24,STATIC = 25,VOID = 26,MAIN = 27,STRING = 28,EXTENDS	= 29,
	RETURN = 30,INT = 31,BOOLEAN = 32,IF = 33,ELSE = 34,WHILE = 35,SYS = 36,LENGTH = 37,TRUE = 38,FALSE  = 39,
	THIS = 40,NEW = 41,ID = 42,INTEGER = 43;
	
	Vector<Error> Errores = new Vector<Error>(1,1);
	
	Vector<TablaDeSimbolos> TablaSim = new Vector<TablaDeSimbolos>(1,1);
	Vector<Identificador> Faltantes = new Vector<Identificador>(1,1);
	
	int TokenActual;
	Tokens token;
	
	String Resultado;
	
	Escaner Scanner;
	boolean success = true;
	
	int type1;
	int type2;
	
	public Parser(String Codigo)
	{
		Scanner = new Escaner(Codigo);
		TokenActual = Siguiente();
		Goal();
	}
	
	public void Goal()
	{
		
		@SuppressWarnings("unused")
		MainClass M1;
		Declaracion_de_Clase CD;
		Vector<Declaracion_de_Clase> ListCD = new Vector<Declaracion_de_Clase>(1,1);
		
		try{
			M1 = MainC();
		}catch(Excepcion_de_Analizador e)
		{
			Errores.add(new Error(Errores.size()+1, "Error Sintactico", e.getLocalizedMessage()));
			success = false;
			M1 = null;
			Trataerror(LLAVE_DER);
		}
		
		while(TokenActual!=EOF)
		{
			try{
				CD = ClassDecl();
			}catch(Excepcion_de_Analizador e)
			{
				Errores.add(new Error(Errores.size()+1, "Error Sintactico", e.getLocalizedMessage()));
				success = false;
				CD = null;
				Trataerror(LLAVE_DER);
			}
			if(CD!=null)
				ListCD.add(CD);
		}
		
		for(int i=0; i<Faltantes.size();i++)
			ChecaID(Faltantes.get(i));
		
		if(success)
		{
			Resultado = "Compilacion Correcta\nTabla de Simbolos...\n";
			
			for(int i=0; i<TablaSim.size(); i++)
				Resultado +=TablaSim.get(i).getId().getID()+"\t"+TablaSim.get(i).getType()+"\n";
		}
		else{
			Resultado = "Compilacion Erronea\n";
			Resultado+= MensajeError();
		}
		
	}
	
	public MainClass MainC() throws Excepcion_de_Analizador
	{
		Identificador id1;
		Identificador id2;
		Declaraciones S1;
		
		Comprueba(CLASS);
		id1 = new Identificador(token.token);
		Comprueba(ID);
		TablaSim.add(new TablaDeSimbolos(id1, "class"));
		Comprueba(LLAVE_IZQ);
		Comprueba(PUBLIC);
		Comprueba(STATIC);
		Comprueba(VOID);
		Comprueba(MAIN);
		Comprueba(PAREN_IZQ);
		Comprueba(STRING);
		Comprueba(CORCHETE_IZQ);
		Comprueba(CORCHETE_DER);
		id2 = new Identificador(token.token);
		Comprueba(ID);
		Comprueba(PAREN_DER);
		Comprueba(LLAVE_IZQ);
		S1 = Stm();
		Comprueba(LLAVE_DER);
		Comprueba(LLAVE_DER);
		
		return new MainClass(id1,id2,S1);
	}
	
	public Declaracion_de_Clase ClassDecl() throws Excepcion_de_Analizador
	{
		Declaracion_de_Clase CD;
		
		Identificador id1;
		Identificador id2 = null;
		Declaracion_de_Variable VD; 
		Declaracion_de_Metodos MD;
		
		Comprueba(CLASS);
		id1 = new Identificador(token.token);
		Comprueba(ID);
		TablaSim.add(new TablaDeSimbolos(id1, "class"));
		if(TokenActual==EXTENDS)
		{
			Comprueba(EXTENDS);
			id2 = new Identificador(token.token);
			Comprueba(ID);
			Faltantes.add(id2);
		}
		Comprueba(LLAVE_IZQ);
		CD = new Declaracion_de_Clase(id1, id2);
		
		while(TokenActual == INT || TokenActual == BOOLEAN || TokenActual == ID)
		{
			try{
				VD = Declaration();
			}catch(Excepcion_de_Analizador e){
				Errores.add(new Error(Errores.size()+1, "Error Sintactico", e.getLocalizedMessage()));
				success = false;
				VD = null;
				Trataerror(PUNTOYCOMA);
			}
			if(VD!=null)
			{
				CD.newVD(VD);
				try{
					Declarada(VD.getID());
					TablaSim.add(new TablaDeSimbolos(VD.getID(), VD.getType().getType()));
				}
				catch(Excepciones_Semanticas e)
				{
					Errores.add(new Error(Errores.size()+1, "Error Semantico", e.getLocalizedMessage()));
					success = false;
				}
			}
		}
		
		while(TokenActual == PUBLIC)
		{
			try{
				MD = Method();
			}catch(Excepcion_de_Analizador e)
			{
				Errores.add(new Error(Errores.size()+1, "Error Sintactico", e.getLocalizedMessage()));
				success = false;
				MD = null;
				Trataerror(LLAVE_DER);
			}
			if(MD!=null)
				CD.newMD(MD);
		}
		
		Comprueba(LLAVE_DER);
		return CD;		
	}
	
	public Declaracion_de_Variable Declaration() throws Excepcion_de_Analizador
	{
		Tipo type;
		Identificador id;
		
		type = Tipo();
		id = new Identificador(token.token);
		Comprueba(ID);
		Comprueba(PUNTOYCOMA);
		
		return new Declaracion_de_Variable(type, id);
	}
	
	public Tipo Tipo()throws Excepcion_de_Analizador
	{
		String tmp = token.token;
		
		switch(TokenActual)
		{
			case INT:
				Comprueba(INT);
				if(TokenActual==CORCHETE_IZQ)
				{
					tmp+=token.token;
					Comprueba(CORCHETE_IZQ);
					tmp+=token.token;
					Comprueba(CORCHETE_DER);
				}
				return new Tipo(tmp);
			case BOOLEAN:
				Comprueba(BOOLEAN);
				return new Tipo(tmp);
			case ID:
				Comprueba(ID);
				return new Tipo(tmp);
		}
		throw new Excepcion_de_Analizador(token.getToken(), "El compilador esperaba: int | int[] | boolean | id");
	}
	
	public Declaracion_de_Metodos Method()throws Excepcion_de_Analizador
	{
		Tipo type;
		Identificador id;
		Tipo ListTypes;
		Identificador ListID;
		Declaracion_de_Variable VD;
		Declaraciones S1;
		Expresion E1;
		
		Comprueba(PUBLIC);
		type = Tipo();
		id = new Identificador(token.token);
		Comprueba(ID);
		Faltantes.add(id);
		try{
			Declarada(id);
			TablaSim.add(new TablaDeSimbolos(id, type.getType()));
		}
		catch(Excepciones_Semanticas e)
		{
			Errores.add(new Error(Errores.size()+1, "Error Semantico", e.getLocalizedMessage()));
			success = false;
		}
		
		Comprueba(PAREN_IZQ);
		Declaracion_de_Metodos MD = new Declaracion_de_Metodos(type, id);
		while(TokenActual==INT || TokenActual==BOOLEAN || TokenActual==ID)
		{ 
			ListTypes =  Tipo();
			ListID = new Identificador(token.token);
			Comprueba(ID);
			Faltantes.add(id);
			MD.newTypeID(ListTypes, ListID);
			try{
				Declarada(ListID);
				TablaSim.add(new TablaDeSimbolos(ListID, ListTypes.getType()));
				TablaSim.get(TablaSim.size()-1).newParametro(new Declaracion_de_Variable(ListTypes, ListID));
			}
			catch(Excepciones_Semanticas e)
			{
				Errores.add(new Error(Errores.size()+1, "Error Semantico", e.getLocalizedMessage()));
				success = false;
			}
			if(TokenActual != COMA)
				break;
			
			Comprueba(COMA);
		}
		Comprueba(PAREN_DER);
		Comprueba(LLAVE_IZQ);
		
		while(TokenActual == INT || TokenActual == BOOLEAN)
		{ 
			try{
				VD = Declaration();
			}catch(Excepcion_de_Analizador e){
				Errores.add(new Error(Errores.size()+1, "Error Sintactico", e.getLocalizedMessage()));
				success = false;
				VD = null;
				Trataerror(PUNTOYCOMA);
			}
			if(VD!=null)
			{
				MD.newVD(VD);
				try{
					Declarada(VD.getID());
					TablaSim.add(new TablaDeSimbolos(VD.getID(), VD.getType().getType()));
				}
				catch(Excepciones_Semanticas e)
				{
					Errores.add(new Error(Errores.size()+1, "Error Semantico", e.getLocalizedMessage()));
					success = false;
				}
			}
		}
		
		while(TokenActual == LLAVE_IZQ || TokenActual == IF || TokenActual == WHILE || TokenActual == SYS || TokenActual == ID)
		{
			try{
				S1 = Stm();
			}catch(Excepcion_de_Analizador e)
			{
				Errores.add(new Error(Errores.size()+1, "Error Sintactico", e.getLocalizedMessage()));
				success = false;
				S1 = null;
				Trataerror(PUNTOYCOMA);
			}
			if(S1!=null)
				MD.newS1(S1);
		}
		
		Comprueba(RETURN);
		E1 = Exp();
		MD.setE1(E1);
		Comprueba(PUNTOYCOMA);
		Comprueba(LLAVE_DER);
		
		return MD;
	}
	
	public Declaraciones Stm()throws Excepcion_de_Analizador
	{
		Declaraciones S1;
		Declaraciones S2;
		Expresion E1;
		Expresion E2;
		Identificador id;
		
		switch(TokenActual)
		{
			case LLAVE_IZQ:
				Comprueba(LLAVE_IZQ);
				S1 = Stm();
				Comprueba(LLAVE_DER);
				return new Corchetes(S1);
			case IF:
				Comprueba(IF);
				Comprueba(PAREN_IZQ);
				E1 = Exp();
				Comprueba(PAREN_DER);
				S1 = Stm();
				Comprueba(ELSE);
				S2 = Stm();
				return new If(E1, S1, S2);
			case WHILE:
				Comprueba(WHILE);
				Comprueba(PAREN_IZQ);
				E1 = Exp();
				Comprueba(PAREN_DER);
				S1 = Stm();
				return new While(E1, S1);
			case SYS:
				Comprueba(SYS);
				Comprueba(PAREN_IZQ);
				E1 = Exp();
				Comprueba(PAREN_DER);
				Comprueba(PUNTOYCOMA);
				return new System_out(E1);
			case ID:
				id = new Identificador(token.token);
				Comprueba(ID);
				Faltantes.add(id);
				switch(TokenActual){
					case IGUAL:
						Comprueba(IGUAL);
						E1 = Exp();
						Comprueba(PUNTOYCOMA);
						return new Igual(id, E1);
					case CORCHETE_IZQ:
						Comprueba(CORCHETE_IZQ);
						E1 = Exp();
						Comprueba(CORCHETE_DER);
						Comprueba(IGUAL);
						E2 = Exp();
						Comprueba(PUNTOYCOMA);
						return new Arrays(id, E1, E2);
				}
				throw new Excepcion_de_Analizador(token.getToken(), "El compilador eperaba: = [");
		}
		throw new Excepcion_de_Analizador(token.getToken(), "El compilador eperaba: if | while | System.out.println | ID");
		
	}
	
	public Expresion Exp()throws Excepcion_de_Analizador
	{
		Expresion E1;
		Identificador id;
		Expresion2 E2;
		
		String tmp;
		
		switch(TokenActual)
		{
			case INTEGER:
				tmp = token.token;
				Comprueba(INTEGER);
				type1 = INT;
				E2 = Exp2();
				return new ExpInteger(tmp, E2);
			case TRUE:
				Comprueba(TRUE);
				type1 = BOOLEAN;
				E2 = Exp2();
				return new ExpPalabra("true", E2);
			case FALSE:
				Comprueba(FALSE);
				type1 = BOOLEAN;
				E2 = Exp2();
				return new ExpPalabra("false", E2);
			case ID:
				id = new Identificador(token.token);
				Comprueba(ID);
				Faltantes.add(id);
				E2 = Exp2();
				return new ExpID(id, E2);
			case THIS:
				Comprueba(THIS);
				type1 = THIS;
				E2 = Exp2();
				return new ExpPalabra("this", E2);
			case NEW:
				Comprueba(NEW);
				if(TokenActual == INT){
					Comprueba(INT);
					type1 = INT;
					Comprueba(CORCHETE_IZQ);
					E1 = Exp();
					Comprueba(CORCHETE_DER);
					E2 = Exp2();
					return new ExpNuevoInt(E1, E2);
				}
				else if(TokenActual == ID){
					id = new Identificador(token.token);
					Comprueba(ID);
					Faltantes.add(id);
					Comprueba(PAREN_IZQ);
					Comprueba(PAREN_DER);
					E2 = Exp2();
					return new ExpNuevaID(id, E2);
				}
				else
					throw new Excepcion_de_Analizador(token.getToken(), "El compilador eperaba: int | ID");
			case NEG:
				Comprueba(NEG);
				type1 = NEG;
				E1 = Exp();
				E2 = Exp2();
				return new ExpNegParen("!", E1, E2);
			case PAREN_IZQ:
				Comprueba(PAREN_IZQ);
				E1 = Exp();
				Comprueba(PAREN_DER);
				E2 = Exp2();
				return new ExpNegParen("(", E1, E2);
		}
		throw new Excepcion_de_Analizador(token.getToken(), "El compilador eperaba: INTEGER | true | false | ID | this | new | ! | (");
	}
	
	public Expresion2 Exp2() throws Excepcion_de_Analizador
	{
		Expresion E1;
		Identificador id;
		Expresion2 E2;

		switch(TokenActual)
		{
			case AND:
				Comprueba(AND);
				E1 = Exp();
				E2 = Exp2();
				return new Exp2Tipo("&&", E1, E2);
			case MENOSQUE:
				Comprueba(MENOSQUE);
				E1 = Exp();
				E2 = Exp2();
				return new Exp2Tipo("<", E1, E2);
			case MAS:
				Comprueba(MAS);
				E1 = Exp();
				E2 = Exp2();
				return new Exp2Tipo("+", E1, E2);
			case MENOS:
				Comprueba(MENOS);
				E1 = Exp();
				E2 = Exp2();
				return new Exp2Tipo("-", E1, E2);
			case MULT:
				Comprueba(MULT);
				E1 = Exp();
				E2 = Exp2();
				return new Exp2Tipo("*", E1, E2);
			case CORCHETE_IZQ:
				Comprueba(CORCHETE_IZQ);
				E1 = Exp();
				Comprueba(CORCHETE_DER);
				E2 = Exp2();
				return new Exp2Tipo("[", E1, E2);
			case PUNTO:
				Comprueba(PUNTO);
				if(TokenActual == LENGTH)
				{
					Comprueba(LENGTH);
					E2 = Exp2();
					return new Exp2Longitud(E2);
				}
				else if (TokenActual == ID)
				{
					id = new Identificador(token.token);
					Comprueba(ID);
					Faltantes.add(id);
					Exp2ID exp2 = new Exp2ID(id);
					Comprueba(PAREN_IZQ);
					while(TokenActual==INTEGER || TokenActual==TRUE || TokenActual==FALSE || TokenActual==ID || 
							TokenActual==THIS || TokenActual==NEW || TokenActual==NEG || TokenActual==PAREN_IZQ)
					{
						//E1 = Exp();
						exp2.newExp(Exp());
						if(TokenActual == COMA)
							Comprueba(COMA);
					}
					Comprueba(PAREN_DER);
					E2 = Exp2();
					exp2.setE2(E2);
					return exp2;
				}
				else
					throw new Excepcion_de_Analizador(token.getToken(), "El compilador eperaba: lengh ! ID");
			default:
				return null;
		}
	}
	
	public int Siguiente()
	{
		token = Scanner.nextToken();
		return token.code;
	}
	
	public void Comprueba(int Esperado) throws Excepcion_de_Analizador
	{
		if(TokenActual == Esperado)
			TokenActual = Siguiente();
		else
			throw new Excepcion_de_Analizador(token.getToken(), "El compilador eperaba: "+Esperado);
	}
	
	public void Trataerror(int TokenControl)
	{
		while(TokenActual!=TokenControl && TokenActual!=0)
		{
			TokenActual = Siguiente();
		}
		if(TokenActual!=0)
			TokenActual = Siguiente();
	}
	
	public String MensajeError()
	{
		Vector<Error> errorscanner = Scanner.getErrores();
		String Mensaje="";
		
		Mensaje+="Error Lexico\n";
		for(int i=0; i<errorscanner.size();i++)
			Mensaje+=errorscanner.elementAt(i).getError()+"\n";
		
		Mensaje+="\nError Sintactico\n";
		for(int i=0; i<Errores.size(); i++)
			Mensaje+=Errores.get(i).getError()+"\n";
		
		return Mensaje;
	}
	
	public String AsignaTexto()
	{
		return Resultado;
	}
	
	public Color getColor()
	{
		if(success)
			return Color.BLUE;
		return Color.RED;
	}
	
	
	//Semantico
	public boolean Declarada(Identificador id) throws Excepciones_Semanticas
	{
		for(int i=0;i<TablaSim.size();i++)
			if(TablaSim.get(i).getId().getID().equals(id.getID()))
				throw new Excepciones_Semanticas(id.getID(), "La declaración de variable a sido repetida");
		return false;
	}
	
	public void ChecaID(Identificador id)
	{
		try{
			BuscaVariable(id);
		}catch(Excepciones_Semanticas e){
			Errores.add(new Error(Errores.size()+1, "Error Semantico", e.getLocalizedMessage()));
			success = false;
		}
	}
	
	public boolean BuscaVariable(Identificador id)throws Excepciones_Semanticas
	{
		for(int i=0; i<TablaSim.size();i++)
			if(TablaSim.get(i).getId().getID().equals(id.getID()))
				return true;
		throw new Excepciones_Semanticas(id.getID(), "Variable no declarada");
	}
	
	public TablaDeSimbolos ObtenVar(Identificador id)throws Excepciones_Semanticas
	{
		for(int i=0; i<TablaSim.size();i++)
			if(TablaSim.get(i).getId().getID().equals(id.getID()))
				return TablaSim.get(i);
		throw new Excepciones_Semanticas(id.getID(), "Variable no declarada");
	}
	
}

