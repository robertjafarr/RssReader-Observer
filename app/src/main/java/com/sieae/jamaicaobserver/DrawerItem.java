package com.sieae.jamaicaobserver;

import java.util.List;

/**
 * Created by Mohammad Arshi Khan on 12-07-2016.
 */
public class DrawerItem {
    public String itemname;
    public String itemtype;
    public String itemiconurl;
    public String itemurl;
    public List<SectionItems> sectionitemlist;

    public List<SectionItems> getSectionitemlist() {
        return sectionitemlist;
    }

    public void setSectionitemlist(List<SectionItems> sectionitemlist) {
        this.sectionitemlist = sectionitemlist;
    }

    public String getItemurl() {
        return itemurl;
    }

    public void setItemurl(String itemurl) {
        this.itemurl = itemurl;
    }

    public String getItemiconurl() {
        return itemiconurl;
    }

    public void setItemiconurl(String itemiconurl) {
        this.itemiconurl = itemiconurl;
    }

    public String getItemtype() {
        return itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

}
