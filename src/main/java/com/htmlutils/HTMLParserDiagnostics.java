package com.htmlutils;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by vvishnoi on 2/7/17.
 */
public class HTMLParserDiagnostics {
    final static Logger logger = Logger.getLogger(HTMLParserDiagnostics.class);
    private static ResourceBundle bundle = ResourceBundle.getBundle("parser");
    private static Map<String, String> info = new HashMap<>();

    public static boolean parse(String file, String uniqueId, boolean insertToDB) throws Exception{
        List<String> tablesToBeExcluded = new ArrayList<String>();
        String[] tablesToBeExcludedArray = bundle.getString("TABLES_TO_BE_EXCLUDED_FROM_DIAGNOSTICS").split(",");
        for(int i=0;i<tablesToBeExcludedArray.length;i++) {
            tablesToBeExcluded.add(tablesToBeExcludedArray[i].trim());
        }

        String uniqueNumber = uniqueId;
        String content = new String(Files.readAllBytes(Paths.get(file)));
        List<String> queries = new ArrayList<String>();
        logger.debug(content);
        Document doc = Jsoup.parse(content);
        Elements tables = doc.select(".OraTable");
        logger.info(tables.size());

        for(int i=0;i<tables.size();i++){
            Element table = tables.get(i);
            String tableName = table.attr("summary").trim();
            if(tablesToBeExcluded.contains(tableName)){
                continue;
            }
            if(!tableName.contains(" ")) {
                logger.info("Processing Table "+ tableName);
                int rowsProcessed = ParserUtils.processTable(table, tableName,queries,uniqueNumber);
                info.put(tableName,""+rowsProcessed);
            }
        }
        if(insertToDB)
            DatabaseUtils.insertToDB(queries);
        else{
            Path spoolFile = Paths.get(uniqueId+".txt");
            Files.write(spoolFile, queries, Charset.forName("UTF-8"));
        }
        ParserUtils.writeParsedDataToFile(info,uniqueId+"_info.txt");
        return true;

    }
}
