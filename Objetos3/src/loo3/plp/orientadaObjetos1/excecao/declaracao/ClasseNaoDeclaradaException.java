package loo3.plp.orientadaObjetos1.excecao.declaracao;

import loo3.plp.expressions2.expression.Id;


/**
 * Exce��o lan�ada quando uma classe que est� sendo referenciada
 * n�o foi declarada anteriormente.
 */
public class ClasseNaoDeclaradaException extends Exception {
    /**
     * Construtor
     * @param id Identificador representando a classe.
     */
    public ClasseNaoDeclaradaException(Id id) {
        this("Classe " + id + " n�o declarada.");
    }

    /**
     * Construtor com mensagem livre, usado por subclasses.
     * @param mensagem a mensagem da excecao.
     */
    protected ClasseNaoDeclaradaException(String mensagem) {
        super(mensagem);
    }
}