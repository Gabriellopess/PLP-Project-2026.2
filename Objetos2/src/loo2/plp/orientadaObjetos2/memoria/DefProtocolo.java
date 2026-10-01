package loo2.plp.orientadaObjetos2.memoria;

import java.util.List;

import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos2.declaracao.protocolo.Assinatura;
import loo2.plp.orientadaObjetos2.declaracao.protocolo.ListaAssinatura;

/**
 * Definicao de um protocolo, guardada nos ambientes de compilacao e de
 * execucao (equivalente a DefClasse para as classes).
 */
public class DefProtocolo {

    /**
     * Nome do protocolo.
     */
    private Id idProtocolo;

    /**
     * Assinaturas dos metodos exigidos pelo protocolo.
     */
    private ListaAssinatura assinaturas;

    /**
     * Construtor.
     *
     * @param idProtocolo nome do protocolo.
     * @param assinaturas assinaturas dos metodos do protocolo.
     */
    public DefProtocolo(Id idProtocolo, ListaAssinatura assinaturas) {
        this.idProtocolo = idProtocolo;
        this.assinaturas = assinaturas;
    }

    public Id getIdProtocolo() {
        return idProtocolo;
    }

    /**
     * Procura a assinatura do metodo com o nome dado.
     *
     * @param nomeMetodo nome do metodo.
     * @return a assinatura, ou <code>null</code> se o protocolo nao tiver
     *          esse metodo.
     */
    public Assinatura getAssinatura(Id nomeMetodo) {
        return assinaturas.getAssinatura(nomeMetodo);
    }

    /**
     * Retorna as assinaturas do protocolo na ordem em que foram declaradas.
     */
    public List<Assinatura> getAssinaturas() {
        return assinaturas.getAssinaturas();
    }
}
