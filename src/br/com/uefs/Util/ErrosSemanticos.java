package br.com.uefs.Util;

public class ErrosSemanticos {
	  private String errosSemanticos;

	    public ErrosSemanticos() {
	        this.errosSemanticos = "";
	    }

	    public String getErrosSemanticos() {
	        return errosSemanticos;
	    }

	    public void addErroSemantico(String erroSemantico) {
	        this.errosSemanticos += erroSemantico;
	    }

}
