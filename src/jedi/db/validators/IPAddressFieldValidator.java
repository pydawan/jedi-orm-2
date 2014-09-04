package jedi.db.validators;

public class IPAddressFieldValidator extends Validator {	

	@Override
	public void validate() {
		Object value = this.getValue();
		if (value instanceof String && ((String) value).matches("\\d{1,3}+.\\d{1,3}+.\\d{1,3}+.\\d{1,3}+")) {
			this.isValid = true;
		} else {
			this.isValid = false;
		}
	}
	
}