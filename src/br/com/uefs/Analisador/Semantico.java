package br.com.uefs.Analisador;

import br.com.uefs.Model.*;
import br.com.uefs.Util.*;

import java.util.ArrayList;


public class Semantico {
	   private TabelaSemantica tabelaSemantica;
	    private PContexto pilha2;
	    private PEscopo pilha1;
	    private String erros; // 7
	    private boolean temStart;
	    private boolean initializer;
	    private boolean initializer2;
	    private boolean ignorarAtribuicao;
	    private Token tokens;

	    public Semantico(Token tokens) {
	        this.tabelaSemantica = new TabelaSemantica();
	        this.pilha2 = new PContexto();
	        this.pilha1 = new PEscopo();
	        this.erros = "";
	        this.tokens = tokens;    
	    }
	    
	    /**
	     * função que analisar a declração do metodo principal 
	     * 
	     */
	    public void declararStart(String linha) {  
	        if(this.temStart) {
	           this.erros += "Erro- Numero de métodos principais 'Start' declarados excedido na linha "+linha+".\n";
	           
	        } else { 
	           this.temStart = true;
	           
	        }
	    }
	    
	    /**
	     * 
	     * função que analisar semantica a variaveis 
	     */
	    public void Var(String linha) {
	        
	        NSemantico no = new NSemantico();
	        no.setDeclaracao("var");
	        no.setLinhaDeclaracao(linha);
	        no.setPalavraReservadaEscopo(this.pilha1.getLastPalavraReservada());
	        no.setIdentificadorEscopo(this.pilha1.getLastIdentificador());
	        this.tabelaSemantica.addNo(no);
	    }
	    
	    /**
	     * função que analisar semantica a a declaração de  variaveis 
	     */
	    public void declararVar(String tipo, String linha) {
	        NSemantico no = new NSemantico();
	        no.setDeclaracao("var");
	        no.setLinhaDeclaracao(linha);
	        no.setTipo(tipo);
	        no.setPalavraReservadaEscopo(this.pilha1.getLastPalavraReservada());
	        no.setIdentificadorEscopo(this.pilha1.getLastIdentificador());
	        this.tabelaSemantica.addNo(no);
	        
	    }
	    
	    /**
	     *função que analisar semantica  de  constante
	     */
	 
	    public void declararConst(String linha) {    
	        NSemantico no = new NSemantico();
	        no.setDeclaracao("const");
	        no.setLinhaDeclaracao(linha);
	        no.setPalavraReservadaEscopo(this.pilha1.getLastPalavraReservada());
	        no.setIdentificadorEscopo(this.pilha1.getLastIdentificador());
	        this.tabelaSemantica.addNo(no);
	        
	    }
	    
	    /**
	     * função que analisar semantica a a declaração de   constante 
	     */
	    public void declararConst(String tipo, String linha) {
	        NSemantico no = new NSemantico();
	        no.setDeclaracao("const");
	        no.setLinhaDeclaracao(linha);
	        no.setTipo(tipo);
	        no.setPalavraReservadaEscopo(this.pilha1.getLastPalavraReservada());
	        no.setIdentificadorEscopo(this.pilha1.getLastIdentificador());
	        this.tabelaSemantica.addNo(no);
	        
	    }
	    
	    /**
	     * função que analisar semantica a a declaração de Blocos
	     */
	    public void Blocos(String linha) {
	        NSemantico no = new NSemantico();
	        no.setDeclaracao("struct");
	        no.setLinhaDeclaracao(linha);
	        no.setPalavraReservadaEscopo(this.pilha1.getLastPalavraReservada());
	        no.setIdentificadorEscopo(this.pilha1.getLastIdentificador());
	        this.tabelaSemantica.addNo(no);
	        
	    }
	    
	    /**
	     * função que analisar semantica a a declaração de Funcao
	     */
	     
	    public void declararFuncao(String linha) {  
	        NSemantico no = new NSemantico();
	        no.setDeclaracao("function");
	        no.setLinhaDeclaracao(linha);
	        no.setPalavraReservadaEscopo(this.pilha1.getLastPalavraReservada());
	        no.setIdentificadorEscopo(this.pilha1.getLastIdentificador());
	        this.tabelaSemantica.addNo(no);
	        
	    }
	    
