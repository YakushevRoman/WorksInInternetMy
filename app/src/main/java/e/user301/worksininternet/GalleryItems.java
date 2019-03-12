package e.user301.worksininternet;

public class GalleryItems {
    private String rCaption;
    private String rId;
    private String rUrl;

    @Override
    public String toString() {
        return rCaption;
    }

    public String getrCaption() {
        return rCaption;
    }

    public void setrCaption(String rCaption) {
        this.rCaption = rCaption;
    }

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getrUrl() {
        return rUrl;
    }

    public void setrUrl(String rUrl) {
        this.rUrl = rUrl;
    }
}
