package br.com.uefs.Analisador;

import br.com.uefs.Model.*;
import br.com.uefs.Util.*;

public class Lexico {
	public String analisar(String texto, Token tokens, TabelaSimbolo simbolos){

        int i = 0;
        int linha = 1;
        String erros = "";
        String anterior = "";
        do {
            char atual = texto.charAt(i);  
            if(operadoresLogicos(Character.toString(atual))){
                int j = i+1;
                char atual2 = texto.charAt(j);                
                if(atual == '&') {
                    if(atual2 == '&') {    
                        tokens.addToken("Op.Logico", "&&", linha);
                        anterior = "&&";
                        i++;
                    } else {
                        tokens.addToken("Op.Logico", Character.toString(atual), linha);
                        anterior = Character.toString(atual);
                    }
                } else if(atual == '|') {    
                    if(atual2 == '|') {
                        tokens.addToken("Op.Logico", "||", linha);
                        anterior = "||";
                        i++;
                    } else {
                        tokens.addToken("Op.Logico", Character.toString(atual), linha);
                        anterior = Character.toString(atual);
                    }
                }
            } else if(operadoresRelacionais(Character.toString(atual))){
                int j = i+1;
                char atual2 = texto.charAt(j);                
                if(atual == '!') {
                    if(atual2 == '=') {  
                        tokens.addToken("Op.Relacional", "!=", linha);
                        anterior = "!=";
                        i++;
                    } else {
                        tokens.addToken("Op.Logico", Character.toString(atual), linha);
                        anterior = Character.toString(atual);
                    }
                } else if(atual == '=') {
                    if(atual2 == '=') {
                        tokens.addToken("Op.Relacional", "==", linha);
                        anterior = "==";
                        i++;
                        
                    } else {
                        tokens.addToken("Op.Relacional", Character.toString(atual), linha);
                        anterior = Character.toString(atual);
                    }
                } else if(atual == '<') {
                    if(atual2 == '=') {
                        tokens.addToken("Op.Relacional", "<=", linha);
                        anterior = "<=";
                        i++;
                    } else {
                        tokens.addToken("Op.Relacional", Character.toString(atual), linha);

                        anterior = Character.toString(atual);
                    }
                } else if(atual == '>') {
                    if(atual2 == '=') {
                        tokens.addToken("Op.Relacional", ">=", linha);
                        anterior = ">=";
                        i++;
                    } else {
                        tokens.addToken("Op.Relacional", Character.toString(atual), linha);
                        anterior = Character.toString(atual);
                    }
                }
            } else if(delimitadores(Character.toString(atual))){
                tokens.addToken("Delimitador", Character.toString(atual), linha);
                anterior = Character.toString(atual);
 
            } else if(operadoresAritmeticos(Character.toString(atual))) {
                int j = (i+1);
                char atual2 = texto.charAt(j);                                
                if(atual == '/') {                    
                    if(atual2 == '/') {
                        while(atual2 != '\n') { 
                            i++; 

                            if(i >= texto.length()) {
                                break;
                            } 
                            atual2 = texto.charAt(i);                        
                        }    
                        linha++;  
                    } else if (atual2 == '*') {   
                        char anterior2 = '§';

                        boolean chave = true;
                        while(chave) { 
                            if(anterior2 == '*' && atual2 == '/'){
                                chave = false;                                
                            } else {
                                if(atual2 == '\n') {
                                    linha++;
                                }
                                i++; 
                                if(i >= texto.length()) {
                                    linha--;
                                    erros += "Erro na linha "+linha+" - Fecha comentario não encontrado!\n";
                                    break;
                                } 
                                anterior2 = atual2;
                                atual2 = texto.charAt(i); 
                            }                                                   
                        }
                    } else {
                        tokens.addToken("Op.Aritmetico", Character.toString(atual), linha); 
                        anterior = Character.toString(atual);
                    }
                } else if(atual == '+') {
                    if(atual2 == '+') {
                        tokens.addToken("Op.Aritmetico", "++", linha); 
                        anterior = "++";
                        i++;
                    } else {
                        tokens.addToken("Op.Aritmetico", Character.toString(atual), linha);
                        anterior = Character.toString(atual);
                    }                    
                } else if(atual == '-') {
                    if(atual2 == '-') {
                        tokens.addToken("Op.Aritmetico", "--", linha); 
                        anterior = "--";
                        i++;
                    } else {
                        int linhaAux = linha;
                        while(espaco(Character.toString(atual2))){
                            if(atual2 == '\n') {
                                linhaAux++;
                            }
                            j++;

                            if(j >= texto.length()) {                                
                                break;
                            } 
                            atual2 = texto.charAt(j);
                        } 
                        if(digito(Character.toString(atual2))) {
                            linha = linhaAux;
                            String numero = "";
                            if(verificaAnterior(anterior)) {
                                numero += "-";
                            } else {
                                tokens.addToken("Op.Aritmetico", Character.toString(atual), linha);                            
                            } 
                            while(digito(Character.toString(atual2))){                                
                                numero += Character.toString(atual2);
                                j++;
                                if(j >= texto.length()) {                                
                                    break;
                                } 
                                atual2 = texto.charAt(j);
                            }
                            if(atual2 == '.') {                                    
                                j++;
                                if(j < texto.length()) { 
                                    atual2 = texto.charAt(j);
                                    if(digito(Character.toString(atual2))) {
                                        numero += ".";
                                        while(digito(Character.toString(atual2))){                                
                                            numero += Character.toString(atual2);
                                            j++;
                                            if(j >= texto.length()) {                                
                                                break;
                                            } 
                                            atual2 = texto.charAt(j);
                                        }
                                        tokens.addToken("Numero", numero, linha);  
                                        anterior = numero;
                                        i = (j-1);
                                    } else {
                                        tokens.addToken("Numero", numero, linha); 
                                        anterior = numero;
                                        i = (j-2);
                                    } 
                                } else {
                                    tokens.addToken("Numero", numero, linha); 
                                    anterior = numero;
                                    i = (j-2);
                                }                                                                    
                            } else {
                                tokens.addToken("Numero", numero, linha);  
                                anterior = numero;
                                i = (j-1);
                            }                              
                        } else {
                            tokens.addToken("Op.Aritmetico", Character.toString(atual), linha);
                            anterior = Character.toString(atual);
                        }                          
                    }
                } else if(atual == '*') {
                    tokens.addToken("Op.Aritmetico", Character.toString(atual), linha);  
                    anterior = Character.toString(atual);
                }  
            } else if(letra(Character.toString(atual))) {  
                String palavra = Character.toString(atual);
                int j = (i+1);
                char atual2 = texto.charAt(j);    
                while(condicaoFinal(Character.toString(atual2))) { 
                    palavra += Character.toString(atual2);
                    j++;                    
                    if(j >= texto.length()) {
                        break;
                    } else {
                        atual2 = texto.charAt(j);
                    }                                             
                }
                if(palavrasReservadas(palavra)) {
                    tokens.addToken("Palavra_Reservada", palavra, linha);  
                    anterior = palavra;
                } else {     
                    String nome;
                    if(simbolos.contem(palavra)) {
                        nome = simbolos.getNomeSimbolo(palavra);
                    } else {
                        nome = simbolos.addSimbolo(palavra);
                    }
                    tokens.addToken(nome, palavra, linha);  
                    anterior = nome;
                }
                i = (j-1);   
            } else if(atual == '"') {   
                i++;
                char atual2 = texto.charAt(i); 
                int linhaInicial = linha;
                String cadeia = "";
                boolean controleErro = true;
                while(atual2 != '"') {                     
                    if(atual2 == '\n'){
                        linha++;
                    }                     
                    if(atual2 == '\\'){
                        char verifica = texto.charAt(i+1);
                        if(verifica == '"') {
                            cadeia += '\"';
                            i = (i+2);
                        } else {
                            cadeia += Character.toString(atual2);

                            i++; 
                        } 
                    } else {
                        cadeia += Character.toString(atual2);
                        i++;   
                    } 
                    if(i >= texto.length()) {
                        linha--;
                        erros += "Erro na linha "+linha+" - Fecha aspas não encontrado!\n";                          
                        controleErro = false;
                        break;
                    } else {
                        atual2 = texto.charAt(i);
                    } 
                }
                if(controleErro) {
                    if(cadeiaDeCaracteres(cadeia)){      
                        tokens.addToken("Cadeia_de_Caracteres", cadeia, linha); 
                        anterior = cadeia;
                    } else {
                        erros += "Erro na linha "+linha+" - Cadeia de caracteres inválida!\n";
                    }   
                }        
            } else if(digito(Character.toString(atual))) {
                String numero = Character.toString(atual);
                int j = (i+1);
                char atual2 = texto.charAt(j); 
                while(digito(Character.toString(atual2))) {   
                    numero += Character.toString(atual2);
                    j++;

                    if(j >= texto.length()) {                                
                        break;
                    } 
                    atual2 = texto.charAt(j);
                }

                if(atual2 == '.') {                    

                    j++;

                    if(j < texto.length()) { 
                        atual2 = texto.charAt(j);   
                        if(digito(Character.toString(atual2))) {
                            numero += ".";            
                            while(digito(Character.toString(atual2))){                           
                                numero += Character.toString(atual2);
                                j++;
                                if(j >= texto.length()) {                                
                                    break;
                                } 
                                atual2 = texto.charAt(j);
                            }
                            tokens.addToken("Numero", numero, linha);  
                            anterior = numero;
                            i = (j-1);
                        } else {
                            tokens.addToken("Numero", numero, linha); 
                            anterior = numero;
                            i = (j-2);
                        } 
                    } else {
                        tokens.addToken("Numero", numero, linha); 
                        anterior = numero;
                        i = (j-2);
                    }                                                                    
                } else {
                    tokens.addToken("Numero", numero, linha);  
                    anterior = numero;
                    i = (j-1);
                }
            } else if(atual == '\n'){

                linha++; 
            } else if(atual != ' ' && atual != '\t' && atual != '\r') {
                erros += "Erro na linha "+linha+" - Caractere inválido\n";
            }
            i++;        
        } while(i < texto.length());     
        return erros;
    }
    
