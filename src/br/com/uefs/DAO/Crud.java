package br.com.uefs.DAO;


import java.io.*;


public class Crud {
	
	  /**
     * Obtem todos os arquivos .txt da pasta Entrada.
     * @return 
     */
	
    public String[] getArquivos() {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".txt");
            }
        };
        File dir = new File("Entrada");
        File[] files = dir.listFiles(filter); 
        String[] arquivos = new String[files.length];
        for(int i=0; i<files.length; i++) { 
            arquivos[i] = files[i].toString();
        }
        return arquivos;
    }
    
    /**
     * Obtem o conteudo do Entrada.
     * @param nomeArquivo String
     * @return 
     */
    public String lerArquivo(String nomeArquivo) {
        try {
            File file = new File(nomeArquivo);
            InputStreamReader ler = new InputStreamReader(new FileInputStream(file));       
            
            FileReader fileReader = new FileReader(nomeArquivo);
            BufferedReader arq = new BufferedReader(fileReader);      
                        
            String texto = ""; 
            String linha = arq.readLine();           
            while(linha != null){                
                texto += linha+"\n"; 
                linha = arq.readLine();
            }   
            arq.close();            
            return texto;
        } catch(IOException e) {
            return e.getMessage();
        }        
    } 
    
    /**
     * Adiciona um conteudo a um arquivo.
     * @param errosLexicos String
     * @param errosSintaticos String
     * @param errosSemanticos String
     * @param nomeArquivo String
     * @return 
     */
    public boolean salvaArquivo(String errosLexicos, String errosSintaticos, String errosSemanticos, String nomeArquivo) {
        try{
            File file = new File(nomeArquivo);       
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);        
            BufferedWriter escrever = new BufferedWriter(fileWriter);
            
            escrever.write(" Código compilados  com sucesso ! ");
            escrever.newLine();
                        
            if(!errosLexicos.isEmpty()) {
                escrever.newLine();
                escrever.write("   Erros  léxicos ");
                escrever.newLine ();
                for(int i=0; i<errosLexicos.length(); i++){
                    if(errosLexicos.charAt(i) == '\n'){
                        escrever.newLine ();
                    } else {
                        escrever.write(errosLexicos.charAt(i));
                    }                
                }
            } else {
                escrever.newLine();
                escrever.write(" Erros  léxicos ");
                escrever.newLine ();
                escrever.write("Não foram encontrados erros Léxicos!");
                escrever.newLine ();
            }
            if(!errosSintaticos.isEmpty()) {
                escrever.newLine ();
                escrever.write("  Erros sintáticos  ");
                escrever.newLine ();
                for(int i=0; i<errosSintaticos.length(); i++){
                    if(errosSintaticos.charAt(i) == '\n'){
                        escrever.newLine ();
                    } else {
                        escrever.write(errosSintaticos.charAt(i));
                    }                
                }
            } else {
                escrever.newLine ();
                escrever.write("  Erros sintáticos ");
                escrever.newLine ();
                escrever.write("Não foram encontrados erros Sintáticos!");
                escrever.newLine ();
            }            
            if(!errosSemanticos.isEmpty()) {
                escrever.newLine ();
                escrever.write(" Erros semânticos ");
                escrever.newLine ();
                for(int i=0; i<errosSemanticos.length(); i++){
                    if(errosSemanticos.charAt(i) == '\n'){
                        escrever.newLine ();
                    } else {
                        escrever.write(errosSemanticos.charAt(i));
                    }                
                }
            } else {
                escrever.newLine ();
                escrever.write("   Erros semânticos ");
                escrever.newLine ();
                escrever.write("Não foram encontrados erros Semânticos!");
                escrever.newLine ();
            }          
            escrever.close();
            return true;
        } catch(IOException e) {
            e.getMessage();
            return false;
        }
    }

}
