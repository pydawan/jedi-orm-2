package jedi.db.validators;


public abstract class Validator {
	
	protected boolean isValid;
	protected Object value;
	protected String errorMessage;
	
	public abstract void validate();
	
	public boolean isValid() {
		return this.isValid;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