    public boolean palavrasReservadas(String palavra) {
        
        if(palavra.equals("const")) {
            return true;
        } else if(palavra.equals("var")) {
            return true;
        } else if(palavra.equals("struct")) {
            return true;
        } else if(palavra.equals("typedef")) {
            return true;
        } else if(palavra.equals("procedure")) {
            return true;
        } else if(palavra.equals("function")) {
            return true;
        } else if(palavra.equals("return")) {
            return true;
        } else if(palavra.equals("start")) {
            return true;
        } else if(palavra.equals("if")) {
            return true;
        } else if(palavra.equals("then")) {
            return true;
        } else if(palavra.equals("else")) {
            return true;
        } else if(palavra.equals("while")) {
            return true;
        } else if(palavra.equals("scan")) {
            return true;
        } else if(palavra.equals("print")) {
            return true;
        } else if(palavra.equals("int")) {
            return true;
        } else if(palavra.equals("float")) {
            return true;
        } else if(palavra.equals("bool")) {
            return true;
        } else if(palavra.equals("string")) {
            return true;
        } else if(palavra.equals("true")) {
            return true;
        } else if(palavra.equals("false")) {
            return true;
        } else if(palavra.equals("extends")) {
            return true;
        }
        return false;
    }
    

    public boolean operadoresAritmeticos(String caractere) {
        return caractere.matches("[\\+|\\-|\\*|/]");
    }
    
