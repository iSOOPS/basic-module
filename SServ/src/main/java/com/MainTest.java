package com;


import com.elasticsearch.SElastic;
import com.elasticsearch.xcontent.SContentBuilder;
import com.elasticsearch.xcontent.SContentBuilderAnalyzer;
import com.elasticsearch.xcontent.SContentBuilderType;
import com.elasticsearch.xcontent.SElasticContent;
import com.ssource.SAES;
import com.ssource.SClass;
import com.ssource.SCode;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainTest {
    public static void main(String[] args) {

        String data = "cb50vdmpkUcOKg8HsPq3WMygnxv9f8hSJbzaZ+hKpeWuWFVwaehAyttnP1E6d6HLVTn+DNIGOYmebQE2TvsaW6QhMj35Q8X26iXDag1CLStSwH6n8oQnzmnbJpXDE7TDrgONU3LEmDtMBdYisovNday7rC8R07QqZcFznbGxtWqSAemGOUkdIlCpPihUkawcIm4mBFxdsKSZhAJSTxHYTr8eIUXT2FCPy+04VwMm/JDcMcsMu/udUxcEU6cMxj8kC3loHB0RH5g1H3+KOJuS/n9xhGyPkLuErbwHZfagRT1Y/zqX4Gt4ij7jQv6QtMzlYczFcB5bfhe0mVTg+HW5ymB5vNFh1h7y8DZa/CKdY9aR12qcFPBLy//wSquq1I0Swjsnfo9fbs5oUndHtYBwKRPpyGZK7ATQLInBis3D3/cnlXZif8K0r9rLcR2M/B99BhvjUzb8N+rQVRHhlHlE11GOj84EOM3tNBQtW2e/L2g=";
        Object aaa1111 = SAES.unCode7AES(data,"ufsQkm8++Up7h2h3n+zdYw==","rlhWmu8JLWlgbr9TIf9/Ow==");

        List<Integer> aaa = new ArrayList<>();
        aaa.add(0);
        aaa.add(1);
        aaa.add(2);
        aaa.add(3);
        aaa.add(4);
        aaa.add(5);
        aaa.add(6);
        aaa.add(7);
        aaa.add(8);

        for (int i=0;i<3;i++){
            aaa.remove(0);
        }
        String bbb = "03";

        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher("-299 9.2");
        if( !isNum.matches() ){
            System.out.print(aaa);
        }

        Integer iii = Integer.valueOf(bbb);
        System.out.print(aaa);


        Integer cccc = aaabbb();
        System.out.print(cccc);
//      1541001600
//        1541030400
        Long monthtime = SClass.timeMillMonthZerois(1541001600,0);
        String strrr = SClass.timeMillisToDate(monthtime);
        System.out.print(strrr);

//        XContentBuilder test = XContentFactory.jsonBuilder()
//                .startObject()
//                .startObject("properties")
//
//                // 父类属性-subString预留字段，多个搜索关键字用中文逗号拼接
//                .startObject("code").field("type", "text").endObject()
//                .startObject("subString").field("type", "text").field("analyzer", "ik_max_word").endObject()
//
//                .startObject("name").field("type", "text").field("analyzer", "ik_max_word").endObject()
//                .startObject("address").field("type", "text").field("analyzer", "ik_max_word").endObject()
//                .startObject("mainCategory").field("type", "text").field("analyzer", "ik_max_word").endObject()
//                .startObject("creditLevel").field("type", "Integer")
//
//                .startObject("heat").field("type", "Integer")
//                .startObject("sale").field("type", "Integer")
//
//                .endObject()
//                .endObject();



        SElasticContent content = new SElasticContent(
                new SContentBuilder("code",         SContentBuilderType.string),
                new SContentBuilder("subString",    SContentBuilderType.string,SContentBuilderAnalyzer.ik_max_word),
                new SContentBuilder("name",         SContentBuilderType.string,SContentBuilderAnalyzer.ik_max_word),
                new SContentBuilder("address",      SContentBuilderType.string,SContentBuilderAnalyzer.ik_max_word),
                new SContentBuilder("mainCategory", SContentBuilderType.string,SContentBuilderAnalyzer.ik_max_word),
                new SContentBuilder("creditLevel",  SContentBuilderType.string),
                new SContentBuilder("heat",         SContentBuilderType.string),
                new SContentBuilder("sale",         SContentBuilderType.string)

        );
        createXBuilder(content.getList());
    }

    public static XContentBuilder createXBuilder(List<SContentBuilder> list) {
        try {
            XContentBuilder mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("properties");
            for (SContentBuilder content : list) {
                mapping.startObject(content.getKeyName()).field("type", content.getKeyType());
                if (content.getAnalyzerType() != null) {
                    mapping.field("analyzer", content.getAnalyzerType());
                }
                mapping.endObject();
            }
            mapping.endObject().endObject();


            XContentBuilder test = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("properties")

                    // 父类属性-subString预留字段，多个搜索关键字用中文逗号拼接
                    .startObject("code").field("type", "text").endObject()
                    .startObject("subString").field("type", "text").field("analyzer", "ik_max_word").endObject()

                    .startObject("name").field("type", "text").field("analyzer", "ik_max_word").endObject()
                    .startObject("address").field("type", "text").field("analyzer", "ik_max_word").endObject()
                    .startObject("mainCategory").field("type", "text").field("analyzer", "ik_max_word").endObject()
                    .startObject("creditLevel").field("type", "Integer")

                    .startObject("heat").field("type", "Integer")
                    .startObject("sale").field("type", "Integer")

                    .endObject()
                    .endObject();

            return mapping;
        } catch (IOException e) {
            return null;
        }

    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static Integer aaabbb(){
        Integer count = 201;
        Integer pa = 1;
        return count/100 * pa;
    }
}
