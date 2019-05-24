package com.iwinad.uploadpicture.http.Entity;

/**
 * Created by yixiaofei on 2017/3/6 0006.
 */

public class BaseEntity{
    //返回码
    private String retCode;
    //返回信息
    private String retMsg;
    //流水账号
    private String logNo;
    //会话ID
    private String sessionId;
    //页码信息
    public PageInfoBean pageInfo;
    //锁定剩余秒数
    public String totalseconds;
    //剩余次数
    public String sylogincount;
    //分享链接
    public String weburl;
    //二维码
    public String twoimg;
    //订单号
    public String orno;
    //推广链接
    public String promotionurl;
    //根据区域查看公交/地铁数量
    public String count;
    //第三方支付总金额
    public String zmoney;

    public String noread;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public String getLogNo() {
        return logNo;
    }

    public void setLogNo(String logNo) {
        this.logNo = logNo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPromotionurl() {
        return promotionurl;
    }

    public void setPromotionurl(String promotionurl) {
        this.promotionurl = promotionurl;
    }

    public static class PageInfoBean {
        /**
         * currentPageNum : 1
         * rowsOfPage : 10
         * totalPageCount : 1
         * recordCount 56
         */

        private String currentPageNum;
        private String rowsOfPage;
        private String totalPageCount;
        private String recordCount;

        public String getRecordCount() {
            return recordCount;
        }

        public void setRecordCount(String recordCount) {
            this.recordCount = recordCount;
        }

        public String getCurrentPageNum() {
            return currentPageNum;
        }

        public void setCurrentPageNum(String currentPageNum) {
            this.currentPageNum = currentPageNum;
        }

        public String getRowsOfPage() {
            return rowsOfPage;
        }

        public void setRowsOfPage(String rowsOfPage) {
            this.rowsOfPage = rowsOfPage;
        }

        public String getTotalPageCount() {
            return totalPageCount;
        }

        public void setTotalPageCount(String totalPageCount) {
            this.totalPageCount = totalPageCount;
        }
    }
}
