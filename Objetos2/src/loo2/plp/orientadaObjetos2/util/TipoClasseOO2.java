package loo2.plp.orientadaObjetos2.util;

import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.util.TipoClasse;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;

/**
 * Tipo nomeado de OO2. Sintaticamente um identificador pode nomear uma
 * classe ou um protocolo, e so o ambiente de compilacao sabe qual dos dois
 * foi declarado. Este tipo e valido quando o nome corresponde a uma classe
 * ou a um protocolo ja declarado.
 *
 * <pre>
 * TipoClasse ::= Id
 * </pre>
 */
public class TipoClasseOO2 extends TipoClasse {

	public TipoClasseOO2(Id tipoClasse) {
		super(tipoClasse);
	}

	public boolean eValido(AmbienteCompilacaoOO1 ambiente) throws ClasseNaoDeclaradaException {
		if (super.eValido(ambiente)) {
			return true;
		}
		return ambiente instanceof AmbienteCompilacaoOO2
				&& ProtocoloUtils.ehProtocolo(getTipo(), (AmbienteCompilacaoOO2) ambiente);
	}
}
