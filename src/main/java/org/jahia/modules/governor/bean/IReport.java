package org.jahia.modules.governor.bean;

import org.jahia.services.content.JCRNodeWrapper;
import org.json.JSONException;
import org.json.JSONObject;
import javax.jcr.RepositoryException;

/**
 * IReport Interface.
 * Created by Juan Carlos Rodas.
 */
public interface IReport {

    /**
     *  the enum SEARCH_DATE_TYPE
     */
    public enum SEARCH_DATE_TYPE{ALL, BEFORE_DATE}

    /**
     * the enum SEARCH_ACTION_TYPE
     */
    public enum SEARCH_ACTION_TYPE{UPDATE, CREATION}

    /**
     * the enum SEARCH_CONTENT_TYPE
     */
    public enum SEARCH_CONTENT_TYPE{PAGE, CONTENT}

    /**
     * the definition SORT_ASC
     */
    public boolean SORT_ASC = true;

    /**
     * the definition SORT_DESC
     */
    public boolean SORT_DESC = false;

    /**
     * addItem
     * <p>add item to the iReport class,
     * custom implementation in each child class.</p>
     *
     * @param node {@link JCRNodeWrapper}
     * @param contentType {@link SEARCH_CONTENT_TYPE}
     * @throws RepositoryException
     */
    public void addItem(JCRNodeWrapper node, SEARCH_CONTENT_TYPE contentType) throws RepositoryException;

    /**
     * getJson
     * <p>get json to the iReport class,
     * custom implementation in each child class,
     * return a specific json for each report.</p>
     *
     * @return {@link JSONObject}
     * @throws JSONException
     * @throws RepositoryException
     */
    public JSONObject getJson() throws JSONException, RepositoryException;

}
