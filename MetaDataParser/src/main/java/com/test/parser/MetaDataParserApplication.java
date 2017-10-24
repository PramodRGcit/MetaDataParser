package com.test.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class MetaDataParserApplication {

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(MetaDataParserApplication.class, args);
	}

	/**
	 * This method returns one of the metadata for any file. Similarly, we could
	 * add many more.
	 * 
	 * @param file
	 * @return
	 */
	public String getCreationDetails(File file) {
		try {
			Path p = Paths.get(file.getAbsolutePath());
			BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
			FileTime fileTime = view.creationTime();
			return ("" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format((fileTime.toMillis())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@PostConstruct
	public void init() {
		writeToFile("Printing Creation Time of input file: "
				+ new MetaDataParserApplication().getCreationDetails(new File(env.getProperty("inputFileName"))));
	}

	/**
	 * This method writes meta-data to output file.
	 * 
	 * @param content
	 */
	public void writeToFile(String content) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(env.getProperty("outputFileName"));
			bw = new BufferedWriter(fw);
			bw.write(content);
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
