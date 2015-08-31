import java.io.File;
import java.util.*;
import java.util.regex.*;
import java.nio.charset.Charset;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;

public class FileSearch {
	final static boolean DEBUG = false;
	
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
		List<FileContent> files;
		try { 
			files = FileLoader(new File[] {new File(startingFile)}, regexMatcher);
		}
		catch (OutOfMemoryError e) {
			files = null;
			System.out.println("Ran out of memory! Try a more specific search.");
			return;
		}
		catch (Exception e) {
			System.out.println("Some unknown error occured.");
			if (DEBUG) { throw e; }
			else { return; }
		}
		System.out.println("Done loading files!");
		
		if (files == null || files.size() == 0) { System.out.println("No files to search."); return; }
		
		for (FileContent file : files) {
			System.out.println(file.filename);
		}
		
		System.out.println("Files loaded: " + files.size());
		
		
		while (true) {
			String searchRegex;
			Matcher searchMatcher;
			
			System.out.print("\nEnter regex to search for in files (\\\\exit exits): ");
			searchRegex = scan.nextLine();
			
			if (DEBUG) {System.out.println(searchRegex);}
			
			if (searchRegex.equals("\\\\exit")) {
				return;
			}
			
			try {
				Pattern pattern = Pattern.compile(searchRegex);
				searchMatcher = pattern.matcher("");
			}
			catch (PatternSyntaxException e) {
				System.out.println("Invalid regex");
				continue;
			}
			
			for (FileContent file : files) {
				searchMatcher.reset(file.content);
				if (searchMatcher.find()) {
					System.out.println(file.filename);
					if (DEBUG) { System.out.format("I found the text" +
                    " \"%s\" starting at " +
                    "index %d and ending at index %d.%n",
                    searchMatcher.group(),
                    searchMatcher.start(),
                    searchMatcher.end()); }
				}
			}
		}
	}
	
	public static List<FileContent> FileLoader(File[] files, Matcher regexMatcher) {
		return FileLoader(files, regexMatcher, new LinkedList<FileContent>());
	}
	
	public static List<FileContent> FileLoader(File[] files, Matcher regexMatcher, List<FileContent> list){
		if (files != null && files.length > 0)
		{
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
		}
		return list;
	}
	
	static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
