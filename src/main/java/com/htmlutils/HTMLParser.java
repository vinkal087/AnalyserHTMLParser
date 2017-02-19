package com.htmlutils;

/**
 * Created by vvishnoi on 2/7/17.
 */
public class HTMLParser {
    public static boolean parseHTML(String file, String uniqueId, boolean insertToDB, boolean  parseTypeIsAnalyser, String propertyFileName) throws Exception{
        if(parseTypeIsAnalyser){
            return HTMLParserAnalysers.parse( file,  uniqueId, insertToDB, propertyFileName);
        }
        else{
            return HTMLParserDiagnostics.parse(file,  uniqueId, insertToDB, propertyFileName);
        }
    }
}
