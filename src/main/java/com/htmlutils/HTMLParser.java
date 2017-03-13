package com.htmlutils;


import net.lingala.zip4j.core.ZipFile;

import java.io.File;

/**
 * Created by vvishnoi on 2/7/17.
 */
public class HTMLParser {
    public static boolean parseHTML(String file, String uniqueId, boolean insertToDB, boolean  parseTypeIsAnalyser, String propertyFileName) throws Exception{

        if(file.endsWith(".zip")){
            String source = file;
            String destination = file.substring(0,file.lastIndexOf(".zip"));

            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
            File folder = new File(destination);
            File[] listOfFiles = folder.listFiles();
            for(int i=0;i<listOfFiles.length;i++){
                if(listOfFiles[i].isFile() ){
                    if(listOfFiles[i].getName().endsWith(".html") || listOfFiles[i].getName().endsWith(".htm")){
                        file = listOfFiles[i].getAbsolutePath();
                    }
                }
            }
        }
        if(parseTypeIsAnalyser){
            return HTMLParserAnalysers.parse( file,  uniqueId, insertToDB, propertyFileName);
        }
        else{
            return HTMLParserDiagnostics.parse(file,  uniqueId, insertToDB, propertyFileName);
        }
    }
}
