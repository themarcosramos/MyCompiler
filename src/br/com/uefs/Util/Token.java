package br.com.uefs.Util;

import java.util.ArrayList;

public class Token {
	
	  private ArrayList<String> ListaDeTokens;

	    public Token() {
	        ListaDeTokens = new ArrayList();
	    }
	    
	    /**
	     * <h2>Adiciona um novo Token.</h2>
	     * @param classe
	     * @param valor
	     * @param linha 
	     */
	    public void addToken(String classe, String valor, int linha) {
	        
	        if(linha != 0) {
	            this.ListaDeTokens.add("<"+classe+", "+valor+", "+linha+">");
	        } else {
	            this.ListaDeTokens.add("<"+classe+", "+valor+">");
	        }    
	    }
	   
	    public ArrayList<String> getTokens() {
	        return this.ListaDeTokens;
	    }
	    
	    public String getUnicToken(int index) {
	        return this.ListaDeTokens.get(index);
	    }
	       
	    public int getSize() {
	        return this.ListaDeTokens.size();
	    }
	    
	    public void printTokens() {
	        
	        this.ListaDeTokens.forEach(
	            (atual) -> {
	                System.out.println(atual);
	            }
	        );
	    }

}
