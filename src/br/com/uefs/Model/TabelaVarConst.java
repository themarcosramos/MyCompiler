package br.com.uefs.Model;

import java.util.ArrayList;

import br.com.uefs.Util.VarConst;

public class TabelaVarConst {
	private ArrayList<VarConst> VarConst;

    public TabelaVarConst() {
        this.VarConst = new ArrayList();
    }

    public ArrayList<VarConst> getVarConst() {
        return VarConst;
    }

    public void addVarConst(VarConst varConst) {
        this.VarConst.add(varConst);
    }

}
