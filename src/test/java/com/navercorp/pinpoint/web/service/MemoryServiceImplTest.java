package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.dao.elasticsearch.ESQueryResult;
import com.navercorp.pinpoint.web.vo.Range;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static smartsight.elasticsearch.common.collect.Sets.newHashSet;

/**
 * Created by root on 17-2-14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

@ContextConfiguration(locations = {"classpath:servlet-context.xml", "classpath:applicationContext-unit.xml"})
public class MemoryServiceImplTest {
    @Autowired
    private XHostMemoryService hostMemoryService;

    @Ignore
    @Test
    public void testGetMemInfo() throws Exception {
//        &to=
        ESQueryResult result = hostMemoryService.getMemoryDetail(newHashSet(), new Range(1487001600000L, 1487055358126L));

        System.out.println(result);
    }
}