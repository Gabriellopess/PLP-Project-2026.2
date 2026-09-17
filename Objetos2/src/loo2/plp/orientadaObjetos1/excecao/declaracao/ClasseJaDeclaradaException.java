package loo2.plp.orientadaObjetos1.excecao.declaracao;

import loo2.plp.expressions2.expression.Id;


/**
 * Exce��o lan�ada quando a classe que est� sendo declarada, j� o foi
 * anteriormente.
 */
public class ClasseJaDeclaradaException extends Exception {
    /**
     * Construtor
     * @param id Identificador representando a classe.
     */
    public ClasseJaDeclaradaException(Id id) {
        this("Classe " + id + " j� declarada.");
    }

    /**
     * Construtor com mensagem livre, usado por subclasses.
     * @param mensagem a mensagem da excecao.
     */
    protected ClasseJaDeclaradaException(String mensagem) {
        super(mensagem);
    }
}
