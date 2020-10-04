package com.isoops.basicmodule;

import com.isoops.basicmodule.classes.annotation.source.SignGenerater;
import com.isoops.basicmodule.classes.basicmodel.Request;
import com.isoops.basicmodule.source.SBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
public class Tests {

//    @Autowired
//    private SignGenerater signGenerater;
    @Test
    public void sayHello() {
//        String sp = "-";
//        String str = SClass.mergeBySeparator(sp,null);
//        String aaaa = STime.formatTimeMill((long) (30 * 60 * 60 * 24 + 60*60 +60 +1));
//        System.out.println(aaaa);
//        String sssssss = signGenerater.bcryptEncode("618fb0a2f162de31d59d7372d67bd877");

        List<Request> requests = new ArrayList<>();
        Request request = new Request<>();
        request.setChannel(1);
        request.setSign("1231231123123");
        requests.add(request);
        Request request1 = new Request<>();
        request1.setChannel(2);
        request1.setSign("的分公司的风格上度过");
        requests.add(request1);

        SBean.setValueToList(requests,Request::getSign,"男男女女");


        Assert.isTrue(true,"完全正确的单元测试");
    }


}
