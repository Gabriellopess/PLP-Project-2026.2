package loo2.plp.orientadaObjetos2.excecao;

/**
 * Erro de execucao do duck typing: uma chamada ou um valor vindo de uma
 * expressao dyn nao pode ser atendido pela classe real do objeto.
 *
 * Estende RuntimeException para nao alterar a clausula throws de todos os
 * comandos (decisao do README). Distingue-se do "Erro de tipo", que e
 * detectado antes da execucao.
 */
public class ErroDuckTypingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErroDuckTypingException(String mensagem) {
        super(mensagem);
    }

    /**
     * Sem o nome do pacote, para a mensagem sair como no README.
     */
    @Override
    public String toString() {
        return "ErroDuckTypingException: " + getMessage();
    }
}
