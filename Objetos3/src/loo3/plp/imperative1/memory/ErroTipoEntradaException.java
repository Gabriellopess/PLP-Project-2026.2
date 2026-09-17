package loo3.plp.imperative1.memory;

public class ErroTipoEntradaException extends Exception {
	
	public ErroTipoEntradaException() {
	    super("Tipo do valor de entrada lido incomp�tivel");
	  }
	
	public ErroTipoEntradaException(String msg) {
	    super(msg);
	  }

}
