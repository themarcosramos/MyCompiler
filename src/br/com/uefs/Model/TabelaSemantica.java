package br.com.uefs.Model;

import java.util.ArrayList;

import br.com.uefs.Util.*;

public class TabelaSemantica {
    private ArrayList<NSemantico> tabelaSemantica;
   private ArrayList<NSemantico> funcoesPendentes;
   private ArrayList<NSemantico> proceduresPendentes;
   private ArrayList<NSemantico> cadeiaStructs;
   protected static int contador = 0;

   public TabelaSemantica() {
       this.tabelaSemantica = new ArrayList();
       this.funcoesPendentes = new ArrayList();
       this.proceduresPendentes = new ArrayList();
       this.cadeiaStructs = new ArrayList();
   }
   
   public boolean verificarFunction(String nome) {    
       for (NSemantico no : this.tabelaSemantica) {            
           if(no.getDeclaracao().equals("function") && no.getNome().equals(nome)) {                
               NSemantico noAtual = this.getLastNo();
               noAtual.setIdSobrecarga(no.getId());
               this.funcoesPendentes.add(noAtual);
               return true;
           }
       }        
       return false;
   }
   
   public boolean verificarProcedure(String nome) {    
       for (NSemantico no : this.tabelaSemantica) {            
           if(no.getDeclaracao().equals("procedure") && no.getNome().equals(nome)) {                
               NSemantico noAtual = this.getLastNo();
               noAtual.setIdSobrecarga(no.getId());
               this.proceduresPendentes.add(noAtual);
               return true;
           }
       }        
       return false;
   }
   
   public boolean verificarStruct(String nome) {    
       for (NSemantico no : this.tabelaSemantica) {            
           if(no.getDeclaracao().equals("struct") && no.getNome().equals(nome)) {                
               return true;
           }
       }        
       return false;
   }
   
   public boolean verificarNovoTipo(String nome) {    
       for (NSemantico no : this.tabelaSemantica) {            
           if(no.getDeclaracao().equals("typedef") && no.getNome().equals(nome)) {                
               return true;
           }
       }        
       return false;
   }
   
   public boolean verificarConst(String nome) {    
       for (NSemantico no : this.tabelaSemantica) {            
           if(no.getDeclaracao().equals("const") && no.getNome().equals(nome)) {                
               return true;
           }
       }        
       return false;
   }
   
   public boolean verificarVar(String nome, String palavraReservadaEscopo, String identificadorEscopo) {        
       for (NSemantico no : this.tabelaSemantica) {            
           if(no.getDeclaracao().equals("var")) {                
               if(no.getPalavraReservadaEscopo().equals(palavraReservadaEscopo) && no.getIdentificadorEscopo().equals(identificadorEscopo)) {
                   if(no.getNome().equals(nome)) {                        
                       boolean trava = true;
                       if(palavraReservadaEscopo.equals("function")) {                                                
                           for (NSemantico f : this.funcoesPendentes) {                            
                               if(f.getNome().equals(identificadorEscopo)) {                                   
                                   trava = false;
                               }                             
                           }
                       } else if(palavraReservadaEscopo.equals("procedure")) {
                           for (NSemantico p : this.proceduresPendentes) {
                               if(p.getNome().equals(identificadorEscopo)) {

                                   trava = false;
                               }                             
                           }
                       } 
                       if(trava)
                           return true; 
                   }   
               }                
           }
       }
       
       return false;
   }
   
   public boolean verificarBloco(String nome, String palavraReservadaEscopo, String identificadorEscopo) {       
       
       ArrayList<String> extensoes = null;
       for(NSemantico noS : this.cadeiaStructs) {
           if(noS.getNome().equals(identificadorEscopo)) {
               extensoes = noS.getExtensoes();
               break;
           }            
       }
       
       if(extensoes != null) {            
           for (NSemantico no : this.tabelaSemantica) {    
               if(no.getDeclaracao().equals("varStruct")) {  
                   if(no.getPalavraReservadaEscopo().equals(palavraReservadaEscopo)) {
                       if(no.getIdentificadorEscopo().equals(identificadorEscopo) || extensoes.contains(no.getIdentificadorEscopo())) {
                           if(no.getNome().equals(nome)) {
                               return true;
                           } 
                       } 
                   }
               }
           }  
       } else {            
           for (NSemantico no : this.tabelaSemantica) {    
               if(no.getDeclaracao().equals("varStruct")) {  
                   if(no.getPalavraReservadaEscopo().equals(palavraReservadaEscopo) && no.getIdentificadorEscopo().equals(identificadorEscopo)) {                       
                       if(no.getNome().equals(nome)) {
                          return true;
                       } 
                   }
               }
           }  
       }
       return false;
   }
          
