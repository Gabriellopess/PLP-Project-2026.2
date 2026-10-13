package loo2.plp.orientadaObjetos2.excecao;

import loo2.plp.expressions2.expression.Id;

/**
 * Excecao lancada quando o protocolo que esta sendo declarado ja o foi
 * anteriormente, ou quando ja existe uma classe com o mesmo nome (classes e
 * protocolos compartilham o mesmo espaco de nomes).
 */
public class ProtocoloJaDeclaradoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor
     * @param id Identificador representando o protocolo.
     */
    public ProtocoloJaDeclaradoException(Id id) {
        super("Protocolo " + id + " ja declarado (ja existe classe ou protocolo com esse nome).");
    }
}
