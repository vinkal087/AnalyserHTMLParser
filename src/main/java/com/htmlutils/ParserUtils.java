package com.htmlutils;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by vvishnoi on 2/7/17.
 */
public class ParserUtils {
    final static Logger logger = Logger.getLogger(ParserUtils.class);


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

                    query += j==0? "'"+str+"'": ", '"+str+"'";
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
        FileWriter writer = new FileWriter(new File(fileName));
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
}
