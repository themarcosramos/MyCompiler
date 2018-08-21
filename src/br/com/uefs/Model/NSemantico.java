package br.com.uefs.Model;

import java.util.ArrayList;

import br.com.uefs.Util.*;

public class NSemantico {
	  private final int id;
	    private String declaracao;  
	    private String linhaDeclaracao;
	    private String tipo;
	    private String nome;      
	    private String palavraReservadaEscopo;
	    private String identificadorEscopo;
	    private String valor; 
	    private final ArrayList<String> extensao;
	    private final ArrayList<Parametros> parametros; 
	    private int idSobrecarga;
	    private boolean erro;
	    protected boolean chave;

	    public NSemantico() {
	        this.id = TabelaSemantica.getId();
	        this.nome = "";
	        this.valor = "";    
	        this.tipo = "";
	        this.extensao = new ArrayList();
	        this.parametros = new ArrayList();
	        this.palavraReservadaEscopo = "";
	        this.identificadorEscopo = "";
	        this.idSobrecarga = 0;
	    }

	    public int getId() {
	        return id;
	    }
	    
	    public String getDeclaracao() {
	        return declaracao;
	    }

	    public void setDeclaracao(String declaracao) {
	        this.declaracao = declaracao;
	    }      

	    public String getLinhaDeclaracao() {
	        return linhaDeclaracao;
	    }

	    public void setLinhaDeclaracao(String linhaDeclaracao) {
	        this.linhaDeclaracao = linhaDeclaracao;
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

	    public String getPalavraReservadaEscopo() {
	        return palavraReservadaEscopo;
	    }

	    public void setPalavraReservadaEscopo(String palavraReservadaEscopo) {
	        this.palavraReservadaEscopo = palavraReservadaEscopo;
	    }

	    public String getIdentificadorEscopo() {
	        return identificadorEscopo;
	    }

	    public void setIdentificadorEscopo(String identificadorEscopo) {
	        this.identificadorEscopo = identificadorEscopo;
	    }
	           
	    public String getValor() {
	        return valor;
	    }

	    public void setValor(String valor) {
	        this.valor = valor;
	    }
	    
	    public void setValor2(String valor) {        
	        this.valor += valor;
	    }  
	    
	    public void setValor3(String valor) {        
	        if(this.chave) {
	            this.valor += ", "+valor;
	            this.chave = false;
	        } else {
	            this.valor += " "+valor;
	            this.chave = true;
	        }        
	    }
	    
	    public int getIdSobrecarga() {
	        return idSobrecarga;
	    }

	    public void setIdSobrecarga(int idSobrecarga) {
	        this.idSobrecarga = idSobrecarga;
	    }

	    public ArrayList<Parametros> getParametros() {
	        return this.parametros;
	    }

	    public void addTipoParametro(String tipo) {
	        this.parametros.add(new Parametros(tipo));
	    }
	    
	    public void addNomeParametro(String nome) {
	        this.parametros.get(this.parametros.size()-1).setNome(nome);
	    }

	    public boolean isErro() {
	        return erro;
	    }

	    public void setErro(boolean erro) {
	        this.erro = erro;
	    }

	    public ArrayList<String> getExtensoes() {
	        return extensao;
	    }

	    public void heranca(String extensao) {
	        this.extensao.add(extensao);
	    }


}
