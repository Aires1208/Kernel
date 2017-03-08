package com.navercorp.pinpoint.web.dao.hbase;


import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.topo.domain.TopoLine;
import com.navercorp.pinpoint.common.topo.domain.XLink;
import com.navercorp.pinpoint.common.topo.domain.XNode;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.util.TimeSlot;
import com.navercorp.pinpoint.web.dao.ServiceIndexDao;
import com.navercorp.pinpoint.web.mapper.TopoLineMapper;
import com.navercorp.pinpoint.web.vo.Range;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class HbaseServiceIndexDaoTest {
    @Mock
    private HbaseOperations2 template2;

    @Mock
    private TopoLineMapper topoLineMapper;

    @Mock
    private TimeSlot timeSlot;

    @InjectMocks
    private ServiceIndexDao serviceIndexDao = new HbaseServiceIndexDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private TopoLine topo1 = new TopoLine(
            newArrayList(new XNode("USER", ServiceType.USER.getCode(), 101L, 0L, 23L),
                    new XNode("fm_active", ServiceType.STAND_ALONE.getCode(), 298L, 0L, 24L)),
            newArrayList(new XLink("USER", "fm-active", 298L, 0L, 24)));

    @Test
    public void should_print_topo_lines_when_given_appName_and_range() {
        //given
        String appName = "EMS";
        long timestamp = 2468555508533L;

        //when
        when(this.template2.find(anyString(), any(Scan.class), any(TopoLineMapper.class)))
                .thenReturn(ImmutableList.of(topo1));
        List<TopoLine> topoLines = serviceIndexDao.getTopoLineSet(appName, new Range(0, timestamp));

        //then
        assertThat(topoLines, is(newArrayList(topo1)));
    }
}