	    /**
	     * função que analisar semantica a a declaração de procedimentos
	     */
	    public void procedimento(String linha) {
	        NSemantico no = new NSemantico();
	        no.setDeclaracao("procedure");
	        no.setLinhaDeclaracao(linha);
	        no.setPalavraReservadaEscopo(this.pilha1.getLastPalavraReservada());
	        no.setIdentificadorEscopo(this.pilha1.getLastIdentificador());
	        this.tabelaSemantica.addNo(no);
	        
	    }
	    
	    /**
	     * função que analisar semantica a a declaração de NovoTipo
	     */
	    public void NovoTipo(String linha) {
	        NSemantico no = new NSemantico();
	        no.setDeclaracao("typedef");
	        no.setLinhaDeclaracao(linha);
	        no.setPalavraReservadaEscopo(this.pilha1.getLastPalavraReservada());
	        no.setIdentificadorEscopo(this.pilha1.getLastIdentificador());
	        this.tabelaSemantica.addNo(no);
	        
	    }
	    
	    /**
	     * função que analisar semantica a a declaração de blocos com variaveis
	     */
	    public void BlocosComVariaveis(String tipo, String linha) {
	        NSemantico no = new NSemantico();
	        no.setDeclaracao("varStruct");
	        no.setLinhaDeclaracao(linha);
	        no.setTipo(tipo);
	        no.setPalavraReservadaEscopo(this.pilha1.getLastPalavraReservada());
	        no.setIdentificadorEscopo(this.pilha1.getLastIdentificador());
	        this.tabelaSemantica.addNo(no);
	        
	    }  
	    
	     /**
	     * função a seguir faz uma adição de tipos  para realizar analise semantica 
	     */
	    public void addTipo(String tipo, String linha) {
	        if(this.pilha1.getLastPalavraReservada().equals("struct")) {             
	            this.BlocosComVariaveis(tipo, linha);
	            
	        } else if(this.pilha2.getLastContexto().equals("function")) {
	            
	            if(this.tabelaSemantica.getLastNo().getTipo().isEmpty()) {  
	                this.tabelaSemantica.addTipo(tipo);
	                
	            } else {
	                this.tabelaSemantica.addTipoParametros(tipo);
	                
	            }            
	        } else if(this.pilha2.getLastContexto().equals("procedure")) {
	            this.tabelaSemantica.addTipoParametros(tipo);
	            
	        } else if(this.pilha2.getLastContexto().equals(this.tabelaSemantica.getLastNo().getDeclaracao())) {
	            
	            this.tabelaSemantica.addTipo(tipo);
	        } else {

	        }
	    }
	    
	    public void heranca(String nome, String linha) {
	        if(this.tabelaSemantica.isStruct(nome)) {
	            this.tabelaSemantica.heranca(nome);
	            
	        } else {
	            
	            this.erros += "Erro- Erro na linha "+linha+", Struct '"+nome+"' não declarada.\n";
	        }
	    }
	    
	    public void addNomeProcedimento(String nome, String linha) {
	        
	        if(!this.tabelaSemantica.verificarProcedure(nome)) {
	            if(this.tabelaSemantica.verificarEscopoAtual(nome, this.pilha1.getLastPalavraReservada(), this.pilha1.getLastIdentificador())) {  
	                this.erros += "Erro - Erro na linha "+linha+", já existe uma declaração no escopo atual com o nome '"+nome+"'.\n";
	                this.tabelaSemantica.getLastNo().setErro(true);  
	                
	            }
	        } else { 
	          this.tabelaSemantica.getLastNo().setErro(true);  
	          
	        }
	        this.tabelaSemantica.addNome(nome);
	    }
	    
	    public void addNomeBloco(String nome, String linha) {
	        
	        if(this.tabelaSemantica.verificarStruct(nome)) {
	            this.tabelaSemantica.getLastNo().setErro(true);  
	            this.erros += "Erro - Erro na linha "+linha+", já existe uma struct declarada como '"+nome+"'.\n";
	            
	        } else {
	            if(this.tabelaSemantica.verificarEscopoAtual(nome, this.pilha1.getLastPalavraReservada(), this.pilha1.getLastIdentificador())) { 
	                this.erros += "Erro- Erro na linha "+linha+", já existe uma declaração no escopo atual com o nome '"+nome+"'.\n";
	                this.tabelaSemantica.getLastNo().setErro(true);
	                
	            }
	        }
	        this.tabelaSemantica.addNome(nome);
	    }
	    
