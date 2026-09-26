package loo2.plp.orientadaObjetos2.memoria;

import loo2.plp.expressions2.expression.Id;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloJaDeclaradoException;
import loo2.plp.orientadaObjetos2.excecao.ProtocoloNaoDeclaradoException;
import loo2.plp.orientadaObjetos2.util.SuperClasseMap;

public interface AmbienteCompilacaoOO2 extends AmbienteCompilacaoOO1{
	
	/**
	 * Mapeia um identificador de classe com o identificador da super-classe.
	 * @param classe identificador da sub-classe
	 * @param superClasse identificador da super-classe
	 * @throws ClasseNaoDeclaradaException Quando a super-classe nao foi declarada
	 */
	public void mapSuperClasse(Id classe, Id superClasse) throws ClasseNaoDeclaradaException;	

	/**
	 * Dado um identificador da classe, recupera a super-classe
	 * @param classe identificador da classe base
	 * @return Definicao da super classe
	 * @throws ClasseNaoDeclaradaException Quando a classe ainda nao foi definida
	 */
	public SuperClasseMap getSuperClasse(Id classe) throws ClasseNaoDeclaradaException;

	/**
	 * Registra um protocolo. Classes e protocolos compartilham o mesmo espaco de nomes.
	 * @param protocolo identificador do protocolo
	 * @param defProtocolo definicao do protocolo
	 * @throws ProtocoloJaDeclaradoException Quando o nome ja e usado por uma classe ou protocolo
	 */
	public void mapProtocolo(Id protocolo, DefProtocolo defProtocolo) throws ProtocoloJaDeclaradoException;

	/**
	 * Recupera a definicao de um protocolo.
	 * @param protocolo identificador do protocolo
	 * @throws ProtocoloNaoDeclaradoException Quando o protocolo nao foi declarado
	 */
	public DefProtocolo getDefProtocolo(Id protocolo) throws ProtocoloNaoDeclaradoException;

	/**
	 * Indica se o nome dado e de um protocolo declarado.
	 * @param nome identificador a verificar
	 */
	public boolean ehProtocolo(Id nome);
}
