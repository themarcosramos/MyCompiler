package br.com.uefs.Model;

import java.util.ArrayList;

import br.com.uefs.Util.Escopo;


public class PEscopo {
	  private ArrayList<Escopo> pilha;

	    public PEscopo() {
	        this.pilha = new ArrayList();
	        this.addEscopo("global", "global");
	    }

	    public void addEscopo(String palavraReservada, String identificador) {
	        this.pilha.add(new Escopo(palavraReservada, identificador));
	    }
	    
	    public int getLastIndex() {
	        return (this.pilha.size()-1);
	    }
	    
	    public String getLastPalavraReservada() {
	        return this.pilha.get(this.getLastIndex()).getPalavraReservada();
	    }
	    
	    public String getLastIdentificador() {
	        return this.pilha.get(this.getLastIndex()).getIdentificador();
	    }
	    
	    public void removerLast() {        
	        if(!this.getLastIdentificador().equals("global")) {
	            this.pilha.remove(this.getLastIndex());
	        }
	    }
}
