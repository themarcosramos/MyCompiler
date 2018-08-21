package br.com.uefs.Model;

import java.util.ArrayList;


public class PContexto {
	 private ArrayList<String> pilha;

	    public PContexto() {
	        this.pilha = new ArrayList();
	    }

	    public int getLastIndex() {
	        return (this.pilha.size()-1);
	    }
	    
	    public void addContexto(String contexto) {
	        this.pilha.add(contexto);
	    }
	    
	    public String getLastContexto() {
	        if(this.pilha.isEmpty())
	            return "";
	        return this.pilha.get(this.getLastIndex());
	    }
	    
	    public void removerLast() {
	        if(!this.pilha.isEmpty()) {
	            this.pilha.remove(this.getLastIndex());
	        }
	    }
}