	    public void addNomeDeNovoTipo(String nome, String linha) {
	        
	        if(this.tabelaSemantica.verificarNovoTipo(nome)) {
	            this.tabelaSemantica.getLastNo().setErro(true);  
	            this.erros += "Erro- Erro na linha "+linha+", já existe um typedef declarado como '"+nome+"'.\n";
	       
	        } else {
	            if(this.tabelaSemantica.verificarEscopoAtual(nome, this.pilha1.getLastPalavraReservada(), this.pilha1.getLastIdentificador())) {
	                this.erros += "Erro- Erro na linha "+linha+", já existe uma declaração no escopo atual com o nome '"+nome+"'.\n";
	                this.tabelaSemantica.getLastNo().setErro(true);  
	                
	            }
	        }
	        this.tabelaSemantica.addNome(nome);
	    }
	       
	    public void addNome(String nome, String linha) { 
	        NSemantico no = this.tabelaSemantica.getLastNo();
	        
	        if(this.pilha2.getLastContexto().equals("function")) {
	            
	            if(this.tabelaSemantica.getLastNo().getNome().isEmpty()) {
	                
	                if(!this.tabelaSemantica.verificarFunction(nome)){
	                    
	                    if(this.tabelaSemantica.verificarEscopoAtual(nome, this.pilha1.getLastPalavraReservada(), this.pilha1.getLastIdentificador())) { 
	                        this.erros += "Erro- Erro na linha "+linha+", já existe uma declaração no escopo atual com o nome '"+nome+"'.\n";
	                        no.setErro(true);  
	                        
	                    }
	                }
	                this.tabelaSemantica.addNome(nome);
	            } else {        
	                
	                if(this.tabelaSemantica.addNomeParametros(nome))
	                    this.erros += "Erro - Erro na linha "+linha+", já existe uma parametro o nome '"+nome+"'.\n";    
	            } 
	        } else if(this.pilha2.getLastContexto().equals("procedure")) {
	            
	            if(this.tabelaSemantica.addNomeParametros(nome)) {
	                this.erros += "Erro- Erro na linha "+linha+", já existe uma parametro o nome '"+nome+"'.\n";
	                no.setErro(true);
	                
	            }
	        } else if(this.pilha1.getLastPalavraReservada().equals("struct")) {
	          
	            if(this.tabelaSemantica.verificarBloco(nome, this.pilha1.getLastPalavraReservada(), this.pilha1.getLastIdentificador())) {
	                this.erros += "Erro - Erro na linha "+linha+", já existe uma variável declarada como '"+nome+"'.\n";;
	                no.setErro(true);
	                
	            }
	            this.tabelaSemantica.addNome(nome);
	        } else {
	            
	            if(no.getDeclaracao().equals("var")) {
	               
	                if(this.tabelaSemantica.verificarVar(nome, this.pilha1.getLastPalavraReservada(), this.pilha1.getLastIdentificador())){
	                    this.erros += "Erro - Erro na linha "+linha+", já existe uma variável declarada como '"+nome+"'.\n";
	                    no.setErro(true);
	               
	                } else if(this.pilha1.getLastPalavraReservada().equals("Global")) {
	                    if(this.tabelaSemantica.verificarEscopoAtual(nome, this.pilha1.getLastPalavraReservada(), this.pilha1.getLastIdentificador())) {    
	                        this.erros += "Erro - Erro na linha "+linha+", já existe uma declaração no escopo atual com o nome '"+nome+"'.\n";
	                        no.setErro(true);
	                        
	                    }    
	                } 
	                if(no.getNome().isEmpty()) {     
	                    this.tabelaSemantica.addNome(nome);
	               
	                } else {
	                    this.declararVar(no.getTipo(), linha);
	                    this.tabelaSemantica.addNome(nome);
	                
	                }                
	            } else if(no.getDeclaracao().equals("const")) {
	                
	                if(this.tabelaSemantica.verificarConst(nome)) {
	                    no.setErro(true);
	                    this.erros += "Erro - Erro na linha "+linha+", já existe uma constante declarada como '"+nome+"'.\n";
	               
	                } else if(this.tabelaSemantica.verificarEscopoAtual(nome, this.pilha1.getLastPalavraReservada(), this.pilha1.getLastIdentificador())){
	                    
	                    no.setErro(true);
	                    this.erros += "Erro - Erro na linha "+linha+", já existe uma declaração no escopo atual com o nome '"+nome+"'.\n";
	                }
	                this.tabelaSemantica.addNome(nome);
	            } else {
	              
	            } 
	        }
	    }
	    
