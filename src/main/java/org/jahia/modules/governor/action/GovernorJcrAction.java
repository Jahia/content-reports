package org.jahia.modules.governor.action;

import org.apache.commons.lang.StringUtils;
import org.jahia.api.Constants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.governor.exception.GovernorException;
import org.jahia.services.content.*;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * GovernorReportAction Class.
 * Created by Juan Carlos Rodas.
 */
public class GovernorJcrAction extends Action {

    /* the logger fot the class */
    private static Logger logger = LoggerFactory.getLogger(GovernorJcrAction.class);

    /* the locale map */
    protected Map<String, Locale> localeMap;

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        logger.info("doExecute: begins the GovernorReportAction action.");
        Boolean isOk = Boolean.FALSE;
        this.localeMap = new HashMap<>();

        /* getting the locales for the site. */
        for (Locale ilocale : renderContext.getSite().getLanguagesAsLocales())
            this.localeMap.put(ilocale.toString(), ilocale);

        try {
            /*checking the necessary parameters*/
            if (StringUtils.isEmpty(req.getParameter("jcrActionId"))) {
                throw new Exception("The parameter jcrActionId is missing.");
            }

            /* returns the specific report specified by the jcrActionId parameter */
            String jcrActionId = req.getParameter("jcrActionId");
            String jcrNodePath = req.getParameter("jcrNodePath");
            String language = req.getParameter("nodeLanguage");
            String jcrPropertyName = req.getParameter("nodePropertyName");
            String jcrPropertyValue = req.getParameter("nodePropertyValue");
            String keywords = req.getParameter("nodeKeywords");

            switch (jcrActionId) {
                case "1":
                    isOk = (  StringUtils.isEmpty(language) ? publishNode(session, jcrNodePath.replaceAll("'",""))  : publishNodeByLanguage(session, jcrNodePath.replaceAll("'",""), language) );
                    return (isOk ? ActionResult.OK_JSON : ActionResult.BAD_REQUEST );
                case "2":
                    isOk = updateProperyNode(session, jcrNodePath.replaceAll("'",""), jcrPropertyName, jcrPropertyValue, language);
                    return (isOk ? ActionResult.OK_JSON : ActionResult.BAD_REQUEST );
                case "3":
                    isOk = addKeywordsNode(session, jcrNodePath.replaceAll("'",""), keywords.replaceAll("'",""));
                    return (isOk ? ActionResult.OK_JSON : ActionResult.BAD_REQUEST );
                case "4":
                    isOk = unlockNode(jcrNodePath.replaceAll("'",""));
                    return (isOk ? ActionResult.OK_JSON : ActionResult.BAD_REQUEST );
                case "5":
                    isOk = unlockAllNodes(jcrNodePath.replaceAll("'",""));
                    return (isOk ? ActionResult.OK_JSON : ActionResult.BAD_REQUEST );
                default:
                    throw new GovernorException("Invalid jcrActionId: " + jcrActionId);
            }

        } catch (GovernorException gex) {
            logger.error("doExecute(), Error,", gex);
            return ActionResult.BAD_REQUEST;
        } catch (Exception ex) {
            logger.error("doExecute(), Error,", ex);
            return ActionResult.BAD_REQUEST;
        }
    }

    /**
     * publish Node by node path.
     *
     * @param session {@link JCRSessionWrapper}
     * @param nodePath {@link String}
     * @return isPublished {@link Boolean}
     */
    private boolean publishNode(JCRSessionWrapper session, String nodePath){
        boolean published = false;
        JCRPublicationService publicationService = JCRPublicationService.getInstance();

        if(publicationService != null){
            try {
                JCRNodeWrapper nodeWrapper = session.getNode(nodePath);
                String name = nodeWrapper.getDisplayableName();

                publicationService.publish(Collections.singletonList(nodeWrapper.getIdentifier()),
                        nodeWrapper.getSession().getWorkspace().getName(),
                        Constants.LIVE_WORKSPACE, Collections.singletonList(""));

                published = true;
            } catch (RepositoryException e) {
                logger.error(e.getMessage(), e);
            }
        }else{
            logger.error("publishNode(), JCRPublicationService is null....");
        }

        return published;
    }

    /**
     * publish node by node path and language.
     *
     * @param session {@link JCRSessionWrapper}
     * @param nodePath {@link String}
     * @param language {@link String}
     * @return isPublished {@link Boolean}
     */
    private boolean publishNodeByLanguage(JCRSessionWrapper session, String nodePath, String language) {
        boolean published = false;
        JCRPublicationService publicationService = JCRPublicationService.getInstance();

        if(publicationService != null){
            Set<String> languages = new HashSet<>();
            languages.add(language);
            try {
                JCRNodeWrapper nodeWrapper = session.getNode(nodePath);
                String name = nodeWrapper.getDisplayableName();

                publicationService.publishByMainId(nodeWrapper.getIdentifier(),
                        nodeWrapper.getSession().getWorkspace().getName(),
                        Constants.LIVE_WORKSPACE,
                        languages, true, Collections.singletonList(""));

                published = true;
            } catch (RepositoryException e) {
                logger.error(e.getMessage(), e);
            }
        }else{
            logger.error("publishNodeByLanguage(), JCRPublicationService is null....");
        }

        return published;
    }

    /**
     * update property node by path
     *
     * @param session {@link JCRSessionWrapper}
     * @param nodePath {@link String}
     * @param propertyName {@link String}
     * @param propertyValue {@link String}
     * @param language {@link String}
     * @return isSaved {@link Boolean}
     */
    private boolean updateProperyNode(JCRSessionWrapper session, String nodePath, String propertyName, String propertyValue, String language) {
        boolean saved = false;

        try {
            JCRNodeWrapper nodeWrapper = session.getNode(nodePath);
            Node translationNode = getTranslationNode(nodeWrapper, language);

            if(translationNode != null){
                translationNode.setProperty(propertyName, propertyValue);
                session.save();
                saved = true;
            }
        } catch (RepositoryException e) {
            logger.error("updateProperyNode(), error trying to save the lang node [" + nodePath + "].", e);
        }

        return saved;
    }

    /**
     * get the translation node.
     *
     * @param parentNode {@link JCRNodeWrapper}
     * @param localeLang {@link String}
     * @return translation node {@link Node}
     * @throws RepositoryException
     */
    private Node getTranslationNode(JCRNodeWrapper parentNode, String localeLang) throws RepositoryException {
        if(this.localeMap.containsKey(localeLang))
            return parentNode.getOrCreateI18N(this.localeMap.get(localeLang));

        return null;
    }

    /**
     * add keywords to node.
     *
     * @param session {@link JCRSessionWrapper}
     * @param nodePath {@link String}
     * @param keywords {@link String}
     * @return isSaved {@link Boolean}
     */
    private boolean addKeywordsNode(JCRSessionWrapper session, String nodePath, String keywords) {
        boolean saved = false;

        try {
            JCRNodeWrapper nodeWrapper = session.getNode(nodePath);
            nodeWrapper.addMixin("jmix:keywords");
            nodeWrapper.setProperty("j:keywords", keywords.split(","));
            session.save();
            saved = true;
            logger.debug("addKeywordsNode(), updated node[" + nodePath + "] with keywords {" + keywords + "}.");
        } catch (RepositoryException e) {
            logger.error("updateProperyNode(), error trying to save the lang node [" + nodePath + "].", e);
        }

        return saved;
    }

    /**
     * unlock node by path.
     *
     * @param nodePath {@link String}
     * @return isUnlocked {@link Boolean}
     */
    private boolean unlockNode(final String nodePath) {
        boolean unlocked = false;

        try {
            JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Object>() {
                public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    session.getNode(nodePath).clearAllLocks();
                    return null;
                }
            });


            unlocked = true;
            logger.debug("unlockNode(), unlock node[" + nodePath + "] .");
        } catch (RepositoryException e) {
            logger.error("unlockNode(), error trying to unlock the node [" + nodePath + "].", e);
        }

        return unlocked;
    }


    /**
     * unlock node and nodes under the parent node.
     *
     * @param nodePath {@link String}
     * @return isUnlocked {@link Boolean}
     */
    private boolean unlockAllNodes(final String nodePath) {
        boolean unlocked = false;

        try {
            JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Object>() {
                public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    simpleUnlockNode(session.getNode(nodePath));
                    return null;
                }
            });

            unlocked = true;
            logger.debug("unlockNode(), unlock node[" + nodePath + "] .");
        } catch (RepositoryException e) {
            logger.error("unlockNode(), error trying to unlock the node [" + nodePath + "].", e);
        }

        return unlocked;
    }



    private void simpleUnlockNode(JCRNodeWrapper parentNode) throws RepositoryException {
        /* unlocking the parent node */
        parentNode.clearAllLocks();

        /* unlocking all the childNodes */
        if(parentNode.hasNodes()){
            for (JCRNodeWrapper childNode: parentNode.getNodes()) {
                simpleUnlockNode(childNode);
            }
        }
    }


}