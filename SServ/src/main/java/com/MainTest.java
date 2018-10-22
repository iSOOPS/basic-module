package com;


import com.ssource.SCode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainTest {
    public static void main(String[] args) {

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
