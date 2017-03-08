package com.navercorp.pinpoint.web.dao.hbase;

import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.vo.Application;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.hadoop.hbase.RowMapper;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class HbaseApplicationIndexDaoTest {

    @Mock
    private HbaseOperations2 hbaseOperations2;

    @Mock
    private RowMapper<List<Application>> applicationNameMapper;

    @Mock
    private RowMapper<List<String>> agentIdMapper;

    @InjectMocks
    private ApplicationIndexDao applicationIndexDao = new HbaseApplicationIndexDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSelectAllApplicationNames() throws Exception {
        //given
        Application application1 = new Application("fm_active", ServiceType.TEST);
        Application application2 = new Application("fm_active", ServiceType.TEST);
        List<Application> apps = ImmutableList.of(application1, application2);

        //when
        when(this.hbaseOperations2.find(anyString(), any(Scan.class), any(RowMapper.class)))
                .thenReturn(ImmutableList.of(newArrayList(application1), newArrayList(application2)));
        List<Application> applicationList = applicationIndexDao.selectAllApplicationNames();

        //then
        assertThat(applicationList, is(apps));
    }

    @Test
    public void testSelectAgentInfo() throws Exception {
        //given
        final String serviceName = "fm_active";

        //when
        when(this.hbaseOperations2.get(anyString(), any(Get.class), any(RowMapper.class)))
                .thenReturn(ImmutableList.of("fm-active1", "fm-active2"));
        List<String> agentIds = applicationIndexDao.selectAgentIds(serviceName);

        //then
        assertThat(agentIds, is(ImmutableList.of("fm-active1", "fm-active2")));
    }
}