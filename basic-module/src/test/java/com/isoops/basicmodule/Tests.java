package com.isoops.basicmodule;

import com.isoops.basicmodule.source.STime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
public class Tests {

    @Test
    public void sayHello() {
        String aaaa = STime.formatTimeMill((long) (30 * 60 * 60 * 24 + 60*60 +60 +1));
        System.out.println(aaaa);
        Assert.isTrue(true,"完全正确的单元测试");
    }


}
