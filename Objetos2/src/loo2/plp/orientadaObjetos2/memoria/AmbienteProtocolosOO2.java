package loo2.plp.orientadaObjetos2.memoria;

import loo2.plp.expressions2.expression.Id;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloJaDeclaradoException;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloNaoDeclaradoException;

/**
 * Operacoes sobre protocolos comuns aos ambientes de compilacao e de
 * execucao de OO2. Classes e protocolos compartilham o mesmo espaco de nomes.
 */
public interface AmbienteProtocolosOO2 {

	/**
	 * Mapeia um identificador de protocolo com a sua definicao.
	 * @param idArg identificador do protocolo
	 * @param defProtocolo definicao do protocolo
	 * @throws ProtocoloJaDeclaradoException Quando ja existe um protocolo ou
	 *         uma classe com esse nome
	 */
	public void mapProtocolo(Id idArg, DefProtocolo defProtocolo) throws ProtocoloJaDeclaradoException;

	/**
	 * Dado o identificador de um protocolo, recupera a sua definicao.
	 * @param idArg identificador do protocolo
	 * @return Definicao do protocolo
	 * @throws ProtocoloNaoDeclaradoException Quando o protocolo nao foi declarado
	 */
	public DefProtocolo getDefProtocolo(Id idArg) throws ProtocoloNaoDeclaradoException;

	/**
	 * Indica se o identificador nomeia um protocolo declarado.
	 * @param idArg identificador a consultar
	 * @return <code>true</code> se existe um protocolo com esse nome
	 */
	public boolean ehProtocolo(Id idArg);
}
