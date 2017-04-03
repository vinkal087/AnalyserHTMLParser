package com.htmlutils;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by vvishnoi on 4/2/17.
 */
public class HTMLParserAnalyserNewFormat {
    final static Logger logger = Logger.getLogger(HTMLParserAnalysers.class);

    public static void main(String[] args) throws Exception{
        //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // Date d=sdf.parse("2014-07-15");
        // System.out.println(d.toString());
        //System.out.println(sdf.format(d));
        String s = ParserUtils.parseDate("26-JAN-17-12:43:52");
        System.out.println(s);
        //  parse("/Users/vvishnoi/Music/Receipt_Analyzer.html","12",false,"ReceiptAnalyzer");
    }

    private static ResourceBundle bundle = null;
    private static Map<String, String> info = new LinkedHashMap<>();
    private static List<String> dbErrors = new ArrayList<>();

    public static boolean parse(String file, String uniqueId, boolean insertToDB, String propertyFileName) throws Exception{
        Map<String, String> typeIdMap = new HashMap<>();
        bundle = ResourceBundle.getBundle(propertyFileName);
        List<String> tablesToBeExcluded = new ArrayList<String>();
        String[] tablesToBeExcludedArray = bundle.getString("TABLES_TO_BE_EXCLUDED").split(",");
        for(int i=0;i<tablesToBeExcludedArray.length;i++) {
            tablesToBeExcluded.add(tablesToBeExcludedArray[i].trim());
        }

        String content = new String(Files.readAllBytes(Paths.get(file)));
        List<String> queries = new ArrayList<String>();
        logger.debug(content);
        Document doc = Jsoup.parse(content);
        String analyzerType = getTypeOfAnalyzer(doc);
        logger.info("Analyzer Type:"+ analyzerType);
        Elements tables = doc.select(".sigcontainer");
        logger.info(tables.size());
        for(int i=0;i<tables.size();i++){
            Element table = tables.get(i);
            if(table.select(".divItemTitle").size()==0)continue;
            Element tableNameElement = table.select(".divItemTitle").get(0);
            if(tableNameElement.getElementsByTag("a").size()==0)continue;
            Element anchorDetail = tableNameElement.getElementsByTag("a").get(0);
            if(anchorDetail.getElementsByTag("table").size()==0)continue;
            Element anchorTable=anchorDetail.getElementsByTag("table").get(0);
            String tableName = anchorTable.getElementsByTag("td").get(0).text();




            if(tableName.contains(" "))continue;
            Element getHtmlTable=null;
            //Get HTML Table
            if(table.select(".divtable").size()>0 && table.select(".divtable").get(0).getElementsByTag("table").size()>0)
                getHtmlTable= table.select(".divtable").get(0).getElementsByTag("table").get(0);
            else
                continue;
            //Process Type Info
            Element sqlTable = table.select(".table1").first();
            setTypeId(sqlTable,tableName,analyzerType,typeIdMap);
            if(tablesToBeExcluded.contains(tableName)){
                continue;
            }
            if(!tableName.contains(" ")) {
                logger.info("Processing Table "+ tableName);
                int rowsProcessed = ParserUtils.processTable(getHtmlTable,tableName,queries,uniqueId);
                info.put(tableName,""+rowsProcessed);

            }
        }
        if(insertToDB)
            DatabaseUtils.insertToDB(queries, dbErrors);
        else{
            ParserUtils.writeDatabaseStatementsToFile(uniqueId,queries);

        }
        if(dbErrors.size()>0){
            ParserUtils.writeErrorDataToFile(dbErrors,uniqueId+"_error.txt");
        }

        info.put("FILE NAME PROCESSED", file);
        info.put("Unique id", uniqueId);
        if(typeIdMap.size()>0){
            String tmpKey = typeIdMap.keySet().iterator().next();
            info.put(tmpKey,typeIdMap.get(tmpKey));
        }
        ParserUtils.writeParsedDataToFile(info,uniqueId+"_info.txt");
        info.clear();
        dbErrors.clear();
        queries.clear();
        return true;
    }


    private static String getTypeOfAnalyzer(Document doc){
        Elements divTitleBar = doc.select(".header_title");
        String type =  divTitleBar.text();
        if(type.contains("Transaction")) return "Transaction";
        else if(type.contains("Adjustment")) return "Adjustment";
        else if(type.contains("Receipt")) return "Receipt";
        return null;
    }

    private static void setTypeId( Element table, String tableName, String analyzerType,  Map<String, String> info ){
        if(!(tableName.equalsIgnoreCase("ra_customer_trx_all") || tableName.equalsIgnoreCase("ar_cash_receipts_all")
                || tableName.equalsIgnoreCase("AR_ADJUSTMENTS_ALL")) ){
            return;
        }
        if(analyzerType.equalsIgnoreCase("Transaction") && tableName.equalsIgnoreCase("ra_customer_trx_all")){
            parseTypeTable(table,tableName, "customer_trx_id", info);
        }
        else if(analyzerType.equalsIgnoreCase("Adjustment") && tableName.equalsIgnoreCase("AR_ADJUSTMENTS_ALL")){
            parseTypeTable(table,tableName, "adjustment_id", info);
        }
        else if(analyzerType.equalsIgnoreCase("Receipt") && tableName.equalsIgnoreCase("ar_cash_receipts_all")){
            parseTypeTable(table,tableName, "cash_receipt_id", info);
        }
    }

    private static void parseTypeTable(Element table, String tableName, String columnName, Map<String,String> info){
        String sqlQuery = columnName+ " =";
        Elements rows = table.select("tr");
        for(int i=0;i<rows.size();i++){
            Elements cols = rows.get(i).select("td");
            for(int j=0;j<cols.size();j++){
                if(cols.get(j).select("pre").size()==0)continue;
                Element pre = cols.get(j).select("pre").get(0);
                String s = pre.text().toLowerCase();
                logger.info("Processing Table "+tableName+" and SQL: "+s);
                s = s.substring(s.indexOf(sqlQuery),s.length());
                s= s.replaceFirst(sqlQuery, "");
                info.put(columnName,s.trim());
            }
        }
    }
}
