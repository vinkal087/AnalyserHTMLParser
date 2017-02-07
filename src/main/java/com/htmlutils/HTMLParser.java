package com.htmlutils;

/**
 * Created by vvishnoi on 2/7/17.
 */
public class HTMLParser {
    public static boolean parseHTML(String file, String uniqueId, boolean insertToDB, boolean  parseTypeIsAnalyser) throws Exception{
        if(parseTypeIsAnalyser){
            return HTMLParserAnalysers.parse( file,  uniqueId, insertToDB);
        }
        else{
            return HTMLParserDiagnostics.parse(file,  uniqueId, insertToDB);
        }
    }
}
