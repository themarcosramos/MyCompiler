package br.com.uefs.Model;

import java.util.ArrayList;

import br.com.uefs.Util.Simbolo;

public class TabelaSimbolo {
	
	  private ArrayList<Simbolo> simbolos;// Lista de Simbolos.
	    private int qtd;
	    
	    
	    public TabelaSimbolo() {
	        this.qtd = 1;
	        this.simbolos = new ArrayList();
	    }
	    
	    /**
	     * Verifica se o valor recebido contem na lista.
	     * @param valor String
	     * @return 
	     */
	    public boolean contem(String valor) {
	        if(this.simbolos.isEmpty()) {
	            return false;
	        }
	        Simbolo atual;
	        for(int i=0; i<this.simbolos.size(); i++) {
	             atual = simbolos.get(i);
	             if(atual.getValor().equals(valor)) {
	                 return true;
	             }
	        }
	        return false;
	    }
	    
	    /**
	     * Retorna o nome referente ao valor recebido.
	     * @param valor String
	     * @return 
	     */
	    public String getNomeSimbolo(String valor) {
	        if(this.simbolos.isEmpty()) {
	            return null;
	        }
	        Simbolo atual;
	        for(int i=0; i<this.simbolos.size(); i++) {
	             atual = simbolos.get(i);
	             if(atual.getValor().equals(valor)) {
	                 return atual.getNome();
	             }
	        }
	        return null;
	    }
	    
	    /**
	     * Adiciona um novo Simbolo a lista.
	     * @param valor String
	     * @return 
	     */
	    public String addSimbolo(String valor) {
	        String nome = "Identificador"+this.qtd;
	        this.qtd++;
	        this.simbolos.add(new Simbolo(nome, valor));
	        return nome;
	    }

}
