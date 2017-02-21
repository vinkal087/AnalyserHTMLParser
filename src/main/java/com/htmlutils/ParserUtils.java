package com.htmlutils;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by vvishnoi on 2/7/17.
 */
public class ParserUtils {
    final static Logger logger = Logger.getLogger(ParserUtils.class);
    private static ResourceBundle bundle = ResourceBundle.getBundle("general");


    public  static int processTable(Element table, String tableName, List<String> queryList, String uniqueNumber){
        int noOfRowsProcessed =0;
        Elements rows = table.select("tr");
        logger.info(tableName+" has rows "+ rows.size());
        Map<Integer,String> map = new HashMap<Integer,String>();
        String tableHeader = "(";
        for(int i=0;i<rows.size();i++){

            //Processing Table Header
            Element row = rows.get(i);
            Elements cols = row.select("th");
            for(int j=0;j<cols.size();j++){
                String str = cols.get(j).text();
                str = str.replaceAll("\\s+","");
                str = str.trim();
                str= str.replaceAll("(^\\h*)|(\\h*$)","");

                logger.debug(tableName+":"+str);
                map.put(j,str);
                tableHeader+= j==0? str: ", "+str;
            }

            //Processing Table Rows
            Elements tableValues = row.select("td");
            String query="";
            if(tableValues.size()>0){
                query += "Insert into "+tableName+ " "+tableHeader+", keyy) values (";
                for(int j=0;j<tableValues.size();j++){
                    String str = tableValues.get(j).text().trim();
                    str= str.replaceAll("(^\\h*)|(\\h*$)","");
                    str = parseDate(str);
                    query += j==0? str: ", "+str;
                }
                query+=", '"+uniqueNumber+"')";
                queryList.add(query);
                noOfRowsProcessed++;
                logger.debug(query);
            }
        }
        return noOfRowsProcessed;
    }

    public static void writeParsedDataToFile(Map<String, String> map, String fileName) throws IOException {
        FileWriter writer = new FileWriter(new File(bundle.getString("FILE_GENERATION_PATH")+"/"+fileName));
        BufferedWriter bw = new BufferedWriter(writer);
        Iterator<String> keys = map.keySet().iterator();
        try {
            while ((keys.hasNext())) {
                String tableName = keys.next();
                String value = tableName + " : " + map.get(tableName)+"\n";
                bw.write(value);


            }
            bw.flush();
        }
        finally {
            if(writer!=null)writer.close();
            if(bw!=null)bw.close();
        }
    }

    public static void writeErrorDataToFile(List<String> list, String fileName) throws IOException {
        FileWriter writer = new FileWriter(new File(bundle.getString("FILE_GENERATION_PATH")+"/"+fileName));
        BufferedWriter bw = new BufferedWriter(writer);
        Iterator<String> itr = list.iterator();
        try {
            while ((itr.hasNext())) {

                String value = itr.next()+"\n";
                bw.write(value);
            }
            bw.flush();
        }
        finally {
            if(writer!=null)writer.close();
            if(bw!=null)bw.close();
        }
    }

    public static void writeDatabaseStatementsToFile(String uniqueId, List<String> queries) throws Exception{
        List<String> modifyQueries = new ArrayList<>();
        for(int i=0;i<queries.size();i++){
            String str = queries.get(i);
            modifyQueries.add(str+";");
        }
        Path spoolFile = Paths.get(bundle.getString("FILE_GENERATION_PATH")+"/"+uniqueId+".sql");
        Files.write(spoolFile, modifyQueries, Charset.forName("UTF-8"));

    }

    public static String parseDate(String s){
        SimpleDateFormat sdf = new SimpleDateFormat("DD-MMM-YY");
        try{
            sdf.parse(s);
            return "to_date('"+s+"','DD-MM-YY-HH24:MI:SS')";
        }
        catch (ParseException e){
            s= "'"+s+"'";
            if(s.contains("&")){
                s= s.replaceAll("&", "' || chr(38) || '");
            }
            return s;
        }
    }

    public static Map<String, String> processId(String tableName, String columnName){
        return null;
    }
}
