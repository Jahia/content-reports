package org.jahia.modules.governor.bean;

import org.jahia.exceptions.JahiaException;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import java.util.Locale;

/**
 * ReportByLanguage Class.
 *
 * Created by Juan Carlos Rodas.
 */
public class ReportByLanguage extends BaseReport {

    private static Logger logger = LoggerFactory.getLogger(ReportByLanguage.class);

    /**
     * The constructor for the class.
     *
     * @param siteNode {@link JCRSiteNode}
     */
    public ReportByLanguage(JCRSiteNode siteNode) {
        super(siteNode);
    }

    @Override
    public void execute(JCRSessionWrapper session, int offset, int limit) throws RepositoryException, JSONException, JahiaException {
    }

    /**
     * getJson
     *
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    public JSONObject getJson() throws JSONException, RepositoryException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONObject jsonObjectItem;

        for(Locale locale :  siteNode.getLanguagesAsLocales()){
            jsonObjectItem = new JSONObject();
            jsonObjectItem.put("language", locale.getLanguage());
            jsonObjectItem.put("displayLanguage", locale.getDisplayLanguage());
            jsonObjectItem.put("country", locale.getCountry());
            jsonObjectItem.put("displayCountry", locale.getDisplayCountry());
            jsonObjectItem.put("locale", locale.toString());
            jsonObjectItem.put("displayName", locale.getDisplayName());
            jsonObjectItem.put("displayScript", locale.getDisplayScript());
            jsonObjectItem.put("displayVariant", locale.getDisplayVariant());
            jsonObjectItem.put("availableEdit", !siteNode.getInactiveLanguages().contains(locale.toString()));
            jsonObjectItem.put("availableLive", !siteNode.getInactiveLiveLanguages().contains(locale.toString()));
            jArray.put(jsonObjectItem);
        }

        jsonObject.put("siteName", siteNode.getName());
        jsonObject.put("siteDisplayableName", siteNode.getDisplayableName());
        jsonObject.put("languageItems", jArray);
        return jsonObject;
    }

}
