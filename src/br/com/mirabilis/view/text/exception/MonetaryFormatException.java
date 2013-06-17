package br.com.mirabilis.view.text.exception;

/**
 * Classe de exce��o da formata��o de uma String para monet�rio.
 * @author Rodrigo Sim�es Rosa
 *
 */
public class MonetaryFormatException extends Exception {

	/**
	 * Serializa��o
	 */
	private static final long serialVersionUID = 1L;
	
	public MonetaryFormatException() {
		super("Ocorreu um erro na formata��o");
	}
	
	public MonetaryFormatException(String value){
		super(value);
	}
	
	public MonetaryFormatException(Throwable e){
		super(e);
	}
}
