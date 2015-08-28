import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.nio.charset.Charset;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;

public class FileSearch {
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: java Search <fileRegex> <startPath>");
		}
		else {
			String startingFile = args[1];
			String fileRegex = args[0];
			
			try {
				Pattern.compile(args[0]);
			}
			catch (PatternSyntaxException e) {
				System.out.println("Invalid regex");
				return;
			}
			
			System.out.println("Loading files...");
			List<FileContent> files = FileLoader(new File[] {new File(startingFile)}, fileRegex);
			System.out.println("Done loading files!");
			
			if (files == null) { System.out.println("No files to search."); return; }
			
			System.out.println("Files loaded: " + files.size());
			
			for (FileContent file : files) {
				System.out.println(file.filename);
			}
		}
	}
	
	public static List<FileContent> FileLoader(File[] files, String fileRegex) {
		return FileLoader(files, fileRegex, new LinkedList<FileContent>());
	}
	
	public static List<FileContent> FileLoader(File[] files, String fileRegex, List<FileContent> list){
		for (File file : files) {
			if (file.isDirectory()) {
				return FileLoader(file.listFiles(), fileRegex, list);
			} else {
				if (file.getName().matches(fileRegex)) {
					FileContent content = new FileContent();
					content.filename = file.getName();
					
					try {
						content.content = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
						list.add(content);
					}
					catch (IOException e) {
						System.out.println("IO Error while reading file: " + file.getName());
					}
				}
			}
		}
		return list;
	}
	
	static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
