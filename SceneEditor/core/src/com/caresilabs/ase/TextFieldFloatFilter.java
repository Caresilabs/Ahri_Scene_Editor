package com.caresilabs.ase;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class TextFieldFloatFilter implements TextField.TextFieldFilter {

	@Override
	public boolean acceptChar ( TextField textField, char c ) {
		if (c == '.' && textField.getText().contains(".")) return false;
		if (c == '-' && textField.getText().contains("-")) return false;
		
		return  Character.isDigit(c) || c == '.' || c == '-';
	}

}
