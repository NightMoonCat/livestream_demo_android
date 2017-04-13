package cn.moon.live.data.model;

public class GiftStatements {
    private Integer id;

    private String uname;

    private String anchor;

    private Integer giftid;

    private Integer giftnum;
    
    private String gdate;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname == null ? null : uname.trim();
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor == null ? null : anchor.trim();
    }

    public Integer getGiftid() {
        return giftid;
    }

    public void setGiftid(Integer giftid) {
        this.giftid = giftid;
    }

    public Integer getGiftnum() {
        return giftnum;
    }

    public void setGiftnum(Integer giftnum) {
        this.giftnum = giftnum;
    }

	public String getGdate() {
		return gdate;
	}

	public void setGdate(String gdate) {
		this.gdate = gdate;
	}
    
}