package com.CODEXIS.PopularDocuments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@SpringBootApplication
public class PopularDocumentsApplication {
	public static Logger logger = Logger.getLogger(PopularDocumentsApplication.class.getName());

	public static void main(String[] args) {
		FileHandler handler = null;
		try {
			String path = ".//queries//" + new SimpleDateFormat("yyyy_MM_dd").format(new Date()) + ".txt";
			File dir = new File(".//queries//");
			if(!dir.exists()){
				dir.mkdirs();
			}
			handler = new FileHandler(path,0,1, true);
			logger.addHandler(handler);
			handler.setFormatter(new SimpleFormatter());
			SpringApplication.run(PopularDocumentsApplication.class, args);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			handler.flush();
			handler.close();
		}
	}
}
