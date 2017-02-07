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
public class HTMLParserAnalysers {
    final static Logger logger = Logger.getLogger(HTMLParserAnalysers.class);

    public static void main(String[] args) throws Exception{
        parse("/Users/vvishnoi/Downloads/upwork/Receipt_Analyzer.html", "11-22",false);
    }

    private static ResourceBundle bundle = ResourceBundle.getBundle("parser");
    private static Map<String, String> info = new HashMap<>();

    public static boolean parse(String file, String uniqueId, boolean insertToDB) throws Exception{
        List<String> tablesToBeExcluded = new ArrayList<String>();
        String[] tablesToBeExcludedArray = bundle.getString("TABLES_TO_BE_EXCLUDED_FROM_DIAGNOSTICS").split(",");
        for(int i=0;i<tablesToBeExcludedArray.length;i++) {
            tablesToBeExcluded.add(tablesToBeExcludedArray[i].trim());
        }

        String content = new String(Files.readAllBytes(Paths.get(file)));
        List<String> queries = new ArrayList<String>();
        logger.debug(content);
        Document doc = Jsoup.parse(content);
        Elements tables = doc.select(".divItem");
        logger.info(tables.size());
        for(int i=0;i<tables.size();i++){
            Element table = tables.get(i);
            if(table.select(".divItemTitle").size()==0)continue;
            Element tableNameElement = table.select(".divItemTitle").get(0);
            if(tableNameElement.getElementsByTag("a").size()==0)continue;
            String tableName = tableNameElement.getElementsByTag("a").get(0).attr("name").trim();
            tableName = tableName.substring(8,tableName.length()-2);
            if(table.select(".divtable").size()==0 || table.select(".divtable").get(0).getElementsByTag("table").size()==0)continue;
            Element getHtmlTable = table.select(".divtable").get(0).getElementsByTag("table").get(0);

            if(!tableName.contains(" ")) {
                logger.info("Processing Table "+ tableName);
                int rowsProcessed = ParserUtils.processTable(getHtmlTable,tableName,queries,uniqueId);
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
