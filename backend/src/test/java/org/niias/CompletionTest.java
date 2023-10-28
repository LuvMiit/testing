package org.niias;

import org.junit.Assert;
import org.junit.Test;
import org.niias.asrb.kn.util.CompletionUtil;

public class CompletionTest
{

    @Test
    public void testConcreteCompletion()
    {
        int mark = 0;
        mark = CompletionUtil.setMonth(mark, 3);
        Assert.assertEquals("001000000000", CompletionUtil.format12month(mark));
        System.out.println("set month 7");
        mark = CompletionUtil.setMonth(mark, 7);
        Assert.assertEquals("001000100000", CompletionUtil.format12month(mark));
        Assert.assertFalse(CompletionUtil.isMonthSet(mark, 6));
        Assert.assertTrue(CompletionUtil.isMonthSet(mark, 3));
        mark = CompletionUtil.unsetMonth(mark, 3);
        Assert.assertFalse(CompletionUtil.isMonthSet(mark, 3));
    }



}
