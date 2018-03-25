package application.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;

import application.dao.MUserDao;
import application.dao.TAttendanceDao;
import application.dao.TLineStatusDao;
import application.emuns.MenuCd;
import application.entity.MUser;
import application.entity.TLineStatus;

/**
 * 勤怠情報操作サービス。
 */
@Service
@Transactional
public class AttendanceService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);

    /** 勤怠情報DAO。 */
    @Autowired
    private TAttendanceDao tAttendancedDao;

    /** LINEステータス情報DAO。 */
    @Autowired
    private TLineStatusDao tLineStatusDao;

    /** ユーザマスタDAO。 */
    @Autowired
    private MUserDao mUserDao;

    /**
     * LINEメニュー操作を受け付ける。
     * @param menuCd メニュー
     * @return 区分明細情報リスト
     */
    public void requestMenu(MenuCd menuCd, MessageEvent<?> evt, TextMessageContent message) {
        logger.debug("requestMenu() {}", menuCd);
        String lineId = evt.getSource().getUserId();
        // ステータス設定
        TLineStatus lineStatus = getLineSutatus(lineId);
        lineStatus.setMenuCd(menuCd.getDivCd());
        lineStatus.setActionName(null);
        lineStatus.setContents(message.getText());
        setLineSutatus(lineStatus);
    }

    /**
     * LINEからの文字列入力を受け付ける。
     * @param text 入力内容
     */
    public void requestText(String text, MessageEvent<?> evt, TextMessageContent message) {
        logger.debug("requestText() {}", text);
        String replyToken = evt.getReplyToken();
        String lineId = evt.getSource().getUserId();
        TLineStatus lineStatus = getLineSutatus(lineId);

        // ステータス設定
        lineStatus.setMenuCd("empty");
        lineStatus.setActionName(null);
        lineStatus.setContents(null);
        tLineStatusDao.save(lineStatus);

        // メニューから操作してください
        LineAPIService.repryMessage(replyToken, "メニューから操作してください");
    }

    /**
     * LINE操作を保存する。
     * @param lineStatus LINEステータス
     */
    private void setLineSutatus(TLineStatus lineStatus) {
        lineStatus.setRequestTime(new Date());
        tLineStatusDao.save(lineStatus);
    }

    /**
     * 前回のLINE操作を取得する。
     * @param lineId 送信元LINE識別子
     * @return LINEステータス。存在しない場合、初期値をセットした新規行
     */
    private TLineStatus getLineSutatus(String lineId) {
        TLineStatus res = tLineStatusDao.getByPk(lineId);
        if (res == null) {
            res = new TLineStatus();
            res.setLineId(lineId);
            MUser user = mUserDao.getByLineId(lineId);
            res.setUserId(user.getUserId());
        }
        return res;
    }
}
