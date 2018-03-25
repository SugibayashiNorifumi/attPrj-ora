package application.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.TAttendanceDao;
import application.dto.DayAttendance;
import application.emuns.AttenanceCd;
import application.entity.TAttendance;

/**
 * リスト出力サービス
 *
 * @author 作成者名
 *
 */
@Service
@Transactional
public class ListOutputService {

    /** 勤怠情報DAO。 */
    @Autowired
    private TAttendanceDao tAttendancedDao;

    /**
     * 勤怠情報をCSV形式で取得する。
     */
    public List<DayAttendance> getDayAttendanceList(String yyyymm) {
    	List<TAttendance> attendanceList = tAttendancedDao.selectByMonth(yyyymm);
    	Map<String, DayAttendance> daysAttendanceMap = new LinkedHashMap<String, DayAttendance>();
    	attendanceList.stream().forEach(tAttendance -> this.setDaysAttendanceMap(daysAttendanceMap, tAttendance));
    	return new ArrayList<DayAttendance>(daysAttendanceMap.values());
    }

    /**
     * 勤怠情報エンティティを日付ごとの勤怠情報マップにセットします。
     * @param daysAttendanceMap 日付ごとの勤怠情報マップ
     * @param tAttendance 勤怠情報エンティティ
     */
    private void setDaysAttendanceMap(Map<String, DayAttendance> daysAttendanceMap, TAttendance tAttendance) {
    	DayAttendance dayAttendance;
    	String daysAttendanceKey = getDaysAttendanceMapKey(tAttendance);
    	if (daysAttendanceMap.containsKey(daysAttendanceKey)) {
    		dayAttendance = daysAttendanceMap.get(daysAttendanceKey);
    	} else {
    		dayAttendance = new DayAttendance();
    		daysAttendanceMap.put(daysAttendanceKey, dayAttendance);
        	dayAttendance.setUserId(tAttendance.getUserId());
        	dayAttendance.setAttendanceDay(tAttendance.getAttendanceDay());
    	}
    	ZoneId zone = ZoneId.systemDefault();
    	if (AttenanceCd.ARRIVAL.getCode().equals(tAttendance.getAttendanceCd())) {
        	dayAttendance.setArrivalTime(
        			LocalDateTime.ofInstant(tAttendance.getAttendanceTime().toInstant(), zone));
    	} else {
        	dayAttendance.setClockOutTime(
        			LocalDateTime.ofInstant(tAttendance.getAttendanceTime().toInstant(), zone));
    	}
    }

    /**
     * 日付ごとの勤怠情報マップ用キーを返します。
     * @param tAttendance 勤怠情報エンティティ
     * @return 日付ごとの勤怠情報マップ用キー
     */
    private String getDaysAttendanceMapKey(TAttendance tAttendance) {
    	return tAttendance.getUserId() + "_" + tAttendance.getAttendanceDay();
    }
}
