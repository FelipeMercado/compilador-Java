package Compilador;


import java.util.*;

public class Escaner 
{
	
	Vector <String> Reservadas = new Vector<String>();
	Vector <Character> Validos = new Vector<Character>();
	
	Vector<Error> Errores = new Vector<Error>(1,1);
	
	String Codigo;
	
	int ap;
	char caracter;
	String token;
	
	public Escaner(String Codigo)
	{
		this.Codigo = Codigo;
		llenarVectores();
		int ap = 0;
		
		if(Codigo.length()!=0)
			caracter = Codigo.charAt(ap);
		else
			caracter = 0;
	}
	
	public Tokens nextToken()
	{
		Tokens tmp = Scanner();
		
		return tmp;
	}
	
	public void Siguiente()
	{
		ap++;
		if(ap<Codigo.length()){
			caracter = Codigo.charAt(ap);
		}
		else{
			ap=-1;
			caracter = 0;
		}
			
	}
	
	public char MiraSiguiente()
	{
		int temp = ap;
		temp++;
		if(temp<Codigo.length()){
			return Codigo.charAt(temp);
		}
		return 0;
	}
	
	public Tokens Scanner()
	{
		token = "";
		
		Ignora();
		
		if(caracter == '/')
			if(MiraSiguiente()== '/' || MiraSiguiente()=='*')
				Comentario();
		
		Ignora();
		
		switch(caracter)
		{
			case 0:
				return new Tokens("EOF",0);
			case '{':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 5);
			case '}':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 6);
			case '(':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 7);
			case ')':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 8);
			case '[':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 9);
			case ']':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 10);
			case ';':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 11);
			case '=':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 12);
			case '&':
				token+=caracter;//CASO &&
				Siguiente();
				if(caracter == '&')
				{
					token+=caracter;
					Siguiente();
					return new Tokens(token, 13);
				}
				ErrorLexico();
				return new Tokens(token, -1);
			case '<':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 14);
			case '+':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 15);
			case '-':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 16);
			case '*':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 17);
			case '.':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 18);
			case ',':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 19);
			case '!':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 20);
			case '_':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 21);
			case '/':
				token+=caracter;
				Siguiente();
				return new Tokens(token, 22);
				
				
			default:
				if(Character.isLetter(caracter))
				{
					token+=caracter;
					Siguiente();
					while(Character.isLetter(caracter) || Character.isDigit(caracter) || caracter == '_')
					{
						token+=caracter;
						Siguiente();
					}
					
					//Syso
					if(token.equals("System"))
					{
						if(caracter == '.')
						{
							token+=caracter;
							Siguiente();
							while(Character.isLetter(caracter))
							{
								token+=caracter;
								Siguiente();
							}
							if(token.equals("System.out"))
							{
								if(caracter == '.')
									token+=caracter;
								Siguiente();
								while(Character.isLetter(caracter))
								{
									token+=caracter;
									Siguiente();
								}
							}
						}
					}
					
					int tmp = isReservada(token);
					if(tmp != -1)
						return new Tokens(token, 23+tmp);
					
					return new Tokens(token, 42);
					
				}
				else if(Character.isDigit(caracter))
				{
					token+=caracter;
					Siguiente();
					
					while(Character.isDigit(caracter))
					{
						token+=caracter;
						Siguiente();
					}
					if(!Validos.contains(caracter))
					{
						while(!Validos.contains(caracter))
						{
							token+=caracter;
							Siguiente();
						}
						
						ErrorLexico();
						return new Tokens(token, -1);
					}
					
					return new Tokens(token, 43);
				}
				else{
					ErrorLexico();
					return new Tokens(token, -1);
				}
		}
		
	}
	
	public int isReservada(String token)
	{
		if(Reservadas.contains(token))
			return Reservadas.indexOf(token);
		else
			return -1;
	}
	
	public void ErrorLexico(){
		while(!Validos.contains(caracter) && !Character.isLetter(caracter) && caracter!=0)
		{
			token+=caracter;
			Siguiente();
		}
		
		Errores.add(new Error(Errores.size()+1,"Error lexico",token+" token invalido"));
	}
	
	public Vector<Error> getErrores()
	{
		return Errores;
	}
	
	public void Ignora()
	{
		while(caracter == ' ' || caracter == '\n' || caracter == '\t')
		{
			Siguiente();
		}
	}
	
	public void Comentario()
	{
		token+=caracter;
		Siguiente();
		
		switch(caracter){
			case '/':
				while(caracter!='\n' && caracter!=0)
				{
					token+=caracter;
					Siguiente();
				}
				System.out.println(token+"\tComentario");
				break;
			case '*':
				token+=caracter;
				Siguiente();
				
				while(ap<Codigo.length())
				{
					if(caracter=='*')
					{
						token+=caracter;
						Siguiente();
						
						if(caracter=='/')
						{
							token+=caracter;
							Siguiente();
							System.out.println(token+"\tComentario");
							break;
						}
					}
					else if(ap==-1)
					{
						ErrorLexico();
						break;
					}
					
					if(caracter!='*')
					{
						token+=caracter;
						Siguiente();
					}
				}
				break;
		}
		token="";
	}
	
	public void llenarVectores()
	{
		Reservadas.add("class");
		Reservadas.add("public");
		Reservadas.add("static");
		Reservadas.add("void");
		Reservadas.add("main");
		Reservadas.add("String");
		Reservadas.add("extends");
		Reservadas.add("return");
		Reservadas.add("int");
		Reservadas.add("boolean");
		Reservadas.add("if");
		Reservadas.add("else");
		Reservadas.add("while");
		Reservadas.add("System.out.println");
		Reservadas.add("length");
		Reservadas.add("true");
		Reservadas.add("false");
		Reservadas.add("this");
		Reservadas.add("new");
		
		Validos.add('{');
		Validos.add('}');
		Validos.add('(');
		Validos.add(')');
		Validos.add('[');
		Validos.add(']');
		Validos.add(';');
		Validos.add('=');
		Validos.add('&');
		Validos.add('<');
		Validos.add('+');
		Validos.add('-');
		Validos.add('*');
		Validos.add('.');
		Validos.add(',');
		Validos.add('!');
		Validos.add('_');
		Validos.add('/');
		Validos.add('\n');
		Validos.add('\t');
		Validos.add(' ');
	}
}