	    public void addValor(String valor, String linha, String classe) { 
	        NSemantico no = this.tabelaSemantica.getLastNo();
	        
	        if(this.initializer) {
	                      
	            if(no.getValor().isEmpty()) {
	                this.tabelaSemantica.addValor(valor, 1);
	                
	            } else {
	                this.tabelaSemantica.addValor(valor, 2);
	                
	            }         
	        } else if(this.initializer2 && !this.ignorarAtribuicao) {
	                        
	            if(no.getDeclaracao().equals("const")) {
	                this.erros += "Erro- Erro na linha "+linha+", a constante '"+no.getNome()+"' não pode ter o valor alterado.\n";
	                this.ignorarAtribuicao = true;
	           
	            } else {
	                
	                if(classe.equals("Identificador") && no.getDeclaracao().equals("var")) {
	                    NSemantico noF = this.tabelaSemantica.isFunction(valor);
	                    if(noF != null) {
	                     
	                        if(!no.getTipo().equals(noF.getTipo())) {
	                            this.erros += "Erro - Erro na linha "+linha+", o tipo de retorno da função '"+noF.getNome()+"' não corresponde com o tipo da variável '"+no.getNome()+"'.\n";
	                        }
	                    }                    
	                }
	                if(no.getValor().isEmpty()) {
	                    this.tabelaSemantica.addValor(valor, 1); 
	                    
	                } else {
	                    this.tabelaSemantica.addValor(valor, 2);
	                }  
	            }
	        } else if(!this.ignorarAtribuicao) {
	        
	            if(classe.equals("Identificador")) {
	                int id = this.tabelaSemantica.verificarDeclaracao(valor, this.pilha1.getLastPalavraReservada(), this.pilha1.getLastIdentificador()); 
	                
	                if(id == 0) {
	                    this.erros += "Erro - Erro na linha "+linha+", o identificador '"+valor+"' não foi inicializado.\n";
	                    this.initializer2 = false;
	                    
	                } else {
	                    if(!this.tabelaSemantica.getLastNo().getDeclaracao().equals("const")) {
	                        this.tabelaSemantica.getLastNo().setValor("");
	                    }
	                    this.initializer2 = true;
	                }
	            } else if(classe.equals("Palavra_Reservada")) {

	            }
	        }
	    }
	    
	    public void atribuicaoDeBlocos(String nome, String linha) {
	        if(!this.tabelaSemantica.verificarDeclaracaoBloco(nome)) {
	            this.erros += "Erro - Erro na linha "+linha+", o identificador '"+nome+"' não foi inicializado.\n";
	        } 
	    }
	    
	    /**
	     * A função a seguir realizar a analise semantica  da inicialização de constante
	     */
	    public void verificarInicializacaoConst(String linha) {  
	        NSemantico no = this.tabelaSemantica.getLastNo();
	        
	        if(no.getValor().isEmpty()) {
	            this.erros += "Erro - Constante '"+no.getNome()+"' não inicializada na linha "+linha+".\n";
	        }
	    }
	    
	    public void addEscopo(String palavraReservada, String Identificador) {                          
	        this.pilha1.addEscopo(palavraReservada, palavraReservada);      
	    }
	    
	    public void addEscopo() {   
	        NSemantico no = this.tabelaSemantica.getLastNo();        
	        this.pilha1.addEscopo(no.getDeclaracao(), no.getNome());      
	    }
	    

	    public void addContexto(String contexto) {
	        this.pilha2.addContexto(contexto);
	    }
	    
