package br.com.uefs.Model;

import java.util.ArrayList;

import br.com.uefs.Util.Funcao;


public class TabelaFuncao {
	  private ArrayList<Funcao> funcoes;

	    public TabelaFuncao() {
	        this.funcoes = new ArrayList();
	    }
	    
	    public ArrayList<Funcao> getFuncoes() {
	        return funcoes;
	    }

	    public void addFuncao(Funcao funcao) {
	        this.funcoes.add(funcao);
	    }

}
