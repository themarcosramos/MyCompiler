package br.com.uefs.Util;

public class Parametros {
	
	   private String tipo;
	    private String nome;

	    public Parametros () {
	        this.tipo = "";
	        this.nome = "";        
	    }

	    public Parametros (String tipo) {
	        this.tipo = tipo;
	        this.nome = ""; 
	    }

	    public String getTipo() {
	        return tipo;
	    }

	    public void setTipo(String tipo) {
	        this.tipo = tipo;
	    }

	    public String getNome() {
	        return nome;
	    }

	    public void setNome(String nome) {
	        this.nome = nome;
	    }
}
