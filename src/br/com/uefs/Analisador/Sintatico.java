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
     * para  poder  realizar a chamada recussiva  para para poder analisar qualm a parte do código .</p><br/> 
      */
    private void Programa() {
        FuncoesOuProcedimentos();
        this.EscopoGlobal();
        this.Program();        
    }

    /**
     * <h2> Program</h2><br/>
      * para  poder  realizar a chamada recussiva  para para poder analisar qualm a parte do código .</p><br/>
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
      * <p>A função FuncoesOuProcedimentos, <br/>
      * analisar  a criação de  uma <br/>
      * função ou  um procedimento. <p> <br/>
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
          this.erros += "Erro - Palavra Reservada não encontrada na linha "+linha.trim()+".\n";                
          this.Panico(" procedure, function ");
         }
   }else {
      System.err.println("Erro - qtd > tokens.getSize() em EscopoGlobal()");
    }    
}
   /**
     * <h2>  EscopoGlobal </h2><br/>
     * <p>A  função EscopoGlobal, analisar  qual a parte do código  :<br/>
     * se é metodo start,a declaração de um variavél, ou de uma constante <br/>
     * ou de um novo tipo criado pelo usuário .</p><br/>
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
                this.erros += "Erro - Palavra Reservada não encontrada na linha "+linha.trim()+".\n";                
                this.Panico("start, struct, typedef, const, var");
            }
        } else {
            this.erros += "Erro  - Palavra Reservada não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";  
        }        
    }
    
   /**
     * <h2>Funcao </h2><br/>
     * <p>A função Funcao,analisar se a criação de um função no código de um usuário <br/>
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
                        this.erros += "Erro - Delimitador '(' não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Delimitador '(' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }                        
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro- Palavra Reservada 'function' não encontrada na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
                } 
        } else {
            this.erros += "Erro - Palavra Reservada 'function' não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }               
    }
    
  /**
    * <h2> BlocoFuncao </h2><br/>
    * <p>A função BlocoFuncao,analisar se a conclusão da assintuda da<br/>
    *  função está correta, e se os códigos dentro do função <br/>
    * está correto e são a função que analisar os parametros.</p><br/> 
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
                                        this.erros += "Erro- Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                        this.Panico("struct, procedure, typedef, const, function, var, start");
                                    }
                                } else {
                                    this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";                                    
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro @5 - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador ')' não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro- Delimitador ')' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
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
                                this.erros += "Erro- Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro- Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Declaração inválida na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
            }
            
            if(TokenInicio != 0 && TokenFim != 0) {
                this.getConteudoF(TokenInicio, TokenFim);
            }   
        } else {
            this.erros += "Erro @9 - Declaração inválida na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    /**
      * <h2>Procedimento </h2><br/>
      * <p>A função Procedimento,analisar se a criação de um procedimento no código de um usuário <br/>
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
                                this.erros += "Erro - Delimitador '(' não encontrado na linha "+linha.trim()+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                              } 
                        } else {
                            this.erros += "Erro  - Delimitador '(' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                              }                               
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Identificador não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                      }
                } else {
                    this.erros += "Erro - Identificador não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                  }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro- Palavra Reservada 'procedure' não encontrada na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
              }  
        } else {
            this.erros += "Erro  - Palavra Reservada 'procedure' não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }              
    }
    
    /**
      * <h2>BlocoProcedimento </h2><br/>
      * <p>A função BlocoProcedimento,analisar se a conclusão da assintuda do,<br/>
      * procedimento está correta e se os códigos dentro do procedimento está <br/>
      * correto e são a função que analisar os parametros.</p><br/>
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
                                        this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                        this.Panico("struct, procedure, typedef, const, function, var, start");
                                    }
                                } else {
                                    this.erros += "Erro- Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro- Delimitador ')' não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Delimitador ')' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
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
                                this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {

                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro  - Delimitador ')' não encontrado na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
            }
            
            if(TokenInicio != 0 && TokenFim != 0) {
                this.getConteudoP(TokenInicio, TokenFim);
            } 
        } else {
            this.erros += "Erro - Delimitador ')' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";    
        }        
    }
    
    /**
      * <h2> NovoTipo</h2><br/>
      * <p>A função NovoTipo, analisar a presença do typedef,<br/>
      * que é a criação de um novo tipo sem ser os tipos <br/>
      * primitivos como : int , float, bool e string.</p> <br/>
      * 
      * inspiração na  gramática  da linguagem C .<br>
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
                this.erros += "Erro - Palavra Reservada 'typedef' não encontrada na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
            } 
        } else {
            this.erros += "Erro- Palavra Reservada 'typedef' não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }               
    }
    
   /**
     * <h2> ConteudoDoTypedef </h2> <br/>
     * <p> A função ConteudoDoTypedef , analisar  o contéudo de um novo tipo,<br/> 
     * criado pelo usuário para  poder ser usado  no código do usuário.<br>
     * 
     * inspiração na  gramática  da linguagem  de programção C. <br>
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
                                this.erros += "Erro - Delimitador ';' não encontrado na linha "+this.getLinhaErro(qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro- Delimitador ';' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Identificador não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro- Identificador não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
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
                                this.erros += "Erro  - Delimitador ';' não encontrado na linha "+this.getLinhaErro(qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }  
                        } else {
                            this.erros += "Erro - Delimitador ';' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                              
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro- Identificador não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Identificador não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Tipo não encontrado na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
            }
        } else {
            this.erros += "Erro - Tipo não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
     * <h2> Var </h2> <br/>
     * </p>A função Var,analisar se a declaração de variavel respeita a regra de ser,<br/>
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
                                this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("false, (, return, print, ++, --, Numero, }, if, while, ;, {, true, "
                                        + "procedure, struct, typedef, function, start, !, scan, Cadeia_de_Caracteres, const, var, Identificador");
                            }
                        } else {
                            this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Panico("false, (, return, print, ++, --, Numero, }, if, while, ;, {, true, "
                            + "procedure, struct, typedef, function, start, !, scan, Cadeia_de_Caracteres, const, var, Identificador");
                    }
                } else {
                    this.erros += "Erro - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'var' não encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, ++, --, Numero, }, if, while, ;, {, true, "
                    + "procedure, struct, typedef, function, start, !, scan, Cadeia_de_Caracteres, const, var, Identificador");
            }
        } else {
            this.erros += "Erro- Palavra Reservada 'var' não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
     * <h2> Const </h2> <br/>
     * </p>A função Const,analisar se a declaração de variavel respeita a regra de ser,<br/>
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
                                this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                            }
                        } else {
                            this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro- Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                    }
                } else {
                    this.erros += "Erro - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'const' não encontrada na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start");
            }
        } else {
            this.erros += "Erro - Palavra Reservada 'const' não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
   /**
     * <h2>Struct</h2><br/>
     * <p>A função Struct,analisar se o a criação de bloco de variaveis,ocorre<br/> 
     * de forma correta com a palavra struct e o identificador mais as declarações.</p><br/>
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
                        this.erros += "Erro - Identificador não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
                    }
                } else {
                    this.erros += "Erro - Identificador não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'struct' não encontrada na linha "+linha.trim()+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
            }
        } else {
            this.erros += "Erro- Palavra Reservada 'struct' não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
   /**
      * <h2> structX</h2><br/>
      * <p>A função structX,analisar o contéudo da declaração de de um bloco<br/>
      *  de variaveis e uma herança que pode ocorre com a palavra extends.</p><br/>
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
                        this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
                    }
                } else {
                    this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
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
                                        this.erros += "Erro - Delimitador '}' não encontrado na linha "+linha.trim()+".\n";
                                        this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
                                    }
                                } else {
                                    this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador '{' não encontrado na linha "+linha.trim()+".\n";
                                this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
                            }
                        } else {
                            this.erros += "Erro- Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Identificador não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
                    }
                } else {
                    System.err.println("Erro - qtd > tokens.getSize() em structX()");
                    this.erros += "Erro - Identificador não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro- Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                this.Panico("struct, procedure, typedef, const, function, var, start, Identificador");
            }
        } else {
            this.erros += "Erro - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
     * <h2> ListagemDeParametros</h2> <br/>
     * <p>A função ListagemDeParametros, chama duas funções para realisar ,<br/>
     * a analise  dos parametros  informados pelo usuário.</p> <br/>
     */
    private void ListagemDeParametros() {
        this.ListaDeDeclaracaoDeParametro();
        this.DeclaracaoDeParametro();        
    }
   /**
     * <h2> DeclaracaoDeParametro</h2><br/>
     * <p>A função DeclaracaoDeParametro realizar <br/>
     * o processo de analisar parametros e chama <br/>  
     * a função <a>ListaDeDeclaracaoDeParametro()</a><br/>
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
      * <p>A função ListaDeDeclaracaoDeParametro,analisar a lista de declaração <br/>
      * de parametros que o usuário pode fazer e utilizar.</p> <br>
      */
    private void ListaDeDeclaracaoDeParametro() {
        this.Tipo();
        this.Declaracoes();
    }
    
    /**
     * <h2> ListaDeVariavel</h2><br/>
     * <p> A função ListaDeVariavel, faz a chama de <br/>
     * dual funções  para analista a lista de <br/>
     * variavaeis </p></br>
     *   
     */
    private void ListaDeVariavel() {
    
        this.DeclararVariavel();
        this.ListaDeDeclararVariavel();    
    }
    
    /**
      * <h2> ListaDeDeclararVariavel </h2> <br/>
      * <p> A função ListaDeDeclararVariavel, realizar o <br/>
      * processo parecido com a função DeDeclararVariavel,<br/>
      * só  analisar mais variaveis. </p><br/>
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
       * <p>A função DeclararVariavel,analisar as várias </br>
       * declarações que o usuário pode fazer.</p> <br/>
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
                this.erros += "Erro - Delimitador ';' não encontrado na linha "+this.getLinhaErro(qtd-1)+".\n";
                this.Panico("}, bool, float, int, string, Identificador");
            }
        } else {
            this.erros += "Erro - Delimitador ';' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
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
      * <p>A função  Declara , chama  duas  funções para concluir a <br/>
      * analiser de  declarações do código  do usuário .</p><br/>
     */
    private void Declara() {
    
        this.Declaracoes();
        this.Igualdade();
    }
    
    /**
     * <h2> Igualdade </h2><br/>
     * <p>A funçõa Igualdade,analisar se tem uma =  entre expressões. </p> <br/>
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
      * <p> A função Inicializar,analisar o possivél inicio que pode ter ,<br/>
      *  alguns  códigos do usuário. </p><br/>
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
                this.erros += "Erro - Erro de inicialização na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                this.Desespero(";, }");
            }
        } else {
            this.erros += "Erro - Erro de inicialização na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
     * <h2> Concluir </h2><br/>
     *<p> A função Concluir , verificar se tem ,  ou }. </p><br/>
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
                        this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                        this.Desespero("}, ;");                        
                    }
                } else {
                    this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.qtd-1)+".\n";
                this.Desespero("}, ;");
            }
        } else {
            this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
   /**
     * <h2>ListaDeInicializar </h2><br/>
     * <p> A função ListaDeInicializar,chama  a <a>Inicializar</a> e <br/>
     * a  função <a> ListaDeInicializarX</a> para   verificar as possiveis 
     * lista de inicializações que o código  do usário  pode ter . </p><br/>
    */
    private void ListaDeInicializar() {
    
        this.Inicializar();
        this.ListaDeInicializarX();
    }
    
   /**
     *<h2>ListaDeInicializarX </h2><br/>
     * <p> A função ListaDeInicializarX, analisar um <br/>
     * possivel lista com varios inicio que o <br/>
     * código do usuário pode ter. </p><br/>
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
      * <p>A  função Declaracoes,  analisar  se o usuário criou um identificador <br>
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
                this.erros += "Erro- Identificador não encontrado na linha "+linha.trim()+".\n";
                this.Desespero("=, ;, ), (");
            }
        } else {
            this.erros += "Erro- Identificador não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    } 
    
    /**
      * <h2>DeclararVariavelX </h2> <br/>
      * <p>A  função DeclararVariavelX,  analisar  se o usuário criou um abrir  cochetes  <br>
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
      * <p>A  função DeclararVariavelXX,  analisar  se o usuário definiu um tipo , um numero 
      * um cadeia de caracteres ou identificador  e se o usuário fechou cochetes <br/>
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
                            this.erros += "Erro - Delimitador ']' não encontrado na linha "+linha.trim()+".\n";
                            this.Desespero("=, ;, ), (");
                        }
                    } else {
                        this.erros += "Erro- Delimitador ']' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    }                
            } else if(atual[1].trim().equals("]")) {
                this.qtd++;
                this.DeclararVariavelX();            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Delimitador ']' não encontrado na linha "+linha.trim()+".\n";
                this.Desespero("=, ;, ), (");
            }
        } else {
            this.erros += "Erro - Delimitador ']' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
     * <h2> Comando</h2> <br/>
     *<p> A função Comando, analisar se é são os comandos possivel de acontece em um código como :<br/>
     * um laço while do tipo enquanto, se alguma expressão, se um comando print para escrever <br/>
     * para usuário,um scan e capturar  dados do usuário ou um retorno de alguma coisa .</p> <br/> 
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
                this.erros += "Erro - Declaração esperada não encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;, "
                        + "{, scan, true, Cadeia_de_Caracteres, var, Identificador");
            }
        } else {
            this.erros += "Erro - Declaração esperada não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
      * <h2> Comandos </h2><br/>  
      * <p>A função comandos , analisar  qual o tipo  de  ação que usuário<br/>
      * escrveu no seu código para  poder ocorre no seu código. </p> <br/>
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
                this.erros += "Erro- Declaração inválida na linha "+linha.trim()+".\n";
                this.Panico("}");
            }
        } else {
            this.erros += "Erro - Declaração inválida na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    } 
    
    /**
      * <h2> ComandosX </h2><br/>
      * <p>A função ComandosX, realizar um ação pareceida que comando, <br/>
      * porém consegue concluir a analiser desta parte importante do<br/>
      * código que usuário escreveu.</p> <br/>
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
     *<p> A função Start,analisar se o método principal start foi declarado, <br/>
     * para que possa ocorrer a execução da compilação do código do usuário.</p><br/>
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
                                                this.erros += "Erro - Delimitador '}' não encontrado na linha "+linha.trim()+".\n";
                                                this.Panico("struct, procedure, typedef, const, function, var, start");
                                                
                                                
                                            }
                                        } else {
                                            this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                        
                                        }                        
                                    } else {
                                        String linha = atual4[2].replaceAll(">", " ");
                                        this.erros += "Erro - Delimitador '{' não encontrado na linha "+linha.trim()+".\n";
                                        this.Panico("struct, procedure, typedef, const, function, var, start");
                                        
                                        
                                    }
                                } else {
                                    this.erros += "Erro- Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro- Delimitador ')' não encontrado na linha "+linha.trim()+".\n";   
                                this.Panico("struct, procedure, typedef, const, function, var, start");
                                
                                
                            }
                        } else {
                            this.erros += "Erro - Delimitador ')' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";   

                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '(' não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("struct, procedure, typedef, const, function, var, start");
                        
                    }
                } else {
                    this.erros += "Erro - Delimitador '(' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    
                    
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
     * <p>A função Print,anlisar se ocorre o print,usando para escrever na tela <br/>
     * do usuário dados e/ou informações(forma de interatividade com usuário).</p></br>
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
                                        this.erros += "Erro - Delimitador ';' não encontrado na linha "+linha.trim()+".\n";
                                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                                    }
                                } else {
                                    this.erros += "Erro - Delimitador ';' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador ')' não encontrado na linha "+linha.trim()+".\n";
                                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                                
                            }
                        } else {
                            this.erros += "Erro - Delimitador ')' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                       
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '(' não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                        
                    }
                } else {
                    this.erros += "Erro - Delimitador '(' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
               
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'print' não encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro- Palavra Reservada 'print' não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        
        }        
    } 
    
    /**
      * <h2> Scan  </h2> <br/>
      *<p>A função Scan,anlisar se ocorre o scan,usando para capturar dados do usuário,<br/>
      *e colocar o valores capturados em variaveis(forma de interatividade com usuário).</p></br>
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
                                        this.erros += "Erro - Delimitador ';' não encontrado na linha "+linha.trim()+".\n";
                                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                                    }
                                } else {
                                    this.erros += "Erro - Delimitador ';' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                                
                                }                    
                            } else {
                                String linha = atual3[2].replaceAll(">", " ");
                                this.erros += "Erro - Delimitador ')' não encontrado na linha "+linha.trim()+".\n";
                                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                                
                                
                            }
                        } else {
                            this.erros += "Erro - Delimitador ')' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                       
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '(' não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                        
                    }
                } else {
                    this.erros += "Erro- Delimitador '(' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
               
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'scan' não encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
                
            }
        } else {
            this.erros += "Erro - Palavra Reservada 'scan' não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
      * <h2> While</h2><br/>
      * <p>A função while,analisar se é o laço de repetição enquanto(While).</p><br/>
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
                                this.erros += "Erro - Delimitador ')' não encontrado na linha "+linha.trim()+".\n";
                                this.Panico("}, false, (, return, print, else, !, ++, --, Numero, if,"
                                        + " while, ;, {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                                
                            }
                        } else {
                            this.erros += "Erro - Delimitador ')' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        
                        }                
                    } else {
                        String linha = atual2[2].replaceAll(">", " ");
                        this.erros += "Erro - Delimitador '(' não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if,"
                                        + " while, ;, {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                        
                    }
                } else {
                    this.erros += "Erro - Delimitador '(' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'while' não encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if,"
                                        + " while, ;, {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro - Palavra Reservada 'while' não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        
        }        
    }
    
    /**
      * <h2>IfThen </h2> <br/>
      * <p>A função IfThen,analisar se acontce um <br/>
      * o if(que é muito usado em codificação),com <br/>
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
                        this.erros += "Erro - Palavra Reservada 'then' não encontrada na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;, {,"
                                + " scan, true, Cadeia_de_Caracteres, var, Identificador");
                        
                    }
                } else {
                    this.erros += "Erro- Palavra Reservada 'then' não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
               
                }
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'if' não encontrado na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;, {,"
                                + " scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro - Palavra Reservada 'if' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }    
    
    /**
     * <h2>Else </h2><br/>
     *  <p>A função Else, analisar se ocorre o else.</p><br/>
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
     * <p>A função RetornoExpressao,analisar é o retorno de uma expressão, para quem chamou.</p> <br/>
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
                        this.erros += "Erro - Delimitador ';' não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificacor");
                    }
                } else {
                    this.erros += "Erro - Delimitador ';' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                        
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Palavra Reservada 'return' não encontrada na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificacor");
                
            }
        } else {
            this.erros += "Erro - Palavra Reservada 'return' não encontrada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
            
        }        
    }
    
    /**
      * <h2> Abertura</h2><br/>
      * <p>A função Abertura, verificar se ocorre abertura de {. </p> <br/> 
      */
    private void Abertura() {
        if(this.qtd < this.tokens.getSize()) {
            String[] atual = this.tokens.getUnicToken(this.qtd).split(",");  

            if(atual[1].trim().equals("{")) {
                this.qtd++;
                this.Aberturalf();
                
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro  - Delimitador '{' não encontrado na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                        + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro - Delimitador '{' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
        }                
    } 
    
    /**
     *  <h2> Aberturalf</h2><br/>
     *  <p>função  verificra abertura de if.</p><br/>
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
                        this.erros += "Erro - Delimitador '}' não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador"); 
                        
                    }
                } else {
                    this.erros += "Erro - Delimitador '}' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Declaração inválida na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro - Declaração inválida na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }       
    }
    
    /**
      * <h2>ListaDeExpressoes </h2> <br/>
      * <p>A função ListaDeExpressoes,analisar uma possivél lista de expressões que <br/>
      * usuário pode ter criado no código separadas com o delimitador ;.</p><br/>
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
                        this.erros += "Erro - Delimitador ';' não encontrado na linha "+linha.trim()+".\n";
                        this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                    }
                } else {
                    this.erros += "Erro- Delimitador ';' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    
                }            
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro  - Delimitador ';' não encontrado na linha "+linha.trim()+".\n";
                this.Panico("false, (, return, print, else, !, ++, --, Numero, }, if, while, ;,"
                                + " {, scan, true, Cadeia_de_Caracteres, var, Identificador");
                
            }
        } else {
            this.erros += "Erro - Delimitador ';' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
        }        
    }
    
   /**
      * <h2>Expressoes </h2> <br/>
      * <p>A função a Expressoes,chama as funções variaveisX e ExpressoesX,<br/>
      * para poder analisar as possivéis expressões que pode acontcer no código.</p><br/>
     */
    private void Expressoes() {
    
        this.VariaveisX();
        this.ExpressoesX();
    }   
    
    /**
     * <h2>ExpressoesX </h2> <br/>
     * <p>A função ExpressoesX,analisar uma possivél lista de expressões que <br/>
     * usuário pode ter criado no código .</p><br/>
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
     *´<p> A função VariaveisX,chama as funções que verifica expressão<br/>
     * condicional e atribuição.</p> </br>
     */
    private void VariaveisX() {
        this.ExpressaoCondicional();
        this.Atribuicao();
    }    
    
    /**
     * <h2> Atribuicao </h2><br/>
     *<p> A função Atribuicao,analisar se é operador relacional =(igual ),<br/>
     * que é usado com  uma forma de abriu um valor ou resultado.</p><br/>
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
      * <p>A função  ExpressaoCondicional, chama <br/>
      * a expressão logica  quando a expressão <br/>
      * condicional  for  logica.</p> <br/>
      */
    private void ExpressaoCondicional() {
        this.ExpressaoLogica();
    }    
    
    /**
     * <h2> ExpressaoLogica</h2> <br/>
     * </p>A função ExpressaoLogica,chama as funções que vão verificar as </br> 
     * expressões  possivél do que o usuário  pode ter criado.</p> <br/>
     */
    private void ExpressaoLogica() {
    
        this.ExpressaoRelacionalOu();
    }   
    
    /**
     * <h2>ExpressaoRelacionalOu</h2><br/>
     *<p> A função ExpressaoRelacionalOu realiza analisar <br/> 
     * se é uma expressao relacional ou ( no caso ou  =>  || ) .</p> <br/> 
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
     *<p> A função ExpressaoRelacionalAnd realiza analisar <br/> 
     * se é uma expressao relacional and ( no caso EE => &&) .</p> <br/> 
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
     *<p> A função ExpressaoRelacionalNot realiza analisar <br/> 
     * se é uma expressao relacional not ( no caso não => !=) .</p> <br/>  
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
     * <p>A função aComparacaoRelacional, realizar a analise de uma comparação<br/>
     * relacional  que pode ter no código  do usuário .</p></br>
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
   * <p>função OpAdicaoSubtracao, analisar uma possivél operação de adição<br/>
   * ou subtração e chama outras funções para realizar outras possiveis <br/>
   * operações operação que pode acontecer  no código do usuário .</p>
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
     * <p>a função OperacaoComExpressao,chama as funções para as possiveis operações<br> 
     * pode ocorrer  com  as expressões .</p><br/>
     */
    private void OperacaoComExpressao() {   
        this.OperacaoEmExpressoes();
        this.OpEmExpressoesMultDiv();
        
    } 
    
   /**
      * <h2> OpEmExpressoesMultDiv </h2><br/>
      * <p>A função OpEmExpressoesMultDiv,realizar o mesmo processo <br/>
      * que a <a>OperacaoEmExpressoes </a> analisar a possovel <br/>
      * operação de multiplicação e Divisão e chama a realizas<br/>
      * as outras análises. </p><br/>
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
     * <p>A função OperacaoEmExpressoes, analisar as possiveis operações ,<br/>
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
                this.erros += "Erro - Expressão inválida na linha "+linha.trim()+".\n";
                this.Desespero("-, +, then, *, ), <=, ||, ==, &&, >, =, ], }, <, !=, >=, ;, /");
            }
        } else {
            
            this.erros += "Erro  - Expressão inválida na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }   

    
   /**
     * <h2> TiposLiteral <h1/> 
     * <p> A função TiposLiteral, analisar se é:<br/>
     * um identificador, um número,uma cadeira de caracteres(string Lietral),<br/> 
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
                        this.erros += "Erro - Delimitador ')' não encontrado na linha "+linha.trim()+".\n";
                        this.Desespero("*, (, <=, ++, --, ==, =, }, !=, ;, /, -,'+, then, ), ||, &&, >, ], <, [, >=, .");
                        
                    }  
                } else {
                    this.erros += "Erro - Delimitador ')' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    
                }                      
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Expressão mal formada na linha "+linha.trim()+".\n";
                this.Desespero("*, (, <=, ++, --, ==, =, }, !=, ;, /, -,'+, then, ), ||, &&, >, ], <, [, >=, .");
                
            }
        } else {
            this.erros += "Erro - Expressão mal formada na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
        }        
    }
    
    /**
     * <h2> IgualdadeNegacao </h2><br/>
     * <p>A função IgualdadeNegacao, analisar  se um <br/>
     * comparação do tipo == igual ou um negação != .</p><br/>
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
                this.erros += "Erro - Operador ('==' ou '!=') não encontrado na linha "+linha.trim()+".\n";
                this.Panico("Numero, false, true, Cadeia_de_Caracteres, (, Identificador, !, ++, --");
                
            }
        } else {
            this.erros += "Erro- Operador ('==' ou '!=') não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
        }        
    }
    
    /**
     * <h2> Relacional </h2> <br/>
     * <p>A função Relacional, analisar se é um um operador relacional:<br/> 
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
                this.erros += "Erro - Operador ('<', '>', '<=', '>=' ou '>') não encontrado na linha "+linha.trim()+".\n";
                this.Panico("Numero, false, true, Cadeia_de_Caracteres, (, Identificador, !, ++, --");
            }
        } else {
            System.err.println("Erro - qtd > tokens.getSize() em Relacional()");
            this.erros += "Erro - Operador ('<', '>', '<=', '>=' ou '>') não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        
        }        
    }      
    
    /**
     * <h2> AdicaoSubtracao </h2><br/>
     *<p> A função AdicaoSubtracao, analisar se é um operacao de adição e subtração,<br/>
     * para isso verificar se é + um sinal adição ou - sinal de subtração.</p><br> 
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
                this.erros += "Erro- Operador ('+' ou '-') não encontrado na linha "+linha.trim()+".\n";
                this.Desespero("then, ), <=, ||, ==, &&, >, =, ], }, <, !=, >=, ;");
            }
        } else {
            this.erros += "Erro - Operador ('+' ou '-') não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        
        }        
    }   
    
    /**
      * <h2> OperaçãoMultDiv </h2><br/>
      * <p>A Função OperaçãoMultDiv, analisar se é um operação de multiplicação ou divisão,<br/>
      * para isso é verificar se é * que é de multiplicação ou / que é de divisão.</p> <br/>  
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
                this.erros += "Erro  - Operador ('*' ou '/') não encontrado na linha "+linha.trim()+".\n";
                this.Panico("Numero, false, true, Cadeia_de_Caracteres, (, Identificador, !, ++, --");
            }
        } else {
            this.erros += "Erro - Operador ('*' ou '/') não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }   
    
    /**
     *<h2> OperadoresIncrementos </h2> <br/>
     *<p>A função OperadoresIncrementos, analisar se <br/>
     * é uma operação de incremento com ++, ou uma operação <br/>
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
                this.erros += "Erro - Operador ('++', '--' ou '!') não encontrado na linha "+linha.trim()+".\n"; 
                this.Desespero("-, +, then, *, ), <=, ||, ==, &&, >, =, ], }, <, !=, >=, ;, /");
                
            }
        } else {
            this.erros += "Erro - Operador ('++', '--' ou '!') não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n"; 
            
        }        
    }
 
   /**
     * <h2>ConclusaoDeAssinatura</h2> <br/>
     * <p>A função ConclusaoDeAssinatura, <br/>
     * analisar a conlusão de assinatura a lista de parametros .</p> <br/>
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
                        this.erros += "Erro - Delimitador ')' não encontrado na linha "+linha.trim()+".\n";
                        this.Desespero("-, +, then, *, ), (, <=, ||, ++, --, ==, &&, >, =, ], }, <, !=, [, >=, ;, /, .");
                    } 
                } else {
                    this.erros += "Erro - Delimitador ')' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
                    
                }    
            } else {
                String linha = atual[2].replaceAll(">", " ");
                this.erros += "Erro - Delimitador ')' não encontrado na linha "+linha.trim()+".\n";
                this.Desespero("-, +, then, *, ), (, <=, ||, ++, --, ==, &&, >, =, ], }, <, !=, [, >=, ;, /, .");
                
            } 
        } else {
            this.erros += "Erro - Delimitador ')' não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
            
        }  
    }
    
    /**
     * <h2> Variaveis</h2> <br/>
     * 
     * <p> A função Variaveis, usar a regra de variaveis<br/>
     *  da gramática para poder realizar a analise da <br/> 
     *  variaveis que o usuário. </p>          
     */
    private void Variaveis() {
        this.VariaveisX();
        this.DeclararVariaveis();   
    }
    
    /**
      *<h2>DeclararVariaveis </h2> <br/>
      *<p>A função DeclararVariaveis,analisar as várias declaração de <br/> 
      *variaves,que pode ser colocadas dentro do abre chave e fecha <br/>
      *chaves que são colocada após as palavras resevadas <br/>
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
      *<p>A funçao Tipo, analisar qual o é tipo que é a variavel se é :<br/>
      *int que significar interio,float que significar real valores <br/>
      *com ponto flutuante, bool que significar booalen do tipo <br/>
      *verdadeiro ou falso ou se é um identificador. </p> <br/>
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
                this.erros += "Erro 85 - Tipo não encontrado na linha "+linha.trim()+".\n";
                this.Panico("Identificador");
                
            }
        } else {
            this.erros += "Erro @85 - Tipo não encontrado na linha "+this.getLinhaErro(this.tokens.getSize()-1)+".\n";
        }        
    }
    
    /**
      * <h2> Panico </h2>
      * <p>A função Panico , e chama  para  guarda os  erros  identificados  na analise<br/>
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
      *  <p>a função Desespero, é chama como forma de contunar leitura <br/>
      * a atraves de um sincrotinização e gurada o erro para  depois ser exibido</p><br/>
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
      * <p>A função é chama   para poder  pega a linha<br/>
      * do erro  sintático  e posteriomente exibir ao usuário.</p> <br/>
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
    * <h2> A  função getConteudoF </h2>
    * <p> A função a getConteudoF analisar e verificar o contéudo de uma função <br/>
    *  e assim conseguir identificar possiveis erros de uma função no codigo do <br/>
    * usuario e retorna o contéudo.</p> <br/>  
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
    * <h2> A  função getConteudoP </h2>
    * <p> A função a getConteudoP analisar e verificar o contéudo de uma procedimento  <br/>
    *  e assim conseguir identificar possiveis erros de um procedimento no codigo do <br/>
    * usuario e retorna o contéudo .</p> <br/>  
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
