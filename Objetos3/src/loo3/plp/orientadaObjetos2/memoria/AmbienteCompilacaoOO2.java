package loo3.plp.orientadaObjetos2.memoria;

import loo3.plp.expressions2.expression.Id;
import loo3.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo3.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo3.plp.orientadaObjetos3.excecao.declaracao.ProtocoloJaDeclaradoException;
import loo3.plp.orientadaObjetos3.excecao.declaracao.ProtocoloNaoDeclaradoException;
import loo3.plp.orientadaObjetos2.util.SuperClasseMap;
import loo3.plp.orientadaObjetos3.memoria.DefProtocolo;

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
	 * Mapeia um identificador de protocolo a sua definicao.
	 * @param protocolo identificador do protocolo
	 * @param defProtocolo definicao (assinaturas) do protocolo
	 * @throws ProtocoloJaDeclaradoException Quando ja existe protocolo com esse nome
	 */
	public void mapDefProtocolo(Id protocolo, DefProtocolo defProtocolo) throws ProtocoloJaDeclaradoException;

	/**
	 * Dado um identificador de protocolo, recupera sua definicao.
	 * @param protocolo identificador do protocolo
	 * @return Definicao do protocolo
	 * @throws ProtocoloNaoDeclaradoException Quando o protocolo nao foi declarado
	 */
	public DefProtocolo getDefProtocolo(Id protocolo) throws ProtocoloNaoDeclaradoException;

}
