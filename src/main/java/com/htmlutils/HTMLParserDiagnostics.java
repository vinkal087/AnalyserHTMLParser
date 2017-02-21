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
 * Created by vvishnoi on 2/7/17.
 */
public class HTMLParserDiagnostics {
    final static Logger logger = Logger.getLogger(HTMLParserDiagnostics.class);
    private static ResourceBundle bundle=null;
    private static Map<String, String> info = new LinkedHashMap<>();
    private static List<String> dbErrors = new ArrayList<>();

    public static boolean parse(String file, String uniqueId, boolean insertToDB, String propertyFileName) throws Exception{
        bundle = ResourceBundle.getBundle(propertyFileName);
        List<String> tablesToBeExcluded = new ArrayList<String>();
        String[] tablesToBeExcludedArray = bundle.getString("TABLES_TO_BE_EXCLUDED").split(",");
        for(int i=0;i<tablesToBeExcludedArray.length;i++) {
            tablesToBeExcluded.add(tablesToBeExcludedArray[i].trim());
        }

        String uniqueNumber = uniqueId;
        String content = new String(Files.readAllBytes(Paths.get(file)));
        String diagnosticType = getDiagnosticType(content);
        List<String> queries = new ArrayList<String>();
        logger.debug(content);
        Document doc = Jsoup.parse(content);
        Elements tables = doc.select(".OraTable");
        logger.info(tables.size());

        for(int i=0;i<tables.size();i++){
            Element table = tables.get(i);
            String tableName = table.attr("summary").trim();
            if(tableName.contains(" ") || !tableName.contains("_")) continue;
            setTypeId(table,tableName,diagnosticType,info);
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
            DatabaseUtils.insertToDB(queries,dbErrors);
        else{
            ParserUtils.writeDatabaseStatementsToFile(uniqueId,queries);
        }
        if(dbErrors.size()>0){
            ParserUtils.writeErrorDataToFile(dbErrors,uniqueId+"_error.txt");
        }
        info.put("FILE NAME PROCESSED", file);
        info.put("Unique id", uniqueId);
        ParserUtils.writeParsedDataToFile(info,uniqueId+"_info.txt");
        info.clear();
        dbErrors.clear();
        queries.clear();
        return true;

    }

    private static String getDiagnosticType(String content){
        if(content.contains("oracle.apps.ar.diag.ARTransactionInfo")){
            return "Transaction";
        }
        else if(content.contains("oracle.apps.ar.diag.ARReceiptInfo")){
            return "Receipt";
        }
        else if(content.contains("oracle.apps.ar.diag.ARAdjustmentInfo")){
            return "Adjustment";
        }
        return null;

    }

    private static void setTypeId( Element table, String tableName, String diagnosticType,  Map<String, String> info ){
        if(!(tableName.equalsIgnoreCase("ra_customer_trx_all") || tableName.equalsIgnoreCase("ar_cash_receipts_all")
                || tableName.equalsIgnoreCase("AR_ADJUSTMENTS_ALL")) ){
            return;
        }
        if(diagnosticType.equalsIgnoreCase("Transaction") && tableName.equalsIgnoreCase("ra_customer_trx_all")){
            parseTypeTable(table,tableName, "customer_trx_id", info);
        }
        else if(diagnosticType.equalsIgnoreCase("Adjustment") && tableName.equalsIgnoreCase("AR_ADJUSTMENTS_ALL")){
            parseTypeTable(table,tableName, "adjustment_id", info);
        }
        else if(diagnosticType.equalsIgnoreCase("Receipt") && tableName.equalsIgnoreCase("ar_cash_receipts_all")){
            parseTypeTable(table,tableName, "cash_receipt_id", info);
        }
    }

    private static void parseTypeTable(Element table, String tableName, String columnName, Map<String,String> info){
        Elements rows = table.select("tr");
        for(int i=0;i<rows.size();i++) {

            //Processing Table Header
            Element row = rows.get(i);
            Elements tableValues = row.select("td");
            if(tableValues.size()==0)continue;
            String str = tableValues.get(0).text().trim();
            logger.info(columnName + " : "+ str);
            info.put(columnName,str);
        }
    }
}
