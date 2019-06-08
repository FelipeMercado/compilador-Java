package Compilador;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class App_Compilador extends JFrame implements ActionListener
{
	
	public static void main(String [] args)
	{
		new App_Compilador();
	}
	
	private static final long serialVersionUID = 1L;
	
	JScrollPane Prog,Res;
	JTextArea Programa,Resultado;	
	JLabel lbPrograma,lbResultado,lbTotal;
	JTextField FilaColumna;
	JButton Compilar;
	
	boolean guarda = false;
	String NombreDoc;
	
	int ancho;
	int alto;
	
	public String TokenTipo;
	
	public App_Compilador()
	{
		super("Compilador");
		setSize(530,600);
		setLocationRelativeTo(null);
		setLayout(null);		
		
		setResizable(false);
		//Crear elementos para la interfaz
		Programa = new JTextArea(10,35);
		Resultado = new JTextArea(10,35);
		JLabel label=new JLabel("Código fuente") ;
		JLabel label2=new JLabel("Tabla de datos") ;
		
		Prog = new JScrollPane(Programa);
		Res = new JScrollPane(Resultado);
		Prog.setBounds(10, 40, 500, 200);
		Res.setBounds(10, 270,500, 200);
		
		Compilar = new JButton("Compilar");
		Compilar.setBounds(210,490,100, 25);
		
		label.setBounds(new Rectangle(220,10,400,21));  
		label.setFont(new java.awt.Font("Arial", 0, 17));
		label.setForeground(Color.blue);
		
		

		label2.setBounds(new Rectangle(220,245,400,21));  
		label2.setFont(new java.awt.Font("Arial", 0, 17));
		label2.setForeground(Color.blue);
		
		
		
		FilaColumna = new JTextField();
		FilaColumna.setPreferredSize(new Dimension(150,20));
		FilaColumna.setEnabled(false);
		FilaColumna.setDisabledTextColor(Color.BLACK);
		FilaColumna.setOpaque(false);
		FilaColumna.setBorder(null);
		
		
		//Agregar los elementos al JFrame
		add(Prog);
		add(Compilar);
		add(Res);
		add(label);
		add(label2);
		
		Resultado.setEnabled(false);
		Resultado.setOpaque(true);
		Resultado.setDisabledTextColor(Color.black);
		
		setVisible(true);
		
		//Escuchadores para os botones
		
		
		Compilar.addActionListener(this);
	}
	
	
	
	
	public void actionPerformed(ActionEvent evento)
	{
		
		
		if(evento.getSource() instanceof JButton){
			if (evento.getSource() == Compilar){
				
				Resultado.setText("");
				
				Parser par = new Parser(Programa.getText());
				Resultado.setDisabledTextColor(par.getColor());
				Resultado.setText(par.AsignaTexto());
				return;
			}
		}
		
	}
	
	
}


