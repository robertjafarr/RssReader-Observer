
package org.parceler;

import java.util.HashMap;
import java.util.Map;
import com.sieae.jamaicaobserver.rss.RSSFeed;
import com.sieae.jamaicaobserver.rss.RSSFeed$$Parcelable;
import com.sieae.jamaicaobserver.rss.RSSItem;
import com.sieae.jamaicaobserver.rss.RSSItem$$Parcelable;

@Generated(value = "org.parceler.ParcelAnnotationProcessor", date = "2017-07-03T17:23-0500")
@SuppressWarnings("unchecked")
public class Parceler$$Parcels
    implements Repository<org.parceler.Parcels.ParcelableFactory>
{

    private final Map<Class, org.parceler.Parcels.ParcelableFactory> map$$0 = new HashMap<Class, org.parceler.Parcels.ParcelableFactory>();

    public Parceler$$Parcels() {
        map$$0 .put(RSSFeed.class, new Parceler$$Parcels.RSSFeed$$Parcelable$$0());
        map$$0 .put(RSSItem.class, new Parceler$$Parcels.RSSItem$$Parcelable$$0());
    }

    public Map<Class, org.parceler.Parcels.ParcelableFactory> get() {
        return map$$0;
    }

    private final static class RSSFeed$$Parcelable$$0
        implements org.parceler.Parcels.ParcelableFactory<RSSFeed>
    {


        @Override
        public RSSFeed$$Parcelable buildParcelable(RSSFeed input) {
            return new RSSFeed$$Parcelable(input);
        }

    }

    private final static class RSSItem$$Parcelable$$0
        implements org.parceler.Parcels.ParcelableFactory<RSSItem>
    {


        @Override
        public RSSItem$$Parcelable buildParcelable(RSSItem input) {
            return new RSSItem$$Parcelable(input);
        }

    }

}
