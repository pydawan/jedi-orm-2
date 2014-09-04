package jedi.db.validators;

public class MeuCharFieldValidator extends CharFieldValidator {
	
	public void validate() {		
		if (super.isValid() && this.getValue().toString().startsWith("Pa")) {
			isValid = true; 
		} else {
			isValid = false;
		}
	}
}
