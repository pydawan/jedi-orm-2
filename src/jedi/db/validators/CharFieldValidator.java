package jedi.db.validators;

public class CharFieldValidator extends Validator {

	@Override
	public void validate() {
		if (this.getValue() instanceof String) {
			this.isValid = true;
		} else {
			this.isValid = false;
		}
	}	
}