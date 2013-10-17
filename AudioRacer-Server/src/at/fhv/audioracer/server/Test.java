package at.fhv.audioracer.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
	
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(Test.class);
		logger.info("Slf4j says: {}{}", "Hello", "!");
		
		logger.info("Slf4j with multiple args: {}, {}, {}, {}", 
				new Object[] { 10, "foo", 20, "bar" });
	}
}
