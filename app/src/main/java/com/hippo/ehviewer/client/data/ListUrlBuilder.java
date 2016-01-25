/*
 * Copyright (C) 2015 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.ehviewer.client.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.hippo.ehviewer.client.EhConfig;
import com.hippo.ehviewer.client.EhUrl;
import com.hippo.ehviewer.client.EhUtils;
import com.hippo.ehviewer.widget.AdvanceSearchTable;
import com.hippo.network.UrlBuilder;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;

public class ListUrlBuilder implements Cloneable, Parcelable {

    @IntDef({MODE_NORMAL, MODE_UPLOADER, MODE_TAG, MODE_WHATS_HOT, MODE_IMAGE_SEARCH})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Mode {}

    // Mode
    public static final int MODE_NORMAL = 0x0;
    public static final int MODE_UPLOADER = 0x1;
    public static final int MODE_TAG = 0x2;
    public static final int MODE_WHATS_HOT = 0x3;
    public static final int MODE_IMAGE_SEARCH = 0x4;

    public static final int ALL_CATEGORT = EhUtils.UNKNOWN - 1;
    //DOUJINSHI|MANGA|ARTIST_CG|GAME_CG|WESTERN|NON_H|IMAGE_SET|COSPLAY|ASIAN_PORN|MISC;

    public static final int DEFAULT_ADVANCE = AdvanceSearchTable.SNAME | AdvanceSearchTable.STAGS;
    public static final int DEFAULT_MIN_RATING = 2;

    @Mode
    private int mMode = MODE_NORMAL;

    private int mPageIndex = 0;

    private int mCategory = EhUtils.NONE;
    private String mKeyword = null;

    private int mAdvanceSearch = -1;
    private int mMinRating = -1;

    /**
     * Make this ListUrlBuilder point to homepage
     */
    public void reset() {
        mMode = MODE_NORMAL;
        mPageIndex = 0;
        mCategory = EhUtils.NONE;
        mKeyword = null;
        mAdvanceSearch = -1;
        mMinRating = -1;
    }

    @Override
    public ListUrlBuilder clone() {
        try {
            return (ListUrlBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Mode
    public int getMode() {
        return mMode;
    }

    public void setMode(@Mode int mode) {
        mMode = mode;
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public void setPageIndex(int pageIndex) {
        mPageIndex = pageIndex;
    }

    public int getCategory() {
        return mCategory;
    }

    public void setCategory(int category) {
        mCategory = category;
    }

    public String getKeyword() {
        return mKeyword;
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
    }

    public int getAdvanceSearch() {
        return mAdvanceSearch;
    }

    public void setAdvanceSearch(int advanceSearch) {
        mAdvanceSearch = advanceSearch;
    }

    public int getMinRating() {
        return mMinRating;
    }

    public void setMinRating(int minRating) {
        mMinRating = minRating;
    }

    /**
     * Make them the same
     * @param glub The template
     */
    public void set(ListUrlBuilder glub) {
        mMode = glub.mMode;
        mPageIndex = glub.mPageIndex;
        mCategory = glub.mCategory;
        mKeyword = glub.mKeyword;
        mAdvanceSearch = glub.mAdvanceSearch;
        mMinRating = glub.mMinRating;
    }

    /*
    public void set(QuickSearch quickSearch) {
        mMode = quickSearch.mode;
        mPageIndex = 0;
        mCategory = quickSearch.category;
        mKeyword = quickSearch.keyword;
        mAdvanceSearch = quickSearch.advancedSearch;
        mMinRating = quickSearch.minRating;
    }
    */

    public String build() {
        switch (mMode) {
            default:
            case MODE_NORMAL:
            case MODE_UPLOADER: {
                boolean filter = false;
                UrlBuilder ub = new UrlBuilder(EhUrl.HOST_EX);
                if (mCategory != EhUtils.NONE) {
                    ub.addQuery("f_doujinshi", ((mCategory & EhConfig.DOUJINSHI) == 0) ? "0" : "1");
                    ub.addQuery("f_manga", ((mCategory & EhConfig.MANGA) == 0) ? "0" : "1");
                    ub.addQuery("f_artistcg", ((mCategory & EhConfig.ARTIST_CG) == 0) ? "0" : "1");
                    ub.addQuery("f_gamecg", ((mCategory & EhConfig.GAME_CG) == 0) ? "0" : "1");
                    ub.addQuery("f_western", ((mCategory & EhConfig.WESTERN) == 0) ? "0" : "1");
                    ub.addQuery("f_non-h", ((mCategory & EhConfig.NON_H) == 0) ? "0" : "1");
                    ub.addQuery("f_imageset", ((mCategory & EhConfig.IMAGE_SET) == 0) ? "0" : "1");
                    ub.addQuery("f_cosplay", ((mCategory & EhConfig.COSPLAY) == 0) ? "0" : "1");
                    ub.addQuery("f_asianporn", ((mCategory & EhConfig.ASIAN_PORN) == 0) ? "0" : "1");
                    ub.addQuery("f_misc", ((mCategory & EhConfig.MISC) == 0) ? "0" : "1");
                    filter = true;
                }
                // Search key
                if (mKeyword != null) {
                    try {
                        ub.addQuery("f_search", URLEncoder.encode(mKeyword, "UTF-8"));
                        filter = true;
                    } catch (UnsupportedEncodingException e) {
                        // Empty
                    }
                }
                // Page index
                if (mPageIndex != 0) {
                    ub.addQuery("page", mPageIndex);
                }
                // Advance search
                if (mAdvanceSearch != -1) {
                    ub.addQuery("advsearch", "1");
                    if((mAdvanceSearch & AdvanceSearchTable.SNAME) != 0) ub.addQuery("f_sname", "on");
                    if((mAdvanceSearch & AdvanceSearchTable.STAGS) != 0) ub.addQuery("f_stags", "on");
                    if((mAdvanceSearch & AdvanceSearchTable.SDESC) != 0) ub.addQuery("f_sdesc", "on");
                    if((mAdvanceSearch & AdvanceSearchTable.STORR) != 0) ub.addQuery("f_storr", "on");
                    if((mAdvanceSearch & AdvanceSearchTable.STO) != 0) ub.addQuery("f_sto", "on");
                    if((mAdvanceSearch & AdvanceSearchTable.SDT1) != 0) ub.addQuery("f_sdt1", "on");
                    if((mAdvanceSearch & AdvanceSearchTable.SDT2) != 0) ub.addQuery("f_sdt2", "on");
                    if((mAdvanceSearch & AdvanceSearchTable.SH) != 0) ub.addQuery("f_sh", "on");
                    // Set min star
                    if (mMinRating != -1) {
                        ub.addQuery("f_sr", "on");
                        ub.addQuery("f_srdd", mMinRating);
                    }
                    filter = true;
                }
                // Add filter foot
                if (filter) {
                    ub.addQuery("f_apply", "Apply+Filter");
                }
                return ub.build();
            }
            case MODE_TAG: {
                StringBuilder sb = new StringBuilder(EhUrl.HOST_EX);
                sb.append("tag/");
                try {
                    sb.append(URLEncoder.encode(mKeyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // Empty
                }
                return sb.toString();
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMode);
        dest.writeInt(this.mPageIndex);
        dest.writeInt(this.mCategory);
        dest.writeString(this.mKeyword);
        dest.writeInt(this.mAdvanceSearch);
        dest.writeInt(this.mMinRating);
    }

    public ListUrlBuilder() {}

    protected ListUrlBuilder(Parcel in) {
        mMode = in.readInt();
        mPageIndex = in.readInt();
        mCategory = in.readInt();
        mKeyword = in.readString();
        mAdvanceSearch = in.readInt();
        mMinRating = in.readInt();
    }

    public static final Parcelable.Creator<ListUrlBuilder> CREATOR = new Parcelable.Creator<ListUrlBuilder>() {

        @Override
        public ListUrlBuilder createFromParcel(Parcel source) {
            return new ListUrlBuilder(source);
        }

        @Override
        public ListUrlBuilder[] newArray(int size) {
            return new ListUrlBuilder[size];
        }
    };
}
