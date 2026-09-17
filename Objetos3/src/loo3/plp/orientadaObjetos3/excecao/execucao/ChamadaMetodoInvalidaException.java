package loo3.plp.orientadaObjetos3.excecao.execucao;

/**
 * Erro de execucao de uma chamada de metodo que nao pode ser verificada
 * estaticamente (duck typing dinamico): receptor que nao e um objeto ou
 * argumentos incompativeis com os parametros formais do metodo encontrado.
 *
 * E uma excecao nao verificada porque so ocorre em chamadas sobre valores de
 * tipo <code>dyn</code>; chamadas verificadas estaticamente nunca a lancam.
 */
public class ChamadaMetodoInvalidaException extends RuntimeException {

	public ChamadaMetodoInvalidaException(String mensagem) {
		super(mensagem);
	}
}