	    public void removerContexto() {        
	        this.pilha2.removerLast();
	    }
	    
	    /**
	     * A função a seguir realizar analise de  sobrecaraga    
	     */
	    public void verificarSobrecaraga() {        
	        this.tabelaSemantica.getFuncoesPendentes().forEach(
	            (no) -> {
	                NSemantico noAtual = this.tabelaSemantica.getNo(no.getIdSobrecarga());
	                if(noAtual != null) {
	                                    
	                    if(this.compararParametros(noAtual.getParametros(), no.getParametros())) {  
	                        this.erros += "Erro - Erro na linha "+no.getLinhaDeclaracao()+", já existe uma funcao declarada como '"+no.getNome()+"' e esta não é uma sobrecarga.\n";
	                   
	                    } else if(!noAtual.getTipo().equals(no.getTipo())) { 
	                        this.erros += "Erro - Erro na linha "+no.getLinhaDeclaracao()+", os tipo de retorno da função não corresponde para uma sobrecarga.\n";
	                    
	                    } else if(!noAtual.getValor().equals(no.getValor())) { 
	                        this.erros += "Erro - Erro na linha "+no.getLinhaDeclaracao()+", não é permitido a sobrescrita de funções.\n";
	                    
	                    }
	                }                
	            }
	        );

	        this.tabelaSemantica.getProceduresPendentes().forEach(
	            (no)-> { 
	                NSemantico noAtual = this.tabelaSemantica.getNo(no.getIdSobrecarga());
	                if(this.compararParametros(noAtual.getParametros(), no.getParametros())) {
	                    this.erros += "Erro- Erro na linha "+no.getLinhaDeclaracao()+", já existe uma procedure declarada como '"+no.getNome()+"' e esta não é uma sobrecarga.\n";
	                
	                } else if(!noAtual.getValor().equals(no.getValor())) {
	                    this.erros += "Erro - Erro na linha "+no.getLinhaDeclaracao()+", não é permitido a sobrescrita de procedures.\n";
	                }
	            }
	        ); 
	    }    
	    
	    private boolean compararParametros(ArrayList<Parametros> p1, ArrayList<Parametros> p2) {
	           
	        if(p1.size() == p2.size()) {
	            int igualdades = 0;
	            
	            for(Parametros n1 : p1) {
	                for(Parametros n2 : p2) {
	                    
	                    if(n1.getTipo().equals(n2.getTipo()) && n1.getNome().equals(n2.getNome())) {
	                        igualdades++;
	                        
	                    } 
	                }
	            }
	            if(igualdades == p1.size()) {
	                return true;
	            }
	        }       
	        return false;
	    }
	        
	    public void addConteudoFuncao(String conteudo, boolean temReturn) {
	        NSemantico no = this.tabelaSemantica.getLastFunction();
	        if(!temReturn) {
	            this.erros += "Erro - Retorno não encontrado na função '"+no.getNome()+"' declarada na linha "+no.getLinhaDeclaracao()+".\n";
	        }
	        no.setValor(conteudo);
	    }
	    
	    public void addConteudoProcedure(String conteudo, boolean temReturn, String linhaRetorno) {
	        NSemantico no = this.tabelaSemantica.getLastProcedure();
	        if(temReturn) {
	            this.erros += "Erro- Erro na linha "+linhaRetorno+", retorno inesperado na procedure '"+no.getNome()+"'.\n";
	        }
	        this.tabelaSemantica.getLastNo().setValor(conteudo);
	    }
	    
	    public void setInitializer(boolean initializer) {
	        this.initializer = initializer;
	    }

	    public boolean isInitializer() {
	        return initializer;
	    }

	    public boolean isInitializer2() {
	        return initializer2;
	    }

	    public void setInitializer2(boolean initializer2) {
	        this.initializer2 = initializer2;
	    }
	    
	    public String getErrosSemanticos() {        
	        return this.erros;
	    }

	    public void setIgnorarAtribuicao(boolean ignorarAtribuicao) {
	        this.ignorarAtribuicao = ignorarAtribuicao;
	    }
	           
	    public void printSemanticTable() {  
	        this.tabelaSemantica.printSemanticTable();
	    }

}
