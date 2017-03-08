package com.navercorp.pinpoint.web.dao.hbase;

import com.google.common.collect.ImmutableList;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.web.dao.TransactionListDao;
import com.navercorp.pinpoint.web.mapper.TransactionListMapper;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XTransactionName;
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

/**
 * Created by root on 16-10-18.
 */

public class HbaseTransactionListDaoTest {
    @Mock
    private HbaseOperations2 template2;

    @Mock
    private TransactionListMapper mapper;

    @InjectMocks
    private TransactionListDao transactionListDao = new HbaseTransactionListDao();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void scan() throws Exception {
        //given
        XTransactionName name1 = new XTransactionName("fm_active", "/getAlarm", 12345667L, "fm-active");
        XTransactionName name2 = new XTransactionName("fm_active", "/getAlarm", 12345667L, "fm-active");

        //when
        when(this.template2.find(anyString(), any(Scan.class), any(RowMapper.class)))
                .thenReturn(ImmutableList.of(newArrayList(name1, name2)));

        List<XTransactionName> transactionNames = transactionListDao.scan();

        //then
        assertThat(transactionNames, is(ImmutableList.of(name1, name2)));
    }

    @Test
    public void getServTracesList() throws Exception {
        //given
        XTransactionName name1 = new XTransactionName("fm_active", "/getAlarm", 12345667L, "fm-active");
        XTransactionName name2 = new XTransactionName("fm_active", "/getAlarm", 12345788L, "fm-active");
        Range range = new Range(12345000L, 12349000L);

        //when
        when(this.template2.get(anyString(), any(byte[].class), any(RowMapper.class)))
                .thenReturn(ImmutableList.of(name1, name2));
        List<XTransactionName> names = transactionListDao.getServiceTracesList("fm_active", range);

        //then
        assertThat(names, is(ImmutableList.of(name1, name2)));
    }
}