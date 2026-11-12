package org.jahia.modules.reports.bean;

import org.jahia.api.Constants;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRValueWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportWipContentTest {

    private ReportWipContent report;

    @Before
    public void setUp() {
        report = new ReportWipContent(mock(JCRSiteNode.class), "/sites/test", BaseReport.SearchContentType.PAGE, 0, "asc");
    }

    @Test
    public void languagesShowAsTyped() throws RepositoryException, JSONException {
        List<String> languages = Arrays.asList("Q&A \"staging\" \\ x", "R&D <b>team</b>", "Équipe d'été & co", "en");
        report.addItem(page(Constants.WORKINPROGRESS_STATUS_LANG, languages));

        assertEquals(languages, wipColumn());
    }

    @Test
    public void statusShowsAsSingleLine() throws RepositoryException, JSONException {
        report.addItem(page(Constants.WORKINPROGRESS_STATUS_ALLCONTENT, null));

        assertEquals(Arrays.asList(Constants.WORKINPROGRESS_STATUS_ALLCONTENT), wipColumn());
    }

    private List<String> wipColumn() throws RepositoryException, JSONException {
        JSONArray cell = new JSONArray(report.getJson().getJSONArray("data").getJSONArray(0).getString(2));
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < cell.length(); i++) {
            lines.add(cell.getString(i));
        }
        return lines;
    }

    private static JCRNodeWrapper page(String status, List<String> languages) throws RepositoryException {
        JCRNodeWrapper node = mock(JCRNodeWrapper.class);
        ExtendedNodeType type = mock(ExtendedNodeType.class);
        when(node.isNodeType("jnt:page")).thenReturn(true);
        when(node.getPath()).thenReturn("/sites/test/home/page");
        when(node.getDisplayableName()).thenReturn("Page");
        when(node.getPrimaryNodeType()).thenReturn(type);
        when(node.getPropertyAsString(Constants.WORKINPROGRESS_STATUS)).thenReturn(status);
        if (languages != null) {
            JCRValueWrapper[] values = new JCRValueWrapper[languages.size()];
            for (int i = 0; i < values.length; i++) {
                values[i] = mock(JCRValueWrapper.class);
                when(values[i].getString()).thenReturn(languages.get(i));
            }
            JCRPropertyWrapper property = mock(JCRPropertyWrapper.class);
            when(property.getValues()).thenReturn(values);
            when(node.getProperty(Constants.WORKINPROGRESS_LANGUAGES)).thenReturn(property);
        }
        return node;
    }
}
