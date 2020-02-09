package ch.mno.commitgame.common;

import java.util.Date;
import java.util.Iterator;

/**
 * Iterator on a date, from a date inclusive to a date exclusive
 */
public class DateIterator implements Iterator<Date> {
        private Date currentDate;
        private Date endDate;
        private int stepSec;

        public DateIterator(Date initDate, Date endDate, int stepSec) {
            this.currentDate=initDate;
            this.endDate=endDate;
            this.stepSec=stepSec;
        }

        @Override
        public boolean hasNext() {
            return currentDate.before(endDate);
        }

        @Override
        public Date next() {
            Date ret = currentDate;
            currentDate = new Date(currentDate.getTime() + stepSec*1000);
            return ret;
        }
    }