   public boolean verificarEscopoAtual(String nome, String palavraReservadaEscopo, String identificadorEscopo) {
       for(NSemantico no : this.tabelaSemantica) {            
           if(no.getPalavraReservadaEscopo().equals(palavraReservadaEscopo) && no.getIdentificadorEscopo().equals(identificadorEscopo)) {
               if(no.getNome().equals(nome)) {
                   return true;
               }
           }
       }
       return false;
   }
   
   public boolean verificarDeclaracaoFunction(String identificadorEscopo, String declaracao, String nome) {        
       for(NSemantico no : this.tabelaSemantica) {            
           if(no.getPalavraReservadaEscopo().equals("function") && no.getIdentificadorEscopo().equals(identificadorEscopo)) {
               if(no.getDeclaracao().equals(declaracao) && no.getNome().equals(nome)) {
                   this.funcoesPendentes.add(this.getLastNo());
                   return true;
               } 
           } else if(no.getDeclaracao().equals("function") && no.getNome().equals(identificadorEscopo)) {
               for(Parametros noP : no.getParametros()) {
                   if(noP.getNome().equals(nome)) {
                       this.funcoesPendentes.add(this.getLastNo());
                       return true;
                   }
               }   
           }
       }                     
       return false;
   }
   
   public boolean verificarDeclaracaoProcedure(String identificadorEscopo, String declaracao, String nome, int idNoDecProc) {        
       for(NSemantico no : this.tabelaSemantica) {            
           if(no.getPalavraReservadaEscopo().equals("procedure") && no.getIdentificadorEscopo().equals(identificadorEscopo)) {
               if(no.getDeclaracao().equals(declaracao) && no.getNome().equals(nome)) {
                   return true;
               }
           } else if(no.getDeclaracao().equals("procedure") && no.getNome().equals(identificadorEscopo)) {
               for(Parametros noP : no.getParametros()) {
                   if(noP.getNome().equals(nome)) {
                       return true;
                   }
               }   
           }
       }      
       return false;
   }
   
   public int verificarDeclaracao(String nome, String palavraReservadaEscopo, String identificadorEscopo) {
       for(NSemantico no : this.tabelaSemantica) {
           
           if(no.getPalavraReservadaEscopo().equals(palavraReservadaEscopo) && no.getIdentificadorEscopo().equals(identificadorEscopo)) {
               if(no.getNome().equals(nome)) {
                   return no.getId();
               }
           }            
       }
       if(!palavraReservadaEscopo.equals("Global")) {
           
           for(NSemantico no : this.tabelaSemantica) {
           
               if(no.getPalavraReservadaEscopo().equals("Global")) {
                   if(no.getNome().equals(nome)) {
                       return no.getId();
                   }
               }            
           }
       }
       
       return 0;
   }
   
   public boolean verificarDeclaracaoBloco(String nome) {
       
       NSemantico no = this.tabelaSemantica.get(this.getLastIndex());
       if(no.getDeclaracao().equals("struct")) {
           
           for(NSemantico s : this.tabelaSemantica) {
               if(s.getPalavraReservadaEscopo().equals(no.getDeclaracao()) && s.getIdentificadorEscopo().equals(no.getNome())) {

                   if(s.getDeclaracao().equals("varStruct") && s.getNome().equals(nome)) {
                       s.setValor("");
                       return true;
                   }
               }  
           }
       }
       
       return false;
   }
   

   public void addTipo(String tipo) {
       this.getLastNo().setTipo(tipo);
   }
   
   public void addNome(String nome) {
       this.getLastNo().setNome(nome);
   }
   
   public void addTipoParametros(String tipoParametros) {
       this.getLastNo().addTipoParametro(tipoParametros);
   }
   
   public boolean addNomeParametros(String nomeParametros) {
       NSemantico no = this.getLastNo();
       boolean chave = false;
       if(!no.getParametros().isEmpty()) {
           for(Parametros noP : no.getParametros()) {
               if(noP.getNome().equals(nomeParametros)) {
                   chave = true;
               }
           }
           no.addNomeParametro(nomeParametros);
       }        
       return chave;
   }    
   
   public void addValor(String valor, int seletor) {
       
       if(seletor == 1) {
           
           this.getLastNo().setValor(valor);
       } else if (seletor == 2) {
           
           this.getLastNo().setValor2(valor);
       } else if (seletor == 3) {
           
           this.getLastNo().setValor3(valor);
       } else {

       }
   }
   
   public NSemantico isFunction(String nome) {
       
       for(NSemantico no : this.tabelaSemantica) {
           if(no.getDeclaracao().equals("functin") && no.getNome().equals(nome)) {
               return no;
           }
       }
       return null;
   }
   
