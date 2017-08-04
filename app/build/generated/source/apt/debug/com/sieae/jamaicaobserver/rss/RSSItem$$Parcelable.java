
package com.sieae.jamaicaobserver.rss;

import android.os.Parcelable;
import android.os.Parcelable.Creator;
import org.parceler.Generated;
import org.parceler.InjectionUtil;
import org.parceler.ParcelWrapper;

@Generated(value = "org.parceler.ParcelAnnotationProcessor", date = "2017-07-03T17:23-0500")
public class RSSItem$$Parcelable
    implements Parcelable, ParcelWrapper<com.sieae.jamaicaobserver.rss.RSSItem>
{

    private com.sieae.jamaicaobserver.rss.RSSItem rSSItem$$4;
    @SuppressWarnings("UnusedDeclaration")
    public final static RSSItem$$Parcelable.Creator$$1 CREATOR = new RSSItem$$Parcelable.Creator$$1();

    public RSSItem$$Parcelable(android.os.Parcel parcel$$5) {
        rSSItem$$4 = new com.sieae.jamaicaobserver.rss.RSSItem();
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "link", parcel$$5 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "isRead", (parcel$$5 .readInt() == 1));
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "description", parcel$$5 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "title", parcel$$5 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "thumburl", parcel$$5 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "rowdescription", parcel$$5 .readString());
        InjectionUtil.setField(com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "pubdate", parcel$$5 .readString());
    }

    public RSSItem$$Parcelable(com.sieae.jamaicaobserver.rss.RSSItem rSSItem$$5) {
        rSSItem$$4 = rSSItem$$5;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel$$6, int flags) {
        parcel$$6 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "link"));
        parcel$$6 .writeInt((InjectionUtil.getField(boolean.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "isRead")? 1 : 0));
        parcel$$6 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "description"));
        parcel$$6 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "title"));
        parcel$$6 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "thumburl"));
        parcel$$6 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "rowdescription"));
        parcel$$6 .writeString(InjectionUtil.getField(java.lang.String.class, com.sieae.jamaicaobserver.rss.RSSItem.class, rSSItem$$4, "pubdate"));
    }

    @Override
    public int describeContents() {
        return  0;
    }

    @Override
    public com.sieae.jamaicaobserver.rss.RSSItem getParcel() {
        return rSSItem$$4;
    }

    private final static class Creator$$1
        implements Creator<RSSItem$$Parcelable>
    {


        @Override
        public RSSItem$$Parcelable createFromParcel(android.os.Parcel parcel$$7) {
            return new RSSItem$$Parcelable(parcel$$7);
        }

        @Override
        public RSSItem$$Parcelable[] newArray(int size) {
            return new RSSItem$$Parcelable[size] ;
        }

    }

}
