
package com.sieae.jamaicaobserver.rss;

import java.util.ArrayList;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import org.parceler.Generated;
import org.parceler.InjectionUtil;
import org.parceler.ParcelWrapper;

@Generated(value = "org.parceler.ParcelAnnotationProcessor", date = "2017-07-17T11:10-0500")
public class RSSFeed$$Parcelable
    implements Parcelable, ParcelWrapper<com.sieae.jamaicaobserver.rss.RSSFeed>
{

    private com.sieae.jamaicaobserver.rss.RSSFeed rSSFeed$$0;
    @SuppressWarnings("UnusedDeclaration")
    public final static RSSFeed$$Parcelable.Creator$$0 CREATOR = new RSSFeed$$Parcelable.Creator$$0();

    public RSSFeed$$Parcelable(android.os.Parcel parcel$$0) {
        rSSFeed$$0 = new com.sieae.jamaicaobserver.rss.RSSFeed();
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "link", parcel$$0 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "description", parcel$$0 .readString());
        int int$$0 = parcel$$0 .readInt();
        ArrayList<com.sieae.jamaicaobserver.rss.RSSItem> list$$0;
        if (int$$0 < 0) {
            list$$0 = null;
        } else {
            list$$0 = new ArrayList<com.sieae.jamaicaobserver.rss.RSSItem>();
            for (int int$$1 = 0; (int$$1 <int$$0); int$$1 ++) {
                com.sieae.jamaicaobserver.rss.RSSItem rSSItem$$1;
                if (parcel$$0 .readInt() == -1) {
                    rSSItem$$1 = null;
                } else {
                    rSSItem$$1 = readcom_sieae_jamaicaobserver_rss_RSSItem(parcel$$0);
                }
                list$$0 .add(rSSItem$$1);
            }
        }
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "itemList", list$$0);
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "title", parcel$$0 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "thumburl", parcel$$0 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "pubdate", parcel$$0 .readString());
    }

    public RSSFeed$$Parcelable(com.sieae.jamaicaobserver.rss.RSSFeed rSSFeed$$1) {
        rSSFeed$$0 = rSSFeed$$1;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel$$1, int flags) {
        parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "link"));
        parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "description"));
        if (InjectionUtil.getField(java.util.List.class, com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "itemList") == null) {
            parcel$$1 .writeInt(-1);
        } else {
            parcel$$1 .writeInt(InjectionUtil.getField(java.util.List.class, com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "itemList").size());
            for (com.sieae.jamaicaobserver.rss.RSSItem rSSItem$$2 : ((java.util.List<com.sieae.jamaicaobserver.rss.RSSItem> ) InjectionUtil.getField(java.util.List.class, com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "itemList"))) {
                if (rSSItem$$2 == null) {
                    parcel$$1 .writeInt(-1);
                } else {
                    parcel$$1 .writeInt(1);
                    writecom_sieae_jamaicaobserver_rss_RSSItem(rSSItem$$2, parcel$$1, flags);
                }
            }
        }
        parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "title"));
        parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "thumburl"));
        parcel$$1 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSFeed.class, rSSFeed$$0, "pubdate"));
    }

    private com.sieae.jamaicaobserver.rss.RSSItem readcom_sieae_jamaicaobserver_rss_RSSItem(android.os.Parcel parcel$$2) {
        com.sieae.jamaicaobserver.rss.RSSItem rSSItem$$0;
        rSSItem$$0 = new com.sieae.jamaicaobserver.rss.RSSItem();
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$0, "link", parcel$$2 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$0, "isRead", (parcel$$2 .readInt() == 1));
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$0, "description", parcel$$2 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$0, "title", parcel$$2 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$0, "thumburl", parcel$$2 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$0, "rowdescription", parcel$$2 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$0, "pubdate", parcel$$2 .readString());
        return rSSItem$$0;
    }

    private void writecom_sieae_jamaicaobserver_rss_RSSItem(com.sieae.jamaicaobserver.rss.RSSItem rSSItem$$3, android.os.Parcel parcel$$3, int flags$$0) {
        parcel$$3 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$3, "link"));
        parcel$$3 .writeInt((InjectionUtil.getField(boolean.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$3, "isRead")? 1 : 0));
        parcel$$3 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$3, "description"));
        parcel$$3 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$3, "title"));
        parcel$$3 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$3, "thumburl"));
        parcel$$3 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$3, "rowdescription"));
        parcel$$3 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$3, "pubdate"));
    }

    @Override
    public int describeContents() {
        return  0;
    }

    @Override
    public com.sieae.jamaicaobserver.rss.RSSFeed getParcel() {
        return rSSFeed$$0;
    }

    private final static class Creator$$0
        implements Creator<RSSFeed$$Parcelable>
    {


        @Override
        public RSSFeed$$Parcelable createFromParcel(android.os.Parcel parcel$$4) {
            return new RSSFeed$$Parcelable(parcel$$4);
        }

        @Override
        public RSSFeed$$Parcelable[] newArray(int size) {
            return new RSSFeed$$Parcelable[size] ;
        }

    }

}