   public boolean isStruct(String nome) {
       
       for(NSemantico no : this.tabelaSemantica) {
           if(no.getDeclaracao().equals("struct") && no.getNome().equals(nome)) {
               return true;
           }
       }
       return false;
   }
   
   public void heranca(String nome) {
       
       NSemantico noAtual = this.getLastNo();
       noAtual.heranca(nome);
       this.cadeiaStructs.add(noAtual);
       this.verificarCadeiaExtensao(noAtual, nome);
   }
   
   public void verificarCadeiaExtensao(NSemantico noAtual, String extensaoAtual) {
       for(NSemantico no : this.cadeiaStructs) {
           if(no.getNome().equals(extensaoAtual)) {
               
               no.getExtensoes().forEach(
                   (e) -> {
                       if(!noAtual.getExtensoes().contains(e)) {
                           noAtual.heranca(e);
                           verificarCadeiaExtensao(noAtual, e);
                       }
                   }
               );
           }
       }
   }
   
   public NSemantico getLastFunction() {       
       for(int i = this.tabelaSemantica.size()-1; i >= 0; i--) {
           NSemantico no = this.tabelaSemantica.get(i);  
           if(no.getDeclaracao().equals("function")) {
               return no;
           }            
       }
       return null;
   }
   
   public NSemantico getLastProcedure() {
       for(int i = this.tabelaSemantica.size()-1; i >= 0; i--) {
           NSemantico no = this.tabelaSemantica.get(i);  
           if(no.getDeclaracao().equals("procedure")) {
               return no;
           }            
       }
       return null;
   }
   
   public NSemantico getLastNo() {
       return this.tabelaSemantica.get(this.getLastIndex());
   }
   
   public void addNo(NSemantico no) {
       this.tabelaSemantica.add(no);
   }
   
   public int getLastIndex() {
       return (this.tabelaSemantica.size()-1);
   }
   
   public void addFuncaoPendente(NSemantico no) {
       this.funcoesPendentes.add(no);
   }

   public ArrayList<NSemantico> getFuncoesPendentes() {
       return funcoesPendentes;
   }    
           
   public void addProcedurePendente(NSemantico no) {
       this.proceduresPendentes.add(no);
   }

   public ArrayList<NSemantico> getProceduresPendentes() {
       return proceduresPendentes;
   }

   public NSemantico getNo(int id) {        
       for(NSemantico no : this.tabelaSemantica) {
           if(no.getId() == id) {
               return no;
           }
       }
       return null;
   }
   
   public static int getId() {
      TabelaSemantica.contador++;
      return TabelaSemantica.contador;        
   }
           
   public void printSemanticTable() {  
       this.tabelaSemantica.forEach(
           (no) -> {
               System.out.println("Id: "+no.getId());                
               System.out.println("Declaração: "+no.getDeclaracao());
               System.out.println("Linha da Declaração: "+no.getLinhaDeclaracao());
               if(!no.getTipo().isEmpty())
                   System.out.println("Tipo: "+no.getTipo());
               if(!no.getNome().isEmpty())
                   System.out.println("Nome: "+no.getNome());
               if(!no.getValor().isEmpty())
                   System.out.println("Valor: "+no.getValor());
               if(!no.getExtensoes().isEmpty()) {
                   System.out.println("Cadeia de Extensão: ");
                   no.getExtensoes().forEach(
                       (p) -> {
                           System.out.println("    "+p);
                       }
                   );
               }
               if(!no.getPalavraReservadaEscopo().isEmpty())                
                   System.out.println("Nome Escopo: "+no.getPalavraReservadaEscopo());
               if(!no.getIdentificadorEscopo().isEmpty())
                   System.out.println("Valor Escopo: "+no.getIdentificadorEscopo());                
               if(!no.getParametros().isEmpty()) {
                   System.out.println("Parametros: ");
                   no.getParametros().forEach(
                       (p) -> {
                           System.out.println("    "+p.getTipo()+" "+p.getNome());
                       }
                   );
               }
               if(no.getIdSobrecarga() != 0)
                   System.out.println("ID Sobrecarga: "+no.getIdSobrecarga());
               System.out.println();
           }
       );
       this.printOthers();
   }
   
   public void printOthers() {
       
       System.out.println(" Funções Pendentes "); 
       this.funcoesPendentes.forEach(
           (no) -> {
               System.out.println("Função: "+no.getNome());  
           }
       );        
       System.out.println("\n Procedures Pendentes "); 
       this.proceduresPendentes.forEach(
           (no) -> {
               System.out.println("Procedure: "+no.getNome()); 
           }
       );
   }

}
