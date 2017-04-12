package com.datafeedtoolbox.datafeeds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Copyright DatafeedToolbox.com 2017 All Rights Reserved
 */
public class ProcessEscapedCharacters {
	public static void main(String[] args) {
		File inputFile = null;
		if(args != null && args.length == 1) {
			inputFile = new File(args[0]);
			if(!inputFile.exists()) {
				System.err.println("Input file doesn't exist: "+args[0]);
				System.exit(1);
			}
			if(!inputFile.canRead()) {
				System.err.println("Can't read input file: "+args[0]);
				System.exit(2);
			}
		} else {
			System.err.println("Usage:");
			System.err.println("java -jar ProcessEscapedCharacters.jar FILENAME");
			System.exit(1);
		}
		if(inputFile == null) {
			System.err.println("There was a problem reading the input file: "+inputFile);
			System.exit(3);
		}

		// Stream the data since data feeds can be large
		FileInputStream in = null;
		System.out.println("Processing file: "+inputFile);
		try {
			in = new FileInputStream(inputFile);
			int c1, c2;
			// Read in the first byte
			c1 = in.read();
			while(true) {
				// If the file is empty, quit.
				if(c1 == -1) break;

				// If we find an escape character,
				// read the next byte to see if its one of our
				// special characters.
				if(c1 == 92) {
					c2 = in.read();
					// Let's break out of our loop if there is no next
					// character
					if(c2 == -1) break;
					if(c2 == 9 || c2 == 10 || c2 == 13) {
						// Oooooh. The next character is a tab or newline!
						// Replace it with a space!
						System.out.print((char)32);
					} else if(c2 == 92) {
						// We found an escaped '\'.
						// Print a single '\'.
						System.out.print((char)92);
					} else {
						// The next character isn't anything interesting
						// Print out the backslash character
						System.out.print((char)c1);
						// Since we've already read the next byte,
						// let's do the following:
						// 1. Set c1 to c2 (the value in c2 is the next byte)
						// 2. Skip the rest of the loop
						//    so that we don't read/print an extra byte
						c1 = c2;
						continue;
					}
				} else {
					System.out.print((char)c1);
				}
				c1 = in.read();
			}
		} catch(FileNotFoundException e) {
			System.err.println("Couldn't find the file: "+e);
		} catch(IOException e) {
			System.err.println("There was a problem encountered while reading the file: "+e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch(IOException e) {
				System.err.println("There was a problem closing the file: "+e);
			}
		}
	}
}
