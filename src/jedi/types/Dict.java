package jedi.types;

import java.util.HashMap;

@SuppressWarnings({ "serial" })
public class Dict extends HashMap<String, Object> {
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {		
		return (T) super.get(key);
	}
}