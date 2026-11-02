package loo2.plp.orientadaObjetos2.excecao;

import loo2.plp.expressions2.expression.Id;

/**
 * Excecao lancada quando o protocolo procurado nao foi declarado.
 */
public class ProtocoloNaoDeclaradoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor
     * @param id Identificador representando o protocolo.
     */
    public ProtocoloNaoDeclaradoException(Id id) {
        super("Protocolo " + id + " nao declarado.");
    }
}
