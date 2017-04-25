package io.weichao.domain;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import io.weichao.activity.BaseFragmentActivity;
import io.weichao.library.R;
import io.weichao.util.AssetsUtil;
import io.weichao.util.BitmapUtil;
import io.weichao.util.ColorUtil;

public class LocalDataSource extends DataSource {
    private BaseFragmentActivity mActivity;

    private Bitmap mBitmap;
    private List<Marker> mMarkerList;

    public LocalDataSource(BaseFragmentActivity activity) {
        mActivity = activity;

////        Marker atl = new IconMarker("ATL", 39.931269, -75.051261, 0, Color.DKGRAY, icon);
//        Marker atl = new IconMarker("ATL", 39.970475, 116.330105, 0, Color.DKGRAY, icon);
//        mMarkerList.add(atl);
//
////        Marker home = new Marker("Mt Laurel", 39.95, -74.9, 0, Color.YELLOW);
//        Marker home = new Marker("Mt Laurel", 39.970475, 116.340105, 0, Color.YELLOW);
//        mMarkerList.add(home);

        mBitmap = BitmapUtil.getBitmap(mActivity, R.mipmap.splash);

        String jsonStr = AssetsUtil.getString(mActivity, "arpoi/local_data.json");
        mMarkerList = loadDataFromJson(jsonStr);
    }

    public List<Marker> getMarkers() {
        return mMarkerList;
    }

    private List<Marker> loadDataFromJson(String str) {
        LinkedList<Marker> list = new LinkedList<>();

        try {
            JSONArray jsonArray = new JSONArray(str);
            for (int i = 0; i < jsonArray.length(); i++) {
                IconMarker iconMarker = null;
                try {
                    JSONObject jObj = (JSONObject) jsonArray.get(i);
                    String name = jObj.getString("name");
                    double latitude = jObj.getDouble("latitude") - 0.000836;
                    double longitude = jObj.getDouble("longitude") - 0.006556;
                    double altitude;
                    try {
                        altitude = jObj.getDouble("altitude");
                    } catch (Exception e) {
                        altitude = 0;
                    }
                    String color;
                    try {
                        color = jObj.getString("color");
                    } catch (Exception e) {
                        color = ColorUtil.getRandomColor();
                    }
                    Bitmap bitmap;
                    try {
                        String bitmapStr = jObj.getString("bitmap");
                        bitmap = AssetsUtil.getBitmap(mActivity, bitmapStr);
                    } catch (Exception e) {
                        bitmap = mBitmap;
                    }
                    iconMarker = new IconMarker(name, latitude, longitude, altitude, Color.parseColor(color), bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (iconMarker != null) {
                    list.add(iconMarker);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}