    public boolean operadoresRelacionais(String caractere) {
        return caractere.matches("[!|=|<|>]");
    }

    public boolean operadoresLogicos(String caractere) {
        if(caractere.equals("|")) {
            return true;
        } else {
            return caractere.matches("[&]");
        }
    }
    
    public boolean delimitadores(String caractere) {
        return caractere.matches("[;|,|\\(|\\)|\\[|\\]|\\{|\\}|\\.]");
    }

    public boolean cadeiaDeCaracteres(String cadeia) {
        String especiais = "\\]|\\[|\\+|\\$|\\^|\\{|\\}|\\|\\?|\\.|\\(|\\)|\\*|\\-|\\\\|\"";
        String outros = "a-z|A-Z|0-9|";
        String asc = " |#|!|%|´|`|@|/|~|_|<|>|=|:|;|,|'|&";
        return cadeia.matches("["+especiais+outros+asc+"]*");
    }

    public boolean condicaoFinal(String caractere) {
        return caractere.matches("[a-z|A-Z|0-9|_]*");
    }

    public boolean letra(String caractere){
        return caractere.matches("[a-z|A-Z]"); 
    }
    
 
    public boolean espaco(String caractere) {
        return caractere.matches("[\t|\n|\r| ]"); 
    }

    public boolean digito(String caractere) {
        return caractere.matches("[0-9]"); 
    }

    public boolean verificaAnterior(String anterior) {
        return anterior.matches("[!|\\=|\\<|\\>|\\+|\\-|\\*|/|\\(|,]*");
    }

}
