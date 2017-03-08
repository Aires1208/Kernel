/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.common.util.DateUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author emeroad
 * @author netspider
 */
public final class Range {
    private final long from;
    private final long to;

    public Range(long from, long to) {
        this.from = from;
        this.to = to;
        validate();
    }

    public Range(long from, long to, boolean check) {
        this.from = from;
        this.to = to;
        if (check) {
            validate();
        }
    }

    public static Range createUncheckedRange(long from, long to) {
        return new Range(from, to, false);
    }

    public long getFrom() {
        return from;
    }

    public long getAvr() {
        return (from + to) / 2L;
    }

    public long getTo() {
        return to;
    }

    public long getRange() {
        return to - from;
    }

    public void validate() {
        if (this.to < this.from) {
            throw new IllegalArgumentException("invalid range:" + this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range range = (Range) o;

        if (from != range.from) return false;
        if (to != range.to) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (from ^ (from >>> 32));
        result = 31 * result + (int) (to ^ (to >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Range{" +
                "from=" + from +
                ", to=" + to +
                ", range=" + getRange() +
                '}';
    }


    public String prettyToString() {
        return "Range{" +
                "from=" + DateUtils.longToDateStr(from) +
                ", to=" + DateUtils.longToDateStr(to) +
                ", range s=" + TimeUnit.MILLISECONDS.toSeconds(getRange()) +
                '}';
    }

    public List<Range> splitRange(int sampleCount, long interval) {
        long tmpFrom = getFrom();
        long tmpTo = getTo();

        List<Range> ranges = newArrayList();
        long xGroupUnitMillis;
        if (interval <= 0) {
            xGroupUnitMillis = (tmpTo - tmpFrom) / sampleCount;
        } else {
            xGroupUnitMillis = interval;
        }
        while (tmpFrom < tmpTo) {
            ranges.add(new Range(tmpFrom, Math.min(tmpFrom + xGroupUnitMillis, tmpTo)));
            tmpFrom += xGroupUnitMillis;
        }
        return ranges;
    }

    public Range sample(long sample, long interval) {
        return new Range(sample - interval / 2, sample + interval / 2);
    }

    public boolean isInRange(long timestamp) {
        return timestamp >= from && timestamp <= to;
    }

    public int getGp(int sampleCount) {
        long range = getRange() / sampleCount;
        return range / 60000 == 0 ? 1 : (int) (range / 60000);
    }
}
