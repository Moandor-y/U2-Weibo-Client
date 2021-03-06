package gov.moandor.androidweibo.dao;

import org.json.JSONException;
import org.json.JSONObject;

import gov.moandor.androidweibo.R;
import gov.moandor.androidweibo.bean.WeiboStatus;
import gov.moandor.androidweibo.util.GlobalContext;
import gov.moandor.androidweibo.util.HttpParams;
import gov.moandor.androidweibo.util.HttpUtils;
import gov.moandor.androidweibo.util.JsonUtils;
import gov.moandor.androidweibo.util.Logger;
import gov.moandor.androidweibo.util.UrlHelper;
import gov.moandor.androidweibo.util.WeiboException;

public class FavoriteDao extends BaseHttpDao<WeiboStatus> {
    private String mToken;
    private long mId;

    @Override
    public WeiboStatus execute() throws WeiboException {
        HttpParams params = new HttpParams();
        params.put("access_token", mToken);
        params.put("id", mId);
        HttpUtils.Method method = HttpUtils.Method.POST;
        String response = HttpUtils.executeNormalTask(method, mUrl, params);
        try {
            JSONObject json = new JSONObject(response);
            return JsonUtils.getWeiboStatusFromJson(json.getJSONObject("status"));
        } catch (JSONException e) {
            Logger.logException(e);
            throw new WeiboException(GlobalContext.getInstance().getString(R.string.json_error));
        }
    }

    @Override
    protected String getUrl() {
        return UrlHelper.FAVORITES_CREATE;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public void setId(long id) {
        mId = id;
    }
}
