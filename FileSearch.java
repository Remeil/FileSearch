import java.io.File;
import java.util.*;
import java.util.regex.*;
import java.nio.charset.Charset;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;

public class FileSearch {
	final static boolean DEBUG = true;
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		String fileRegex;
		String startingFile;
		Matcher regexMatcher;
		
		if (args.length >= 2) {
			fileRegex = args[0];
			startingFile = args[1];
		}
		else {
			System.out.println("Also usable from the command line: java FileSearch <fileRegex> <startingFile>");
			System.out.print("Regex to match on for filenames: ");
			fileRegex = scan.nextLine();
			System.out.print("Staring file search location: ");
			startingFile = scan.nextLine();
		}
		
		try {
			Pattern pattern = Pattern.compile(fileRegex);
			regexMatcher = pattern.matcher("");
		}
		catch (PatternSyntaxException e) {
			System.out.println("Invalid regex");
			return;
		}
		
		System.out.println("Loading files...");
		List<FileContent> files = FileLoader(new File[] {new File(startingFile)}, regexMatcher);
		System.out.println("Done loading files!");
		
		if (files == null) { System.out.println("No files to search."); return; }
		
		System.out.println("Files loaded: " + files.size());
		
		for (FileContent file : files) {
			System.out.println(file.filename);
			System.out.println(file.content);
		}
		
		String searchRegex;
		
		while (true) {
			System.out.print("\nEnter regex to search for in files (\\\\exit exits): ");
			searchRegex = scan.nextLine();
			
			if (DEBUG) {System.out.println(searchRegex);}
			
			if (searchRegex.equals("\\\\exit")) {
				return;
			}
			
			try {
				Pattern.compile(searchRegex);
			}
			catch (PatternSyntaxException e) {
				System.out.println("Invalid regex");
				continue;
			}
			
			for (FileContent file : files) {
				regexMatcher.reset(file.content);
				if (regexMatcher.find()) {
					System.out.println(file.filename);
				}
			}
		}
	}
	
	public static List<FileContent> FileLoader(File[] files, Matcher regexMatcher) {
		return FileLoader(files, regexMatcher, new LinkedList<FileContent>());
	}
	
	public static List<FileContent> FileLoader(File[] files, Matcher regexMatcher, List<FileContent> list){
		for (File file : files) {
			if (file.isDirectory()) {
				FileLoader(file.listFiles(), regexMatcher, list);
			} else {
				regexMatcher.reset(file.getName());
				if (regexMatcher.find()) {
					FileContent content = new FileContent();
					content.filename = file.getName();
					
					try {
						content.content = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
						list.add(content);
						if (DEBUG) { System.out.println("File " + list.size() + " added! " + file.getName()); }
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
