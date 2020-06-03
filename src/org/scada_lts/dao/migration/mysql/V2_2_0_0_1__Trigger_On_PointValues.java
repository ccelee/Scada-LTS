package org.scada_lts.dao.migration.mysql;
/*
 * (c) 2020 hyski.mateusz@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author  hyski.mateusz@gmail.com on 30.04.2020
 */
public class V2_2_0_0_1__Trigger_On_PointValues implements SpringJdbcMigration {

    public void migrate(JdbcTemplate jdbcTmp) throws Exception {

        try {
            jdbcTmp.execute( ""
                    +"CREATE TRIGGER onlyForStorungsAndAlarmValues AFTER INSERT "
                    +"ON pointValues "
                    +"FOR EACH ROW BEGIN "
                    +"DECLARE PLC_ALARM_LEVEL INT(1); "
                    +"select plcAlarmLevel into PLC_ALARM_LEVEL from dataPoints where id=new.dataPointId;  "
                    +""
                    +" IF (PLC_ALARM_LEVEL = 1 OR PLC_ALARM_LEVEl = 2) THEN "
                    + ""
                    +"insert into plcAlarms ("
                    +"  pointId,"
                    +"  pointXid,"
                    +"  pointType,"
                    +"  pointName,"
                    +"  triggerTime,"
                    +"  inactiveTime,"
                    +"  acknowledgeTime,"
                    +"  lastpointValue"
                    +") values ("
                    +"  (select id from dataPoints where id=new.dataPointId),"
                    +"  (select xid from dataPoints where id=new.dataPointId),"
                    +"  PLC_ALARM_LEVEL,"
                    +"  (select pointName from dataPoints where id=new.dataPointId),"
                    +"  (select from_unixtime(unix_timestamp())),"
                    +"  null,"
                    +"  null,"
                    +" (select pointValue from pointValues where ts=(select max(ts) from pointValues where dataPointId=new.dataPointId))"
                    +"); "
                    +""
                    +""
                    +" END IF;"
                    + ""
                    + ""
                    +"end; ");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}