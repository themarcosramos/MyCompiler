package br.com.uefs.View;

import br.com.uefs.Analisador.*;
import br.com.uefs.DAO.*;
import br.com.uefs.Model.*;
import br.com.uefs.Util.*;


public class Main {

	public static void main(String[] args) {
        
	System.out.println("\n \t BEM VINDO ! \n");
		 
	System.out.println("################################################");
	System.out.println("####                                       #####");
	System.out.println("####  EXECUTANDO COMPILAÇÃO  DA LINGUAGEM  #####");
	System.out.println("####                                       #####");
	System.out.println("################################################ \n");
        
        Crud IDE = new Crud();
        
        Lexico lexico = new Lexico();
        
    System.out.println("################################################");
	System.out.println("####                                       #####");
	System.out.println("####            Análise  lexica            #####");
	System.out.println("####                                       #####");
	System.out.println("################################################ \n ");           
        
        Sintatico sintatico = new Sintatico();
       	        
	System.out.println("################################################");
	System.out.println("####                                       #####");
	System.out.println("####            Análise  Sintatica         #####");
	System.out.println("####                                       #####");
	System.out.println("################################################ \n");
	        
        String[] arquivos = IDE.getArquivos();
  
        for(int i=0; i<arquivos.length; i++) {
            String texto = IDE.lerArquivo(arquivos[i]);
            String[] arrayS = arquivos[i].split("\\\\");

            if(!texto.isEmpty()) {
                Token tokens = new Token();
                TabelaSimbolo simbolos = new TabelaSimbolo();
   
	    	System.out.println("################################################");
	    	System.out.println("####                                       #####");
	    	System.out.println("####            Tabela de Simbolos         #####");
	    	System.out.println("####                                       #####");
	    	System.out.println("################################################\n");
                
                String errosLexicos = lexico.analisar(texto, tokens, simbolos);                                
                Semantico semantico = new Semantico(tokens);
                
                String errosSintaticos = sintatico.analisar(tokens, semantico);
                       	        
	       System.out.println("################################################");
	       System.out.println("####                                        #####");
	       System.out.println("####            Análise  Semantica          #####");
	       System.out.println("####                                        #####");
	       System.out.println("################################################ \n");
	        
              //  semantico.printSemanticTable();
	       
             //   System.out.println("\n ERROS \n"+semantico.getErrosSemanticos());

	         IDE.salvaArquivo(errosLexicos, errosSintaticos, semantico.getErrosSemanticos(), "Saída/"+arrayS[1]);
                System.out.println("\n");
	                
	    	System.out.println("################################################");
	    	System.out.println("####                                       #####");
	    	System.out.println("####            EXECUTANDO CONCLUÍDA       #####");
	    	System.out.println("####                                       #####");
	    	System.out.println("################################################ \n");
	    	            
	        System.out.println("\t !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	    	System.out.println("\t !!!!     OBRIGADO  !!!!!!!!!! ");
	    	System.out.println("\t !!!!!!!!!!!!!!!!!!!!!!!!!!!!! \n");
	    	
	    	
            }            
        }  

	}

}