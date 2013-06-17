package br.com.mirabilis.view.text;

import android.text.Editable;
import android.text.Selection;
import android.widget.EditText;

/**
 * Classe de controle de otimiza��es nos campos edi��o de texto.
 * @author Rodrigo Sim�es Rosa
 *
 */
public class FormatterEditText {
	
	/**
	 * Seta o posicionamento do cursor para o ultimo caracter do campo de texto.
	 * @param txtField
	 */
	public static void setFocusLastPosition(EditText txtField){
		int pos = txtField.getText().length();
		Editable editable = (Editable) txtField.getText();
		Selection.setSelection(editable, pos);
	}
}
