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
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@PostConstruct
	public void init() {
		Map<String, String> printMap = new MetaDataParserApplication().getCreationDetails(new File(env.getProperty("inputFileName")));
		List<String> contents = new ArrayList<>();
		for (Map.Entry<String, String> map : printMap.entrySet()) {
			contents.add(map.getKey()+map.getValue());
		}
		writeToFile(contents);
	}
	
	/**
	 * This method returns one of the metadata for any file. Similarly, we could
	 * add many more.
	 * 
	 * @param file
	 * @return
	 */
	public Map<String, String> getCreationDetails(File file) {
		Map<String, String> myReturnMap = new HashMap<>();
		try {
			Path p = Paths.get(file.getAbsolutePath());
			BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
			FileTime fileTime = view.creationTime();
			myReturnMap.put("Printing Creation Time of input file: ", "" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format((fileTime.toMillis())));
			// your reference
			FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(p, FileOwnerAttributeView.class);
	        UserPrincipal owner = ownerAttributeView.getOwner();
	        myReturnMap.put("Printing Owner: ", owner.getName());
			//
		} catch (IOException e) {
			e.printStackTrace();
		}
		return myReturnMap;
	}
	/**
	 * This method writes meta-data to output file.
	 * 
	 * @param content
	 */
	public void writeToFile(List<String> contents) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(env.getProperty("outputFileName"));
			bw = new BufferedWriter(fw);
			for(String s: contents){
				bw.write(s+"\n");
			}
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
