package com.navercorp.pinpoint.web.util;

import com.navercorp.pinpoint.web.vo.TransactionId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by root on 16-11-14.
 */
public class SplitListUtils {
    public static List<List<TransactionId>> splitTransactionIdList(List<TransactionId> transactionIdList, int maxTransactionIdListSize) {
        if (transactionIdList == null || transactionIdList.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<TransactionId>> splitTransactionIdList = new ArrayList<>();

        int index = 0;
        int endIndex = transactionIdList.size();
        while (index < endIndex) {
            int subListEndIndex = Math.min(index + maxTransactionIdListSize, endIndex);
            splitTransactionIdList.add(transactionIdList.subList(index, subListEndIndex));
            index = subListEndIndex;
        }

        return splitTransactionIdList;
    }
}
