package br.com.uefs.Analisador;

import br.com.uefs.Model.*;
import br.com.uefs.Util.*;

public class Sintatico {
    private String erros;
    private Token tokens;
    private int qtd;
    private Semantico semantico;
    
    public Sintatico() {    }
    
 
    public String analisar(Token tokens, Semantico semantico) {
        
        this.tokens = tokens;        
        this.qtd = 0;
        this.erros = "";
        this.semantico = semantico;
        if(this.qtd < this.tokens.getSize()) {
            this.Programa();
            this.semantico.verificarSobrecaraga();
        }        
        return this.erros;
    }
    
    /**
     * <h2> Programa</h2><br/>
     * para  poder  realizar a chamada recussiva  para para poder analisar qualm a parte do c�digo .</p><br/> 
      */
    private void Programa() {
        FuncoesOuProcedimentos();
        this.EscopoGlobal();
        this.Program();        
    }

    /**
     * <h2> Program</h2><br/>
      * para  poder  realizar a chamada recussiva  para para poder analisar qualm a parte do c�digo .</p><br/>
     */
    private void Program() {
        if(this.qtd < this.tokens.getSize()) {  
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            if(atual[1].trim().equals("struct") || atual[1].trim().equals("procedure") ||
                    atual[1].trim().equals("typedef") || atual[1].trim().equals("const") ||
                    atual[1].trim().equals("function") || atual[1].trim().equals("var") ||
                    atual[1].trim().equals("start")) {
                this.EscopoGlobal();
                FuncoesOuProcedimentos();
                this.Program();
            }
        }   
    }
    /**
      * <h2>FuncoesOuProcedimentos </h2><br/>
      * <p>A fun��o FuncoesOuProcedimentos, <br/>
      * analisar  a cria��o de  uma <br/>
      * fun��o ou  um procedimento. <p> <br/>
     */
private void FuncoesOuProcedimentos(){
  if(this.qtd < this.tokens.getSize()) {
       String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
       if(atual[1].trim().equals("function")) {
          this.Funcao(); 
       } else if(atual[1].trim().equals("procedure")) {
          this.Procedimento();
       } else {
          String linha = atual[2].replaceAll(">", " ");
          this.erros += "Erro - Palavra Reservada n�o encontrada na linha "+linha.trim()+".\n";                
          this.Panico(" procedure, function ");
         }
   }else {
      System.err.println("Erro - qtd > tokens.getSize() em EscopoGlobal()");
    }    
}
   /**
     * <h2>  EscopoGlobal </h2><br/>
     * <p>A  fun��o EscopoGlobal, analisar  qual a parte do c�digo  :<br/>
     * se � metodo start,a declara��o de um variav�l, ou de uma constante <br/>
     * ou de um novo tipo criado pelo usu�rio .</p><br/>
     */
    private void EscopoGlobal() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            if(atual[1].trim().equals("start")) {
                this.Start();  
            } else if(atual[1].trim().equals("var")) {
                this.Var(); 
            } else if(atual[1].trim().equals("const")) {
                this.Const(); 
            } else if(atual[1].trim().equals("struct")) {
                this.Struct();  
            } else if(atual[1].trim().equals("typedef")) {
                this.NovoTipo();
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada n�o encontrada na linha "+linha.trim()+".\n";                
                this.Panico("start, struct, typedef, const, var");
            }
        } else {
            this.erros += "Erro  - Palavra Reservada n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";  
        }        
    }
    
   /**
     * <h2>Funcao </h2><br/>
     * <p>A fun��o Funcao,analisar se a cria��o de um fun��o no c�digo de um usu�rio <br/>
     * ocorre de  forma  correta  com a palavra resevada, e depois identificador. </p><br/>
     */
    private void Funcao() {
        if(this.qtd < this.tokens.getSize()) {   
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("function")) {
                this.semantico.declararFuncao(atual[2].replaceAll(">", " ").trim());
                this.qtd++;
                this.semantico.addContexto("function");
                this.Tipo();                               
                this.Declaracoes();                 
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");        
                    if(atual2[1].trim().equals("(")) {
                        this.qtd++;  
                        this.semantico.addEscopo();
                        this.BlocoFuncao();

                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '(' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Delimitador '(' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }                        
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro- Palavra Reservada 'function' n�o encontrada na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
                } 
        } else {
            this.erros += "Erro - Palavra Reservada 'function' n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }               
    }
    
  /**
    * <h2> BlocoFuncao </h2><br/>
    * <p>A fun��o BlocoFuncao,analisar se a conclus�o da assintuda da<br/>
    *  fun��o est� correta, e se os c�digos dentro do fun��o <br/>
    * est� correto e s�o a fun��o que analisar os parametros.</p><br/> 
    */
    private void BlocoFuncao() {
        
        if(this.qtd < this.tokens.getSize()) {
            
            int TokenInicio = 0;
            int TokenFim = 0;
            
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("bool") || atual[1].trim().equals("float") ||
                    atual[1].trim().equals("int") || atual[1].trim().equals("string") ||
                    atual[0].contains("Identificador")) {
                
                this.ListagemDeParametros();
                this.semantico.removerContexto();
                if(this.qtd < this.tokens.getSize()) {

                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(","); 
                    if(atual2[1].trim().equals(")")) {

                        this.semantico.removerContexto();
                        this.qtd++;
                        if(this.qtd < this.tokens.getSize()) {
                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");
                            if(atual3[1].trim().equals("{")) {
                                this.qtd++;    
                                TokenInicio = this.qtd;
                                this.Comandos();
                                if(this.qtd < this.tokens.getSize()) {
                                    String[] atual4 = this.tokens.getUnicToken(this.qtd).split(",");
                                    if(atual4[1].trim().equals("}")) {
                                        
                                        TokenFim = this.qtd;
                                        this.qtd++;
                                    } else {
                                        String linha = atual4[2].replaceAll(">", " ");
                                        this.erros += "Erro- Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                        this.Panico("struct, procedure, typedef, const, function, var, start");
                                    }
                                } else {
                                    this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";                                    
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro @5 - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro- Delimitador ')' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else if(atual[1].trim().equals(")")) {

                this.qtd++;
                if(this.qtd < this.tokens.getSize()) {

                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");
                    if(atual2[1].trim().equals("{")) {
                        
                        this.qtd++;  
                        TokenInicio = this.qtd;
                        this.Comandos();
                        if(this.qtd < this.tokens.getSize()) {

                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");
                            if(atual3[1].trim().equals("}")) {

                                TokenFim = this.qtd;
                                this.qtd++;
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro- Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro- Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Declara��o inv�lida na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
            }
            
            if(TokenInicio != 0 && TokenFim != 0) {
                this.getConteudoF(TokenInicio, TokenFim);
            }   
        } else {
            this.erros += "Erro @9 - Declara��o inv�lida na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    /**
      * <h2>Procedimento </h2><br/>
      * <p>A fun��o Procedimento,analisar se a cria��o de um procedimento no c�digo de um usu�rio <br/>
      * ocorre de  forma  correta  com a palavra resevada, e depois identificador. </p><br/>
     */
    private void Procedimento() {
        if(this.qtd < this.tokens.getSize()) {
            
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("procedure")) {

                this.semantico.procedimento(atual[2].replaceAll(">", " ").trim());                
                this.qtd++;
                if(this.qtd < this.tokens.getSize()) {

                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");        
                 
                    if(atual2[0].contains("Identificador")) {

                        this.semantico.addNomeProcedimento(atual2[1].trim(), atual2[2].replaceAll(">", " ").trim());
                        this.qtd++;
                        if(this.qtd < this.tokens.getSize()) {

                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");        
                            if(atual3[1].trim().equals("(")) {

                                this.qtd++;    
                                this.semantico.addEscopo();
                                this.BlocoProcedimento();

                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador '(' n�o encontrado na linha "+linha.trim()+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                              } 
                        } else {
                            this.erros += "Erro  - Delimitador '(' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                              }                               
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Identificador n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                      }
                } else {
                    this.erros += "Erro - Identificador n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                  }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro- Palavra Reservada 'procedure' n�o encontrada na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
              }  
        } else {
            this.erros += "Erro  - Palavra Reservada 'procedure' n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }              
    }
    
    /**
      * <h2>BlocoProcedimento </h2><br/>
      * <p>A fun��o BlocoProcedimento,analisar se a conclus�o da assintuda do,<br/>
      * procedimento est� correta e se os c�digos dentro do procedimento est� <br/>
      * correto e s�o a fun��o que analisar os parametros.</p><br/>
      */
    private void BlocoProcedimento() {
        
        if(this.qtd < this.tokens.getSize()) {
            int TokenInicio = 0;
            int TokenFim = 0;
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("bool") || atual[1].trim().equals("float") ||
                    atual[1].trim().equals("int") || atual[1].trim().equals("string") ||
                    atual[0].contains("Identificador")) {
                                                
                this.semantico.addContexto("procedure");
                this.ListagemDeParametros();
                this.semantico.removerContexto();
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");        

                    if(atual2[1].trim().equals(")")) {
                        this.qtd++;
                        if(this.qtd < this.tokens.getSize()) {
                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");        
                            if(atual3[1].trim().equals("{")) {                                                               
                                this.qtd++;   
                                TokenInicio = this.qtd;
                                this.Comandos();                                
                                if(this.qtd < this.tokens.getSize()) {

                                    String[] atual4 = this.tokens.getUnicToken(this.qtd).split(",");        
                                    if(atual4[1].trim().equals("}")) {      
                                        TokenFim = this.qtd;
                                        this.qtd++; 
                                    } else {
                                        String linha = atual4[2].replaceAll(">", " ");
                                        this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                        this.Panico("struct, procedure, typedef, const, function, var, start");
                                    }
                                } else {
                                    this.erros += "Erro- Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro- Delimitador ')' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }              
            } else if(atual[1].trim().equals(")")) {

                this.qtd++;
                if(this.qtd < this.tokens.getSize()) {

                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");        
                    if(atual2[1].trim().equals("{")) {
                        
                        this.qtd++;    
                        TokenInicio = this.qtd;
                        this.Comandos();
                        if(this.qtd < this.tokens.getSize()) {

                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");        
                            if(atual3[1].trim().equals("}")) {
                                
                                TokenFim = this.qtd;
                                this.qtd++;    
                            } else {

                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {

                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro  - Delimitador ')' n�o encontrado na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
            }
            
            if(TokenInicio != 0 && TokenFim != 0) {
                this.getConteudoP(TokenInicio, TokenFim);
            } 
        } else {
            this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";    
        }        
    }
    
    /**
      * <h2> NovoTipo</h2><br/>
      * <p>A fun��o NovoTipo, analisar a presen�a do typedef,<br/>
      * que � a cria��o de um novo tipo sem ser os tipos <br/>
      * primitivos como : int , float, bool e string.</p> <br/>
      * 
      * inspira��o na  gram�tica  da linguagem C .<br>
      */
    private void NovoTipo() {
        if(this.qtd < this.tokens.getSize()) { 
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("typedef")) {

                this.semantico.NovoTipo(atual[2].replaceAll(">", " ").trim());
                this.qtd++;                
                this.ConteudoDoTypedef();                
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'typedef' n�o encontrada na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
            } 
        } else {
            this.erros += "Erro- Palavra Reservada 'typedef' n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }               
    }
    
   /**
     * <h2> ConteudoDoTypedef </h2> <br/>
     * <p> A fun��o ConteudoDoTypedef , analisar  o cont�udo de um novo tipo,<br/> 
     * criado pelo usu�rio para  poder ser usado  no c�digo do usu�rio.<br>
     * 
     * inspira��o na  gram�tica  da linguagem  de program��o C. <br>
     * </p><br/>
     */ 
    private void ConteudoDoTypedef() {
    
        if(this.qtd < this.tokens.getSize()) {
            
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("bool") || atual[1].trim().equals("float") ||
                    atual[1].trim().equals("int") || atual[1].trim().equals("string") ||
                    atual[0].contains("Identificador")) {
                
                this.semantico.addContexto("typedef");
                this.Tipo();
                this.semantico.removerContexto();
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");        
                    if(atual2[0].contains("Identificador")) {
                        this.semantico.addNomeDeNovoTipo(atual2[1].trim(), atual2[2].replaceAll(">", " ").trim());
                        this.qtd++;
                        if(this.qtd < this.tokens.getSize()) {
                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");        
                            if(atual3[1].trim().equals(";")) {
                                this.qtd++;
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+this.getLinhaErro(qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro- Delimitador ';' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Identificador n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro- Identificador n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }              
            } else if(atual[1].trim().equals("struct")) {
                this.Struct();
                if(this.qtd < this.tokens.getSize()) {

                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");        
                    if(atual2[0].contains("Identificador")) {

                        this.qtd++;
                        if(this.qtd < this.tokens.getSize()) {

                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");        
                            if(atual3[1].trim().equals(";")) {

                                this.qtd++;
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro  - Delimitador ';' n�o encontrado na linha "+this.getLinhaErro(qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }  
                        } else {
                            this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                              
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro- Identificador n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Identificador n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Tipo n�o encontrado na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
            }
        } else {
            this.erros += "Erro - Tipo n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
     * <h2> Var </h2> <br/>
     * </p>A fun��o Var,analisar se a declara��o de variavel respeita a regra de ser,<br/>
     * a palavra resevada var{tipo identificador e/ou varios tipos e identificadores<br/>
     * separando a variaveis de mesmo tipo com , e de tipos diferentes com com ; }.</p><br/>
     */
    private void Var() {
       
        if(this.qtd < this.tokens.getSize()) {
            
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("var")) {

                this.semantico.Var(atual[2].replaceAll(">", " ").trim());
                this.qtd++;      
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");
                    if(atual2[1].trim().equals("{")) {

                        this.qtd++;     
                        this.semantico.addContexto("var");
                        this.ListaDeVariavel();
                        this.semantico.removerContexto();
                        if(this.qtd < this.tokens.getSize()) {

                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");
                            if(atual3[1].trim().equals("}")) {

                                this.qtd++;
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("false, (, return, print, ++, --, Numero, }, if, while, ;, {, true, "
                                        + "procedure, struct, typedef, function, start, !, scan, Cadeia_de_Caracteres, const, var, Identificador");
                            }
                        } else {
                            this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Panico("false, (, return, print, ++, --, Numero, }, if, while, ;, {, true, "
                            + "procedure, struct, typedef, function, start, !, scan, Cadeia_de_Caracteres, const, var, Identificador");
                    }
                } else {
                    this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'var' n�o encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, ++, --, Numero, }, if, while, ;, {, true, "
                    + "procedure, struct, typedef, function, start, !, scan, Cadeia_de_Caracteres, const, var, Identificador");
            }
        } else {
            this.erros += "Erro- Palavra Reservada 'var' n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
     * <h2> Const </h2> <br/>
     * </p>A fun��o Const,analisar se a declara��o de variavel respeita a regra de ser,<br/>
     * a palavra resevada const{tipo identificador e/ou varios tipos e identificadores <br/>
     * separando a variaveis de mesmo tipo com , e de tipos diferentes com com ; }.</p><br/>
     */
    private void Const() {
    
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("const")) {
                this.semantico.declararConst(atual[2].replaceAll(">", " ").trim());
                this.qtd++;
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");        
                    if(atual2[1].trim().equals("{")) {
                        this.qtd++;
                        this.semantico.addContexto("const");
                        this.ListaDeVariavel();
                        this.semantico.removerContexto();
                        if(this.qtd < this.tokens.getSize()) {

                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");        
                            if(atual3[1].trim().equals("}")) {
                                this.semantico.verificarInicializacaoConst(atual[2].replaceAll(">", " ").trim());
                                this.qtd++;
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro- Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'const' n�o encontrada na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
            }
        } else {
            this.erros += "Erro - Palavra Reservada 'const' n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
   /**
     * <h2>Struct</h2><br/>
     * <p>A fun��o Struct,analisar se o a cria��o de bloco de variaveis,ocorre<br/> 
     * de forma correta com a palavra struct e o identificador mais as declara��es.</p><br/>
     */
    private void Struct() {
        
        if(this.qtd < this.tokens.getSize()) {
            
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("struct")) {
                this.semantico.Blocos(atual[2].replaceAll(">", " ").trim());
                this.qtd++;    
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");        
                    if(atual2[0].contains("Identificador")) {
                        this.semantico.addNomeBloco(atual2[1].trim(), atual2[2].replaceAll(">", " ").trim());
                        this.qtd++;
                        this.semantico.addEscopo();
                        this.structX();
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Identificador n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
                    }
                } else {
                    this.erros += "Erro - Identificador n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'struct' n�o encontrada na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
            }
        } else {
            this.erros += "Erro- Palavra Reservada 'struct' n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
   /**
      * <h2> structX</h2><br/>
      * <p>A fun��o structX,analisar o cont�udo da declara��o de de um bloco<br/>
      *  de variaveis e uma heran�a que pode ocorre com a palavra extends.</p><br/>
     */
    private void structX() {
        if(this.qtd < this.tokens.getSize()) { 
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("{")) { 
                this.qtd++;
                this.ListaDeVariavel();
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");        
                    if(atual2[1].trim().equals("}")) { 
                        this.qtd++;
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
                    }
                } else {
                    this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }
            } else if(atual[1].trim().equals("extends")) {
                this.qtd++;
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");        
                    if(atual2[0].contains("Identificador")) {
                        this.semantico.heranca(atual2[1].trim(), atual2[2].replaceAll(">", " ").trim());
                        this.qtd++;
                        if(this.qtd < this.tokens.getSize()) {
                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");        
                            if(atual3[1].trim().equals("{")) {                            
                                this.qtd++;
                                this.ListaDeVariavel();
                                if(this.qtd < this.tokens.getSize()) {
                                    String[] atual4 = this.tokens.getUnicToken(this.qtd).split(",");        
                                    if(atual4[1].trim().equals("}")) {
                                        this.qtd++;
                                    } else {
                                        String linha = atual4[2].replaceAll(">", " ");
                                        this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+linha.trim()+".\n";
                                        this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
                                    }
                                } else {
                                    this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+linha.trim()+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
                            }
                        } else {
                            this.erros += "Erro- Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Identificador n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
                    }
                } else {
                    System.err.println("Erro - qtd > tokens.getSize() em structX()");
                    this.erros += "Erro - Identificador n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro- Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
            }
        } else {
            this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
     * <h2> ListagemDeParametros</h2> <br/>
     * <p>A fun��o ListagemDeParametros, chama duas fun��es para realisar ,<br/>
     * a analise  dos parametros  informados pelo usu�rio.</p> <br/>
     */
    private void ListagemDeParametros() {
        this.ListaDeDeclaracaoDeParametro();
        this.DeclaracaoDeParametro();        
    }
   /**
     * <h2> DeclaracaoDeParametro</h2><br/>
     * <p>A fun��o DeclaracaoDeParametro realizar <br/>
     * o processo de analisar parametros e chama <br/>  
     * a fun��o <a>ListaDeDeclaracaoDeParametro()</a><br/>
     * para completar  a analise.</p><br/>
    */
    private void DeclaracaoDeParametro() {   
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[0].contains("Delimitador") && atual.length == 4) { 
               this.qtd++;
                this.ListaDeDeclaracaoDeParametro();
                this.DeclaracaoDeParametro();
            }
        } 

    }
    
     /**
      * <h2> ListaDeDeclaracaoDeParametro </h2><br/>
      * <p>A fun��o ListaDeDeclaracaoDeParametro,analisar a lista de declara��o <br/>
      * de parametros que o usu�rio pode fazer e utilizar.</p> <br>
      */
    private void ListaDeDeclaracaoDeParametro() {
        this.Tipo();
        this.Declaracoes();
    }
    
    /**
     * <h2> ListaDeVariavel</h2><br/>
     * <p> A fun��o ListaDeVariavel, faz a chama de <br/>
     * dual fun��es  para analista a lista de <br/>
     * variavaeis </p></br>
     *   
     */
    private void ListaDeVariavel() {
    
        this.DeclararVariavel();
        this.ListaDeDeclararVariavel();    
    }
    
    /**
      * <h2> ListaDeDeclararVariavel </h2> <br/>
      * <p> A fun��o ListaDeDeclararVariavel, realizar o <br/>
      * processo parecido com a fun��o DeDeclararVariavel,<br/>
      * s�  analisar mais variaveis. </p><br/>
      */
    private void ListaDeDeclararVariavel() {
        if(this.qtd < this.tokens.getSize()) {   
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            if(atual[1].trim().equals("bool") || atual[1].trim().equals("float") || 
                    atual[1].trim().equals("int") || atual[1].trim().equals("string") ||
                    atual[0].contains("Identificador")) {

                this.DeclararVariavel();
                this.ListaDeDeclararVariavel();
            }
        } 
    }
    
    /**
       * 
       * <h2> DeclararVariavel </h2> <br/>
       * 
       * <p>A fun��o DeclararVariavel,analisar as v�rias </br>
       * declara��es que o usu�rio pode fazer.</p> <br/>
     */
    private void DeclararVariavel() {
        this.Tipo();
        this.ListaDeDeclaracoes();
        if(this.qtd < this.tokens.getSize()) {            
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            if(atual[1].trim().equals(";")) {
                this.qtd++;          
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+this.getLinhaErro(qtd-1)+".\n";
                this.Panico("}, bool, float, int, string, Identificador");
            }
        } else {
            this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }          
    }
    

    private void ListaDeDeclaracoes() {
            
        this.Declara();
        this.ListaDeDeclaracoesX();
    } 
    
    private void ListaDeDeclaracoesX() {
    
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[0].contains("Delimitador") && atual.length == 4) { 
                this.qtd++;
                this.Declara();
                this.ListaDeDeclaracoesX();
            }
        } 
    }
    
    /**
      *<h2>Declara</h2> <br/>
      * <p>A fun��o  Declara , chama  duas  fun��es para concluir a <br/>
      * analiser de  declara��es do c�digo  do usu�rio .</p><br/>
     */
    private void Declara() {
    
        this.Declaracoes();
        this.Igualdade();
    }
    
    /**
     * <h2> Igualdade </h2><br/>
     * <p>A fun��a Igualdade,analisar se tem uma =  entre express�es. </p> <br/>
     */
    private void Igualdade() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            if(atual[1].trim().equals("=")) {
                this.qtd++;   
                this.semantico.setInitializer(true);
                this.Inicializar();
                this.semantico.setInitializer(false);
            }
        } 
    }
    
    /**
      * <h2>Inicializar </h2><br/>
      * <p> A fun��o Inicializar,analisar o possiv�l inicio que pode ter ,<br/>
      *  alguns  c�digos do usu�rio. </p><br/>
     */
    private void Inicializar() {
        if(this.qtd < this.tokens.getSize()) { 
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[0].contains("Numero") || atual[1].trim().equals("false") ||
                    atual[1].trim().equals("true") || atual[0].contains("Cadeia_de_Caracteres") ||
                    atual[1].trim().equals("(") || atual[0].contains("Identificador") ||
                    atual[1].trim().equals("!") || atual[1].trim().equals("++") ||
                    atual[1].trim().equals("--")) {
                this.VariaveisX();
            } else if(atual[1].trim().equals("{")) {
                this.qtd++;
                this.ListaDeInicializar();
                this.Concluir();
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Erro de inicializa��o na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                this.Desespero(";, }");
            }
        } else {
            this.erros += "Erro - Erro de inicializa��o na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
     * <h2> Concluir </h2><br/>
     *<p> A fun��o Concluir , verificar se tem ,  ou }. </p><br/>
     */
    private void Concluir() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            if(atual[1].trim().equals("}")) {
                this.qtd++;          
            } else if(atual[0].contains("Delimitador") && atual.length == 4) {
                this.qtd++; 
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");
                    if(atual2[1].trim().equals("}")) {
                        this.qtd++;    
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Desespero("}, ;");                        
                    }
                } else {
                    this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                this.Desespero("}, ;");
            }
        } else {
            this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
   /**
     * <h2>ListaDeInicializar </h2><br/>
     * <p> A fun��o ListaDeInicializar,chama  a <a>Inicializar</a> e <br/>
     * a  fun��o <a> ListaDeInicializarX</a> para   verificar as possiveis 
     * lista de inicializa��es que o c�digo  do us�rio  pode ter . </p><br/>
    */
    private void ListaDeInicializar() {
    
        this.Inicializar();
        this.ListaDeInicializarX();
    }
    
   /**
     *<h2>ListaDeInicializarX </h2><br/>
     * <p> A fun��o ListaDeInicializarX, analisar um <br/>
     * possivel lista com varios inicio que o <br/>
     * c�digo do usu�rio pode ter. </p><br/>
    */
    private void ListaDeInicializarX() {
        if(this.qtd < this.tokens.getSize()) {   
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            if(atual[0].contains("Delimitador") && atual.length == 4) {
                this.qtd++;
                this.Inicializar();
                this.ListaDeInicializarX();
            }
        }
    }
    
    /**
      * <h2>Declaracoes </h2> <br/>
      * <p>A  fun��o Declaracoes,  analisar  se o usu�rio criou um identificador <br>
      *  de um array <a>DeclararVariavelX</a> para continuar  a analiser .</p><br/>
      * 
      */
    private void Declaracoes() {
        if(this.qtd < this.tokens.getSize()) { 
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            if(atual[0].contains("Identificador")) {
                this.semantico.addNome(atual[1].trim(), atual[2].replaceAll(">", " ").trim());
                this.qtd++;                
                this.DeclararVariavelX();
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro- Identificador n�o encontrado na linha "+linha.trim()+".\n";
                this.Desespero("=, ;, ), (");
            }
        } else {
            this.erros += "Erro- Identificador n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    } 
    
    /**
      * <h2>DeclararVariavelX </h2> <br/>
      * <p>A  fun��o DeclararVariavelX,  analisar  se o usu�rio criou um abrir  cochetes  <br>
      *  de um array <a>DeclararVariavelXX</a> para continuar  a analiser .</p><br/>
      */
    private void DeclararVariavelX() {
        if(this.qtd < this.tokens.getSize()) {  
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            if(atual[1].trim().equals("[")) {
                this.qtd++;
                this.DeclararVariavelXX();
            }
        } 

    }
    
    /**
      * <h2>DeclararVariavelXX</h2> <br/>
      * <p>A  fun��o DeclararVariavelXX,  analisar  se o usu�rio definiu um tipo , um numero 
      * um cadeia de caracteres ou identificador  e se o usu�rio fechou cochetes <br/>
      * de um array <a>DeclararVariavelXX</a> para continuar  a analiser .</p><br/>
      */
    private void DeclararVariavelXX() {
        if(this.qtd < this.tokens.getSize()) {  
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            if(atual[1].trim().equals("false") || atual[1].trim().equals("true") ||
                    atual[1].trim().equals("(") || atual[1].trim().equals("!") ||
                    atual[1].trim().equals("++") || atual[1].trim().equals("--") ||
                    atual[0].contains("Numero") || atual[0].contains("Cadeia_de_Caracteres") ||
                    atual[0].contains("Identificador")) {
                    this.ExpressaoCondicional();
                    if(this.qtd < this.tokens.getSize()) {
                        String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");
                        if(atual2[1].trim().equals("]")) {
                            this.qtd++;
                            this.DeclararVariavelX();
                        } else {
                            String linha = atual2[2].replaceAll(">", " ");
                            this.erros += "Erro - Delimitador ']' n�o encontrado na linha "+linha.trim()+".\n";
                            this.Desespero("=, ;, ), (");
                        }
                    } else {
                        this.erros += "Erro- Delimitador ']' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    }                
            } else if(atual[1].trim().equals("]")) {
                this.qtd++;
                this.DeclararVariavelX();            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Delimitador ']' n�o encontrado na linha "+linha.trim()+".\n";
                this.Desespero("=, ;, ), (");
            }
        } else {
            this.erros += "Erro - Delimitador ']' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
     * <h2> Comando</h2> <br/>
     *<p> A fun��o Comando, analisar se � s�o os comandos possivel de acontece em um c�digo como :<br/>
     * um la�o while do tipo enquanto, se alguma express�o, se um comando print para escrever <br/>
     * para usu�rio,um scan e capturar  dados do usu�rio ou um retorno de alguma coisa .</p> <br/> 
     */
    private void Comando() {
        if(this.qtd < this.tokens.getSize()) { 
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            if(atual[1].trim().equals("while")) {
                this.While();   
            } else if(atual[1].trim().equals("false") || atual[1].trim().equals(";") ||
                    atual[1].trim().equals("true") || atual[1].trim().equals("(") ||
                    atual[1].trim().equals("!") || atual[1].trim().equals("++") ||
                    atual[1].trim().equals("--") || atual[0].contains("Numero") ||
                    atual[0].contains("Cadeia_de_Caracteres") || atual[0].contains("Identificador")) {
                
                this.ListaDeExpressoes();     
            } else if(atual[1].trim().equals("{")) {
                this.Abertura();
    
            } else if(atual[1].trim().equals("print")) {
                this.Print();
     
            } else if(atual[1].trim().equals("scan")) {
                this.Scan();
   
            } else if(atual[1].trim().equals("if")) {
                this.IfThen();
 
            } else if(atual[1].trim().equals("return")) {
                this.RetornoExpressao();
                
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Declara��o esperada n�o encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;, "
                        + "{, scan, true, Cadeia_de_Caracteres, var, Identificador");
            }
        } else {
            this.erros += "Erro - Declara��o esperada n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
      * <h2> Comandos </h2><br/>  
      * <p>A fun��o comandos , analisar  qual o tipo  de  a��o que usu�rio<br/>
      * escrveu no seu c�digo para  poder ocorre no seu c�digo. </p> <br/>
      * 
      */
    private void Comandos() {
        if(this.qtd < this.tokens.getSize()) {
            
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("false") || atual[1].trim().equals("(")
                    || atual[1].trim().equals("return") || atual[1].trim().equals("print")
                    || atual[1].trim().equals("!") || atual[1].trim().equals("++")
                    || atual[1].trim().equals("--") || atual[0].contains("Numero")
                    || atual[1].trim().equals("if") || atual[1].trim().equals("while")
                    || atual[1].trim().equals(";") || atual[1].trim().equals("{")
                    || atual[1].trim().equals("scan") || atual[1].trim().equals("true")
                    || atual[0].contains("Cadeia_de_Caracteres") || atual[0].contains("Identificador")) {

                this.Comando();
                this.ComandosX();

            } else if(atual[1].trim().equals("var")) {
                this.Var(); 
                this.ComandosX(); 
                
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro- Declara��o inv�lida na linha "+linha.trim()+".\n";
                this.Panico("}");
            }
        } else {
            this.erros += "Erro - Declara��o inv�lida na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    } 
    
    /**
      * <h2> ComandosX </h2><br/>
      * <p>A fun��o ComandosX, realizar um a��o pareceida que comando, <br/>
      * por�m consegue concluir a analiser desta parte importante do<br/>
      * c�digo que usu�rio escreveu.</p> <br/>
      */
    private void ComandosX() {
    
        if(this.qtd < this.tokens.getSize()) {
            
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");        
            if(atual[1].trim().equals("false") || atual[1].trim().equals("(")
                    || atual[1].trim().equals("return") || atual[1].trim().equals("print")
                    || atual[1].trim().equals("!") || atual[1].trim().equals("++")
                    || atual[1].trim().equals("--") || atual[0].contains("Numero")
                    || atual[1].trim().equals("if") || atual[1].trim().equals("while")
                    || atual[1].trim().equals(";") || atual[1].trim().equals("{")
                    || atual[1].trim().equals("scan") || atual[1].trim().equals("true")
                    || atual[0].contains("Cadeia_de_Caracteres") || atual[0].contains("Identificador")) {

                this.Comando();
                this.ComandosX();

            } else if(atual[1].trim().equals("var")) {

                this.Var(); 
                this.ComandosX(); 
            } 
        } 
    }
    
    /**
     * <h2> Start </h2> <br/>
     *<p> A fun��o Start,analisar se o m�todo principal start foi declarado, <br/>
     * para que possa ocorrer a execu��o da compila��o do c�digo do usu�rio.</p><br/>
     */
    private void Start() {
            
        if(this.qtd < this.tokens.getSize()) {     
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            
            if(atual[1].trim().equals("start")) {                  
                this.semantico.declararStart(atual[2].replaceAll(">", " ").trim());
                this.qtd++;  
                
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");  
                    
                    if(atual2[1].trim().equals("(")) {
                        this.qtd++;
                        
                        if(this.qtd < this.tokens.getSize()) {
                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(","); 
                            
                            if(atual3[1].trim().equals(")")) {                    
                                this.qtd++;
                                
                                if(this.qtd < this.tokens.getSize()) {
                                    String[] atual4 = this.tokens.getUnicToken(this.qtd).split(","); 
                                    
                                    if(atual4[1].trim().equals("{")) {
                                        this.qtd++;
                                        this.semantico.addEscopo("start", "start");
                                        this.Comandos();  
                                        
                                        if(this.qtd < this.tokens.getSize()) {
                                            String[] atual5 = this.tokens.getUnicToken(this.qtd).split(","); 
                                            
                                            if(atual5[1].trim().equals("}")) {                                                   
                                                this.qtd++;   
                                                
                                                
                                            } else {                 
                                                String linha = atual5[2].replaceAll(">", " ");
                                                this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+linha.trim()+".\n";
                                                this.Panico("struct, procedure, typedef, const, function, var, start");
                                                
                                                
                                            }
                                        } else {
                                            this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                        
                                        }                        
                                    } else {
                                        String linha = atual4[2].replaceAll(">", " ");
                                        this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+linha.trim()+".\n";
                                        this.Panico("struct, procedure, typedef, const, function, var, start");
                                        
                                        
                                    }
                                } else {
                                    this.erros += "Erro- Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro- Delimitador ')' n�o encontrado na linha "+linha.trim()+".\n";   
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                                
                                
                            }
                        } else {
                            this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";   

                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '(' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                        
                    }
                } else {
                    this.erros += "Erro - Delimitador '(' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    
                    
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'Start' esperada na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
                
            }
        } else {
            this.erros += "Erro - Palavra Reservada 'Start' esperada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
        }                
    }
    
    /**
     * <h2>Print </h2> <br/>
     * <p>A fun��o Print,anlisar se ocorre o print,usando para escrever na tela <br/>
     * do usu�rio dados e/ou informa��es(forma de interatividade com usu�rio).</p></br>
     */
    private void Print() { 
        if(this.qtd < this.tokens.getSize()) {  
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  
            
            if(atual[1].trim().equals("print")) {
                this.qtd++;
                
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");  
                    
                    if(atual2[1].trim().equals("(")) {
                        this.qtd++;
                        this.Variaveis();

                        if(this.qtd < this.tokens.getSize()) {
                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");  
                              
                            if(atual3[1].trim().equals(")")) {
                                this.qtd++;
                                
                                if(this.qtd < this.tokens.getSize()) {
                                    String[] atual4 = this.tokens.getUnicToken(this.qtd).split(",");  

                                    if(atual4[1].trim().equals(";")) {
                                        this.qtd++;
                                        
                                        
                                    } else {
                                        String linha = atual4[2].replaceAll(">", " ");
                                        this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+linha.trim()+".\n";
                                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                                    }
                                } else {
                                    this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+linha.trim()+".\n";
                                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                                
                            }
                        } else {
                            this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                       
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '(' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                        
                    }
                } else {
                    this.erros += "Erro - Delimitador '(' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
               
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'print' n�o encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro- Palavra Reservada 'print' n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        
        }        
    } 
    
    /**
      * <h2> Scan  </h2> <br/>
      *<p>A fun��o Scan,anlisar se ocorre o scan,usando para capturar dados do usu�rio,<br/>
      *e colocar o valores capturados em variaveis(forma de interatividade com usu�rio).</p></br>
      */
    private void Scan() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  
            
            if(atual[1].trim().equals("scan")) {
                this.qtd++;
                
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");  
                    
                    if(atual2[1].trim().equals("(")) {    
                        this.qtd++;
                        this.Variaveis();
                        
                        if(this.qtd < this.tokens.getSize()) {
                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");  

                            if(atual3[1].trim().equals(")")) {
                                this.qtd++;
                                
                                if(this.qtd < this.tokens.getSize()) {
                                    String[] atual4 = this.tokens.getUnicToken(this.qtd).split(",");  
                                    
                                    if(atual4[1].trim().equals(";")) {
                                        this.qtd++;
                                        
                                    } else {

                                        String linha = atual4[2].replaceAll(">", " ");
                                        this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+linha.trim()+".\n";
                                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                                    }
                                } else {
                                    this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+linha.trim()+".\n";
                                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                                
                                
                            }
                        } else {
                            this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                       
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '(' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                        
                    }
                } else {
                    this.erros += "Erro- Delimitador '(' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
               
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'scan' n�o encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
                
            }
        } else {
            this.erros += "Erro - Palavra Reservada 'scan' n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
      * <h2> While</h2><br/>
      * <p>A fun��o while,analisar se � o la�o de repeti��o enquanto(While).</p><br/>
     */
    private void While() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(","); 
            
            if(atual[1].trim().equals("while")) {
                this.qtd++;
                
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");  
                    
                    if(atual2[1].trim().equals("(")) {
                        this.qtd++;
                        this.Expressoes();
                        
                        if(this.qtd < this.tokens.getSize()) {
                            String[] atual3 = this.tokens.getUnicToken(this.qtd).split(",");  
                     
                            if(atual3[1].trim().equals(")")) {
                                this.qtd++;
                                this.Comando();
                                
                                
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+linha.trim()+".\n";
                                this.Panico("}, false, (, return, print, else, !, ++, --, Numero, if,"
                                        + " while, ;, {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                                
                            }
                        } else {
                            this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '(' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if,"
                                        + " while, ;, {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                        
                    }
                } else {
                    this.erros += "Erro - Delimitador '(' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'while' n�o encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if,"
                                        + " while, ;, {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro - Palavra Reservada 'while' n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        
        }        
    }
    
    /**
      * <h2>IfThen </h2> <br/>
      * <p>A fun��o IfThen,analisar se acontce um <br/>
      * o if(que � muito usado em codifica��o),com <br/>
      * a regra que a linguem exigir com then.</p> <br/>
     */
    private void IfThen() {
        if(this.qtd < this.tokens.getSize()) { 
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  

            if(atual[1].trim().equals("if")) {
                this.qtd++;
                this.Expressoes();
                
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");  

                    if(atual2[1].trim().equals("then")) {
                        this.qtd++;
                        this.Comando();
                        this.Else();
                        
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Palavra Reservada 'then' n�o encontrada na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;, {,"
                                + " scan, true, Cadeia_de_Caracteres, var, Identificador");
                        
                    }
                } else {
                    this.erros += "Erro- Palavra Reservada 'then' n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
               
                }
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'if' n�o encontrado na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;, {,"
                                + " scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro - Palavra Reservada 'if' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }    
    
    /**
     * <h2>Else </h2><br/>
     *  <p>A fun��o Else, analisar se ocorre o else.</p><br/>
     */ 
    private void Else() {
        if(this.qtd < this.tokens.getSize()) { 
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  

            if(atual[1].trim().equals("else")) {
                this.qtd++;
                this.Comando();
                
            }
        } 
    }
    
    /**
     * <h2> RetornoExpressao </h2> <br/>
     * <p>A fun��o RetornoExpressao,analisar � o retorno de uma express�o, para quem chamou.</p> <br/>
     */
    private void RetornoExpressao() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  
            
            if(atual[1].trim().equals("return")) {
                this.qtd++;
                this.Expressoes();
                
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");  

                    if(atual2[1].trim().equals(";")) {
                        this.qtd++;
                        
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificacor");
                    }
                } else {
                    this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'return' n�o encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificacor");
                
            }
        } else {
            this.erros += "Erro - Palavra Reservada 'return' n�o encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
            
        }        
    }
    
    /**
      * <h2> Abertura</h2><br/>
      * <p>A fun��o Abertura, verificar se ocorre abertura de {. </p> <br/> 
      */
    private void Abertura() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  

            if(atual[1].trim().equals("{")) {
                this.qtd++;
                this.Aberturalf();
                
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro  - Delimitador '{' n�o encontrado na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                        + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro - Delimitador '{' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
        }                
    } 
    
    /**
     *  <h2> Aberturalf</h2><br/>
     *  <p>fun��o  verificra abertura de if.</p><br/>
     */
    private void Aberturalf() {
        if(this.qtd < this.tokens.getSize()) { 
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  

            if(atual[1].trim().equals("}")) {
                this.qtd++;

            } else if(atual[1].trim().equals("false") || atual[1].trim().equals("(") ||
                    atual[1].trim().equals("return") || atual[1].trim().equals("print") ||
                    atual[1].trim().equals("!") || atual[1].trim().equals("++") ||
                    atual[1].trim().equals("--") || atual[1].trim().equals("var") ||
                    atual[1].trim().equals("if") || atual[1].trim().equals("while") ||
                    atual[1].trim().equals(";") || atual[1].trim().equals("{") || 
                    atual[1].trim().equals("scan") || atual[1].trim().equals("true") ||
                    atual[0].contains("Cadeia_de_Caracteres") || atual[0].contains("Identificador")) {
                this.Comandos();

                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");  

                    if(atual2[1].trim().equals("}")) {
                        this.qtd++;
                        
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador"); 
                        
                    }
                } else {
                    this.erros += "Erro - Delimitador '}' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Declara��o inv�lida na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro - Declara��o inv�lida na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }       
    }
    
    /**
      * <h2>ListaDeExpressoes </h2> <br/>
      * <p>A fun��o ListaDeExpressoes,analisar uma possiv�l lista de express�es que <br/>
      * usu�rio pode ter criado no c�digo separadas com o delimitador ;.</p><br/>
      */
    private void ListaDeExpressoes() {
    
        if(this.qtd < this.tokens.getSize()) { 
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  

            if(atual[1].trim().equals(";")) {
                this.semantico.setIgnorarAtribuicao(false);
                this.qtd++;

            } else if(atual[1].trim().equals("false") || atual[1].trim().equals("true") ||
                    atual[1].trim().equals("(") || atual[1].trim().equals("!") ||
                    atual[1].trim().equals("++") || atual[1].trim().equals("--") ||
                    atual[0].contains("Numero") || atual[0].contains("Cadeia_de_Caracteres") ||
                    atual[0].contains("Identificador")) {
                this.Expressoes();
                
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");  
                    
                    if(atual2[1].trim().equals(";")) {
                        this.semantico.setIgnorarAtribuicao(false);
                        this.qtd++;
                        
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                    }
                } else {
                    this.erros += "Erro- Delimitador ';' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro  - Delimitador ';' n�o encontrado na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro - Delimitador ';' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
        }        
    }
    
   /**
      * <h2>Expressoes </h2> <br/>
      * <p>A fun��o a Expressoes,chama as fun��es variaveisX e ExpressoesX,<br/>
      * para poder analisar as possiv�is express�es que pode acontcer no c�digo.</p><br/>
     */
    private void Expressoes() {
    
        this.VariaveisX();
        this.ExpressoesX();
    }   
    
    /**
     * <h2>ExpressoesX </h2> <br/>
     * <p>A fun��o ExpressoesX,analisar uma possiv�l lista de express�es que <br/>
     * usu�rio pode ter criado no c�digo .</p><br/>
     */
    
    private void ExpressoesX() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            String linhaS = atual[2].replaceAll(">", " ").trim();

            if(atual[0].contains("Delimitador") && atual.length == 4) {
                this.semantico.addValor(",", linhaS, "Op");
                this.qtd++;
                this.VariaveisX();
                this.ExpressoesX();
                
            }

        } 
    }
    
    /**
     * <h2>VariaveisX </h2> <br/>
     *�<p> A fun��o VariaveisX,chama as fun��es que verifica express�o<br/>
     * condicional e atribui��o.</p> </br>
     */
    private void VariaveisX() {
        this.ExpressaoCondicional();
        this.Atribuicao();
    }    
    
    /**
     * <h2> Atribuicao </h2><br/>
     *<p> A fun��o Atribuicao,analisar se � operador relacional =(igual ),<br/>
     * que � usado com  uma forma de abriu um valor ou resultado.</p><br/>
     */
    private void Atribuicao() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  

            if(atual[1].trim().equals("=")) {
                this.qtd++;
                
                if(this.semantico.isInitializer()) { 
                    this.semantico.addValor("=", atual[2].replaceAll(">", " ").trim(), "Op");
                    this.ExpressaoCondicional();
                    this.Atribuicao();
                    
                } else {
                    this.ExpressaoCondicional();
                    this.Atribuicao();
                    this.semantico.setInitializer2(false);
                    
                }                
            }
        } 
    }
    
    /**
      * <h2>  ExpressaoCondicional </h2><br/>  
      * <p>A fun��o  ExpressaoCondicional, chama <br/>
      * a express�o logica  quando a express�o <br/>
      * condicional  for  logica.</p> <br/>
      */
    private void ExpressaoCondicional() {
        this.ExpressaoLogica();
    }    
    
    /**
     * <h2> ExpressaoLogica</h2> <br/>
     * </p>A fun��o ExpressaoLogica,chama as fun��es que v�o verificar as </br> 
     * express�es  possiv�l do que o usu�rio  pode ter criado.</p> <br/>
     */
    private void ExpressaoLogica() {
    
        this.ExpressaoRelacionalOu();
    }   
    
    /**
     * <h2>ExpressaoRelacionalOu</h2><br/>
     *<p> A fun��o ExpressaoRelacionalOu realiza analisar <br/> 
     * se � uma expressao relacional ou ( no caso ou  =>  || ) .</p> <br/> 
     */
    private void ExpressaoRelacionalOu() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  
            String linhaS = atual[2].replaceAll(">", " ");

            if(atual[1].trim().equals("||")) {
                this.semantico.addValor("||", linhaS, "Op");
                this.qtd++;
                this.ExpressaoRelacionalOu();
                
            }

        } 

    }

    /**
     * <h2>ExpressaoRelacionalAnd</h2><br/>
     *<p> A fun��o ExpressaoRelacionalAnd realiza analisar <br/> 
     * se � uma expressao relacional and ( no caso EE => &&) .</p> <br/> 
     */
    private void ExpressaoRelacionalAnd() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  
            String linhaS = atual[2].replaceAll(">", " ").trim();
            
            if(atual[1].trim().equals("&&")) {
                this.semantico.addValor("&&", linhaS, "Op");
                this.qtd++;
                this.ExpressaoRelacionalAnd();
                
            }

        } 

    }

    
    /** 
     * <h2>ExpressaoRelacionalNot</h2><br/>
     *<p> A fun��o ExpressaoRelacionalNot realiza analisar <br/> 
     * se � uma expressao relacional not ( no caso n�o => !=) .</p> <br/>  
     */
    private void ExpressaoRelacionalNot() {
    
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  
      
            if(atual[1].trim().equals("!=") || atual[1].trim().equals("==")) {
                this.IgualdadeNegacao();
                this.ExpressaoRelacionalNot();
                
            }

        } 

    }
 
  
    
    /**
     * <h2>ComparacaoRelacional </h2> <br>
     * <p>A fun��o aComparacaoRelacional, realizar a analise de uma compara��o<br/>
     * relacional  que pode ter no c�digo  do usu�rio .</p></br>
     */
    private void ComparacaoRelacional() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  

            if(atual[1].trim().equals("<") || atual[1].trim().equals("<=") ||
                    atual[1].trim().equals(">") || atual[1].trim().equals(">=")) {


                this.ComparacaoRelacional();
            }

        } 

    }
    
 
    
  /**
   *<h2> OpAdicaoSubtracao </h2><br/>
   * <p>fun��o OpAdicaoSubtracao, analisar uma possiv�l opera��o de adi��o<br/>
   * ou subtra��o e chama outras fun��es para realizar outras possiveis <br/>
   * opera��es opera��o que pode acontecer  no c�digo do usu�rio .</p>
    */
    private void OpAdicaoSubtracao() {
        if(this.qtd < this.tokens.getSize()) { 
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  

            if(atual[1].trim().equals("-") || atual[1].trim().equals("+")) {
                this.AdicaoSubtracao();
                this.OperacaoComExpressao();
                this.OpAdicaoSubtracao();

            }

        } 
    }
    
    /**
     * <h2>OperacaoComExpressao</h2> <br/>
     * <p>a fun��o OperacaoComExpressao,chama as fun��es para as possiveis opera��es<br> 
     * pode ocorrer  com  as express�es .</p><br/>
     */
    private void OperacaoComExpressao() {   
        this.OperacaoEmExpressoes();
        this.OpEmExpressoesMultDiv();
        
    } 
    
   /**
      * <h2> OpEmExpressoesMultDiv </h2><br/>
      * <p>A fun��o OpEmExpressoesMultDiv,realizar o mesmo processo <br/>
      * que a <a>OperacaoEmExpressoes </a> analisar a possovel <br/>
      * opera��o de multiplica��o e Divis�o e chama a realizas<br/>
      * as outras an�lises. </p><br/>
      */
    private void OpEmExpressoesMultDiv() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  
         
            if(atual[1].trim().equals("*") || atual[1].trim().equals("/")) {
                this.OperacaoMultDiv();
                this.OperacaoEmExpressoes();
                this.OpEmExpressoesMultDiv();
                
            }

        } 

    }
    
   /**
     * <h2> OperacaoEmExpressoes </h2><br/>
     * <p>A fun��o OperacaoEmExpressoes, analisar as possiveis opera��es ,<br/>
     * que pode acontcer com  expressoes. </p><br/>
     */ 
    private void OperacaoEmExpressoes() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  

            if(atual[1].trim().equals("!") || atual[1].trim().equals("++") || atual[1].trim().equals("--")) {
                this.OperadoresIncrementos();
                this.OperacaoEmExpressoes();
   
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Express�o inv�lida na linha "+linha.trim()+".\n";
                this.Desespero("-, +, then, *, ), <=, ||, ==, &&, >, =, ], }, <, !=, >=, ;, /");
            }
        } else {
            
            this.erros += "Erro  - Express�o inv�lida na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }   

    
   /**
     * <h2> TiposLiteral <h1/> 
     * <p> A fun��o TiposLiteral, analisar se �:<br/>
     * um identificador, um n�mero,uma cadeira de caracteres(string Lietral),<br/> 
     * do tipo boolean true (verdade)ou false(false).</p><br/>
    */
    private void TiposLiteral() { 
        if(this.qtd < this.tokens.getSize()) {     
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            String linhaS = atual[2].replaceAll(">", " ").trim();
          
            if(atual[0].contains("Identificador")){
                this.semantico.addValor(atual[1].trim(), linhaS, "Identificador");
                this.qtd++;

             
            } else if(atual[0].contains("Numero")) {
                this.semantico.addValor(atual[1].trim(), linhaS, "Op");
                this.qtd++;

            } else if(atual[0].contains("Cadeia_de_Caracteres")) {
                this.semantico.addValor(atual[1].trim(), linhaS, "Op");
                this.qtd++;

            } else if(atual[1].trim().equals("true")) {
                this.semantico.addValor(atual[1].trim(), linhaS, "Op");
                this.qtd++;
 
            } else if(atual[1].trim().equals("false")) {
                this.semantico.addValor(atual[1].trim(), linhaS, "Op");
                this.qtd++;

            } else if(atual[1].trim().equals("(")) {
                this.semantico.addValor("(", linhaS, "Op");
                this.qtd++;
                this.Expressoes();
                
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");
                    String linhaS2 = atual[2].replaceAll(">", " ");
                   
                    if(atual2[1].trim().equals(")")) {
                        this.semantico.addValor(")", linhaS, "Op");
                        this.qtd++;
                        
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Desespero("*, (, <=, ++, --, ==, =, }, !=, ;, /, -,'+, then, ), ||, &&, >, ], <, [, >=, .");
                        
                    }  
                } else {
                    this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    
                }                      
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Express�o mal formada na linha "+linha.trim()+".\n";
                this.Desespero("*, (, <=, ++, --, ==, =, }, !=, ;, /, -,'+, then, ), ||, &&, >, ], <, [, >=, .");
                
            }
        } else {
            this.erros += "Erro - Express�o mal formada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
        }        
    }
    
    /**
     * <h2> IgualdadeNegacao </h2><br/>
     * <p>A fun��o IgualdadeNegacao, analisar  se um <br/>
     * compara��o do tipo == igual ou um nega��o != .</p><br/>
     */
    private void IgualdadeNegacao() {      
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            String linhaS = atual[2].replaceAll(">", " ").trim();
           
            if(atual[1].trim().equals("==")){
                this.semantico.addValor("==", linhaS, "Op");
                this.qtd++;

            } else if(atual[1].trim().equals("!=")){
                this.semantico.addValor("!=", linhaS, "Op");
                this.qtd++;
                
            } else{
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Operador ('==' ou '!=') n�o encontrado na linha "+linha.trim()+".\n";
                this.Panico("Numero, false, true, Cadeia_de_Caracteres, (, Identificador, !, ++, --");
                
            }
        } else {
            this.erros += "Erro- Operador ('==' ou '!=') n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
        }        
    }
    
    /**
     * <h2> Relacional </h2> <br/>
     * <p>A fun��o Relacional, analisar se � um um operador relacional:<br/> 
     * > maior ,>= maior igual,< menor, <= menor igual. </p> </br>
    */
    private void Relacional() { 
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            String linhaS = atual[2].replaceAll(">", " ").trim();
      
            if(atual[1].trim().equals("<")){

                this.semantico.addValor("<", linhaS, "Op");
                this.qtd++;
  
            } else if(atual[1].trim().equals(">")){
                this.semantico.addValor(">", linhaS, "Op");
                this.qtd++;
  
            } else if(atual[1].trim().equals("<=")){
                this.semantico.addValor("<=", linhaS, "Op");
                this.qtd++;
  
            } else if(atual[1].trim().equals(">=")){
                this.semantico.addValor(">=", linhaS, "Op");
                this.qtd++;
                
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Operador ('<', '>', '<=', '>=' ou '>') n�o encontrado na linha "+linha.trim()+".\n";
                this.Panico("Numero, false, true, Cadeia_de_Caracteres, (, Identificador, !, ++, --");
            }
        } else {
            System.err.println("Erro - qtd > tokens.getSize() em Relacional()");
            this.erros += "Erro - Operador ('<', '>', '<=', '>=' ou '>') n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        
        }        
    }      
    
    /**
     * <h2> AdicaoSubtracao </h2><br/>
     *<p> A fun��o AdicaoSubtracao, analisar se � um operacao de adi��o e subtra��o,<br/>
     * para isso verificar se � + um sinal adi��o ou - sinal de subtra��o.</p><br> 
     */
    private void AdicaoSubtracao() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            String linhaS = atual[2].replaceAll(">", " ").trim();

            if(atual[1].trim().equals("+")){
                this.semantico.addValor("+", linhaS, "Op");
                this.qtd++;

            } else if(atual[1].trim().equals("-")){
                this.semantico.addValor("-", linhaS, "Op");
                this.qtd++;    
                
            } else{
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro- Operador ('+' ou '-') n�o encontrado na linha "+linha.trim()+".\n";
                this.Desespero("then, ), <=, ||, ==, &&, >, =, ], }, <, !=, >=, ;");
            }
        } else {
            this.erros += "Erro - Operador ('+' ou '-') n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        
        }        
    }   
    
    /**
      * <h2> Opera��oMultDiv </h2><br/>
      * <p>A Fun��o Opera��oMultDiv, analisar se � um opera��o de multiplica��o ou divis�o,<br/>
      * para isso � verificar se � * que � de multiplica��o ou / que � de divis�o.</p> <br/>  
      */
    private void OperacaoMultDiv() {    
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            String linhaS = atual[2].replaceAll(">", " ").trim();

            if(atual[1].trim().equals("*")){
                this.semantico.addValor("*", linhaS, "Op");
                this.qtd++;

            } else if(atual[1].trim().equals("/")){
                this.semantico.addValor("/", linhaS, "Op");
                this.qtd++;   
                
            } else{
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro  - Operador ('*' ou '/') n�o encontrado na linha "+linha.trim()+".\n";
                this.Panico("Numero, false, true, Cadeia_de_Caracteres, (, Identificador, !, ++, --");
            }
        } else {
            this.erros += "Erro - Operador ('*' ou '/') n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }   
    
    /**
     *<h2> OperadoresIncrementos </h2> <br/>
     *<p>A fun��o OperadoresIncrementos, analisar se <br/>
     * � uma opera��o de incremento com ++, ou uma opera��o <br/>
     * de decremento com -- .</p><br/>
     */
    private void OperadoresIncrementos() {
        if(this.qtd < this.tokens.getSize()) {   
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            String linhaS = atual[2].replaceAll(">", " ").trim();

            if(atual[1].trim().equals("++")){
                this.semantico.addValor("++", linhaS, "Op");
                this.qtd++;
   
            } else if(atual[1].trim().equals("--")){
                this.semantico.addValor("--", linhaS, "Op");
                this.qtd++;

            } else if(atual[1].trim().equals("!")){
                this.semantico.addValor("!", linhaS, "Op");
                this.qtd++;
                
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Operador ('++', '--' ou '!') n�o encontrado na linha "+linha.trim()+".\n"; 
                this.Desespero("-, +, then, *, ), <=, ||, ==, &&, >, =, ], }, <, !=, >=, ;, /");
                
            }
        } else {
            this.erros += "Erro - Operador ('++', '--' ou '!') n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n"; 
            
        }        
    }
 
   /**
     * <h2>ConclusaoDeAssinatura</h2> <br/>
     * <p>A fun��o ConclusaoDeAssinatura, <br/>
     * analisar a conlus�o de assinatura a lista de parametros .</p> <br/>
    */
    private void ConclusaoDeAssinatura() {
        if(this.qtd < this.tokens.getSize()) { 
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");

            if(atual[1].trim().equals(")")){
                this.semantico.addValor(")", atual[2].replaceAll(">", " ").trim(), "Op");
                this.qtd++; 

            } else if(atual[0].contains("Numero") || atual[1].trim().equals("false") ||
                    atual[1].trim().equals("true") || atual[0].contains("Cadeia_de_Caracteres") ||
                    atual[1].trim().equals("(") || atual[0].contains("Identificador") ||
                    atual[1].trim().equals("!") || atual[1].trim().equals("++") ||
                    atual[1].trim().equals("--")) {
                this.Variaveis();
                
                if(this.qtd < this.tokens.getSize()) {
                    String[] atual2 = this.tokens.getUnicToken(this.qtd).split(",");

                    if(atual2[1].trim().equals(")")){
                        this.semantico.addValor(")", atual2[2].replaceAll(">", " ").trim(), "Op");
                        this.qtd++;

                    } else{
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+linha.trim()+".\n";
                        this.Desespero("-, +, then, *, ), (, <=, ||, ++, --, ==, &&, >, =, ], }, <, !=, [, >=, ;, /, .");
                    } 
                } else {
                    this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    
                }    
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+linha.trim()+".\n";
                this.Desespero("-, +, then, *, ), (, <=, ||, ++, --, ==, &&, >, =, ], }, <, !=, [, >=, ;, /, .");
                
            } 
        } else {
            this.erros += "Erro - Delimitador ')' n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
        }  
    }
    
    /**
     * <h2> Variaveis</h2> <br/>
     * 
     * <p> A fun��o Variaveis, usar a regra de variaveis<br/>
     *  da gram�tica para poder realizar a analise da <br/> 
     *  variaveis que o usu�rio. </p>          
     */
    private void Variaveis() {
        this.VariaveisX();
        this.DeclararVariaveis();   
    }
    
    /**
      *<h2>DeclararVariaveis </h2> <br/>
      *<p>A fun��o DeclararVariaveis,analisar as v�rias declara��o de <br/> 
      *variaves,que pode ser colocadas dentro do abre chave e fecha <br/>
      *chaves que s�o colocada ap�s as palavras resevadas <br/>
      * var e/ou cont.</p><br/>
      */
    private void DeclararVariaveis() { 
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            
            if(atual[0].contains("Delimitador") && atual.length == 4) {
                this.semantico.addValor(",", atual[2].replaceAll(">", " ").trim(), "op");
                this.qtd++;
                this.VariaveisX();
                this.DeclararVariaveis();
                
            }
        } 
    }
    
   /**
      *<h2> Tipo </h2> <br/>
      *<p>A fun�ao Tipo, analisar qual o � tipo que � a variavel se � :<br/>
      *int que significar interio,float que significar real valores <br/>
      *com ponto flutuante, bool que significar booalen do tipo <br/>
      *verdadeiro ou falso ou se � um identificador. </p> <br/>
      */
    private void Tipo() {  
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
            String linha = atual[2].replaceAll(">", " ").trim();
          
            if(atual[1].trim().equals("int")) {
                this.semantico.addTipo("int", linha);
                this.qtd++;

   
            } else if(atual[1].trim().equals("string")) {
                this.semantico.addTipo("string", linha);  
                this.qtd++;
       
            } else if(atual[1].trim().equals("float")) {
                this.semantico.addTipo("float", linha);
                this.qtd++;
 
            } else if(atual[1].trim().equals("bool")) {
                this.semantico.addTipo("bool", linha);
                this.qtd++;
     
            } else if(atual[0].contains("Identificador")) {
                this.semantico.addTipo(atual[1].trim(), linha);
                this.qtd++;     
                
            } else  {
                this.erros += "Erro 85 - Tipo n�o encontrado na linha "+linha.trim()+".\n";
                this.Panico("Identificador");
                
            }
        } else {
            this.erros += "Erro @85 - Tipo n�o encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
      * <h2> Panico </h2>
      * <p>A fun��o Panico , e chama  para  guarda os  erros  identificados  na analise<br/>
      * para ser exibido  posteriomente. </p><br/>
    */
    private void Panico(String errosAchados) {
        do {
            if(this.qtd < this.tokens.getSize()) {
                String[] seguintes = errosAchados.split(",");
                
                for (String seguinte : seguintes) {
                    if (this.tokens.getUnicToken(qtd).contains(seguinte.trim())) {
                        return;
                        
                    }             
                }   
                this.qtd++;
                
              } else {
               break;
            } 
            
        } while(true);        
    }
    
   /**
      * <h2> Desespero </h2> <br/> 
      *  <p>a fun��o Desespero, � chama como forma de contunar leitura <br/>
      * a atraves de um sincrotiniza��o e gurada o erro para  depois ser exibido</p><br/>
     */
    private void Desespero(String errosAchados) { 
        do {
            if(this.qtd < this.tokens.getSize()) {
                String[] seguintes = errosAchados.split(",");
 
                for (String seguinte : seguintes) {
                    String[] atual = this.tokens.getUnicToken(this.qtd).split(",");
                    
                    if (atual[1].trim().equals(seguinte.trim()) || atual[0].contains(seguinte.trim()) ||
                            (atual[0].contains("Delimitador") && atual.length == 4)) {
                        return;   
                    }               
                }
                this.qtd++;
                
            } else {
               break; 
               
            }   
        } while(true);        
    }
    /**
      * <h2>  LinhaDoErro </h2>
      * <p>A fun��o � chama   para poder  pega a linha<br/>
      * do erro  sint�tico  e posteriomente exibir ao usu�rio.</p> <br/>
      */
    private int getLinhaErro(int id) {          
        String[] atual = this.tokens.getUnicToken(id).split(",");
        
        if(atual.length == 4) {
            String linha = atual[3].replaceAll(">", " ");
            return Integer.parseInt(linha.trim());
        } else {
            String linha = atual[2].replaceAll(">", " ");
            return Integer.parseInt(linha.trim());
        }        
    }
    
   /**
    * <h2> A  fun��o getConteudoF </h2>
    * <p> A fun��o a getConteudoF analisar e verificar o cont�udo de uma fun��o <br/>
    *  e assim conseguir identificar possiveis erros de uma fun��o no codigo do <br/>
    * usuario e retorna o cont�udo.</p> <br/>  
    */
    private void getConteudoF(int TokenInicio, int TokenFim) {
        String conteudo = "";
        boolean temReturn = false;
        int tokenAtual = TokenInicio;
        while(tokenAtual < TokenFim) {
            String[] atual = this.tokens.getUnicToken(tokenAtual).split(",");
            if(atual[1].trim().equals("return")) {
                temReturn = true;
            }             
            conteudo += atual[1].trim();
            tokenAtual++;
        }
        this.semantico.addConteudoFuncao(conteudo, temReturn);
    }
    
    /**
    * <h2> A  fun��o getConteudoP </h2>
    * <p> A fun��o a getConteudoP analisar e verificar o cont�udo de uma procedimento  <br/>
    *  e assim conseguir identificar possiveis erros de um procedimento no codigo do <br/>
    * usuario e retorna o cont�udo .</p> <br/>  
    */
    private void getConteudoP(int TokenInicio, int TokenFim) {
        String conteudo = "";
        boolean temReturn = false;
        String linhaRetorno = "";
        int tokenAtual = TokenInicio;       
        while(tokenAtual < TokenFim) {
            String[] atual = this.tokens.getUnicToken(tokenAtual).split(",");            
            if(atual[1].trim().equals("return")) {
                temReturn = true;
                linhaRetorno = atual[2].replaceAll(">", " ").trim();
            }
            conteudo += atual[1].trim();
            tokenAtual++;
        }
        this.semantico.addConteudoProcedure(conteudo, temReturn, linhaRetorno);
    }
}
